package by.fxg.speceditor.scenes.format;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Disposable;

import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class ScenesAssetIndexer implements Disposable {
	protected AssetManager assetManager;
	protected Map<String, Class<?>> assetTypes;
	protected Map<UUID, String> uuidToPathMap;
	protected ZipFile packedAssetsFile;
	
	public ScenesAssetIndexer(FileHandle packedAssetsFileHandle) throws ZipException, IOException {
		this(new ZipFile(packedAssetsFileHandle.file()));
	}
	
	public ScenesAssetIndexer(ZipFile packedAssetsFile) {
		if (packedAssetsFile == null) throw new NullPointerException("Packed assets file cannot be null");
		this.assetManager = new AssetManager(new ZipFileHandleResolver(packedAssetsFile), true);
		this.assetTypes = new HashMap<>();
		this.uuidToPathMap = new HashMap<>();
		this.packedAssetsFile = packedAssetsFile;
		
		this.addAssetType(Texture.class, "png", "jpg", "jpeg", "bmp", "cim", "etc1", "ktx", "zktx");
		this.addAssetType(Model.class, "obj", "g3db", "g3dj");
		this.addAssetType(SceneAsset.class, "gltf", "glb");
	}
	
	public void loadAssets(boolean finishAssetLoading) {
		Enumeration<? extends ZipEntry> entries = this.packedAssetsFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (entry != null && !entry.isDirectory()) {
				ZipFileHandle handle = new ZipFileHandle(this.packedAssetsFile, entry.getName());
				this.assetManager.load(entry.getName(), this.assetTypes.get(handle.extension()));
				this.uuidToPathMap.put(UUID.fromString(handle.nameWithoutExtension()), entry.getName());
			}
		}
		if (finishAssetLoading) this.assetManager.finishLoading();
	}
	
	public <T> T getAsset(Class<T> type, UUID uuid) {
		if (type == null) throw new NullPointerException("Asset type cannot be null");
		if (uuid == null) throw new NullPointerException("Asset UUID cannot be null");
		return this.assetManager.get(this.getPath(uuid), type);
	}
	
	public String getPath(UUID uuid) {
		return this.uuidToPathMap.get(uuid);
	}
	
	public void addAssetType(Class<?> type, String... extensions) {
		for (String extension : extensions) {
			this.assetTypes.put(extension, type);
		}
	}
	
	public AssetManager getAssetManager() {
		return this.assetManager;
	}
	
	public Map<UUID, String> getUUIDToPathMap() {
		return this.uuidToPathMap;
	}

	public void dispose() {
		this.assetManager.dispose();
		this.assetTypes.clear();
		this.uuidToPathMap.clear();
		this.packedAssetsFile = null;
	}
}
