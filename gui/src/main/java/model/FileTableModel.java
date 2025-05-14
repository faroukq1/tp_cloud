package model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.io.*;
import java.net.Socket;

public class FileTableModel extends AbstractTableModel {

    private String[] columnNames = {"File ID", "File Name", "Upload Date"};
    public static ArrayList<FileRecord> fileList;

    // Constructor to initialize the model and fetch files from the server
    public FileTableModel(String username) {
        fileList = new ArrayList<>();
        fetchFilesFromServer(username);  // Fetch files for the specified user
    }

    // Method to fetch files from the server
    private void fetchFilesFromServer(String username) {
        String serverAddress = "localhost";  // Server address
        int serverPort = 3020;              // Server port

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send fetch request to the server
            out.println("fetch," + username);

            // Read server response
            String response = in.readLine();

            if (response != null && response.startsWith("OK")) {
                // Read each file detail sent by the server
                while ((response = in.readLine()) != null) {
                    if (!response.isEmpty()) {
                        // Process each line of file details (file ID, filename, upload date)
                        String[] fileInfo = response.split(",");
                        int fileId = Integer.parseInt(fileInfo[0]);
                        String fileName = fileInfo[1];
                        String fileDate = fileInfo[2];

                        // Create a new FileRecord and add it to the list
                        FileRecord fileRecord = new FileRecord(fileId, fileName, fileDate);
                        fileList.add(fileRecord);
                    }
                }
                fireTableDataChanged();  // Notify the table that the data has changed
            } else {
                System.out.println("No files found or invalid response from server.");
            }
        } catch (IOException e) {
            // Handle any network-related exceptions
            System.err.println("Error communicating with server: " + e.getMessage());
        }
    }

    // Returns the number of rows (file records) in the table
    @Override
    public int getRowCount() {
        return fileList.size();
    }
    
    // in FileTableModel
public FileRecord getFileRecord(int rowIndex) {
    return fileList.get(rowIndex);
}


    // Returns the number of columns in the table (3 columns)
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    // Returns the value at the specified row and column
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FileRecord fileRecord = fileList.get(rowIndex);  // Get the file record at the given row
        switch (columnIndex) {
            case 0: // File ID
                return fileRecord.getFileID();
            case 1: // File Name
                return fileRecord.getFileName();
            case 2: // Upload Date
                return fileRecord.getUploadDate();
            default:
                return null;
        }
    }

    // Returns the column name based on the column index
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // Set a new list of file records and notify the table that data has changed
    public void setFileList(ArrayList<FileRecord> newFileList) {
        fileList = newFileList;
        fireTableDataChanged();  // Notify the table to refresh
    }
    
    
    public void refresh(String username) {
    fileList.clear();                   // Clear the current list
    fetchFilesFromServer(username);     // Fetch the updated list from the server
    fireTableDataChanged();             // Notify the table to refresh
}

}
