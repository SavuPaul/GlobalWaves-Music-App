package app.Merch;

import app.Formats.FormatVisitor;
import app.Formats.FormattingInstance;
import app.User.Artist;
import app.User.User;
import fileio.input.CommandInput;
import lombok.Getter;

public class Merch implements FormattingInstance {
    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private Integer price;

    public Merch(final String name, final String description, final Integer price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    /**
     * Computes the message for adding merch.
     *
     * @param user         the user
     * @param commandInput the command input
     * @return             String message
     */
    public static String merchMessage(final User user, final CommandInput commandInput) {
        if (!user.getRole().equals("artist")) {
            return user.getUsername() + " is not an artist.";
        } else {
            Artist artist = (Artist) user;
            String name = commandInput.getName();
            if (artist.hasMerch(name) == 1) {
                return user.getUsername() + " has merchandise with "
                        + "the same name.";
            } else {
                int price = commandInput.getPrice();
                if (price < 0) {
                    return "Price for merchandise can not be negative.";
                } else {
                    String description = commandInput.getDescription();
                    Merch merch = new Merch(name, description, price);
                    artist.addMerch(merch);
                    return user.getUsername() + " has added new merchandise successfully.";
                }
            }
        }
    }

    /**
     * Accept method for visitor (merch)
     * @param visitor the visitor
     * @param format  the string format for the specific entity
     */
    @Override
    public void accept(final FormatVisitor visitor,
                       final StringBuilder format) {
        visitor.visit(this, format);
    }
}
