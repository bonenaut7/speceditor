package by.fxg.speceditor.TO_REMOVE;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.screen.project.map.SubscreenEditor;
import by.fxg.speceditor.std.render.IRendererType.ViewportSettings;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UInputField;
import by.fxg.speceditor.ui.URenderBlock;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ModuleViewport extends BaseSubscreen {
	public SubscreenEditor parent;
	public URenderBlock[] blocks = new URenderBlock[5];
	
	protected UCheckbox hitboxSelectionCheckbox;
	protected UButton button;
	
	protected UInputField hbLineInput;
	protected UInputField[] bufferColorInput = new UInputField[4];
	protected UInputField[] cameraSettingsInput = new UInputField[3];
	protected UInputField[] fogInput = new UInputField[4];
	protected UInputField[] ambientLightInput = new UInputField[3];
	
	public ModuleViewport(SubscreenEditor parent) {
		this.parent = parent;
		//clearColor, Fog, AmbientLight
		
		this.hitboxSelectionCheckbox = new UCheckbox(false, 0, 0, 0, 0);
		this.button = new UButton("", 0, 0, 0, 0);
		String numeral = "0123456789-.";
		this.hbLineInput = new UInputField(0, 0, 0, 0).setAllowedCharacters(numeral).setMaxLength(10);
		for (int i = 0; i != this.bufferColorInput.length; i++) this.bufferColorInput[i] = new UInputField(0, 0, 0, 0).setAllowedCharacters(numeral).setMaxLength(10);
		for (int i = 0; i != this.cameraSettingsInput.length; i++) this.cameraSettingsInput[i] = new UInputField(0, 0, 0, 0).setAllowedCharacters(numeral).setMaxLength(10);
		for (int i = 0; i != this.fogInput.length; i++) this.fogInput[i] = new UInputField(0, 0, 0, 0).setAllowedCharacters(numeral).setMaxLength(10);
		for (int i = 0; i != this.ambientLightInput.length; i++) this.ambientLightInput[i] = new UInputField(0, 0, 0, 0).setAllowedCharacters(numeral).setMaxLength(10);
		
		this.blocks[0] = new URenderBlock("Hitbox selection", 0, 0, 0) { //1 boolean, 1 input
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				foster.setString("Hitbox depth:").draw(this.x, y -= 15, Align.left);
				hitboxSelectionCheckbox.setValue(ViewportSettings.viewportHitboxDepth);
				hitboxSelectionCheckbox.setTransforms(this.x + (int)foster.getWidth() + 5, y -= 10, 12, 12);
				hitboxSelectionCheckbox.update();
				hitboxSelectionCheckbox.render(shape);
				ViewportSettings.viewportHitboxDepth = hitboxSelectionCheckbox.getValue();
				
				foster.setString("Hitbox line width:").draw(this.x, y -= 5, Align.left);
				shape.setColor(UColor.aquablack);
				shape.filledRectangle(this.x + (int)foster.getWidth() + 5, y - 11, this.width - (int)foster.getWidth() - 5, 15);
				hbLineInput.setTransforms(this.x + (int)foster.getWidth() + 5, y - 11, this.width - (int)foster.getWidth() - 5, 15);
				hbLineInput.update();
				hbLineInput.render(batch, shape, foster);
				
				ViewportSettings.viewportHitboxWidth = resetInputField(hbLineInput, ViewportSettings.viewportHitboxWidth);
				return y;
			}
		}.setDropped(true);
		
		this.blocks[1] = new URenderBlock("Screen(buffer) color", 0, 0, 0) { //4 input
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				foster.setString("Color:").draw(this.x, y -= 15, Align.left); y -= 8;
				for (int i = 0; i != 4; i++) {
					switch(i) {
						case 0: foster.setString("R:"); break;
						case 1: foster.setString("G:"); break;
						case 2: foster.setString("B:"); break;
						case 3: foster.setString("A:"); break;
					}
					foster.draw(this.x + 14, y - 9);
					shape.setColor(UColor.overlay);
					shape.rectangle(this.x + 24, y - 20, 12, 14);
					
					shape.setColor(i == 0 ? UColor.redblack : i == 1 ? UColor.greenblack : i == 2 ? UColor.blueblack : UColor.aquablack);
					shape.filledRectangle(this.x + 40, y - 20, this.width - 40, 15);
					bufferColorInput[i].setTransforms(this.x + 40, y -= 20, this.width - 40, 15);
					bufferColorInput[i].update();
					bufferColorInput[i].render(batch, shape, foster);
				}
				
				ViewportSettings.bufferColor.r = resetInputField(bufferColorInput[0], ViewportSettings.bufferColor.r);
				ViewportSettings.bufferColor.g = resetInputField(bufferColorInput[1], ViewportSettings.bufferColor.g);
				ViewportSettings.bufferColor.b = resetInputField(bufferColorInput[2], ViewportSettings.bufferColor.b);
				ViewportSettings.bufferColor.a = resetInputField(bufferColorInput[3], ViewportSettings.bufferColor.a);
				return y + 7;
			}
		}.setDropped(true);
		
		this.blocks[2] = new URenderBlock("Camera settings", 0, 0, 0) { //4 input
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				y -= 10;
				for (int i = 0; i != 3; i++) {
					switch(i) {
						case 0: foster.setString("FOV:"); break;
						case 1: foster.setString("Far:"); break;
						case 2: foster.setString("Near:"); break;
					}
					foster.draw(this.x, y - 9, Align.left);
					shape.setColor(UColor.overlay);
					shape.rectangle(this.x + (int)foster.getWidth() + 5, y - 20, 12, 14);
					
					shape.setColor(UColor.yellowblack);
					shape.filledRectangle(this.x + (int)foster.getWidth() + 22, y - 20, this.width - (int)foster.getWidth() - 22, 15);
					cameraSettingsInput[i].setTransforms(this.x + (int)foster.getWidth() + 22, y -= 20, this.width - (int)foster.getWidth() - 22, 15);
					cameraSettingsInput[i].update();
					cameraSettingsInput[i].render(batch, shape, foster);
				}
				
				ViewportSettings.cameraSettings.x = resetInputField(cameraSettingsInput[0], ViewportSettings.cameraSettings.x);
				ViewportSettings.cameraSettings.y = resetInputField(cameraSettingsInput[1], ViewportSettings.cameraSettings.y);
				ViewportSettings.cameraSettings.z = resetInputField(cameraSettingsInput[2], ViewportSettings.cameraSettings.z);
				parent.parent.subViewport.camera.fieldOfView = ViewportSettings.cameraSettings.x;
				parent.parent.subViewport.camera.far = ViewportSettings.cameraSettings.y;
				parent.parent.subViewport.camera.near = ViewportSettings.cameraSettings.z;
				parent.parent.subViewport.camera.update();
				return y + 7;
			}
		}.setDropped(true);
		
		this.blocks[3] = new URenderBlock("Fog", 0, 0, 0) { //4 input
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				ColorAttribute attribute = null;
				for (Attribute attribute$ : ViewportSettings.viewportAttributes) {
					if (attribute$.type == ColorAttribute.Fog) {
						attribute = (ColorAttribute)attribute$;
						break;
					}
				}
				
				if (attribute != null) {
					foster.setString("Color:").draw(this.x, y -= 15, Align.left); y -= 8;
					for (int i = 0; i != 4; i++) {
						switch(i) {
							case 0: foster.setString("R:"); break;
							case 1: foster.setString("G:"); break;
							case 2: foster.setString("B:"); break;
							case 3: foster.setString("A:"); break;
						}
						foster.draw(this.x + 14, y - 9);
						shape.setColor(UColor.overlay);
						shape.rectangle(this.x + 24, y - 20, 12, 14);
						
						shape.setColor(i == 0 ? UColor.redblack : i == 1 ? UColor.greenblack : i == 2 ? UColor.blueblack : UColor.aquablack);
						shape.filledRectangle(this.x + 40, y - 20, this.width - 40, 15);
						fogInput[i].setTransforms(this.x + 40, y -= 20, this.width - 40, 15);
						fogInput[i].update();
						fogInput[i].render(batch, shape, foster);
					}
					button.setTransforms(this.x, (y -= 5) - 10, this.width, 10).setName("Remove attribute");
					button.render(shape, foster);
					if (button.isPressed()) {
						ViewportSettings.viewportAttributes.removeValue(attribute, true);
						ViewportSettings.shouldUpdate = true;
					}
					
					attribute.color.r = resetInputField(fogInput[0], attribute.color.r);
					attribute.color.g = resetInputField(fogInput[1], attribute.color.g);
					attribute.color.b = resetInputField(fogInput[2], attribute.color.b);
					attribute.color.a = resetInputField(fogInput[3], attribute.color.a);
				} else {
					foster.setString("Attribute not created.").draw(this.x, y -= 15, Align.left);
					button.setTransforms(this.x, (y -= 12) - 10, this.width, 10).setName("Create attribute");
					button.render(shape, foster);
					if (button.isPressed()) {
						ViewportSettings.viewportAttributes.add(ColorAttribute.createFog(ViewportSettings.bufferColor.r, ViewportSettings.bufferColor.g, ViewportSettings.bufferColor.b, ViewportSettings.bufferColor.a));
						ViewportSettings.shouldUpdate = true;
					}
				}
				return y;
			}
		}.setDropped(true);
		
		this.blocks[4] = new URenderBlock("Ambient light", 0, 0, 0) { //3 input
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				ColorAttribute attribute = null;
				for (Attribute attribute$ : ViewportSettings.viewportAttributes) {
					if (attribute$.type == ColorAttribute.AmbientLight) {
						attribute = (ColorAttribute)attribute$;
						break;
					}
				}
				
				if (attribute != null) {
					foster.setString("Color:").draw(this.x, y -= 15, Align.left); y -= 8;
					for (int i = 0; i != 3; i++) {
						switch(i) {
							case 0: foster.setString("R:"); break;
							case 1: foster.setString("G:"); break;
							case 2: foster.setString("B:"); break;
						}
						foster.draw(this.x + 14, y - 9);
						shape.setColor(UColor.overlay);
						shape.rectangle(this.x + 24, y - 20, 12, 14);
						
						shape.setColor(i == 0 ? UColor.redblack : i == 1 ? UColor.greenblack : UColor.blueblack);
						shape.filledRectangle(this.x + 40, y - 20, this.width - 40, 15);
						ambientLightInput[i].setTransforms(this.x + 40, y -= 20, this.width - 40, 15);
						ambientLightInput[i].update();
						ambientLightInput[i].render(batch, shape, foster);
					}
					button.setTransforms(this.x, (y -= 5) - 10, this.width, 10).setName("Remove attribute");
					button.render(shape, foster);
					if (button.isPressed()) {
						ViewportSettings.viewportAttributes.removeValue(attribute, true);
						ViewportSettings.shouldUpdate = true;
					}
					
					attribute.color.r = resetInputField(ambientLightInput[0], attribute.color.r);
					attribute.color.g = resetInputField(ambientLightInput[1], attribute.color.g);
					attribute.color.b = resetInputField(ambientLightInput[2], attribute.color.b);
				} else {
					foster.setString("Attribute not created.").draw(this.x, y -= 15, Align.left);
					button.setTransforms(this.x, (y -= 12) - 10, this.width, 10).setName("Create attribute");
					button.render(shape, foster);
					if (button.isPressed()) {
						ViewportSettings.viewportAttributes.add(ColorAttribute.createAmbientLight(0, 0, 0, 1));
						ViewportSettings.shouldUpdate = true;
					}
				}
				return y;
			}
		}.setDropped(true);
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		for (URenderBlock block : this.blocks) block.setTransforms(x + 10, width - 19, 15);
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		int hOffset = y + height - 5;
		for (URenderBlock block : this.blocks) hOffset = block.render(hOffset, batch, shape, foster);
	}
	
	private float resetInputField(UInputField field, float value) {
		if (!field.isFocused()) {
			field.setText(String.valueOf(value));
		} else {
			try { return Float.valueOf(field.getText()); } catch (Exception e) {}
		}
		return value;
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {}
}
