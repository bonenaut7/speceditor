package by.fxg.speceditor.project;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import by.fxg.speceditor.utils.Utils;

public class ProjectAssetManager implements Disposable {
	public static ProjectAssetManager INSTANCE;
	private Map<UUID, ProjectAsset<?>> projectAssets = new HashMap<>();
	private Map<Path, UUID> pathToUUIDMap = new HashMap<>();
	
	public ProjectAssetManager() {
		INSTANCE = this;
	}
	
	public boolean hasAsset(FileHandle handle) {
		return this.pathToUUIDMap.containsKey(handle.file().toPath());
	}
	
	public void addAsset(ProjectAsset<?> projectAsset) {
		this.projectAssets.put(projectAsset.getUUID(), projectAsset);
		this.pathToUUIDMap.put(projectAsset.getFile().file().toPath(), projectAsset.getUUID());
		Utils.logDebug("New asset added `", projectAsset.getPathToAsset() + "` with uuid: ", projectAsset.getUUID().toString());
	}
	
	public <TYPE> ProjectAsset<TYPE> getAsset(Class<TYPE> clazz, FileHandle handle) {
		if (this.hasAsset(handle)) {
			return (ProjectAsset<TYPE>)this.projectAssets.get(this.pathToUUIDMap.get(handle.file().toPath()));
		}
		return null;
	}
	
	public <TYPE> ProjectAsset<TYPE> getLoadAsset(Class<TYPE> clazz, FileHandle handle) {
		if (!this.hasAsset(handle)) {
			ProjectAsset<TYPE> projectAsset = new ProjectAsset<>(clazz, handle.file().getAbsolutePath(), true);
			projectAsset.load();
			this.addAsset(projectAsset);
			return projectAsset;
		}
		return this.getAsset(clazz, handle);
	}
	
	public void loadAssetIndexes(FileHandle indexesFile) {
		
	}
	
	public void saveAssetIndexes(FileHandle indexesFile) {
		
	}

	public void dispose() {
		for (ProjectAsset<?> projectAsset : this.projectAssets.values()) {
			if (projectAsset.isLoaded()) {
				projectAsset.unload();
			}
		}
		this.pathToUUIDMap.clear();
		this.projectAssets.clear();
	}
}
