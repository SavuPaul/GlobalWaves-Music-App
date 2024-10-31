package app.wrappedStats;

import lombok.Getter;
import lombok.Setter;

// this class helps with determining the total number
// of listens that a user has for a certain artist
@Getter @Setter
public class ArtistHelper {
    private String name;
    private int listens;

    public ArtistHelper(final String name, final int listens) {
        this.name = name;
        this.listens = listens;
    }
}
