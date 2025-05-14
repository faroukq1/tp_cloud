package dashboard;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;

import model.FileRecord;
import model.FileTableModel;
import states.AppStates;

public class Dashboard extends JFrame {

    private FileTableModel tableModel;
    private JTable fileTable;
    private JLabel timeLabel; // Time display label

    public Dashboard() {
        setTitle("Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Top Panel ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("SID CLOUD", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton uploadButton = new JButton("Upload File");
        JButton deleteButton = new JButton("Delete File");
        btnPanel.add(uploadButton);
        btnPanel.add(deleteButton);
        topPanel.add(btnPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // --- File Table ---
        tableModel = new FileTableModel(AppStates.getCurrentUserName());
        fileTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(fileTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // --- Bottom Panel with Sync Button and Time Label ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        timeLabel = new JLabel("Last Sync: Never");
        JButton syncButton = new JButton("Sync");

        bottomPanel.add(timeLabel, BorderLayout.WEST);
        bottomPanel.add(syncButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- Button Actions ---
        uploadButton.addActionListener(e -> handleUpload());
        deleteButton.addActionListener(e -> handleDelete());
        syncButton.addActionListener(e -> handleSync());

        // Initial sync on startup
        // handleSync();

        // Start background thread for automatic sync every 60 seconds
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60000); // 60 seconds
                    SwingUtilities.invokeLater(this::handleSync);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();

        setVisible(true);
    }

    private void handleUpload() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String username = AppStates.getCurrentUserName();
            Client client = new Client(username);
            if (client.upload(selectedFile.getAbsolutePath())) {
                JOptionPane.showMessageDialog(this, "File uploaded successfully!");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Upload failed.");
            }
            client.close();
        }
    }

    private void handleDelete() {
        int viewRow = fileTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a file to delete.");
            return;
        }
        int modelRow = fileTable.convertRowIndexToModel(viewRow);
        FileRecord record = tableModel.getFileRecord(modelRow);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete \"" + record.getFileName() + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Client client = new Client(AppStates.getCurrentUserName());
        boolean success = client.supprimerFichier(AppStates.getCurrentUserName(), record.getFileName());
        client.close();

        if (success) {
            JOptionPane.showMessageDialog(this, "File deleted.");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Delete failed.");
        }
    }

