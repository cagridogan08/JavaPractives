package Designer;

import javax.swing.*;
import java.awt.*;

/**
 * Represents a UI component on the design canvas
 * Stores all properties and handles visual rendering
 */
public class DesignComponent {
    private final Class<?> componentType;
    private Rectangle bounds;
    private String text;
    private Color backgroundColor;
    private boolean visible;
    private boolean enabled;
    private boolean editable;
    private boolean selected;
    private int columns;

    public DesignComponent(Class<?> type, int x, int y) {
        this.componentType = type;
        this.bounds = new Rectangle(x, y, 100, 30);
        this.text = type.getSimpleName();
        this.backgroundColor = Color.LIGHT_GRAY;
        this.visible = true;
        this.enabled = true;
        this.editable = true;
        this.selected = false;
        this.columns = 10;

        initializeDefaults();
    }

    private void initializeDefaults() {
        if (componentType == JPanel.class) {
            bounds.setSize(150, 100);
            backgroundColor = Color.WHITE;
        } else if (componentType == JTextField.class) {
            bounds.setSize(120, 25);
            text = "TextField";
        } else if (componentType == JButton.class) {
            text = "Button";
        } else if (componentType == JLabel.class) {
            text = "Label";
        } else if (componentType == JCheckBox.class) {
            text = "CheckBox";
        }
    }

    public void draw(Graphics2D g2d) {
        if (!visible) return;

        // Draw component representation
        Color fillColor = enabled ? backgroundColor : backgroundColor.darker();
        g2d.setColor(fillColor);
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Draw component-specific details
        drawComponentSpecifics(g2d);

        // Draw component text
        if (text != null && !text.isEmpty()) {
            drawComponentText(g2d);
        }

        // Draw disabled overlay
        if (!enabled) {
            g2d.setColor(new Color(128, 128, 128, 100));
            g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private void drawComponentSpecifics(Graphics2D g2d) {
        if (componentType == JCheckBox.class) {
            // Draw checkbox square
            int checkSize = 12;
            int checkX = bounds.x + 5;
            int checkY = bounds.y + (bounds.height - checkSize) / 2;
            g2d.setColor(Color.WHITE);
            g2d.fillRect(checkX, checkY, checkSize, checkSize);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(checkX, checkY, checkSize, checkSize);

            if (selected) {
                g2d.drawLine(checkX + 2, checkY + 6, checkX + 5, checkY + 9);
                g2d.drawLine(checkX + 5, checkY + 9, checkX + 10, checkY + 4);
            }
        }
    }

    private void drawComponentText(Graphics2D g2d) {
        g2d.setColor(enabled ? Color.BLACK : Color.GRAY);
        FontMetrics fm = g2d.getFontMetrics();

        int textX, textY;
        if (componentType == JCheckBox.class) {
            textX = bounds.x + 20;
            textY = bounds.y + (bounds.height + fm.getAscent()) / 2;
        } else {
            textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
            textY = bounds.y + (bounds.height + fm.getAscent()) / 2;
        }

        g2d.drawString(text, textX, textY);
    }

    // Getters and Setters
    public Rectangle getBounds() {
        return bounds;
    }

    public void setLocation(int x, int y) {
        bounds.setLocation(x, y);
    }

    public void setSize(int width, int height) {
        bounds.setSize(width, height);
    }

    public void setBounds(int x, int y, int width, int height) {
        bounds.setBounds(x, y, width, height);
    }

    public Class<?> getComponentType() {
        return componentType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }
}