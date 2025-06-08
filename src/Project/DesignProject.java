package Project;

import java.util.*; /**
 * Represents a design project containing multiple screens
 */
public class DesignProject {
    private String name;
    private String description;
    private List<DesignScreen> screens;
    private DesignScreen activeScreen;
    private Map<String, Object> projectSettings;
    private Date createdDate;
    private Date lastModifiedDate;

    public DesignProject(String name) {
        this.name = name;
        this.description = "";
        this.screens = new ArrayList<>();
        this.projectSettings = new HashMap<>();
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();

        // Default project settings
        projectSettings.put("targetResolution", "1920x1080");
        projectSettings.put("theme", "Light");
        projectSettings.put("gridSize", 10);
        projectSettings.put("snapToGrid", true);
    }

    public void addScreen(DesignScreen screen) {
        screens.add(screen);
        screen.setProject(this);
        updateModifiedDate();
    }

    public void removeScreen(DesignScreen screen) {
        screens.remove(screen);
        if (activeScreen == screen && !screens.isEmpty()) {
            activeScreen = screens.getFirst();
        }
        updateModifiedDate();
    }

    public DesignScreen createNewScreen(String name, ScreenType type) {
        DesignScreen newScreen = new DesignScreen(name, type);
        addScreen(newScreen);
        return newScreen;
    }

    public void updateModifiedDate() {
        lastModifiedDate = new Date();
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; updateModifiedDate(); }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; updateModifiedDate(); }
    public List<DesignScreen> getScreens() { return screens; }
    public DesignScreen getActiveScreen() { return activeScreen; }
    public void setActiveScreen(DesignScreen activeScreen) { this.activeScreen = activeScreen; }
    public Map<String, Object> getProjectSettings() { return projectSettings; }
    public Date getCreatedDate() { return createdDate; }
    public Date getLastModifiedDate() { return lastModifiedDate; }
}
