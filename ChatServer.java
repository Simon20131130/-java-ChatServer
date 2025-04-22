import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    // Itt tároljuk az összes csatlakozott kliens kimenetét
    private static final Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Chat szerver elindult a 12345-ös porton...");
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            while (true) {
                // Várjuk a kliens csatlakozását
                Socket clientSocket = serverSocket.accept();
                System.out.println("Új kliens csatlakozott: " + clientSocket);

                // Külön szálon kezeljük
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            clientWriters.add(out);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Kapott üzenet: " + message);
                // Továbbküldés minden más kliensnek
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Kliens lecsatlakozott: " + socket);
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
