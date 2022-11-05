package by.fxg.speceditor.scenes;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.project.BasicProject;
import by.fxg.speceditor.project.ProjectSolver;
import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.scenes.screen.ScreenSceneProject;
import by.fxg.speceditor.screen.gui.GuiError;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.elements.ElementFolder;
import by.fxg.speceditor.std.viewport.DefaultRenderer;
import by.fxg.speceditor.std.viewport.IViewportRenderer;

public class ScenesProject extends BasicProject {
	private final FileHandle scenesDataFile;
	public ScenesProjectIO io;
	public ScreenSceneProject projectScreen;
	public SpecObjectTree objectTree;
	public IViewportRenderer renderer;
	
	public ScenesProject(ProjectSolver solver, FileHandle folder) {
		super(solver, folder);
		this.io = new ScenesProjectIO(this);
		this.scenesDataFile = folder.child("scenes.data");
	}
	
	public ScenesProject(ProjectSolver solver, FileHandle folder, String name, boolean backupSaving, long backupInterval) {
		super(solver, folder, name, backupSaving, backupInterval);
		this.io = new ScenesProjectIO(this);
		this.scenesDataFile = folder.child("scenes.data");
	}

	public boolean loadProject() {
		this.assetManager = new ProjectAssetManager();
		this.objectTree = new SpecObjectTree().setHandler(new ScenesObjectTreeHandler(this));
		this.renderer = new DefaultRenderer(this.objectTree);
		
		if (this.projectFolder.child("scenes.data").exists()) {
			if (!this.io.loadProjectData(this.scenesDataFile, this.assetManager, this.renderer, this.objectTree)) {
				SpecEditor.get.renderer.currentGui = new GuiError("PrefabProject#loadProject", this.io.getLastException());
			}
		} else this.objectTree.getStack().add(new ElementFolder("PROJECT ROOT"));
		return true;
	}	
	
	public boolean saveProject() {
		return this.io.writeProjectData(this.scenesDataFile, ProjectAssetManager.INSTANCE, this.renderer, this.objectTree);
	}
	
	public void onProjectOpened() {
		SpecEditor.get.renderer.currentScreen = this.projectScreen = new ScreenSceneProject(this);
		this.objectTree.refreshTree();
	}
	
	public void makeBackup() {
		LocalDateTime date = LocalDateTime.now();
		String fileName = String.format("[%02d%s%04d][%02d-%02d-%02d]scenes.data", date.getDayOfMonth(), date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase(), date.getYear(), date.getHour(), date.getMinute(), date.getSecond());
		this.io.writeProjectData(this.projectFolder.child("backups/").child(fileName), ProjectAssetManager.INSTANCE, this.renderer, this.objectTree);
		this.lastSaveDate = String.format("%04d.%s.%02d %02d:%02d", date.getDayOfMonth(), date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase(), date.getYear(), date.getHour(), date.getMinute());
	}
}
