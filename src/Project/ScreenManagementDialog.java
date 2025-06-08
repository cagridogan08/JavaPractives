package Project;

import Designer.DesignComponent;
import Designer.FormPreview;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Comprehensive dialog for managing all screens in a project
 * Provides overview, bulk operations, and detailed management capabilities
 */
public class ScreenManagementDialog extends JDialog {
    private DesignProject project;
    private DefaultTableModel tableModel;
    private JTable screensTable;
    private JButton addButton;
    private JButton duplicateButton;
    private JButton removeButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JButton settingsButton;
    private JButton previewButton;
    private JLabel projectInfoLabel;
    private JProgressBar screenCountProgress;

    public ScreenManagementDialog(JFrame parent, DesignProject project) {
        super(parent, "Manage Screens - " + project.getName(), true);
        this.project = project;
        initializeDialog();
        createComponents();
        layoutComponents();
        setupEvents();
        loadScreenData();
    }

    private void initializeDialog() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void createComponents() {
        // Table model with custom columns
        String[] columnNames = {"", "Screen Name", "Type", "Size", "Components", "Visible", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 5; // Name and Visible columns editable
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: return Icon.class;
                    case 5: return Boolean.class;
                    default: return String.class;
                }
            }
        };

        screensTable = new JTable(tableModel);
        screensTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        screensTable.setRowHeight(25);
        screensTable.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        TableColumnModel columnModel = screensTable.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(30); // Icon
        columnModel.getColumn(1).setPreferredWidth(150); // Name
        columnModel.getColumn(2).setPreferredWidth(100); // Type
        columnModel.getColumn(3).setPreferredWidth(80);  // Size
        columnModel.getColumn(4).setPreferredWidth(80);  // Components
        columnModel.getColumn(5).setPreferredWidth(60);  // Visible
        columnModel.getColumn(6).setPreferredWidth(200); // Description

        // Custom renderer for the icon column
        screensTable.getColumnModel().getColumn(0).setCellRenderer(new ScreenIconRenderer());

        // Action buttons
        addButton = new JButton("Add Screen");
        addButton.setToolTipText("Add a new screen to the project");

        duplicateButton = new JButton("Duplicate");
        duplicateButton.setToolTipText("Duplicate the selected screen");

        removeButton = new JButton("Remove");
        removeButton.setToolTipText("Remove the selected screen");

        moveUpButton = new JButton("↑ Move Up");
        moveUpButton.setToolTipText("Move screen up in the list");

        moveDownButton = new JButton("↓ Move Down");
        moveDownButton.setToolTipText("Move screen down in the list");

        settingsButton = new JButton("Settings");
        settingsButton.setToolTipText("Edit settings for the selected screen");

        previewButton = new JButton("Preview");
        previewButton.setToolTipText("Preview the selected screen");

        // Project info components
        projectInfoLabel = new JLabel();
        screenCountProgress = new JProgressBar(0, 20);
        screenCountProgress.setStringPainted(true);
        screenCountProgress.setString("0 screens");

        updateButtonStates();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top panel - project information
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center panel - screens table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Project Screens"));

        JScrollPane tableScrollPane = new JScrollPane(screensTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 400));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Right panel - action buttons
        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.EAST);

        // Bottom panel - dialog buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // Project info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        projectInfoLabel.setFont(projectInfoLabel.getFont().deriveFont(Font.BOLD, 14f));
        infoPanel.add(projectInfoLabel);

        // Screen count progress
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        progressPanel.add(new JLabel("Screens: "));
        progressPanel.add(screenCountProgress);
        infoPanel.add(progressPanel);

        panel.add(infoPanel, BorderLayout.WEST);

        // Quick stats panel
        JPanel statsPanel = createStatsPanel();
        panel.add(statsPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Quick Stats"));

        // Count screens by type
        int mainScreens = 0, dialogScreens = 0, customScreens = 0;
        int totalComponents = 0;

        for (DesignScreen screen : project.getScreens()) {
            switch (screen.getType()) {
                case MAIN: mainScreens++; break;
                case DIALOG: dialogScreens++; break;
                case CUSTOM: customScreens++; break;
            }
            totalComponents += screen.getComponents().size();
        }

        panel.add(new JLabel("Main: " + mainScreens));
        panel.add(new JLabel("Dialogs: " + dialogScreens));
        panel.add(new JLabel("Custom: " + customScreens));
        panel.add(new JLabel("Total Components: " + totalComponents));
        panel.add(new JLabel("Active: " + project.getScreens().stream().mapToInt(s -> s.isVisible() ? 1 : 0).sum()));
        panel.add(new JLabel(""));

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 0, 3, 0);
        gbc.weightx = 1.0;

        int row = 0;

        // Add section
        gbc.gridy = row++;
        panel.add(addButton, gbc);

        gbc.gridy = row++;
        panel.add(duplicateButton, gbc);

        // Separator
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(3, 0, 3, 0);

        // Edit section
        gbc.gridy = row++;
        panel.add(settingsButton, gbc);

        gbc.gridy = row++;
        panel.add(previewButton, gbc);

        // Separator
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(3, 0, 3, 0);

        // Order section
        gbc.gridy = row++;
        panel.add(moveUpButton, gbc);

        gbc.gridy = row++;
        panel.add(moveDownButton, gbc);

        // Separator
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(3, 0, 3, 0);

        // Remove section
        gbc.gridy = row++;
        panel.add(removeButton, gbc);

        // Spacer
        gbc.gridy = row++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton exportButton = new JButton("Export List");
        exportButton.setToolTipText("Export screen list to file");
        exportButton.addActionListener(e -> exportScreenList());

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        panel.add(exportButton);
        panel.add(closeButton);

        return panel;
    }

    private void setupEvents() {
        // Table selection listener
        screensTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Double-click to edit
        screensTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedScreen();
                }
            }
        });

        // Button actions
        addButton.addActionListener(e -> addNewScreen());
        duplicateButton.addActionListener(e -> duplicateSelectedScreen());
        removeButton.addActionListener(e -> removeSelectedScreen());
        moveUpButton.addActionListener(e -> moveSelectedScreen(-1));
        moveDownButton.addActionListener(e -> moveSelectedScreen(1));
        settingsButton.addActionListener(e -> editSelectedScreen());
        previewButton.addActionListener(e -> previewSelectedScreen());

        // Table model listener for live updates
        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 1) { // Name column changed
                int row = e.getFirstRow();
                if (row >= 0 && row < project.getScreens().size()) {
                    String newName = (String) tableModel.getValueAt(row, 1);
                    project.getScreens().get(row).setName(newName);
                }
            } else if (e.getColumn() == 5) { // Visible column changed
                int row = e.getFirstRow();
                if (row >= 0 && row < project.getScreens().size()) {
                    Boolean visible = (Boolean) tableModel.getValueAt(row, 5);
                    project.getScreens().get(row).setVisible(visible);
                }
            }
            updateProjectInfo();
        });
    }

    private void loadScreenData() {
        tableModel.setRowCount(0);

        for (DesignScreen screen : project.getScreens()) {
            Object[] rowData = {
                    getIconForScreenType(screen.getType()),
                    screen.getName(),
                    screen.getType().getDisplayName(),
                    screen.getScreenSetting("width", 800) + "×" + screen.getScreenSetting("height", 600),
                    screen.getComponents().size(),
                    screen.isVisible(),
                    truncateDescription(screen.getDescription())
            };
            tableModel.addRow(rowData);
        }

        updateProjectInfo();
        updateButtonStates();
    }

    private String truncateDescription(String description) {
        if (description == null || description.length() <= 50) {
            return description;
        }
        return description.substring(0, 47) + "...";
    }

    private Icon getIconForScreenType(ScreenType type) {
        // Create simple colored icons for different screen types
        return new ColorIcon(getScreenTypeColor(type), 16, 16);
    }

    private Color getScreenTypeColor(ScreenType type) {
        switch (type) {
            case MAIN: return new Color(100, 150, 255);
            case DIALOG: return new Color(255, 150, 100);
            case LOGIN: return new Color(100, 255, 150);
            case SPLASH: return new Color(255, 100, 255);
            case SETTINGS: return new Color(150, 150, 255);
            case DASHBOARD: return new Color(255, 255, 100);
            default: return Color.GRAY;
        }
    }

    private void updateProjectInfo() {
        projectInfoLabel.setText("Project: " + project.getName() + " (" + project.getScreens().size() + " screens)");

        int screenCount = project.getScreens().size();
        screenCountProgress.setValue(Math.min(screenCount, 20));
        screenCountProgress.setString(screenCount + " screens");

        if (screenCount > 15) {
            screenCountProgress.setForeground(Color.RED);
        } else if (screenCount > 10) {
            screenCountProgress.setForeground(Color.ORANGE);
        } else {
            screenCountProgress.setForeground(Color.GREEN);
        }
    }

    private void updateButtonStates() {
        int selectedRow = screensTable.getSelectedRow();
        boolean hasSelection = selectedRow >= 0;
        boolean hasMultipleScreens = project.getScreens().size() > 1;

        duplicateButton.setEnabled(hasSelection);
        removeButton.setEnabled(hasSelection && hasMultipleScreens);
        moveUpButton.setEnabled(hasSelection && selectedRow > 0);
        moveDownButton.setEnabled(hasSelection && selectedRow < project.getScreens().size() - 1);
        settingsButton.setEnabled(hasSelection);
        previewButton.setEnabled(hasSelection);
    }

    private void addNewScreen() {
        AddScreenDialog dialog = new AddScreenDialog((JFrame) getParent());
        ScreenCreationData data = dialog.showDialog();

        if (data != null) {
            DesignScreen newScreen = project.createNewScreen(data.name, data.type);
            newScreen.setDescription(data.description);
            loadScreenData();

            // Select the new screen
            int newIndex = project.getScreens().size() - 1;
            screensTable.setRowSelectionInterval(newIndex, newIndex);
        }
    }

    private void duplicateSelectedScreen() {
        int selectedRow = screensTable.getSelectedRow();
        if (selectedRow >= 0) {
            DesignScreen originalScreen = project.getScreens().get(selectedRow);
            DesignScreen newScreen = new DesignScreen(originalScreen.getName() + " Copy", originalScreen.getType());
            newScreen.setDescription(originalScreen.getDescription());

            // Copy components (simplified)
            for (DesignComponent comp : originalScreen.getComponents()) {
                // Create copy with offset
                DesignComponent newComp;
                newComp = new DesignComponent(comp.getComponentType(),
                        comp.getBounds().x + 20, comp.getBounds().y + 20);
                // Copy basic properties
                newComp.setText(comp.getText());
                newComp.setSize(comp.getBounds().width, comp.getBounds().height);
                newComp.setBackgroundColor(comp.getBackgroundColor());
                newComp.setVisible(comp.isVisible());
                newComp.setEnabled(comp.isEnabled());

                newScreen.addComponent(newComp);
            }

            project.addScreen(newScreen);
            loadScreenData();

            // Select the new screen
            int newIndex = project.getScreens().size() - 1;
            screensTable.setRowSelectionInterval(newIndex, newIndex);
        }
    }

    private void removeSelectedScreen() {
        int selectedRow = screensTable.getSelectedRow();
        if (selectedRow >= 0 && project.getScreens().size() > 1) {
            DesignScreen screen = project.getScreens().get(selectedRow);

            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove the screen '" + screen.getName() + "'?\n" +
                            "This action cannot be undone.",
                    "Remove Screen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                project.removeScreen(screen);
                loadScreenData();

                // Select next available row
                if (selectedRow >= project.getScreens().size()) {
                    selectedRow = project.getScreens().size() - 1;
                }
                if (selectedRow >= 0) {
                    screensTable.setRowSelectionInterval(selectedRow, selectedRow);
                }
            }
        }
    }

    private void moveSelectedScreen(int direction) {
        int selectedRow = screensTable.getSelectedRow();
        if (selectedRow >= 0) {
            int newIndex = selectedRow + direction;
            if (newIndex >= 0 && newIndex < project.getScreens().size()) {
                List<DesignScreen> screens = project.getScreens();
                DesignScreen screen = screens.remove(selectedRow);
                screens.add(newIndex, screen);

                loadScreenData();
                screensTable.setRowSelectionInterval(newIndex, newIndex);
            }
        }
    }

    private void editSelectedScreen() {
        int selectedRow = screensTable.getSelectedRow();
        if (selectedRow >= 0) {
            DesignScreen screen = project.getScreens().get(selectedRow);
            ScreenSettingsDialog dialog = new ScreenSettingsDialog((JFrame) getParent(), screen);
            dialog.setVisible(true);
            loadScreenData(); // Refresh data after editing
        }
    }

    private void previewSelectedScreen() {
        int selectedRow = screensTable.getSelectedRow();
        if (selectedRow >= 0) {
            DesignScreen screen = project.getScreens().get(selectedRow);
            FormPreview preview = new FormPreview();
            JFrame previewFrame = preview.createPreviewFrame(screen.getComponents());
            previewFrame.setTitle("Preview: " + screen.getName());
            previewFrame.setLocationRelativeTo(this);
            previewFrame.setVisible(true);
        }
    }

    private void exportScreenList() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(project.getName() + "_screens.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile());
                writer.write("Screen List for Project: " + project.getName() + "\n");
                writer.write("Generated: " + new java.util.Date() + "\n\n");

                for (DesignScreen screen : project.getScreens()) {
                    writer.write("Screen: " + screen.getName() + "\n");
                    writer.write("Type: " + screen.getType().getDisplayName() + "\n");
                    writer.write("Components: " + screen.getComponents().size() + "\n");
                    writer.write("Visible: " + screen.isVisible() + "\n");
                    writer.write("Description: " + screen.getDescription() + "\n\n");
                }

                writer.close();
                JOptionPane.showMessageDialog(this, "Screen list exported successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting screen list: " + e.getMessage());
            }
        }
    }
}

/**
 * Custom table cell renderer for screen type icons
 */
class ScreenIconRenderer extends JLabel implements TableCellRenderer {
    public ScreenIconRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Icon) {
            setIcon((Icon) value);
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        return this;
    }
}

/**
 * Simple colored icon for screen types
 */
class ColorIcon implements Icon {
    private Color color;
    private int width;
    private int height;

    public ColorIcon(Color color, int width, int height) {
        this.color = color;
        this.width = width;
        this.height = height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(color);
        g2d.fillOval(x + 2, y + 2, width - 4, height - 4);
        g2d.setColor(color.darker());
        g2d.drawOval(x + 2, y + 2, width - 4, height - 4);
        g2d.dispose();
    }

    @Override
    public int getIconWidth() { return width; }

    @Override
    public int getIconHeight() { return height; }
}