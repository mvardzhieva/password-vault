package password.vault.command.types;

import password.vault.command.Command;
import password.vault.command.CommandExecutor;
import password.vault.entities.Website;
import password.vault.repositories.CredentialsRepository;
import password.vault.util.MessageConstants;

import java.util.Iterator;
import java.util.List;

public class RemovePassword implements Command {
    private static final int NUMBER_OF_ARGUMENTS = 3;
    private static final int WEBSITE_INDEX = 1;
    private static final int USER_INDEX = 2;

    private CommandExecutor executor;
    private CredentialsRepository credentialsRepository;

    public RemovePassword(CommandExecutor executor) {
        this.executor = executor;
        this.credentialsRepository = new CredentialsRepository(this.executor);
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
        if (websites == null) {
            return MessageConstants.USER_NO_WEBSITES;
        }
        Iterator<Website> iterator = websites.iterator();
        while (iterator.hasNext()) {
            Website w = iterator.next();
            if (w.name().equals(website) && w.user().username().equals(user)) {
                executor.removeWebsite(username, w);
                credentialsRepository.add(username);
                return "Successfully removed";
            }
        }
        return MessageConstants.INVALID_WEBSITE_USERNAME;
    }
}
