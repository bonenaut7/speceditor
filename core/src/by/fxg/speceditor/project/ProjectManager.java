package by.fxg.speceditor.project;

import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.utils.Utils;

public class ProjectManager {
	public static ProjectManager INSTANCE;
	public static BasicProject currentProject;
	private Array<IProjectSolver> registeredSolvers = new Array<>();
	
	public ProjectManager() {
		INSTANCE = this;
		Gdx.files.internal("spec/addons/project-types/").file().mkdirs();
		
		Utils.logDebug("Project Manager", "Searching for project solvers...");
		for (FileHandle fileHandle : Gdx.files.internal("spec/addons/project-types/").list(".ini")) {
			if (!fileHandle.isDirectory()) {
				this.discoverSolver(fileHandle);
			}
		}
		Utils.logDebug("Project Manager", String.format("Loaded %s project solvers.", this.registeredSolvers.size));
	}

	private void discoverSolver(FileHandle handle) {
		try {
			Ini solverConfig = new Ini(handle.file());
			if (solverConfig.containsKey("PROJECT-SOLVER") && solverConfig.get("PROJECT-SOLVER").containsKey("project-type")) {
				Section solverInfo = solverConfig.get("PROJECT-SOLVER");
				if (solverInfo.get("project-type").length() > 2 && !solverInfo.get("project-type").equalsIgnoreCase("undefined")) {
					//check required plugins
					if (solverInfo.containsKey("solver-class") && !solverInfo.get("solver-class").equalsIgnoreCase("none")) {
						try {
							Class<?> clazz = Class.forName(solverInfo.get("solver-class"));
							if (clazz != null && IProjectSolver.class.isAssignableFrom(clazz) && clazz.getConstructor(Ini.class) != null) {
								this.registeredSolvers.add((IProjectSolver)clazz.getConstructor(Ini.class).newInstance(solverConfig));
							} else Utils.logDebug("Project Manager", String.format("Found invalid project solver class: %s", solverInfo.get("solver-class")));
						} catch (Exception exception) {
							Utils.logError(exception, "Project Manager", String.format("Something wrong happened while discovering solver: %s", solverInfo.get("solver-class")));
						}
					} else {
						this.registeredSolvers.add(new DefaultProjectSolver(solverConfig));
						Utils.logDebug("Project Manager", String.format("Blank project solver used because solver not configured at: %s", handle.path()));
					}
				} else Utils.logInfo("Project Manager", String.format("Unable to load project solver, incorrect project type at: %s", handle.path()));
			} else Utils.logInfo("Project Manager", String.format("Unable to load project solver, invalid configuration at: %s", handle.path()));
		} catch (InvalidFileFormatException exception) {
			Utils.logError(exception, "Project Manager", String.format("Unable to parse project solver at: %s", handle.path()));
		} catch (IOException exception) {
			Utils.logError(exception, "Project Manager", String.format("IO Exception(%s) while parsing project solver at: %s", exception.getMessage(), handle.path()));
		}
	}
	
	/** Discovers project type, searches and returns available solver **/
	public IProjectSolver discoveryProject(FileHandle projectConfigHandle) {
		try {
			Ini projectConfig = new Ini(projectConfigHandle.file());
			if (projectConfig.containsKey("PROJECT-HEADER") && projectConfig.get("PROJECT-HEADER").containsKey("project-type")) {
				String projectType = projectConfig.get("PROJECT-HEADER").get("project-type");
				for (IProjectSolver solver : this.registeredSolvers) {
					if (solver.acceptProject(projectConfig, projectType)) return solver;
				}
			} else Utils.logDebug("Project Manager", String.format("Unable to discover project config, incorrect header at: %s", projectConfigHandle.path()));
		} catch (InvalidFileFormatException exception) {
			Utils.logError(exception, "Project Manager", String.format("Unable to discovery project config at: %s", projectConfigHandle.path()));
		} catch (IOException exception) {
			Utils.logError(exception, "Project Manager", String.format("IO Exception(%s) while discovering project config at: %s", exception.getMessage(), projectConfigHandle.path()));
		}
		return null;
	}
	
	/** Project solver used for loading specific types of projects that solver supports **/
	public static interface IProjectSolver {
		/** Localized type name for project description **/ String getTypeName();
		/** Flag for project discoverer, reads headers if true **/ boolean acceptProject(Ini projectConfig, String type);
		/** Flag for project discoverer, enables 'load project' button **/ boolean canLoadProject(FileHandle projectConfig);
		/** Returns project blank with loaded header. **/ BasicProject preLoadProject(FileHandle projectConfig);
		/** Called when asked for project creation with this solver. **/ void createProject(FileHandle projectFolder);
	}
}
