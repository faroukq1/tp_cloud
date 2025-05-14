package com.mycompany.servercloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class testcli {
    
public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 3020);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // out.println("insc,nour,1234");
        // out.println("del,farouk");
        // out.println("log,farouk,1234");

        
        
        // start file uploading test

        String username = "farouk";
        String filename = "dummy.txt";
        // write your own path        
        byte [] fileData = Files.readAllBytes(Paths.get("C:\\Users\\NBX\\Desktop\\dummy.txt"));
        String fileDataString = new String(fileData);
        out.println("upload,"+username+","+filename+","+fileDataString);
        
        // end file uploading test
        String response = in.readLine();
        System.out.println(" Réponse serveur : " + response);

        socket.close();
}

    
}

