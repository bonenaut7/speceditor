package by.fxg.speceditor.project.assets;

import java.util.UUID;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.ResourceManager;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.utils.Utils;

public class ProjectAsset<TYPE> {
	private final Array<IProjectAssetHandler<TYPE>> assetHandlers = new Array<>();
	private UUID assetUUID;
	private TYPE asset;
	/** Local path to asset, where `Local path` is project folder **/
	private String pathToAsset;
	private AssetDescriptor<TYPE> descriptor;

	public ProjectAsset(Class<TYPE> type, String path, boolean isPathAbsolute) {
		this.assetUUID = UUID.randomUUID();
		this.asset = null;
		if (isPathAbsolute) {
			this.pathToAsset = path.replace("\\", "/").split(Utils.format(ProjectManager.currentProject.getProjectFolder().file().getAbsolutePath().replace("\\", "/"), "/"))[1];
			this.descriptor = new AssetDescriptor<TYPE>(path, type);
		} else {
			this.pathToAsset = path;
			this.descriptor = new AssetDescriptor<TYPE>(ProjectManager.currentProject.getProjectFolder().child(path).file().getAbsolutePath(), type);
		}
	}
	
	public ProjectAsset<TYPE> setUUID(String uuid) {
		this.assetUUID = UUID.fromString(uuid);
		return this;
	}
	
	public FileHandle getFile() {
		return ProjectManager.currentProject.getProjectFolder().child(this.pathToAsset);
	}
	
	public boolean load() {
		if (ResourceManager.INSTANCE.loadAsset(this.descriptor)) {
			this.asset = ResourceManager.INSTANCE.get(this.descriptor);
			this.assetHandlers.forEach(handler -> handler.onAssetLoad(this));
			Utils.logDebug("asset `", this.assetUUID.toString(), "`(`", this.pathToAsset, "`) loaded.");
			return true;
		}
		return false;
	}
	
	public boolean unload() {
		this.assetHandlers.forEach(handler -> handler.onAssetUnload(this));
		if (ResourceManager.INSTANCE.unloadAsset(this.descriptor)) {
			Utils.logDebug("asset `", this.assetUUID.toString(), "`(`", this.pathToAsset, "`) unloaded.");
			this.asset = null;
			return true;
		}
		return false;
	}
	
	public boolean isLoaded() {
		return this.asset != null;
	}
	
	public boolean addHandler(IProjectAssetHandler<TYPE> handler) {
		if (!this.assetHandlers.contains(handler, true)) {
			this.assetHandlers.add(handler);
			handler.onAssetHandlerAdded(this);
			Utils.logDebug("asset `", this.assetUUID.toString(), "`(`", this.pathToAsset, "`) added handler. Handlers: ", this.assetHandlers.size);
			return true;
		}
		return false;
	}
	
	public boolean removeHandler(IProjectAssetHandler<TYPE> handler) {
		if (this.assetHandlers.removeValue(handler, true)) {
			Utils.logDebug("asset `", this.assetUUID.toString(), "`(`", this.pathToAsset, "`) removed handler. Handlers left: ", this.assetHandlers.size);
			handler.onAssetHandlerRemoved(this);
			return true;
		}
		return false;
	}
	
	public boolean removeHandlerWithoutNotify(IProjectAssetHandler<TYPE> handler) {
		return this.assetHandlers.removeValue(handler, true);
	}
	
	public UUID getUUID() { return this.assetUUID; }
	public TYPE getAsset() { return this.asset; }
	public String getPathToAsset() { return this.pathToAsset; }
	public AssetDescriptor<TYPE> getAssetDescriptor() { return this.descriptor; }
}