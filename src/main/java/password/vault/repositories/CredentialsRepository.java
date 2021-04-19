package password.vault.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import password.vault.command.CommandExecutor;
import password.vault.entities.Website;
import password.vault.util.DatabaseConstants;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CredentialsRepository {
    private CommandExecutor executor;

    public CredentialsRepository(CommandExecutor executor) {
        this.executor = executor;
    }

    public void add(String username) {
        File file = new File(DatabaseConstants.RESOURCES_DIRECTORY + File.separator + username + ".json");
        write(file, executor.getUsersWebsites().get(username));
    }

    public List<Website> read(File file) {
        try {
//            return new ObjectMapper().readValue(file, List.class);
            return new ObjectMapper().readValue(file,new TypeReference<List<Website>>() {});
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file: ", e);
        }
    }

    private void write(File file, List<Website> websites) {
        try {
            new ObjectMapper().writeValue(file, websites);
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while writing to a file: ", e);
        }
    }
}
