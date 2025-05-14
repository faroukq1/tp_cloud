package com.mycompany.servercloud;

import BDD.BDDManager;
import BDD.FileModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerCloud {

    private static final int port = 3020;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Serveur en écoute sur le port " + port);
            while (true) {
                Socket client = server.accept();
                new Thread(new Traitement_client(client)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Traitement_client implements Runnable {
    Socket client;

    public Traitement_client(Socket client) {
        this.client = client;
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
                            out.println("OK registration_success");
                        } else {
                            System.out.println(parts[0]);
                            System.out.println(parts[1]);
                            out.println("ERROR username_taken");
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
                            out.println("OK " + username);
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
                        String fileDataBase64 = parts[3];

                        byte[] fileData = java.util.Base64.getDecoder().decode(fileDataBase64);
                        if (BDDManager.handleUpload(username, filename, fileData)) {
                            out.println("OK");
                        } else {
                            out.println("File upload failed");
                        }
                    } else {
                        out.println("BAD_FORMAT");
                    }
                    break;

                case "del":
                    if (parts.length == 2) {
                        String username = parts[1];
                        if (BDDManager.deleteUser(username)) {
                            out.println("user " + username + " has been deleted");
                        } else {
                            out.println("invalid username");
                        }
                    }
                    break;

                    case "fetch":
                        if (parts.length == 2) {
                            String username = parts[1];
                            ArrayList<FileModel> files = BDDManager.fetchAllFiles(username);
                        
                        // Corrected the condition to check if the list is not empty
                        if (files != null && !files.isEmpty()) {
                            out.println("OK " + files.size());
                
                            // Send file details
                            for (FileModel file : files) {
                                String fileDetails = file.getId() + "," + file.getNomFichier() + "," + file.getDateUpload();
                                System.out.println("1 : " + fileDetails);
                                out.println(fileDetails);
                            }
                        } else {
                            out.println("NO_FILES_FOUND");
                        }
                    }
                    break;
                
                default:
                    out.println("UNKNOWN_COMMAND");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
