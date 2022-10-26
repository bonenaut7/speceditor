package by.fxg.speceditor.project;

import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.utils.Utils;

public class ProjectManager {
	public static ProjectManager INSTANCE;
	public static BasicProject currentProject;
	private Array<ProjectSolver> registeredSolvers = new Array<>();
	
	public ProjectManager() {
		INSTANCE = this;
	}
	
	public void postInit() {
		Utils.logDebug("[ProjectManager] Loaded ", this.registeredSolvers.size, " project solvers.");
	}
	
	/** Discovers project type, searches and returns available solver **/
	public ProjectSolver discoverProject(FileHandle projectConfigHandle) {
		try {
			Ini projectConfig = new Ini(projectConfigHandle.file());
			if (projectConfig.containsKey("PROJECT-HEADER") && projectConfig.get("PROJECT-HEADER").containsKey("project-type")) {
				String projectType = projectConfig.get("PROJECT-HEADER").get("project-type");
				for (ProjectSolver solver : this.registeredSolvers) {
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
	
	public boolean registerProjectSolver(ProjectSolver solver) {
		if (solver != null && !this.registeredSolvers.contains(solver, true)) {
			this.registeredSolvers.add(solver);
			return true;
		}
		return false;
	}
	
	public boolean removeProjectSolver(ProjectSolver solver) {
		if (solver != null) {
			return this.registeredSolvers.removeValue(solver, true);
		}
		return false;
	}
	
	public Iterable<ProjectSolver> getSolvers() {
		return this.registeredSolvers;
	}
}
