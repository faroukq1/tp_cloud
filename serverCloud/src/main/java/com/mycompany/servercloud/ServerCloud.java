package com.mycompany.servercloud;
import BDD.BDDManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;


public class ServerCloud {
    
    private static final int port = 3020;
    public static void main(String[] args) {
        
      try (ServerSocket server= new ServerSocket(port)) {
            System.out.println("✅ Serveur en écoute sur le port " + port);
            while (true) {
                Socket client = server.accept();
                new Thread( new Traitement_client(client)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
}

}

 class Traitement_client implements Runnable {
    Socket client;
    public Traitement_client(Socket client){
        this.client=client;
    }

        @Override
       public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true)
        ) {
            String request = in.readLine();
            System.out.println("Requête reçue : " + request);
            
            
            String[] parts = request.split(",");
            String command = parts[0];

            switch (command) {
                case "insc":
                    if (parts.length == 3) {
                        String username = parts[1];
                        String password = parts[2];
                        if (BDDManager.inscription(username, password)) {
                            out.println("incription done");
                        } else {
                            System.out.println(parts[0]);
                            System.out.println(parts[1]);
                            out.println("incription failed");
                        }
                    } else {
                        out.println("BAD_FORMAT");
                    }
                    break;

                case "log":
                    if (parts.length == 3) {
                        String username = parts[1];
                        String password = parts[2];
                        if (BDDManager.login(username, password)) {
                            out.println("welcome : " + username);
                        } else {
                            out.println("ERR");
                        }
                    } else {
                        out.println("BAD_FORMAT");
                    }
                    break;

                case "upload":
                    if (parts.length == 4) {
                        String username = parts[1];
                        String filename = parts[2];
                        String fileDataString = parts[3];

                        byte [] fileData = fileDataString.getBytes();
                        if (BDDManager.handleUpload(username, filename, fileData))
                            out.println("File uploaded successfully");
                        else
                            out.println("File upload failed");
                    } else {
                        out.println("BAD_FORMAT");
                    }
                    break;

                case "del":
                    if (parts.length == 2) {
                        String username = parts[1];
                        if (BDDManager.deleteUser(username))
                            out.println("user " + username + " has been deleted"); 
                        else 
                            out.println("invalid username");
                    break;
                }

                default:
                    out.println("UNKNOWN_COMMAND");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}