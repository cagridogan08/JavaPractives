import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import Designer.*;
import Project.*;
/**
 * Professional IDE-style Screen Designer Application
 * Features dockable panels, toolbars, and professional layout similar to industrial design tools
 */
public class ScreenDesignerApp extends JFrame {
    private DesignPanel canvas;
    private ComponentPalette palette;
    private PropertyPanel propertyPanel;
    private ScreenTabsPanel screenTabsPanel;
    private JTree projectTree;
    private JTable propertyTable;
    private JPanel statusBar;

    public ScreenDesignerApp() {
        setTitle("Screen Designer Professional - Multi-Screen Projects");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setLocationRelativeTo(null);

        // Set professional look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeComponents();
        layoutComponents();
        setupMenuAndToolbars();
    }

    private void initializeComponents() {
        canvas = new DesignPanel();
        palette = new ComponentPalette();
        propertyPanel = new PropertyPanel();
        screenTabsPanel = new ScreenTabsPanel();

        // Wire up connections
        propertyPanel.setCanvas(canvas);
        canvas.setPropertyPanel(propertyPanel);
        screenTabsPanel.setDesignCanvas(canvas);

        // Create project tree
        createProjectTree();

        // Create status bar
        createStatusBar();

        // Initial tree update
        updateProjectTree();
    }

