package by.fxg.speceditor.prefabs;

import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.project.BasicProject;
import by.fxg.speceditor.project.ProjectSolver;
import by.fxg.speceditor.utils.Utils;

public class PrefabProjectSolver extends ProjectSolver {
	public PrefabProjectSolver() {
		super("Prefab", "STD-PREFAB");
	}
	
	public boolean canLoadProject(FileHandle projectFile) {
		return false;
	}

	public BasicProject preLoadProject(FileHandle projectFile) {
		PrefabProject project = new PrefabProject(this);
		project.loadConfiguration(projectFile);
		return project;
	}
	
	public void createProject(FileHandle projectFolder) {
		Utils.logError(null, "PrefabProjectSolver#createProject", "Not implemented.");
	}
}
