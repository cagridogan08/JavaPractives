import Designer.CodeGenerator;
import Designer.CodeViewDialog;
import Designer.*;
import Project.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * Professional IDE-style Screen Designer Application
 * Features dockable panels, toolbars, and professional layout similar to industrial design tools
 */
public class DesignerWinccStyle extends JFrame {
    private DesignPanel canvas;
    private ComponentPalette palette;
    private PropertyPanel propertyPanel;
    private ScreenTabsPanel screenTabsPanel;
    private JTree projectTree;
    private JTable propertyTable;
    private JPanel statusBar;

    public DesignerWinccStyle() {
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
        canvasScroll.setBorder(BorderFactory.createEmptyBorder());
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
        toolbar.setBorder(BorderFactory.createTitledBorder("Main Toolbar"));

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
        toolbar.setBorder(BorderFactory.createLoweredBevelBorder());

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
        zoomLabel.setBorder(BorderFactory.createEmptyBorder());

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
        viewMenu.add(createCheckMenuItem("Show Grid", true, e -> {}));
        viewMenu.add(createCheckMenuItem("Snap to Grid", true, e -> {}));
        viewMenu.add(createCheckMenuItem("Show Rulers", false, e -> {}));
        viewMenu.addSeparator();
        viewMenu.add(createCheckMenuItem("Project View", true, e -> {}));
        viewMenu.add(createCheckMenuItem("Properties Panel", true, e -> {}));
        viewMenu.add(createCheckMenuItem("Component Palette", true, e -> {}));

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
        ProjectManager.getInstance().createNewProject();
        setTitle("Screen Designer Professional - " + ProjectManager.getInstance().getCurrentProject().getName());
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
        setTitle("Screen Designer Professional - " + ProjectManager.getInstance().getCurrentProject().getName());
    }

    private void addNewScreen() {
        AddScreenDialog dialog = new AddScreenDialog(this);
        ScreenCreationData data = dialog.showDialog();
        if (data != null) {
            DesignProject project = ProjectManager.getInstance().getCurrentProject();
            DesignScreen newScreen = project.createNewScreen(data.name, data.type);
            newScreen.setDescription(data.description);
            ProjectManager.getInstance().loadProject(project);
        }
    }

    private void manageScreens() {
        ScreenManagementDialog dialog = new ScreenManagementDialog(this, ProjectManager.getInstance().getCurrentProject());
        dialog.setVisible(true);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ScreenDesignerApp().setVisible(true);
        });
    }
}