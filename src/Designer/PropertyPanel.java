package Designer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Dynamic property panel with real-time updates and event handling
 * Provides comprehensive property editing with immediate visual feedback
 */
public class PropertyPanel extends JPanel {
    private JPanel propertyContainer;
    private DesignComponent currentComponent;
    private DesignPanel canvas;

    public PropertyPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Properties");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        add(titleLabel, BorderLayout.NORTH);

        propertyContainer = new JPanel();
        propertyContainer.setLayout(new GridBagLayout());

        JScrollPane scrollPane = new JScrollPane(propertyContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        showNoSelectionMessage();
    }

    public void setCanvas(DesignPanel canvas) {
        this.canvas = canvas;
    }

    public void updateProperties(DesignComponent component) {
        this.currentComponent = component;
        propertyContainer.removeAll();

        if (component == null) {
            showNoSelectionMessage();
        } else {
            showComponentProperties(component);
        }

        propertyContainer.revalidate();
        propertyContainer.repaint();
    }

    public void refreshProperties() {
        if (currentComponent != null) {
            updateProperties(currentComponent);
        }
    }

    public void clearSelection() {
        updateProperties(null);
    }

    private void showNoSelectionMessage() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 0, 0);

        JLabel noSelectionLabel = new JLabel("<html><center><b>Select a component</b><br>to edit properties</center></html>");
        noSelectionLabel.setForeground(Color.GRAY);
        propertyContainer.add(noSelectionLabel, gbc);
    }

    private void showComponentProperties(DesignComponent component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Component Type (read-only with icon)
        JLabel typeLabel = new JLabel(component.getComponentType().getSimpleName());
        typeLabel.setFont(typeLabel.getFont().deriveFont(Font.BOLD));
        typeLabel.setForeground(new Color(0, 100, 0));
        addPropertyRow("Type:", typeLabel, gbc, row++);

        // Add separator
        addSeparator(gbc, row++);

        // Text Property - Dynamic with real-time updates
        addTextProperty(component, gbc, row++);

        // Add separator
        addSeparator(gbc, row++);

        // Position and Size Properties - Dynamic with range sliders
        addPositionProperties(component, gbc, row);
        row += 4;

        // Add separator
        addSeparator(gbc, row++);

        // Appearance Properties
        addAppearanceProperties(component, gbc, row);
        row += 3;

        // Add separator
        addSeparator(gbc, row++);

        // Component-specific properties
        addComponentSpecificProperties(component, gbc, row);
        row += getComponentSpecificPropertyCount(component);

        // Add separator
        addSeparator(gbc, row++);

        // Event Properties
        addEventProperties(component, gbc, row);
    }

    private void addTextProperty(DesignComponent component, GridBagConstraints gbc, int row) {
        JTextField textField = new JTextField(component.getText());
        textField.setToolTipText("Enter component text - updates in real-time");

        // Real-time text updates
        addRealTimeTextListener(textField, component);

        // Style the text field
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));

        addPropertyRow("Text:", textField, gbc, row);
    }

    private void addRealTimeTextListener(JTextField textField, DesignComponent component) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateText(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateText(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateText(); }

            private void updateText() {
                SwingUtilities.invokeLater(() -> {
                    component.setText(textField.getText());
                    canvas.repaint();
                });
            }
        });
    }

    private void addPositionProperties(DesignComponent component, GridBagConstraints gbc, int startRow) {
        Rectangle bounds = component.getBounds();

        // X Position with slider
        JSlider xSlider = new JSlider(0, 800, bounds.x);
        JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(bounds.x, 0, 2000, 1));
        addDynamicPositionProperty("X:", xSlider, xSpinner, component, true, gbc, startRow);

        // Y Position with slider
        JSlider ySlider = new JSlider(0, 600, bounds.y);
        JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(bounds.y, 0, 2000, 1));
        addDynamicPositionProperty("Y:", ySlider, ySpinner, component, false, gbc, startRow + 1);

        // Width with slider
        JSlider widthSlider = new JSlider(20, 400, bounds.width);
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(bounds.width, 10, 1000, 1));
        addDynamicSizeProperty("Width:", widthSlider, widthSpinner, component, true, gbc, startRow + 2);

        // Height with slider
        JSlider heightSlider = new JSlider(20, 400, bounds.height);
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(bounds.height, 10, 1000, 1));
        addDynamicSizeProperty("Height:", heightSlider, heightSpinner, component, false, gbc, startRow + 3);
    }

    private void addDynamicPositionProperty(String label, JSlider slider, JSpinner spinner,
                                            DesignComponent component, boolean isX,
                                            GridBagConstraints gbc, int row) {
        JPanel controlPanel = new JPanel(new BorderLayout(5, 0));
        controlPanel.add(slider, BorderLayout.CENTER);
        controlPanel.add(spinner, BorderLayout.EAST);

        // Sync slider and spinner
        slider.addChangeListener(e -> {
            if (!slider.getValueIsAdjusting()) {
                spinner.setValue(slider.getValue());
                updatePosition(component, isX, slider.getValue());
            }
        });

        spinner.addChangeListener(e -> {
            int value = (Integer) spinner.getValue();
            slider.setValue(Math.min(value, slider.getMaximum()));
            updatePosition(component, isX, value);
        });

        addPropertyRow(label, controlPanel, gbc, row);
    }

    private void addDynamicSizeProperty(String label, JSlider slider, JSpinner spinner,
                                        DesignComponent component, boolean isWidth,
                                        GridBagConstraints gbc, int row) {
        JPanel controlPanel = new JPanel(new BorderLayout(5, 0));
        controlPanel.add(slider, BorderLayout.CENTER);
        controlPanel.add(spinner, BorderLayout.EAST);

        // Sync slider and spinner
        slider.addChangeListener(e -> {
            if (!slider.getValueIsAdjusting()) {
                spinner.setValue(slider.getValue());
                updateSize(component, isWidth, slider.getValue());
            }
        });

        spinner.addChangeListener(e -> {
            int value = (Integer) spinner.getValue();
            slider.setValue(Math.min(value, slider.getMaximum()));
            updateSize(component, isWidth, value);
        });

        addPropertyRow(label, controlPanel, gbc, row);
    }

    private void updatePosition(DesignComponent component, boolean isX, int value) {
        Rectangle bounds = component.getBounds();
        if (isX) {
            component.setLocation(value, bounds.y);
        } else {
            component.setLocation(bounds.x, value);
        }
        canvas.repaint();
    }

    private void updateSize(DesignComponent component, boolean isWidth, int value) {
        Rectangle bounds = component.getBounds();
        if (isWidth) {
            component.setSize(value, bounds.height);
        } else {
            component.setSize(bounds.width, value);
        }
        canvas.repaint();
    }

    private void addAppearanceProperties(DesignComponent component, GridBagConstraints gbc, int startRow) {
        // Background Color with preview
        JPanel colorPanel = new JPanel(new BorderLayout(5, 0));
        JButton colorButton = new JButton("Choose");
        colorButton.setPreferredSize(new Dimension(70, 25));

        JPanel colorPreview = new JPanel();
        colorPreview.setBackground(component.getBackgroundColor());
        colorPreview.setPreferredSize(new Dimension(40, 25));
        colorPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        colorPanel.add(colorPreview, BorderLayout.WEST);
        colorPanel.add(colorButton, BorderLayout.CENTER);

        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Background Color", component.getBackgroundColor());
            if (newColor != null) {
                component.setBackgroundColor(newColor);
                colorPreview.setBackground(newColor);
                canvas.repaint();
            }
        });

        addPropertyRow("Background:", colorPanel, gbc, startRow);

        // Visible Property with enhanced checkbox
        JCheckBox visibleCheckBox = new JCheckBox("", component.isVisible());
        visibleCheckBox.addActionListener(e -> {
            component.setVisible(visibleCheckBox.isSelected());
            canvas.repaint();
        });
        addPropertyRow("Visible:", visibleCheckBox, gbc, startRow + 1);

        // Enabled Property
        JCheckBox enabledCheckBox = new JCheckBox("", component.isEnabled());
        enabledCheckBox.addActionListener(e -> {
            component.setEnabled(enabledCheckBox.isSelected());
            canvas.repaint();
        });
        addPropertyRow("Enabled:", enabledCheckBox, gbc, startRow + 2);
    }

    private void addEventProperties(DesignComponent component, GridBagConstraints gbc, int startRow) {
        int row = startRow;

        // Events section header
        JLabel eventsLabel = new JLabel("Events");
        eventsLabel.setFont(eventsLabel.getFont().deriveFont(Font.BOLD));
        eventsLabel.setForeground(new Color(0, 0, 150));
        addPropertyRow("", eventsLabel, gbc, row++);

        // Click Event
        JTextField clickEventField = new JTextField("onClick");
        clickEventField.setToolTipText("Method name for click event");
        clickEventField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateEvent(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateEvent(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateEvent(); }

            private void updateEvent() {
                // Store event handler name in component
                SwingUtilities.invokeLater(() -> {
                    // You could extend DesignComponent to store event handlers
                    canvas.repaint();
                });
            }
        });
        addPropertyRow("Click Event:", clickEventField, gbc, row++);

        // Focus Event (for text components)
        if (component.getComponentType() == JTextField.class ||
                component.getComponentType() == JTextArea.class) {
            JTextField focusEventField = new JTextField("onFocus");
            focusEventField.setToolTipText("Method name for focus event");
            addPropertyRow("Focus Event:", focusEventField, gbc, row++);
        }

        // Selection Event (for checkboxes, lists, etc.)
        if (component.getComponentType() == JCheckBox.class ||
                component.getComponentType() == JComboBox.class ||
                component.getComponentType() == JList.class) {
            JTextField selectionEventField = new JTextField("onSelection");
            selectionEventField.setToolTipText("Method name for selection change event");
            addPropertyRow("Selection Event:", selectionEventField, gbc, row++);
        }
    }

    private void addComponentSpecificProperties(DesignComponent component, GridBagConstraints gbc, int startRow) {
        int row = startRow;

        // Component-specific properties header
        JLabel specificLabel = new JLabel("Component Properties");
        specificLabel.setFont(specificLabel.getFont().deriveFont(Font.BOLD));
        specificLabel.setForeground(new Color(150, 0, 0));
        addPropertyRow("", specificLabel, gbc, row++);

        if (component.getComponentType() == JTextField.class) {
            // TextField-specific properties
            JSlider columnsSlider = new JSlider(1, 50, component.getColumns());
            JSpinner columnsSpinner = new JSpinner(new SpinnerNumberModel(component.getColumns(), 1, 50, 1));

            JPanel columnsPanel = new JPanel(new BorderLayout(5, 0));
            columnsPanel.add(columnsSlider, BorderLayout.CENTER);
            columnsPanel.add(columnsSpinner, BorderLayout.EAST);

            columnsSlider.addChangeListener(e -> {
                if (!columnsSlider.getValueIsAdjusting()) {
                    columnsSpinner.setValue(columnsSlider.getValue());
                    component.setColumns(columnsSlider.getValue());
                    canvas.repaint();
                }
            });

            columnsSpinner.addChangeListener(e -> {
                int value = (Integer) columnsSpinner.getValue();
                columnsSlider.setValue(value);
                component.setColumns(value);
                canvas.repaint();
            });

            addPropertyRow("Columns:", columnsPanel, gbc, row++);

            JCheckBox editableCheckBox = new JCheckBox("", component.isEditable());
            editableCheckBox.addActionListener(e -> {
                component.setEditable(editableCheckBox.isSelected());
                canvas.repaint();
            });
            addPropertyRow("Editable:", editableCheckBox, gbc, row++);

        } else if (component.getComponentType() == JCheckBox.class) {
            JCheckBox selectedCheckBox = new JCheckBox("", component.isSelected());
            selectedCheckBox.addActionListener(e -> {
                component.setSelected(selectedCheckBox.isSelected());
                canvas.repaint();
            });
            addPropertyRow("Selected:", selectedCheckBox, gbc, row++);
        }
    }

    private int getComponentSpecificPropertyCount(DesignComponent component) {
        if (component.getComponentType() == JTextField.class) {
            return 3; // Header + Columns + Editable
        } else if (component.getComponentType() == JCheckBox.class) {
            return 2; // Header + Selected
        }
        return 1; // Just header
    }

    private void addSeparator(GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        JSeparator separator = new JSeparator();
        propertyContainer.add(separator, gbc);

        // Reset constraints
        gbc.gridwidth = 1;
        gbc.insets = new Insets(3, 3, 3, 3);
    }

    private void addPropertyRow(String label, JComponent editor, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        if (!label.isEmpty()) {
            JLabel labelComponent = new JLabel(label);
            labelComponent.setPreferredSize(new Dimension(80, 25));
            propertyContainer.add(labelComponent, gbc);

            gbc.gridx = 1;
        } else {
            gbc.gridwidth = 2;
        }

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        editor.setPreferredSize(new Dimension(120, 25));
        propertyContainer.add(editor, gbc);
    }
}