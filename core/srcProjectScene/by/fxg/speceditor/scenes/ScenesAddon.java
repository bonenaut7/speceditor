package by.fxg.speceditor.scenes;

import by.fxg.speceditor.api.addon.AddonInfo;
import by.fxg.speceditor.api.addon.ISpecAddon;
import by.fxg.speceditor.project.ProjectManager;

public class ScenesAddon implements ISpecAddon {
	private AddonInfo info;
	private ScenesProjectSolver solver;
	
	public ScenesAddon() {
		this.info = AddonInfo.create("STD-Scenes", "std-scene", "0.0.0", "-", "FXG");
		this.solver = new ScenesProjectSolver();
	}
	
	public void onLoad() {
		ProjectManager.INSTANCE.registerProjectSolver(this.solver);
	}

	public void onUnload() {
		ProjectManager.INSTANCE.removeProjectSolver(this.solver);
	}

	public AddonInfo getInfo() {
		return this.info;
	}
}
