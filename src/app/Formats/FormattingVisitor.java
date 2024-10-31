package app.Formats;

import app.Merch.Merch;
import app.SpecialUsersEvents.Announcement;
import app.SpecialUsersEvents.Event;
import app.audio.Collections.Album;
import app.audio.Collections.Podcast;

public final class FormattingVisitor implements FormatVisitor {

    @Override
    public void visit(final Event event,
                      final StringBuilder format) {
        format.append(event.getName());
        format.append(" - ").append(event.getDate()).append(":\n\t");
        format.append(event.getDescription());
    }

    @Override
    public void visit(final Merch merch,
                      final StringBuilder format) {
        format.append(merch.getName())
                .append(" - ")
                .append(merch.getPrice())
                .append(":\n\t")
                .append(merch.getDescription());
    }

    @Override
    public void visit(final Album album,
                      final StringBuilder format) {
        format.append(album.getName());
    }

    @Override
    public void visit(final Podcast podcast,
                      final StringBuilder format) {
        format.append(podcast.getName())
                .append(":\n\t");
        if (podcast.getEpisodes() != null) {
            int i;
            for (i = 0; i < podcast.getEpisodes().size() - 1; i++) {
                format.append(podcast.getEpisodes().get(i).getName())
                        .append(" - ")
                        .append(podcast.getEpisodes().get(i).getDescription())
                        .append(", ");
            }
            format.append(podcast.getEpisodes().get(i).getName())
                    .append(" - ")
                    .append(podcast.getEpisodes().get(i).getDescription());
        }
        format.append("]");
    }

    @Override
    public void visit(final Announcement announcement,
                      final StringBuilder format) {
        format.append(announcement.getName())
                .append("\n\t")
                .append(announcement.getDescription())
                .append("\n");
    }
}
