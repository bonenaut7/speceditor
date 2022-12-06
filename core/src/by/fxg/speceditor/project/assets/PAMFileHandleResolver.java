package by.fxg.speceditor.project.assets;

import java.util.regex.Pattern;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class PAMFileHandleResolver implements FileHandleResolver {
	private final ProjectAssetManager projectAssetManager;
	protected final String splitter = "&::$", quotedSplitter = Pattern.quote(this.splitter);
	
	protected PAMFileHandleResolver(ProjectAssetManager projectAssetManager) {
		this.projectAssetManager = projectAssetManager;
	}
	
	public FileHandle resolve(String fileName) {
		if (fileName != null) {
			String path = fileName.replace('\\', '/');
			if (path.contains(this.splitter)) {
				String[] props = path.split(this.quotedSplitter);
				SpakArchive spakArchive = this.projectAssetManager.pakArchives.get(props[0]);
				if (spakArchive != null && spakArchive.containsEntry(props[1])) {
					return new SpakFileHandle(spakArchive.pakFile, props[1].replace('\\', '/'));
				}
			} else {
				for (SpakArchive spakArchive : this.projectAssetManager.pakArchives.values()) {
					if (spakArchive.containsEntry(path)) {
						return new SpakFileHandle(spakArchive.pakFile, path.replace('\\', '/'));
					}
				}
			}
		}
		return null;
	}
}

