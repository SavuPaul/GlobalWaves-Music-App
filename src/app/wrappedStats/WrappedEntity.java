package app.wrappedStats;

import lombok.Getter;

/*
     Each user will have an array of type WrappedEntity.
     The purpose of this class is to build objects based on the type of objects
     which count as a "listen" for a user (songs / albums / episodes).
     This class uses Builder design pattern.
*/
@Getter
public final class WrappedEntity {
    private String name; // the name of the entity (song name / episode name)
    private String artist;
    private String host;
    private int listens; // number of listens for such an object
    private String genre;
    private String album;

    public static class Builder {
        private String name;
        private String artist;
        private String host;
        private int listens;
        private String genre;
        private String album;

        public Builder(final String name, final int listens) {
            this.name = name;
            this.listens = listens;
        }

        /**
         * Sets fields for artist, genre and album
         * @param artist the artist
         * @param genre  the genre
         * @param album  the album
         * @return       the builder object
         */
        public Builder artistGenreAlbum(final String artist, final String genre,
                                        final String album) {
            this.artist = artist;
            this.genre = genre;
            this.album = album;
            return this;
        }

        /**
         * Sets the host for the object.
         * @param host the host
         * @return     the builder object
         */
        public Builder host(final String host) {
            this.host = host;
            return this;
        }

        /**
         * Creates new object
         * @return WrappedEntity object
         */
        public WrappedEntity build() {
            return new WrappedEntity(this);
        }
    }

    private WrappedEntity(final Builder builder) {
        this.name = builder.name;
        this.artist = builder.artist;
        this.host = builder.host;
        this.album = builder.album;
        this.genre = builder.genre;
        this.listens = builder.listens;
    }

    /**
     * Adds a listen for the object
     */
    public void addListen() {
        this.listens++;
    }
}
