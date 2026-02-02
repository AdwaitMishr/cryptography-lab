import java.io.*;
import java.net.*;
import java.util.Scanner;

public class AESClient {
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 5000;
        byte[] key = AESEngine.hexToBytes("0f1571c947d9e8590cb7add6af7f6798");

        Socket socket = new Socket(host, port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter plaintext message: ");
        String plaintext = sc.nextLine();
        
        // Prepare and Encrypt
        byte[] padded = AESEngine.pad(plaintext);
        byte[] encrypted = AESEngine.encrypt(padded, key);
        String ciphertextHex = AESEngine.bytesToHex(encrypted);

        System.out.println("Plaintext message: " + plaintext);
        System.out.println("Plaintext (padded hex): " + AESEngine.bytesToHex(padded));
        System.out.println("Encryption Key: " + AESEngine.bytesToHex(key));
        System.out.println("Ciphertext: " + ciphertextHex);

        // Send
        System.out.println("Sending ciphertext to server...");
        out.writeUTF(ciphertextHex);

        // Receive Response
        String responseHex = in.readUTF();
        byte[] decryptedResBytes = AESEngine.decrypt(AESEngine.hexToBytes(responseHex), key);
        System.out.println("Server response (decrypted): " + new String(decryptedResBytes).trim());

        socket.close();
        sc.close();
    }
}