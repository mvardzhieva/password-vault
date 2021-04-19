package password.vault.command.types;

import org.apache.commons.codec.binary.Base64;
import password.vault.command.Command;
import password.vault.command.CommandExecutor;
import password.vault.entities.User;
import password.vault.entities.Website;
import password.vault.repositories.CredentialsRepository;
import password.vault.util.MessageConstants;
import password.vault.util.Password;

public class GeneratePassword implements Command {
    private static final int NUMBER_OF_ARGUMENTS = 3;
    private static final int WEBSITE_INDEX = 1;
    private static final int USER_INDEX = 2;

    private CommandExecutor executor;
    private CredentialsRepository credentialsRepository;
    private Password password;

    public GeneratePassword(CommandExecutor executor) {
        this.executor = executor;
        this.credentialsRepository = new CredentialsRepository(this.executor);
        this.password = new Password();
    }

    @Override
    public String execute(String username, String[] message) {
        if (message.length != NUMBER_OF_ARGUMENTS) {
            return MessageConstants.REQUIRED_WEBSITE_AND_USERNAME;
        }
        if (username == null) {
            return MessageConstants.USER_NOT_LOGGED;
        }
        String website = message[WEBSITE_INDEX];
        String websiteUsername = message[USER_INDEX];
        String generatedPassword = password.generate();
        byte[] encodedPassword = Base64.encodeBase64(generatedPassword.getBytes());
        executor.addWebsite(username, new Website(website, new User(websiteUsername, encodedPassword)));
        credentialsRepository.add(username);
        return "Website successfully added, your password is " + generatedPassword;
    }
}
