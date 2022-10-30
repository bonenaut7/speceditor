package by.fxg.speceditor.scenes;

import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.project.BasicProject;
import by.fxg.speceditor.project.ProjectSolver;
import by.fxg.speceditor.scenes.screen.ScreenSceneProject;
import by.fxg.speceditor.screen.gui.GuiError;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.elements.ElementFolder;
import by.fxg.speceditor.std.viewport.DefaultRenderer;
import by.fxg.speceditor.std.viewport.IViewportRenderer;

public class ScenesProject extends BasicProject {
	public ScenesProjectIO io;
	public ScreenSceneProject projectScreen;
	public SpecObjectTree objectTree;
	public IViewportRenderer renderer;
	
	public ScenesProject(ProjectSolver solver, FileHandle folder) {
		super(solver, folder);
		this.io = new ScenesProjectIO(this);
	}
	
	public ScenesProject(ProjectSolver solver, FileHandle folder, String name, boolean backupSaving, long backupInterval) {
		super(solver, folder, name, backupSaving, backupInterval);
		this.io = new ScenesProjectIO(this);
	}

	public boolean loadProject() {
		this.objectTree = new SpecObjectTree().setHandler(new ScenesObjectTreeHandler(this));
		this.objectTree.getStack().add(new ElementFolder("Root folder"));
		this.renderer = new DefaultRenderer(this.objectTree);
		
		if (this.projectFolder.child("scenes.data").exists() && !this.io.loadProjectData(this.renderer, this.objectTree.getStack())) {
			SpecEditor.get.renderer.currentGui = new GuiError("PrefabProject#loadProject", this.io.getLastException());
		}
		return true;
	}	
	
	public boolean saveProject() {
		return false;
	}
	
	public void onProjectOpened() {
		SpecEditor.get.renderer.currentScreen = this.projectScreen = new ScreenSceneProject(this);
	}
}
