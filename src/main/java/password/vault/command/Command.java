package password.vault.command;

public interface Command {
    String execute(String username, String[] message);

}
