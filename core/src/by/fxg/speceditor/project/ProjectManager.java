package by.fxg.speceditor.project;

import java.io.IOException;
import java.util.Map;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.utils.Utils;

public class ProjectManager {
	public static ProjectManager INSTANCE;
	public static BasicProject currentProject;
	private Array<ProjectSolver> registeredSolvers = new Array<>();
	/** Paths to project folders that specified in Project {@link com.badlogic.gdx.Preferences} List **/
	private Array<FileHandle> specifiedProjects = new Array<>();
	
	public static void setCurrentProject(BasicProject project) {
		if (project != null) {
			currentProject = project;
			Gdx.graphics.setTitle(Utils.format("SpecEditor - ", project.getProjectName()));
		} else {
			currentProject = null;
			Gdx.graphics.setTitle("SpecEditor");
		}
	}
	
	public void postInit() {
		Utils.logDebug("[ProjectManager] Loaded ", this.registeredSolvers.size, " project solvers.");
	
		Map<String, ?> projectList = Gdx.app.getPreferences("by.fxg.speceditor.ProjectList").get();
		for (String key : projectList.keySet()) {
			if (projectList.get(key) instanceof String) {
				FileHandle projectFile = Gdx.files.absolute((String)projectList.get(key)).child("project.ini");
				if (projectFile.exists()) this.specifiedProjects.add(projectFile.parent());
			}
		}
		Utils.logDebug("[ProjectManager] Found ", this.specifiedProjects.size, " existing projects.");
	}
	
	/** Discovers project type, searches and returns available solver **/
	public ProjectSolver discoverProject(FileHandle projectFolder) {
		try {
			FileHandle projectFile = projectFolder == null ? null : projectFolder.child("project.ini");
			if (projectFile != null && projectFile.exists()) {
				Ini projectConfig = new Ini(projectFile.file());
				if (projectConfig.containsKey("PROJECT-HEADER") && projectConfig.get("PROJECT-HEADER").containsKey("project-type")) {
					String projectType = projectConfig.get("PROJECT-HEADER").get("project-type");
					for (ProjectSolver solver : this.registeredSolvers) {
						if (solver.acceptProject(projectConfig, projectType)) return solver;
					}
				} else Utils.logDebug("Project Manager", String.format("Unable to discover project config, incorrect header at: %s", projectFolder.path()));
			} else Utils.logDebug("Project", String.format("Unable to discover project config. There is no project at: %s", projectFolder.path()));
		} catch (InvalidFileFormatException exception) {
			Utils.logError(exception, "Project Manager", String.format("Unable to discovery project at: %s", projectFolder.path()));
		} catch (IOException exception) {
			Utils.logError(exception, "Project Manager", String.format("IO Exception(%s) while discovering project at: %s", exception.getMessage(), projectFolder.path()));
		}
		return null;
	}
	
	public boolean registerProjectSolver(ProjectSolver solver) {
		if (solver != null && !this.registeredSolvers.contains(solver, true)) {
			this.registeredSolvers.add(solver);
			return true;
		}
		return false;
	}
	
	public boolean removeProjectSolver(ProjectSolver solver) {
		if (solver != null) {
			return this.registeredSolvers.removeValue(solver, true);
		}
		return false;
	}
	
	/** Updates Project {@link com.badlogic.gdx.Preferences} List and adds #projectFolder if not present **/
	public void setRecentProject(FileHandle projectFolder) {
		if (projectFolder.child("project.ini").exists()) {
			Array<FileHandle> newArray = new Array<>();
			newArray.add(projectFolder);
			for (FileHandle fileHandle : this.specifiedProjects) {
				if (!newArray.contains(fileHandle, false)) {
					newArray.add(fileHandle);
				}
			}
			this.specifiedProjects = newArray;
			Preferences projectList = Gdx.app.getPreferences("by.fxg.speceditor.ProjectList");
			projectList.clear();
			for (int i = 0; i != newArray.size; i++) {
				projectList.putString(String.valueOf(i), newArray.get(i).path());
			}
			projectList.flush();
		}
	}
	
	public Iterable<ProjectSolver> getSolvers() {
		return this.registeredSolvers;
	}
	
	public Array<FileHandle> getSpecifiedProjects() {
		return this.specifiedProjects;
	}
}
