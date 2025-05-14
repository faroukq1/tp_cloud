package auth;

import javax.swing.*;
import java.awt.*;

public class AuthGUI extends JFrame {
    public static CardLayout cardLayout = new CardLayout();
    public static JPanel rightPanel;

    public AuthGUI() {
        setTitle("SID CLOUD");
        setSize(950, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left Sidebar
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Welcome", SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginTabBtn = new JButton("Log In");
        JButton registerTabBtn = new JButton("Register");

        loginTabBtn.setMaximumSize(new Dimension(200, 40));
        registerTabBtn.setMaximumSize(new Dimension(200, 40));
        loginTabBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerTabBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Switch panels on button click
        loginTabBtn.addActionListener(e -> cardLayout.show(rightPanel, "login"));
        registerTabBtn.addActionListener(e -> cardLayout.show(rightPanel, "register"));

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(welcomeLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        leftPanel.add(loginTabBtn);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(registerTabBtn);
        leftPanel.add(Box.createVerticalGlue());

        // Right Panel (CardLayout)
        rightPanel = new JPanel(cardLayout);
        rightPanel.add(new LoginPanel(), "login");
        rightPanel.add(new RegisterPanel(), "register");

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        cardLayout.show(rightPanel, "login");

        setVisible(true);
    }
}
