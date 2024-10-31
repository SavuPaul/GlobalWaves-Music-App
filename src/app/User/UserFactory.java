package app.User;

public final class UserFactory {
    /**
     * Creates user based on type.
     *
     * @param type the type
     * @return     the user
     */
    public static User createNewUser(final String type, final String username,
                                     final int age, final String city) {
        return switch (type) {
            case "user" -> new User(username, age, city);
            case "artist" -> new Artist(username, age, city);
            case "host" -> new Host(username, age, city);
            default -> null;
        };
    }
}
