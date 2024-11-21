package lf;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.model.Filters;

public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    private Color primaryColor = new Color(30, 136, 229); // Brighter blue
    private Color backgroundColor = Color.WHITE;
    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 32);
    private Font subtitleFont = new Font("Segoe UI", Font.BOLD, 24);

    public LoginPage() {
        // Basic frame setup
        setTitle("Lost and Found - Rajalakshmi");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // App name
        JLabel appNameLabel = new JLabel("Lost and Found");
        appNameLabel.setFont(titleFont);
        appNameLabel.setForeground(primaryColor);
        appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Welcome text
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(subtitleFont);
        titleLabel.setForeground(new Color(33, 33, 33));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(mainFont);
        subtitleLabel.setForeground(new Color(117, 117, 117));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form container
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(backgroundColor);
        formContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        // Email field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(mainFont);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField = new JTextField(20);
        styleTextField(emailField);

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(mainFont);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);

        // Login button
        loginButton = new JButton("Sign In");
        styleButton(loginButton);
        loginButton.addActionListener(e -> loginUser());

        // Register button
        registerButton = new JButton("Create an account");
        registerButton.setFont(mainFont);
        registerButton.setForeground(primaryColor);
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setFocusPainted(false);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> {
            new RegisterPage();
            dispose();
        });

        // Message label
        messageLabel = new JLabel();
        messageLabel.setFont(mainFont);
        messageLabel.setForeground(Color.RED);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components with proper spacing
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(appNameLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(40));

        // Add form elements
        formContainer.add(emailLabel);
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(emailField);
        formContainer.add(Box.createVerticalStrut(20));
        formContainer.add(passwordLabel);
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(passwordField);
        formContainer.add(Box.createVerticalStrut(30));
        formContainer.add(loginButton);
        formContainer.add(Box.createVerticalStrut(15));
        formContainer.add(messageLabel);
        formContainer.add(Box.createVerticalStrut(15));
        formContainer.add(registerButton);

        mainPanel.add(formContainer);
        mainPanel.add(Box.createVerticalGlue());

        // Add main panel to frame
        add(mainPanel);
        setVisible(true);
    }

    private void styleTextField(JTextField field) {
        field.setFont(mainFont);
        field.setMaximumSize(new Dimension(300, 35));
        field.setPreferredSize(new Dimension(300, 35));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(primaryColor);
        button.setMaximumSize(new Dimension(300, 40));
        button.setPreferredSize(new Dimension(300, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(primaryColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(primaryColor);
            }
        });
    }

    private void loginUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Check if email is valid
        if (!User.isValidDomain(email)) {
            showError("Invalid email domain. Only @rajalaksh.mi.edu.in allowed.");
            return;
        }

        MongoCollection<Document> usersCollection = MongoDBUtil.getDatabase().getCollection("users");

        Document userDoc = usersCollection.find(Filters.eq("email", email)).first();
        if (userDoc == null) {
            showError("User not found.");
            return;
        }

        String storedPassword = userDoc.getString("password");
        if (storedPassword.equals(password)) {
            messageLabel.setForeground(new Color(46, 125, 50));
            messageLabel.setText("Login successful!");
            new DashboardPage(storedPassword);
            dispose();
        } else {
            showError("Incorrect password.");
        }
    }

    private void showError(String message) {
        messageLabel.setForeground(new Color(198, 40, 40));
        messageLabel.setText(message);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}