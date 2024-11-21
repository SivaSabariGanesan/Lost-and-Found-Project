package lf;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.*;
import com.mongodb.client.*;
import org.bson.Document;

public class DashboardPage extends JFrame {
    // Constants
    private static final Color PRIMARY_COLOR = new Color(30, 136, 229);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final int BORDER_RADIUS = 10;
    private static final String IMAGE_STORAGE_PATH = "images/";
    private static final Dimension FIELD_SIZE = new Dimension(300, 35);
    private static final Dimension BUTTON_SIZE = new Dimension(150, 40);

    // UI Components
    private JTextField rollNoField, nameField, contactField, locationField, itemNameField;
    private JTextArea itemDescriptionField;
    private JComboBox<String> statusComboBox;
    private JButton uploadButton, postButton, myPostsButton, allPostsButton;
    private JLabel photoLabel;
    private JPanel postsPanel;
    private JScrollPane postsScrollPane;
    
    // State
    private File selectedImage;
    private String loggedInUserRollNo;
    private boolean showingMyPosts = true;

    public DashboardPage(String loggedInUserRollNo) {
        this.loggedInUserRollNo = loggedInUserRollNo;
        createImageDirectory();
        setupFrame();
        initializeComponents();
        loadPosts(true);
        setVisible(true);
    }

    private void createImageDirectory() {
        File directory = new File(IMAGE_STORAGE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void setupFrame() {
        setTitle("Lost and Found Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);

        JPanel mainContainer = new JPanel(new BorderLayout(20, 20));
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainContainer.setBackground(BACKGROUND_COLOR);
        setContentPane(mainContainer);

        // Add window listener for responsiveness
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                updateLayoutForScreenSize();
            }
        });
    }

