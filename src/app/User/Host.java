package app.User;

import app.SpecialUsersEvents.Announcement;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.Admin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import fileio.input.EpisodeInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class Host extends User {
    private ArrayList<Podcast> podcasts;
    private ArrayList<Announcement> announcements;
    public Host(final String username, final int age, final String city) {
        super(username, age, city);
    }

    /**
     * Checks if a host already has a podcast with a certain name.
     *
     * @param crtPodcast the podcast
     * @return 1 if podcast already exists, 0 otherwise
     */
    public int hasPodcast(final String crtPodcast) {
        if (getPodcasts() != null) {
            for (Podcast podcast : getPodcasts()) {
                if (podcast.getName().equals(crtPodcast)) {
                    return 1;
                }
            }
            return 0;
        }
        return 0;
    }

    /**
     * Adds a new podcast to the database and also
     * generates message
     *
     * @param commandInput  the command input
     * @return              String message
     */
    public String addPodcast(final CommandInput commandInput,
                             final Admin admin) {
        if (hasPodcast(commandInput.getName()) == 1) {
            return getUsername() + " has another podcast"
                    + " with the same name.";
        } else {
            ArrayList<EpisodeInput> episodes = commandInput.getEpisodes();
            if (Podcast.checkEpisodeTwice(episodes) == 1) {
                return getUsername() + " has the same episode"
                        + " in this podcast.";
            } else {
                // if all conditions passed, then podcast can be created
                String name = commandInput.getName();
                String owner = commandInput.getUsername();
                ArrayList<Episode> episodesToAdd = new ArrayList<>();

                // transforms the EpisodeInput episodes into Episode episodes
                for (EpisodeInput episodeInput : episodes) {
                    Episode newEpisode = new Episode(episodeInput.getName(),
                            episodeInput.getDuration(), episodeInput.getDescription());
                    episodesToAdd.add(newEpisode);
                }

                Podcast podcast = new Podcast(name, owner, episodesToAdd);
                admin.addPodcasts(podcast);
                addPodcastInList(podcast);

                return getUsername() + " has added new podcast successfully.";
            }
        }
    }

    /**
     * Removes a podcast from a host and generates message.
     *
     * @param commandInput the command input
     * @return             the String message
     */
    public String removePodcast(final CommandInput commandInput,
                                final Admin admin) {
        String name = commandInput.getName();
        if (hasPodcast(name) == 0) {
            return getUsername() + " doesn't have a "
                    + "podcast with the given name.";
        } else {
            // check if any of the user's players
            // are playing the podcast
            User user = admin.getUser(commandInput.getUsername());
            Podcast podcast = admin.findPodcastInDatabase(name, (Host) user);
            if (admin.isPlayedPodcast(podcast) == 1) {
                return getUsername() + " can't delete this podcast.";
            } else {
                removePodcastFromList(podcast);
                admin.removePodcastFromDatabase(podcast);
                return getUsername() + " deleted the podcast successfully.";
            }
        }
    }

    /**
     * Removes a podcast from a host's list of podcasts.
     *
     * @param podcast the podcast
     */
    public void removePodcastFromList(final Podcast podcast) {
        if (getPodcasts() != null) {
            getPodcasts().removeIf(crtPodcast -> {
                return crtPodcast.getName().equals(podcast.getName());
            });
        }
    }

    /**
     * Adds podcast to the host's list of podcasts
     *
     * @param podcast the podcast
     */
    public void addPodcastInList(final Podcast podcast) {
        if (getPodcasts() == null) {
            podcasts = new ArrayList<>();
        }
        podcasts.add(podcast);
    }

    /**
     * Checks if a host already has an announcement
     * with a certain name
     *
     * @param name the name of the announcement
     * @return 1 if announcement exists, 0 otherwise
     */
    public int announcementExists(final String name) {
        if (getAnnouncements() != null) {
            for (Announcement announcement : announcements) {
                if (announcement.getName().equals(name)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * Adds an announcement for a host
     *
     * @param commandInput  the command input
     * @return              String message
     */
    public String addAnnouncement(final CommandInput commandInput) {
         if (!getRole().equals("host")) {
             return getUsername() + " is not a host.";
         } else {
             String name = commandInput.getName();
             if (announcementExists(name) == 1) {
                 return getUsername() + " has already added an"
                         + "announcement with this name.";
             } else {
                 // an announcement can be created
                 String description = commandInput.getDescription();

                 Announcement announcement = new Announcement(name, description);
                 addAnnouncementInList(announcement);
                 return getUsername() + " has successfully added new announcement.";
             }
         }
    }

    /**
     * Removes an announcement from a certain host.
     *
     * @param commandInput the command input
     * @return             String message
     */
    public String removeAnnouncement(final CommandInput commandInput) {
                String name = commandInput.getName();
                if (announcementExists(name) == 0) {
                    return getUsername() + " has no announcement"
                            + " with the given name.";
                } else {
                    removeAnnouncementFromList(name);
                    return getUsername() + " has successfully"
                            + " deleted the announcement.";
                }
    }

    /**
     * Adds a new announcement to the
     * host's list of announcements
     *
     * @param announcement the announcement
     */
    public void addAnnouncementInList(final Announcement announcement) {
        if (getAnnouncements() == null) {
            announcements = new ArrayList<>();
        }
        announcements.add(announcement);
    }

    /**
     * Removes an announcement from a host.
     */
    public void removeAnnouncementFromList(final String name) {
        if (getAnnouncements() != null) {
            getAnnouncements().removeIf(announcement -> {
                return announcement.getName().equals(name);
            });
        }
    }

    /**
     * Gets all the podcasts from a host.
     *
     * @return the array node
     */
    public ArrayNode getPodcastsFromHost() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode mainNode = objectMapper.createArrayNode();
        if (getPodcasts() != null) {
            for (Podcast podcast : getPodcasts()) {
                ObjectNode node = objectMapper.createObjectNode();
                node.put("name", podcast.getName());
                List<Episode> episodes = podcast.getEpisodes();
                ArrayList<String> nameEpisodes = new ArrayList<>();
                for (Episode episode : episodes) {
                    nameEpisodes.add(episode.getName());
                }
                node.putPOJO("episodes", nameEpisodes);
                mainNode.add(node);
            }
        }
        return mainNode;
    }
}
