import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordUtils {

    private static final int ITERATIONS  = 100_000;
    private static final int KEY_BITS    = 256;
    private static final int SALT_BYTES  = 16;
    private static final String ALGO     = "PBKDF2WithHmacSHA256";
    private static final String PREFIX   = "$PBKDF2$";

    private PasswordUtils() {}

    public static String hash(String rawPassword) {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        byte[] hash = derive(rawPassword.toCharArray(), salt, ITERATIONS, KEY_BITS);
        return PREFIX + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Verifies a raw password against a stored value.
     * Handles both PBKDF2 hashes (stored in DB) and plain-text (new objects
     * not yet persisted), so existing code keeps working without changes.
     */
    public static boolean verify(String rawPassword, String stored) {
        if (rawPassword == null || stored == null) return false;
        if (!stored.startsWith(PREFIX)) return stored.equals(rawPassword);
        // $PBKDF2$<iter>$<salt>$<hash>  → split("\\$") gives ["","PBKDF2",iter,salt,hash]
        String[] parts = stored.split("\\$");
        if (parts.length != 5) return false;
        try {
            int iter = Integer.parseInt(parts[2]);
            byte[] salt = Base64.getDecoder().decode(parts[3]);
            byte[] expected = Base64.getDecoder().decode(parts[4]);
            byte[] actual = derive(rawPassword.toCharArray(), salt, iter, expected.length * 8);
            return constantTimeEquals(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] derive(char[] password, byte[] salt, int iter, int keyBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iter, keyBits);
            return SecretKeyFactory.getInstance(ALGO).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("PBKDF2 unavailable", e);
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int diff = 0;
        for (int i = 0; i < a.length; i++) diff |= a[i] ^ b[i];
        return diff == 0;
    }
}
