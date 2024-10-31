package app.Formats;

import app.Merch.Merch;
import app.SpecialUsersEvents.Announcement;
import app.SpecialUsersEvents.Event;
import app.audio.Collections.Album;
import app.audio.Collections.Podcast;

public interface FormatVisitor {
    /**
     * Visitor method for event
     * @param event  the event
     * @param format the format
     */
    void visit(Event event, StringBuilder format);

    /**
     * Visitor method for merch
     * @param merch  the merch
     * @param format the format
     */
    void visit(Merch merch, StringBuilder format);

    /**
     * Visitor method for album
     * @param album  the album
     * @param format the format
     */
    void visit(Album album, StringBuilder format);

    /**
     * Visitor method for podcast
     * @param podcast the podcast
     * @param format  the format
     */
    void visit(Podcast podcast, StringBuilder format);

    /**
     * Visitor method for announcement
     * @param announcement  the announcement
     * @param format        the format
     */
    void visit(Announcement announcement, StringBuilder format);
}
