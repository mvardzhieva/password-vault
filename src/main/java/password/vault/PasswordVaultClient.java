package password.vault;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PasswordVaultClient {
    private static final String DISCONNECT = "disconnect";
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 4242;
    private static final int BUFFER_SIZE = 1024;

    private ByteBuffer buffer;

    public PasswordVaultClient() {
        this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    public static void main(String[] args) {
        new PasswordVaultClient().start();
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            System.out.println("Connected to the server.");
            while (true) {
                String message = scanner.nextLine();
                if (message.equals(DISCONNECT)) {
                    break;
                }
                buffer.clear();
                buffer.put(message.getBytes());
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
                socketChannel.read(buffer);
                buffer.flip();
                byte[] byteArray = new byte[buffer.remaining()];
                buffer.get(byteArray);
                String reply = new String(byteArray, StandardCharsets.UTF_8);
                System.out.println("The server replied: " + reply);
            }
        } catch (IOException e) {
            System.err.println("An error occurred in the client I/O: " + e.getMessage());
        }
    }
}
