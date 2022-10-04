package by.fxg.speceditor.prefabs;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.math.Vector3;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.prefabs.screen.ScreenPrefabProject;
import by.fxg.speceditor.project.BasicProject;
import by.fxg.speceditor.project.ProjectManager.IProjectSolver;
import by.fxg.speceditor.screen.gui.GuiError;
import by.fxg.speceditor.std.objecttree.ElementStack;
import by.fxg.speceditor.std.objecttree.elements.ElementFolder;
import by.fxg.speceditor.std.render.DefaultRenderer;
import by.fxg.speceditor.std.render.IRendererType;
import by.fxg.speceditor.std.render.IRendererType.ViewportSettings;

public class PrefabProject extends BasicProject {
	public PrefabProjectIO io;
	public ScreenPrefabProject projectScreen;
	public IRendererType renderer;
	
	private ElementStack tmpObjectTree; //used on loading to load stack, then use it in #onProjectOpened
	
	public PrefabProject(IProjectSolver solver) {
		super(solver);
		this.tmpObjectTree = new ElementStack();
		
		ElementFolder folder = new ElementFolder();
		for (int i = 0; i != 10; i++) {
			ElementFolder _folder = new ElementFolder("Folder " + i);
			_folder.getFolderStack().add(folder);
			_folder.setFolderOpened(true);
			folder = _folder;
		}
		this.tmpObjectTree.add(folder);
		for (int i = 0; i != 15; i++) {
			this.tmpObjectTree.add(new ElementFolder());
		}
	}
	
	public void loadConfiguration(FileHandle fileHandle) {
		super.loadConfiguration(fileHandle);
		this.io = new PrefabProjectIO(this);
	}

	public boolean loadProject() {
		if (!this.projectFolder.child("data.prj").exists() || !this.io.loadProjectData(this.tmpObjectTree)) {
			ViewportSettings.viewportHitboxDepth = true;
			ViewportSettings.viewportHitboxWidth = 2.0F;
			ViewportSettings.bufferColor = new Color(0.12F, 0.12F, 0.12F, 1.0F);
			ViewportSettings.cameraSettings = new Vector3(67F, 50.0f, 0.1f);
			ViewportSettings.viewportAttributes.add(new BlendingAttribute(1f), FloatAttribute.createAlphaTest(0.5f), ColorAttribute.createAmbientLight(0.4f, 0.4f, 0.4f, 1F));
			ViewportSettings.shouldUpdate = true;
			if (this.projectFolder.child("data.prj").exists()) {
				Game.get.renderer.currentGui = new GuiError("PrefabProject#loadProject", this.io.getLastException());
			}
		}
		return true;
	}	
	
	public void onProjectOpened() {
		ScreenPrefabProject screen = new ScreenPrefabProject(this);
		screen.subObjectTree.objectTree.setStack(this.tmpObjectTree).setHandler(new PrefabObjectTreeHandler(this));
		screen.subViewport.renderer = this.renderer = new DefaultRenderer(screen.subObjectTree.objectTree, screen.subViewport.camera);
		Game.get.renderer.currentScreen = this.projectScreen = screen;
		//TODO in future allow to use GLTF renderer when everything will be in better quality
	}
}
