package app.wrappedStats;

import lombok.Getter;
import lombok.Setter;

// this genre helps with determining the
// most listened genre by a user
@Getter
@Setter
public class GenreHelper {
    private String name;
    private int listens;

    public GenreHelper(final String name,
                       final int listens) {
        this.name = name;
        this.listens = listens;
    }
}
