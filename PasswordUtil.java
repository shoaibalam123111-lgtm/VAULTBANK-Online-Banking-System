
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Generate secure password hash
    public static String hashPassword(String password) {
        return BCrypt.hashpw(
            password,
            BCrypt.gensalt(12)
        );
    }

    // Verify entered password
    public static boolean checkPassword(
            String password,
            String hashedPassword) {

        return BCrypt.checkpw(
            password,
            hashedPassword
        );
    }
}

