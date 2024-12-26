package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter your username: ");
            String username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty. Exiting...");
                return;
            }

            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected as " + username);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(username + " has joined the chat!");

            new Thread(() -> handleReceiving(socket, username)).start();
            handleSending(socket, username, out);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleReceiving(Socket socket, String username) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                if (!message.startsWith(username + ":")) {
                    System.out.println("\n" + message);
                }
                System.out.print(username + ": ");
            }
        } catch (IOException e) {
            System.out.println("Connection closed.");
        }
    }

    private static void handleSending(Socket socket, String username, PrintWriter out) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print(username + ": ");
                String message = scanner.nextLine();
                if ("exit".equalsIgnoreCase(message)) {
                    out.println(username + " has left the chat.");
                    break;
                }
                out.println(username + ": " + message);
            }
            socket.close();
        } catch (IOException e) {
            System.out.println("Error sending message.");
        }
    }
}
