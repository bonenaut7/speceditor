package by.fxg.speceditor.project;

import java.time.Instant;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.specformat.editor.SpecFormatSaver;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.render.IRendererType;

public class Project {
	/* current project instances */ 
	public static Project instance;			//Project instance
	public static IRendererType renderer;	//Renderer instance
	/* project data structure */
	private SpecFormatSaver saver;
	
	public FileHandle projectFolder;
	public FileHandle projectHeader;
	public FileHandle saveFile;
	
	public String projectName;
	public String lastSaveDate;
	public boolean backupSaving;
	
	public ProjectType projectType;
	public Vector3 cameraSettings = new Vector3();
	public Color bufferColor = new Color();
	public Array<Attribute> viewportAttributes = new Array<>();
	public boolean viewportHitboxDepth = true;
	public float viewportHitboxWidth = 1f;
	
	public Project(FileHandle projectFolder) {
		this.projectFolder = projectFolder;
		this.projectHeader = projectFolder.child("project.header");
		this.saveFile = projectFolder.child("project.spe");
		this.saver = new SpecFormatSaver(this);
	}
	
	public void loadHeader() {
		this.saver.loadProjectHeader();
	}
	
	public void saveHeader() { 
		this.lastSaveDate = Date.from(Instant.now()).toString();
		this.saver.writeProjectHeader();
	}
	
	public void loadProject(PMObjectExplorer pmoe) {
		instance = this;
		renderer.clear(false);
		pmoe.elementStack.clear();
		this.saver.loadProjectData(pmoe);
	}
	
	public void saveProject(PMObjectExplorer pmoe) {
		this.saver.writeProjectData(pmoe);
	}
	
	public static Project createProject(String projectName, boolean backupSaving) {
		Project project = new Project(Gdx.files.local("spec/projects/").child(projectName));
		project.projectName = projectName;
		project.backupSaving = backupSaving;
		project.lastSaveDate = "-";
		project.projectType = ProjectType.DEFAULT;
		project.saveHeader();
		return project;
	}
	
	public static Project discoverProject(FileHandle projectFolder) {
		if (projectFolder.exists()) {
			FileHandle projectFile = projectFolder.child("project.header");
			if (projectFile.exists()) {
				Project project = new Project(projectFolder);
				project.loadHeader();
				return project;
			}
		}
		return null;
	}
}
