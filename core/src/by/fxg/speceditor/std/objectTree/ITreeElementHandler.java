package by.fxg.speceditor.std.objectTree;

public interface ITreeElementHandler {
	/** Called when DropDown clicked. Return true to cancel default action. **/
	boolean onDropdownClick(SpecObjectTree objectTree, String id);
	
	/** Called when any object in ObjectTree changes selection value **/
	void onRefresh(SpecObjectTree objectTree);
}
