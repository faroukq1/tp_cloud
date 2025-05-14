package com.mycompany.servercloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class testcli {
    
public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 3020);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // out.println("insc,farouk,1234");
        // out.println("del,farouk");
        out.println("log,farouk,1234");
        String response = in.readLine();
        System.out.println(" Réponse serveur : " + response);

        socket.close();
}

    
}
