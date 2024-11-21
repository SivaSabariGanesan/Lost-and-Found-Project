package lf;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class PostViewPage {
    
    private JPanel postsPanel; // This should be the main container for displaying posts
    
    // Constructor to initialize the posts panel
    public PostViewPage() {
        postsPanel = new JPanel();
        postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS)); // Stack the posts vertically
        JScrollPane scrollPane = new JScrollPane(postsPanel); // Add scrolling to the posts panel
        JFrame frame = new JFrame("Lost and Found Posts");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400); // Adjust the size as needed
        frame.add(scrollPane);
        frame.setVisible(true);
    }

    // Method to load posts from the MongoDB collection
    public void loadPosts() {
        MongoCollection<Document> collection = MongoDBUtil.getDatabase().getCollection("lost_and_found");

        for (Document post : collection.find()) {
            String rollNo = post.getString("rollNo");
            String name = post.getString("name");
            String contact = post.getString("contact");
            String location = post.getString("location");
            String itemName = post.getString("itemName");
            String itemDescription = post.getString("itemDescription");
            String status = post.getString("status");
            String photoPath = post.getString("photoPath");

            // Create a panel for each post
            JPanel postPanel = new JPanel();
            postPanel.setLayout(new BorderLayout());
            postPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            // Add the image to the top of the post panel
            if (photoPath != null && !photoPath.isEmpty()) {
                File imageFile = new File(photoPath);
                if (imageFile.exists()) {
                    ImageIcon imageIcon = new ImageIcon(photoPath);
                    Image img = imageIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(img);
                    JLabel imageLabel = new JLabel(imageIcon);
                    postPanel.add(imageLabel, BorderLayout.NORTH);
                }
            }

            // Add text details to the panel
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            StringBuilder postDetails = new StringBuilder();
            postDetails.append("Item Name: ").append(itemName).append("\n")
                    .append("Item Description: ").append(itemDescription).append("\n")
                    .append("Roll No: ").append(rollNo).append("\n")
                    .append("Name: ").append(name).append("\n")
                    .append("Contact: ").append(contact).append("\n")
                    .append("Location: ").append(location).append("\n")
                    .append("Status: ").append(status).append("\n");

            JTextArea postTextArea = new JTextArea(postDetails.toString());
            postTextArea.setEditable(false);
            postTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
            postTextArea.setBackground(Color.WHITE);
            postTextArea.setWrapStyleWord(true);
            postTextArea.setLineWrap(true);

            textPanel.add(postTextArea);
            postPanel.add(textPanel, BorderLayout.CENTER);

            // Create a "View" button for each post
            JButton viewButton = new JButton("View");
            viewButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Display the post details in a new window or dialog when the "View" button is clicked
                    viewPostDetails(rollNo, name, contact, location, itemName, itemDescription, status, photoPath);
                }
            });

            // Add the "View" button to the post panel
            postPanel.add(viewButton, BorderLayout.SOUTH);

            postsPanel.add(postPanel); // Add the post panel to the main postsPanel
        }
    }

    // Method to display the full post details when the "View" button is clicked
    private void viewPostDetails(String rollNo, String name, String contact, String location, String itemName, 
                                 String itemDescription, String status, String photoPath) {
        // Create a new JFrame to display the full post details
        JFrame postDetailFrame = new JFrame("Post Details");
        postDetailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        postDetailFrame.setSize(400, 500); // Adjust size as needed
        
        // Create a JPanel for the details
        JPanel postDetailPanel = new JPanel();
        postDetailPanel.setLayout(new BoxLayout(postDetailPanel, BoxLayout.Y_AXIS));

        // Add the image if available
        if (photoPath != null && !photoPath.isEmpty()) {
            File imageFile = new File(photoPath);
            if (imageFile.exists()) {
                ImageIcon imageIcon = new ImageIcon(photoPath);
                Image img = imageIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(img);
                JLabel imageLabel = new JLabel(imageIcon);
                postDetailPanel.add(imageLabel); // Add image to the detail panel
            }
        }

        // Add the text details
        JTextArea postDetailsArea = new JTextArea();
        postDetailsArea.setEditable(false);
        postDetailsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        postDetailsArea.setText("Item Name: " + itemName + "\n" +
                "Item Description: " + itemDescription + "\n" +
                "Roll No: " + rollNo + "\n" +
                "Name: " + name + "\n" +
                "Contact: " + contact + "\n" +
                "Location: " + location + "\n" +
                "Status: " + status);
        
        postDetailPanel.add(postDetailsArea);
        postDetailFrame.add(postDetailPanel);
        postDetailFrame.setVisible(true);
    }

    // Main method to run the Swing application
    public static void main(String[] args) {
        // Swing UI should be run on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PostViewPage postViewPage = new PostViewPage();
                postViewPage.loadPosts(); // Load the posts after the UI is set up
            }
        });
    }
}
