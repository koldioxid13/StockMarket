import java.nio.file.*;
import java.io.IOException;
import java.util.List;

public class UserManager {
    private static final String USER_FILE = "users.txt";

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
            String userLine = user.getUserName() + "|" + user.getPassword() + System.lineSeparator();
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
}