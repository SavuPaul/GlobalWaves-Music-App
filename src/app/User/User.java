package app.User;

import app.Admin;
import app.audio.Collections.Album;
import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Collections.PlaylistOutput;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.Player.Player;
import app.Player.PlayerStats;
import app.searchBar.Filters;
import app.searchBar.SearchBar;
import app.utils.Enums;
import app.wrappedStats.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The type User.
 */
public class User {
    @Getter
    private String username;
    @Getter
    private int age;
    @Getter
    private String city;
    @Getter
    private ArrayList<Playlist> playlists;
    @Getter
    private ArrayList<Song> likedSongs;
    @Getter
    private ArrayList<Playlist> followedPlaylists;
    @Getter @Setter
    private String connection;
    @Getter @Setter
    private String role;
    @Getter @Setter
    private String currentPage;
    @Getter
    private final Player player;
    @Getter
    private Player tempPlayer = new Player();
    @Getter
    private ArrayList<WrappedEntity> listenedEntities;
    @Getter
    private ArrayList<ArtistHelper> artistHelper;
    @Getter
    private ArrayList<GenreHelper> genreHelper;
    @Getter
    private ArrayList<AlbumHelper> albumHelper;
    @Getter
    private int lastLoad;
    private final SearchBar searchBar;
    private boolean lastSearched;
    private static final int LIMIT = 5;

    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public User(final String username, final int age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.role = "user";
        this.currentPage = "Home";
        connection = "online";
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
        player = new Player();
        searchBar = new SearchBar(username);
        lastSearched = false;
    }

