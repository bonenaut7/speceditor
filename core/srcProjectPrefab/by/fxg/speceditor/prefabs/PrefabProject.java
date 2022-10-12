package by.fxg.speceditor.prefabs;

import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.prefabs.screen.ScreenPrefabProject;
import by.fxg.speceditor.project.BasicProject;
import by.fxg.speceditor.project.ProjectSolver;
import by.fxg.speceditor.screen.gui.GuiError;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.elements.ElementFolder;
import by.fxg.speceditor.std.viewport.DefaultRenderer;
import by.fxg.speceditor.std.viewport.IViewportRenderer;

public class PrefabProject extends BasicProject {
	public PrefabProjectIO io;
	public ScreenPrefabProject projectScreen;
	public SpecObjectTree objectTree;
	public IViewportRenderer renderer;
	
	public PrefabProject(ProjectSolver solver) {
		super(solver);
		this.objectTree = new SpecObjectTree().setHandler(new PrefabObjectTreeHandler(this));
		this.objectTree.getStack().add(new ElementFolder("Root folder"));

		this.renderer = new DefaultRenderer(this.objectTree);
	}
	
	public void loadConfiguration(FileHandle fileHandle) {
		super.loadConfiguration(fileHandle);
		this.io = new PrefabProjectIO(this);
	}

	public boolean loadProject() {
		if (!this.projectFolder.child("data.prj").exists() || !this.io.loadProjectData(this.renderer, this.objectTree.getStack())) {
			if (this.projectFolder.child("data.prj").exists()) {
				Game.get.renderer.currentGui = new GuiError("PrefabProject#loadProject", this.io.getLastException());
			}
		}
		return true;
	}	
	
	public void onProjectOpened() {
		Game.get.renderer.currentScreen = this.projectScreen = new ScreenPrefabProject(this);
	}
}
