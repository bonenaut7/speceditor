package by.fxg.speceditor.project;

import java.io.IOException;
import java.time.Instant;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.utils.Utils;

public abstract class BasicProject {
	/** Project-solver of this project **/
	protected ProjectSolver solver;
	/** Manager of project's assets **/
	protected ProjectAssetManager assetManager;
	/** Loaded project-header of this project. Contains project settings and info. **/
	protected Ini config;
	protected FileHandle projectFolder;
	
	protected String type, name, lastSaveDate;
	protected boolean backupSaving;
	protected long backupInterval; //in seconds
	
	/** Constructor for project load **/
	public BasicProject(ProjectSolver solver, FileHandle folder) {
		this.solver = solver;
		this.projectFolder = folder;
	}
	
	/** Constructor for project creation process **/
	public BasicProject(ProjectSolver solver, FileHandle folder, String name, boolean backupSaving, long backupInterval) {
		this.solver = solver;
		this.type = solver.getDisplayName();
		this.projectFolder = folder;
		this.name = name;
		this.backupSaving = backupSaving;
		this.backupInterval = backupInterval;
		
		this.config = new Ini();
	}
	
	/** Loads project-header info, needed before project load **/
	public void loadConfiguration() {
		try {
			FileHandle projectFile = this.projectFolder == null ? null : this.projectFolder.child("project.ini");
			if (projectFile != null && projectFile.exists()) {
				this.config = new Ini(this.projectFolder.child("project.ini").file());
				if (this.config.containsKey("PROJECT-HEADER")) {
					Section headerInfo = this.config.get("PROJECT-HEADER");
					this.type = headerInfo.containsKey("project-type") ? headerInfo.get("project-type") : "UNDEFINED";
					this.name = headerInfo.containsKey("project-name") ? headerInfo.get("project-name") : "Undefined";
					this.lastSaveDate = headerInfo.containsKey("last-save-date") ? headerInfo.get("last-save-date") : "Undefined";
					this.backupSaving = headerInfo.containsKey("backup-saving") ? headerInfo.get("backup-saving", boolean.class) : true;
					this.backupInterval = headerInfo.containsKey("backup-interval") ? headerInfo.get("backup-interval", long.class) : 600L;
				} else Utils.logDebug("Project", String.format("Unable to load project with %s solver. There is no header in project file at: %s", this.solver.getDisplayName(), this.projectFolder.path()));
			} else Utils.logDebug("Project", String.format("Unable to load project with %s solver. There is no project at: %s", this.solver.getDisplayName(), this.projectFolder.path()));
		} catch (InvalidFileFormatException e) {
			Utils.logError(e, "BasicProject#loadHeader", "IFFE unhandled exception");
		} catch (IOException e) {
			Utils.logError(e, "BasicProject#loadHeader", "IO unhandled exception");
		} catch (Exception e) {
			Utils.logError(e, "BasicProject#loadHeader", "Unknown unhandled exception");
		}
	}
	
	public void saveConfiguration() {
		try {
			if (this.config == null) this.config = new Ini();
			this.config.remove("PROJECT-HEADER");
			Section header = this.config.add("PROJECT-HEADER");
			header.add("project-type", this.solver.solverType);
			header.add("project-name", this.name);
			header.add("last-save-date", Instant.now().toString()); 
			header.add("backup-saving", this.backupSaving);
			header.add("backup-interval", this.backupInterval);
			FileHandle projectFile = this.projectFolder.child("project.ini");
			if (!projectFile.exists()) projectFile.file().createNewFile();
			this.config.store(projectFile.file());
		} catch (IOException ioexception) {
			Utils.logError(ioexception, "BasicProject", "Unable to save project: ", this.name);
			ioexception.printStackTrace();
		}
	}
	
	/** Called when project needed to be loaded. Return false to cancel loading. **/
	abstract public boolean loadProject();
	/** Called when project needed to be saved. Return false to fail saving. **/
	abstract public boolean saveProject();
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
	
	public ProjectAssetManager getProjectAssetManager() { return this.assetManager; }
	
	public FileHandle getProjectFolder() { return this.projectFolder; }
	public String getProjectName() { return this.name; }
	public String getProjectType() { return this.solver.getDisplayName(); }
	public String getSaveDate() { return this.lastSaveDate; }
	public boolean isBackupsEnabled() { return this.backupSaving; }
}
