package by.fxg.pilesos.specpak;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.GdxRuntimeException;

import by.fxg.pilesos.utils.GDXUtil;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class PakAssetManager {
	protected final AssetManager assetManager;
	protected PakAssetManagerFileHandleResolver resolver;
	protected Map<String, Class<?>> assetTypes = new HashMap<>();
	protected Map<String, PakArchive> pakArchives = new HashMap<>();
	
	public PakAssetManager() {
		this.resolver = new PakAssetManagerFileHandleResolver(this);
		this.assetManager = new AssetManager(this.resolver);
		
		this.addAssetType(Texture.class, "png", "jpg", "jpeg", "bmp", "cim", "etc1", "ktx", "zktx");
		this.addAssetType(Model.class, "obj", "g3db", "g3dj");
		this.addAssetType(SceneAsset.class, "gltf", "glb");
	}
	
	public PakAssetManager queueLoadAssetsFrom(String pakArchive) {
		PakArchive archive = this.pakArchives.get(pakArchive);
		if (archive != null) {
			for (String entry : archive.getEntries()) {
				Class<?> type = this.getTypeForExtension(GDXUtil.pathExtension(entry));
				if (type != null) {
					String path = this.resolver.formatAssetPath(pakArchive, entry);
					if (!this.assetManager.isLoaded(entry, type)) {
						this.assetManager.load(path, type);
					}
				}
			}
		}
		return this;
	}
	
	public PakAssetManager queueUnloadAssetsFrom(String pakArchive) {
		PakArchive archive = this.pakArchives.get(pakArchive);
		if (archive != null) {
			for (String entry : archive.getEntries()) {
				String path = this.resolver.formatAssetPath(pakArchive, entry);
				if (this.assetManager.isLoaded(path)) {
					this.assetManager.unload(path);
				}
			}
		}
		return this;
	}

	public PakAssetManager addPackedAssets(FileHandle... pakFiles) {
		for (FileHandle handle : pakFiles) {
			this.addPackedAsset(handle.nameWithoutExtension(), handle);
		}
		return this;
	}

	public boolean addPackedAsset(String pakName, FileHandle pakFile) {
		if (pakFile == null || !pakFile.exists()) throw new GdxRuntimeException("FileHandle of PAK file provided to PakAssetManager can't be null");
		String name = pakName == null ? pakFile.nameWithoutExtension() : pakName;
		if (this.pakArchives.containsKey(name)) throw new GdxRuntimeException("PAK with name " + name + " already loaded!");
		try {
			ZipFile zipFile = new ZipFile(pakFile.file());
			this.pakArchives.put(name, new PakArchive(name, zipFile));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean removePackedAsset(String archiveName, boolean queueUnload, boolean finishLoading) {
		if (this.pakArchives.containsKey(archiveName)) {
			if (queueUnload) this.queueUnloadAssetsFrom(archiveName);
			if (finishLoading) this.assetManager.finishLoading();
			this.pakArchives.remove(archiveName);
			return true;
		}
		return false;
	}
	
	public <T> boolean isAssetLoaded(String pakArchive, String pakAsset, Class<T> type) { return this.isAssetLoaded(this.resolver.formatAssetPath(pakArchive, pakAsset), type); }
	public <T> boolean isAssetLoaded(String pakPath, Class<T> type) {
		return this.assetManager.isLoaded(pakPath, type);
	}
	
	public boolean isArchivePresent(String pakArchive) {
		return this.pakArchives.containsKey(pakArchive);
	}
	
	public <T> T getLoadAsset(String pakArchive, String pakAsset, Class<T> type) { return this.getLoadAsset(this.resolver.formatAssetPath(pakArchive, pakAsset), type); }
	public <T> T getLoadAsset(String pakPath, Class<T> type) {
		if (!this.isAssetLoaded(pakPath, type)) {
			this.assetManager.load(pakPath, type);
			this.assetManager.finishLoadingAsset(pakPath);
		}
		return this.assetManager.get(pakPath, type);
	}
	
	public <T> T getAsset(String pakArchive, String pakAsset, Class<T> type) { return this.getAsset(this.resolver.formatAssetPath(pakArchive, pakAsset), type); }
	public <T> T getAsset(String pakPath, Class<T> type) {
		return this.assetManager.get(pakPath, type);
	}
	
	public PakArchive getArchive(String pakArchive) {
		return this.pakArchives.get(pakArchive);
	}
	
	public Class<?> getTypeForExtension(String extension) {
		return this.assetTypes.get(extension);
	}
	
	public PakAssetManagerFileHandleResolver getResolver() {
		return this.resolver;
	}

	public AssetManager getAssetManager() {
		return this.assetManager;
	}
	
	private void addAssetType(Class<?> type, String... extensions) {
		for (String extension : extensions) {
			this.assetTypes.put(extension, type);
		}
	}
}
