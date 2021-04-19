package password.vault.command.types;

import password.vault.command.Command;

public class Disconnect implements Command {
    @Override
    public String execute(String username, String[] message) {
        return "Disconnected from server";
    }
}
