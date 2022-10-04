package by.fxg.speceditor.prefabs;

import org.ini4j.Ini;

import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.project.BasicProject;
import by.fxg.speceditor.project.DefaultProjectSolver;
import by.fxg.speceditor.utils.Utils;

public class PrefabProjectSolver extends DefaultProjectSolver {
	public PrefabProjectSolver(Ini solverConfig) {
		super(solverConfig);
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
