package Designer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Component palette containing draggable UI components
 * Users can drag components from here to the design canvas
 */
public class ComponentPalette extends JPanel {

    public ComponentPalette() {
        setLayout(new GridLayout(0, 1, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add draggable component buttons
        addComponent("Button", JButton.class);
        addComponent("Label", JLabel.class);
        addComponent("TextField", JTextField.class);
        addComponent("CheckBox", JCheckBox.class);
        addComponent("Panel", JPanel.class);
        addComponent("ComboBox", JComboBox.class);
        addComponent("List", JList.class);
        addComponent("TextArea", JTextArea.class);
    }

    private void addComponent(String name, Class<?> componentClass) {
        JButton button = createDraggableComponent(name, componentClass);
        add(button);
    }

    private JButton createDraggableComponent(String name, Class<?> componentClass) {
        JButton button = new JButton(name);
        button.setPreferredSize(new Dimension(150, 30));
        button.setToolTipText("Drag to canvas to add " + name);

        // Style the button
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Make the button draggable
        button.setTransferHandler(new ComponentTransferHandler(componentClass));
        button.addMouseListener(new PaletteMouseListener());

        return button;
    }

    /**
     * Mouse listener for palette buttons to initiate drag operations
     */
    private static class PaletteMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            JComponent comp = (JComponent) e.getSource();
            TransferHandler handler = comp.getTransferHandler();
            handler.exportAsDrag(comp, e, TransferHandler.COPY);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            button.setBackground(Color.LIGHT_GRAY);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            button.setBackground(Color.WHITE);
        }
    }
}