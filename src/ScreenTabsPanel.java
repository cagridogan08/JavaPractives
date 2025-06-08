import Project.*;
import Designer.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * Panel for managing and navigating between multiple screens in a project
 * Provides tabs for each screen and controls for adding/removing screens
 */
public class ScreenTabsPanel extends JPanel implements ProjectManager.ProjectListener {
    private JTabbedPane screenTabs;
    private JButton addScreenButton;
    private JButton removeScreenButton;
    private JButton duplicateScreenButton;
    private DesignPanel designCanvas;
    private DesignProject currentProject;

    public ScreenTabsPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        layoutComponents();
        setupEvents();

        // Register with project manager
        ProjectManager.getInstance().addListener(this);

        // Load current project
        projectChanged(ProjectManager.getInstance().getCurrentProject());
    }

    private void initializeComponents() {
        // Create tabbed pane for screens
        screenTabs = new JTabbedPane(JTabbedPane.BOTTOM);
        screenTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // Create control buttons
        addScreenButton = new JButton("+");
        addScreenButton.setToolTipText("Add New Screen");
        addScreenButton.setPreferredSize(new Dimension(30, 25));

        removeScreenButton = new JButton("×");
        removeScreenButton.setToolTipText("Remove Current Screen");
        removeScreenButton.setPreferredSize(new Dimension(30, 25));

        duplicateScreenButton = new JButton("⧉");
        duplicateScreenButton.setToolTipText("Duplicate Current Screen");
        duplicateScreenButton.setPreferredSize(new Dimension(30, 25));
    }

    private void layoutComponents() {
        // Create toolbar for screen management
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        toolbar.setBorder(BorderFactory.createEtchedBorder());

        toolbar.add(new JLabel("Screens: "));
        toolbar.add(addScreenButton);
        toolbar.add(duplicateScreenButton);
        toolbar.add(removeScreenButton);

        // Project info panel
        JPanel projectInfoPanel = createProjectInfoPanel();

        // Main panel with project info and toolbar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(projectInfoPanel, BorderLayout.WEST);
        topPanel.add(toolbar, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(screenTabs, BorderLayout.CENTER);
    }

    private JPanel createProjectInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder());

        JLabel projectLabel = new JLabel("Project: Untitled");
        projectLabel.setFont(projectLabel.getFont().deriveFont(Font.BOLD));

        JButton projectSettingsButton = new JButton("⚙");
        projectSettingsButton.setToolTipText("Project Settings");
        projectSettingsButton.setPreferredSize(new Dimension(25, 25));
        projectSettingsButton.addActionListener(e -> showProjectSettings());

        panel.add(projectLabel);
        panel.add(projectSettingsButton);

        return panel;
    }

    private void setupEvents() {
        addScreenButton.addActionListener(e -> showAddScreenDialog());
        removeScreenButton.addActionListener(e -> removeCurrentScreen());
        duplicateScreenButton.addActionListener(e -> duplicateCurrentScreen());

        screenTabs.addChangeListener(e -> {
            if (currentProject != null && screenTabs.getSelectedIndex() >= 0) {
                DesignScreen selectedScreen = currentProject.getScreens().get(screenTabs.getSelectedIndex());
                currentProject.setActiveScreen(selectedScreen);
                updateCanvasForScreen(selectedScreen);
            }
        });

        // Add right-click context menu for tabs
        screenTabs.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int tabIndex = screenTabs.indexAtLocation(e.getX(), e.getY());
                    if (tabIndex >= 0) {
                        screenTabs.setSelectedIndex(tabIndex);
                        showTabContextMenu(e.getComponent(), e.getX(), e.getY(), tabIndex);
                    }
                }
            }
        });
    }

    private void showAddScreenDialog() {
        AddScreenDialog dialog = new AddScreenDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        ScreenCreationData data = dialog.showDialog();

        if (data != null) {
            DesignScreen newScreen = currentProject.createNewScreen(data.name, data.type);
            newScreen.setDescription(data.description);

            // Apply screen type specific settings
            applyScreenTypeSettings(newScreen, data.type);

            refreshScreenTabs();

            // Switch to the new screen
            int newIndex = currentProject.getScreens().size() - 1;
            screenTabs.setSelectedIndex(newIndex);
        }
    }

    private void applyScreenTypeSettings(DesignScreen screen, ScreenType type) {
        // Apply default components based on screen type
        switch (type) {
            case LOGIN:
                // Add default login components
                addLoginComponents(screen);
                break;
            case SPLASH:
                // Add splash screen components
                addSplashComponents(screen);
                break;
            case DASHBOARD:
                // Add dashboard components
                addDashboardComponents(screen);
                break;
            // Add more default layouts as needed
        }
    }

    private void addLoginComponents(DesignScreen screen) {
        // Add a login panel with default components

    }

    private void addSplashComponents(DesignScreen screen) {
        // Add splash screen components (logo, progress bar, etc.)
        DesignComponent logoLabel = new DesignComponent(JLabel.class, 100, 50);
        logoLabel.setText("Application Logo");
        logoLabel.setSize(200, 50);
        screen.addComponent(logoLabel);

        DesignComponent progressBar = new DesignComponent(JProgressBar.class, 100, 150);
        progressBar.setSize(200, 25);
        screen.addComponent(progressBar);
    }

    private void addDashboardComponents(DesignScreen screen) {
        // Add dashboard components (charts, data grids, etc.)

    }

    private void removeCurrentScreen() {
        if (currentProject.getScreens().size() <= 1) {
            JOptionPane.showMessageDialog(this,
                    "Cannot remove the last screen. Projects must have at least one screen.",
                    "Cannot Remove Screen",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove the current screen?",
                "Remove Screen",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DesignScreen currentScreen = currentProject.getActiveScreen();
            currentProject.removeScreen(currentScreen);
            refreshScreenTabs();
        }
    }

    private void duplicateCurrentScreen() {
        DesignScreen currentScreen = currentProject.getActiveScreen();
        if (currentScreen != null) {
            DesignScreen newScreen = new DesignScreen(currentScreen.getName() + " Copy", currentScreen.getType());
            newScreen.setDescription(currentScreen.getDescription());

            // Copy all components
            for (DesignComponent comp : currentScreen.getComponents()) {
                // Create a copy of the component (simplified - you might want deep copy)
                DesignComponent newComp;
                newComp = new DesignComponent(comp.getComponentType(),
                        comp.getBounds().x + 20, comp.getBounds().y + 20);

                // Copy properties
                newComp.setText(comp.getText());
                newComp.setSize(comp.getBounds().width, comp.getBounds().height);
                newComp.setBackgroundColor(comp.getBackgroundColor());
                newComp.setVisible(comp.isVisible());
                newComp.setEnabled(comp.isEnabled());

                newScreen.addComponent(newComp);
            }

            // Copy screen settings
            for (Map.Entry<String, Object> entry : currentScreen.getScreenSettings().entrySet()) {
                newScreen.setScreenSetting(entry.getKey(), entry.getValue());
            }

            currentProject.addScreen(newScreen);
            refreshScreenTabs();

            // Switch to the new screen
            int newIndex = currentProject.getScreens().size() - 1;
            screenTabs.setSelectedIndex(newIndex);
        }
    }

    private void showTabContextMenu(Component component, int x, int y, int tabIndex) {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem renameItem = new JMenuItem("Rename Screen");
        renameItem.addActionListener(e -> renameScreen(tabIndex));

        JMenuItem duplicateItem = new JMenuItem("Duplicate Screen");
        duplicateItem.addActionListener(e -> {
            screenTabs.setSelectedIndex(tabIndex);
            duplicateCurrentScreen();
        });

        JMenuItem removeItem = new JMenuItem("Remove Screen");
        removeItem.addActionListener(e -> {
            screenTabs.setSelectedIndex(tabIndex);
            removeCurrentScreen();
        });
        removeItem.setEnabled(currentProject.getScreens().size() > 1);

        JMenuItem settingsItem = new JMenuItem("Screen Settings");
        settingsItem.addActionListener(e -> showScreenSettings(tabIndex));

        contextMenu.add(renameItem);
        contextMenu.add(duplicateItem);
        contextMenu.addSeparator();
        contextMenu.add(settingsItem);
        contextMenu.addSeparator();
        contextMenu.add(removeItem);

        contextMenu.show(component, x, y);
    }

    private void renameScreen(int tabIndex) {
        DesignScreen screen = currentProject.getScreens().get(tabIndex);
        String newName = JOptionPane.showInputDialog(this, "Enter new screen name:", screen.getName());

        if (newName != null && !newName.trim().isEmpty()) {
            screen.setName(newName.trim());
            refreshScreenTabs();
        }
    }

    private void showScreenSettings(int tabIndex) {
        DesignScreen screen = currentProject.getScreens().get(tabIndex);
        ScreenSettingsDialog dialog = new ScreenSettingsDialog((JFrame) SwingUtilities.getWindowAncestor(this), screen);
        dialog.setVisible(true);
    }

    private void showProjectSettings() {
        ProjectSettingsDialog dialog = new ProjectSettingsDialog((JFrame) SwingUtilities.getWindowAncestor(this), currentProject);
        dialog.setVisible(true);

        // Update project info after settings change
        refreshProjectInfo();
    }

    public void setDesignCanvas(DesignPanel canvas) {
        this.designCanvas = canvas;
    }

    public void selectScreen(int screenIndex) {
        if (screenIndex >= 0 && screenIndex < screenTabs.getTabCount()) {
            screenTabs.setSelectedIndex(screenIndex);
        }
    }

    public void selectScreenByName(String screenName) {
        if (currentProject != null) {
            for (int i = 0; i < currentProject.getScreens().size(); i++) {
                if (currentProject.getScreens().get(i).getName().equals(screenName)) {
                    selectScreen(i);
                    break;
                }
            }
        }
    }

    private void updateCanvasForScreen(DesignScreen screen) {
        if (designCanvas != null) {
            // Clear current canvas
            designCanvas.getDesignComponents().clear();

            // Add components from the selected screen
            for (DesignComponent comp : screen.getComponents()) {
                designCanvas.getDesignComponents().add(comp);
            }

            designCanvas.repaint();
        }
    }

    @Override
    public void projectChanged(DesignProject project) {
        this.currentProject = project;
        refreshScreenTabs();
        refreshProjectInfo();
    }

    private void refreshScreenTabs() {
        screenTabs.removeAll();

        if (currentProject != null) {
            for (int i = 0; i < currentProject.getScreens().size(); i++) {
                DesignScreen screen = currentProject.getScreens().get(i);

                // Create tab with screen type icon
                String tabTitle = screen.getName();
                String tooltip = screen.getType().getDescription();
                if (!screen.getDescription().isEmpty()) {
                    tooltip += ": " + screen.getDescription();
                }

                JPanel tabContent = new JPanel();
                screenTabs.addTab(tabTitle, tabContent);
                screenTabs.setToolTipTextAt(i, tooltip);

                // Set different colors for different screen types
                Color tabColor = getScreenTypeColor(screen.getType());
                screenTabs.setBackgroundAt(i, tabColor);
            }

            // Select active screen
            if (currentProject.getActiveScreen() != null) {
                int activeIndex = currentProject.getScreens().indexOf(currentProject.getActiveScreen());
                if (activeIndex >= 0) {
                    screenTabs.setSelectedIndex(activeIndex);
                    updateCanvasForScreen(currentProject.getActiveScreen());
                }
            }
        }

        // Update button states
        removeScreenButton.setEnabled(currentProject != null && currentProject.getScreens().size() > 1);
        duplicateScreenButton.setEnabled(currentProject != null && currentProject.getActiveScreen() != null);
    }

    private void refreshProjectInfo() {

    }

    private Color getScreenTypeColor(ScreenType type) {
        switch (type) {
            case MAIN: return new Color(240, 248, 255); // Light blue
            case DIALOG: return new Color(255, 248, 240); // Light orange
            case LOGIN: return new Color(240, 255, 240); // Light green
            case SPLASH: return new Color(255, 240, 255); // Light magenta
            case SETTINGS: return new Color(248, 248, 255); // Light purple
            case DASHBOARD: return new Color(255, 255, 240); // Light yellow
            default: return Color.WHITE;
        }
    }
}

/**
 * Data class for screen creation
 */
