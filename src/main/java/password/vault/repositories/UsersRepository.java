package password.vault.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class UsersRepository {
    public void writeUsers(File file, Map<String, String> users) {
        try {
            new ObjectMapper().writeValue(file, users);
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while writing to a file: ", e);
        }
    }

    public Map<String, String> readUsers(File file) {
        try {
            return new ObjectMapper().readValue(file, Map.class);
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file: ", e);
        }
    }
}
