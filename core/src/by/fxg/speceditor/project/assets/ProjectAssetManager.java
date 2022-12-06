package by.fxg.speceditor.project.assets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.InputChunked;
import com.esotericsoftware.kryo.io.OutputChunked;

import by.fxg.speceditor.utils.Utils;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class ProjectAssetManager implements Disposable {
	public static ProjectAssetManager INSTANCE;
	protected AssetManager assetManager;
	protected PAMFileHandleResolver resolver;
	protected Map<String, Class<?>> assetTypes = new HashMap<>();
	protected Map<String, SpakArchive> pakArchives = new HashMap<>();
	
	public ProjectAssetManager() {
		INSTANCE = this;
		this.resolver = new PAMFileHandleResolver(this);
		this.assetManager = new AssetManager(this.resolver);

		this.addAssetType(Texture.class, "png", "jpg", "jpeg", "bmp", "cim", "etc1", "ktx", "zktx"); //TODO: Add basis support
		this.addAssetType(Model.class, "obj", "g3db", "g3dj");
		this.addAssetType(SceneAsset.class, "gltf", "glb");
	}
	
	public <T> T getGdxAsset(String path, Class<T> type) {
		return INSTANCE.assetManager.get(path, type);
	}

	public <T> boolean loadGdxAsset(String path, Class<T> type) {
		if (!this.assetManager.isLoaded(path, type)) {
			this.assetManager.load(path, type);
			this.assetManager.finishLoadingAsset(path);
			return this.assetManager.isLoaded(path, type);
		}
		return false;
	}
	
	public <T> boolean unloadGdxAsset(String path, Class<T> type) {
		if (this.assetManager.isLoaded(path, type)) {
			this.assetManager.unload(path);
			return !this.assetManager.isLoaded(path, type);
		}
		return false;
	}
	
	public boolean addPakArchive(FileHandle handle) { return this.addPakArchive(handle.nameWithoutExtension(), handle); }
	public boolean addPakArchive(String name, FileHandle handle) {
		if (handle != null && handle.exists()) {
			if (!this.isPakArchiveExists(name)) {
				try {
					this.pakArchives.put(name, new SpakArchive(handle, name));
					 Utils.logDebug("PAK archive added: ", handle.path());
					 return true;
				} catch (Exception exception) {
					Utils.logError(exception, "ProjectAssetManager", "Unable to add PAK archive from path: ", handle.path());
				}
			} else Utils.logDebug("Unable to add PAK archive, `", name, "` already exists.");
		} else Utils.logDebug("Unable to add PAK archive, path is null or not exists.");
		return false;
	}
	
	public boolean removePakArchive(String name) {
		if (name != null) {
			if (this.isPakArchiveExists(name)) {
				SpakArchive archive = this.pakArchives.remove(name);
				archive.dispose();
				Utils.logDebug("PAK archive `", name, "` removed.");
				return true;
			} else Utils.logDebug("Unable to remove PAK archive `", name, "` because it not exist.");
		} else Utils.logDebug("Unable to remove PAK archive because name is null.");
		return false;
	}
	
	public boolean isPakArchiveExists(String pakName) {
		return this.pakArchives.containsKey(pakName);
	}
	
	public boolean isPakAssetExists(String pakName, String pakAsset) {
		SpakArchive pakArchive = this.getPakArchive(pakName);
		return pakArchive != null && pakArchive.containsEntry(pakName);
	}
	
	public SpakArchive getPakArchive(String pakName) {
		return this.pakArchives.get(pakName);
	}
	
	public SpakAsset<?> getPakAsset(String pakName, String pakAsset) {
		SpakArchive pakArchive = this.getPakArchive(pakName);
		return pakArchive != null ? pakArchive.getPakAsset(pakAsset) : null;
	}
	
	public void saveIndexes(Kryo kryo, OutputChunked output) throws IOException {
		output.writeInt(0x00000000);
		output.writeInt(this.pakArchives.size());
		for (SpakArchive archive : this.pakArchives.values()) {
			output.writeString(archive.getName());
			output.writeString(archive.getFileHandle().file().getAbsolutePath());
		}
	}
	
	public void loadIndexes(Kryo kryo, InputChunked input) throws IOException {
		int version = input.readInt();
		int archives = input.readInt();
		for (int i = 0; i != archives; i++) {
			String name = input.readString();
			FileHandle handle = Gdx.files.absolute(input.readString());
			if (handle.exists()) {
				if (this.addPakArchive(name, handle)) {
					this.getPakArchive(name).loadAssets();
				} else Utils.logWarn("Deserialization", "SpakArchive `", name, "` is not loaded. Skipping...");
			} else Utils.logWarn("Deserialization", "SpakArchive `", name, "` is not found by path `", handle.path(), "`. Skipping...");
		}
	}

	public Map<String, SpakArchive> getSpakArchiveMap() {
		return this.pakArchives;
	}
	
	public Class<?> getTypeForExtension(String extension) {
		return this.assetTypes.get(extension);
	}
	
	private void addAssetType(Class<?> type, String... extensions) {
		for (String extension : extensions) {
			this.assetTypes.put(extension, type);
		}
	}

	public void dispose() {
		for (SpakArchive archive : this.pakArchives.values()) {
			archive.dispose();
		}
		this.assetManager.dispose();
	}
}
