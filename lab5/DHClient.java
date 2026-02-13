import java.io.*;
import java.net.*;
import java.math.BigInteger;
import java.util.Scanner;

public class DHClient {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Port (Alice: 8888, Bob: 9999): ");
        int port = sc.nextInt();
        Socket socket = new Socket("localhost", port);

        System.out.print("Private Key: ");
        BigInteger priv = sc.nextBigInteger();
        BigInteger pub = DHEngine.g.modPow(priv, DHEngine.p);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        out.writeUTF(pub.toString());
        BigInteger partnerPub = new BigInteger(in.readUTF());
        BigInteger secret = partnerPub.modPow(priv, DHEngine.p);
        System.out.println("Shared Secret Established: " + secret);

        sc.nextLine();
        new Thread(() -> {
            try { while(true) System.out.println("\n[Attacker]: " + DHEngine.xorCipher(in.readUTF(), secret)); } catch(Exception e){}
        }).start();

        while(true) {
            String msg = sc.nextLine();
            out.writeUTF(DHEngine.xorCipher(msg, secret));
        }
    }
}