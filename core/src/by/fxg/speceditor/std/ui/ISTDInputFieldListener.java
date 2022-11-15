package by.fxg.speceditor.std.ui;

/** Listener for {@link STDInputField}
 *  Can be used for multiple input fields, because they're using ID system with strings
**/
public interface ISTDInputFieldListener {
	/** Called when {@link STDInputField} focuses **/
	default void onInputFieldFocusAdded(STDInputField inputField, String id) {}
	
	/** Called every update while {@link STDInputField} is focused **/
	default void whileInputFieldFocused(STDInputField inputField, String id) {}
	
	/** Called every update while {@link STDInputField} is not focused **/
	default void whileInputFieldNotFocused(STDInputField inputField, String id) {}
	
	/** Called every text change (Typing, Pasting, Removing text, Clearing, etc.) **/
	default void onInputFieldTextChanged(STDInputField inputField, String id, String textAdded) {}
	
	/** Called when {@link STDInputField} losses his focus **/
	default void onInputFieldFocusRemoved(STDInputField inputField, String id) {}
}
