/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BDD;
import java.sql.*;
/**
 *
 * @author Latitude7480
 */
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
            if(t==0)
                return false ; 
            return FileManager.createUserDocier(username); // creation de docier coté cloud 
        } catch (SQLException e) {
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

    
    

}
