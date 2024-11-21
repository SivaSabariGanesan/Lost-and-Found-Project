package lf;

import java.awt.*;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

class PostRenderer extends JPanel implements ListCellRenderer<String> {
    private JLabel imageLabel;
    private JLabel textLabel;

    public PostRenderer() {
        setLayout(new BorderLayout(10, 10));
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(50, 50)); // Thumbnail size
        textLabel = new JLabel();
        add(imageLabel, BorderLayout.WEST);
        add(textLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        // Parse post details
        String[] parts = value.split("\\|");
        String text = parts[0]; // Item Name and Status
        String imagePath = parts.length > 1 ? parts[1] : "No Image";

        // Set text
        textLabel.setText(text);

        // Set image
        if (!"No Image".equals(imagePath)) {
            ImageIcon icon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } else {
            imageLabel.setIcon(null); // Clear icon if no image
            imageLabel.setText("No Image");
        }

        // Highlight selected cell
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
