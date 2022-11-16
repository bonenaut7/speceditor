package by.fxg.speceditor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.TextureFrameBuffer;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.ResourceManager;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.render.RenderManager;
import by.fxg.speceditor.std.ui.STDDropdownArea;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.ColoredInputField;
import by.fxg.speceditor.ui.NumberCursorInputField;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UDropdownClick;
import by.fxg.speceditor.ui.UDropdownSelectMultiple;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.ui.UHoldButton;
import by.fxg.speceditor.ui.URenderBlock;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScreenTestUI extends BaseScreen {
	public UButton button;
	public UHoldButton holdButton;
	public UDropdownSelectSingle dropdownSelectSingle;
	public UDropdownSelectMultiple dropdownSelectMultiple;
	public UDropdownClick dropdownClick;
	public STDDropdownArea dropdownArea;
	public UCheckbox checkbox;
	public URenderBlock renderBlock;
	
	public STDInputField stdField, coloredField, numberField;
	public TextureFrameBuffer tfb;
	
	public ScreenTestUI() {
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		
		this.button = new UButton("Button");
		this.holdButton = new UHoldButton("Hold Button [F]", Keys.F, 120);
		this.dropdownSelectSingle = new UDropdownSelectSingle(20, "Dropdown select single", "OK", "FALSE", "TRUE");
		this.dropdownSelectMultiple = new UDropdownSelectMultiple(20, "Dropdown select multiple", "ABBA", "ITCHY", "SCRATCHY");
		this.dropdownClick = new UDropdownClick("Dropdown Click", 20, "Open", "Save", "Save as", "Exit");
		this.dropdownArea = new STDDropdownArea(15);
		Array<STDDropdownAreaElement> array = new Array<>();
		array.add(STDDropdownAreaElement.button("", "First"));
		array.add(STDDropdownAreaElement.subwindow("Second")
				.add(STDDropdownAreaElement.button("", "Scnd-First"))
				.add(STDDropdownAreaElement.button("", "Scnd-Second"))
				.add(STDDropdownAreaElement.button("", "Scnd-Third")));
		array.add(STDDropdownAreaElement.subwindow("Third")
				.add(STDDropdownAreaElement.subwindow("Thrd-First").add(STDDropdownAreaElement.button("", "ThrdFrst-First")))
				.add(STDDropdownAreaElement.subwindow("Thrd-Second").add(STDDropdownAreaElement.button("", "ThrdScnd-First")))
				.add(STDDropdownAreaElement.subwindow("Thrd-Third").add(STDDropdownAreaElement.button("", "ThrdThird-First")))
				);
		array.add(STDDropdownAreaElement.subwindow("Fourth").add(STDDropdownAreaElement.subwindow("Fourth-0").add(STDDropdownAreaElement.subwindow("Fourth-1").add(STDDropdownAreaElement.subwindow("Fourth-2")))));
		array.add(STDDropdownAreaElement.line());
		array.add(STDDropdownAreaElement.button("", "Fifth"));
		this.dropdownArea.setElements(array, SpecEditor.fosterNoDraw);
		this.checkbox = new UCheckbox();
		this.renderBlock = new URenderBlock("RenderBlock") {
			protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
				shape.setColor(Color.ORANGE);
				shape.filledRectangle(this.x, yOffset -= 50, this.width, 50);
				//yOffset-=50;
				foster.setString("Contents").draw(this.x + this.width / 2, yOffset + 24);
				return yOffset;
			}
		};
		
		this.stdField = new STDInputField().setMaxLength(32);
		this.coloredField = new ColoredInputField().setMaxLength(32).setAllowFullfocus(false);
		this.numberField = new NumberCursorInputField().setMaxLength(32).setAllowFullfocus(false);
		this.tfb = new TextureFrameBuffer().flip(false, true);
		this.tfb.getTexture().getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		this.resize(width, height);
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		foster.setFont(ResourceManager.smallFont);
		this.holdButton.update();
		this.dropdownSelectSingle.update();
		this.dropdownSelectMultiple.update(foster);
		this.dropdownClick.update();
		this.checkbox.update();
		if (this.button.isPressed()) {
			this.dropdownArea.open(width - 365, height - 20);
		}
		
		this.stdField.setFoster(foster).update();
		this.coloredField.setFoster(foster).update();
		this.numberField.setFoster(foster).update();
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		boolean framebuffer = true;
		if (framebuffer) this.tfb.capture();
		
		batch.begin();
		shape.setColor(0.12f, 0.12f, 0.12f, 1f);
		shape.filledRectangle(0, 0, width, height);
		this.button.render(shape, foster);
		this.holdButton.render(shape, foster);
		this.dropdownClick.render(shape, foster);
		if (!this.dropdownClick.isFocused()) {
			this.dropdownSelectSingle.render(shape, foster);
			if (!this.dropdownSelectSingle.isFocused()) {
				this.dropdownSelectMultiple.render(shape, foster);
				if (!this.dropdownSelectMultiple.isFocused()) {
					this.checkbox.render(shape);
				}
			}
		}
		
		boolean snap = framebuffer;
		shape.setColor(UColor.aquagray);
		shape.line(width - 350, height - 50, width - 300, height - 50, 1, snap);
		shape.line(width - 350, height - 100, width - 350, height - 50, 1, snap);
		shape.line(width - 350, height - 100, width - 300, height - 100, 1, snap);
		shape.line(width - 300, height - 100, width - 300, height - 50, 1, snap);
		
		this.renderBlock.render(batch, shape, foster, height - 5);
		this.stdField.render(batch, shape);
		this.coloredField.render(batch, shape);
		this.numberField.render(batch, shape);
		this.dropdownArea.render(shape, foster);
		batch.flush();
		batch.end();
		
		if (framebuffer) {
			this.tfb.endCapture();
			batch.begin();
			batch.draw(this.tfb.getTexture(), 0, 0);
			
			float mod = 2.0f;
			float startX = width - 370, endX = width;
			float startY = height - 305, endY = height;
			float minU = startX / width, maxU = endX / width, minV = startY / height, maxV = endY / height;
			float sizeX = ((maxU - minU) * width) * mod, sizeY = ((maxV - minV) * height) * mod;
			float mouseU = GDXUtil.getMouseX() / (float)width, mouseV = GDXUtil.getMouseY() / (float)height;
			float mouseX = (GDXUtil.getMouseX() - startX) * mod, mouseY = (GDXUtil.getMouseY() - startY) * mod;
			batch.draw(this.tfb.getTexture().getTexture(), 0, height - sizeY, sizeX, sizeY, minU, minV, maxU, maxV);
			if (mouseU > minU && mouseU < maxU && mouseV > minV && mouseV < maxV) {
				shape.setColor(Color.RED);
				shape.rectangle(mouseX, height - sizeY + mouseY, mod, mod);
			}
			shape.setColor(Color.WHITE);
			shape.rectangle(0, height - sizeY, sizeX, sizeY);
			foster.setString(Utils.format("Zoom: x", Utils.dFormat(mod, 0), ". Width: ", sizeX, ", Height: ", sizeY)).draw(5, height - sizeY - foster.getHeight() - 5, Align.left);
			batch.end();
		}
	}
	
	public void resize(int width, int height) {
		this.button.setTransforms(width - 105, height - 25, 100, 20);
		this.holdButton.setTransforms(width - 105, height - 50, 100, 20);
		this.dropdownClick.setTransforms(width - 210, height - 25, 100, 20);
		this.dropdownSelectSingle.setTransforms(width - 210, height - 50, 100, 20);
		this.dropdownSelectMultiple.setTransforms(width - 210, height - 75, 100, 20);
		this.checkbox.setTransforms(width - 235, height - 100, 20, 20);
		this.renderBlock.setTransforms(width - 365, 150);
		this.stdField.setTransforms(width - 105, height - 75, 100, 20);
		this.coloredField.setTransforms(width - 105, height - 100, 100, 20);
		this.numberField.setTransforms(width - 210, height - 100, 100, 20);
		RenderManager.shape.update();
	}
}
