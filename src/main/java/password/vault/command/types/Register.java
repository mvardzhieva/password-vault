package password.vault.command.types;

import password.vault.command.Command;
import password.vault.command.CommandExecutor;
import password.vault.repositories.UsersRepository;
import password.vault.util.DatabaseConstants;
import password.vault.util.Password;

public class Register implements Command {
    private static final int NUMBER_OF_ARGUMENTS = 4;
    private static final int USER_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;
    private static final int PASSWORD_REPEATED_INDEX = 3;

    private CommandExecutor executor;
    private Password password;
    private UsersRepository usersRepository;

    public Register(CommandExecutor executor) {
        this.executor = executor;
        this.password = new Password();
        this.usersRepository = new UsersRepository();
    }

    @Override
    public String execute(String username, String[] message) {
        if (message.length != NUMBER_OF_ARGUMENTS) {
            return "Command requires username, password and repeated password";
        }
        String user = message[USER_INDEX];
        if (executor.getUsers().containsKey(user)) {
            return "Username " + user + " is already taken, select another one";
        }
        String password = message[PASSWORD_INDEX];
        String passwordRepeated = message[PASSWORD_REPEATED_INDEX];
        if (!password.equals(passwordRepeated)) {
            return "Password and repeated password must match";
        }
        executor.addUser(user, this.password.hash(password));
        usersRepository.writeUsers(DatabaseConstants.USERS_FILE, executor.getUsers());
        return "Username " + user + " successfully registered";
    }
}
