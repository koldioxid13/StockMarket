import java.nio.file.*;
import java.util.List;

public final class Authenticator {
    private static Authenticator authenticator = null;

    private Authenticator(){}

    private static final String USER_FILE = "users.txt";

    public static Authenticator getAuthenticator() {
        if (authenticator == null) {
            authenticator = new Authenticator();
        }
        return authenticator;
    }

    public static String isValidUser(User user) {
        try {
            if (!Files.exists(Paths.get(USER_FILE))) return "invalidUser";

            List<String> lines = Files.readAllLines(Paths.get(USER_FILE));

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String storedName = parts[0];
                    String storedPass = parts[1];

                    if (storedName.equals(user.getUserName())) {
                        if (storedPass.equals(user.getPassword())) {
                            return "valid";
                        } else {
                            return "wrongPassword";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "invalidUser";
    }
}
