package password.vault.command;

import password.vault.entities.Website;
import password.vault.repositories.CredentialsRepository;
import password.vault.repositories.UsersRepository;
import password.vault.util.DatabaseConstants;
import password.vault.command.types.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CommandExecutor {
    private static final int NUMBER_OF_ARGUMENTS = 4;
    private static final int COMMAND_INDEX = 0;
    private static final String FILE_EXTENSION = "[.][^.]+$";

    private UsersRepository usersRepository;
    private CredentialsRepository credentialsRepository;
    private Map<String, String> users;
    private final Map<CommandType, Command> commands;
    private Map<String, List<Website>> usersWebsites;

    public CommandExecutor() {
        createResourcesDirectory();
        this.usersRepository = new UsersRepository();
        initializeUsers();
        this.commands = new HashMap<>();
        this.credentialsRepository=new CredentialsRepository(this);
        initializeUsersWebsites();
        generateCommands();
    }

    public String executeCommand(String username, String message) {
        String[] arguments = message.split("\\s+", NUMBER_OF_ARGUMENTS);
        String commandName = arguments[COMMAND_INDEX].toUpperCase();
        if (CommandType.contains(commandName)) {
            Command command = this.commands.get(CommandType.valueOf(commandName));
            return command.execute(username, arguments);
        }
        return "Unknown command";
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public void addUser(String user, String hashedPassword) {
        users.put(user, hashedPassword);
    }

    public Map<String, List<Website>> getUsersWebsites() {
        return usersWebsites;
    }

    public void addWebsite(String user, Website website) {
        if (!usersWebsites.containsKey(user)) {
            usersWebsites.put(user, new ArrayList<>());
        }
        usersWebsites.get(user).add(website);
    }

    public void removeWebsite(String user, Website website) {
        usersWebsites.get(user).remove(website);
    }

    private void createResourcesDirectory() {
        File resources = new File(DatabaseConstants.RESOURCES_DIRECTORY);
        if (!resources.exists()) {
            resources.mkdir();
        }
    }

    private void initializeUsers() {
        users = Files.exists(DatabaseConstants.USERS_PATH)
                ? usersRepository.readUsers(DatabaseConstants.USERS_FILE)
                : new HashMap<>();
    }

    private void generateCommands() {
        this.commands.put(CommandType.REGISTER, new Register(this));
        this.commands.put(CommandType.LOGIN, new Login(this));
        this.commands.put(CommandType.LOGOUT, new Logout());
        this.commands.put(CommandType.DISCONNECT, new Disconnect());
        this.commands.put(CommandType.GENERATE_PASSWORD, new GeneratePassword(this));
        this.commands.put(CommandType.RETRIEVE_CREDENTIALS, new RetrieveCredentials(this));
        this.commands.put(CommandType.REMOVE_PASSWORD, new RemovePassword(this));
        this.commands.put(CommandType.ADD_PASSWORD, new AddPassword(this));
    }

    private void initializeUsersWebsites() {
        usersWebsites = new HashMap<>();
        try (Stream<Path> paths = Files.walk(Paths.get(DatabaseConstants.RESOURCES_DIRECTORY))) {
            paths.filter(p -> !p.equals(DatabaseConstants.USERS_PATH) && !p.equals(DatabaseConstants.RESOURCES_PATH))
                    .forEach(p -> usersWebsites
                            .put(p.getFileName().toString().replaceFirst(FILE_EXTENSION, ""),
                                    credentialsRepository.read(p.toFile())));
        } catch (IOException e) {
            System.err.println("An error occurred while reading data from files: " + e.getMessage());
        }
    }

}
