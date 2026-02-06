import java.io.*;
import java.net.*;
import java.math.BigInteger;
import java.util.Scanner;

public class RSAServer {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("--- RSA Key Setup ---");
        System.out.print("Enter prime p: ");
        BigInteger p = sc.nextBigInteger();
        System.out.print("Enter prime q: ");
        BigInteger q = sc.nextBigInteger();
        
        BigInteger n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        
        System.out.print("Enter public exponent e (must be coprime to " + phi + "): ");
        BigInteger e = sc.nextBigInteger();
        BigInteger d = e.modInverse(phi);
        
        System.out.println("Generated Private Key d: " + d);

        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("\nServer waiting for connection on port 1234...");
        Socket socket = serverSocket.accept();
        
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        out.writeObject(e);
        out.writeObject(n);

        BigInteger[] ciphertext = (BigInteger[]) in.readObject();
        
        System.out.print("\nCiphertext received: ");
        StringBuilder decryptedMessage = new StringBuilder();
        for (BigInteger c : ciphertext) {
            System.out.print(c + " ");
            BigInteger m = RSAEngine.decrypt(c, d, n);
            decryptedMessage.append((char) m.intValue());
        }

        System.out.println("\nDecrypted Message: " + decryptedMessage.toString());

        socket.close();
        serverSocket.close();
        sc.close();
    }
}