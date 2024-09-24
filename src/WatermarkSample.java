import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class WatermarkSample extends JFrame {
    private JTextField nameField;
    private JLabel imageLabel;
    private JLabel watermarkImageLabel;
    private JTable table;
    private DefaultTableModel model;
    private String selectedImagePath = "";
    private String selectedWatermarkImagePath = "";

    public WatermarkSample() {
        initComponents();
        addTableHeader();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 3));

        formPanel.add(new JLabel("Image Source:"));
        JButton uploadButton = new JButton("Browse Image");
        formPanel.add(uploadButton);
        imageLabel = new JLabel();
        formPanel.add(imageLabel);

        formPanel.add(new JLabel("Watermark Text:"));
        nameField = new JTextField();
        formPanel.add(nameField);
        formPanel.add(new JLabel());

        formPanel.add(new JLabel("Watermark Image:"));
        JButton uploadWatermarkButton = new JButton("Browse Image");
        formPanel.add(uploadWatermarkButton);
        watermarkImageLabel = new JLabel();
        formPanel.add(watermarkImageLabel);

        add(formPanel, BorderLayout.NORTH);

        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    selectedImagePath = selectedFile.getAbsolutePath();
                    try {
                        BufferedImage image = ImageIO.read(selectedFile);
                        ImageIcon icon = new ImageIcon(image.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                        imageLabel.setIcon(icon);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        uploadWatermarkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    selectedWatermarkImagePath = selectedFile.getAbsolutePath();
                    try {
                        BufferedImage image = ImageIO.read(selectedFile);
                        ImageIcon icon = new ImageIcon(image.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                        watermarkImageLabel.setIcon(icon);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Data");
        JButton deleteButton = new JButton("Delete Data");
        JButton resetButton = new JButton("Reset");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText() == null ? "" : nameField.getText();
                try {
                    BufferedImage originalImage = ImageIO.read(new File(selectedImagePath));
                    BufferedImage watermarkedImage = addWatermark(originalImage, name);
                    if (!selectedWatermarkImagePath.isEmpty()) {
                        BufferedImage watermarkImage = ImageIO.read(new File(selectedWatermarkImagePath));
                        watermarkedImage = addImageWatermark(watermarkedImage, watermarkImage);
                    }
                    JLabel imageLabel = new JLabel();
                    ImageIcon imageIcon = new ImageIcon(watermarkedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH));
                    imageLabel.setIcon(imageIcon);
                    imageLabel.setHorizontalAlignment(JLabel.CENTER);
                    imageLabel.setVerticalAlignment(JLabel.CENTER);

                    model.addRow(new Object[]{name, imageLabel, new JButton("Download")});
                    JOptionPane.showMessageDialog(null, "Data Inserted");
                    clearFields();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    model.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(null, "Data Deleted");
                } else {
                    JOptionPane.showMessageDialog(null, "No row selected");
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
                JOptionPane.showMessageDialog(null, "Fields Reset");
            }
        });

        table = new JTable();
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void addTableHeader() {
        model = new DefaultTableModel();
        Object[] newIdentifiers = new Object[]{"Name", "Image", "Download"};
        model.setColumnIdentifiers(newIdentifiers);
        table.setModel(model);
        table.setFillsViewportHeight(true);
        table.getColumn("Image").setCellRenderer(new ImageCellRenderer());
        table.getColumn("Download").setCellRenderer(new ButtonRenderer());
        table.getColumn("Download").setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    class ImageCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            TableColumn tb = table.getColumn("Image");
            tb.setMaxWidth(300);
            tb.setMinWidth(300);
            table.setRowHeight(200);
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof JLabel) {
                label = (JLabel) value;
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setVerticalAlignment(JLabel.CENTER);
            }
            return label;
        }
    }

    class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JButton) {
                return (JButton) value;
            } else if (value instanceof JLabel) {
                return (JLabel) value;
            } else {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Download");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = "Download";
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = table.getSelectedRow();
                JLabel imageLabel = (JLabel) model.getValueAt(selectedRow, 1);
                ImageIcon imageIcon = (ImageIcon) imageLabel.getIcon();
                BufferedImage image = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                imageIcon.paintIcon(null, g2d, 0, 0);
                g2d.dispose();

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Specify a file to save");
                int userSelection = fileChooser.showSaveDialog(WatermarkSample.this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    try {
                        ImageIO.write(image, "png", fileToSave);
                        JOptionPane.showMessageDialog(null, "Image saved successfully");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    private BufferedImage addWatermark(BufferedImage image, String watermarkText) {
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle rect = new Rectangle(image.getWidth(), image.getHeight());
        int x = rect.x + (rect.width - fontMetrics.stringWidth(watermarkText)) / 2;
        int y = rect.y + (rect.height - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
        g2d.drawString(watermarkText, x, y);
        g2d.dispose();
        return image;
    }

    private BufferedImage addImageWatermark(BufferedImage image, BufferedImage watermarkImage) {
        int watermarkWidth = image.getWidth() / 4;
        int watermarkHeight = image.getHeight() / 4;
        Image scaledWatermarkImage = watermarkImage.getScaledInstance(watermarkWidth, watermarkHeight, Image.SCALE_SMOOTH);

        BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resultImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        int x = (image.getWidth() - watermarkWidth) / 2;
        int y = (image.getHeight() - watermarkHeight) / 2;
        g2d.drawImage(scaledWatermarkImage, x, y, null);
        g2d.dispose();
        return resultImage;
    }

    private void clearFields() {
        nameField.setText("");
        imageLabel.setIcon(null);
        watermarkImageLabel.setIcon(null);
        selectedImagePath = "";
        selectedWatermarkImagePath = "";
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WatermarkSample().setVisible(true);
            }
        });
    }
}