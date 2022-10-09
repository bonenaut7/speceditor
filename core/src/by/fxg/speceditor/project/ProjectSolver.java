package by.fxg.speceditor.project;

import org.ini4j.Ini;

import com.badlogic.gdx.files.FileHandle;

public class ProjectSolver {
	protected String typeName;
	protected String solverType;
	
	/** Requires **/
	public ProjectSolver(String typeName, String solverType) {
		this.typeName = typeName;
		this.solverType = solverType;
	}
	
	/** Localized type name for project description **/
	public String getTypeName() {
		return this.typeName;
	}

	/** Flag for project discoverer, reads headers if true **/
	public boolean acceptProject(Ini projectConfig, String type) {
		return type.equalsIgnoreCase(this.solverType);
	}

	/** Flag for project discoverer, enables 'load project' button **/
	public boolean canLoadProject(FileHandle projectFile) {
		return false;
	}

	/** Returns project blank with loaded header. **/
	public BasicProject preLoadProject(FileHandle projectFile) {
		return null;
	}
	
	/** Called when asked for project creation with this solver. **/
	public void createProject(FileHandle projectFolder) {
		if (!projectFolder.exists()) projectFolder.mkdirs();
	}
}
