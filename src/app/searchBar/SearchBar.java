package app.searchBar;


import app.Admin;
import app.audio.LibraryEntry;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static app.searchBar.FilterUtils.*;

/**
 * The type Search bar.
 */
public final class SearchBar {
    private List<LibraryEntry> resultsLibEntry;
    private ArrayList<String> resultsUserEntry;
    private final String user;
    private static final Integer MAX_RESULTS = 5;
    @Getter
    private String lastSearchType;

    @Getter
    private LibraryEntry lastSelectedLibEntry;
    @Getter
    private String lastSelectedUserEntry;

    /**
     * Instantiates a new Search bar.
     *
     * @param user the user
     */
    public SearchBar(final String user) {
        this.resultsLibEntry = new ArrayList<>();
        this.resultsUserEntry = new ArrayList<>();
        this.user = user;
    }

    /**
     * Clear selection.
     */
    public void clearSelection() {
        lastSelectedLibEntry = null;
        lastSelectedUserEntry = null;
        lastSearchType = null;
    }

    /**
     * Search list (library entry).
     *
     * @param filters the filters
     * @param type    the type
     * @return the list
     */
    public List<LibraryEntry> searchLibEntry(final Filters filters, final String type,
                                             final Admin admin) {
        List<LibraryEntry> entries;

        switch (type) {
            case "song":
                entries = new ArrayList<>(admin.getSongs());

                if (filters.getName() != null) {
                    entries = filterByName(entries, filters.getName());
                }

                if (filters.getAlbum() != null) {
                    entries = filterByAlbum(entries, filters.getAlbum());
                }

                if (filters.getTags() != null) {
                    entries = filterByTags(entries, filters.getTags());
                }

                if (filters.getLyrics() != null) {
                    entries = filterByLyrics(entries, filters.getLyrics());
                }

                if (filters.getGenre() != null) {
                    entries = filterByGenre(entries, filters.getGenre());
                }

                if (filters.getReleaseYear() != null) {
                    entries = filterByReleaseYear(entries, filters.getReleaseYear());
                }

                if (filters.getArtist() != null) {
                    entries = filterByArtist(entries, filters.getArtist());
                }

                break;
            case "playlist":
                entries = new ArrayList<>(admin.getPlaylists());

                entries = filterByPlaylistVisibility(entries, user);

                if (filters.getName() != null) {
                    entries = filterByName(entries, filters.getName());
                }

                if (filters.getOwner() != null) {
                    entries = filterByOwner(entries, filters.getOwner());
                }

                if (filters.getFollowers() != null) {
                    entries = filterByFollowers(entries, filters.getFollowers());
                }

                break;
            case "podcast":
                entries = new ArrayList<>(admin.getPodcasts());

                if (filters.getName() != null) {
                    entries = filterByName(entries, filters.getName());
                }

                if (filters.getOwner() != null) {
                    entries = filterByOwner(entries, filters.getOwner());
                }

                break;
            case "album":
                entries = new ArrayList<>(admin.getAlbums());

                if (filters.getName() != null) {
                    entries = filterByName(entries, filters.getName());
                }

                if (filters.getOwner() != null) {
                    entries = filterByOwner(entries, filters.getOwner());
                }

                if (filters.getDescription() != null) {
                    entries = filterByDescription(entries, filters.getDescription());
                }
                break;
            default:
                entries = new ArrayList<>();
        }

        while (entries.size() > MAX_RESULTS) {
            entries.remove(entries.size() - 1);
        }
        this.resultsLibEntry = entries;
        this.lastSearchType = type;
        return this.resultsLibEntry;
    }

    /**
     * Search list (users).
     *
     * @param filters the filters
     * @param type    the type
     * @return the list
     */
    public ArrayList<String> searchUserEntry(final Filters filters, final String type,
                                             final Admin admin) {
        ArrayList<String> filteredArtists = new ArrayList<>();
        ArrayList<String> filteredHosts = new ArrayList<>();
        switch (type) {
            case "artist":
                filteredArtists = admin.filterByUsername(type, filters.getName());
                this.resultsUserEntry = filteredArtists;
                break;
            case "host":
                filteredHosts = admin.filterByUsername(type, filters.getName());
                this.resultsUserEntry = filteredHosts;
                break;
            default:
                break;
        }
        return this.resultsUserEntry;
    }

    /**
     * Select library entry.
     *
     * @param itemNumber the item number
     * @return the library entry
     */
    public LibraryEntry selectLibEntry(final Integer itemNumber) {
        if (this.resultsLibEntry.size() < itemNumber) {
            resultsLibEntry.clear();

            return null;
        } else {
            lastSelectedLibEntry =  this.resultsLibEntry.get(itemNumber - 1);
            resultsLibEntry.clear();

            return lastSelectedLibEntry;
        }
    }

    /**
     * Select user entry.
     *
     * @param itemNumber the item number
     * @return the library entry
     */
    public String selectUserEntry(final Integer itemNumber) {
        if (this.resultsUserEntry.size() < itemNumber) {
            resultsUserEntry.clear();

            return null;
        } else {
            lastSelectedUserEntry = this.resultsUserEntry.get(itemNumber - 1);
            resultsUserEntry.clear();

            return lastSelectedUserEntry;
        }
    }
}
