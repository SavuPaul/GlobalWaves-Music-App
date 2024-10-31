package app;

import app.Merch.Merch;
import app.User.Artist;
import app.User.Host;
import app.audio.Collections.PlaylistOutput;
import app.Player.PlayerStats;
import app.searchBar.Filters;
import app.User.User;
import app.Pages.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Command runner.
 */
public final class CommandRunner {
    /**
     * The Object mapper.
     */
    private static ObjectMapper objectMapper = new ObjectMapper();

    private CommandRunner() {
    }

    /**
     * Search object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode search(final CommandInput commandInput,
                                    final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        Filters filters = new Filters(commandInput.getFilters());
        String type = commandInput.getType();

        if (user != null) {
            if (user.getConnection().equals("online")) {
                int ts = commandInput.getTimestamp();
                ArrayList<String> results = user.search(filters, type, admin, ts);
                if (results != null) {
                    String message = "Search returned " + results.size() + " results";

                    ObjectNode objectNode = objectMapper.createObjectNode();
                    objectNode.put("command", commandInput.getCommand());
                    objectNode.put("user", commandInput.getUsername());
                    objectNode.put("timestamp", commandInput.getTimestamp());
                    objectNode.put("message", message);
                    objectNode.put("results", objectMapper.valueToTree(results));

                    return objectNode;
                }
            } else {
                ArrayList<String> results = new ArrayList<>();
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");
                objectNode.putPOJO("results", results);

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Select object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode select(final CommandInput commandInput,
                                    final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());

        if (user != null) {
            if (user.getConnection().equals("online")) {
                String message = user.select(commandInput.getItemNumber());

                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Load object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode load(final CommandInput commandInput,
                                  final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            if (user.getConnection().equals("online")) {
                String message = user.load(commandInput.getTimestamp());


                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Play pause object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode playPause(final CommandInput commandInput,
                                       final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (user.getConnection().equals("online")) {
                String message = user.playPause();

                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Repeat object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode repeat(final CommandInput commandInput,
                                    final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (user.getConnection().equals("online")) {
                String message = user.repeat();

                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Shuffle object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode shuffle(final CommandInput commandInput,
                                     final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        Integer seed = commandInput.getSeed();
        if (user != null) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (user.getConnection().equals("online")) {
                String message = user.shuffle(seed);

                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Forward object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode forward(final CommandInput commandInput,
                                     final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (user.getConnection().equals("online")) {
                String message = user.forward();

                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Backward object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode backward(final CommandInput commandInput,
                                      final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (user.getConnection().equals("online")) {
                String message = user.backward();

                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Like object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode like(final CommandInput commandInput,
                                  final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (user.getConnection().equals("online")) {
                String message = user.like();

                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Next object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode next(final CommandInput commandInput,
                                  final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (user.getConnection().equals("online")) {
                String message = user.next();

                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Prev object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode prev(final CommandInput commandInput,
                                  final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (user.getConnection().equals("online")) {
                String message = user.prev();

                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Create playlist object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode createPlaylist(final CommandInput commandInput,
                                            final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (user.getConnection().equals("online")) {
                String message = user.createPlaylist(commandInput.getPlaylistName(),
                        commandInput.getTimestamp());

                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Add remove in playlist object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode addRemoveInPlaylist(final CommandInput commandInput,
                                                 final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (user.getConnection().equals("online")) {
                String message = user.addRemoveInPlaylist(commandInput.getPlaylistId());

                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Switch visibility object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode switchVisibility(final CommandInput commandInput,
                                              final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (user.getConnection().equals("online")) {
                String message = user.switchPlaylistVisibility(commandInput.getPlaylistId());

                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Show playlists object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode showPlaylists(final CommandInput commandInput,
                                           final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        ArrayList<PlaylistOutput> playlists = user.showPlaylists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Follow object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode follow(final CommandInput commandInput,
                                    final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            if (user.getConnection().equals("online")) {
                String message = user.follow();

                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername() + " is offline.");

                return objectNode;
            }
        }
        return null;
    }

    /**
     * Status object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode status(final CommandInput commandInput,
                                    final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            String connection = user.getConnection();
            PlayerStats stats = user.getPlayerStats(connection);

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("stats", objectMapper.valueToTree(stats));

            return objectNode;
        }
        return null;
    }

    /**
     * Show liked songs object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode showLikedSongs(final CommandInput commandInput,
                                            final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        ArrayList<String> songs = user.showPreferredSongs();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(songs));

        return objectNode;
    }

    /**
     * Gets preferred genre.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the preferred genre
     */
    public static ObjectNode getPreferredGenre(final CommandInput commandInput,
                                               final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());
        String preferredGenre = user.getPreferredGenre();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(preferredGenre));

        return objectNode;
    }

    /**
     * Gets top 5 songs.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the top 5 songs
     */
    public static ObjectNode getTop5Songs(final CommandInput commandInput,
                                          final Admin admin) {
        List<String> songs = admin.getTop5Songs();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(songs));

        return objectNode;
    }

    /**
     * Gets top 5 playlists.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the top 5 playlists
     */
    public static ObjectNode getTop5Playlists(final CommandInput commandInput,
                                              final Admin admin) {
        List<String> playlists = admin.getTop5Playlists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Gets top 5 albums.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the top 5 albums object node
     */
    public static ObjectNode getTop5Albums(final CommandInput commandInput,
                                           final Admin admin) {
        List<String> albums = admin.getTop5Albums();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(albums));

        return objectNode;
    }

    /**
     * Gets top 5 artists.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the top 5 artists object node
     */
    public static ObjectNode getTop5Artists(final CommandInput commandInput,
                                            final Admin admin) {
        List<String> artists = admin.getTop5Artists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(artists));

        return objectNode;
    }

    /**
     * switchConnectionStatus object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode switchConnectionStatus(final CommandInput commandInput,
                                                    final Admin admin) {
        User user = admin.getUser(commandInput.getUsername());

        if (user != null) {
            if (user.getRole().equals("user")) {
                String message = user.switchConnectionStatus(user);

                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", message);

                return objectNode;
            } else {
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("command", commandInput.getCommand());
                objectNode.put("user", commandInput.getUsername());
                objectNode.put("timestamp", commandInput.getTimestamp());
                objectNode.put("message", commandInput.getUsername()
                                + " is not a normal user.");

                return objectNode;
            }
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", "The username "
                    + commandInput.getUsername() + " doesn't exist.");

            return objectNode;
        }
    }

    /**
     * getOnlineUsers object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode getOnlineUsers(final CommandInput commandInput,
                                            final Admin admin) {
        ArrayList<String> onlineUsers = admin.getOnlineUsers();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.putPOJO("result", onlineUsers);

        return objectNode;
    }

    /**
     * addUser object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node with the user
     */
    public static ObjectNode addUser(final CommandInput commandInput,
                                     final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        User newUser = User.createUser(commandInput);

        // Admin.addUser also adds the user besides returning a message
        String message = admin.addUser(newUser);
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * deleteUser object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode deleteUser(final CommandInput commandInput,
                                        final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = admin.deleteUser(user);
            objectNode.put("message", message);
        } else {
            objectNode.put("message", "The username "
                    +  commandInput.getUsername() + " doesn't exist");
        }
        return objectNode;
    }

    /**
     * getAllUsers object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode getAllUsers(final CommandInput commandInput,
                                         final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        ArrayList<String> result = admin.getAllUsers();
        objectNode.putPOJO("result", result);
        return objectNode;
    }

    /**
     * addPodcast object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode addPodcast(final CommandInput commandInput,
                                        final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        User user = admin.getUser(commandInput.getUsername());
        if (user != null && user.getRole().equals("host")) {
            String message = ((Host) user).addPodcast(commandInput, admin);
            objectNode.put("message", message);
        } else if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername()
                           + " doesn't exist.");
        } else {
            objectNode.put("message", user.getUsername() + " is not a host.");
        }
        return objectNode;
    }

    /**
     * removePodcast object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode removePodcast(final CommandInput commandInput,
                                           final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        User user = admin.getUser(commandInput.getUsername());
        if (user != null && user.getRole().equals("host")) {
            String message = ((Host) user).removePodcast(commandInput, admin);
            objectNode.put("message", message);
        } else if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername()
                    + " doesn't exist.");
        } else {
            objectNode.put("message", user.getUsername() + " is not a host.");
        }
        return objectNode;
    }

    /**
     * showPodcasts object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode showPodcasts(final CommandInput commandInput,
                                          final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        User user = admin.getUser(commandInput.getUsername());

        if (user != null) {
            if (user.getRole().equals("host")) {
                ArrayNode results = ((Host) user).getPodcastsFromHost();
                objectNode.putArray("result").addAll(results);
                return objectNode;
            }
        }
        return null;
    }

    /**
     * addAnnouncement object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode addAnnouncement(final CommandInput commandInput,
                                             final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        User user = admin.getUser(commandInput.getUsername());

        if (user != null && user.getRole().equals("host")) {
            String message = ((Host) user).addAnnouncement(commandInput);
            objectNode.put("message", message);
        } else if (user == null) {
            objectNode.put("message", "The username "
                    + commandInput.getUsername() + " doesn't exist.");
        } else {
            objectNode.put("message", user.getUsername() + " is not a host.");
        }
        return objectNode;
    }

    /**
     * removeAnnouncement object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode removeAnnouncement(final CommandInput commandInput,
                                                final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        User user = admin.getUser(commandInput.getUsername());

        if (user != null && user.getRole().equals("host")) {
            String message = ((Host) user).removeAnnouncement(commandInput);
            objectNode.put("message", message);
        } else if (user == null) {
            objectNode.put("message", "The username "
                    + commandInput.getUsername() + "doesn't exist");
        } else {
            objectNode.put("message", user.getUsername() + " is not a host.");
        }
        return objectNode;
    }

    /**
     * addAlbum object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node of the album
     */
    public static ObjectNode addAlbum(final CommandInput commandInput,
                                      final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        User user = admin.getUser(commandInput.getUsername());
        if (user != null && user.getRole().equals("artist")) {
            String message = ((Artist) user).addAlbum(commandInput, admin);
            objectNode.put("message", message);
        } else if (user == null) {
            objectNode.put("message", "The username "
                    + commandInput.getUsername() + " doesn't exist");
        } else {
            objectNode.put("message", user.getUsername() + " is not an artist.");
        }
        return objectNode;
    }

    /**
     * Remove album onject node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode removeAlbum(final CommandInput commandInput,
                                         final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        User user = admin.getUser(commandInput.getUsername());

        if (user != null && user.getRole().equals("artist")) {
            String message = ((Artist) user).removeAlbum(commandInput, admin);
            objectNode.put("message", message);
        } else if (user == null) {
            objectNode.put("message", "The username "
                    + commandInput.getUsername() + " doesn't exist.");
        } else {
            objectNode.put("message", user.getUsername() + " is not an artist.");
        }
        return objectNode;
    }

    /**
     * showAlbums object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode showAlbums(final CommandInput commandInput,
                                        final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            if (user.getRole().equals("artist")) {
                ArrayNode results = ((Artist) user).getAlbumsFromArtist();
                objectNode.putArray("result").addAll(results);
                return objectNode;
            }
        }
        return null;
    }

    /**
     * addEvent object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the objectNode
     */
    public static ObjectNode addEvent(final CommandInput commandInput,
                                      final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = admin.getUser(commandInput.getUsername());

        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername()
                    + " doesn't exist.");
            return objectNode;
        } else if (user.getRole().equals("artist")) {
            String message = ((Artist) user).addEvent(commandInput);
            objectNode.put("message", message);
            return objectNode;
        } else {
            objectNode.put("message", user.getUsername() + " is not an artist.");
            return objectNode;
        }
    }

    /**
     * removeEvent object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode removeEvent(final CommandInput commandInput,
                                         final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = admin.getUser(commandInput.getUsername());

        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername()
                    + " doesn't exist.");
            return objectNode;
        } else if (user.getRole().equals("artist")) {
            String message = ((Artist) user).removeEvent(commandInput);
            objectNode.put("message", message);
            return objectNode;
        } else {
            objectNode.put("message", user.getUsername() + " is not an artist.");
            return objectNode;
        }
    }

    /**
     * addMerch object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode addMerch(final CommandInput commandInput,
                                      final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = admin.getUser(commandInput.getUsername());

        if (user != null) {
            String message = Merch.merchMessage(user, commandInput);
            objectNode.put("message", message);
        } else {
            objectNode.put("message", "The username " + commandInput.getUsername()
                    + " doesn't exist.");
        }
        return objectNode;
    }

    /**
     * printCurrentPage object node.
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode printCurrentPage(final CommandInput commandInput,
                                              final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        User user = admin.getUser(commandInput.getUsername());
        if (user != null) {
            if (user.getConnection().equals("online")) {
                String message = Page.messagePage(user);
                objectNode.put("message", message);
                return objectNode;
            } else {
                objectNode.put("message", user.getUsername() + " is offline.");
                return objectNode;
            }
        }
        return null;
    }

    /**
     * Change page object node
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode changePage(final CommandInput commandInput,
                                        final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        User user = admin.getUser(commandInput.getUsername());

        if (user != null) {
            String message = user.changePage(commandInput);
            objectNode.put("message", message);
        }

        return objectNode;
    }

    /**
     * Wrapped object node
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return the object node
     */
    public static ObjectNode wrapped(final CommandInput commandInput,
                                     final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        User user = admin.getUser(commandInput.getUsername());

        if (user != null) {
            int ts = commandInput.getTimestamp();
            ObjectNode results = user.wrapped(user.getRole(), user.getUsername(),
                    ts, admin);
            objectNode.putPOJO("result", results);
            return objectNode;
        }
        return null;
    }

    /**
     * buyMerch object node
     *
     * @param commandInput the command input
     * @param admin        the admin
     * @return             the object node
     */
    public static ObjectNode buyMerch(final CommandInput commandInput,
                                      final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        User user = admin.getUser(commandInput.getUsername());
        return null;
    }

    /**
     * endProgram object Node
     *
     * @param admin the admin
     * @return the object node
     */
    public static ObjectNode endProgram(final Admin admin) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "endProgram");

        ObjectNode results = admin.endProgram();
        objectNode.putPOJO("result", results);
        return objectNode;
    }
}
