import javax.swing.*;

public static void main(String[] args) {
    // Set system look and feel with proper exception handling
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException |
             IllegalAccessException | UnsupportedLookAndFeelException e) {
        System.err.println("Could not set system look and feel: " + e.getMessage());
        // Application will continue with default look and feel
    }

    // Create and show the application
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            new ScreenDesignerApp().setVisible(true);
        }
    });
}