package socket_project;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static final Set<PrintWriter> clients = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Chat server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                clients.add(out);
                String message;
                while ((message = in.readLine()) != null) {
                    broadcast(message);
                }
            } catch (IOException e) {
                System.out.println("A client disconnected.");
            } finally {
                cleanup();
            }
        }

        private void broadcast(String message) {
            synchronized (clients) {
                clients.forEach(client -> client.println(message));
            }
        }

        private void cleanup() {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket.");
            }
            clients.removeIf(writer -> writer.checkError());
        }
    }
}