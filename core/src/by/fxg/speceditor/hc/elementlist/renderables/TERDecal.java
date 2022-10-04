package by.fxg.speceditor.hc.elementlist.renderables;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.hc.elementlist.EnumTransform;
import by.fxg.speceditor.hc.elementlist.TreeElementRenderable;
import by.fxg.speceditor.hc.elementlist.elements.ElementDecal;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.ui.UInputField;
import by.fxg.speceditor.ui.URenderBlock;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class TERDecal extends TreeElementRenderable<ElementDecal> {
	public URenderBlock[] blocks = new URenderBlock[2];
	public UInputField[] input = new UInputField[10];
	protected UCheckbox checkbox;
	protected UButton button;
	public UDropdownSelectSingle select;
	
	public TERDecal(ElementDecal object) {
		super(object);
		
		String numeral = "0123456789-.";
		for (int i = 0; i != 6; i++) {
			Vector3 vector = i < 3 ? renderable.getTransform(EnumTransform.TRANSLATE) : renderable.getTransform(EnumTransform.ROTATE);
			this.input[i] = new UInputField(0, 0, 0, 0).setAllowedCharacters(numeral).setMaxLength(10).setText(String.valueOf(i % 3 == 0 ? vector.x : i % 3 == 1 ? vector.y : vector.z));
		}
		for (int i = 6; i != 8; i++) this.input[i] = new UInputField(0, 0, 0, 0).setAllowedCharacters(numeral).setMaxLength(10).setText(String.valueOf(i == 6 ? renderable.decal.scale.x : renderable.decal.scale.y));
		
		this.input[8] = new UInputField(0, 0, 0, 0).setMaxLength(24).setText(renderable.getName());
		this.input[9] = new UInputField(0, 0, 0, 0).setMaxLength(128).setText(renderable.decal.localDecalHandle);
		this.checkbox = new UCheckbox(false, 0, 0, 0, 0);
		this.button = new UButton("", 0, 0, 0, 0);
		this.select = new UDropdownSelectSingle(0, 0, 0, 0, 12, "Select texture");

		this.blocks[0] = new URenderBlock("Parameters", 0, 0, 0) {
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				foster.setString("Name:").draw(this.x, (y -= 10) - 8, Align.left);
				shape.setColor(UColor.suboverlay);
				shape.filledRectangle(this.x + (int)foster.getWidth() + 5, y - 19, this.width - (int)foster.getWidth() - 5, 15);
				TERDecal.this.input[8].setTransforms(this.x + (int)foster.getWidth() + 5, y -= 19, this.width - (int)foster.getWidth() - 5, 15).update();
				TERDecal.this.input[8].render(batch, shape, foster);
				shape.setColor(UColor.gray);
				shape.line(this.x, y -= 5, this.x + this.width, y);
				foster.setString("Ext decal path:").draw(this.x, y - 8, Align.left);
				shape.setColor(UColor.suboverlay);
				shape.filledRectangle(this.x + (int)foster.getWidth() + 5, y - 19, this.width - (int)foster.getWidth() - 5, 15);
				TERDecal.this.input[9].setTransforms(this.x + (int)foster.getWidth() + 5, y -= 19, this.width - (int)foster.getWidth() - 5, 15).update();
				TERDecal.this.input[9].render(batch, shape, foster);
				shape.setColor(UColor.gray);
				shape.line(this.x, y -= 5, this.x + this.width, y);
				TERDecal.this.button.setTransforms(this.x, (y -= 5) - 11, this.width, 12).setName("Select decal");
				TERDecal.this.button.render(shape, foster);
				if (TERDecal.this.button.isPressed()) {
					try {
						FileHandle handle = Utils.selectFileDialog("Supported textures (*.png; *.jpg)", "png", "jpg", "jpeg");
						SpriteStack.remove(handle);
						TERDecal.this.renderable.decal.setDecal(Decal.newDecal(SpriteStack.getTextureRegion(handle), true), handle);
					} catch (Exception e) { e.printStackTrace(); }
				}
				foster.setString("Camera-facing billboard").draw(this.x + 18, y - 18, Align.left);
				TERDecal.this.checkbox.setTransforms(this.x, (y -= 19) - 9, 12, 12).setValue(TERDecal.this.renderable.decal.isBillboard()).update();
				TERDecal.this.checkbox.render(shape);
				TERDecal.this.renderable.decal.setBillboard(TERDecal.this.checkbox.getValue());
				return y;
			}
		}.setDropped(true);
		
		this.blocks[1] = new URenderBlock("Transform", 0, 0, 0) {
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				for (int i = 0; i != 2; i++) {
					switch(i) {
						case 0: foster.setString("Position:"); break;
						case 1: foster.setString("Rotation:"); break;
					}
					foster.draw(this.x, y -= 15, Align.left);
					y -= 8;
					for (int j = 0; j != 3; j++) {
						switch(j) {
							case 0: foster.setString("X:"); break;
							case 1: foster.setString("Y:"); break;
							case 2: foster.setString("Z:"); break;
						}
						foster.draw(this.x + 14, y - 9);
						drawWheelInput(TERDecal.this.renderable.getTransform(EnumTransform.values()[i]), j, shape, this.x + 24, y - 20, 12, 14);
						
						shape.setColor(i == 0 ? UColor.redblack : i == 1 ? UColor.greenblack : UColor.blueblack);
						shape.filledRectangle(this.x + 40, y - 20, this.width - 40, 15);
						TERDecal.this.input[j + i * 3].setTransforms(this.x + 40, y -= 20, this.width - 40, 15).update();
						TERDecal.this.input[j + i * 3].render(batch, shape, foster);
					}
				}
				foster.setString("Scale:").draw(this.x, y -= 15, Align.left);
				y -= 8;
				for (int i = 0; i != 2; i++) {
					foster.setString(i == 0 ? "X:" : "Y:").draw(this.x + 14, y - 9);
					drawWheelInput(TERDecal.this.renderable.decal.scale, i, shape, this.x + 24, y - 20, 12, 14);
					shape.setColor(UColor.blueblack);
					shape.filledRectangle(this.x + 40, y - 20, this.width - 40, 15);
					TERDecal.this.input[6 + i].setTransforms(this.x + 40, y -= 20, this.width - 40, 15).update();
					TERDecal.this.input[6 + i].render(batch, shape, foster);
				}
				y += 8;
				return y;
			}
		}.setDropped(true);
	}
	
	public void resetInputFields() {
		this.renderable.setName(this.input[8].getText().length() == 0 ? "Unnamed" : this.input[8].getText());
		this.renderable.decal.localDecalHandle = this.input[9].getText();
		for (int i = 0; i != 8; i++) {
			if (i < 6) {
				Vector3 vector = i < 3 ? this.renderable.getTransform(EnumTransform.TRANSLATE) : i < 6 ? this.renderable.getTransform(EnumTransform.ROTATE) : this.renderable.getTransform(EnumTransform.SCALE);
				if (!this.input[i].isFocused()) {
					this.input[i].setText(String.valueOf(i % 3 == 0 ? vector.x : i % 3 == 1 ? vector.y : vector.z));
				} else {
					try {
						vector.set(i % 3 == 0 ? Float.valueOf(this.input[i].getText()) : vector.x, i % 3 == 1 ? Float.valueOf(this.input[i].getText()) : vector.y, i % 3 == 2 ? Float.valueOf(this.input[i].getText()) : vector.z);
					} catch (Exception e) {}
				}
			}
			if (i > 5) {
				Vector2 vector = this.renderable.decal.scale;
				if (!this.input[i].isFocused()) this.input[i].setText(String.valueOf(i % 3 == 0 ? vector.x : vector.y));
				else {
					try {
						vector.set(i % 3 == 0 ? Float.valueOf(this.input[i].getText()) : vector.x, i % 3 == 1 ? Float.valueOf(this.input[i].getText()) : vector.y);
					} catch (Exception e) {}
				}
			}
		}
	}
	
	public void update(int x, int y, int width, int height, boolean allowMouse) {
		for (URenderBlock block : this.blocks) block.setTransforms(x + 10, width - 19, 15);
		this.resetInputFields();
	}

	public int render(Batch batch, ShapeDrawer shape, Foster foster, int hOffset, int x, int y, int width, int height, boolean allowMouse) {
		for (URenderBlock block : this.blocks) hOffset = block.render(hOffset, batch, shape, foster);
		return hOffset;
	}
}
