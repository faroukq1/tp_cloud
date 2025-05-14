package dashboard;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import model.FileRecord;
import model.FileTableModel;
import states.AppStates;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on the screen

        setLayout(new BorderLayout());

        // Top Panel (with Title on the Left and Upload Button on the Right)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout()); // BorderLayout to position title and button on opposite sides
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around the top panel

        // Left: Title label "SID CLOUD"
        JLabel titleLabel = new JLabel("SID CLOUD", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Right: Upload Button
        JButton uploadButton = new JButton("Upload File");

        // Adding components to the topPanel
        topPanel.add(titleLabel, BorderLayout.WEST); // Title on the left
        topPanel.add(uploadButton, BorderLayout.EAST); // Upload button on the right

        // Sample data for the table (just for initialization)
        ArrayList<FileRecord> fileList = new ArrayList<>();
        fileList.add(new FileRecord(5, "dkjshfodisj.txt", "2025-05-14 14:59:49.0"));
        fileList.add(new FileRecord(6, "sample.txt", "2025-05-14 15:00:00.0"));
        fileList.add(new FileRecord(7, "example.txt", "2025-05-14 15:10:00.0"));

        // Create and set up the JTable with sample data
        FileTableModel tableModel = new FileTableModel(AppStates.getCurrentUserName());
        JTable fileTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(fileTable);

        // Add Components to the frame
        add(topPanel, BorderLayout.NORTH); // Top panel with Title and Upload button
        add(tableScrollPane, BorderLayout.CENTER); // Table in the main content area

        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                String username = AppStates.getCurrentUserName();
                Client client = new Client(username);
                if (client.upload(filePath)) {
                    JOptionPane.showMessageDialog(null, "File uploaded successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "File upload failed.");
                }
                client.close();
            }
        });

        // Set visibility of the frame
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dashboard::new); // Run the Dashboard application
    }
}



