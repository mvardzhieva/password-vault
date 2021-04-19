package password.vault.exceptions;

public class PasswordsClientException extends RuntimeException {
    public PasswordsClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
