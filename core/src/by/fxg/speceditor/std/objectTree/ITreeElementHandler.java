package by.fxg.speceditor.std.objectTree;

import by.fxg.speceditor.std.ui.STDDropdownArea;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement;

public interface ITreeElementHandler {
	/** Called when DropdownArea clicked. Return false to cancel default action. **/
	default boolean specObjectTree_onDropdownClick(SpecObjectTree objectTree, STDDropdownArea area, String areaID, STDDropdownAreaElement element, String elementID) { return true; }
	
	/** Called when element being added to DropdownArea. Return false to cancel default action. **/
	default boolean specObjectTree_onDropdownAreaAddElement(SpecObjectTree objectTree, STDDropdownArea area, String areaID, STDDropdownAreaElement parent, STDDropdownAreaElement target) { return true; }
	
	/** Called when any object in ObjectTree changes selection value **/
	default void onRefresh(SpecObjectTree objectTree) {}
}
