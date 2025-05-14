package BDD;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

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

    public static ArrayList<FileModel> fetchAllFiles(String username) {
        ArrayList<FileModel> filesList = new ArrayList<>();

        String query = "SELECT f.id, f.user_id, f.nom_fichier, f.date_upload " +
                       "FROM files f " +
                       "JOIN users u ON f.user_id = u.id " +
                       "WHERE u.username = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int fileId = rs.getInt("id");
                    int userId = rs.getInt("user_id");
                    String nomFichier = rs.getString("nom_fichier");
                    Timestamp dateUpload = rs.getTimestamp("date_upload");

                    // Add each file record to the list
                    filesList.add(new FileModel(fileId, userId, nomFichier, dateUpload));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filesList;
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
    
            boolean isSaved = saveFileToDatabase(username, filename, fileData);
            System.out.println(isSaved ? "File uploaded successfully!" : "Failed uploading file");    
            return isSaved;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    /*public static boolean handleUpload(String username, String filename, byte[] fileData) {
    // Define user directory (change this as needed)
    Path userDir = Paths.get(System.getProperty("user.home"), "Desktop", "data", username);
    File userFolder = userDir.toFile();

    // Create folder if it doesn't exist
    if (!userFolder.exists()) {
        boolean canCreate = userFolder.mkdirs();
        System.out.println(canCreate ? "Folder created successfully." : "Failed to create folder.");
    }

    // Define the full file path
    Path filePath = userDir.resolve(filename);

    try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile(), false)) {
        // Write the file data
        fileOut.write(fileData);
        fileOut.flush();

        // Optionally save file data to the database
        boolean isSaved = saveFileToDatabase(username, filename, fileData);
        System.out.println(isSaved ? "File uploaded successfully!" : "Failed uploading file");

        return isSaved;
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}
    */


    
    public static boolean saveFileToDatabase(String username, String filename, byte[] fileData) throws IOException {
        try (Connection conn = connect()) {
            int userId = -1;
    
            String sql = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt("id");
                } else {
                    System.out.println("User not found: " + username);
                    return false;
                }
            }
    
            // 2. Create project directory for storing the file
            Path projectUserDir = Paths.get("BDD DOCK", "data", username);
            System.out.println("Creating folder at: " + projectUserDir.toAbsolutePath());
            Files.createDirectories(projectUserDir);
            
            // 3. Save the file to the project directory
            Path projectFilePath = projectUserDir.resolve(filename);
            try (FileOutputStream out = new FileOutputStream(projectFilePath.toFile(), false)) {
                out.write(fileData);
            }
    
            String insertSql = "INSERT INTO files (user_id, nom_fichier) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, userId);
                stmt.setString(2, filename);
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Something went wrong: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean deleteFile(String username, String filename) {
    try (Connection conn = connect()) {
        // 1) Récupérer l'ID utilisateur
        String sqlUser = "SELECT id FROM users WHERE username = ?";
        int userId;
        try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Utilisateur non trouvé : " + username);
                return false;
            }
            userId = rs.getInt("id");
        }

        // 2) Supprimer la ligne dans files
        String sqlDel = "DELETE FROM files WHERE user_id = ? AND nom_fichier = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlDel)) {
            ps.setInt(1, userId);
            ps.setString(2, filename);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                System.out.println("Aucun tuple supprimé pour " + filename);
                return false;
            }
        }

        // 3) Supprimer le fichier du disque (desktop data)
        Path userDir = Paths.get(System.getProperty("user.home"), "Desktop", "data", username);
        Path filePath = userDir.resolve(filename);
        boolean deleted = Files.deleteIfExists(filePath);
        System.out.println(deleted 
            ? "Fichier supprimé du disque : " + filePath 
            : "Fichier introuvable sur le disque : " + filePath);

        return true;
    } catch (SQLException | IOException e) {
        e.printStackTrace();
        return false;
    }
}

    
    public static void main (String [] args) {
            testConnection();
    }

}
