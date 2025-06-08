package Project;

/**
 * Types of screens that can be created
 */
public enum ScreenType {
    MAIN("Main Window", "Primary application window"),
    DIALOG("Dialog", "Modal or non-modal dialog"),
    LOGIN("Login Screen", "User authentication screen"),
    SPLASH("Splash Screen", "Application startup screen"),
    SETTINGS("Settings", "Application settings/preferences"),
    ABOUT("About Dialog", "About/information dialog"),
    WIZARD("Wizard Page", "Step-by-step wizard page"),
    DASHBOARD("Dashboard", "Data dashboard or overview"),
    REPORT("Report", "Data report or summary"),
    FORM("Form", "Data entry form"),
    LIST("List View", "List or table view"),
    DETAIL("Detail View", "Detail/edit view"),
    CUSTOM("Custom", "Custom screen type");

    private final String displayName;
    private final String description;

    ScreenType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }

    @Override
    public String toString() { return displayName; }
}
