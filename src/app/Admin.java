package app;

import app.User.Artist;
import app.User.Host;
import app.audio.Collections.Album;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.User.User;
import app.Player.PlayerSource;
import app.utils.Enums;
import app.wrappedStats.WrappedEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The type Admin.
 */
public final class Admin {
    @Getter
    private static List<User> users = new ArrayList<>();
    private static List<Song> songs = new ArrayList<>();
    private static List<Podcast> podcasts = new ArrayList<>();
    private static List<Album> albums = new ArrayList<>();
    private static int timestamp = 0;
    private static final int LIMIT = 5;

    // Singleton implementation for admin
    private static final Admin INSTANCE = new Admin();

    private Admin() {
    }

    public static Admin getInstance() {
        return INSTANCE;
    }

    /**
     * Sets users.
     *
     * @param userInputList the user input list
     */
    public void setUsers(final List<UserInput> userInputList) {
        users = new ArrayList<>();
        for (UserInput userInput : userInputList) {
            users.add(new User(userInput.getUsername(),
                    userInput.getAge(), userInput.getCity(), "user"));
        }
    }

    /**
     * Sets songs.
     *
     * @param songInputList the song input list
     */
    public void setSongs(final List<SongInput> songInputList) {
        songs = new ArrayList<>();
        for (SongInput songInput : songInputList) {
            songs.add(new Song(songInput.getName(), songInput.getDuration(), songInput.getAlbum(),
                    songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                    songInput.getReleaseYear(), songInput.getArtist()));
        }
    }


    /**
     * Sets podcasts.
     *
     * @param podcastInputList the podcast input list
     */
    public void setPodcasts(final List<PodcastInput> podcastInputList) {
        podcasts = new ArrayList<>();
        for (PodcastInput podcastInput : podcastInputList) {
            List<Episode> episodes = new ArrayList<>();
            for (EpisodeInput episodeInput : podcastInput.getEpisodes()) {
                episodes.add(new Episode(episodeInput.getName(),
                                         episodeInput.getDuration(),
                                         episodeInput.getDescription()));
            }
            podcasts.add(new Podcast(podcastInput.getName(), podcastInput.getOwner(), episodes));
        }
    }

    /**
     * Gets songs.
     *
     * @return the songs
     */
    public List<Song> getSongs() {
        return new ArrayList<>(songs);
    }

    /**
     * Gets podcasts.
     *
     * @return the podcasts
     */
    public List<Podcast> getPodcasts() {
        return new ArrayList<>(podcasts);
    }

