import java.io.*;
import java.net.*;
import java.math.BigInteger;
import java.util.Scanner;

public class MITMNode {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        BigInteger privE = new BigInteger("15");
        BigInteger pubE = DHEngine.g.modPow(privE, DHEngine.p);

        ServerSocket proxyServer = new ServerSocket(8888); 
        System.out.println("[EVE] Waiting for Alice on port 8888...");
        Socket alice = proxyServer.accept();
        
        Socket relay = new Socket("localhost", 9999);
        DataInputStream fromA = new DataInputStream(alice.getInputStream());
        DataOutputStream toA = new DataOutputStream(alice.getOutputStream());
        DataInputStream fromS = new DataInputStream(relay.getInputStream());
        DataOutputStream toS = new DataOutputStream(relay.getOutputStream());

        BigInteger pubA = new BigInteger(fromA.readUTF());
        toS.writeUTF(pubE.toString()); 
        
        BigInteger pubB = new BigInteger(fromS.readUTF());
        toA.writeUTF(pubE.toString()); 

        BigInteger keyWithAlice = pubA.modPow(privE, DHEngine.p);
        BigInteger keyWithBob = pubB.modPow(privE, DHEngine.p);
        System.out.println("[EVE] Secrets Established -> Alice: " + keyWithAlice + " | Bob: " + keyWithBob);

        while (true) {
            String cipher = fromA.readUTF();
            String plain = DHEngine.xorCipher(cipher, keyWithAlice);
            System.out.println("\n[EVE] Alice originally sent: " + plain);
            
            System.out.print("[EVE] Enter the FAKE message to send to Bob: ");
            String fakeMsg = sc.nextLine();
            
            toS.writeUTF(DHEngine.xorCipher(fakeMsg, keyWithBob));
        }
    }
}