import java.io.*;
import java.net.*;
import java.util.function.Consumer;

/**
 * ChatClient — one instance per logged-in user (Guest or Receptionist).
 * Connects to ChatServer via Socket; incoming messages are forwarded to a
 * JavaFX-thread callback so the UI can update safely.
 */
public class ChatClient {

    private Socket socket;
    private PrintWriter out;
    private final String username;
    private final Consumer<String> onMessage;   // called on JavaFX thread
    private boolean connected = false;

    public ChatClient(String username, Consumer<String> onMessage) {
        this.username = username;
        this.onMessage = onMessage;
    }

    /** Connect to the server and start the listener thread. */
    public void connect() {
        try {
            socket = new Socket("localhost", ChatServer.getPort());
            out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            // Register our username with the server
            out.println(username);
            connected = true;

            Thread reader = new Thread(() -> {
                try (BufferedReader in =
                         new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        final String msg = line;
                        javafx.application.Platform.runLater(() -> onMessage.accept(msg));
                    }
                } catch (IOException e) {
                    connected = false;
                    javafx.application.Platform.runLater(() ->
                        onMessage.accept("[SYSTEM] Disconnected from chat server."));
                }
            });
            reader.setDaemon(true);
            reader.start();

        } catch (IOException e) {
            connected = false;
            javafx.application.Platform.runLater(() ->
                onMessage.accept("[SYSTEM] Could not connect to chat server."));
        }
    }

    /** Send a plain-text message (the server will prepend our username). */
    public void sendMessage(String message) {
        if (connected && out != null) {
            out.println(message);
        }
    }

    public boolean isConnected() { return connected; }

    public String getUsername() { return username; }

    public void disconnect() {
        connected = false;
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
}
