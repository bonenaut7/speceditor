package by.fxg.speceditor.scenes;

import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.project.BasicProject;
import by.fxg.speceditor.project.ProjectSolver;
import by.fxg.speceditor.utils.Utils;

public class ScenesProjectSolver extends ProjectSolver {
	public ScenesProjectSolver() {
		super("Prefab", "STD-PREFAB");
	}
	
	public boolean canLoadProject(FileHandle projectFile) {
		return false;
	}

	public BasicProject preLoadProject(FileHandle projectFile) {
		ScenesProject project = new ScenesProject(this);
		project.loadConfiguration(projectFile);
		return project;
	}
	
	public void createProject(FileHandle projectFolder) {
		Utils.logError(null, "PrefabProjectSolver#createProject", "Not implemented.");
	}
}
