package Designer;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;

public class CodeViewDialog extends JDialog {
    private String generatedCode;

    public CodeViewDialog(JFrame parent, String code) {
        super(parent, "Generated Java Code", true);
        this.generatedCode = code;

        initializeDialog();
        createComponents();
    }

    private void initializeDialog() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
    }

    private void createComponents() {
        // Create text area for code display
        JTextArea codeArea = new JTextArea(generatedCode);
        codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        codeArea.setEditable(false);
        codeArea.setCaretPosition(0);
        codeArea.setTabSize(4);

        // Add syntax highlighting colors
        codeArea.setBackground(new Color(248, 248, 248));
        codeArea.setForeground(new Color(51, 51, 51));

        JScrollPane scrollPane = new JScrollPane(codeArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = createButtonPanel(codeArea);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add status label
        JLabel statusLabel = new JLabel("Generated code is ready. You can copy or save it.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.NORTH);
    }

    private JPanel createButtonPanel(JTextArea codeArea) {
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // Copy to clipboard button
        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.setIcon(new ImageIcon()); // You could add an icon here
        copyButton.addActionListener(e -> {
            codeArea.selectAll();
            codeArea.copy();
            JOptionPane.showMessageDialog(this,
                    "Code copied to clipboard!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Save to file button
        JButton saveButton = new JButton("Save to File");
        saveButton.addActionListener(e -> saveCodeToFile());

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(copyButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private void saveCodeToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("GeneratedForm.java"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Java files (*.java)", "java"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                if (!fileName.endsWith(".java")) {
                    fileName += ".java";
                }

                FileWriter writer = new FileWriter(fileName);
                writer.write(generatedCode);
                writer.close();

                JOptionPane.showMessageDialog(this,
                        "Code saved successfully to:\n" + fileName,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error saving file:\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}