    /**
     * Gets playlists.
     *
     * @return the playlists
     */
    public List<Playlist> getPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        for (User user : users) {
            playlists.addAll(user.getPlaylists());
        }
        return playlists;
    }

    /**
     * Gets albums.
     *
     * @return the albums
     */
    public List<Album> getAlbums() {
        List<Album> albums2 = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals("artist")) {
                Artist artist = (Artist) user;
                if (artist.getAlbums() != null) {
                    albums2.addAll(artist.getAlbums());
                }
            }
        }
        return albums2;
    }

    /**
     * Gets user.
     *
     * @param username the username
     * @return the user
     */
    public User getUser(final String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Update timestamp.
     *
     * @param newTimestamp the new timestamp
     */
    public void updateTimestamp(final int newTimestamp) {
        int elapsed = newTimestamp - timestamp;
        timestamp = newTimestamp;
        if (elapsed == 0) {
            return;
        }

        for (User user : users) {
            user.simulateTime(elapsed);
        }
    }

    /**
     * Gets top 5 songs.
     *
     * @return the top 5 songs
     */
    public List<String> getTop5Songs() {
        for (Album album : albums) {
            if (album.getSongs() != null) {
                songs.addAll(album.getSongs());
            }
        }
        List<Song> sortedSongs = new ArrayList<>(songs);
        sortedSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());
        List<String> topSongs = new ArrayList<>();
        int count = 0;
        for (Song song : sortedSongs) {
            if (count >= LIMIT) {
                break;
            }
            topSongs.add(song.getName());
            count++;
        }
        return topSongs;
    }

    /**
     * Gets top 5 playlists.
     *
     * @return the top 5 playlists
     */
    public List<String> getTop5Playlists() {
        List<Playlist> sortedPlaylists = new ArrayList<>(getPlaylists());
        sortedPlaylists.sort(Comparator.comparingInt(Playlist::getFollowers)
                .reversed()
                .thenComparing(Playlist::getTimestamp, Comparator.naturalOrder()));
        List<String> topPlaylists = new ArrayList<>();
        int count = 0;
        for (Playlist playlist : sortedPlaylists) {
            if (count >= LIMIT) {
                break;
            }
            topPlaylists.add(playlist.getName());
            count++;
        }
        return topPlaylists;
    }

    /**
     * Gets top 5 albums.
     *
     * @return the top 5 albums
     */
    public List<String> getTop5Albums() {
        for (Album album : albums) {
            album.computeLikes();
        }
        List<Album> sortedAlbums = new ArrayList<>(albums);
        sortedAlbums.sort(Comparator.comparingInt(Album::getTotalLikes)
                .reversed().thenComparing(Album::getName));

        List<String> sortedAlbumsNames = new ArrayList<>();
        for (Album album : sortedAlbums) {
            sortedAlbumsNames.add(album.getName());
        }
        if (sortedAlbumsNames.size() > LIMIT) {
            return sortedAlbumsNames.subList(0, LIMIT);
        } else {
            return sortedAlbumsNames;
        }
    }

    /**
     * Gets top 5 artists.
     *
     * @return the top 5 arists
     */
    public List<String> getTop5Artists() {
        List<Artist> artists = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals("artist")) {
                ((Artist) user).computeLikes();
                artists.add((Artist) user);
            }
        }
        artists.sort(Comparator.comparingInt(Artist::getTotalLikes).reversed());
        List<String> sortedArtists = new ArrayList<>();
        for (Artist artist : artists) {
            sortedArtists.add(artist.getUsername());
        }

        if (sortedArtists.size() > LIMIT) {
            return sortedArtists.subList(0, LIMIT);
        } else {
            return sortedArtists;
        }
    }

    /**
     * Gets all normal online users
     *
     * @return the online users
     */
    public ArrayList<String> getOnlineUsers() {
        ArrayList<String> onlineUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getConnection().equals("online")) {
                onlineUsers.add(user.getUsername());
            }
        }
        return onlineUsers;
    }

    /**
     * Adds new user to the list of users
     *
     * @param user the new user
     */
    public String addUser(final User user) {
        // user is added only if they don't exist
        if (user != null && userExists(user) == 0) {
            if (user.getRole().equals("user")) {
                users.add(user);
                user.setConnection("online");
            }
            if (user.getRole().equals("artist")) {
                Artist artist = (Artist) user;
                users.add(artist);
                artist.setConnection("offline");
            }
            if (user.getRole().equals("host")) {
                Host host = (Host) user;
                users.add(host);
                host.setConnection("offline");
            }
            return "The username " + user.getUsername() + " has been added successfully.";
        } else if (user != null) {
            return "The username " + user.getUsername() + " is already taken.";
        }
        return null;
    }

    /**
     * Checks if a user already exists
     *
     * @param crtUser the user
     * @return 1 if user exists already, 0 otherwise
     */
    private int userExists(final User crtUser) {
        if (crtUser != null) {
            for (User user : users) {
                if (user.getUsername().equals(crtUser.getUsername())) {
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * Adds the songs from the albums to the database
     *
     * @param songList the list of songs
     */
    public void addSongsToDatabase(final ArrayList<SongInput> songList) {
        for (SongInput songInput : songList) {
            Song newSong = new Song(songInput.getName(),
                    songInput.getDuration(), songInput.getAlbum(),
                    songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                    songInput.getReleaseYear(), songInput.getArtist());
            songs.add(newSong);
        }
    }

    /**
     * Adds an album to the database.
     *
     * @param album the album
     */
    public void addAlbumToDatabase(final Album album) {
        albums.add(album);
    }

    /**
     * Removes an album from the database
     *
     * @param album the album
     */
    public void removeAlbumFromDatabase(final Album album) {
        albums.remove(album);
    }

    /**
     * Gets all artists from the database
     *
     * @return array list of type Artist
     */
    private ArrayList<Artist> getArtists() {
        ArrayList<Artist> allArtists = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals("artist")) {
                allArtists.add((Artist) user);
            }
        }
        return allArtists;
    }

    /**
     * Gets all hosts from the database.
     *
     * @return array list of type Host
     */
    private ArrayList<Host> getHosts() {
        ArrayList<Host> allHosts = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals("host")) {
                allHosts.add((Host) user);
            }
        }
        return allHosts;
    }

    /**
     * Searches for artists whose usernames begin with start
     *
     * @param start is the beginning of the username
     * @return the artists after filters were applied
     */
    public ArrayList<String> filterByUsername(final String type, final String start) {
        if (type.equals("artist")) {
            ArrayList<Artist> allArtists = getArtists();
            ArrayList<String> artists = new ArrayList<>();
            for (Artist artist : allArtists) {
                if (artist.getUsername().startsWith(start)) {
                    artists.add(artist.getUsername());
                }
            }
            if (artists.size() > LIMIT) {
                ArrayList<String> filteredArtists = new ArrayList<>();
                for (int i = 0; i < LIMIT; i++) {
                    filteredArtists.add(artists.get(i));
                }
                return filteredArtists;
            } else {
                return artists;
            }
        } else if (type.equals("host")) {
            ArrayList<Host> allHosts = getHosts();
            ArrayList<String> hosts = new ArrayList<>();
            for (Host host : allHosts) {
                if (host.getUsername().startsWith(start)) {
                    hosts.add(host.getUsername());
                }
            }
            if (hosts.size() > LIMIT) {
                ArrayList<String> filteredHosts = new ArrayList<>();
                for (int i = 0; i < LIMIT; i++) {
                    filteredHosts.add(hosts.get(i));
                }
                return filteredHosts;
            } else {
                return hosts;
            }
        }
        return null;
    }

    /**
     * Loads users from the app: normal users first,
     * then artists followed by hosts
     *
     * @return ArrayList containing users
     */
    public ArrayList<String> getAllUsers() {
        // make 3 array lists which will be merged
        ArrayList<String> normalUsers = new ArrayList<>();
        ArrayList<String> artists = new ArrayList<>();
        ArrayList<String> hosts = new ArrayList<>();

        for (User user : users) {
            if (user.getRole().equals("user")) {
                normalUsers.add(user.getUsername());
            } else if (user.getRole().equals("artist")) {
                artists.add(user.getUsername());
            } else if (user.getRole().equals("host")) {
                hosts.add(user.getUsername());
            }
        }

        // merge the arrays
        ArrayList<String> allUsersSorted = new ArrayList<>();
        allUsersSorted.addAll(normalUsers);
        allUsersSorted.addAll(artists);
        allUsersSorted.addAll(hosts);

        return allUsersSorted;
    }

    /**
     * Removes a user and returns a message. If the user
     * cannot be removed, returns another message.
     *
     * @param crtUser the user
     * @return String message
     */
    public String deleteUser(final User crtUser) {
        // first we check each user's current playing instance
        for (User user : users) {
            if (user.getPlayer().getSource() != null) {
                if (user.getPlayer().getSource().getAudioFile().getType().equals("song")) {
                    Song crtSong = (Song) user.getPlayer().getSource().getAudioFile();
                    if (crtSong.getArtist().equals(crtUser.getUsername())) {
                        return crtUser.getUsername() + " can't be deleted.";
                    }
                }
                if (user.getPlayer().getSource().getType()
                        .equals(Enums.PlayerSourceType.PODCAST)) {
                    Podcast crtPodcast
                            = (Podcast) user.getPlayer().getSource().getAudioCollection();
                    if (crtPodcast.getOwner().equals(crtUser.getUsername())) {
                        return crtUser.getUsername() + " can't be deleted.";
                    }
                }
                if (user.getPlayer().getSource().getType()
                        .equals(Enums.PlayerSourceType.PLAYLIST)) {
                    Playlist crtPlaylist
                            = (Playlist) user.getPlayer().getSource().getAudioCollection();
                    if (crtPlaylist.getOwner().equals(crtUser.getUsername())) {
                        return crtUser.getUsername() + " can't be deleted.";
                    }
                }
            }
        }
        // then we check if any other user is on the crtUser's page
        for (User user : users) {
            if (user.getCurrentPage().equals(crtUser.getUsername())) {
                return crtUser.getUsername() + " can't be deleted.";
            }
        }
        deleteConnections(crtUser);
        users.remove(crtUser);
        return crtUser.getUsername() + " was successfully deleted.";
    }

    /**
     * Removes any connection between the deleted user
     * and the rest of the users
     *
     * @param crtUser the current user
     */
    private void deleteConnections(final User crtUser) {
        // user - user connections
        for (User user : users) {
            // First, we remove crtUser from all users' liked songs
            if (user.getLikedSongs() != null) {
                user.getLikedSongs().removeIf(song -> {
                    return song.getArtist().equals(crtUser.getUsername());
                });
            }
            // Secondly, we remove crtUser from all users' playlists
            if (user.getPlaylists() != null) {
                for (Playlist playlist : user.getPlaylists()) {
                    if (playlist.getSongs() != null) {
                        playlist.getSongs().removeIf(song -> {
                            return song.getArtist().equals(crtUser.getUsername());
                        });
                    }
                }
            }
            // Thirdly, we remove every user's followed playlists where the owner is crtUser
            if (user.getFollowedPlaylists() != null) {
                user.getFollowedPlaylists().removeIf(playlist -> {
                    return playlist.getOwner().equals(crtUser.getUsername());
                });
            }
            // we decrease the number of followers for each playlist that crtUser follows
            if (crtUser.getFollowedPlaylists() != null) {
                for (Playlist playlist : crtUser.getFollowedPlaylists()) {
                    if (playlist.getFollowers() > 0) {
                        playlist.decreaseFollowers();
                    }
                }
            }
        }

        // user - database connections
        songs.removeIf(song -> song.getArtist().equals(crtUser.getUsername()));
        podcasts.removeIf(podcast -> podcast.getOwner().equals(crtUser.getUsername()));
        albums.removeIf(album -> album.getOwner().equals(crtUser.getUsername()));
    }

    /**
     * Adds new podcast to the database list of podcasts.
     *
     * @param podcast the podcast
     */
    public void addPodcasts(final Podcast podcast) {
        podcasts.add(podcast);
    }

    /**
     * Finds a certain album by a certain artist
     * in the database
     *
     * @param name   the name of the album
     * @param artist the name of the artist
     * @return       album
     */
    public Album findAlbumInDatabase(final String name, final Artist artist) {
        for (User user : users) {
            if (user.getRole().equals("artist")
                    && user.getUsername().equals(artist.getUsername())) {
                Artist crtArtist = (Artist) user;
                if (artist.getAlbums() != null) {
                    for (Album album : artist.getAlbums()) {
                        if (album.getName().equals(name)) {
                            return album;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds a certain album by a certain artist
     * in the database
     *
     * @param name   the name of the album
     * @param artist the name of the artist
     * @return       album
     */
    public Album findAlbumInDatabase(final String name, final String artist) {
        for (User user : users) {
            if (user.getRole().equals("artist")
                    && user.getUsername().equals(artist)) {
                Artist crtArtist = (Artist) user;
                if (crtArtist.getAlbums() != null) {
                    for (Album album : crtArtist.getAlbums()) {
                        if (album.getName().equals(name)) {
                            return album;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if an album or any song from it is
     * played or if a song from it is part of any
     * user's playlist.
     *
     * @param album the album
     * @return      1 if album is played, 0 otherwise
     */
    public int isPlayedAlbum(final Album album) {
        for (User user : users) {
            // checks if a user has a playlist with a song
            // from the album contained in it, return 1 if yes
            if (playlistContainsAlbumSong(album, user) == 1) {
                return 1;
            } else if (user.getCurrentPage().equals(album.getOwner())) {
                return 1;
            } else if (user.getPlayer() != null) {
                // checks for all user's currently loaded source
                if (user.getPlayer().getSource() != null) {
                    if (user.getPlayer().getSource().getType()
                            .equals(Enums.PlayerSourceType.ALBUM)) {
                        String userAlbum
                                = user.getPlayer().getSource().getAudioCollection().getName();
                        String userAlbumOwner
                                = user.getPlayer().getSource().getAudioCollection().getOwner();
                        if (userAlbum.equals(album.getName())
                            && userAlbumOwner.equals(album.getOwner())) {
                            return 1;
                        }
                    } else if (user.getPlayer().getSource().getType()
                            .equals(Enums.PlayerSourceType.LIBRARY)) {
                        if (user.getPlayer().getSource().getAudioFile().getType().equals("SONG")) {
                            String songName = user.getPlayer().getSource().getAudioFile().getName();
                            if (album.containsSong(songName) == 1) {
                                return 1;
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Checks if any playlist contains an album song.
     *
     * @param album the album
     * @param user  the user
     * @return      1 if yes, 0 if not
     */
    private int playlistContainsAlbumSong(final Album album, final User user) {
        if (user.getPlaylists() != null) {
            ArrayList<Playlist> playlists = user.getPlaylists();
            for (Playlist playlist : playlists) {
                if (album.getSongs() != null) {
                    for (Song song : album.getSongs()) {
                        if (playlist.containsSong(song)) {
                            return 1;
                        }
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Finds a certain podcast by a certain owner
     * in the database
     *
     * @param name   the name of the album
     * @param host   the name of the artist
     * @return       podcast
     */
    public Podcast findPodcastInDatabase(final String name, final Host host) {
        for (Podcast podcast : podcasts) {
            if (podcast.getName().equals(name)
                && podcast.getOwner().equals(host.getUsername())) {
                return podcast;
            }
        }
        return null;
    }

    /**
     * Checks if a podcast is loaded
     * by any user at that moment
     *
     * @param podcast the podcast
     * @return        1 if album is played, 0 otherwise
     */
    public int isPlayedPodcast(final Podcast podcast) {
        for (User user : users) {
            if (user.getPlayer().getSource() != null) {
                if (user.getPlayer().getSource().getType().equals(Enums.PlayerSourceType.PODCAST)) {
                    String name = podcast.getName();
                    String owner = podcast.getOwner();
                    PlayerSource crtSource = user.getPlayer().getSource();
                    if (crtSource.getAudioCollection().getName().equals(name)
                        && crtSource.getAudioCollection().getOwner().equals(owner)) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * When a podcast is removed from a host,
     * it has to be removed from the database too.
     *
     * @param podcast the podcast
     */
    public void removePodcastFromDatabase(final Podcast podcast) {
        podcasts.removeIf(crtPodcast -> {
            return crtPodcast.getName().equals(podcast.getName())
                    && crtPodcast.getOwner().equals(podcast.getOwner());
        });
    }

    /**
     * When an album is removed from an artist, all
     * its songs have to be removed from the database.
     *
     * @param album the album
     */
    public void removeAlbumSongsFromDatabase(final Album album) {
        if (album.getSongs() != null) {
            ArrayList<Song> albumSongs = album.getSongs();
            for (Song song : albumSongs) {
                songs.removeIf(dbSong -> {
                    return dbSong.getName().equals(song.getName())
                            && dbSong.getArtist().equals(song.getArtist());
                });
            }
        }
    }

    /**
     * Finds song in database.
     *
     * @param name the name
     * @return     the song
     */
    public Song findSongInDatabase(final String name) {
        for (Song song : songs) {
            if (song.getName().equals(name)) {
                return song;
            }
        }
        return null;
    }

    /**
     * Updates entity list for all users.
     *
     * @param ts the timestamp
     */
    public void updateWrapped(final int ts) {
        for (User user : users) {
            user.addEntity(user.getPlayer(), ts, this);
        }
    }

    /**
     * Sums up all listens for every song for a specific artist.
     *
     * @param artist the artist
     */
    public void gatherSongs(final Artist artist) {
        for (User user : users) {
            if (user.getListenedEntities() != null) {
                for (WrappedEntity entity : user.getListenedEntities()) {
                    if (entity.getArtist().equals(artist.getUsername())) {
                        artist.addListenedSongs(entity);
                    }
                }
            }
        }
    }

    /**
     * Gets top 5 fans who listened to an artist
     *
     * @param artist the artist
     */
    public void gatherFans(final Artist artist) {
        for (User user : users) {
            if (user.getListenedEntities() != null) {
                for (WrappedEntity entity : user.getListenedEntities()) {
                    if (entity.getArtist().equals(artist.getUsername())) {
                        artist.addFans(user.getUsername(), entity.getListens());
                    }
                }
            }
        }
    }

    /**
     * endProgram object node
     *
     * @return the object node
     */
    public ObjectNode endProgram() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode main = objectMapper.createObjectNode();
        // artists contains all artists from the platform, sorted lexicographically
        ArrayList<String> artists = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals("artist")) {
                Artist artist = (Artist) user;
                if (artist.getListeners() > 0) {
                    artists.add(user.getUsername());
                }
            }
        }
        artists.sort(String::compareTo);
        int rank = 0;
        for (String artist : artists) {
            rank++;
            ObjectNode node = objectMapper.createObjectNode();
            node.put("merchRevenue", 0.0);
            node.put("songRevenue", 0.0);
            node.put("ranking", rank);
            node.put("mostProfitableSong", "N/A");
            main.putPOJO(artist, node);
        }
        return main;
    }

    /**
     * Reset.
     */
    public void reset() {
        users = new ArrayList<>();
        songs = new ArrayList<>();
        podcasts = new ArrayList<>();
        albums = new ArrayList<>();
        timestamp = 0;
    }
}
