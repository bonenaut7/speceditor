package by.fxg.speceditor.project.assets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
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
	
	public <TYPE> ProjectAsset<TYPE> getAsset(Class<TYPE> type, FileHandle handle) {
		if (this.hasAsset(handle)) {
			return (ProjectAsset<TYPE>)this.projectAssets.get(this.pathToUUIDMap.get(handle.file().toPath()));
		}
		return null;
	}
	
	public <TYPE> ProjectAsset<TYPE> getAsset(Class<TYPE> type, UUID uuid) {
		ProjectAsset<?> projectAsset = this.projectAssets.get(uuid);
		return projectAsset != null && projectAsset.getAsset() != null ? (projectAsset.getAsset().getClass().isAssignableFrom(type) ? (ProjectAsset<TYPE>)projectAsset : null) : null;
	}
	
	public ProjectAsset<?> getAsset(UUID uuid) {
		return this.projectAssets.get(uuid);
	}
	
	public <TYPE> ProjectAsset<TYPE> getLoadAsset(Class<TYPE> clazz, FileHandle handle) {
		if (!this.hasAsset(handle)) {
			ProjectAsset<TYPE> projectAsset = new ProjectAsset<>(clazz, handle.file().getAbsolutePath());
			projectAsset.load();
			this.addAsset(projectAsset);
			return projectAsset;
		}
		return this.getAsset(clazz, handle);
	}
	
	public void saveIndexes(DataOutputStream dos) throws IOException {
		Array<Class<?>> indexes = new Array<>();
		Array<ProjectAsset<?>> unidentifiedAssets = new Array<>();
		for (ProjectAsset<?> projectAsset : INSTANCE.projectAssets.values()) { //idk what the fuck is happened, but got this working only through INSTANCE lol...
			if (projectAsset.getType() != null) {
				if (!indexes.contains(projectAsset.getType(), true) && projectAsset.getAssetHandlersSize() > 0) {
					indexes.add(projectAsset.getType());
				}
			} else unidentifiedAssets.add(projectAsset);
		}
		if (unidentifiedAssets.size > 0) Utils.logError(null, "ProjectAssetManager", "There is ", unidentifiedAssets.size, " assets that can't be indexed!");
		
		
		dos.writeInt(0x00000000); //version
		dos.writeInt(indexes.size); //size of type indexes
		dos.writeInt(this.projectAssets.size() - unidentifiedAssets.size); //size of indexes
		for (int i = 0; i != indexes.size; i++) dos.writeUTF(indexes.get(i).getTypeName());
		for (ProjectAsset<?> projectAsset : this.projectAssets.values()) {
			dos.writeInt(indexes.indexOf(projectAsset.getType(), true));
			dos.writeUTF(projectAsset.getPathToAsset());
			dos.writeUTF(projectAsset.getUUID().toString());
		}
	}
	
	public void loadIndexes(DataInputStream dis) throws IOException {
		dis.skip(4); //int version = dis.readInt();
		int typeIndexesSize = dis.readInt();
		int indexesSize = dis.readInt();
		Array<Class<?>> typeIndexes = new Array<>();
		
		for (int i = 0; i != typeIndexesSize; i++) {
			String classType = dis.readUTF();
			try {
				typeIndexes.add(Class.forName(classType));
			} catch (ClassNotFoundException classNotFoundException) {
				Utils.logDebug("[ProjectAssetManager][loadIndexes] Index class not found, skipping... (", classType, ")");
				typeIndexes.add(null);
			}
		}
		for (int i = 0; i != indexesSize; i++) {
			int index = dis.readInt();
			String path = dis.readUTF();
			UUID uuid = UUID.fromString(dis.readUTF());
			if (index > -1) {
				ProjectAsset<?> projectAsset = new ProjectAsset(typeIndexes.get(index), uuid, path);
				projectAsset.load();
				this.addAsset(projectAsset);
			}
		}
	}
	
	public Map<UUID, ProjectAsset<?>> getAssetMap() {
		return this.projectAssets;
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
