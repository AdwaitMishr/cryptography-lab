import java.io.*;
import java.net.*;

public class DHServer {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9999);
        System.out.println("[SERVER] Relay Active. Waiting for connections...");
        
        Socket s1 = server.accept(); 
        DataInputStream in1 = new DataInputStream(s1.getInputStream());
        DataOutputStream out1 = new DataOutputStream(s1.getOutputStream());
        
        Socket s2 = server.accept();
        DataInputStream in2 = new DataInputStream(s2.getInputStream());
        DataOutputStream out2 = new DataOutputStream(s2.getOutputStream());

        new Thread(() -> { try { while(true) out2.writeUTF(in1.readUTF()); } catch(Exception e){} }).start();
        new Thread(() -> { try { while(true) out1.writeUTF(in2.readUTF()); } catch(Exception e){} }).start();
    }
}