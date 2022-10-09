package by.fxg.speceditor.prefabs;

import by.fxg.speceditor.api.addon.AddonInfo;
import by.fxg.speceditor.api.addon.ISpecAddon;
import by.fxg.speceditor.project.ProjectManager;

public class PrefabAddon implements ISpecAddon {
	private AddonInfo info;
	private PrefabProjectSolver solver;
	
	public PrefabAddon() {
		this.info = AddonInfo.create("STD-Prefabs", "std-prefab", "0.0.0", "-", "FXG");
		this.solver = new PrefabProjectSolver();
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
