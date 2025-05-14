package model;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;

public class DashboardTable extends JPanel {

    private JTable table;
    private FileTableModel model;

    // Constructor takes the username to fetch files for that user
    public DashboardTable(String username) {
        setLayout(new BorderLayout());

        // Initialize the table model and fetch the file data for the given user
        model = new FileTableModel(username);
        table = new JTable(model);

        // Make the table scrollable
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Optionally, you can adjust the column widths for better visibility
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // File ID
        columnModel.getColumn(1).setPreferredWidth(300); // File Name
        columnModel.getColumn(2).setPreferredWidth(200); // Upload Date
    }

    // Method to update the table with new data (if necessary)
    public void updateTable(ArrayList<FileRecord> newFileList) {
        model.setFileList(newFileList);  // Update the file list in the table model
        model.fireTableDataChanged();    // Notify the table that data has changed
    }
}
