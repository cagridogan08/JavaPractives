package Designer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CodeGenerator {

    public String generateCode(List<DesignComponent> components) {
        StringBuilder code = new StringBuilder();

        // Class header
        code.append("import javax.swing.*;\n");
        code.append("import java.awt.*;\n");
        code.append("import java.awt.event.*;\n\n");

        code.append("public class GeneratedForm extends JFrame {\n");
        code.append("    // Component declarations\n");

        // Generate component declarations
        for (int i = 0; i < components.size(); i++) {
            DesignComponent comp = components.get(i);
            String componentName = getComponentName(comp, i);
            String componentType = getJavaComponentType(comp.getComponentType());
            code.append("    private ").append(componentType).append(" ").append(componentName).append(";\n");
        }

        code.append("\n    public GeneratedForm() {\n");
        code.append("        initializeComponents();\n");
        code.append("        setupLayout();\n");
        code.append("        setupFrame();\n");
        code.append("    }\n\n");

        // Initialize components method
        generateInitializeMethod(code, components);

        // Setup layout method
        generateLayoutMethod(code, components);

        // Setup frame method
        generateFrameMethod(code);

        // Main method
        generateMainMethod(code);

        code.append("}\n");

        return code.toString();
    }

    private void generateInitializeMethod(StringBuilder code, List<DesignComponent> components) {
        code.append("    private void initializeComponents() {\n");

        for (int i = 0; i < components.size(); i++) {
            DesignComponent comp = components.get(i);
            String componentName = getComponentName(comp, i);
            String componentType = getJavaComponentType(comp.getComponentType());

            code.append("        ").append(componentName).append(" = new ").append(componentType).append("();\n");

            // Set component properties
            generateComponentProperties(code, comp, componentName);
            code.append("\n");
        }

        code.append("    }\n\n");
    }

    private void generateComponentProperties(StringBuilder code, DesignComponent comp, String componentName) {
        // Set text property
        if (comp.getText() != null && !comp.getText().isEmpty()) {
            code.append("        ").append(componentName).append(".setText(\"").append(comp.getText()).append("\");\n");
        }

        // Set enabled property
        if (!comp.isEnabled()) {
            code.append("        ").append(componentName).append(".setEnabled(false);\n");
        }

        // Set visible property
        if (!comp.isVisible()) {
            code.append("        ").append(componentName).append(".setVisible(false);\n");
        }

        // Component-specific properties
        generateSpecificProperties(code, comp, componentName);

        // Set background color if not default
        generateBackgroundColor(code, comp, componentName);
    }

    private void generateSpecificProperties(StringBuilder code, DesignComponent comp, String componentName) {
        if (comp.getComponentType() == JTextField.class && !comp.isEditable()) {
            code.append("        ").append(componentName).append(".setEditable(false);\n");
        }

        if (comp.getComponentType() == JCheckBox.class && comp.isSelected()) {
            code.append("        ").append(componentName).append(".setSelected(true);\n");
        }

        if (comp.getComponentType() == JTextField.class && comp.getColumns() != 10) {
            code.append("        ").append(componentName).append(".setColumns(").append(comp.getColumns()).append(");\n");
        }
    }

    private void generateBackgroundColor(StringBuilder code, DesignComponent comp, String componentName) {
        Color bg = comp.getBackgroundColor();
        if (!bg.equals(Color.LIGHT_GRAY) && !bg.equals(Color.WHITE)) {
            code.append("        ").append(componentName).append(".setBackground(new Color(")
                    .append(bg.getRed()).append(", ").append(bg.getGreen()).append(", ").append(bg.getBlue()).append("));\n");
        }

        // Make panels opaque to show background color
        if (comp.getComponentType() == JPanel.class) {
            code.append("        ").append(componentName).append(".setOpaque(true);\n");
        }
    }

    private void generateLayoutMethod(StringBuilder code, List<DesignComponent> components) {
        code.append("    private void setupLayout() {\n");
        code.append("        setLayout(null); // Using absolute positioning\n\n");

        for (int i = 0; i < components.size(); i++) {
            DesignComponent comp = components.get(i);
            String componentName = getComponentName(comp, i);
            Rectangle bounds = comp.getBounds();

            code.append("        ").append(componentName).append(".setBounds(")
                    .append(bounds.x).append(", ").append(bounds.y).append(", ")
                    .append(bounds.width).append(", ").append(bounds.height).append(");\n");
            code.append("        add(").append(componentName).append(");\n\n");
        }

        code.append("    }\n\n");
    }

    private void generateFrameMethod(StringBuilder code) {
        code.append("    private void setupFrame() {\n");
        code.append("        setTitle(\"Generated Form\");\n");
        code.append("        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);\n");
        code.append("        setSize(800, 600);\n");
        code.append("        setLocationRelativeTo(null);\n");
        code.append("    }\n\n");
    }

    private void generateMainMethod(StringBuilder code) {
        code.append("    public static void main(String[] args) {\n");
        code.append("        SwingUtilities.invokeLater(() -> {\n");
        code.append("            try {\n");
        code.append("                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());\n");
        code.append("            } catch (Exception e) {\n");
        code.append("                e.printStackTrace();\n");
        code.append("            }\n");
        code.append("            new GeneratedForm().setVisible(true);\n");
        code.append("        });\n");
        code.append("    }\n");
    }

    private String getComponentName(DesignComponent comp, int index) {
        String baseName = comp.getComponentType().getSimpleName().toLowerCase();
        if (baseName.startsWith("j")) {
            baseName = baseName.substring(1); // Remove 'j' prefix
        }
        return baseName + (index + 1);
    }

    private String getJavaComponentType(Class<?> componentType) {
        return componentType.getSimpleName();
    }
}