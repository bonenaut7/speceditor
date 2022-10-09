package by.fxg.speceditor.api.addon;

public interface ISpecAddon {
	AddonInfo getInfo();

	void onLoad();
	void onUnload();
}
