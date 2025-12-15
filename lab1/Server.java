import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int port = 5000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[SERVER] Listening on port " + port + "...");
            Socket socket = serverSocket.accept();
            System.out.println("[SERVER] Client connected!");

            Thread listenThread = new Thread(() -> listenForMessages(socket));
            listenThread.start();

            handleUserMenu(socket);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void listenForMessages(Socket socket) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String received;
            while ((received = input.readLine()) != null) {
                String[] parts = received.split(":", 3);
                if (parts.length == 3) {
                    String algo = parts[0];
                    String key = parts[1];
                    String ciphertext = parts[2];
                    
                    System.out.println("\n\n>>> MSG RECEIVED!");
                    System.out.println("   Ciphertext: " + ciphertext);
                    System.out.println("   Key: " + key + " (Auto-detected)");
                    
                    String plaintext = decrypt(algo, key, ciphertext);
                    System.out.println("   [DECRYPTED]: " + plaintext);
                    System.out.print("\nSelect Option: ");
                }
            }
        } catch (IOException e) { System.exit(0); }
    }

    private static String decrypt(String algo, String key, String text) {
        try {
            switch (algo) {
                case "CAESAR": return CipherLogic.caesar(text, Integer.parseInt(key), false);
                case "MONO":   return CipherLogic.monoalphabetic(text, key, false);
                case "PLAY":   return CipherLogic.playfair(text, key, false);
                case "HILL":   return CipherLogic.hill(text, key, false);
                case "VIG":    return CipherLogic.vigenere(text, key, false);
                case "OTP":    return CipherLogic.vigenere(text, key, false); // Reuse Vigenere Logic
                case "RAIL":   return CipherLogic.railFence(text, Integer.parseInt(key), false);
                default: return "UNKNOWN ALGO";
            }
        } catch (Exception e) { return "Error decrypting: " + e.getMessage(); }
    }

    private static void handleUserMenu(Socket socket) {
        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                System.out.println("\n=== SERVER MENU ===");
                System.out.println("1. Encrypt & Send");
                System.out.println("2. Exit");
                System.out.print("Select: ");
                String choice = scanner.nextLine();
                if (choice.equals("2")) System.exit(0);
                if (choice.equals("1")) performEncryptionAndSend(output);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void performEncryptionAndSend(PrintWriter output) {
        System.out.println("1. Caesar | 2. Mono | 3. Playfair | 4. Hill | 5. Vigenere | 6. OTP | 7. Rail");
        int algo = Integer.parseInt(scanner.nextLine());
        System.out.print("Plaintext: ");
        String text = scanner.nextLine();
        
        String key = "", cipher = "", code = "";
        
        if (algo == 1) {
            System.out.print("Shift: "); key = scanner.nextLine();
            cipher = CipherLogic.caesar(text, Integer.parseInt(key), true);
            code = "CAESAR";
        } else if (algo == 2) {
            System.out.print("Key (26 chars): "); key = scanner.nextLine();
            cipher = CipherLogic.monoalphabetic(text, key, true);
            code = "MONO";
        } else if (algo == 3) {
            System.out.print("Keyword: "); key = scanner.nextLine();
            cipher = CipherLogic.playfair(text, key, true);
            code = "PLAY";
        } else if (algo == 4) {
            System.out.print("Key (4 chars): "); key = scanner.nextLine();
            cipher = CipherLogic.hill(text, key, true);
            code = "HILL";
        } else if (algo == 5) {
            System.out.print("Keyword: "); key = scanner.nextLine();
            cipher = CipherLogic.vigenere(text, key, true);
            code = "VIG";
        } else if (algo == 6) {
            System.out.print("Key (Length >= Text): "); key = scanner.nextLine();
            cipher = CipherLogic.vigenere(text, key, true);
            code = "OTP";
        } else if (algo == 7) {
            System.out.print("Rails: "); key = scanner.nextLine();
            cipher = CipherLogic.railFence(text, Integer.parseInt(key), true);
            code = "RAIL";
        }

        output.println(code + ":" + key + ":" + cipher);
        System.out.println("Sent: " + cipher);
    }
}