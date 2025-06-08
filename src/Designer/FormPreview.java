package Designer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FormPreview {

    public JFrame createPreviewFrame(List<DesignComponent> components) {
        JFrame previewFrame = new JFrame("Form Preview");
        previewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        previewFrame.setSize(800, 600);
        previewFrame.setLayout(null);

        // Create actual Swing components based on design components
        for (DesignComponent designComp : components) {
            JComponent actualComp = createActualComponent(designComp);
            if (actualComp != null) {
                previewFrame.add(actualComp);
            }
        }

        return previewFrame;
    }

    private JComponent createActualComponent(DesignComponent designComp) {
        JComponent component = null;

        try {
            // Create the actual Swing component
            component = createComponentByType(designComp);

            if (component != null) {
                // Apply common properties
                applyCommonProperties(component, designComp);

                // Apply component-specific properties
                applySpecificProperties(component, designComp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return component;
    }

    private JComponent createComponentByType(DesignComponent designComp) {
        Class<?> type = designComp.getComponentType();

        if (type == JButton.class) {
            return new JButton(designComp.getText());
        } else if (type == JLabel.class) {
            return new JLabel(designComp.getText());
        } else if (type == JTextField.class) {
            JTextField textField = new JTextField(designComp.getText());
            textField.setEditable(designComp.isEditable());
            textField.setColumns(designComp.getColumns());
            return textField;
        } else if (type == JCheckBox.class) {
            JCheckBox checkBox = new JCheckBox(designComp.getText());
            checkBox.setSelected(designComp.isSelected());
            return checkBox;
        } else if (type == JPanel.class) {
            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createTitledBorder("Panel"));
            return panel;
        } else if (type == JComboBox.class) {
            JComboBox<String> comboBox = new JComboBox<>(new String[]{"Option 1", "Option 2", "Option 3"});
            return comboBox;
        } else if (type == JList.class) {
            JList<String> list = new JList<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"});
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            return new JScrollPane(list);
        } else if (type == JTextArea.class) {
            JTextArea textArea = new JTextArea(designComp.getText());
            textArea.setRows(5);
            textArea.setColumns(20);
            return new JScrollPane(textArea);
        }

        return null;
    }

    private void applyCommonProperties(JComponent component, DesignComponent designComp) {
        Rectangle bounds = designComp.getBounds();
        component.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        component.setBackground(designComp.getBackgroundColor());
        component.setEnabled(designComp.isEnabled());
        component.setVisible(designComp.isVisible());

        // Make panels opaque to show background color
        if (component instanceof JPanel) {
            component.setOpaque(true);
        }
    }

    private void applySpecificProperties(JComponent component, DesignComponent designComp) {
        // Apply component-specific properties that aren't handled in creation
        if (component instanceof JTextField) {
            JTextField textField = (JTextField) component;
            textField.setEditable(designComp.isEditable());
        }

        if (component instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) component;
            checkBox.setSelected(designComp.isSelected());
        }
    }
}