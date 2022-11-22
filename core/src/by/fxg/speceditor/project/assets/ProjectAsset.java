package by.fxg.speceditor.project.assets;

import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.ResourceManager;
import by.fxg.speceditor.utils.Utils;

public class ProjectAsset<TYPE> {
	private final Array<IProjectAssetHandler<TYPE>> assetHandlers = new Array<>();
	private final Class<TYPE> classType;
	private UUID assetUUID;
	private TYPE asset;
	/** Local path to asset, where `Local path` is project folder **/
	private String pathToAsset;
	private AssetDescriptor<TYPE> descriptor;

	public ProjectAsset(Class<TYPE> type, UUID uuid, String path) {
		this.classType = type;
		this.assetUUID = uuid;
		this.asset = null;
		this.pathToAsset = path;
		this.descriptor = new AssetDescriptor<TYPE>(path, type);
	}

	public ProjectAsset(Class<TYPE> type, String path) {
		this(type, UUID.randomUUID(), path);
	}
	
	public ProjectAsset<TYPE> setUUID(String uuid) {
		this.assetUUID = UUID.fromString(uuid);
		return this;
	}
	
	public FileHandle getFile() {
		return Gdx.files.absolute(this.pathToAsset);
	}
	
	public boolean load() {
		if (ResourceManager.INSTANCE.loadAsset(this.descriptor)) {
			this.asset = ResourceManager.get(this.descriptor);
			this.assetHandlers.forEach(handler -> handler.onAssetLoad(this));
			Utils.logDebug("Asset `", this.assetUUID.toString(), "`(`", this.pathToAsset, "`) loaded.");
			return true;
		}
		return false;
	}
	
	public boolean unload() {
		this.assetHandlers.forEach(handler -> handler.onAssetUnload(this));
		if (ResourceManager.INSTANCE.unloadAsset(this.descriptor)) {
			Utils.logDebug("Asset `", this.assetUUID.toString(), "`(`", this.pathToAsset, "`) unloaded.");
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
			Utils.logDebug("Asset `", this.assetUUID.toString(), "`(`", this.pathToAsset, "`) added handler. Handlers: ", this.assetHandlers.size);
			return true;
		}
		return false;
	}
	
	public boolean removeHandler(IProjectAssetHandler<TYPE> handler) {
		if (this.assetHandlers.removeValue(handler, true)) {
			Utils.logDebug("Asset `", this.assetUUID.toString(), "`(`", this.pathToAsset, "`) removed handler. Handlers left: ", this.assetHandlers.size);
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
	public int getAssetHandlersSize() { return this.assetHandlers.size; }
	
	public final Class<TYPE> getType() { return this.classType; }
}
