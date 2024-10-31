package app.audio.Files;

import app.audio.LibraryEntry;
import lombok.Getter;

@Getter
public abstract class AudioFile extends LibraryEntry {
    private final Integer duration;
    private final String type;

    public AudioFile(final String name, final Integer duration, final String type) {
        super(name);
        this.duration = duration;
        this.type = type;
    }
}
