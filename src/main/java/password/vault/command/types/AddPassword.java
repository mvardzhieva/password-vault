package password.vault.command.types;

import org.apache.commons.codec.binary.Base64;
import password.vault.command.Command;
import password.vault.command.CommandExecutor;
import password.vault.entities.User;
import password.vault.entities.Website;
import password.vault.exceptions.PasswordsClientException;
import password.vault.repositories.CredentialsRepository;
import password.vault.util.AlgorithmConstants;
import password.vault.util.MessageConstants;
import password.vault.util.Password;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class AddPassword implements Command {
    private static final int STATUS_CODE_COMPROMISED = 200;
    private static final String API_URL = "https://api.enzoic.com";
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String API_SECRET = System.getenv("API_SECRET");
    private static final int NUMBER_OF_ARGUMENTS = 4;
    private static final int WEBSITE_INDEX = 1;
    private static final int USER_INDEX = 2;
    private static final int PASSWORD_INDEX = 3;

    private CommandExecutor executor;
    private CredentialsRepository credentialsRepository;
    private Password password;
    private HttpClient client;

    public AddPassword(CommandExecutor executor) {
        this.executor = executor;
        this.credentialsRepository = new CredentialsRepository(this.executor);
        this.password = new Password();
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public String execute(String username, String[] message) {
        if (message.length != NUMBER_OF_ARGUMENTS) {
            return "Command requires website, username and password";
        }
        if (username == null) {
            return MessageConstants.USER_NOT_LOGGED;
        }
        String website = message[WEBSITE_INDEX];
        String websiteUsername = message[USER_INDEX];
        String password = message[PASSWORD_INDEX];
        if (isPasswordCompromised(password)) {
            return "Password is compromised";
        }
        if (existsWebsiteWithUsername(username, website, websiteUsername)) {
            return "Website with this username already exists";
        }
        byte[] encodedPassword = Base64.encodeBase64(password.getBytes());
        executor.addWebsite(username, new Website(website, new User(websiteUsername, encodedPassword)));
        credentialsRepository.add(username);
        return "Password successfully added";
    }

    private boolean isPasswordCompromised(String password) {
        String url = API_URL
                + "/passwords?sha1=" + this.password.hash(AlgorithmConstants.SHA1, password)
                + "&md5=" + this.password.hash(AlgorithmConstants.MD5, password)
                + "&sha256=" + this.password.hash(AlgorithmConstants.SHA256, password);
        String encodedKeys = new String(Base64.encodeBase64((API_KEY + ":" + API_SECRET).getBytes()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("authorization", "basic " + encodedKeys)
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == STATUS_CODE_COMPROMISED;
        } catch (IOException | InterruptedException e) {
            throw new PasswordsClientException(e.getMessage(), e);
        }
    }

    private boolean existsWebsiteWithUsername(String username, String websiteName, String websiteUsername) {
        List<Website> websites = executor.getUsersWebsites().get(username);
        if (websites == null || websites.isEmpty()) {
            return false;
        }
        for (Website w : websites) {
            if (w.name().equals(websiteName) && w.user().username().equals(websiteUsername)) {
                return true;
            }
        }
        return false;
    }
}
