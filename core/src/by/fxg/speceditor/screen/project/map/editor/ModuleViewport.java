package by.fxg.speceditor.screen.project.map.editor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.project.Project;
import by.fxg.speceditor.screen.project.map.SubscreenEditor;
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
				ModuleViewport.this.hitboxSelectionCheckbox.setValue(Project.instance.viewportHitboxDepth);
				ModuleViewport.this.hitboxSelectionCheckbox.setTransforms(this.x + (int)foster.getWidth() + 5, y -= 10, 12, 12);
				ModuleViewport.this.hitboxSelectionCheckbox.update();
				ModuleViewport.this.hitboxSelectionCheckbox.render(shape);
				Project.instance.viewportHitboxDepth = ModuleViewport.this.hitboxSelectionCheckbox.getValue();
				
				foster.setString("Hitbox line width:").draw(this.x, y -= 5, Align.left);
				shape.setColor(UColor.aquablack);
				shape.filledRectangle(this.x + (int)foster.getWidth() + 5, y - 11, this.width - (int)foster.getWidth() - 5, 15);
				ModuleViewport.this.hbLineInput.setTransforms(this.x + (int)foster.getWidth() + 5, y - 11, this.width - (int)foster.getWidth() - 5, 15);
				ModuleViewport.this.hbLineInput.update();
				ModuleViewport.this.hbLineInput.render(batch, shape, foster);
				
				Project.instance.viewportHitboxWidth = ModuleViewport.this.resetInputField(ModuleViewport.this.hbLineInput, Project.instance.viewportHitboxWidth);
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
					ModuleViewport.this.bufferColorInput[i].setTransforms(this.x + 40, y -= 20, this.width - 40, 15);
					ModuleViewport.this.bufferColorInput[i].update();
					ModuleViewport.this.bufferColorInput[i].render(batch, shape, foster);
				}
				
				Project project = Project.instance;
				project.bufferColor.r = ModuleViewport.this.resetInputField(ModuleViewport.this.bufferColorInput[0], project.bufferColor.r);
				project.bufferColor.g = ModuleViewport.this.resetInputField(ModuleViewport.this.bufferColorInput[1], project.bufferColor.g);
				project.bufferColor.b = ModuleViewport.this.resetInputField(ModuleViewport.this.bufferColorInput[2], project.bufferColor.b);
				project.bufferColor.a = ModuleViewport.this.resetInputField(ModuleViewport.this.bufferColorInput[3], project.bufferColor.a);
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
					ModuleViewport.this.cameraSettingsInput[i].setTransforms(this.x + (int)foster.getWidth() + 22, y -= 20, this.width - (int)foster.getWidth() - 22, 15);
					ModuleViewport.this.cameraSettingsInput[i].update();
					ModuleViewport.this.cameraSettingsInput[i].render(batch, shape, foster);
				}
				
				Project project = Project.instance;
				project.cameraSettings.x = ModuleViewport.this.resetInputField(ModuleViewport.this.cameraSettingsInput[0], project.cameraSettings.x);
				project.cameraSettings.y = ModuleViewport.this.resetInputField(ModuleViewport.this.cameraSettingsInput[1], project.cameraSettings.y);
				project.cameraSettings.z = ModuleViewport.this.resetInputField(ModuleViewport.this.cameraSettingsInput[2], project.cameraSettings.z);
				ModuleViewport.this.parent.parent.subViewport.camera.fieldOfView = project.cameraSettings.x;
				ModuleViewport.this.parent.parent.subViewport.camera.far = project.cameraSettings.y;
				ModuleViewport.this.parent.parent.subViewport.camera.near = project.cameraSettings.z;
				ModuleViewport.this.parent.parent.subViewport.camera.update();
				return y + 7;
			}
		}.setDropped(true);
		
		this.blocks[3] = new URenderBlock("Fog", 0, 0, 0) { //4 input
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				Project project = Project.instance;
				ColorAttribute attribute = null;
				for (Attribute attribute$ : project.viewportAttributes) {
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
						ModuleViewport.this.fogInput[i].setTransforms(this.x + 40, y -= 20, this.width - 40, 15);
						ModuleViewport.this.fogInput[i].update();
						ModuleViewport.this.fogInput[i].render(batch, shape, foster);
					}
					ModuleViewport.this.button.setTransforms(this.x, (y -= 5) - 10, this.width, 10).setName("Remove attribute");
					ModuleViewport.this.button.render(shape, foster);
					if (ModuleViewport.this.button.isPressed()) {
						project.viewportAttributes.removeValue(attribute, true);
						Project.renderer.update();
					}
					
					attribute.color.r = ModuleViewport.this.resetInputField(ModuleViewport.this.fogInput[0], attribute.color.r);
					attribute.color.g = ModuleViewport.this.resetInputField(ModuleViewport.this.fogInput[1], attribute.color.g);
					attribute.color.b = ModuleViewport.this.resetInputField(ModuleViewport.this.fogInput[2], attribute.color.b);
					attribute.color.a = ModuleViewport.this.resetInputField(ModuleViewport.this.fogInput[3], attribute.color.a);
				} else {
					foster.setString("Attribute not created.").draw(this.x, y -= 15, Align.left);
					ModuleViewport.this.button.setTransforms(this.x, (y -= 12) - 10, this.width, 10).setName("Create attribute");
					ModuleViewport.this.button.render(shape, foster);
					if (ModuleViewport.this.button.isPressed()) {
						project.viewportAttributes.add(ColorAttribute.createFog(project.bufferColor.r, project.bufferColor.g, project.bufferColor.b, project.bufferColor.a));
						Project.renderer.update();
					}
				}
				return y;
			}
		}.setDropped(true);
		
		this.blocks[4] = new URenderBlock("Ambient light", 0, 0, 0) { //3 input
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				Project project = Project.instance;
				ColorAttribute attribute = null;
				for (Attribute attribute$ : project.viewportAttributes) {
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
						ModuleViewport.this.ambientLightInput[i].setTransforms(this.x + 40, y -= 20, this.width - 40, 15);
						ModuleViewport.this.ambientLightInput[i].update();
						ModuleViewport.this.ambientLightInput[i].render(batch, shape, foster);
					}
					ModuleViewport.this.button.setTransforms(this.x, (y -= 5) - 10, this.width, 10).setName("Remove attribute");
					ModuleViewport.this.button.render(shape, foster);
					if (ModuleViewport.this.button.isPressed()) {
						project.viewportAttributes.removeValue(attribute, true);
						Project.renderer.update();
					}
					
					attribute.color.r = ModuleViewport.this.resetInputField(ModuleViewport.this.ambientLightInput[0], attribute.color.r);
					attribute.color.g = ModuleViewport.this.resetInputField(ModuleViewport.this.ambientLightInput[1], attribute.color.g);
					attribute.color.b = ModuleViewport.this.resetInputField(ModuleViewport.this.ambientLightInput[2], attribute.color.b);
				} else {
					foster.setString("Attribute not created.").draw(this.x, y -= 15, Align.left);
					ModuleViewport.this.button.setTransforms(this.x, (y -= 12) - 10, this.width, 10).setName("Create attribute");
					ModuleViewport.this.button.render(shape, foster);
					if (ModuleViewport.this.button.isPressed()) {
						project.viewportAttributes.add(ColorAttribute.createAmbientLight(0, 0, 0, 1));
						Project.renderer.update();
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