    private void createProjectTree() {
        // Create project tree similar to IDE project explorer
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Projects");
        DefaultMutableTreeNode currentProject = new DefaultMutableTreeNode("Untitled Project");

        DefaultMutableTreeNode screensNode = new DefaultMutableTreeNode("Screens");
        screensNode.add(new DefaultMutableTreeNode("Main Screen"));

        DefaultMutableTreeNode resourcesNode = new DefaultMutableTreeNode("Resources");
        resourcesNode.add(new DefaultMutableTreeNode("Images"));
        resourcesNode.add(new DefaultMutableTreeNode("Icons"));

        DefaultMutableTreeNode componentsNode = new DefaultMutableTreeNode("Custom Components");

        currentProject.add(screensNode);
        currentProject.add(resourcesNode);
        currentProject.add(componentsNode);
        root.add(currentProject);

        projectTree = new JTree(root);
        projectTree.setRootVisible(true);
        projectTree.expandRow(0);
        projectTree.expandRow(1);

        // Add tree selection listener for opening screens
        projectTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) projectTree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                handleTreeSelection(selectedNode);
            }
        });

        // Add double-click listener for better UX
        projectTree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) projectTree.getLastSelectedPathComponent();
                    if (selectedNode != null) {
                        handleTreeDoubleClick(selectedNode);
                    }
                }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showTreeContextMenu(e);
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showTreeContextMenu(e);
                }
            }
        });

        // Register for project changes to update tree
        ProjectManager.getInstance().addListener(project -> updateProjectTree());
    }

    private void handleTreeSelection(DefaultMutableTreeNode selectedNode) {
        Object userObject = selectedNode.getUserObject();
        if (userObject instanceof String) {
            String nodeName = (String) userObject;

            // Check if this is a screen node by looking at its parent
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
            if (parent != null && parent.getUserObject().toString().startsWith("Screens")) {
                // Just select the screen in the tree - don't open it yet
                // Opening happens on double-click for better UX
                projectTree.setSelectionPath(new javax.swing.tree.TreePath(selectedNode.getPath()));
            }
        }
    }

    private void handleTreeDoubleClick(DefaultMutableTreeNode selectedNode) {
        Object userObject = selectedNode.getUserObject();
        if (userObject instanceof String) {
            String nodeName = (String) userObject;

            // Handle different types of double-clicks
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
            if (parent != null) {
                String parentName = parent.getUserObject().toString();

                if (parentName.startsWith("Screens")) {
                    // Double-click on screen - OPEN the screen in tabs
                    String screenName = nodeName;
                    int parenIndex = screenName.indexOf(" (");
                    if (parenIndex > 0) {
                        screenName = screenName.substring(0, parenIndex);
                    }
                    openScreenInTabPanel(screenName);

                } else if (parentName.equals("Custom Components")) {
                    // Double-click on custom component - open component editor (future feature)
                    editCustomComponent(nodeName);

                } else if (parentName.equals("Resources")) {
                    // Double-click on resource folder - open resource manager (future feature)
                    openResourceManager(nodeName);
                }
            } else if (nodeName.startsWith("Screens")) {
                // Double-click on Screens folder - add new screen
                addNewScreen();
            } else if (nodeName.equals("Custom Components")) {
                // Double-click on Custom Components folder - create new component
                createCustomComponent();
            }
        }
    }

    private void openScreenInTabPanel(String screenName) {
        DesignProject project = ProjectManager.getInstance().getCurrentProject();
        if (project != null) {
            // Find the screen by name
            for (int i = 0; i < project.getScreens().size(); i++) {
                DesignScreen screen = project.getScreens().get(i);
                if (screen.getName().equals(screenName)) {
                    // Set as active screen
                    project.setActiveScreen(screen);

                    // Update the tab panel to show this screen
                    screenTabsPanel.selectScreen(i);

                    // Update the canvas to show this screen's components
                    canvas.getDesignComponents().clear();
                    for (DesignComponent comp : screen.getComponents()) {
                        canvas.getDesignComponents().add(comp);
                    }
                    canvas.repaint();

                    // Update status
                    updateStatusBar();
                    break;
                }
            }
        }
    }

    private void openScreenSettings(String screenName) {
        DesignProject project = ProjectManager.getInstance().getCurrentProject();
        if (project != null) {
            for (DesignScreen screen : project.getScreens()) {
                if (screen.getName().equals(screenName)) {
                    ScreenSettingsDialog dialog = new ScreenSettingsDialog(this, screen);
                    dialog.setVisible(true);
                    updateProjectTree(); // Refresh tree in case screen name changed
                    break;
                }
            }
        }
    }

    private void editCustomComponent(String componentName) {
        // Future feature: Open custom component editor
        JOptionPane.showMessageDialog(this,
                "Custom component editor for '" + componentName + "' not yet implemented.\n" +
                        "This will allow editing custom component properties and behavior.",
                "Feature Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void openResourceManager(String resourceType) {
        // Future feature: Open resource manager
        JOptionPane.showMessageDialog(this,
                "Resource manager for '" + resourceType + "' not yet implemented.\n" +
                        "This will allow managing images, icons, fonts, and other resources.",
                "Feature Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showTreeContextMenu(java.awt.event.MouseEvent e) {
        // Select the node at the mouse position
        int row = projectTree.getRowForLocation(e.getX(), e.getY());
        if (row >= 0) {
            projectTree.setSelectionRow(row);
        }

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) projectTree.getLastSelectedPathComponent();
        if (selectedNode == null) return;

        Object userObject = selectedNode.getUserObject();
        if (userObject instanceof String) {
            String nodeName = (String) userObject;
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();

            JPopupMenu contextMenu = new JPopupMenu();

            if (parent != null && parent.getUserObject().toString().startsWith("Screens")) {
                // Screen context menu
                String screenName = nodeName;
                int parenIndex = screenName.indexOf(" (");
                if (parenIndex > 0) {
                    screenName = screenName.substring(0, parenIndex);
                }

                JMenuItem openItem = new JMenuItem("Open Screen");
//                openItem.addActionListener(event -> openScreenInTabPanel(screenName));
                contextMenu.add(openItem);

                JMenuItem settingsItem = new JMenuItem("Screen Settings...");
                contextMenu.add(settingsItem);

                contextMenu.addSeparator();

                JMenuItem duplicateItem = new JMenuItem("Duplicate Screen");
                contextMenu.add(duplicateItem);

                JMenuItem renameItem = new JMenuItem("Rename Screen...");
                contextMenu.add(renameItem);

                contextMenu.addSeparator();

                JMenuItem removeItem = new JMenuItem("Remove Screen");
                contextMenu.add(removeItem);

            } else if (nodeName.startsWith("Screens")) {
                // Screens folder context menu
                JMenuItem addScreenItem = new JMenuItem("Add New Screen...");
                addScreenItem.addActionListener(event -> addNewScreen());
                contextMenu.add(addScreenItem);

                JMenuItem manageScreensItem = new JMenuItem("Manage Screens...");
                manageScreensItem.addActionListener(event -> manageScreens());
                contextMenu.add(manageScreensItem);

            } else if (nodeName.equals("Custom Components")) {
                // Custom Components folder context menu
                JMenuItem createComponentItem = new JMenuItem("Create Custom Component...");
                createComponentItem.addActionListener(event -> createCustomComponent());
                contextMenu.add(createComponentItem);

            } else if (parent != null && parent.getUserObject().toString().equals("Custom Components")) {
                // Custom component context menu
                JMenuItem editItem = new JMenuItem("Edit Component...");
                editItem.addActionListener(event -> editCustomComponent(nodeName));
                contextMenu.add(editItem);

                JMenuItem removeItem = new JMenuItem("Remove Component");
                removeItem.addActionListener(event -> removeCustomComponent(nodeName));
                contextMenu.add(removeItem);
            }

            if (contextMenu.getComponentCount() > 0) {
                contextMenu.show(projectTree, e.getX(), e.getY());
            }
        }
    }

    private void duplicateScreen(String screenName) {
        DesignProject project = ProjectManager.getInstance().getCurrentProject();
        if (project != null) {
            for (DesignScreen screen : project.getScreens()) {
                if (screen.getName().equals(screenName)) {
                    DesignScreen newScreen = new DesignScreen(screen.getName() + " Copy", screen.getType());
                    newScreen.setDescription(screen.getDescription());

                    // Copy components (simplified)
                    for (DesignComponent comp : screen.getComponents()) {
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

                    project.addScreen(newScreen);
                    ProjectManager.getInstance().loadProject(project);
                    break;
                }
            }
        }
    }

    private void renameScreen(String screenName) {
        String newName = JOptionPane.showInputDialog(this, "Enter new screen name:", screenName);
        if (newName != null && !newName.trim().isEmpty()) {
            DesignProject project = ProjectManager.getInstance().getCurrentProject();
            if (project != null) {
                for (DesignScreen screen : project.getScreens()) {
                    if (screen.getName().equals(screenName)) {
                        screen.setName(newName.trim());
                        ProjectManager.getInstance().loadProject(project);
                        break;
                    }
                }
            }
        }
    }

    private void removeScreen(String screenName) {
        DesignProject project = ProjectManager.getInstance().getCurrentProject();
        if (project != null && project.getScreens().size() > 1) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove the screen '" + screenName + "'?\n" +
                            "This action cannot be undone.",
                    "Remove Screen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                for (DesignScreen screen : project.getScreens()) {
                    if (screen.getName().equals(screenName)) {
                        project.removeScreen(screen);
                        ProjectManager.getInstance().loadProject(project);
                        break;
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Cannot remove the last screen. Projects must have at least one screen.",
                    "Cannot Remove Screen",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createCustomComponent() {
//        CustomComponentCreator creator = new CustomComponentCreator(this);
//        CustomComponentDefinition newComponent = creator.showDialog();
//
//        if (newComponent != null) {
//            CustomComponentManager.getInstance().addCustomComponent(newComponent);
//            updateProjectTree();
//            JOptionPane.showMessageDialog(this,
//                    "Custom component '" + newComponent.getDisplayName() + "' created successfully!",
//                    "Success",
//                    JOptionPane.INFORMATION_MESSAGE);
//        }
    }

    private void removeCustomComponent(String componentName) {
//        int result = JOptionPane.showConfirmDialog(this,
//                "Are you sure you want to remove the custom component '" + componentName + "'?\n" +
//                        "This action cannot be undone.",
//                "Remove Custom Component",
//                JOptionPane.YES_NO_OPTION,
//                JOptionPane.WARNING_MESSAGE);
//
//        if (result == JOptionPane.YES_OPTION) {
//            CustomComponentManager.getInstance().removeCustomComponent(componentName);
//            updateProjectTree();
//        }
    }

    private void updateProjectTree() {
        SwingUtilities.invokeLater(() -> {
            DefaultTreeModel treeModel = (DefaultTreeModel) projectTree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

            // Clear existing project nodes
            root.removeAllChildren();

            // Get current project
            DesignProject currentProject = ProjectManager.getInstance().getCurrentProject();
            if (currentProject != null) {
                DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(currentProject.getName());

                // Add Screens node
                DefaultMutableTreeNode screensNode = new DefaultMutableTreeNode("Screens (" + currentProject.getScreens().size() + ")");
                for (DesignScreen screen : currentProject.getScreens()) {
                    DefaultMutableTreeNode screenNode = new DefaultMutableTreeNode(
                            screen.getName() + " (" + screen.getType().getDisplayName() + ")"
                    );
                    screensNode.add(screenNode);
                }
                projectNode.add(screensNode);

                // Add Resources node
                DefaultMutableTreeNode resourcesNode = new DefaultMutableTreeNode("Resources");
                resourcesNode.add(new DefaultMutableTreeNode("Images"));
                resourcesNode.add(new DefaultMutableTreeNode("Icons"));
                resourcesNode.add(new DefaultMutableTreeNode("Fonts"));
                projectNode.add(resourcesNode);

                // Add Custom Components node
                DefaultMutableTreeNode componentsNode = new DefaultMutableTreeNode("Custom Components");
                // Add custom components from CustomComponentManager
//                for (String componentName : CustomComponentManager.getInstance().getAllCustomComponents().keySet()) {
//                    componentsNode.add(new DefaultMutableTreeNode(componentName));
//                }
                projectNode.add(componentsNode);

                root.add(projectNode);

                // Notify tree model of changes
                treeModel.reload();

                // Expand the project and screens nodes
                projectTree.expandRow(0); // Root
                projectTree.expandRow(1); // Project
                projectTree.expandRow(2); // Screens
            }

            // Update status bar project name
            updateStatusBar();
        });
    }

    private void updateStatusBar() {
        DesignProject currentProject = ProjectManager.getInstance().getCurrentProject();
        if (currentProject != null) {
            // Update left status
            JPanel leftStatus = (JPanel) statusBar.getComponent(0);
            Component[] components = leftStatus.getComponents();

            // Find and update project name label (should be at index 4: Ready | Project: Name)
            if (components.length > 4 && components[4] instanceof JLabel) {
                ((JLabel) components[4]).setText("Project: " + currentProject.getName());
            }
        }
    }

    private void createStatusBar() {
        statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusBar.setPreferredSize(new Dimension(0, 25));

        // Left status info
        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        leftStatus.add(new JLabel("Ready"));
        leftStatus.add(createStatusSeparator());
        leftStatus.add(new JLabel("Project: Untitled"));

        // Right status info  
        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        rightStatus.add(new JLabel("Selection Mode"));
        rightStatus.add(createStatusSeparator());
        rightStatus.add(new JLabel("100%"));
        rightStatus.add(createStatusSeparator());
        rightStatus.add(new JLabel("0,0"));

        statusBar.add(leftStatus, BorderLayout.WEST);
        statusBar.add(rightStatus, BorderLayout.EAST);
    }

    private JLabel createStatusSeparator() {
        JLabel separator = new JLabel("|");
        separator.setForeground(Color.GRAY);
        return separator;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Create main split pane layout
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(250);
        mainSplitPane.setResizeWeight(0.0);

        // Left panel - Project Explorer
        JPanel leftPanel = createLeftPanel();
        mainSplitPane.setLeftComponent(leftPanel);

        // Center-Right panel
        JSplitPane centerRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerRightSplitPane.setDividerLocation(1100);
        centerRightSplitPane.setResizeWeight(1.0);

        // Center panel - Main design area
        JPanel centerPanel = createCenterPanel();
        centerRightSplitPane.setLeftComponent(centerPanel);

        // Right panel - Properties and tools
        JPanel rightPanel = createRightPanel();
        centerRightSplitPane.setRightComponent(rightPanel);

        mainSplitPane.setRightComponent(centerRightSplitPane);

        add(mainSplitPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());

        // Create tabbed pane for left side
        JTabbedPane leftTabs = new JTabbedPane(JTabbedPane.BOTTOM);

        // Project View tab
        JScrollPane projectScroll = new JScrollPane(projectTree);
        projectScroll.setBorder(BorderFactory.createTitledBorder("Project View"));
        leftTabs.addTab("Project", projectScroll);

        // Component Palette tab
        JScrollPane paletteScroll = new JScrollPane(palette);
        paletteScroll.setBorder(BorderFactory.createTitledBorder("Component Palette"));
        leftTabs.addTab("Components", paletteScroll);

        leftPanel.add(leftTabs, BorderLayout.CENTER);

        return leftPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Create main toolbar
        JPanel mainToolbar = createMainToolbar();
        centerPanel.add(mainToolbar, BorderLayout.NORTH);

        // Create design area with tabs
        JPanel designArea = new JPanel(new BorderLayout());

        // Screen tabs
        designArea.add(screenTabsPanel, BorderLayout.NORTH);

        // Canvas with scroll
        JScrollPane canvasScroll = new JScrollPane(canvas);
        canvasScroll.setBorder(BorderFactory.createLoweredBevelBorder());
        canvasScroll.setBackground(Color.DARK_GRAY);
        designArea.add(canvasScroll, BorderLayout.CENTER);

        // Mode and zoom toolbar
        JPanel bottomToolbar = createModeToolbar();
        designArea.add(bottomToolbar, BorderLayout.SOUTH);

        centerPanel.add(designArea, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Create tabbed pane for right side
        JTabbedPane rightTabs = new JTabbedPane(JTabbedPane.BOTTOM);

        // Properties tab
        JScrollPane propScroll = new JScrollPane(propertyPanel);
        propScroll.setBorder(BorderFactory.createTitledBorder("Properties"));
        rightTabs.addTab("Properties", propScroll);

        // Events tab (placeholder)
        JPanel eventsPanel = new JPanel();
        eventsPanel.add(new JLabel("Event handlers will be shown here"));
        rightTabs.addTab("Events", eventsPanel);

        // Structure tab (placeholder)
        JPanel structurePanel = new JPanel();
        structurePanel.add(new JLabel("Component hierarchy will be shown here"));
        rightTabs.addTab("Structure", structurePanel);

        rightPanel.add(rightTabs, BorderLayout.CENTER);

        return rightPanel;
    }

    private JPanel createMainToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        toolbar.setBorder(BorderFactory.createRaisedBevelBorder());

        // File operations
        toolbar.add(createToolbarButton("📄", "New Project", e -> createNewProject()));
        toolbar.add(createToolbarButton("📁", "Open Project", e -> openProject()));
        toolbar.add(createToolbarButton("💾", "Save Project", e -> saveProject()));
        toolbar.add(createToolbarSeparator());

        // Edit operations
        toolbar.add(createToolbarButton("↶", "Undo", e -> {}));
        toolbar.add(createToolbarButton("↷", "Redo", e -> {}));
        toolbar.add(createToolbarSeparator());

        // Component operations
        toolbar.add(createToolbarButton("📋", "Copy", e -> {}));
        toolbar.add(createToolbarButton("📄", "Paste", e -> {}));
        toolbar.add(createToolbarButton("🗑️", "Delete", e -> {}));
        toolbar.add(createToolbarSeparator());

        // Alignment tools
        toolbar.add(createToolbarButton("⬅️", "Align Left", e -> {}));
        toolbar.add(createToolbarButton("➡️", "Align Right", e -> {}));
        toolbar.add(createToolbarButton("⬆️", "Align Top", e -> {}));
        toolbar.add(createToolbarButton("⬇️", "Align Bottom", e -> {}));
        toolbar.add(createToolbarSeparator());

        // Preview and generate
        toolbar.add(createToolbarButton("👁️", "Preview", e -> previewForm()));
        toolbar.add(createToolbarButton("⚙️", "Generate Code", e -> generateCode()));

        return toolbar;
    }

    private JPanel createModeToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBorder(BorderFactory.createRaisedBevelBorder());

        // Left side - mode controls
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));

        // Mode toggle buttons
        JToggleButton selectionModeButton = new JToggleButton("🔲");
        JToggleButton panModeButton = new JToggleButton("✋");

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(selectionModeButton);
        modeGroup.add(panModeButton);

        selectionModeButton.setSelected(true);
        selectionModeButton.setToolTipText("Selection Mode (Space)");
        panModeButton.setToolTipText("Pan Mode (Space)");

        selectionModeButton.addActionListener(e -> {
            if (selectionModeButton.isSelected()) {
                canvas.setInteractionMode(DesignPanel.InteractionMode.SELECTION);
            }
        });

        panModeButton.addActionListener(e -> {
            if (panModeButton.isSelected()) {
                canvas.setInteractionMode(DesignPanel.InteractionMode.PAN);
            }
        });

        // Listen for mode changes from canvas
        canvas.setModeChangeListener(newMode -> {
            SwingUtilities.invokeLater(() -> {
                if (newMode == DesignPanel.InteractionMode.SELECTION) {
                    selectionModeButton.setSelected(true);
                } else {
                    panModeButton.setSelected(true);
                }
            });
        });

        leftControls.add(new JLabel("Mode:"));
        leftControls.add(selectionModeButton);
        leftControls.add(panModeButton);

        // Center - zoom controls
        JPanel centerControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));

        JButton zoomOutButton = new JButton("-");
        JLabel zoomLabel = new JLabel("100%");
        JButton zoomInButton = new JButton("+");
        JButton fitButton = new JButton("Fit");

        zoomOutButton.setPreferredSize(new Dimension(25, 25));
        zoomInButton.setPreferredSize(new Dimension(25, 25));
        fitButton.setPreferredSize(new Dimension(40, 25));
        zoomLabel.setPreferredSize(new Dimension(40, 25));
        zoomLabel.setHorizontalAlignment(JLabel.CENTER);
        zoomLabel.setBorder(BorderFactory.createLoweredBevelBorder());

        zoomOutButton.addActionListener(e -> canvas.zoomOut());
        zoomInButton.addActionListener(e -> canvas.zoomIn());
        fitButton.addActionListener(e -> fitCanvasToWindow());

        // Update zoom label
        Timer zoomUpdateTimer = new Timer(100, e -> {
            int zoomPercent = (int) Math.round(canvas.getZoomFactor() * 100);
            zoomLabel.setText(zoomPercent + "%");
        });
        zoomUpdateTimer.start();

        centerControls.add(new JLabel("Zoom:"));
        centerControls.add(zoomOutButton);
        centerControls.add(zoomLabel);
        centerControls.add(zoomInButton);
        centerControls.add(fitButton);

        // Right side - coordinates and info
        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        rightControls.add(new JLabel("Grid: 10px"));
        rightControls.add(new JLabel("|"));
        rightControls.add(new JLabel("Snap: On"));

        toolbar.add(leftControls, BorderLayout.WEST);
        toolbar.add(centerControls, BorderLayout.CENTER);
        toolbar.add(rightControls, BorderLayout.EAST);

        return toolbar;
    }

    private JButton createToolbarButton(String text, String tooltip, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(28, 28));
        button.setMargin(new Insets(2, 2, 2, 2));
        button.addActionListener(action);
        return button;
    }

    private JPanel createToolbarSeparator() {
        JPanel separator = new JPanel();
        separator.setPreferredSize(new Dimension(8, 28));
        separator.setBorder(BorderFactory.createEtchedBorder());
        return separator;
    }

    private void setupMenuAndToolbars() {
        setJMenuBar(createProfessionalMenuBar());
    }

    private JMenuBar createProfessionalMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("New Project", "Ctrl+N", e -> createNewProject()));
        fileMenu.add(createMenuItem("Open Project...", "Ctrl+O", e -> openProject()));
        fileMenu.add(createMenuItem("Save Project", "Ctrl+S", e -> saveProject()));
        fileMenu.add(createMenuItem("Save Project As...", "Ctrl+Shift+S", e -> saveProjectAs()));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Import...", null, e -> {}));
        fileMenu.add(createMenuItem("Export...", null, e -> {}));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Recent Projects", null, e -> {}));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", "Alt+F4", e -> System.exit(0)));

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.add(createMenuItem("Undo", "Ctrl+Z", e -> {}));
        editMenu.add(createMenuItem("Redo", "Ctrl+Y", e -> {}));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("Cut", "Ctrl+X", e -> {}));
        editMenu.add(createMenuItem("Copy", "Ctrl+C", e -> {}));
        editMenu.add(createMenuItem("Paste", "Ctrl+V", e -> {}));
        editMenu.add(createMenuItem("Delete", "Del", e -> {}));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("Select All", "Ctrl+A", e -> {}));
        editMenu.add(createMenuItem("Find...", "Ctrl+F", e -> {}));

        // View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(createMenuItem("Zoom In", "Ctrl++", e -> canvas.zoomIn()));
        viewMenu.add(createMenuItem("Zoom Out", "Ctrl+-", e -> canvas.zoomOut()));
        viewMenu.add(createMenuItem("Zoom to Fit", "Ctrl+0", e -> fitCanvasToWindow()));
        viewMenu.add(createMenuItem("Actual Size", "Ctrl+1", e -> canvas.resetZoom()));
        viewMenu.addSeparator();
     
        viewMenu.addSeparator();
        viewMenu.add(createCheckMenuItem("Project View", true, e -> {}));
        viewMenu.add(createCheckMenuItem("Properties Panel", true, e -> {}));
        viewMenu.add(createCheckMenuItem("Component Palette", true, e -> {}));

        JCheckBoxMenuItem showGridItem = createCheckMenuItem("Show Grid", canvas.isShowGrid(), e -> {
            canvas.setShowGrid(((JCheckBoxMenuItem) e.getSource()).isSelected());
            updateToolbarGridInfo();
        });
        viewMenu.add(showGridItem);

        JCheckBoxMenuItem snapToGridItem = createCheckMenuItem("Snap to Grid", canvas.isSnapToGrid(), e -> {
            canvas.setSnapToGrid(((JCheckBoxMenuItem) e.getSource()).isSelected());
            updateToolbarGridInfo();
        });
        viewMenu.add(snapToGridItem);

        JCheckBoxMenuItem showRulersItem = createCheckMenuItem("Show Rulers", canvas.isShowRulers(), e -> {
            canvas.setShowRulers(((JCheckBoxMenuItem) e.getSource()).isSelected());
        });
        // Project Menu
        JMenu projectMenu = new JMenu("Project");
        projectMenu.add(createMenuItem("Project Settings...", null, e -> showProjectSettings()));
        projectMenu.addSeparator();
        projectMenu.add(createMenuItem("Add Screen...", "Ctrl+Shift+N", e -> addNewScreen()));
        projectMenu.add(createMenuItem("Manage Screens...", null, e -> manageScreens()));
        projectMenu.addSeparator();
        projectMenu.add(createMenuItem("Build Project", "F7", e -> {}));
        projectMenu.add(createMenuItem("Clean Project", null, e -> {}));

        // Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.add(createMenuItem("Generate Code...", "F5", e -> generateCode()));
        toolsMenu.add(createMenuItem("Preview Form", "F6", e -> previewForm()));
        toolsMenu.addSeparator();
        toolsMenu.add(createMenuItem("Custom Components...", null, e -> {}));
        toolsMenu.add(createMenuItem("Import Components...", null, e -> {}));
        toolsMenu.addSeparator();
        toolsMenu.add(createMenuItem("Options...", null, e -> {}));

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(createMenuItem("User Guide", "F1", e -> {}));
        helpMenu.add(createMenuItem("Keyboard Shortcuts", null, e -> {}));
        helpMenu.addSeparator();
        helpMenu.add(createMenuItem("Check for Updates...", null, e -> {}));
        helpMenu.add(createMenuItem("About Screen Designer", null, e -> showAboutDialog()));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(projectMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void updateToolbarGridInfo() {
        // Find the mode toolbar and update grid info
        SwingUtilities.invokeLater(() -> {
            JSplitPane mainSplit = (JSplitPane) getContentPane().getComponent(0);
            JSplitPane centerRightSplit = (JSplitPane) mainSplit.getRightComponent();
            JPanel designPanel = (JPanel) centerRightSplit.getLeftComponent();
            JPanel toolbar = (JPanel) designPanel.getComponent(2); // Mode toolbar is at index 2

            JPanel rightControls = (JPanel) toolbar.getClientProperty("rightControls");
            if (rightControls != null) {
                JLabel gridSizeLabel = (JLabel) rightControls.getClientProperty("gridSizeLabel");
                JLabel snapLabel = (JLabel) rightControls.getClientProperty("snapLabel");

                if (gridSizeLabel != null) {
                    gridSizeLabel.setText("Grid: " + canvas.getGridSize() + "px");
                }
                if (snapLabel != null) {
                    snapLabel.setText("Snap: " + (canvas.isSnapToGrid() ? "On" : "Off"));
                }
            }
        });
    }

    private JMenuItem createMenuItem(String text, String accelerator, java.awt.event.ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        if (accelerator != null) {
            item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
        }
        item.addActionListener(action);
        return item;
    }

    private JCheckBoxMenuItem createCheckMenuItem(String text, boolean selected, java.awt.event.ActionListener action) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(text, selected);
        item.addActionListener(action);
        return item;
    }

    private void fitCanvasToWindow() {
        JScrollPane scrollPane = (JScrollPane) canvas.getParent().getParent();
        Dimension viewportSize = scrollPane.getViewport().getSize();
        Dimension canvasSize = new Dimension(800, 600);

        double zoomX = (double) viewportSize.width / canvasSize.width;
        double zoomY = (double) viewportSize.height / canvasSize.height;
        double fitZoom = Math.min(zoomX, zoomY) * 0.9;

        canvas.setZoomFactor(fitZoom);
        canvas.resetPan();
    }

    // Project management methods (simplified)
    private void createNewProject() {
        int result = JOptionPane.showConfirmDialog(this,
                "Create a new project? Unsaved changes will be lost.",
                "New Project",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            ProjectManager.getInstance().createNewProject();
            setTitle("Screen Designer Professional - " + ProjectManager.getInstance().getCurrentProject().getName());
            updateProjectTree(); // Ensure tree is updated
        }
    }

    private void openProject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Screen Designer Projects (*.sdp)", "sdp"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, "Project loading not yet implemented.");
        }
    }

    private void saveProject() {
        JOptionPane.showMessageDialog(this, "Project saving not yet implemented.");
    }

    private void saveProjectAs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Screen Designer Projects (*.sdp)", "sdp"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, "Project saving not yet implemented.");
        }
    }

    private void showProjectSettings() {
        ProjectSettingsDialog dialog = new ProjectSettingsDialog(this, ProjectManager.getInstance().getCurrentProject());
        dialog.setVisible(true);

        // Update title and tree after settings change
        setTitle("Screen Designer Professional - " + ProjectManager.getInstance().getCurrentProject().getName());
        updateProjectTree();
    }

    private void addNewScreen() {
        AddScreenDialog dialog = new AddScreenDialog(this);
        ScreenCreationData data = dialog.showDialog();
        if (data != null) {
            DesignProject project = ProjectManager.getInstance().getCurrentProject();
            DesignScreen newScreen = project.createNewScreen(data.name, data.type);
            newScreen.setDescription(data.description);

            // Trigger project change notification which will update tree
            ProjectManager.getInstance().loadProject(project);
        }
    }

    private void manageScreens() {
        ScreenManagementDialog dialog = new ScreenManagementDialog(this, ProjectManager.getInstance().getCurrentProject());
        dialog.setVisible(true);

        // Update tree after potential screen changes
        updateProjectTree();
    }

    private void generateCode() {
        CodeGenerator generator = new CodeGenerator();
        String code = generator.generateCode(canvas.getDesignComponents());
        CodeViewDialog codeDialog = new CodeViewDialog(this, code);
        codeDialog.setVisible(true);
    }

    private void previewForm() {
        FormPreview preview = new FormPreview();
        JFrame previewFrame = preview.createPreviewFrame(canvas.getDesignComponents());
        previewFrame.setLocationRelativeTo(this);
        previewFrame.setVisible(true);
    }

    private void showAboutDialog() {
        String aboutText = "<html><center>" +
                "<h2>Screen Designer Professional</h2>" +
                "<p>Multi-Screen GUI Design Tool</p>" +
                "<p>Version 1.0 Professional Edition</p>" +
                "<br>" +
                "<p>Professional IDE-style interface for GUI design</p>" +
                "</center></html>";
        JOptionPane.showMessageDialog(this, aboutText, "About Screen Designer", JOptionPane.INFORMATION_MESSAGE);
    }

}