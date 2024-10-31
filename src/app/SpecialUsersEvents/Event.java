package app.SpecialUsersEvents;

import app.Formats.FormatVisitor;
import app.Formats.FormattingInstance;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Getter
public final class Event implements FormattingInstance {
    private String name;
    private String artist;
    private String description;
    private String date;

    public Event(final String name, final String artist,
                 final String description, final String date) {
        this.name = name;
        this.artist = artist;
        this.description = description;
        this.date = date;
    }

    /**
     * Checks if a date fits the format or not.
     *
     * @param date the date
     * @return the boolean
     */
    public static boolean validDate(final String date) {
        String regex = "^(0[1-9]|[1-2][0-9]|3[0-1])-(0[1-9]|1[0-2])-"
                + "(19\\d{2}|20[0-1]\\d|202[0-3])$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);

        return matcher.matches();
    }

    @Override
    public void accept(final FormatVisitor visitor,
                       final StringBuilder format) {
        visitor.visit(this, format);
    }
}
