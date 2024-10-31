package app.Pages;

import app.Admin;
import app.Formats.FormatVisitor;
import app.Formats.FormattingVisitor;
import app.User.Artist;
import app.User.Host;
import app.User.User;
import app.audio.Collections.Playlist;
import app.audio.Files.Song;

import java.util.ArrayList;

public final class Page {
    // private constructor to prevent instantiation
    private Page() {
    }
    /**
     * Generates the message for pages
     *
     * @param user the user
     * @return String
     */
    public static String messagePage(final User user) {
        if (user != null) {
            String crtName = user.getCurrentPage();
            if (crtName.equals("Home")) {
                return homePageOutput(user);
            } else if (crtName.equals("LikedContent")) {
                return likedContentOutput(user);
            } else {
                return otherPageOutput(crtName);
            }
        }
        return null;
    }

    /**
     * Generates the output for the home page of a normal user
     *
     * @param user the user
     * @return     String
     */
    private static String homePageOutput(final User user) {
        ArrayList<String> likedSongs = user.get5LikedSongs();
        ArrayList<String> likedPlaylists = user.get5LikedPlaylists();
        return "Liked songs:\n\t" + likedSongs + "\n\nFollowed playlists:\n\t"
                + likedPlaylists;
    }

    /**
     * Generates the output for the liked content page of a normal user
     *
     * @param user the user
     * @return     String
     */
    private static String likedContentOutput(final User user) {
        ArrayList<Song> likedSongs = user.getLikedSongs();
        ArrayList<Playlist> followedPlaylist = user.getFollowedPlaylists();

        String format = "Liked songs:\n\t[";
        int i;
        for (i = 0; i < likedSongs.size() - 1; i++) {
            format = format.concat(likedSongs.get(i).getName() + " - "
                    + likedSongs.get(i).getArtist() + ", ");
        }
        if (!likedSongs.isEmpty()) {
            format = format.concat(likedSongs.get(i).getName() + " - "
                    + likedSongs.get(i).getArtist() + "]\n\n");
        } else {
            format = format.concat("]\n\n");
        }
        format = format.concat("Followed playlists:\n\t[");
        for (i = 0; i < followedPlaylist.size() - 1; i++) {
            format = format.concat(followedPlaylist.get(i).getName() + " - "
                    + followedPlaylist.get(i).getOwner() + ", ");
        }
        if (!followedPlaylist.isEmpty()) {
            format = format.concat(followedPlaylist.get(i).getName() + " - "
                    + followedPlaylist.get(i).getOwner() + "]");
        } else {
            format = format.concat("]");
        }
        return format;
    }

    /**
     * Generates the output of an artist's/host's page
     *
     * @param crtName the name of the artist/host
     * @return        String
     */
    private static String otherPageOutput(final String crtName) {
        // first, we need to search the user and find their role
        String role = null;
        User crtUser = null;
        for (User user : Admin.getUsers()) {
            if (user.getUsername().equals(crtName)) {
                crtUser = user;
                role = user.getRole();
                break;
            }
        }
        if (role != null && role.equals("artist")) {
            Artist artist = (Artist) crtUser;
            StringBuilder format = new StringBuilder("Albums:\n\t[");
            FormatVisitor formattingVisitor = new FormattingVisitor();
            if (artist.getAlbums() != null) {
                int i = 0;
                for (i = 0; i < artist.getAlbums().size() - 1; i++) {
                    artist.getAlbums().get(i).accept(formattingVisitor, format);
                    format.append(", ");
                }
                artist.getAlbums().get(i).accept(formattingVisitor, format);
            }

            format.append("]\n\nMerch:\n\t[");
            if (artist.getMerch() != null) {
                int i = 0;
                for (i = 0; i < artist.getMerch().size() - 1; i++) {
                    artist.getMerch().get(i).accept(formattingVisitor, format);
                    format.append(", ");
                }
                artist.getMerch().get(i).accept(formattingVisitor, format);
            }

            format.append("]\n\nEvents:\n\t[");
            if (artist.getEvents() != null) {
                int i = 0;
                for (i = 0; i < artist.getEvents().size() - 1; i++) {
                    artist.getEvents().get(i).accept(formattingVisitor, format);
                    format.append(", ");
                }
                artist.getEvents().get(i).accept(formattingVisitor, format);
            }
            format.append("]");
            return format.toString();
        } else if (role != null && role.equals("host")) {
            Host host = (Host) crtUser;
            StringBuilder format = new StringBuilder("Podcasts:\n\t[");
            FormatVisitor formattingVisitor = new FormattingVisitor();

            if (host.getPodcasts() != null) {
                int i;
                for (i = 0; i < host.getPodcasts().size() - 1; i++) {
                    host.getPodcasts().get(i).accept(formattingVisitor, format);
                    format.append(", ");
                }
                host.getPodcasts().get(i).accept(formattingVisitor, format);
            }

            format.append("]\n\nAnnouncements\n\t[");
            if (host.getAnnouncements() != null) {
                int i;
                for (i = 0; i < host.getAnnouncements().size() - 1; i++) {
                    host.getAnnouncements().get(i).accept(formattingVisitor, format);
                    format.append(", ");
                }
                host.getAnnouncements().get(i).accept(formattingVisitor, format);
            }
            format.append("]");
            return format.toString();
        }
        return null;
    }
}
