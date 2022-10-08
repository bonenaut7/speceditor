package by.fxg.speceditor.ui;

/** Listener for {@link STDInputField}
 *  Can be used for multiple input fields, because they're using ID system with strings
**/
public interface ISTDInputFieldListener {
	/** Called when {@link STDInputField} focuses **/
	default void onFocusAdded(STDInputField inputField, String id) {}
	
	/** Called every update while {@link STDInputField} is focused **/
	default void whileFocused(STDInputField inputField, String id) {}
	
	/** Called every update while {@link STDInputField} is not focused **/
	default void whileNotFocused(STDInputField inputField, String id) {}
	
	/** Called when {@link STDInputField} losses his focus **/
	default void onFocusRemoved(STDInputField inputField, String id) {}
}
