package password.vault.command.types;

import password.vault.command.Command;
import password.vault.util.MessageConstants;

public class Logout implements Command {
    @Override
    public String execute(String username, String[] message) {
        if (username != null) {
            return "Successfully logged out";
        }
        return MessageConstants.USER_NOT_LOGGED;
    }
}
