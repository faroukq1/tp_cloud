package auth;
import javax.swing.*;

import dashboard.Dashboard;
import states.AppStates;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginPanel extends JPanel {
    public LoginPanel() {
        setLayout(null);
        setBackground(Color.DARK_GRAY);

        JLabel title = new JLabel("Login");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(260, 50, 300, 30);

        // Username and Password fields
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        usernameField.setBounds(250, 110, 300, 30);
        passwordField.setBounds(250, 160, 300, 30);

        JLabel usernameLabel = makeLabel("Username:", 110);
        JLabel passwordLabel = makeLabel("Password:", 160);

        // Login button
        JButton loginBtn = new JButton("Log In");
        loginBtn.setBackground(Color.ORANGE);
        loginBtn.setForeground(Color.BLACK);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setBounds(250, 210, 300, 35);


        // Add components
        add(title);
        add(usernameField);
        add(passwordField);
        add(usernameLabel);
        add(passwordLabel);
        add(loginBtn);

      loginBtn.addActionListener(e -> {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean valid = handleLoginIn(username, password);
        if (valid) {
            System.out.println("the user name is : " + AppStates.getCurrentUserName());
            // 🟢 Close the AuthGUI window
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            // 🟢 Open the Dashboard
            new Dashboard();
    } else {
        System.out.println("Wrong");
    }
});

    }

    private JLabel makeLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setBounds(150, y, 100, 25);
        return label;
    }

    public boolean handleLoginIn(String username, String password) {
        try (Socket socket = new Socket("localhost", 3020);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // Send login request to the server
            String loginMessage = "log," + username + "," + password;
            out.println(loginMessage);
        
            // Read the server response
            String response = in.readLine();
        
            String[] data = response.split(" "); 
        
            if (data.length != 2) {
                JOptionPane.showMessageDialog(null, response, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        
            boolean valid = data[0].equalsIgnoreCase("OK");
            if (!valid) {
                // If login fails, show the wrong credentials message
                JOptionPane.showMessageDialog(null, "Wrong username or password", "Login Status", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // If the response is valid, return true
            AppStates.setCurrentUser(data[1]);
            AppStates.isLogIn(true);
            return valid;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Something went wrong: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    
}
