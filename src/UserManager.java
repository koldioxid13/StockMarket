import java.nio.file.*;
import java.io.IOException;
import java.util.List;

public final class UserManager {
    private static UserManager userManager = null;

    private UserManager(){}

    private static final String USER_FILE = "users.txt";
    private User currentUser;
    private User[] allUsers;

    public static UserManager getUserManager() {
        if (userManager == null) {
            userManager = new UserManager();
        }
        return userManager;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public boolean saveUser(User user) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(USER_FILE));
            boolean exists = false;
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equals(user.getUserName())) {
                    exists = true;
                    return false;
                }
            }
            String userLine = user.getUserName() + "|" + user.getPassword() + "|" + user.getCash() + System.lineSeparator();
            Files.write(
                    Paths.get(USER_FILE),
                    userLine.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

            return true;


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserCash(User user) {
        try {
            if (!Files.exists(Paths.get(USER_FILE))) return "0";

            List<String> lines = Files.readAllLines(Paths.get(USER_FILE));

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String storedName = parts[0];
                    String storedCash = parts[2];

                    if (storedName.equals(user.getUserName())) {
                        return storedCash;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}