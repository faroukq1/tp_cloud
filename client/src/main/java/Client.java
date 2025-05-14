import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private final String username;
    private final String mdp;
    private Socket client;

    public Client(String username, String mdp) {
        this.username = username;
        this.mdp = mdp;
        try {
            this.client = new Socket("localhost", 3020);
            System.out.println("Connexion établie avec le serveur.");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean login() {
        return sendRequest("log," + username + "," + mdp);
    }

    public boolean inscription() {
        return sendRequest("insc," + username + "," + mdp);
    }

    private boolean sendRequest(String message) {
        try (
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))
        ) {
            out.println(message);
            String response = in.readLine();
            return "OK".equalsIgnoreCase(response);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Méthode d'upload de fichier
     */
    public boolean upload(String username, String filePath) {
    try {
        File originalFile = new File(filePath);
        String fileName = originalFile.getName();
        
        // Read file data as bytes
        byte[] fileData = Files.readAllBytes(originalFile.toPath());
        
        // Convert file data to Base64 String to avoid issues with binary data in command string
        String fileDataBase64 = java.util.Base64.getEncoder().encodeToString(fileData);
        
        try (
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))
        ) {
            // Send the complete command with all parts including the file data as Base64
            out.println("upload," + username + "," + fileName + "," + fileDataBase64);
            
            // Receive response
            String response = in.readLine();
            return "OK".equalsIgnoreCase(response);
        }
    } catch (IOException ex) {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
}


    public void close() {
        try {
            if (client != null && !client.isClosed()) {
                client.close();
                System.out.println("Connexion fermée.");
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static void main (String [] args) {
           Client f = new Client("farouk","1234");
           f.upload("farouk", "C:/Users/Latitude7480/Desktop/nn.txt");
           
            
    }

}
