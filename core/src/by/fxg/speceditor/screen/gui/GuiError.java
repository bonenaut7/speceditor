package by.fxg.speceditor.screen.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.render.RenderManager;
import by.fxg.speceditor.screen.BaseScreen;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GuiError extends BaseScreen {
	private final IFocusable focusedObject;
	private Array<String> strings = new Array<>();
	private UButton buttonClose;
	
	public GuiError(String exceptionPlace, Throwable exception) {
		this.strings.add("We've got error at: " + exceptionPlace, "");
		
		if (exception != null) {
			this.strings.add(exception.getMessage() == null ? exception.getClass().getTypeName() : exception.getMessage(), "");
			for (int i = 0; i != 16 && i < exception.getStackTrace().length; i++) {
				this.strings.add(exception.getStackTrace()[i].toString() == null ? "null" : exception.getStackTrace()[i].toString());
			}
		} else this.strings.add("No exception provided! :(");
		
		this.buttonClose = new UButton("Cancel");
		
		this.resize(Utils.getWidth(), Utils.getHeight());
		this.focusedObject = SpecInterface.INSTANCE.currentFocus;
		SpecInterface.INSTANCE.currentFocus = null;
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (SpecEditor.get.getInput().isKeyboardDown(Keys.ESCAPE, false) || this.buttonClose.isPressed()) {
			SpecEditor.get.renderer.currentGui = null;
			SpecInterface.INSTANCE.currentFocus = this.focusedObject;
		}
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		shape.setColor(0, 0, 0, 0.5f);
		shape.filledRectangle(0, 0, width, height);
				
		float longestString = 0f;
		for (String str : this.strings) {
			foster.setString(str);
			if (foster.getWidth() > longestString) longestString = foster.getWidth();
		}
	
		float boxSizeX = Math.max(longestString + 20, width / 4);
		float boxSizeY = 80 + this.strings.size * foster.getHeight();
		
		float x = width / 2 - boxSizeX / 2, y = height / 2 - boxSizeY / 2;
		shape.setColor(0.12f, 0.12f, 0.12f, 1);
		shape.filledRectangle(x, y, boxSizeX, boxSizeY);
		shape.setColor(1, 1, 1, 1);
		shape.rectangle(x, y, boxSizeX, boxSizeY, 2f);
		for (int i = 0; i != this.strings.size; i++) {
			foster.setString(this.strings.get(i)).draw(x + boxSizeX / 2, y + boxSizeY - 20 - (foster.getHeight() + 2) * i);
		}
		this.buttonClose.render(shape, foster);
		batch.end();
	}
	
	public void resize(int width, int height) {
		float longestString = 0f;
		for (String str : strings) {
			RenderManager.foster.setString(str);
			if (RenderManager.foster.getWidth() > longestString) longestString = RenderManager.foster.getWidth();
		}
		
		float boxSizeX = Math.max(longestString + 20, width / 4);
		float x = width / 2 - boxSizeX / 2, y = height / 2 - (80 + this.strings.size * RenderManager.foster.getHeight()) / 2;
		int buttonWidth = ((int)boxSizeX / 2 - 30) / 2;
		this.buttonClose.setTransforms((int)(x + boxSizeX) - 5 - buttonWidth, (int)y + 5, buttonWidth, 15);
	}
}
