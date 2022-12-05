package by.fxg.speceditor.project.assets;

public interface ISpakAssetUser<TYPE> {
	/** Calls when user attaches to the SpakAsset **/
	default void onSpakUserAdded(SpakAsset<TYPE> asset) {}
	/** Calls when user removes from the SpakAsset **/
	default void onSpakUserRemoved(SpakAsset<TYPE> asset) {}
	/** Calls after asset loaded **/
	default void onAssetLoad(SpakAsset<TYPE> asset) {}
	/** Calls before asset unloaded **/
	default void onAssetUnload(SpakAsset<TYPE> asset) {}
}
