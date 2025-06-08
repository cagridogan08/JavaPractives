package Project;

import Designer.DesignComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; /**
 * Represents a single screen/page in a design project
 */
public class DesignScreen {
    private String name;
    private ScreenType type;
    private List<DesignComponent> components;
    private Map<String, Object> screenSettings;
    private DesignProject project;
    private String description;
    private boolean isVisible;

    public DesignScreen(String name, ScreenType type) {
        this.name = name;
        this.type = type;
        this.components = new ArrayList<>();
        this.screenSettings = new HashMap<>();
        this.description = "";
        this.isVisible = true;

        // Default screen settings based on type
        initializeDefaultSettings();
    }

    private void initializeDefaultSettings() {
        screenSettings.put("backgroundColor", java.awt.Color.WHITE);
        screenSettings.put("width", 800);
        screenSettings.put("height", 600);

        switch (type) {
            case MAIN:
                screenSettings.put("showMenuBar", true);
                screenSettings.put("showToolbar", true);
                screenSettings.put("showStatusBar", true);
                break;
            case DIALOG:
                screenSettings.put("modal", true);
                screenSettings.put("resizable", false);
                screenSettings.put("width", 400);
                screenSettings.put("height", 300);
                break;
            case LOGIN:
                screenSettings.put("centerOnScreen", true);
                screenSettings.put("showTitleBar", true);
                screenSettings.put("width", 350);
                screenSettings.put("height", 250);
                break;
            case SPLASH:
                screenSettings.put("undecorated", true);
                screenSettings.put("centerOnScreen", true);
                screenSettings.put("autoClose", true);
                screenSettings.put("displayTime", 3000);
                break;
            case CUSTOM:
                // Custom screens have flexible settings
                break;
        }
    }

    public void addComponent(DesignComponent component) {
        components.add(component);
        if (project != null) {
            project.updateModifiedDate();
        }
    }

    public void removeComponent(DesignComponent component) {
        components.remove(component);
        if (project != null) {
            project.updateModifiedDate();
        }
    }

    public void clearComponents() {
        components.clear();
        if (project != null) {
            project.updateModifiedDate();
        }
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ScreenType getType() { return type; }
    public void setType(ScreenType type) { this.type = type; initializeDefaultSettings(); }
    public List<DesignComponent> getComponents() { return components; }
    public Map<String, Object> getScreenSettings() { return screenSettings; }
    public DesignProject getProject() { return project; }
    public void setProject(DesignProject project) { this.project = project; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { this.isVisible = visible; }

    public Object getScreenSetting(String key, Object defaultValue) {
        return screenSettings.getOrDefault(key, defaultValue);
    }

    public void setScreenSetting(String key, Object value) {
        screenSettings.put(key, value);
        if (project != null) {
            project.updateModifiedDate();
        }
    }
}
