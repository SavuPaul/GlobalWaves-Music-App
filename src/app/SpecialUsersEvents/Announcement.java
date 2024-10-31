package app.SpecialUsersEvents;

import app.Formats.FormatVisitor;
import app.Formats.FormattingInstance;
import lombok.Getter;

@Getter
public class Announcement implements FormattingInstance {
    private String name;
    private String description;

    public Announcement(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Accept method for visitor (Announcement)
     * @param visitor the visitor
     * @param format  the string format for the specific entity
     */
    @Override
    public void accept(final FormatVisitor visitor,
                       final StringBuilder format) {
        visitor.visit(this, format);
    }
}