    public User(final String username, final int age, final String city, final String type) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.role = type;
        this.currentPage = "Home";
        connection = "online";
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
        player = new Player();
        searchBar = new SearchBar(username);
        lastSearched = false;
    }

    /**
     * Search array list.
     *
     * @param filters the filters
     * @param type    the type
     * @return the array list
     */
    public ArrayList<String> search(final Filters filters, final String type,
                                    final Admin admin, final int ts) {
        searchBar.clearSelection();
        // add entities to the user's wrappedEntity array
        addEntity(player, ts, admin);
        player.stop();
        tempPlayer.stop();

        lastSearched = true;
        ArrayList<String> results = new ArrayList<>();
        if (type.equals("song") || type.equals("podcast")
                || type.equals("playlist") || type.equals("album")) {
            List<LibraryEntry> libraryEntries
                    = searchBar.searchLibEntry(filters, type, admin);

            for (LibraryEntry libraryEntry : libraryEntries) {
                results.add(libraryEntry.getName());
            }
            return results;
        } else if (type.equals("artist") || type.equals("host")) {
            return searchBar.searchUserEntry(filters, type, admin);
        }
        return null;
    }

    /**
     * Adds songs/episodes to the user's listened entities
     * which counted as listens between last load timestamp
     * and the current one.
     *
     * @param crtPlayer the player
     * @param ts        the timestamp
     */
    public void addEntity(final Player crtPlayer, final int ts, final Admin admin) {
        if (crtPlayer == null) {
            return;
        } else {
            // case for album
            if (crtPlayer.getType() != null) {
                if (crtPlayer.getType().equals("album")) {
                    int interval = ts - this.lastLoad;
                    if (tempPlayer.getSource() != null) {
                        String name = tempPlayer.getSource().getAudioCollection().getName();
                        String owner = tempPlayer.getSource().getAudioCollection().getOwner();
                        // gets the album from the player
                        Album crtAlbum = admin.findAlbumInDatabase(name, owner);
                        if (crtAlbum != null && crtAlbum.getSongs() != null) {
                            for (Song song : crtAlbum.getSongs()) {
                                if (song.getDuration() <= interval) {
                                    int ok = 0;
                                    if (listenedEntities == null) {
                                        listenedEntities = new ArrayList<WrappedEntity>();
                                    }
                                    // checks if the current song is already in the entity list
                                    for (WrappedEntity entity : listenedEntities) {
                                        if (entity.getName().equals(song.getName())) {
                                            song.addListen();
                                            entity.addListen();
                                            crtAlbum.addListen();
                                            ok = 1;
                                            break;
                                        }
                                    }
                                    // if not, create it and add it
                                    if (ok == 0) {
                                        // make object with builder design pattern
                                        WrappedEntity newEntity = new WrappedEntity
                                                .Builder(song.getName(), 1)
                                                .artistGenreAlbum(song.getArtist(),
                                                        song.getGenre(), song.getAlbum()).build();
                                        listenedEntities.add(newEntity);
                                        crtAlbum.addListen();
                                        song.addListen();
                                    }
                                    // subtracts duration from interval to continue checking
                                    // if songs have been counted as a listen or not
                                    interval = interval - song.getDuration();
                                    if (interval < 0) {
                                        break;
                                    }
                                } else if (interval >= 0) {
                                    WrappedEntity newEntity = new WrappedEntity
                                            .Builder(song.getName(), 1)
                                            .artistGenreAlbum(song.getArtist(),
                                                    song.getGenre(), song.getAlbum()).build();
                                    if (listenedEntities != null) {
                                        listenedEntities.add(newEntity);
                                    }
                                    crtAlbum.addListen();
                                    song.addListen();
                                    interval = -1;
                                }
                            }
                        }
                    }
                } else if (player.getType().equals("song")) {
                    if (tempPlayer.getSource() != null) {
                        String name = tempPlayer.getSource().getAudioFile().getName();
                        // finds song in database
                        Song song = admin.findSongInDatabase(name);
                        Album crtAlbum = null;
                        if (song != null) {
                            song.addListen();
                            String albumName = song.getAlbum();
                            String artistName = song.getArtist();
                            crtAlbum = admin.findAlbumInDatabase(albumName, artistName);
                        }
                        // checks if the current song is already in the entity list
                        int ok = 0;
                        if (listenedEntities != null) {
                            for (WrappedEntity entity : listenedEntities) {
                                if (entity.getName().equals(name)) {
                                    entity.addListen();
                                    if (crtAlbum != null) {
                                        crtAlbum.addListen();
                                    }
                                    ok = 1;
                                    break;
                                }
                            }
                            if (ok == 0) {
                                if (song != null) {
                                    // make object
                                    WrappedEntity newEntity = new WrappedEntity
                                            .Builder(name, 1)
                                            .artistGenreAlbum(song.getArtist(),
                                                    song.getGenre(), song.getAlbum()).build();
                                    listenedEntities.add(newEntity);
                                    if (crtAlbum != null) {
                                        crtAlbum.addListen();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Select string.
     *
     * @param itemNumber the item number
     * @return the string
     */
    public String select(final int itemNumber) {
        if (!lastSearched) {
            return "Please conduct a search before making a selection.";
        }

        lastSearched = false;

        LibraryEntry selectedLibEntry = searchBar.selectLibEntry(itemNumber);
        String selectedUserEntry = searchBar.selectUserEntry(itemNumber);

        if (selectedLibEntry == null && selectedUserEntry == null) {
            return "The selected ID is too high.";
        } else if (selectedLibEntry == null) {
            // sets current user's page to the name of the artist/host
            setCurrentPage(selectedUserEntry);
            return "Successfully selected " + selectedUserEntry + "'s page.";
        } else {
            return "Successfully selected %s.".formatted(selectedLibEntry.getName());
        }
    }

    /**
     * Load string.
     *
     * @return the string
     */
    public String load(final int timestamp) {
        if (searchBar.getLastSelectedLibEntry() == null) {
            return "Please select a source before attempting to load.";
        }

        if (!searchBar.getLastSearchType().equals("song")
            && ((AudioCollection) searchBar.getLastSelectedLibEntry()).getNumberOfTracks() == 0) {
            return "You can't load an empty audio collection!";
        }

        player.setSource(searchBar.getLastSelectedLibEntry(), searchBar.getLastSearchType());
        tempPlayer.setSource(searchBar.getLastSelectedLibEntry(), searchBar.getLastSearchType());

        searchBar.clearSelection();

        player.pause();
        // gets the timestamp of the load for future commands such as "wrapped"
        // so that the songs / episodes which were listened to, are added
        this.lastLoad = timestamp;

        return "Playback loaded successfully.";
    }

    /**
     * Play pause string.
     *
     * @return the string
     */
    public String playPause() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to pause or resume playback.";
        }

        player.pause();

        if (player.getPaused()) {
            return "Playback paused successfully.";
        } else {
            return "Playback resumed successfully.";
        }
    }

    /**
     * Repeat string.
     *
     * @return the string
     */
    public String repeat() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before setting the repeat status.";
        }

        Enums.RepeatMode repeatMode = player.repeat();
        String repeatStatus = "";

        switch (repeatMode) {
            case NO_REPEAT -> {
                repeatStatus = "no repeat";
            }
            case REPEAT_ONCE -> {
                repeatStatus = "repeat once";
            }
            case REPEAT_ALL -> {
                repeatStatus = "repeat all";
            }
            case REPEAT_INFINITE -> {
                repeatStatus = "repeat infinite";
            }
            case REPEAT_CURRENT_SONG -> {
                repeatStatus = "repeat current song";
            }
            default -> {
                repeatStatus = "";
            }
        }

        return "Repeat mode changed to %s.".formatted(repeatStatus);
    }

    /**
     * Shuffle string.
     *
     * @param seed the seed
     * @return the string
     */
    public String shuffle(final Integer seed) {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before using the shuffle function.";
        }

        if (!player.getType().equals("playlist") && !player.getType().equals("album")) {
            return "The loaded source is not a playlist or an album.";
        }

        player.shuffle(seed);

        if (player.getShuffle()) {
            return "Shuffle function activated successfully.";
        }
        return "Shuffle function deactivated successfully.";
    }

    /**
     * Forward string.
     *
     * @return the string
     */
    public String forward() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to forward.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipNext();

        return "Skipped forward successfully.";
    }

    /**
     * Backward string.
     *
     * @return the string
     */
    public String backward() {
        if (player.getCurrentAudioFile() == null) {
            return "Please select a source before rewinding.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipPrev();

        return "Rewound successfully.";
    }

    /**
     * Like string.
     *
     * @return the string
     */
    public String like() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before liking or unliking.";
        }

        if (!player.getType().equals("song")
                && !player.getType().equals("playlist")
                && !player.getType().equals("album")) {
            return "Loaded source is not a song.";
        }

        Song song = (Song) player.getCurrentAudioFile();

        if (likedSongs.contains(song)) {
            likedSongs.remove(song);
            song.dislike();
            return "Unlike registered successfully.";
        }

        likedSongs.add(song);
        song.like();
        return "Like registered successfully.";
    }

    /**
     * Next string.
     *
     * @return the string
     */
    public String next() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        player.next();

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        return "Skipped to next track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Prev string.
     *
     * @return the string
     */
    public String prev() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before returning to the previous track.";
        }

        player.prev();

        return "Returned to previous track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Create playlist string.
     *
     * @param name      the name
     * @param timestamp the timestamp
     * @return the string
     */
    public String createPlaylist(final String name, final int timestamp) {
        if (playlists.stream().anyMatch(playlist -> playlist.getName().equals(name))) {
            return "A playlist with the same name already exists.";
        }

        playlists.add(new Playlist(name, username, timestamp));

        return "Playlist created successfully.";
    }

    /**
     * Add remove in playlist string.
     *
     * @param id the id
     * @return the string
     */
    public String addRemoveInPlaylist(final int id) {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before adding to or removing from the playlist.";
        }

        if (player.getType().equals("podcast")) {
            return "The loaded source is not a song.";
        }

        if (id > playlists.size()) {
            return "The specified playlist does not exist.";
        }

        Playlist playlist = playlists.get(id - 1);

        if (playlist.containsSong((Song) player.getCurrentAudioFile())) {
            playlist.removeSong((Song) player.getCurrentAudioFile());
            return "Successfully removed from playlist.";
        }

        playlist.addSong((Song) player.getCurrentAudioFile());
        return "Successfully added to playlist.";
    }

    /**
     * Switch playlist visibility string.
     *
     * @param playlistId the playlist id
     * @return the string
     */
    public String switchPlaylistVisibility(final Integer playlistId) {
        if (playlistId > playlists.size()) {
            return "The specified playlist ID is too high.";
        }

        Playlist playlist = playlists.get(playlistId - 1);
        playlist.switchVisibility();

        if (playlist.getVisibility() == Enums.Visibility.PUBLIC) {
            return "Visibility status updated successfully to public.";
        }

        return "Visibility status updated successfully to private.";
    }

    /**
     * Show playlists array list.
     *
     * @return the array list
     */
    public ArrayList<PlaylistOutput> showPlaylists() {
        ArrayList<PlaylistOutput> playlistOutputs = new ArrayList<>();
        for (Playlist playlist : playlists) {
            playlistOutputs.add(new PlaylistOutput(playlist));
        }

        return playlistOutputs;
    }

    /**
     * Follow string.
     *
     * @return the string
     */
    public String follow() {
        LibraryEntry selection = searchBar.getLastSelectedLibEntry();
        String type = searchBar.getLastSearchType();

        if (selection == null) {
            return "Please select a source before following or unfollowing.";
        }

        if (!type.equals("playlist")) {
            return "The selected source is not a playlist.";
        }

        Playlist playlist = (Playlist) selection;

        if (playlist.getOwner().equals(username)) {
            return "You cannot follow or unfollow your own playlist.";
        }

        if (followedPlaylists.contains(playlist)) {
            followedPlaylists.remove(playlist);
            playlist.decreaseFollowers();

            return "Playlist unfollowed successfully.";
        }

        followedPlaylists.add(playlist);
        playlist.increaseFollowers();


        return "Playlist followed successfully.";
    }

    /**
     * Gets player stats.
     *
     * @return the player stats
     */
    public PlayerStats getPlayerStats(final String connected) {
        return player.getStats(connected);
    }

    /**
     * Show preferred songs array list.
     *
     * @return the array list
     */
    public ArrayList<String> showPreferredSongs() {
        ArrayList<String> results = new ArrayList<>();
        for (AudioFile audioFile : likedSongs) {
            results.add(audioFile.getName());
        }

        return results;
    }

    /**
     * Gets preferred genre.
     *
     * @return the preferred genre
     */
    public String getPreferredGenre() {
        String[] genres = {"pop", "rock", "rap"};
        int[] counts = new int[genres.length];
        int mostLikedIndex = -1;
        int mostLikedCount = 0;

        for (Song song : likedSongs) {
            for (int i = 0; i < genres.length; i++) {
                if (song.getGenre().equals(genres[i])) {
                    counts[i]++;
                    if (counts[i] > mostLikedCount) {
                        mostLikedCount = counts[i];
                        mostLikedIndex = i;
                    }
                    break;
                }
            }
        }

        String preferredGenre = mostLikedIndex != -1 ? genres[mostLikedIndex] : "unknown";
        return "This user's preferred genre is %s.".formatted(preferredGenre);
    }

    /**
     * Simulate time.
     *
     * @param time the time
     */
    public void simulateTime(final int time) {
        if (getConnection().equals("online")) {
            player.simulatePlayer(time);
        }
    }

    /**
     * Switches connection status of the user
     *
     * @param user the user
     */
    public String switchConnectionStatus(final User user) {
        String currentStatus = user.getConnection();

        // changes status from online to offline or vice-versa
        if (currentStatus.equals("online")) {
            user.setConnection("offline");
            return user.getUsername() + " has changed status successfully.";
        } else {
            user.setConnection("online");
            return user.getUsername() + " has changed status successfully.";
        }
    }

    /**
     * Creates new user
     *
     * @param commandInput the command
     * @return new user
     */
    public static User createUser(final CommandInput commandInput) {
        String username = commandInput.getUsername();
        String type = commandInput.getType();
        int age = commandInput.getAge();
        String city = commandInput.getCity();

        return UserFactory.createNewUser(type, username, age, city);
    }

    /**
     * Makes a String array list of the top 5 liked songs from
     * the user's liked songs list
     *
     * @return top 5 most liked songs from the user's liked songs
     */
    public ArrayList<String> get5LikedSongs() {
        if (getLikedSongs() != null) {
            ArrayList<Song> likedSongsUser = getLikedSongs();
            ArrayList<Song> top5Songs = new ArrayList<>(likedSongsUser);
            top5Songs.sort(Comparator.comparingInt(Song::getLikes).reversed());
            ArrayList<String> top5SongsNames = new ArrayList<>();

            for (int i = 0; i < top5Songs.size(); i++) {
                if (i == LIMIT) {
                    break;
                }
                top5SongsNames.add(top5Songs.get(i).getName());
            }
            return top5SongsNames;
        }
        return null;
    }

    /**
     * @return top 5 followed playlists by a user with most likes
     */
    public ArrayList<String> get5LikedPlaylists() {
        if (getFollowedPlaylists() != null) {
            ArrayList<Playlist> followedPlists = getFollowedPlaylists();
            for (Playlist playlist : followedPlists) {
                playlist.computeTotalLikes();
            }
            ArrayList<Playlist> top5Playlists = new ArrayList<>(followedPlists);
            top5Playlists.sort(Comparator.comparingInt(Playlist::getTotalLikes).reversed());
            ArrayList<String> top5LikedPlaylists = new ArrayList<>();
            for (int i = 0; i < top5Playlists.size(); i++) {
                if (i == LIMIT) {
                    break;
                }
                top5LikedPlaylists.add(top5Playlists.get(i).getName());
            }
            return top5LikedPlaylists;
        }
        return null;
    }

    /**
     * Changes the page of a normal user.
     *
     * @param commandInput the command input
     * @return             String message
     */
    public String changePage(final CommandInput commandInput) {
        String nextPage = commandInput.getNextPage();
        this.setCurrentPage(nextPage);
        if (nextPage.equals("Home") || nextPage.equals("LikedContent")) {
            return getUsername() + " accessed "
                    + getCurrentPage() + " successfully.";
        } else {
            return getUsername() + " is trying to access a non-existent page";
        }
    }

    /**
     * Produces the array node for the wrapped function.
     *
     * @param crtRole  the role
     * @param name     the username
     * @param ts       the timestamp
     * @param admin    the admin
     * @return         the array node
     */
    public ObjectNode wrapped(final String crtRole, final String name,
                             final int ts, final Admin admin) {
        // Before making the arrayNode, the list of listened entities has to be updated.
        // If the wrapped command is called by a special user,
        // the wrapped for all users has to be updated
        if (crtRole.equals("artist")) {
            addEntity(player, ts, admin);
            admin.updateWrapped(ts);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        switch (crtRole) {
            case "user":
                ObjectNode topArtists = top5ListenedArtists();
                node.putPOJO("topArtists", topArtists);
                ObjectNode topGenres = top5Genres();
                node.putPOJO("topGenres", topGenres);
                ObjectNode topSongs = top5Songs();
                node.putPOJO("topSongs", topSongs);
                ObjectNode topAlbums = top5Albums();
                node.putPOJO("topAlbums", topAlbums);
                ObjectNode topPodcasts = top5Podcasts();
                node.putPOJO("topEpisodes", topPodcasts);
                return node;
            case "artist":
                ObjectNode topArtistAlbums = top5ArtistAlbums();
                node.putPOJO("topAlbums", topArtistAlbums);
                ObjectNode topArtistSongs = top5ArtistSongs(admin);
                node.putPOJO("topSongs", topArtistSongs);
                ArrayList<String> topFans = top5Fans(admin);
                node.putPOJO("topFans", topFans);
                int listeners = listenersCount(admin);
                node.putPOJO("listeners", listeners);
                return node;
            case "host":
                // still not implemented
                break;
            default:
                break;
        }
        return node;
    }

    /**
     * Gets top 5 most listened artists for the user
     *
     * @return the object node
     */
    public ObjectNode top5ListenedArtists() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        if (artistHelper == null) {
            artistHelper = new ArrayList<ArtistHelper>();
        }
        // adds all artists from listenedEntities to artistHelper only once
        // and adds all their listens together
        if (listenedEntities != null) {
            for (WrappedEntity entity : listenedEntities) {
                int ok = 0;
                for (ArtistHelper artist : artistHelper) {
                    if (artist.getName().equals(entity.getArtist())) {
                        artist.setListens(artist.getListens() + entity.getListens());
                        ok = 1;
                    }
                }
                if (ok == 0) {
                    if (entity.getArtist() != null) {
                        ArtistHelper artist
                                = new ArtistHelper(entity.getArtist(), entity.getListens());
                        artistHelper.add(artist);
                    }
                }
            }

            artistHelper.sort(Comparator.comparingInt(ArtistHelper::getListens).reversed());

            if (artistHelper.size() > LIMIT) {
                for (int i = 0; i < LIMIT; i++) {
                    node.put(artistHelper.get(i).getName(), artistHelper.get(i).getListens());
                }
            } else {
                for (ArtistHelper artist : artistHelper) {
                    node.putPOJO(artist.getName(), artist.getListens());
                }
            }
            return node;
        }
        return null;
    }

    /**
     * Gets the most 5 listened genres for a user
     *
     * @return the object node
     */
    public ObjectNode top5Genres() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        if (genreHelper == null) {
            genreHelper = new ArrayList<GenreHelper>();
        }
        // adds all genres from listenedEntities to genreHelper only once
        // and adds all their listens together
        if (listenedEntities != null) {
            for (WrappedEntity entity : listenedEntities) {
                int ok = 0;
                for (GenreHelper genre : genreHelper) {
                    if (genre.getName().equals(entity.getGenre())) {
                        genre.setListens(genre.getListens() + entity.getListens());
                        ok = 1;
                    }
                }
                if (ok == 0) {
                    if (entity.getGenre() != null) {
                        GenreHelper genre = new GenreHelper(entity.getGenre(), entity.getListens());
                        genreHelper.add(genre);
                    }
                }
            }

            genreHelper.sort(Comparator.comparingInt(GenreHelper::getListens).reversed());

            if (genreHelper.size() > LIMIT) {
                for (int i = 0; i < LIMIT; i++) {
                    node.put(genreHelper.get(i).getName(), genreHelper.get(i).getListens());
                }
            } else {
                for (GenreHelper genre : genreHelper) {
                    node.put(genre.getName(), genre.getListens());
                }
            }
            return node;
        }
        return null;
    }

    /**
     * Gets the top 5 most listened songs by a user
     *
     * @return the object node
     */
    public ObjectNode top5Songs() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        // select only the songs from listened Entities
        ArrayList<WrappedEntity> listenedSongs = new ArrayList<>();
        if (listenedEntities != null) {
            for (WrappedEntity entity : listenedEntities) {
                if (entity.getArtist() != null) {
                    listenedSongs.add(entity);
                }
            }
        }
        if (listenedSongs != null && listenedSongs.size() > 0) {
            listenedSongs.sort(Comparator.comparingInt(WrappedEntity::getListens).reversed());
            for (int i = 0; i < listenedSongs.size() - 1; i++) {
                for (int j = i + 1; j < listenedSongs.size(); j++) {
                    if (listenedSongs.get(i).getListens() == listenedSongs.get(j).getListens()) {
                        if (listenedSongs.get(i).getName()
                                .compareTo(listenedSongs.get(j).getName()) > 0) {
                            // Entity swap
                            WrappedEntity temp = listenedSongs.get(i);
                            listenedSongs.set(i, listenedSongs.get(j));
                            listenedSongs.set(j, temp);
                        }
                    }
                }
            }
            if (listenedSongs.size() > LIMIT) {
                for (int i = 0; i < LIMIT; i++) {
                    node.put(listenedSongs.get(i).getName(), listenedSongs.get(i).getListens());
                }
            } else {
                for (WrappedEntity song : listenedSongs) {
                    node.put(song.getName(), song.getListens());
                }
            }
            return node;
        }
        return null;
    }

    /**
     * Gets the top 5 albums based on listens.
     *
     * @return the object node
     */
    public ObjectNode top5Albums() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        if (albumHelper == null) {
            albumHelper = new ArrayList<AlbumHelper>();
        }
        // adds all albums from listenedEntities to albumHelper only once
        // and adds all their listens together
        if (listenedEntities != null) {
            for (WrappedEntity entity : listenedEntities) {
                int ok = 0;
                for (AlbumHelper album : albumHelper) {
                    if (album.getName().equals(entity.getAlbum())) {
                        album.setListens(album.getListens() + entity.getListens());
                        ok = 1;
                    }
                }
                if (ok == 0) {
                    if (entity.getAlbum() != null) {
                        AlbumHelper album = new AlbumHelper(entity.getAlbum(), entity.getListens());
                        albumHelper.add(album);
                    }
                }
            }

            albumHelper.sort(Comparator.comparingInt(AlbumHelper::getListens).reversed());

            if (albumHelper.size() > LIMIT) {
                for (int i = 0; i < LIMIT; i++) {
                    node.put(albumHelper.get(i).getName(), albumHelper.get(i).getListens());
                }
            } else {
                for (AlbumHelper album : albumHelper) {
                    node.put(album.getName(), album.getListens());
                }
            }
            return node;
        }
        return null;
    }

    /**
     * Gets top 5 podcasts based on listens.
     *
     * @return the object node
     */
    public ObjectNode top5Podcasts() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        // select only the songs from listened Entities
        ArrayList<WrappedEntity> listenedPodcasts = new ArrayList<>();
        if (listenedEntities != null) {
            for (WrappedEntity entity : listenedEntities) {
                if (entity.getHost() != null) {
                    listenedPodcasts.add(entity);
                }
            }
        }
        if (listenedPodcasts != null && listenedPodcasts.size() > 0) {
            listenedPodcasts.sort(Comparator.comparingInt(WrappedEntity::getListens).reversed());
            int maximum = listenedPodcasts.get(0).getListens();
            ArrayList<WrappedEntity> podcastHelper = new ArrayList<>();
            for (WrappedEntity entity : listenedPodcasts) {
                if (entity.getListens() == maximum) {
                    podcastHelper.add(entity);
                }
            }
            podcastHelper.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));
            if (podcastHelper.size() > LIMIT) {
                for (int i = 0; i < LIMIT; i++) {
                    node.put(podcastHelper.get(i).getName(), podcastHelper.get(i).getListens());
                }
            } else {
                for (WrappedEntity song : podcastHelper) {
                    node.put(song.getName(), song.getListens());
                }
            }
            return node;
        }
        return node;
    }

    /**
     * Gets top 5 albums by listens for an artist
     *
     * @return the object node
     */
    public ObjectNode top5ArtistAlbums() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        if (getRole().equals("artist")) {
            Artist artist = (Artist) this;
            if (artist.getAlbums() != null) {
                artist.getAlbums().sort(Comparator.comparingInt(Album::getListens).reversed());

                if (artist.getAlbums().size() > LIMIT) {
                    for (int i = 0; i < LIMIT; i++) {
                        node.put(artist.getAlbums().get(i).getName(),
                                artist.getAlbums().get(i).getListens());
                    }
                } else {
                    for (Album album : artist.getAlbums()) {
                        node.put(album.getName(), album.getListens());
                    }
                }
            }
        }
        return node;
    }

    /**
     * Gets the top 5 listened songs for an artist based on listens
     *
     * @return the object node
     */
    public ObjectNode top5ArtistSongs(final Admin admin) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();

        if (getRole().equals("artist")) {
            Artist artist = (Artist) admin.getUser(getUsername());
            admin.gatherSongs(artist);

            if (artist != null && artist.getListenedSongs() != null) {
                artist.getListenedSongs().sort(Comparator.
                        comparingInt(SongHelper::getListens).reversed());
                for (int i = 0; i < artist.getListenedSongs().size() - 1; i++) {
                    for (int j = i + 1; j < artist.getListenedSongs().size(); j++) {
                        if (artist.getListenedSongs().get(i).getListens()
                                == artist.getListenedSongs().get(j).getListens()) {
                            if (artist.getListenedSongs().get(i).getName()
                                    .compareTo(artist.getListenedSongs().get(j).getName()) > 0) {
                                // Entity swap
                                SongHelper temp = artist.getListenedSongs().get(i);
                                artist.getListenedSongs().set(i, artist.getListenedSongs().get(j));
                                artist.getListenedSongs().set(j, temp);
                            }
                        }
                    }
                }
                if (artist.getListenedSongs().size() > LIMIT) {
                    for (int i = 0; i < LIMIT; i++) {
                        node.put(artist.getListenedSongs().get(i).getName(),
                                artist.getListenedSongs().get(i).getListens());
                    }
                } else {
                    for (SongHelper song : artist.getListenedSongs()) {
                        node.put(song.getName(), song.getListens());
                    }
                }
            }
        }
        return node;
    }

    /**
     * Gets the names of the top 5 fans for an artist.
     *
     * @return String array list
     */
    public ArrayList<String> top5Fans(final Admin admin) {
        ArrayList<String> top5fans = new ArrayList<>();
        if (getRole().equals("artist")) {
            Artist artist = (Artist) admin.getUser(getUsername());
            admin.gatherFans(artist);

            if (artist != null && artist.getFans() != null) {
                artist.getFans().sort(Comparator
                        .comparingInt(ListenersHelper::getListens).reversed());
                if (artist.getFans().size() > LIMIT) {
                    for (int i = 0; i < LIMIT; i++) {
                        top5fans.add(artist.getFans().get(i).getUsername());
                    }
                } else {
                    for (ListenersHelper fan : artist.getFans()) {
                        top5fans.add(fan.getUsername());
                    }
                }
            }
        }
        return top5fans;
    }

    /**
     * Gets the number of listeners an artist has
     *
     * @param admin the admin
     * @return      the number of listeners (int)
     */
    public int listenersCount(final Admin admin) {
        if (getRole().equals("artist")) {
            Artist artist = (Artist) admin.getUser(getUsername());
            if (artist != null) {
                return artist.getListeners();
            }
        }
        return 0;
    }
}
