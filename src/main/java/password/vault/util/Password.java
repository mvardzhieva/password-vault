package password.vault.util;

import org.mindrot.jbcrypt.BCrypt;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Password {
    private static final int WORKLOAD = 12;
    private static final int PASSWORD_LENGTH = 15;
    private static final int FROM_SYMBOL_ASCII_CODE = 33;
    private static final int TO_SYMBOL_ASCII_CODE = 122;

    public String hash(String passwordPlaintext) {
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(passwordPlaintext, salt);
    }

    public boolean areMatching(String passwordPlaintext, String hashedPassword) {
        return BCrypt.checkpw(passwordPlaintext, hashedPassword);
    }

    public String generate() {
        return new Random()
                .ints(PASSWORD_LENGTH, FROM_SYMBOL_ASCII_CODE, TO_SYMBOL_ASCII_CODE)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public String hash(String algorithm, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashedPassword = new StringBuilder(no.toString(16));
            while (hashedPassword.length() < 32) {
                hashedPassword.insert(0, "0");
            }
            return hashedPassword.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
