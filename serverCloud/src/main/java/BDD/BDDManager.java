package BDD;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class BDDManager {
    private static final String URL = "jdbc:mysql://localhost:3307/cloud_drive";
    private static final String USER = "user";
    private static final String PASSWORD = "pass";
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public static void testConnection() {
        try (Connection conn = connect()) {
            System.out.println("Connexion à la BDD réussie !");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
    }

    
    public static boolean inscription(String username, String password) {
        try (Connection conn = connect()) {
            String sql = "INSERT INTO users (username, mdp) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
           int t = stmt.executeUpdate();
            if(t == 0)
                return false;
             
            return FileManager.createUserDocier(username);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur inscription : " + e.getMessage());
            return false;
        }
    }


    public static boolean login (String username, String mdp) {

            try(Connection conn = connect()){
            String sql = "SELECT * FROM users WHERE username = ? AND mdp = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, mdp);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); 
    }catch (SQLException e) {
            System.err.println(" Erreur login : " + e.getMessage());
            return false;
        }


    }
    public static boolean deleteUser (String username) {
        try (Connection conn = connect()) {
            String sql = "DELETE FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public static boolean handleUpload(String username, String filename, byte[] fileData) {
        // Define user directory
        Path userDir = Paths.get(System.getProperty("user.home"), "Desktop", "data", username);
        File userFolder = userDir.toFile();
    
        if (!userFolder.exists()) {
            boolean canCreate = userFolder.mkdirs();
            System.out.println(canCreate ? "Folder not found, creating folder..." : "Failed to create user folder.");
        }
            Path filePath = userDir.resolve(filename);
    
        try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile(), false)) {  // Use filePath.toFile() to get File
            // Write the file data
            fileOut.write(fileData);
            fileOut.flush();
    
            boolean isSaved = saveFileToDatabase(username, filename, fileData.length);
            System.out.println(isSaved ? "File uploaded successfully!" : "Failed uploading file");    
            return isSaved;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    

    public static boolean saveFileToDatabase (String username, String filename, long fileData) {
        return true;
    }
    public static void main (String [] args) {
            testConnection();
    }

}
