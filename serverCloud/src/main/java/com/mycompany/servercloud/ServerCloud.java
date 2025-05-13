/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.servercloud;
import BDD.BDDManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


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



 class Traitement_client implements Runnable{
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
            if (parts.length == 3 && parts[0].equals("insc")) {
                String username = parts[1];
                String password = parts[2];

                if (BDDManager.inscription(username, password)) {
                    out.println("OK");
                } else {
                    out.println("ERR");
                }
            } else {
                out.println("BAD_FORMAT");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}