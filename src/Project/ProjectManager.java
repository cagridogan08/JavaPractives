package Project;

import Designer.DesignComponent;

import java.util.*;

/**
 * Manages multi-screen design projects
 * Each project can contain multiple screens (pages) with different purposes
 */
public class ProjectManager {
    private static ProjectManager instance;
    private DesignProject currentProject;
    private final List<ProjectListener> listeners;

    private ProjectManager() {
        listeners = new ArrayList<>();
        createNewProject();
    }

    public static ProjectManager getInstance() {
        if (instance == null) {
            instance = new ProjectManager();
        }
        return instance;
    }

    public DesignProject getCurrentProject() {
        return currentProject;
    }

    public void createNewProject() {
        currentProject = new DesignProject("Untitled Project");
        // Add default screens
        currentProject.addScreen(new DesignScreen("Main Screen", ScreenType.MAIN));
        currentProject.setActiveScreen(currentProject.getScreens().getFirst());
        notifyListeners();
    }

    public void loadProject(DesignProject project) {
        this.currentProject = project;
        if (currentProject.getScreens().isEmpty()) {
            currentProject.addScreen(new DesignScreen("Main Screen", ScreenType.MAIN));
        }
        if (currentProject.getActiveScreen() == null) {
            currentProject.setActiveScreen(currentProject.getScreens().getFirst());
        }
        notifyListeners();
    }

    public void addListener(ProjectListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ProjectListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ProjectListener listener : listeners) {
            listener.projectChanged(currentProject);
        }
    }

    public interface ProjectListener {
        void projectChanged(DesignProject project);
    }
}

