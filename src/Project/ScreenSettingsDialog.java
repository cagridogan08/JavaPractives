package Project;

import javax.swing.*;
import java.awt.*;


public class ScreenSettingsDialog extends JDialog {
    private DesignScreen screen;
    private JTextField nameField;
    private JComboBox<ScreenType> typeComboBox;
    private JTextArea descriptionArea;
    private JSpinner widthSpinner;
    private JSpinner heightSpinner;
    private JButton backgroundColorButton;
    private Color selectedBackgroundColor;
    private JCheckBox visibleCheckBox;
    private JCheckBox modalCheckBox;
    private JCheckBox resizableCheckBox;
    private JCheckBox centerOnScreenCheckBox;
    private JCheckBox showTitleBarCheckBox;
    private JCheckBox showMenuBarCheckBox;
    private JCheckBox showToolbarCheckBox;
    private JCheckBox showStatusBarCheckBox;
    private JSpinner displayTimeSpinner;

    public ScreenSettingsDialog(JFrame parent, DesignScreen screen) {
        super(parent, "Screen Settings - " + screen.getName(), true);
        this.screen = screen;
        initializeDialog();
        createComponents();
        layoutComponents();
        setupEvents();
        loadScreenData();
    }

    private void initializeDialog() {
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void createComponents() {
        nameField = new JTextField(20);
        typeComboBox = new JComboBox<>(ScreenType.values());
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        widthSpinner = new JSpinner(new SpinnerNumberModel(800, 100, 2000, 10));
        heightSpinner = new JSpinner(new SpinnerNumberModel(600, 100, 2000, 10));

        backgroundColorButton = new JButton("Choose Color");
        backgroundColorButton.setPreferredSize(new Dimension(120, 25));

        // General settings
        visibleCheckBox = new JCheckBox("Visible in project");
        modalCheckBox = new JCheckBox("Modal dialog");
        resizableCheckBox = new JCheckBox("Resizable");
        centerOnScreenCheckBox = new JCheckBox("Center on screen");
        showTitleBarCheckBox = new JCheckBox("Show title bar");

        // Main window specific
        showMenuBarCheckBox = new JCheckBox("Show menu bar");
        showToolbarCheckBox = new JCheckBox("Show toolbar");
        showStatusBarCheckBox = new JCheckBox("Show status bar");

        // Splash screen specific
        displayTimeSpinner = new JSpinner(new SpinnerNumberModel(3000, 1000, 10000, 500));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Create tabbed pane for different setting categories
        JTabbedPane tabbedPane = new JTabbedPane();

        // General tab
        JPanel generalPanel = createGeneralPanel();
        tabbedPane.addTab("General", generalPanel);

        // Appearance tab
        JPanel appearancePanel = createAppearancePanel();
        tabbedPane.addTab("Appearance", appearancePanel);

        // Behavior tab
        JPanel behaviorPanel = createBehaviorPanel();
        tabbedPane.addTab("Behavior", behaviorPanel);

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

        // Screen Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Screen Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(nameField, gbc);
        row++;

        // Screen Type
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Screen Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(typeComboBox, gbc);
        row++;

        // Dimensions section
        JPanel dimensionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dimensionsPanel.setBorder(BorderFactory.createTitledBorder("Dimensions"));

        dimensionsPanel.add(new JLabel("Width:"));
        dimensionsPanel.add(Box.createHorizontalStrut(5));
        dimensionsPanel.add(widthSpinner);
        dimensionsPanel.add(Box.createHorizontalStrut(15));
        dimensionsPanel.add(new JLabel("Height:"));
        dimensionsPanel.add(Box.createHorizontalStrut(5));
        dimensionsPanel.add(heightSpinner);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(dimensionsPanel, gbc);
        row++;

        // General options
        JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("General Options"));
        optionsPanel.add(visibleCheckBox);
        optionsPanel.add(centerOnScreenCheckBox);
        optionsPanel.add(showTitleBarCheckBox);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(optionsPanel, gbc);
        row++;

        // Description
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        panel.add(new JScrollPane(descriptionArea), gbc);

        return panel;
    }

    private JPanel createAppearancePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Background Color
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Background Color:"), gbc);
        gbc.gridx = 1;
        panel.add(backgroundColorButton, gbc);
        row++;

        // Main window specific UI elements
        JPanel mainWindowPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        mainWindowPanel.setBorder(BorderFactory.createTitledBorder("Main Window Elements"));
        mainWindowPanel.add(showMenuBarCheckBox);
        mainWindowPanel.add(showToolbarCheckBox);
        mainWindowPanel.add(showStatusBarCheckBox);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(mainWindowPanel, gbc);
        row++;

        // Add spacer
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel createBehaviorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Dialog behavior
        JPanel dialogPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        dialogPanel.setBorder(BorderFactory.createTitledBorder("Dialog Behavior"));
        dialogPanel.add(modalCheckBox);
        dialogPanel.add(resizableCheckBox);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(dialogPanel, gbc);
        row++;

