package by.fxg.speceditor.screen.gui;

import com.badlogic.gdx.utils.Null;

import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.screen.BaseScreen;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;

public abstract class Gui extends BaseScreen {
	protected final IFocusable focusedObject;
	
	/** Provide null value **/
	public Gui(@Null Gui gui) {
		this.focusedObject = SpecInterface.INSTANCE.currentFocus;
		SpecInterface.INSTANCE.currentFocus = null;
	}
	
	public void closeGui() {
		SpecEditor.get.renderer.currentGui = null;
		SpecInterface.INSTANCE.currentFocus = this.focusedObject;
	}
}