    private void initializeComponents() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setLeftComponent(createFormPanel());
        splitPane.setRightComponent(createPostsPanel());
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER_RADIUS),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("Add New Item");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Form fields
        rollNoField = createStyledTextField();
        rollNoField.setText(loggedInUserRollNo);
        rollNoField.setEditable(false);
        nameField = createStyledTextField();
        contactField = createStyledTextField();
        locationField = createStyledTextField();
        itemNameField = createStyledTextField();
        
        // Description
        itemDescriptionField = new JTextArea(4, 20);
        itemDescriptionField.setFont(BODY_FONT);
        itemDescriptionField.setLineWrap(true);
        itemDescriptionField.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(itemDescriptionField);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // Status
        statusComboBox = new JComboBox<>(new String[]{"Lost", "Found"});
        statusComboBox.setFont(BODY_FONT);
        statusComboBox.setMaximumSize(FIELD_SIZE);

        // Image upload
        uploadButton = createStyledButton("Upload Image", false);
        uploadButton.addActionListener(e -> uploadImage());
        photoLabel = new JLabel("No Image Selected");
        photoLabel.setPreferredSize(new Dimension(100, 100));
        photoLabel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // Post button
        postButton = createStyledButton("Post Item", true);
        postButton.addActionListener(e -> postItem());

        // Add components
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createFormField("Roll No:", rollNoField));
        formPanel.add(createFormField("Name:", nameField));
        formPanel.add(createFormField("Contact:", contactField));
        formPanel.add(createFormField("Location:", locationField));
        formPanel.add(createFormField("Item Name:", itemNameField));
        formPanel.add(createFormField("Description:", scrollPane));
        formPanel.add(createFormField("Status:", statusComboBox));
        
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imagePanel.setBackground(CARD_COLOR);
        imagePanel.add(uploadButton);
        imagePanel.add(photoLabel);
        imagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(imagePanel);
        
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(postButton);

        return formPanel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(BODY_FONT);
        field.setMaximumSize(FIELD_SIZE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(BODY_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(!isPrimary);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(BUTTON_SIZE);
        button.setPreferredSize(BUTTON_SIZE);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (isPrimary) {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(TEXT_PRIMARY);
            button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        }

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(isPrimary ? PRIMARY_COLOR.darker() : new Color(245, 245, 245));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(isPrimary ? PRIMARY_COLOR : Color.WHITE);
            }
        });

        return button;
    }

    private JPanel createFormField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel(labelText);
        label.setFont(BODY_FONT);
        label.setForeground(TEXT_PRIMARY);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JPanel createPostsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER_RADIUS),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Posts");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(CARD_COLOR);
        
        myPostsButton = createStyledButton("My Posts", false);
        allPostsButton = createStyledButton("All Posts", false);
        
        myPostsButton.addActionListener(e -> {
            showingMyPosts = true;
            loadPosts(true);
        });
        allPostsButton.addActionListener(e -> {
            showingMyPosts = false;
            loadPosts(false);
        });
        
        buttonsPanel.add(myPostsButton);
        buttonsPanel.add(allPostsButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);

        // Posts container
        postsPanel = new JPanel();
        postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));
        postsPanel.setBackground(BACKGROUND_COLOR);
        
        postsScrollPane = new JScrollPane(postsPanel);
        postsScrollPane.setBorder(null);
        postsScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(postsScrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Image");
        
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return f.isDirectory() || name.endsWith(".jpg") || 
                       name.endsWith(".jpeg") || name.endsWith(".png") || 
                       name.endsWith(".gif");
            }
            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImage = fileChooser.getSelectedFile();
            ImageIcon originalIcon = new ImageIcon(selectedImage.getAbsolutePath());
            Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            photoLabel.setIcon(new ImageIcon(scaledImage));
            photoLabel.setText("");
        }
    }

    private void postItem() {
        if (validateForm()) {
            String imageName = saveImage();
            if (savePost(imageName)) {
                clearForm();
                loadPosts(showingMyPosts);  // Reload posts after successful save
                JOptionPane.showMessageDialog(this,
                    "Item posted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty() ||
            contactField.getText().trim().isEmpty() ||
            locationField.getText().trim().isEmpty() ||
            itemNameField.getText().trim().isEmpty() ||
            itemDescriptionField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private String saveImage() {
        String imageName = "";
        if (selectedImage != null) {
            try {
                String timestamp = String.valueOf(System.currentTimeMillis());
                String extension = selectedImage.getName().substring(
                    selectedImage.getName().lastIndexOf("."));
                imageName = timestamp + extension;
                
                Path source = selectedImage.toPath();
                Path destination = Paths.get(IMAGE_STORAGE_PATH + imageName);
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error saving image: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        return imageName;
    }

    private boolean savePost(String imageName) {
        Document post = new Document()
            .append("rollNo", rollNoField.getText())
            .append("name", nameField.getText())
            .append("contact", contactField.getText())
            .append("location", locationField.getText())
            .append("itemName", itemNameField.getText())
            .append("itemDescription", itemDescriptionField.getText())
            .append("status", statusComboBox.getSelectedItem())
            .append("imageName", imageName)
            .append("timestamp", System.currentTimeMillis());

        try {
            MongoCollection<Document> collection = MongoDBUtil.getDatabase()
                .getCollection("lost_and_found");
            collection.insertOne(post);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error posting item: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void loadPosts(boolean filterByUser) {
        postsPanel.removeAll();

        MongoCollection<Document> collection = MongoDBUtil.getDatabase()
            .getCollection("lost_and_found");
        FindIterable<Document> iterable;

        if (filterByUser) {
            iterable = collection.find(new Document("rollNo", loggedInUserRollNo));
        } else {
            iterable = collection.find();
        }

        iterable.sort(new Document("timestamp", -1));

        try (MongoCursor<Document> cursor = iterable.iterator()) {
            while (cursor.hasNext()) {
                Document post = cursor.next();
                postsPanel.add(createPostCard(post));
                postsPanel.add(Box.createVerticalStrut(10));
            }
        }

        postsPanel.revalidate();
        postsPanel.repaint();
    }

    private JPanel createPostCard(Document post) {
        JPanel cardPanel = new JPanel(new BorderLayout(15, 15));
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER_RADIUS),
            new EmptyBorder(15, 15, 15, 15)
        ));
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        // Image
        JPanel imagePanel = createPostImagePanel(post.getString("imageName"));
        cardPanel.add(imagePanel, BorderLayout.WEST);

        // Details
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);

        // Header (Item name and status)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        
        JLabel itemNameLabel = new JLabel(post.getString("itemName"));
        itemNameLabel.setFont(SUBTITLE_FONT);
        headerPanel.add(itemNameLabel, BorderLayout.WEST);
        
        JLabel statusLabel = new JLabel(post.getString("status"));
        statusLabel.setFont(BODY_FONT);
        statusLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        detailsPanel.add(headerPanel, gbc);

        // Details rows
        gbc.gridy++;
        addDetailRow(detailsPanel, "Posted by:", post.getString("name"), gbc);
        gbc.gridy++;
        addDetailRow(detailsPanel, "Location:", post.getString("location"), gbc);
        gbc.gridy++;
        addDetailRow(detailsPanel, "Contact:", post.getString("contact"), gbc);
        gbc.gridy++;
        addDetailRow(detailsPanel, "Description:", post.getString("itemDescription"), gbc);

        cardPanel.add(detailsPanel, BorderLayout.CENTER);
        return cardPanel;
    }

    private JPanel createPostImagePanel(String imageName) {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(CARD_COLOR);
        imagePanel.setPreferredSize(new Dimension(150, 150));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        if (imageName != null && !imageName.isEmpty()) {
            String imagePath = IMAGE_STORAGE_PATH + imageName;
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image scaledImage = originalIcon.getImage()
                .getScaledInstance(130, 130, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            imageLabel.setText("No Image");
            imageLabel.setFont(BODY_FONT);
        }
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        return imagePanel;
    }

    private void addDetailRow(JPanel panel, String label, String value, GridBagConstraints gbc) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(BODY_FONT);
        labelComponent.setForeground(TEXT_SECONDARY);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(BODY_FONT);
        valueComponent.setForeground(TEXT_PRIMARY);
        
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        rowPanel.setBackground(CARD_COLOR);
        rowPanel.add(labelComponent);
        rowPanel.add(valueComponent);
        
        panel.add(rowPanel, gbc);
    }

    private void clearForm() {
        nameField.setText("");
        contactField.setText("");
        locationField.setText("");
        itemNameField.setText("");
        itemDescriptionField.setText("");
        selectedImage = null;
        photoLabel.setIcon(null);
        photoLabel.setText("No Image Selected");
        statusComboBox.setSelectedIndex(0);
    }

    private void updateLayoutForScreenSize() {
        int width = getWidth();
        int height = getHeight();
        
        if (width < 800 || height < 600) {
            // Switch to a vertical layout for smaller screens
            getContentPane().removeAll();
            setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
            add(createFormPanel());
            add(Box.createVerticalStrut(20));
            add(createPostsPanel());
        } else {
            // Switch back to the split pane layout for larger screens
            getContentPane().removeAll();
            setLayout(new BorderLayout());
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setDividerLocation(400);
            splitPane.setDividerSize(1);
            splitPane.setBorder(null);
            splitPane.setLeftComponent(createFormPanel());
            splitPane.setRightComponent(createPostsPanel());
            add(splitPane, BorderLayout.CENTER);
        }
        
        revalidate();
        repaint();
    }

    private static class RoundedBorder extends AbstractBorder {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new DashboardPage("12345");
        });
    }
}