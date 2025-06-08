package Project;

import javax.swing.*;
import java.awt.*; /**
 * Dialog for editing project-wide settings
 * Configures global project properties and defaults
 */
public class ProjectSettingsDialog extends JDialog {
    private DesignProject project;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<String> resolutionComboBox;
    private JComboBox<String> themeComboBox;
    private JSpinner gridSizeSpinner;
    private JCheckBox snapToGridCheckBox;
    private JTextField authorField;
    private JTextField versionField;
    private JComboBox<String> languageComboBox;
    private JComboBox<String> lookAndFeelComboBox;

    public ProjectSettingsDialog(JFrame parent, DesignProject project) {
        super(parent, "Project Settings", true);
        this.project = project;
        initializeDialog();
        createComponents();
        layoutComponents();
        setupEvents();
        loadProjectData();
    }

    private void initializeDialog() {
        setSize(500, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void createComponents() {
        nameField = new JTextField(20);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        String[] resolutions = {"1920x1080", "1366x768", "1280x720", "1024x768", "800x600", "Custom"};
        resolutionComboBox = new JComboBox<>(resolutions);

        String[] themes = {"Light", "Dark", "System", "Custom"};
        themeComboBox = new JComboBox<>(themes);

        gridSizeSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
        snapToGridCheckBox = new JCheckBox("Snap to grid");

        authorField = new JTextField(20);
        versionField = new JTextField("1.0", 10);

        String[] languages = {"Java", "Kotlin", "Scala"};
        languageComboBox = new JComboBox<>(languages);

        String[] lookAndFeels = {"System", "Metal", "Nimbus", "Windows", "GTK+"};
        lookAndFeelComboBox = new JComboBox<>(lookAndFeels);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // General tab
        JPanel generalPanel = createGeneralPanel();
        tabbedPane.addTab("General", generalPanel);

        // Design tab
        JPanel designPanel = createDesignPanel();
        tabbedPane.addTab("Design", designPanel);

        // Code Generation tab
        JPanel codePanel = createCodePanel();
        tabbedPane.addTab("Code Generation", codePanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Project Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Project Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(nameField, gbc);
        row++;

        // Author
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(authorField, gbc);
        row++;

        // Version
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Version:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(versionField, gbc);
        row++;

        // Target Resolution
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Target Resolution:"), gbc);
        gbc.gridx = 1;
        panel.add(resolutionComboBox, gbc);
        row++;

        // Description
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        panel.add(new JScrollPane(descriptionArea), gbc);

        return panel;
    }

    private JPanel createDesignPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Theme
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Theme:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(themeComboBox, gbc);
        row++;

        // Grid settings
        JPanel gridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        gridPanel.setBorder(BorderFactory.createTitledBorder("Grid Settings"));

        gridPanel.add(new JLabel("Grid Size:"));
        gridPanel.add(Box.createHorizontalStrut(5));
        gridPanel.add(gridSizeSpinner);
        gridPanel.add(Box.createHorizontalStrut(15));
        gridPanel.add(snapToGridCheckBox);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(gridPanel, gbc);
        row++;

        // Add spacer
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel createCodePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Target Language
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Target Language:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(languageComboBox, gbc);
        row++;

        // Look and Feel
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Look and Feel:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(lookAndFeelComboBox, gbc);
        row++;

        // Code generation options
        JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Code Generation Options"));

        JCheckBox generateCommentsCheckBox = new JCheckBox("Generate comments", true);
        JCheckBox generateEventsCheckBox = new JCheckBox("Generate event handlers", true);
        JCheckBox optimizeCodeCheckBox = new JCheckBox("Optimize generated code", false);

        optionsPanel.add(generateCommentsCheckBox);
        optionsPanel.add(generateEventsCheckBox);
        optionsPanel.add(optimizeCodeCheckBox);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(optionsPanel, gbc);
        row++;

        // Add spacer
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton resetButton = new JButton("Reset to Defaults");
        JButton cancelButton = new JButton("Cancel");
        JButton okButton = new JButton("OK");

        resetButton.addActionListener(e -> resetToDefaults());
        cancelButton.addActionListener(e -> dispose());
        okButton.addActionListener(e -> saveAndClose());

        panel.add(resetButton);
        panel.add(cancelButton);
        panel.add(okButton);

        return panel;
    }

    private void setupEvents() {
        // Add any specific event handlers here
    }

    private void loadProjectData() {
        nameField.setText(project.getName());
        descriptionArea.setText(project.getDescription());

        String resolution = (String) project.getProjectSettings().getOrDefault("targetResolution", "1920x1080");
        resolutionComboBox.setSelectedItem(resolution);

        String theme = (String) project.getProjectSettings().getOrDefault("theme", "Light");
        themeComboBox.setSelectedItem(theme);

        Integer gridSize = (Integer) project.getProjectSettings().getOrDefault("gridSize", 10);
        gridSizeSpinner.setValue(gridSize);

        Boolean snapToGrid = (Boolean) project.getProjectSettings().getOrDefault("snapToGrid", true);
        snapToGridCheckBox.setSelected(snapToGrid);

        String author = (String) project.getProjectSettings().getOrDefault("author", "");
        authorField.setText(author);

        String version = (String) project.getProjectSettings().getOrDefault("version", "1.0");
        versionField.setText(version);

        String language = (String) project.getProjectSettings().getOrDefault("targetLanguage", "Java");
        languageComboBox.setSelectedItem(language);

        String lookAndFeel = (String) project.getProjectSettings().getOrDefault("lookAndFeel", "System");
        lookAndFeelComboBox.setSelectedItem(lookAndFeel);
    }

    private void resetToDefaults() {
        resolutionComboBox.setSelectedItem("1920x1080");
        themeComboBox.setSelectedItem("Light");
        gridSizeSpinner.setValue(10);
        snapToGridCheckBox.setSelected(true);
        authorField.setText("");
        versionField.setText("1.0");
        languageComboBox.setSelectedItem("Java");
        lookAndFeelComboBox.setSelectedItem("System");
    }

    private void saveAndClose() {
        project.setName(nameField.getText().trim());
        project.setDescription(descriptionArea.getText().trim());

        project.getProjectSettings().put("targetResolution", resolutionComboBox.getSelectedItem());
        project.getProjectSettings().put("theme", themeComboBox.getSelectedItem());
        project.getProjectSettings().put("gridSize", gridSizeSpinner.getValue());
        project.getProjectSettings().put("snapToGrid", snapToGridCheckBox.isSelected());
        project.getProjectSettings().put("author", authorField.getText().trim());
        project.getProjectSettings().put("version", versionField.getText().trim());
        project.getProjectSettings().put("targetLanguage", languageComboBox.getSelectedItem());
        project.getProjectSettings().put("lookAndFeel", lookAndFeelComboBox.getSelectedItem());

        dispose();
    }
}
