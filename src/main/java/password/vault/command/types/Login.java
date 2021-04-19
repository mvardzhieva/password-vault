package password.vault.command.types;

import password.vault.command.Command;
import password.vault.command.CommandExecutor;
import password.vault.util.Password;

public class Login implements Command {
    private static final int NUMBER_OF_ARGUMENTS = 3;
    private static final int USER_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;

    private CommandExecutor executor;
    private Password password;

    public Login(CommandExecutor executor) {
        this.executor = executor;
        this.password = new Password();
    }

    @Override
    public String execute(String username, String[] message) {
        if (message.length != NUMBER_OF_ARGUMENTS) {
            return "Command requires username and password";
        }
        String user = message[USER_INDEX];
        String password = message[PASSWORD_INDEX];
        if (executor.getUsers().containsKey(user)
                && this.password.areMatching(password, executor.getUsers().get(user))) {
            return "User " + user + " successfully logged in";
        }
        return "Invalid username/password combination";
    }
}
