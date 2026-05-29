import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

    private static final int PORT = 9090;
    private static ChatServer instance;

    private ServerSocket serverSocket;
    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private boolean running = false;


    public static synchronized ChatServer getInstance() {
        if (instance == null) instance = new ChatServer();
        return instance;
    }


    public void start() {
        if (running) return;
        running = true;
        Thread serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("[ChatServer] Listening on port " + PORT);
                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(clientSocket, this);
                    clients.add(handler);
                    Thread t = new Thread(handler);
                    t.setDaemon(true);
                    t.start();
                }
            } catch (IOException e) {
                if (running) System.err.println("[ChatServer] Error: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    // ── Broadcast to ALL connected clients ─────────────────────────────────────
    public void broadcastAll(String message) {
        for (ClientHandler c : clients) {
            c.send(message);
        }
    }

    public void removeClient(ClientHandler handler) {
        clients.remove(handler);
    }

    public static int getPort() { return PORT; }

    // ── Inner class: one handler per connected client ──────────────────────────
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private final ChatServer server;
        private PrintWriter out;
        private String username = "Unknown";

        ClientHandler(Socket socket, ChatServer server) {
            this.socket = socket;
            this.server = server;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);

                // First line the client sends is its username
                username = in.readLine();
                if (username == null) return;

                server.broadcastAll("[SYSTEM] " + username + " joined the chat.");

                String line;
                while ((line = in.readLine()) != null) {
                    String msg = username + ": " + line;
                    server.broadcastAll(msg);
                }
            } catch (IOException e) {
                // client disconnected
            } finally {
                server.removeClient(this);
                server.broadcastAll("[SYSTEM] " + username + " left the chat.");
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        public void send(String message) {
            if (out != null) out.println(message);
        }
    }
}
