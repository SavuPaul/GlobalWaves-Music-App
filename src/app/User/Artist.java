package app.User;

import app.Admin;
import app.SpecialUsersEvents.Event;
import app.Merch.Merch;
import app.audio.Collections.Album;
import app.audio.Files.Song;
import app.wrappedStats.ListenersHelper;
import app.wrappedStats.SongHelper;
import app.wrappedStats.WrappedEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
public final class Artist extends User {
    private ArrayList<Album> albums;
    @Getter @Setter
    private ArrayList<Event> events;
    @Getter @Setter
    private ArrayList<Merch> merch;
    @Getter @Setter
    private int totalLikes;
    @Getter
    private ArrayList<SongHelper> listenedSongs;
    @Getter
    private ArrayList<ListenersHelper> fans;
    @Getter
    private int listeners;
    public Artist(final String username, final int age, final String city) {
        super(username, age, city, "artist");
        this.totalLikes = 0;
    }

    /**
     * Adds new album to the database
     * and also generates a message
     *
     * @param commandInput  the command input
     * @return              String message
     */
    public String addAlbum(final CommandInput commandInput,
                           final Admin admin) {
        if (!getRole().equals("artist")) {
            return getUsername() + " is not an artist.";
        } else {
            if (hasAlbum(commandInput.getName()) == 1) {
                return getUsername() + " has another album "
                        + "with the same name.";
            } else {
                ArrayList<SongInput> albumSongs = commandInput.getSongs();
                if (Album.checkSongTwice(albumSongs) == 1) {
                    return getUsername() + " has the same"
                            + " song at least twice in this album.";
                } else {
                    // if all conditions passed, then album can be created
                    String name = commandInput.getName();
                    String artist = commandInput.getUsername();
                    String releaseYear = commandInput.getReleaseYear();
                    String description = commandInput.getDescription();
                    ArrayList<SongInput> songs = commandInput.getSongs();

                    admin.addSongsToDatabase(songs);

                    ArrayList<Song> songsToAdd = new ArrayList<>();
                    for (SongInput songInput : songs) {
                        Song newSong = new Song(songInput.getName(),
                                songInput.getDuration(), songInput.getAlbum(),
                                songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                                songInput.getReleaseYear(), songInput.getArtist());
                        songsToAdd.add(newSong);
                    }

                    Album newAlbum =  new Album(name, artist, releaseYear, description, songsToAdd);
                    addAlbumInList(newAlbum);
                    admin.addAlbumToDatabase(newAlbum);
                    return getUsername() + " has added"
                            + " new album successfully.";
                }
            }
        }
    }

    /**
     * checks if an artist already has an album with that name
     *
     * @param albumCrt the album
     * @return 1 if artist already has that album name, 0 otherwise
     */
    public int hasAlbum(final String albumCrt) {
        if (getAlbums() != null) {
            for (Album album : getAlbums()) {
                if (album.getName().equals(albumCrt)) {
                    return 1;
                }
            }
            return 0;
        }
        return 0;
    }

    /**
     * Adds an album to the artist's list of albums
     *
     * @param album the album
     */
    public void addAlbumInList(final Album album) {
        if (getAlbums() == null) {
            albums = new ArrayList<>();
            albums.add(album);
        } else {
            albums.add(album);
        }
    }

    /**
     * Removes an album from an artist.
     *
     * @param commandInput the command input
     * @return             String message
     */
    public String removeAlbum(final CommandInput commandInput,
                              final Admin admin) {
        String name = commandInput.getName();
        if (hasAlbum(name) == 0) {
            return getUsername() + " doesn't have an "
                    + "album with the given name.";
        } else {
            // check if any of the user's players
            // play the album or any song from it
            User user = admin.getUser(commandInput.getUsername());
            Album album = admin.findAlbumInDatabase(name, (Artist) user);
            if (admin.isPlayedAlbum(album) == 1) {
                return getUsername() + " can't delete this album.";
            } else {
                if (album != null) {
                    admin.removeAlbumSongsFromDatabase(album);
                }
                removeAlbumFromList(album);
                admin.removeAlbumFromDatabase(album);
                return getUsername() + " deleted the album successfully.";
            }
        }
    }

    /**
     * Removes an album from an artist.
     *
     * @param album the album
     */
    public void removeAlbumFromList(final Album album) {
        if (getAlbums() != null) {
            getAlbums().removeIf(crtAlbum -> {
                return crtAlbum.getName().equals(album.getName());
            });
        }
    }

