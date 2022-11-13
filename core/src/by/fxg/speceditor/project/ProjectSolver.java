package by.fxg.speceditor.project;

import org.ini4j.Ini;

import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.utils.BaseSubscreen;

public abstract class ProjectSolver {
	/** Display name of solver **/
	protected String displayName;
	/** Unique type name, used in project headers **/
	protected String solverType;
	
	/** XXX docs **/
	public ProjectSolver(String displayName, String solverType) {
		this.displayName = displayName;
		this.solverType = solverType;
	}
	
	/** Localized type name for project description **/
	public String getDisplayName() {
		return this.displayName;
	}

	/** Flag for project discoverer, reads headers if true **/
	public boolean acceptProject(Ini projectConfig, String type) {
		return type.equalsIgnoreCase(this.solverType);
	}

	/** Flag for project discoverer, enables 'load project' button **/
	public boolean canLoadProject(FileHandle projectFolder) {
		return projectFolder != null && projectFolder.exists() && projectFolder.child("project.ini").exists() && !projectFolder.child("project.ini").isDirectory();
	}

	/** Returns project blank with loaded header. **/
	abstract public BasicProject preLoadProject(FileHandle projectFolder);
	
	/** Returns ability of solver to create projects and be used in project creation screen **/
	public boolean isAbleToCreateProject() {
		return false;
	}
	
	/** Subscreen used in project creation menu for setting parameters and etc <br>
	 *  Return new subscreen object. After that, project creation screen will use {@link BaseSubscreen#resize(int, int, int, int)} method **/
	public BaseSubscreen getProjectCreationSubscreen() {
		return null;
	}
}
