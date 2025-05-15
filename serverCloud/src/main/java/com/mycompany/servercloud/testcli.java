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

        // out.println("insc,nour,1234");
        // out.println("del,farouk");
        // out.println("log,farouk,1234");
        out.println("fetch,farouk");
        
        
        // start file uploading test

        // String username = "farouk";
        // String filename = "requete.txt";
        // // write your own path        
        // byte [] fileData = Files.readAllBytes(Paths.get("C:\\Users\\Latitude7480\\Desktop\\requete.txt"));
        // String fileDataString = new String(fileData);
        // out.println("upload,"+username+","+filename+","+fileDataString);
        
        // end file uploading test
        // String response = in.readLine();











        // to read all the files
    //     String response = in.readLine();

    //     // If the response starts with "OK", it means the server is sending files
    //     if (response.startsWith("OK")) {
    //     // Read all file details until the server stops sending data
    //     while ((response = in.readLine()) != null) {
    //             if (!response.isEmpty()) {
    //         // Process each line of file details (id, filename, date)
    //         String[] fileInfo = response.split(",");
    //         int fileId = Integer.parseInt(fileInfo[0]);
    //         String fileName = fileInfo[1];
    //         String fileDate = fileInfo[2];  

    //         // Print or store the file details
    //         System.out.println("File ID: " + fileId);
    //         System.out.println("File Name: " + fileName);
    //         System.out.println("Upload Date: " + fileDate);
    //     }
    // }
// } else {
//     System.out.println("No files found or invalid response from server.");
// }


        socket.close();
}

    
}

