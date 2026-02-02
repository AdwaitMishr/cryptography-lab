import java.io.*;
import java.net.*;

public class AESServer {
    public static void main(String[] args) throws IOException {
        int port = 5000;
        byte[] key = AESEngine.hexToBytes("0f1571c947d9e8590cb7add6af7f6798");

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is running... waiting for client.");

        Socket socket = serverSocket.accept();
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        String ciphertextHex = in.readUTF();
        System.out.println("Ciphertext received: " + ciphertextHex);
        
        byte[] cipherBytes = AESEngine.hexToBytes(ciphertextHex);
        byte[] decryptedBytes = AESEngine.decrypt(cipherBytes, key);
        String decryptedMsg = new String(decryptedBytes).trim();
        
        System.out.println("Decryption Key: " + AESEngine.bytesToHex(key));
        System.out.println("Decrypted Plaintext: " + decryptedMsg);

        String ack = "Message received successfully.";
        byte[] encryptedAck = AESEngine.encrypt(AESEngine.pad(ack), key);
        out.writeUTF(AESEngine.bytesToHex(encryptedAck));

        socket.close();
        serverSocket.close();
    }
}