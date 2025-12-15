import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            System.out.println("[CLIENT] Connected to Server!");

            Thread listenThread = new Thread(() -> listenForMessages(socket));
            listenThread.start();

            handleUserMenu(socket);
        } catch (IOException e) { 
            System.out.println("Server not found."); 
        }
    }

    private static void listenForMessages(Socket socket) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String received;
            while ((received = input.readLine()) != null) {
                String[] parts = received.split(":", 3);
                if (parts.length == 3) {
                    System.out.println("\n\n>>> MSG RECEIVED!");
                    System.out.println("   Ciphertext: " + parts[2]);
                    System.out.println("   Key: " + parts[1]);
                    System.out.println("   [DECRYPTED]: " + decrypt(parts[0], parts[1], parts[2]));
                    System.out.print("\nSelect: ");
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
                case "OTP":    return CipherLogic.vigenere(text, key, false);
                case "RAIL":   return CipherLogic.railFence(text, Integer.parseInt(key), false);
                default: return "UNKNOWN";
            }
        } catch (Exception e) { return "Error"; }
    }

    private static void handleUserMenu(Socket socket) {
        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                System.out.println("\n=== CLIENT MENU ===");
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
            cipher = CipherLogic.caesar(text, Integer.parseInt(key), true); code = "CAESAR";
        } else if (algo == 2) {
            System.out.print("Key (26 chars): "); key = scanner.nextLine();
            cipher = CipherLogic.monoalphabetic(text, key, true); code = "MONO";
        } else if (algo == 3) {
            System.out.print("Keyword: "); key = scanner.nextLine();
            cipher = CipherLogic.playfair(text, key, true); code = "PLAY";
        } else if (algo == 4) {
            System.out.print("Key (4 chars): "); key = scanner.nextLine();
            cipher = CipherLogic.hill(text, key, true); code = "HILL";
        } else if (algo == 5) {
            System.out.print("Keyword: "); key = scanner.nextLine();
            cipher = CipherLogic.vigenere(text, key, true); code = "VIG";
        } else if (algo == 6) {
            System.out.print("Key: "); key = scanner.nextLine();
            cipher = CipherLogic.vigenere(text, key, true); code = "OTP";
        } else if (algo == 7) {
            System.out.print("Rails: "); key = scanner.nextLine();
            cipher = CipherLogic.railFence(text, Integer.parseInt(key), true); code = "RAIL";
        }
        output.println(code + ":" + key + ":" + cipher);
        System.out.println("Sent: " + cipher);
    }
}