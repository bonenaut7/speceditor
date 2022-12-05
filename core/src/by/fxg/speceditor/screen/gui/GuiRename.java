package by.fxg.speceditor.screen.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.ColoredInputField;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UHoldButton;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class GuiRename extends Gui {
	protected UButton buttonClose;
	protected UHoldButton buttonConfirm;
	protected STDInputField inputField;
	
	protected String[] text;
	protected int textWidth = 0;
	
	public GuiRename(String... text) {
		super(null);
		this.buttonClose = new UButton("Cancel").setColor(UColor.greenblack);
		this.buttonConfirm = new UHoldButton("Rename", Keys.ENTER, 30).setColor(UColor.yellowblack);
		this.inputField = new ColoredInputField().setAllowFullfocus(false);
		this.inputField.setFocused(true);
		this.text = text;
		
		for (String str : text) {
			if (SpecEditor.fosterNoDraw.setString(str).getWidth() > textWidth) {
				this.textWidth = (int)SpecEditor.fosterNoDraw.getWidth();
			}
		}
		
		this.resize(Utils.getWidth(), Utils.getHeight());
	}
	
	public GuiRename setText(String text, int maxLength) {
		this.inputField.setTextWithPointer(text).setMaxLength(maxLength).dropOffset();
		return this;
	}
	
	public GuiRename setAllowedCharacters(String allowedCharacters) {
		this.inputField.setAllowedCharacters(allowedCharacters);
		return this;
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (SpecEditor.get.getInput().isKeyboardDown(Keys.ESCAPE, false) || this.buttonClose.isPressed()) this.closeGui();
		if (this.buttonConfirm.isPressed()) {
			this.closeGui();
			this.onRenamed(this.inputField.getText());
		}
		this.buttonConfirm.update();
		this.inputField.update();
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		shape.setColor(0, 0, 0, 0.5f);
		shape.filledRectangle(0, 0, width, height);
		
		int boxWidth = Math.max(this.textWidth + 20, 132), boxHeight = 60 + this.text.length * 12;
		int x = width / 2 - boxWidth / 2, y = height / 2 - boxHeight / 2;
		shape.setColor(0.12f, 0.12f, 0.12f, 1);
		shape.filledRectangle(x, y, boxWidth, boxHeight);
		shape.setColor(1, 1, 1, 1);
		shape.rectangle(x, y, boxWidth, boxHeight, 2f);

		int textY = y + boxHeight - 8;
		for (int i = 0; i != this.text.length; i++) {
			foster.setString(this.text[i]).draw(x + boxWidth / 2, textY -= 12);
		}
		
		this.buttonClose.render(shape, foster);
		this.buttonConfirm.render(shape, foster);
		this.inputField.setFoster(foster).render(batch, shape);
		batch.end();
	}
	
	public void resize(int width, int height) {
		int boxWidth = Math.max(this.textWidth + 20, 132), boxHeight = 60 + this.text.length * 12;
		int x = width / 2 - boxWidth / 2, y = height / 2 - boxHeight / 2;
		this.buttonClose.setTransforms(x + boxWidth - 60, y + 10, 50, 13);
		this.buttonConfirm.setTransforms(x + boxWidth - 120, y + 10, 50, 13);
		this.inputField.setTransforms(x + 10, y + 30, boxWidth - 20, 14);
	}
	
	abstract public void onRenamed(String value);
}