    /**
     * gets the albums from an artist
     *
     * @return arrayNode of album (name + songs)
     */
    public ArrayNode getAlbumsFromArtist() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode mainNode = objectMapper.createArrayNode();
        if (getAlbums() != null) {
            for (Album album : getAlbums()) {
                ObjectNode node = objectMapper.createObjectNode();
                node.put("name", album.getName());
                ArrayList<Song> songs = album.getSongs();
                ArrayList<String> nameSongs = new ArrayList<>();
                for (Song song : songs) {
                    nameSongs.add(song.getName());
                }
                node.putPOJO("songs", nameSongs);
                mainNode.add(node);
            }
        }
        return mainNode;
    }

    /**
     * Computes the total number of likes for an artist
     */
    public void computeLikes() {
        if (getAlbums() != null) {
            for (Album album : getAlbums()) {
                album.computeLikes();
                setTotalLikes(getTotalLikes() + album.getTotalLikes());
            }
        }
    }

    /**
     * Generates message when adding a new event
     *
     * @param commandInput the command input
     * @return             String message
     */
    public String addEvent(final CommandInput commandInput) {
        String name = commandInput.getName();
        String date = commandInput.getDate();
        if (eventExists(name) == 1) {
            return getUsername() + " has another event with the same name.";
        } else if (!Event.validDate(date)) {
            return "Event for " + getUsername() + " does not"
                    + " have a valid date.";
        } else {
            String description = commandInput.getDescription();
            Event event = new Event(name, getUsername(), description, date);
            addEventToList(event);
            return getUsername() + " has added new event successfully.";
        }
    }

    /**
     * Generates message when removing an event
     *
     * @param commandInput the command input
     * @return             String message
     */
    public String removeEvent(final CommandInput commandInput) {
        String name = commandInput.getName();
        if (eventExists(name) == 0) {
            return getUsername() + " doesn't have an event with the given name.";
        } else {
            removeEventFromList(name);
            return getUsername() + " deleted the event successfully.";
        }
    }

    /**
     * Checks if an event already exists for an artist
     *
     * @param event the event
     * @return 1 if event exists already, 0 otherwise
     */
    public int eventExists(final String event) {
        if (getEvents() != null) {
            ArrayList<Event> artistEvents = getEvents();
            for (Event crtEvent : artistEvents) {
                if (crtEvent.getName().equals(event)) {
                    return 1;
                }
            }
            return 0;
        }
        return 0;
    }

    /**
     * Adds event to the artist's list of events
     *
     * @param event the event
     */
    private void addEventToList(final Event event) {
        if (getEvents() == null) {
            events = new ArrayList<>();
        }
        events.add(event);
    }

    /**
     * Removes event from the artist's list of events.
     *
     * @param crtEvent the event
     */
    private void removeEventFromList(final String crtEvent) {
        events.removeIf(event -> event.getName().equals(crtEvent));
    }

    /**
     * Checks if artist already owns that merch name
     *
     * @return 1 if merch exists, 0 otherwise
     */
    public int hasMerch(final String name) {
        if (getMerch() != null) {
            ArrayList<Merch> artistMerch = getMerch();
            for (Merch item : artistMerch) {
                if (item.getName().equals(name)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * Adds merch to the artist's list of merch
     *
     * @param item the new merch
     */
    public void addMerch(final Merch item) {
        if (getMerch() == null) {
            merch = new ArrayList<>();
        }
        merch.add(item);
    }

    /**
     * Adds a song to the list of listened songs.
     *
     * @param crtSong the song
     */
    public void addListenedSongs(final WrappedEntity crtSong) {
        if (getListenedSongs() == null) {
            listenedSongs = new ArrayList<>();
        } else {
            int ok = 0;
            // checks if crtSong has already been listened to
            for (SongHelper song : listenedSongs) {
                if (song.getName().equals(crtSong.getName())) {
                    ok = 1;
                    song.setListens(song.getListens() + crtSong.getListens());
                }
            }
            if (ok == 0) {
                // create the instance
                SongHelper song = new SongHelper(crtSong.getName(), crtSong.getListens());
                listenedSongs.add(song);
            }
        }
    }

    /**
     * Adds a fan to the list of fans.
     *
     * @param name    the name of the fan
     * @param listens the number of listens
     */
    public void addFans(final String name, final int listens) {
        if (getFans() == null) {
            fans = new ArrayList<>();
        } else {
            int ok = 0;
            // checks if name has already listened to their songs
            for (ListenersHelper fan : fans) {
                if (fan.getUsername().equals(name)) {
                    ok = 1;
                    fan.setListens(fan.getListens() + listens);
                }
            }
            if (ok == 0) {
                // create the instance
                ListenersHelper fan = new ListenersHelper(name, listens);
                fans.add(fan);
                listeners++;
            }
        }
    }
}
