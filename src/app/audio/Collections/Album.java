package app.audio.Collections;

import app.Formats.FormatVisitor;
import app.Formats.FormattingInstance;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
public final class Album extends AudioCollection implements FormattingInstance {
    @Setter
    private String releaseYear;
    @Setter
    private String description;
    @Setter
    private ArrayList<Song> songs;
    @Getter @Setter
    private int totalLikes;
    @Getter
    private int listens;

    public Album(final String name, final String artist, final String releaseYear,
                 final String description, final ArrayList<Song> songs) {
        super(name, artist);
        this.releaseYear = releaseYear;
        this.description = description;
        this.songs = songs;
        this.totalLikes = 0;
        this.listens = 0;
    }

    /**
     * Checks if a song exists twice in album
     *
     * @param songs the list of songs
     * @return 1 if song exists twice, 0 otherwise
     */
    public static int checkSongTwice(final ArrayList<SongInput> songs) {
        if (songs != null) {
            for (int i = 0; i < songs.size() - 1; i++) {
                for (int j = i + 1; j < songs.size(); j++) {
                    if (songs.get(i).getName().equals(songs.get(j).getName())) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Checks if an album contains a certain song.
     *
     * @param name the name of the song
     * @return     1 if album contains song, 0 otherwise
     */
    public int containsSong(final String name) {
        if (getSongs() != null) {
            ArrayList<Song> songsAlbum = getSongs();
            for (Song song : songsAlbum) {
                if (song.getName().equals(name)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * Computes the total number of likes for an album.
     */
    public void computeLikes() {
        if (getSongs() != null) {
            for (Song song : getSongs()) {
                setTotalLikes(getTotalLikes() + song.getLikes());
            }
        }
    }

    @Override
    public int getNumberOfTracks() {
        return songs.size();
    }

    @Override
    public AudioFile getTrackByIndex(final int index) {
        return songs.get(index);
    }

    /**
     * Checks if album description starts with the given string.
     *
     * @param descriptionToMatch the description
     * @return the boolean
     */
    public boolean matchesDescription(final String descriptionToMatch) {
        if (getDescription() != null) {
            if (getDescription().startsWith(descriptionToMatch)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds one listen to the album
     */
    public void addListen() {
        this.listens++;
    }


    @Override
    public void accept(final FormatVisitor visitor,
                       final StringBuilder format) {
        visitor.visit(this, format);
    }
}
