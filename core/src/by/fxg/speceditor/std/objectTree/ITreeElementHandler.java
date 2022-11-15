package by.fxg.speceditor.std.objectTree;

public interface ITreeElementHandler {
	/** Called when DropDown clicked. Return true to cancel default action. **/
	default boolean onDropdownClick(SpecObjectTree objectTree, String id) { return false; }
	
	/** Called when any object in ObjectTree changes selection value **/
	default void onRefresh(SpecObjectTree objectTree) {}
}
