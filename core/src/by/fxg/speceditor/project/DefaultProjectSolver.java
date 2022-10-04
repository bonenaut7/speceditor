package by.fxg.speceditor.project;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.project.ProjectManager.IProjectSolver;

public class DefaultProjectSolver implements IProjectSolver {
	protected String typeName;
	protected String solverType;
	
	/** Requires **/
	public DefaultProjectSolver(Ini solverConfig) {
		Section solverInfo = solverConfig.get("PROJECT-SOLVER");
		this.typeName = solverInfo.containsKey("type-name") ? solverInfo.get("type-name") : "UNDEFINED";
		this.solverType = solverInfo.containsKey("project-type") ? solverInfo.get("project-type") : "UNDEFINED";
	}
	
	public String getTypeName() {
		return this.typeName;
	}

	public boolean acceptProject(Ini projectConfig, String type) {
		return type.equalsIgnoreCase(this.solverType);
	}

	public boolean canLoadProject(FileHandle projectFile) {
		return false;
	}

	public BasicProject preLoadProject(FileHandle projectFile) {
		return null;
	}
	
	public void createProject(FileHandle projectFolder) {
		if (!projectFolder.exists()) projectFolder.mkdirs();
		
	}
}
