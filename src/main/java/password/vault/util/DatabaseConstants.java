package password.vault.util;

import java.io.File;
import java.nio.file.Path;

public class DatabaseConstants {
    public static final String RESOURCES_DIRECTORY = "resources";
    public static final Path RESOURCES_PATH = Path.of("resources");
    public static final File USERS_FILE = new File("resources" + File.separator + "users.json");
    public static final Path USERS_PATH = Path.of("resources" + File.separator + "users.json");
}
