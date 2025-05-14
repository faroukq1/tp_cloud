package auth;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RegisterPanel extends JPanel {
    public RegisterPanel() {
        setLayout(null);
        setBackground(Color.DARK_GRAY);

        JLabel title = new JLabel("Register");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(250, 30, 300, 30);

        // Username field
        JLabel usernameLabel = makeLabel("Username:", 100);
        JTextField usernameField = new JTextField();
        usernameField.setBounds(250, 100, 300, 30);

        // Password field
        JLabel passwordLabel = makeLabel("Password:", 150);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(250, 150, 300, 30);

        // Register Button
        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(Color.ORANGE);
        registerBtn.setForeground(Color.BLACK);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        registerBtn.setBounds(250, 200, 300, 35);

        // Add components to panel
        add(title);
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(registerBtn);

        registerBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean valid = handleRegisterRequest(username, password);
            if (valid) {
                AuthGUI.cardLayout.show(AuthGUI.rightPanel, "login");
            }
        });
    }
    private JLabel makeLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setBounds(150, y, 100, 25);
        return label;
    }


    public boolean handleRegisterRequest(String username, String password) {
    try (Socket socket = new Socket("localhost", 3020);
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
    ) {
        // Send the registration request to the server
        String registerMessage = "insc," + username + "," + password;
        out.println(registerMessage);

        // Read and parse the server's response
        String response = in.readLine();
        String[] data = response.split(" ");

        if (data.length != 2) {
            JOptionPane.showMessageDialog(null, response, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean success = data[0].equalsIgnoreCase("OK");
        if (!success) {
            if (data[1].equalsIgnoreCase("username_taken")) {
                JOptionPane.showMessageDialog(null, "Username is already taken", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return false;
            } else if (data[1].equalsIgnoreCase("bad_format")) {
                JOptionPane.showMessageDialog(null, "Invalid request format", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            JOptionPane.showMessageDialog(null, "Registration : " + data[1], "Registration", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }

        JOptionPane.showMessageDialog(null, "Registration successful!", "Registration", JOptionPane.INFORMATION_MESSAGE);
        return true;
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}

}
