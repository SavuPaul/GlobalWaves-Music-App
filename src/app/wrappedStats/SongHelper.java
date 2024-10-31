package app.wrappedStats;

import lombok.Getter;
import lombok.Setter;

// this class helps with determining the top 5 songs
// for each artist based on the number of listens
@Getter @Setter
public class SongHelper {
    private String name;
    private int listens;

    public SongHelper(final String name,
                      final int listens) {
        this.name = name;
        this.listens = listens;
    }
}
