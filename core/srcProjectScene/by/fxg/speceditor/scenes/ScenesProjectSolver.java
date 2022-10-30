package by.fxg.speceditor.scenes;

import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.project.BasicProject;
import by.fxg.speceditor.project.ProjectSolver;
import by.fxg.speceditor.scenes.screen.ScenesSubscreenCreateProject;
import by.fxg.speceditor.utils.BaseSubscreen;

public class ScenesProjectSolver extends ProjectSolver {
	public ScenesProjectSolver() {
		super("Standard scene", "STD-PREFAB");
	}

	public BasicProject preLoadProject(FileHandle projectFile) {
		ScenesProject project = new ScenesProject(this);
		project.loadConfiguration(projectFile);
		return project;
	}
	
	public boolean isAbleToCreateProject() {
		return true;
	}
	
	public BaseSubscreen getProjectCreationSubscreen() {
		return new ScenesSubscreenCreateProject();
	}
}
