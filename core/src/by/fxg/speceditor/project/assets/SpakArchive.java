package by.fxg.speceditor.project.assets;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

import org.zeroturnaround.zip.ZipInfoCallback;
import org.zeroturnaround.zip.ZipUtil;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import by.fxg.speceditor.utils.StringUtils;
import by.fxg.speceditor.utils.Utils;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SpakArchive implements ZipInfoCallback, Disposable {
	protected final FileHandle pakHandle;
	protected final File pakFile;
	protected String name;
	protected Map<String, SpakAsset<?>> assets = new HashMap<>();
	
	public SpakArchive(FileHandle pakHandle, String name) throws ZipException, IOException {
		this.pakHandle = pakHandle;
		this.pakFile = pakHandle.file();
		this.name = name;
		
		ZipUtil.iterate(this.pakFile, this);
	}
	
	/** @returns entry path or null **/
	public String addAsset(FileHandle handle) {
		if (handle != null && handle.exists()) {
			Class<?> type = ProjectAssetManager.INSTANCE.getTypeForExtension(handle.extension());
			if (type != null) {
				String entry = this.containsEntry(handle.name()) ? Utils.format(UUID.randomUUID(), ".", handle.extension()) : handle.name();
				ZipUtil.addEntry(this.pakFile, entry, handle.file());
				this.assets.put(entry, new SpakAsset(this, type, this.name + ProjectAssetManager.INSTANCE.resolver.splitter + entry, entry));
				Utils.logDebug("SpakAsset `", entry + "` imported to `", this.name, "`");
				return entry;
			} else Utils.logDebug("SpakAsset `", handle.path(), "` can't be added, unknown asset type `", handle.extension(), "`.");
		} else Utils.logDebug("SpakAsset can't be added, handle is null.");
		return null;
	}
	
	public boolean renameAsset(String entry, String newEntry) {
		if (entry != null && newEntry != null) {
			if (this.containsEntry(entry)) {
				try {
					Map<String, Object> env = new HashMap<>();
					env.put("create", true);
					FileSystem zipFS = FileSystems.newFileSystem(this.pakFile.toPath(), null);
					Files.move(zipFS.getPath(entry), zipFS.getPath(newEntry));
					zipFS.close();
					SpakAsset<?> asset = this.assets.remove(entry);
					this.assets.put(newEntry, asset.setPath(this.name + ProjectAssetManager.INSTANCE.resolver.splitter + newEntry, newEntry));
					Utils.logDebug("SpakAsset `", entry, "(`", this.name, "`) renamed to `", newEntry, "`");
					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else Utils.logDebug("SpakAsset `", entry, "` can't be renamed, entry is not found.");
		} else Utils.logDebug("SpakAsset can't be renamed, entry name is null.");
		return false;
	}
	
	public boolean deleteAsset(String path) {
		if (this.containsEntry(path)) {
			this.assets.remove(path).dispose();
			ZipUtil.removeEntry(this.pakFile, path);
			Utils.logDebug("SpakAsset `", path, "(`", this.name, "`) deleted.");
			return true;
		} else Utils.logDebug("SpakAsset can't be deleted, handle is null.");
		return false;
	}
	
	public void loadAssets() {
		for (SpakAsset<?> asset : this.assets.values()) {
			asset.load();
		}
	}
	
	public void unloadAssets() {
		for (SpakAsset<?> asset : this.assets.values()) {
			asset.unload();
		}
	}
	
	public SpakAsset<?> getPakAsset(String path) {
		return this.assets.get(path);
	}
	
	public boolean containsEntry(String entryName) {
		return ZipUtil.containsEntry(this.pakFile, entryName.replace('\\', '/'));
	}
	
	public String getName() { return this.name; }
	public FileHandle getFileHandle() { return this.pakHandle; }
	public File getFile() { return this.pakFile; }
	public Map<String, SpakAsset<?>> getAssetsMap() { return this.assets; }
	
	@Override //processed by ZipUtil#iterate from constructor, makes SpakAsset objects for valid assets
	public void process(ZipEntry zipEntry) throws IOException {
		Class<?> type = ProjectAssetManager.INSTANCE.getTypeForExtension(StringUtils.extensionOf(zipEntry.getName()));
		if (type != null) {
			this.assets.put(zipEntry.getName(), new SpakAsset(this, type, this.name + ProjectAssetManager.INSTANCE.resolver.splitter + zipEntry.getName(), zipEntry.getName()));
			Utils.logDebug("SpakAsset added `", zipEntry.getName() + "` from `", this.name, "`");
		}
	}
	
	public void dispose() {
		for (SpakAsset<?> asset : this.assets.values()) {
			asset.dispose();
		}
		this.assets.clear();
	}
}
