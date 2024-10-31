package app.wrappedStats;

import lombok.Getter;
import lombok.Setter;

// this class helps with determining the top 5 fans
// for each artist based on the number of listens
@Getter @Setter
public class ListenersHelper {
    private String username;
    private int listens;

    public ListenersHelper(final String username,
                           final int listens) {
        this.username = username;
        this.listens = listens;
    }
}
