package by.fxg.speceditor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.ui.STDInputField;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UDropdownClick;
import by.fxg.speceditor.ui.UDropdownSelectMultiple;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.ui.UHoldButton;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScreenTestUI extends BaseScreen {
	public UButton button;
	public UHoldButton holdButton;
	public UDropdownSelectSingle dropdownSelectSingle;
	public UDropdownSelectMultiple dropdownSelectMultiple;
	public UDropdownClick dropdownClick;
	public UCheckbox checkbox;
	
	public STDInputField stdField0, stdField1;
	
	public ScreenTestUI() {
		
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		this.button = new UButton("Button", width / 2, height / 2, 100, 40);
		this.holdButton = new UHoldButton("Hold Button [F]", Keys.F, 120, width / 2, height / 2 - 50, 100, 40);
		this.dropdownSelectSingle = new UDropdownSelectSingle(width / 2 - 60, height / 2 + 20, 50, 20, 20, "Dropdown select single", "OK", "FALSE", "TRUE");
		this.dropdownSelectMultiple = new UDropdownSelectMultiple(width / 2 - 120, height / 2 + 20, 50, 20, 20, "Dropdown select multiple", "ABBA", "ITCHY", "SCRATCHY");
		this.dropdownClick = new UDropdownClick("Dropdown Click", width / 2 - 180, height / 2 + 20, 50, 20, 20, "Open", "Save", "Save as", "Exit");
		this.checkbox = new UCheckbox(width / 2 + 110, height / 2 + 24, 16, 16);
	
		this.stdField0 = new STDInputField(null).setMaxLength(32).setAllowFullfocus(false);
		this.stdField1 = new STDInputField(null).setMaxLength(32);
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		this.holdButton.update();
		this.dropdownSelectSingle.update();
		this.dropdownSelectMultiple.update(foster);
		this.dropdownClick.update();
		this.checkbox.update();
		
		this.stdField0.setFoster(foster).update();
		this.stdField1.setFoster(foster).update();
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		shape.setColor(0.12f, 0.12f, 0.12f, 1f);
		shape.filledRectangle(0, 0, width, height);
		this.button.render(shape, foster);
		this.holdButton.render(shape, foster);
		this.dropdownSelectSingle.render(shape, foster);
		this.dropdownSelectMultiple.render(shape, foster);
		this.dropdownClick.render(shape, foster);
		this.checkbox.render(shape);
		
		this.stdField0.setTransforms(width / 2, height / 2 - 105, 100, 20).render(batch, shape);
		this.stdField1.setTransforms(width / 2, height / 2 - 130, 100, 20).render(batch, shape);
		batch.end();
	}
	
	public void resize(int width, int height) {}
}
