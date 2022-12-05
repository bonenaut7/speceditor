package by.fxg.speceditor.project.assets;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import by.fxg.speceditor.utils.Utils;

//Asset of specific PAK
public class SpakAsset<TYPE> implements Disposable {
	private final SpakArchive archive;
	private final Class<TYPE> classType;
	private String assetPath, path;
	private final Array<ISpakAssetUser<TYPE>> users = new Array<>();
	private TYPE asset;

	public SpakAsset(SpakArchive archive, Class<TYPE> type, String assetPath, String path) {
		this.archive = archive;
		this.classType = type;
		this.assetPath = assetPath;
		this.path = path;
		this.asset = null;
	}
	
	protected SpakAsset<TYPE> setPath(String assetPath, String path) {
		this.assetPath = assetPath;
		this.path = path;
		return this;
	}
	
	public boolean load() {
		if (!this.isLoaded()) {
			if (ProjectAssetManager.INSTANCE.loadGdxAsset(this.assetPath, this.classType)) {
				this.asset = ProjectAssetManager.INSTANCE.getGdxAsset(this.assetPath, this.classType);
				this.users.forEach(user -> user.onAssetLoad(this));
				Utils.logDebug("SpakAsset `", this.path, "`(`", this.archive.name, "`) loaded.");
				return true;
			}
		}
		return false;
	}
	
	public boolean unload() {
		if (this.isLoaded()) {
			this.users.forEach(user -> user.onAssetUnload(this));
			if (ProjectAssetManager.INSTANCE.unloadGdxAsset(this.assetPath, this.classType)) {
				Utils.logDebug("SpakAsset `", this.path, "`(`", this.archive.name, "`) unloaded.");
				this.asset = null;
				return true;
			}
		}
		return false;
	}
	
	public boolean isLoaded() {
		return this.asset != null;
	}
	
	public boolean addUser(ISpakAssetUser<TYPE> user) {
		if (!this.users.contains(user, true)) {
			this.users.add(user);
			user.onSpakUserAdded(this);
			Utils.logDebug("SpakAsset `", this.path, "`(`", this.archive.name, "`) added user. Users: ", this.users.size);
			return true;
		}
		return false;
	}
	
	public boolean removeUser(ISpakAssetUser<TYPE> user) {
		if (this.users.removeValue(user, true)) {
			Utils.logDebug("SpakAsset `", this.path, "`(`", this.archive.name, "`) removed user. Users left: ", this.users.size);
			user.onSpakUserRemoved(this);
			return true;
		}
		return false;
	}
	
	public boolean removeUserWithoutNotify(ISpakAssetUser<TYPE> user) {
		return this.users.removeValue(user, true);
	}
	
	public TYPE getAsset() { return this.asset; }
	public int getAssetHandlersSize() { return this.users.size; }
	
	public final String getPath() { return this.path; }
	public final String getAssetPath() { return this.assetPath; }
	public final Class<TYPE> getType() { return this.classType; }
	public final SpakArchive getArchive() { return this.archive; }
	
	public void dispose() {
		this.unload();
		this.users.forEach(user -> user.onSpakUserRemoved(this));
		Utils.logDebug("SpakAsset `", this.path, "`(`", this.archive.name, "`) disposed.");
	}
}
