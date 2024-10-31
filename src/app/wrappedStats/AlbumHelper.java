package app.wrappedStats;

import lombok.Getter;
import lombok.Setter;

// this class helps with determining
// the top albums by listens for a user
@Getter
@Setter
public class AlbumHelper {
    private String name;
    private int listens;

    public AlbumHelper(final String name,
                       final int listens) {
        this.name = name;
        this.listens = listens;
    }
}
