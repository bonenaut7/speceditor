package by.fxg.pilesos.specpak;

import java.util.regex.Pattern;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.pilesos.utils.ZipFileHandle;

public class PakAssetManagerFileHandleResolver implements FileHandleResolver {
	private final PakAssetManager pakAssetManager;
	protected final String splitter = "&::$", quotedSplitter = Pattern.quote(this.splitter);
	
	protected PakAssetManagerFileHandleResolver(PakAssetManager pakAssetManager) {
		this.pakAssetManager = pakAssetManager;
	}
	
	public String formatAssetPath(String pakArchive, String entryName) {
		return GDXUtil.format(pakArchive, this.splitter, entryName);
	}
	
	public FileHandle resolve(String fileName) {
		if (fileName != null) {
			String path = fileName.replace('\\', '/');
			if (path.contains(this.splitter)) {
				String[] props = path.split(this.quotedSplitter);
				PakArchive pakArchive = this.pakAssetManager.pakArchives.get(props[0]);
				if (pakArchive != null && pakArchive.containsEntry(props[1])) {
					return new ZipFileHandle(pakArchive.getZipFile(), props[1].replace('\\', '/'));
				}
			} else {
				for (PakArchive pakArchive : this.pakAssetManager.pakArchives.values()) {
					if (pakArchive.containsEntry(path)) {
						return new ZipFileHandle(pakArchive.getZipFile(), path.replace('\\', '/'));
					}
				}
			}
		}
		return null;
	}
}
