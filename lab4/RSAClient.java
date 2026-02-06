import java.io.*;
import java.net.*;
import java.math.BigInteger;
import java.util.Scanner;

public class RSAClient {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        Socket socket = new Socket("localhost", 1234);
        
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        BigInteger e = (BigInteger) in.readObject();
        BigInteger n = (BigInteger) in.readObject();
        System.out.println("Connected to Server.");
        System.out.println("Using Public Key: (e=" + e + ", n=" + n + ")");

        System.out.print("\nEnter plaintext message: ");
        String message = sc.nextLine();

        BigInteger[] ciphertext = new BigInteger[message.length()];
        System.out.print("Encrypted values: ");
        for (int i = 0; i < message.length(); i++) {
            BigInteger m = BigInteger.valueOf((int) message.charAt(i));
            ciphertext[i] = RSAEngine.encrypt(m, e, n);
            System.out.print(ciphertext[i] + " ");
        }

        out.writeObject(ciphertext);
        System.out.println("\nCiphertext sent to server!");

        socket.close();
        sc.close();
    }
}