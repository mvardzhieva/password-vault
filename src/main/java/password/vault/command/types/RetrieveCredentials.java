package password.vault.command.types;

import org.apache.commons.codec.binary.Base64;
import password.vault.command.Command;
import password.vault.command.CommandExecutor;
import password.vault.entities.Website;
import password.vault.util.MessageConstants;

import java.util.List;

public class RetrieveCredentials implements Command {
    private static final int NUMBER_OF_ARGUMENTS = 3;
    private static final int WEBSITE_INDEX = 1;
    private static final int USER_INDEX = 2;

    private CommandExecutor executor;

    public RetrieveCredentials(CommandExecutor executor) {
        this.executor = executor;
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
        String user = message[USER_INDEX];
        List<Website> websites = executor.getUsersWebsites().get(username);
        if (websites == null || websites.isEmpty()) {
            return MessageConstants.USER_NO_WEBSITES;
        }
        for (Website w : websites) {
            if (w.name().equals(website) && w.user().username().equals(user)) {
                String password = new String(Base64.decodeBase64(w.user().password()));
                return "Password for this website is " + password;
            }
        }
        return MessageConstants.INVALID_WEBSITE_USERNAME;
    }
}
