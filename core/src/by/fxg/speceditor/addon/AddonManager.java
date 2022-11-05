package by.fxg.speceditor.addon;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.api.addon.ISpecAddon;
import by.fxg.speceditor.scenes.ScenesAddon;
import by.fxg.speceditor.utils.Utils;

public class AddonManager {
	public static AddonManager INSTANCE;
	private Array<ISpecAddon> addons = new Array<>();
	private Map<String, ISpecAddon> activeAddons = new HashMap<>();
	
	public AddonManager() {
		this.discoverAddons();
		this.addons.forEach(this::loadAddon);
	}
	
	public void postInit() {
		Utils.logDebug("[AddonManager] Loaded ", this.addons.size, " addons; active: ", this.activeAddons.size());
	}
	
	private void discoverAddons() {
		//this.addons.add(new SceneAddon()); //standard scene project addon
		this.addons.add(new ScenesAddon()); //standard prefab project addon
		
		// addons discovery
	}
	
	public void loadAddon(ISpecAddon addon) {
		if (addon != null) {
			if (addon.getInfo() != null) {
				if (!this.activeAddons.containsKey(addon.getInfo().addonID)) {
					if (this.checkDependencies(addon.getInfo().dependencies)) {
						this.activeAddons.put(addon.getInfo().addonID, addon);
						addon.onLoad();
					} else ; //FIXME Add dependency.missing message, dependencies are not implemented
				} else Utils.logDebug("[AddonManager#loadAddon] ", "adready loaded");
			} else Utils.logDebug("[AddonManager#loadAddon] ", "null info");
		} else Utils.logDebug("[AddonManager#loadAddon] ", "null addon");
	}
	
	public void unloadAddon(ISpecAddon addon) {
		if (addon != null) {
			if (addon.getInfo() != null) {
				if (this.activeAddons.containsKey(addon.getInfo().addonID)) {

				} else Utils.logDebug("[AddonManager#loadAddon] ", "not loaded");
			} else Utils.logDebug("[AddonManager#loadAddon] ", "null info");
		} else Utils.logDebug("[AddonManager#loadAddon] ", "null addon");
	}
	
	private boolean checkDependencies(String dependencies) {
		return true;
	}
}
