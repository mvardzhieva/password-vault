package password.vault;

import password.vault.command.CommandExecutor;
import password.vault.util.MessageConstants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PasswordVaultServer {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 4242;
    private static final int BUFFER_SIZE = 1024;
    private static final int USER_INDEX = 1;

    private ByteBuffer buffer;
    private boolean isServerRunning;
    private Map<SocketChannel, String> loggedUsers;
    private CommandExecutor executor;

    public PasswordVaultServer() {
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.isServerRunning = true;
        loggedUsers = new HashMap<>();
        this.executor = new CommandExecutor();
    }

    public static void main(String[] args) {
        new PasswordVaultServer().start();
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (this.isServerRunning) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        read(key);
                    } else if (key.isAcceptable()) {
                        accept(selector, key);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            System.err.println("There is a problem with the server socket: " + e.getMessage());
        }
    }

    public void stop() {
        this.isServerRunning = false;
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        this.buffer.clear();
        int r = socketChannel.read(this.buffer);
        if (r < 0) {
            System.out.println(MessageConstants.USER_DISCONNECTED);
            socketChannel.close();
            return;
        }
        this.buffer.flip();
        String message = StandardCharsets.UTF_8.decode(this.buffer).toString().trim();
        String result = this.executor.executeCommand(this.loggedUsers.get(socketChannel), message);
        updateLoggedUsers(result, socketChannel);

        System.out.println(result);

        this.buffer.clear();
        this.buffer.put((result + System.lineSeparator()).getBytes());
        this.buffer.flip();
        socketChannel.write(this.buffer);
        if (result.equals(MessageConstants.USER_DISCONNECTED)) {
            socketChannel.close();
        }
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = socketChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private void updateLoggedUsers(String result, SocketChannel socketChannel) {
        if (result.contains(MessageConstants.USER_REGISTERED) || result.contains(MessageConstants.USER_LOGIN)) {
            String user = result.split(" ")[USER_INDEX];
            this.loggedUsers.put(socketChannel, user);
        } else if (result.equals(MessageConstants.USER_LOGOUT) || result.equals(MessageConstants.USER_DISCONNECTED)) {
            this.loggedUsers.put(socketChannel, null);
        }
    }
}
