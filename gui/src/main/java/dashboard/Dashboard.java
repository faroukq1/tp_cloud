package dashboard;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import model.FileRecord;
import model.FileTableModel;
import states.AppStates;

public class Dashboard extends JFrame {

    private FileTableModel tableModel;    // *** CHANGED: moved to field
    private JTable fileTable;             // *** CHANGED: moved to field

    public Dashboard() {
        setTitle("Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on the screen
        setLayout(new BorderLayout());

        // --- Top Panel avec titre à gauche et boutons à droite ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre "SID CLOUD"
        JLabel titleLabel = new JLabel("SID CLOUD", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // *** CHANGED: Panel pour Upload et Delete ***
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton uploadButton = new JButton("Upload File");
        JButton deleteButton = new JButton("Delete File");   // *** ADDED ***
        btnPanel.add(uploadButton);
        btnPanel.add(deleteButton);                           // *** ADDED ***
        topPanel.add(btnPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // --- Table des fichiers ---
        tableModel = new FileTableModel(AppStates.getCurrentUserName());  // *** CHANGED: use field
        fileTable  = new JTable(tableModel);                             // *** CHANGED: use field
        JScrollPane tableScrollPane = new JScrollPane(fileTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // --- Action du bouton Upload ---
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                String username = AppStates.getCurrentUserName();
                Client client = new Client(username);
                if (client.upload(filePath)) {
                    JOptionPane.showMessageDialog(this, "File uploaded successfully!");
                    tableModel.refresh(username);                          // *** CHANGED: refresh after upload
                } else {
                    JOptionPane.showMessageDialog(this, "File upload failed.");
                }
                client.close();
            }
        });

        // --- Action du bouton Delete File ---
        deleteButton.addActionListener(e -> {
            int viewRow = fileTable.getSelectedRow();
            if (viewRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a file to delete.");
                return;
            }
            int modelRow = fileTable.convertRowIndexToModel(viewRow);      // take sorting into account
            FileRecord record = tableModel.getFileRecord(modelRow);        // *** CHANGED: new getter in model

            int confirm = JOptionPane.showConfirmDialog(this,
                "supprimer \"" + record.getFileName() + "\"?",
                "Confirmer",
                JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            Client client = new Client(AppStates.getCurrentUserName());
            boolean ok = client.supprimerFichier(
                AppStates.getCurrentUserName(),
                record.getFileName()                                    // pass filename
            );
            client.close();

            if (ok) {
                JOptionPane.showMessageDialog(this, "supprimer avec success.");
                tableModel.refresh(AppStates.getCurrentUserName());    // *** CHANGED: refresh after delete
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dashboard::new);
    }
}
