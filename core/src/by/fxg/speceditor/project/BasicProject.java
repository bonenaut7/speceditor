package by.fxg.speceditor.project;

import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.Storage;
import by.fxg.speceditor.utils.Utils;

public abstract class BasicProject {
	/** Project-solver of this project **/
	protected ProjectSolver solver;
	/** Manager of project's assets **/
	protected ProjectAssetManager assetManager = new ProjectAssetManager();
	/** Loaded project-header of this project. Contains project settings and info. **/
	protected Ini config;
	protected FileHandle projectFolder;
	
	protected String type, name, folderName, lastSaveDate;
	protected boolean backupSaving;
	protected long backupInterval; //in seconds
	
	/** Constructor for project load **/
	public BasicProject(ProjectSolver solver) {
		this.solver = solver;
	}
	
	/** Constructor for project creation process **/
	public BasicProject(ProjectSolver solver, String name, String folderName, boolean backupSaving, long backupInterval) {
		this.solver = solver;
		this.type = solver.getTypeName();
		this.name = name;
		this.folderName = folderName;
		this.backupSaving = backupSaving;
		this.backupInterval = backupInterval;
		
		this.config = new Ini();
		this.projectFolder = Storage.projectsFolder.child(folderName);
	}
	
	/** Loads project-header info, needed before project load **/
	public void loadConfiguration(FileHandle fileHandle) {
		try {
			this.config = new Ini(fileHandle.file());
			this.projectFolder = fileHandle.parent();
			if (this.config.containsKey("PROJECT-HEADER")) {
				Section headerInfo = this.config.get("PROJECT-HEADER");
				this.type = headerInfo.containsKey("project-type") ? headerInfo.get("project-type") : "UNDEFINED";
				this.name = headerInfo.containsKey("project-name") ? headerInfo.get("project-name") : "Undefined";
				this.folderName = fileHandle.parent().name();
				this.lastSaveDate = headerInfo.containsKey("last-save-date") ? headerInfo.get("last-save-date") : "Undefined";
				this.backupSaving = headerInfo.containsKey("backup-saving") ? headerInfo.get("backup-saving", boolean.class) : true;
				this.backupInterval = 300L;//headerInfo.containsKey("backup-interval") ? : 300L; //300 seconds by default
			} else Utils.logDebug("Project", String.format("Unable to load project with %s solver. There is no header in project file at: %s", this.solver.getTypeName(), fileHandle.path()));
		} catch (InvalidFileFormatException e) {
			Utils.logError(e, "BasicProject#loadHeader", "IFFE unhandled exception");
		} catch (IOException e) {
			Utils.logError(e, "BasicProject#loadHeader", "IO unhandled exception");
		}
	}
	
	public void saveConfiguration() {
		
	}
	
	/** Called when project needed to be loaded. Return false to cancel loading. **/
	abstract public boolean loadProject();
	/** Called in project selection screen. Used to open project screen. **/
	abstract public void onProjectOpened();
	
	/** Returns preference value from project configuration **/
	public <TYPE> TYPE getPreference(String name, Class<TYPE> typeClass, TYPE defaultValue) {
		if (!this.config.containsKey("PREFERENCES")) this.config.add("PREFERENCES");
		return this.config.get("PREFERENCES").containsKey(name) ? this.config.get("PREFERENCES").get(name, typeClass) : defaultValue;
	}

	/** Sets preference value in project configuration **/
	public void setPreference(String name, Object object) {
		if (!this.config.containsKey("PREFERENCES")) this.config.add("PREFERENCES");
		this.config.get("PREFERENCES").put(name, object);
	}
	
	public FileHandle getProjectFolder() { return this.projectFolder; }
	public String getProjectName() { return this.name; }
	public String getProjectType() { return this.solver.getTypeName(); }
	public String getSaveDate() { return this.lastSaveDate; }
	public boolean isBackupsEnabled() { return this.backupSaving; }
}
