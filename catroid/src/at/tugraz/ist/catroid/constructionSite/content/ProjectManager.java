package at.tugraz.ist.catroid.constructionSite.content;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectManager extends Observable {
    //private final String DEFAULT_PROJECT_NAME = "defaultProject";

    private Sprite currentSprite;
    private Project project;
    private static ProjectManager instance;
    private Script currentScript;

    //isn't used
    //    private ProjectManager(Context context, String projectName) {
    //        this.context = context;
    //        DEFAULT_PROJECT_NAME = context.getString(R.string.default_project_name);
    //        try {
    //            if (projectName != null && projectName.length() != 0) {
    //                if (!loadProject(projectName, context)) {
    //                    if (!loadProject(DEFAULT_PROJECT_NAME, context)) {
    //                        project = StorageHandler.getInstance().createDefaultProject(context);
    //                        currentSprite = project.getSpriteList().get(0); // stage
    //                    }
    //                }
    //            } else {
    //                project = StorageHandler.getInstance().createDefaultProject(context);
    //                currentSprite = project.getSpriteList().get(0); // stage
    //            }
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }

    private ProjectManager() {
    }

    public static ProjectManager getInstance() {
        if (instance == null) {
            instance = new ProjectManager();
        }
        return instance;
    }

    public boolean loadProject(String projectName, Context context) {
        try {
            project = StorageHandler.getInstance().loadProject(projectName);
            if (project == null) {
				initializeNewProject(context.getString(R.string.default_project_name), context);
            }
            currentSprite = null;
            currentScript = null;
            setChanged();
            notifyObservers();
            return true;
        } catch (Exception e) {
            Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));
            return false;
        }
    }

    public void saveProject(Context context) {
        try {
            if (project == null) {
                return;
            }
            StorageHandler.getInstance().saveProject(project);
        } catch (IOException e) {
            Utils.displayErrorMessage(context, context.getString(R.string.error_save_project));
        }
    }

    public void resetProject(Context context) throws NameNotFoundException {
        project = new Project(context, project.getName());
        currentSprite = null;
        currentScript = null;
        setChanged();
        notifyObservers();
    }

    public void addSprite(Sprite sprite) {
        project.addSprite(sprite);
    }
    
    public void addScript(Script script) {
        currentSprite.getScriptList().add(script);
    }

    public void addBrick(Brick brick) {
        currentScript.addBrick(brick);
        setChanged();
        notifyObservers();
    }

    public void moveBrickUpInList(int position) {
        if (position >= 0 && position < currentScript.getBrickList().size()) {
            currentScript.moveBrickBySteps(currentScript.getBrickList().get(position), -1);
            setChanged();
            notifyObservers();
        }
    }

    public void moveBrickDownInList(int position) {
        if (position >= 0 && position < currentScript.getBrickList().size()) {
            currentScript.moveBrickBySteps(currentScript.getBrickList().get(position), 1);
            setChanged();
            notifyObservers();
        }
    }

    public void initializeNewProject(String projectName, Context context) {
        try {
            project = new Project(context, projectName);
            currentSprite = null;
            currentScript = null;
            saveProject(context);
            setChanged();
            notifyObservers();
        } catch (NameNotFoundException e) {
            Utils.displayErrorMessage(context, context.getString(R.string.error_save_project));
        }
    }

    public void setObserver(Observer observer) {
        addObserver(observer);
    }

    public Sprite getCurrentSprite() {
        return currentSprite;
    }
    
    public Project getCurrentProject() {
		return project;
    }

    public Script getCurrentScript() {
        return currentScript;
    }

    /**
     * @return false if project doesn't contain the new sprite, true otherwise
     */
	public boolean setCurrentSprite(Sprite sprite) {
		if (sprite == null) { //sometimes we want to set the currentSprite to null because we don't have a currentSprite
			currentSprite = null;
            return true;
        }
		if (project.getSpriteList().contains(sprite)) {
			currentSprite = sprite;
            return true;
        }
        return false;
    }

    /**
     * @return false if currentSprite doesn't contain the new script, true
     *         otherwise
     */
    public boolean setCurrentScript(Script script) {
		if (script == null) {
			currentScript = null;
			return true;
		}
        if (currentSprite.getScriptList().contains(script)) {
            currentScript = script;
            return true;
        }
        return false;
    }

    public boolean scriptExists(String scriptName) {
        for (Script script : currentSprite.getScriptList()) {
            if (script.getName().equalsIgnoreCase(scriptName)) {
                return true;
            }
        }
        return false;
    }

    public boolean spriteExists(String spriteName) {
        for (Sprite tempSprite : project.getSpriteList()) {
            if (tempSprite.getName().equalsIgnoreCase(spriteName)) {
                return true;
            }
        }
        return false;
    }
}