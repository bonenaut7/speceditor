package by.fxg.speceditor.addon;

import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.api.addon.ISpecAddon;

public class AddonManager {
	public static AddonManager INSTANCE;
	private Array<ISpecAddon> activeAddons = new Array<>();
	
	public AddonManager() {
		INSTANCE = this;
		
	}
	
	public void discoverAddons() {
		
	}
	
	public void loadAddons() {
		
	}
	
	public void loadAddon() {
		
	}
	
	public void unloadAddon() {
		
	}
}
