package by.fxg.speceditor.project;

public interface IProjectAssetHandler<TYPE> {
	/** Calls when handler attaches to the ProjectAsset **/
	default void onAssetHandlerAdded(ProjectAsset<TYPE> asset) {}
	/** Calls when handler removes from the ProjectAsset **/
	default void onAssetHandlerRemoved(ProjectAsset<TYPE> asset) {}
	/** Calls after asset loaded **/
	default void onAssetLoad(ProjectAsset<TYPE> asset) {}
	/** Calls before asset unloaded **/
	default void onAssetUnload(ProjectAsset<TYPE> asset) {}
}
