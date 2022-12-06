package by.fxg.speceditor.std.ui;

import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UDragArea;
import by.fxg.speceditor.ui.UDropdownClick;
import by.fxg.speceditor.ui.UDropdownSelectMultiple;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.ui.UHoldButton;
import by.fxg.speceditor.ui.UIOptionSelectMultipleList;
import by.fxg.speceditor.ui.UIOptionSelectSingleList;

public interface ISTDInterfaceActionListener {
	//default void onButtonAction(UButton button, String id) {}
	default void onHoldButtonAction(UHoldButton holdButton, String id, int ticks) {}
	default void onCheckboxAction(UCheckbox checkbox, String id) {}
	default void onDropdownClickAction(UDropdownClick dropdownClick, String id, int variant) {}
	default void onDropdownSelectSingleAction(UDropdownSelectSingle dropdownSelectSingle, String id, int variant) {}
	default void onDropdownSelectMultipleAction(UDropdownSelectMultiple dropdownSelectMultiple, String id, int variant) {}
	default void onDragAreaDrag(UDragArea dragArea, String id, int start, int value, boolean stopFocus) {}
	default void onOptionSingleListAction(UIOptionSelectSingleList optionSingleList, String id, int option) {}
	default void onOptionMultipleListAction(UIOptionSelectMultipleList optionMultipleList, String id, int option, boolean value) {}
	
	default void onDropdownAreaClick(STDDropdownArea area, String id, STDDropdownAreaElement element, String elementID) {}
	default boolean onDropdownAreaAddElement(STDDropdownArea area, String id, STDDropdownAreaElement parent, STDDropdownAreaElement target) { return true; }
}
