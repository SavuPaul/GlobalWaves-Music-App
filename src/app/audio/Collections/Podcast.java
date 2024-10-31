package app.audio.Collections;

import app.Formats.FormatVisitor;
import app.Formats.FormattingInstance;
import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import fileio.input.EpisodeInput;

import java.util.ArrayList;
import java.util.List;

public final class Podcast extends AudioCollection implements FormattingInstance {
    private final List<Episode> episodes;

    public Podcast(final String name, final String owner, final List<Episode> episodes) {
        super(name, owner);
        this.episodes = episodes;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    /**
     * Checks if an episode exists twice in podcast
     *
     * @param episodes the list of songs
     * @return 1 if episode exists twice, 0 otherwise
     */
    public static int checkEpisodeTwice(final ArrayList<EpisodeInput> episodes) {
        if (episodes != null) {
            for (int i = 0; i < episodes.size() - 1; i++) {
                for (int j = i + 1; j < episodes.size(); j++) {
                    if (episodes.get(i).getName().equals(episodes.get(j).getName())) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public int getNumberOfTracks() {
        return episodes.size();
    }

    @Override
    public AudioFile getTrackByIndex(final int index) {
        return episodes.get(index);
    }

    @Override
    public void accept(final FormatVisitor visitor,
                       final StringBuilder format) {
        visitor.visit(this, format);
    }
}
