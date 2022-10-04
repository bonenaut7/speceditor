package by.fxg.speceditor.TO_REMOVE;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UInputField;
import by.fxg.speceditor.ui.URenderBlock;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class TERLight extends TreeElementRenderable<ElementLight> {
	public URenderBlock[] blocks = new URenderBlock[2];

	protected UInputField nameInput;
	
	protected UInputField[] colorInput = new UInputField[3];
	protected UInputField[] posInput = new UInputField[3];
	protected UInputField intensityInput;
	
	public TERLight(ElementLight object) {
		super(object);
		
		String numeral = "0123456789-.";
		this.nameInput = new UInputField(0, 0, 0, 0).setMaxLength(24).setText(renderable.getName());
		for (int i = 0; i != 3; i++) {
			this.colorInput[i] = new UInputField(0, 0, 0, 0).setAllowedCharacters(numeral).setMaxLength(10).setText(String.valueOf(i == 0 ? renderable.light.color.r : i == 1 ? renderable.light.color.g : renderable.light.color.b));
		}
		for (int i = 0; i != 3; i++) {
			this.posInput[i] = new UInputField(0, 0, 0, 0).setAllowedCharacters(numeral).setMaxLength(10).setText(String.valueOf(i == 0 ? renderable.light.position.x : i == 1 ? renderable.light.position.y : renderable.light.position.z));
		}
		this.intensityInput = new UInputField(0, 0, 0, 0).setAllowedCharacters(numeral).setMaxLength(10).setText(String.valueOf(renderable.light.intensity));

		this.blocks[0] = new URenderBlock("Parameters", 0, 0, 0) {
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				foster.setString("Name:").draw(this.x, (y -= 10) - 8, Align.left);
				shape.setColor(UColor.suboverlay);
				shape.filledRectangle(this.x + (int)foster.getWidth() + 5, y - 19, this.width - (int)foster.getWidth() - 5, 15);
				nameInput.setTransforms(this.x + (int)foster.getWidth() + 5, y -= 19, this.width - (int)foster.getWidth() - 5, 15).update();
				nameInput.render(batch, shape, foster);
				return y;
			}
		}.setDropped(true);
		
		this.blocks[1] = new URenderBlock("Transform", 0, 0, 0) {
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				foster.setString("Color:").draw(this.x, y -= 15, Align.left); y -= 8;
				for (int i = 0; i != 3; i++) {
					switch(i) {
						case 0: foster.setString("R:"); break;
						case 1: foster.setString("G:"); break;
						case 2: foster.setString("B:"); break;
					}
					foster.draw(this.x + 14, y - 9);
					drawWheelInput(renderable.light.color, i, shape, this.x + 24, y - 20, 12, 14);
					
					shape.setColor(i == 0 ? UColor.redblack : i == 1 ? UColor.greenblack : UColor.blueblack);
					shape.filledRectangle(this.x + 40, y - 20, this.width - 40, 15);
					colorInput[i].setTransforms(this.x + 40, y -= 20, this.width - 40, 15).update();
					colorInput[i].render(batch, shape, foster);
				}
				foster.setString("Position:").draw(this.x, y -= 15, Align.left); y -= 8;
				for (int i = 0; i != 3; i++) {
					switch(i) { //TODO: find good realization of that shit
						case 0: foster.setString("X:"); break;
						case 1: foster.setString("Y:"); break;
						case 2: foster.setString("Z:"); break;
					}
					foster.draw(this.x + 14, y - 9);
					drawWheelInput(renderable.light.position, i, shape, this.x + 24, y - 20, 12, 14);
					
					shape.setColor(UColor.aquagray);
					shape.filledRectangle(this.x + 40, y - 20, this.width - 40, 15);
					posInput[i].setTransforms(this.x + 40, y -= 20, this.width - 40, 15).update();
					posInput[i].render(batch, shape, foster);
				}
				
				foster.setString("Intensity:").draw(this.x, y -= 15, Align.left); y -= 8;
				renderable.light.intensity = drawWheelInput(renderable.light.intensity, shape, this.x + 24, y - 20, 12, 14);
				
				shape.setColor(UColor.yellowgray);
				shape.filledRectangle(this.x + 40, y - 20, this.width - 40, 15);
				intensityInput.setTransforms(this.x + 40, y -= 20, this.width - 40, 15).update();
				intensityInput.render(batch, shape, foster);
				y += 8;
				return y;
			}
		}.setDropped(true);
	}
	
	public void resetInputFields() {
		this.renderable.setName(this.nameInput.getText().length() == 0 ? "Unnamed" : this.nameInput.getText());
		
		Color color = this.renderable.light.color;
		for (int i = 0; i != 3; i++) {
			if (!this.colorInput[i].isFocused()) {
				this.colorInput[i].setText(String.valueOf(i == 0 ? color.r : i == 1 ? color.g : color.b));
			} else {
				try {
					color.set(i == 0 ? Float.valueOf(this.colorInput[i].getText()) : color.r, i == 1 ? Float.valueOf(this.colorInput[i].getText()) : color.g, i == 2 ? Float.valueOf(this.colorInput[i].getText()) : color.b, 1f);
				} catch (Exception e) {}
			}
		}
		
		Vector3 position = this.renderable.light.position;
		for (int i = 0; i != 3; i++) {
			if (!this.posInput[i].isFocused()) {
				this.posInput[i].setText(String.valueOf(i == 0 ? position.x : i == 1 ? position.y : position.z));
			} else {
				try {
					position.set(i == 0 ? Float.valueOf(this.posInput[i].getText()) : position.x, i == 1 ? Float.valueOf(this.posInput[i].getText()) : position.y, i == 2 ? Float.valueOf(this.posInput[i].getText()) : position.z);
					this.renderable.editorDecal.getDecal().setPosition(position);
				} catch (Exception e) {}
			}
		}
		
		if (!this.intensityInput.isFocused()) {
			this.intensityInput.setText(String.valueOf(this.renderable.light.intensity));
		} else {
			try {
				this.renderable.light.intensity = Float.valueOf(this.intensityInput.getText());
			} catch (Exception e) {}
		}
	}
	
	public void update(int x, int y, int width, int height, boolean allowMouse) {
		for (URenderBlock block : this.blocks) block.setTransforms(x + 10, width - 20, 15);
		this.resetInputFields();
	}

	public int render(Batch batch, ShapeDrawer shape, Foster foster, int hOffset, int x, int y, int width, int height, boolean allowMouse) {
		for (URenderBlock block : this.blocks) hOffset = block.render(hOffset, batch, shape, foster);
		return hOffset;
	}
}
