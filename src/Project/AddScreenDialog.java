package Project;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Professional dialog for creating new screens in a project
 * Features screen type selection with visual previews and descriptions
 */
public class AddScreenDialog extends JDialog {
    private JTextField nameField;
    private JList<ScreenType> typeList;
    private JTextArea descriptionArea;
    private JLabel previewLabel;
    private JCheckBox addDefaultComponentsCheckBox;
    private JPanel detailsPanel;

    private ScreenCreationData result;
    private boolean cancelled = true;

    public AddScreenDialog(JFrame parent) {
        super(parent, "Add New Screen", true);
        initializeDialog();
        createComponents();
        layoutComponents();
        setupEvents();
        updatePreview();
    }

    private void initializeDialog() {
        setSize(700, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
    }

    private void createComponents() {
        // Screen name field
        nameField = new JTextField("New Screen", 25);

        // Screen type list with custom renderer
        typeList = new JList<>(ScreenType.values());
        typeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        typeList.setSelectedValue(ScreenType.MAIN, true);
        typeList.setCellRenderer(new ScreenTypeListCellRenderer());
        typeList.setVisibleRowCount(8);

        // Description area
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(getBackground());
        descriptionArea.setEditable(false);

        // Preview panel
        previewLabel = new JLabel();
        previewLabel.setHorizontalAlignment(JLabel.CENTER);
        previewLabel.setVerticalAlignment(JLabel.CENTER);
        previewLabel.setPreferredSize(new Dimension(250, 200));
        previewLabel.setBorder(BorderFactory.createEmptyBorder());
        previewLabel.setBackground(Color.WHITE);
        previewLabel.setOpaque(true);

        // Options
        addDefaultComponentsCheckBox = new JCheckBox("Add default components for this screen type", true);

        // Details panel for selected screen type
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Screen Type Details"));
        detailsPanel.setPreferredSize(new Dimension(250, 120));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top panel - screen name
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        topPanel.add(new JLabel("Screen Name:"));
        topPanel.add(nameField);
        add(topPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Left side - screen type selection
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Select Screen Type"));

        JScrollPane typeScrollPane = new JScrollPane(typeList);
        typeScrollPane.setPreferredSize(new Dimension(200, 250));
        leftPanel.add(typeScrollPane, BorderLayout.CENTER);

        // Add options at bottom of left panel
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        optionsPanel.add(addDefaultComponentsCheckBox, BorderLayout.CENTER);
        leftPanel.add(optionsPanel, BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Right side - preview and details
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        // Preview panel
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        previewPanel.add(previewLabel, BorderLayout.CENTER);
        rightPanel.add(previewPanel, BorderLayout.CENTER);

        // Details panel
        rightPanel.add(detailsPanel, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Bottom panel - buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton cancelButton = new JButton("Cancel");
        JButton createButton = new JButton("Create Screen");

        // Style buttons
        createButton.setPreferredSize(new Dimension(120, 30));
        cancelButton.setPreferredSize(new Dimension(80, 30));

        cancelButton.addActionListener(e -> dispose());
        createButton.addActionListener(e -> createScreen());

        // Make Create button default
        getRootPane().setDefaultButton(createButton);

        panel.add(cancelButton);
        panel.add(createButton);

        return panel;
    }

    private void setupEvents() {
        typeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreview();
                updateDetails();
            }
        });

        nameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
    }

    private void updatePreview() {
        ScreenType selectedType = typeList.getSelectedValue();
        if (selectedType != null) {
            String previewHtml = generatePreviewHtml(selectedType);
            previewLabel.setText(previewHtml);
        }
    }

    private void updateDetails() {
        ScreenType selectedType = typeList.getSelectedValue();
        if (selectedType != null) {
            detailsPanel.removeAll();

            // Create details content
            JPanel content = new JPanel(new BorderLayout());

            // Description
            JTextArea detailsText = new JTextArea();
            detailsText.setText(getDetailedDescription(selectedType));
            detailsText.setLineWrap(true);
            detailsText.setWrapStyleWord(true);
            detailsText.setEditable(false);
            detailsText.setBackground(getBackground());
            detailsText.setFont(detailsText.getFont().deriveFont(Font.PLAIN, 11f));

            content.add(new JScrollPane(detailsText), BorderLayout.CENTER);

            // Default components info
            if (addDefaultComponentsCheckBox.isSelected()) {
                JLabel componentsLabel = new JLabel("<html><b>Default components:</b> " + getDefaultComponents(selectedType) + "</html>");
                componentsLabel.setFont(componentsLabel.getFont().deriveFont(10f));
                componentsLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
                content.add(componentsLabel, BorderLayout.SOUTH);
            }

            detailsPanel.add(content, BorderLayout.CENTER);
            detailsPanel.revalidate();
            detailsPanel.repaint();
        }
    }

    private String generatePreviewHtml(ScreenType type) {
        String screenName = nameField.getText().isEmpty() ? "New Screen" : nameField.getText();

        StringBuilder html = new StringBuilder();
        html.append("<html><center>");
        html.append("<div style='font-family: monospace; font-size: 10px; line-height: 1.2;'>");
        html.append("<b style='font-size: 12px;'>").append(screenName).append("</b><br><br>");

        switch (type) {
            case MAIN:
                html.append("┌─────────────────────┐<br>");
                html.append("│ ≡ File Edit View Help │<br>");
                html.append("├─────────────────────┤<br>");
                html.append("│ ⚡ 📁 💾 ✂️ 📋 🔍    │<br>");
                html.append("├─────────────────────┤<br>");
                html.append("│                     │<br>");
                html.append("│                     │<br>");
                html.append("│   Main Content      │<br>");
                html.append("│      Area           │<br>");
                html.append("│                     │<br>");
                html.append("│                     │<br>");
                html.append("├─────────────────────┤<br>");
                html.append("│ Ready │ 🔘 100%     │<br>");
                html.append("└─────────────────────┘");
                break;

            case DIALOG:
                html.append("┌─────────────────┐<br>");
                html.append("│ Dialog Title  ❌ │<br>");
                html.append("├─────────────────┤<br>");
                html.append("│                 │<br>");
                html.append("│    Content      │<br>");
                html.append("│     Area        │<br>");
                html.append("│                 │<br>");
                html.append("├─────────────────┤<br>");
                html.append("│      [OK] [Cancel] │<br>");
                html.append("└─────────────────┘");
                break;

            case LOGIN:
                html.append("┌───────────────┐<br>");
                html.append("│ 🔐 Login      │<br>");
                html.append("├───────────────┤<br>");
                html.append("│ Username:     │<br>");
                html.append("│ [____________] │<br>");
                html.append("│ Password:     │<br>");
                html.append("│ [____________] │<br>");
                html.append("│ ☐ Remember me │<br>");
                html.append("│    [Login]    │<br>");
                html.append("│ Forgot password? │<br>");
                html.append("└───────────────┘");
                break;

            case SPLASH:
                html.append("┌─────────────────┐<br>");
                html.append("│                 │<br>");
                html.append("│   🚀 MyApp      │<br>");
                html.append("│   Version 1.0   │<br>");
                html.append("│                 │<br>");
                html.append("│ ████████████░░  │<br>");
                html.append("│  Loading...     │<br>");
                html.append("│                 │<br>");
                html.append("└─────────────────┘");
                break;

            case SETTINGS:
                html.append("┌─────────────────┐<br>");
                html.append("│ ⚙️ Settings      │<br>");
                html.append("├─────────────────┤<br>");
                html.append("│ ▶ General       │<br>");
                html.append("│ ▷ Appearance    │<br>");
                html.append("│ ▷ Privacy       │<br>");
                html.append("│ ▷ Advanced      │<br>");
                html.append("├─────────────────┤<br>");
                html.append("│  [Apply] [OK]   │<br>");
                html.append("└─────────────────┘");
                break;

            case DASHBOARD:
                html.append("┌─────────────────┐<br>");
                html.append("│ 📊 Dashboard    │<br>");
                html.append("├─────────────────┤<br>");
                html.append("│ ▄▄▄ ▲   📈 75%  │<br>");
                html.append("│ ███ █   ━━━━━━  │<br>");
                html.append("│ ███ █   📉 25%  │<br>");
                html.append("│ Charts & Stats  │<br>");
                html.append("└─────────────────┘");
                break;

            case WIZARD:
                html.append("┌─────────────────┐<br>");
                html.append("│ 🧙‍♂️ Wizard        │<br>");
                html.append("├─────────────────┤<br>");
                html.append("│ ● ○ ○ ○ Step 1  │<br>");
                html.append("│                 │<br>");
                html.append("│  Step Content   │<br>");
                html.append("│                 │<br>");
                html.append("├─────────────────┤<br>");
                html.append("│ [< Back] [Next >] │<br>");
                html.append("└─────────────────┘");
                break;

            case FORM:
                html.append("┌─────────────────┐<br>");
                html.append("│ 📝 Data Entry   │<br>");
                html.append("├─────────────────┤<br>");
                html.append("│ Name: [_______] │<br>");
                html.append("│ Email:[_______] │<br>");
                html.append("│ Phone:[_______] │<br>");
                html.append("│ ☐ Subscribe    │<br>");
                html.append("├─────────────────┤<br>");
                html.append("│ [Save] [Reset]  │<br>");
                html.append("└─────────────────┘");
                break;

            case LIST:
                html.append("┌─────────────────┐<br>");
                html.append("│ 📋 Items List   │<br>");
                html.append("├─────────────────┤<br>");
                html.append("│ ▶ Item 1       │<br>");
                html.append("│   Item 2       │<br>");
                html.append("│   Item 3       │<br>");
                html.append("│   Item 4       │<br>");
                html.append("├─────────────────┤<br>");
                html.append("│ [Add] [Delete]  │<br>");
                html.append("└─────────────────┘");
                break;

            default:
                html.append("┌─────────────────┐<br>");
                html.append("│                 │<br>");
                html.append("│     Custom      │<br>");
                html.append("│     Screen      │<br>");
                html.append("│                 │<br>");
                html.append("└─────────────────┘");
                break;
        }

        html.append("</div></center></html>");
        return html.toString();
    }

    private String getDetailedDescription(ScreenType type) {
        switch (type) {
            case MAIN:
                return "Primary application window with menu bar, toolbar, main content area, and status bar. Ideal for the main interface of desktop applications.";
            case DIALOG:
                return "Modal or non-modal dialog window for user interactions, confirmations, or data entry. Typically includes OK/Cancel buttons.";
            case LOGIN:
                return "User authentication screen with username/password fields, remember me option, and login button. Essential for secure applications.";
            case SPLASH:
                return "Application startup screen displaying logo, version info, and loading progress. Shows while the application initializes.";
            case SETTINGS:
                return "Configuration and preferences screen with categorized options. Allows users to customize application behavior.";
            case DASHBOARD:
                return "Data visualization and overview screen with charts, graphs, and key metrics. Perfect for analytics and monitoring.";
            case WIZARD:
                return "Multi-step guided process with navigation between steps. Ideal for complex workflows and setup procedures.";
            case FORM:
                return "Data entry interface with input fields, validation, and submit actions. Used for collecting user information.";
            case LIST:
                return "Display and manage collections of items with selection, filtering, and CRUD operations.";
            case DETAIL:
                return "Detailed view of a single item with comprehensive information and editing capabilities.";
            case ABOUT:
                return "Information dialog showing application details, version, credits, and legal information.";
            case REPORT:
                return "Formatted data presentation with tables, charts, and export capabilities for business reporting.";
            default:
                return "Custom screen type with flexible layout and functionality defined by your specific requirements.";
        }
    }

    private String getDefaultComponents(ScreenType type) {
        switch (type) {
            case MAIN:
                return "Menu bar, toolbar, status bar";
            case DIALOG:
                return "OK button, Cancel button";
            case LOGIN:
                return "Username field, password field, login button, remember checkbox";
            case SPLASH:
                return "Logo label, progress bar, version label";
            case SETTINGS:
                return "Category list, options panel";
            case DASHBOARD:
                return "Data grid, chart components";
            case WIZARD:
                return "Step indicator, navigation buttons";
            case FORM:
                return "Input fields, submit/reset buttons";
            case LIST:
                return "List component, action buttons";
            default:
                return "None";
        }
    }

    private void createScreen() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a screen name.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }

        ScreenType type = typeList.getSelectedValue();
        if (type == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a screen type.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String description = getDetailedDescription(type);

        result = new ScreenCreationData(name, type, description);
        cancelled = false;
        dispose();
    }

    public ScreenCreationData showDialog() {
        setVisible(true);
        return cancelled ? null : result;
    }
}

/**
 * Custom list cell renderer for screen types
 */
class ScreenTypeListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof ScreenType) {
            ScreenType type = (ScreenType) value;
            setText(getIconForType(type) + " " + type.getDisplayName());
            setToolTipText(type.getDescription());
        }

        return this;
    }

    private String getIconForType(ScreenType type) {
        switch (type) {
            case MAIN: return "🏠";
            case DIALOG: return "💬";
            case LOGIN: return "🔐";
            case SPLASH: return "🚀";
            case SETTINGS: return "⚙️";
            case DASHBOARD: return "📊";
            case WIZARD: return "🧙‍♂️";
            case FORM: return "📝";
            case LIST: return "📋";
            case DETAIL: return "🔍";
            case ABOUT: return "ℹ️";
            case REPORT: return "📄";
            default: return "📱";
        }
    }
}