        // Splash screen behavior
        JPanel splashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        splashPanel.setBorder(BorderFactory.createTitledBorder("Splash Screen"));
        splashPanel.add(new JLabel("Display Time (ms):"));
        splashPanel.add(displayTimeSpinner);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(splashPanel, gbc);
        row++;

        // Add spacer
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton cancelButton = new JButton("Cancel");
        JButton okButton = new JButton("OK");

        cancelButton.addActionListener(e -> dispose());
        okButton.addActionListener(e -> saveAndClose());

        panel.add(cancelButton);
        panel.add(okButton);

        return panel;
    }

    private void setupEvents() {
        backgroundColorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(this, "Choose Background Color", selectedBackgroundColor);
            if (color != null) {
                selectedBackgroundColor = color;
                backgroundColorButton.setBackground(color);
                backgroundColorButton.setOpaque(true);
                backgroundColorButton.setText(""); // Remove text to show color better
            }
        });

        typeComboBox.addActionListener(e -> updateTypeSpecificControls());
    }

    private void updateTypeSpecificControls() {
        ScreenType selectedType = (ScreenType) typeComboBox.getSelectedItem();

        // Enable/disable controls based on screen type
        boolean isMainWindow = selectedType == ScreenType.MAIN;
        boolean isDialog = selectedType == ScreenType.DIALOG;
        boolean isSplash = selectedType == ScreenType.SPLASH;

        showMenuBarCheckBox.setEnabled(isMainWindow);
        showToolbarCheckBox.setEnabled(isMainWindow);
        showStatusBarCheckBox.setEnabled(isMainWindow);

        modalCheckBox.setEnabled(isDialog);
        resizableCheckBox.setEnabled(isDialog || isMainWindow);

        displayTimeSpinner.setEnabled(isSplash);
    }

    private void loadScreenData() {
        nameField.setText(screen.getName());
        typeComboBox.setSelectedItem(screen.getType());
        descriptionArea.setText(screen.getDescription());

        Integer width = (Integer) screen.getScreenSetting("width", 800);
        Integer height = (Integer) screen.getScreenSetting("height", 600);
        widthSpinner.setValue(width);
        heightSpinner.setValue(height);

        selectedBackgroundColor = (Color) screen.getScreenSetting("backgroundColor", Color.WHITE);
        backgroundColorButton.setBackground(selectedBackgroundColor);
        backgroundColorButton.setOpaque(true);
        backgroundColorButton.setText("");

        visibleCheckBox.setSelected(screen.isVisible());

        // Load type-specific settings
        modalCheckBox.setSelected((Boolean) screen.getScreenSetting("modal", false));
        resizableCheckBox.setSelected((Boolean) screen.getScreenSetting("resizable", true));
        centerOnScreenCheckBox.setSelected((Boolean) screen.getScreenSetting("centerOnScreen", false));
        showTitleBarCheckBox.setSelected((Boolean) screen.getScreenSetting("showTitleBar", true));
        showMenuBarCheckBox.setSelected((Boolean) screen.getScreenSetting("showMenuBar", true));
        showToolbarCheckBox.setSelected((Boolean) screen.getScreenSetting("showToolbar", true));
        showStatusBarCheckBox.setSelected((Boolean) screen.getScreenSetting("showStatusBar", true));

        Integer displayTime = (Integer) screen.getScreenSetting("displayTime", 3000);
        displayTimeSpinner.setValue(displayTime);

        updateTypeSpecificControls();
    }

    private void saveAndClose() {
        screen.setName(nameField.getText().trim());
        screen.setType((ScreenType) typeComboBox.getSelectedItem());
        screen.setDescription(descriptionArea.getText().trim());

        screen.setScreenSetting("width", widthSpinner.getValue());
        screen.setScreenSetting("height", heightSpinner.getValue());
        screen.setScreenSetting("backgroundColor", selectedBackgroundColor);

        screen.setVisible(visibleCheckBox.isSelected());

        // Save type-specific settings
        screen.setScreenSetting("modal", modalCheckBox.isSelected());
        screen.setScreenSetting("resizable", resizableCheckBox.isSelected());
        screen.setScreenSetting("centerOnScreen", centerOnScreenCheckBox.isSelected());
        screen.setScreenSetting("showTitleBar", showTitleBarCheckBox.isSelected());
        screen.setScreenSetting("showMenuBar", showMenuBarCheckBox.isSelected());
        screen.setScreenSetting("showToolbar", showToolbarCheckBox.isSelected());
        screen.setScreenSetting("showStatusBar", showStatusBarCheckBox.isSelected());
        screen.setScreenSetting("displayTime", displayTimeSpinner.getValue());

        dispose();
    }
}

