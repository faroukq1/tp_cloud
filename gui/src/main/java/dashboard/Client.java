package dashboard;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private final String username;
    private Socket client;

    public Client(String username) {
        this.username = username;
        try {
            this.client = new Socket("localhost", 3020);
            System.out.println("Connexion établie avec le serveur.");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean login() {
        return sendRequest("log," + username);
    }

    public boolean inscription() {
        return sendRequest("insc," + username);
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

    public boolean upload(String filePath) {
        try {
            File originalFile = new File(filePath);
            String fileName = originalFile.getName();
    
            // Step 1: Create ~/Desktop/application directory if not exists
            String userHome = System.getProperty("user.home");
            File desktopDir = new File(userHome, "Desktop");
            File appDir = new File(desktopDir, "application");
            if (!appDir.exists()) {
                appDir.mkdirs(); // Create the "application" directory
            }
    
            // Step 2: Copy the file to ~/Desktop/application
            File destFile = new File(appDir, fileName);
            Files.copy(originalFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    
            // Step 3: Read file data
            byte[] fileData = Files.readAllBytes(originalFile.toPath());
            String fileDataBase64 = java.util.Base64.getEncoder().encodeToString(fileData);
    
            // Step 4: Send to server
            try (
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))
            ) {
                out.println("upload," + username + "," + fileName + "," + fileDataBase64);
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

    public static void main(String[] args) {
        Client client = new Client("farouk");
        client.upload("C:/Users/Latitude7480/Desktop/nn.txt");
        client.close();
    }
}
