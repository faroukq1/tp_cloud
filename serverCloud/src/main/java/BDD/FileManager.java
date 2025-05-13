/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BDD;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileManager {
    private static final String STORAGE_ROOT = "C:/Users/Latitude7480/Desktop/data" ;// path ou on stock tous dans le docker 
    
    
    public static boolean createUserDocier(String username) {
        Path userDir = Paths.get(STORAGE_ROOT, username);
        try {
            if (Files.notExists(userDir)) {
                Files.createDirectories(userDir);
                System.out.println("Dossier cloud créé pour '" + username + "' : " + userDir);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Impossible de créer le dossier pour '" + username + "' : " + e.getMessage());
            return false;
        }
        
    }
}