    private void handleSync() {
        refreshTable();
        ArrayList<FileRecord> currentUserList = FileTableModel.fileList;
        ArrayList<Pair> localFileName = new ArrayList<>();
        ArrayList<Intersection> intersectionLocalDatabase = new ArrayList<>();
        String folderPath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "application";

        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        // Collect local files
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    String lastModified = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")
                        .format(new java.util.Date(file.lastModified()));
                    String filePath = file.getAbsolutePath();
                    localFileName.add(new Pair(fileName, lastModified, filePath));
                }
            }
        }

        // Find intersection between local and database files
        for (FileRecord databaseItem : currentUserList) {
            for (Pair localItem : localFileName) {
                if (databaseItem.getFileName().equals(localItem.fileName)) {
                    intersectionLocalDatabase.add(new Intersection(
                        Integer.toString(databaseItem.getFileID()),
                        databaseItem.getFileName(),
                        databaseItem.getUploadDate(),
                        localItem.fileDate,
                        localItem.filePath
                    ));
                }
            }
        }

        // Process each intersecting file
        for (Intersection item : intersectionLocalDatabase) {
            try {
                boolean changeDatabaseFile = isDatabaseOlder(item.getDatabaseDate(), item.getLocalDate());

                if (changeDatabaseFile) {
                    System.out.println(item.getFilename() + " needs update");
                    System.out.println("Local file path: " + item.getFilePath());

                    // DELETE operation
                    try (
                        Socket deleteSocket = new Socket("localhost", 3020);
                        PrintWriter deleteOut = new PrintWriter(deleteSocket.getOutputStream(), true);
                        BufferedReader deleteIn = new BufferedReader(new InputStreamReader(deleteSocket.getInputStream()))
                    ) {
                        String deleteCommand = "delete," + AppStates.getCurrentUserName() + "," + item.getFilename();
                        deleteOut.println(deleteCommand);
                        String deleteResponse = deleteIn.readLine();

                        if ("OK".equals(deleteResponse)) {
                            System.out.println("Server deleted old file: " + item.getFilename());

                            // UPLOAD operation
                            try (
                                Socket uploadSocket = new Socket("localhost", 3020);
                                PrintWriter uploadOut = new PrintWriter(uploadSocket.getOutputStream(), true);
                                BufferedReader uploadIn = new BufferedReader(new InputStreamReader(uploadSocket.getInputStream()))
                            ) {
                                Path path = Paths.get(item.getFilePath());
                                byte[] fileBytes = Files.readAllBytes(path);
                                String base64File = Base64.getEncoder().encodeToString(fileBytes);

                                String uploadCommand = "upload," + AppStates.getCurrentUserName() + "," + item.getFilename() + "," + base64File;
                                uploadOut.println(uploadCommand);

                                String uploadResponse = uploadIn.readLine();
                                if ("OK".equals(uploadResponse)) {
                                    System.out.println("File reuploaded successfully: " + item.getFilename());
                                } else {
                                    System.out.println("Upload failed for " + item.getFilename() + ": " + uploadResponse);
                                }
                            }
                        } else {
                            System.out.println("Delete failed for " + item.getFilename() + ": " + deleteResponse);
                        }
                    }
                    refreshTable();
                } else {
                    System.out.println("File up to date: " + item.getFilename());
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error synchronizing file: " + item.getFilename());
            }
        }

        updateSyncTime();
        JOptionPane.showMessageDialog(this, "Synchronization complete!");
    }

    private void refreshTable() {
        tableModel.refresh(AppStates.getCurrentUserName());
    }

    private void updateSyncTime() {
        SwingUtilities.invokeLater(() -> {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            timeLabel.setText("Last Sync: " + now);
        });
    }

    public static boolean isDatabaseOlder(String databaseDate, String localDate) {
        try {
            if (databaseDate.endsWith(".0")) {
                databaseDate = databaseDate.substring(0, databaseDate.length() - 2);
            }

            DateTimeFormatter databaseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter localFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

            LocalDateTime dbTime = LocalDateTime.parse(databaseDate, databaseFormatter);
            LocalDateTime localTime = LocalDateTime.parse(localDate, localFormatter);

            return dbTime.isBefore(localTime);
        } catch (Exception e) {
            System.out.println("Error parsing dates: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dashboard::new);
    }
}

// Helper class for local files
class Pair {
    String fileName;
    String fileDate;
    String filePath;
    public Pair(String fileName, String fileDate, String filePath) {
        this.fileName = fileName;
        this.fileDate = fileDate;
        this.filePath = filePath;
    }
}

// Class representing intersection entries
class Intersection {
    private String fileId;
    private String filename;
    private String databaseDate;
    private String localDate;
    private String filePath;

    public Intersection(String fileId, String filename, String databaseDate, String localDate, String filePath) {
        this.fileId = fileId;
        this.filename = filename;
        this.databaseDate = databaseDate;
        this.localDate = localDate;
        this.filePath = filePath;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFilename() {
        return filename;
    }

    public String getDatabaseDate() {
        return databaseDate;
    }

    public String getLocalDate() {
        return localDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setLocalDate(String date) {
        this.localDate = date;
    }

    @Override
    public String toString() {
        return "Intersection{" +
            "fileId='" + fileId + '\'' +
            ", filename='" + filename + '\'' +
            ", databaseDate='" + databaseDate + '\'' +
            ", localDate='" + localDate + '\'' +
            ", filePath='" + filePath + '\'' +
            '}';
    }
}
