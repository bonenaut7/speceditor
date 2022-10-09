package by.fxg.speceditor.prefabs.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.std.render.IRendererType.ViewportSettings;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.ColoredInputField;
import by.fxg.speceditor.ui.ColoredInputField.Builder;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.URenderBlock;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenPrefabEditorModuleViewport extends BaseSubscreen implements ISTDInputFieldListener {
	private final String[] colors = {"R", "G", "B", "A"}, cameraSettings = {"FOV", "Far", "Near"};
	private final Color[] fieldColors = {UColor.redblack, UColor.greenblack, UColor.blueblack, UColor.suboverlay};
	public SubscreenPrefabEditor parent;
	public URenderBlock[] blocks = new URenderBlock[5];
	
	protected UCheckbox hitboxSelectionCheckbox;
	protected UButton button;
	protected STDInputField hitboxWidth;
	protected STDInputField[] bufferColor = new STDInputField[4];
	protected STDInputField[] camera = new STDInputField[3];
	protected STDInputField[] fog = new STDInputField[4];
	protected STDInputField[] ambientLight = new STDInputField[3];
	
	public SubscreenPrefabEditorModuleViewport(SubscreenPrefabEditor parent) {
		this.parent = parent;
		//clearColor, Fog, AmbientLight
		
		this.hitboxSelectionCheckbox = new UCheckbox(false, 0, 0, 0, 0);
		this.button = new UButton("", 0, 0, 0, 0);

		Builder builder = (Builder)new ColoredInputField.Builder().setFoster(Game.fosterNoDraw).setAllowFullfocus(false).setNumeralInput(true).setMaxLength(12);
		this.hitboxWidth = builder.setBackgroundColor(UColor.greengray).setListener(this, "hitboxWidth").build();
		for (int i = 0; i != this.bufferColor.length; i++) this.bufferColor[i] = builder.setBackgroundColor(this.fieldColors[i]).setListener(this, "bufferColor").build();
		for (int i = 0; i != this.camera.length; i++) this.camera[i] = builder.setBackgroundColor(UColor.yellowblack).setListener(this, "camera").build();
		for (int i = 0; i != this.fog.length; i++) this.fog[i] = builder.setBackgroundColor(this.fieldColors[i]).setListener(this, "fog").build();
		for (int i = 0; i != this.ambientLight.length; i++) this.ambientLight[i] = builder.setBackgroundColor(this.fieldColors[i]).setListener(this, "ambientLight").build();
		builder.addToLink(this.bufferColor).linkFields();
		builder.addToLink(this.camera).linkFields();
		builder.addToLink(this.fog).linkFields();
		builder.addToLink(this.ambientLight).linkFields();
		
		this.blocks[0] = new URenderBlock("Hitbox selection") { //1 boolean, 1 input
			protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
				foster.setString("Hitbox depth:").draw(this.x, yOffset -= 1, Align.left);
				hitboxSelectionCheckbox.setValue(ViewportSettings.viewportHitboxDepth).setTransforms(this.x + (int)foster.getWidth() + 5, yOffset -= 10, 12, 12).update();
				hitboxSelectionCheckbox.render(shape);
				ViewportSettings.viewportHitboxDepth = hitboxSelectionCheckbox.getValue();
				
				foster.setString("Hitbox line width:").draw(this.x, yOffset -= 5, Align.left);
				hitboxWidth.setTransforms(this.x + (int)foster.getWidth() + 5, yOffset - 10, this.width - (int)foster.getWidth() - 5, 15).setFoster(foster).update();
				hitboxWidth.render(batch, shape);
				return yOffset;
			}
		}.setDropped(true);
		
		this.blocks[1] = new URenderBlock("Screen(buffer) color") { //4 input
			protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
				int sizePerPart = (this.width - 20 - (int)foster.setString(colors[0]).getWidth() * 2) / 2;
				foster.setString("Color:").draw(this.x, yOffset, Align.left);
				yOffset -= 16;
				for (int i = 0; i != 2; i++) {
					foster.setString(colors[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + 1);
					bufferColor[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset - 10, sizePerPart, 15).setFoster(foster).update();
					bufferColor[i].render(batch, shape);
				}
				yOffset -= 16;
				for (int i = 0, k = 2; i != 2; i++, k++) {
					foster.setString(colors[k]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + 1);
					bufferColor[k].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset - 10, sizePerPart, 15).setFoster(foster).update();
					bufferColor[k].render(batch, shape);
				}
				return yOffset;
			}
		}.setDropped(true);
		
		this.blocks[2] = new URenderBlock("Camera settings") { //4 input
			protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
				yOffset += 3;
				for (int i = 0; i != 3; i++) {
					foster.setString(cameraSettings[i]).draw(this.x, yOffset - 7, Align.left);
					camera[i].setTransforms(this.x + (int)foster.getWidth() + 5, yOffset -= 18, this.width - (int)foster.getWidth() - 5, 15).setFoster(foster).update();
					camera[i].render(batch, shape);
				}
				return yOffset + 10;
			}
		}.setDropped(true);
		
		this.blocks[3] = new URenderBlock("Fog") { //4 input
			protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
				ColorAttribute attribute = _searchForAttribute(ColorAttribute.Fog);
				if (attribute != null) {
					int sizePerPart = (this.width - 20 - (int)foster.setString(colors[0]).getWidth() * 2) / 2;
					foster.setString("Fog color:").draw(this.x, yOffset, Align.left);
					yOffset -= 16;
					for (int i = 0; i != 2; i++) {
						foster.setString(colors[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + 1);
						fog[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset - 10, sizePerPart, 15).setFoster(foster).update();
						fog[i].render(batch, shape);
					}
					yOffset -= 16;
					for (int i = 0, k = 2; i != 2; i++, k++) {
						foster.setString(colors[k]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + 1);
						fog[k].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset - 10, sizePerPart, 15).setFoster(foster).update();
						fog[k].render(batch, shape);
					}
					yOffset -= 16;
					button.setTransforms(this.x, yOffset - 10, this.width, 10).setName("Remove attribute");
					button.render(shape, foster);
					if (button.isPressed()) {
						ViewportSettings.viewportAttributes.removeValue(attribute, true);
						ViewportSettings.shouldUpdate = true;
					}
				} else {
					foster.setString("Attribute not created.").draw(this.x, yOffset, Align.left);
					button.setTransforms(this.x, (yOffset -= 12) - 10, this.width, 10).setName("Create attribute");
					button.render(shape, foster);
					if (button.isPressed()) {
						ViewportSettings.viewportAttributes.add(ColorAttribute.createFog(ViewportSettings.bufferColor.r, ViewportSettings.bufferColor.g, ViewportSettings.bufferColor.b, ViewportSettings.bufferColor.a));
						ViewportSettings.shouldUpdate = true;
					}
				}
				return yOffset;
			}
		}.setDropped(true);
		
		this.blocks[4] = new URenderBlock("Ambient light") { //3 input
			protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
				ColorAttribute attribute = _searchForAttribute(ColorAttribute.AmbientLight);
				if (attribute != null) {
					
					int sizePerPart = (this.width - 20 - (int)foster.setString(colors[0]).getWidth() * 2) / 2;
					foster.setString("Ambient light color:").draw(this.x, yOffset, Align.left);
					yOffset -= 16;
					for (int i = 0; i != 2; i++) {
						foster.setString(colors[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + 1);
						ambientLight[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset - 10, sizePerPart, 15).setFoster(foster).update();
						ambientLight[i].render(batch, shape);
					}
					yOffset -= 16;
					for (int i = 0, k = 2; i != 1; i++, k++) {
						foster.setString(colors[k]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + 1);
						ambientLight[k].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset - 10, sizePerPart, 15).setFoster(foster).update();
						ambientLight[k].render(batch, shape);
					}
					foster.setString("A - from buffer").draw(this.x + this.width / 2 + 7, yOffset, Align.left);
					yOffset -= 16;
					button.setTransforms(this.x, yOffset - 10, this.width, 10).setName("Remove attribute");
					button.render(shape, foster);
					if (button.isPressed()) {
						ViewportSettings.viewportAttributes.removeValue(attribute, true);
						ViewportSettings.shouldUpdate = true;
					}
				} else {
					foster.setString("Attribute not created.").draw(this.x, yOffset, Align.left);
					button.setTransforms(this.x, (yOffset -= 12) - 10, this.width, 10).setName("Create attribute");
					button.render(shape, foster);
					if (button.isPressed()) {
						ViewportSettings.viewportAttributes.add(ColorAttribute.createAmbientLight(0, 0, 0, 1));
						ViewportSettings.shouldUpdate = true;
					}
				}
				return yOffset;
			}
		}.setDropped(true);
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		for (URenderBlock block : this.blocks) block.setTransforms(x + 10, width - 19);
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		int hOffset = y + height - 5;
		for (URenderBlock block : this.blocks) hOffset = block.render(batch, shape, foster, hOffset);
	}

	public void whileFocused(STDInputField inputField, String id) {
		switch (id) {
			case "hitboxWidth": ViewportSettings.viewportHitboxWidth = this._convertTextToFloat(this.hitboxWidth, ViewportSettings.viewportHitboxWidth); break;
			case "bufferColor": this._convertTextToColor(ViewportSettings.bufferColor, this.bufferColor[0], this.bufferColor[1], this.bufferColor[2], this.bufferColor[3]); break;
			case "camera": {
				this._convertTextToVector3(ViewportSettings.cameraSettings, this.camera[0], this.camera[1], this.camera[2]);
				this.parent.screenProject.subViewport.camera.fieldOfView = ViewportSettings.cameraSettings.x;
				this.parent.screenProject.subViewport.camera.far = ViewportSettings.cameraSettings.y;
				this.parent.screenProject.subViewport.camera.near = ViewportSettings.cameraSettings.z;
				this.parent.screenProject.subViewport.camera.update();
			} break;
			case "fog": {
				ColorAttribute fog = this._searchForAttribute(ColorAttribute.Fog);
				if (fog != null) this._convertTextToColor(fog.color, this.fog[0], this.fog[1], this.fog[2], this.fog[3]);
			} break;
			case "ambientLight": {
				ColorAttribute ambientLight = this._searchForAttribute(ColorAttribute.AmbientLight);
				if (ambientLight != null) this._convertTextToColor(ambientLight.color, this.ambientLight[0], this.ambientLight[1], this.ambientLight[2], this.bufferColor[3]);
			} break;
		}
	}

	public void whileNotFocused(STDInputField inputField, String id) {
		switch (id) {
			case "hitboxWidth": this.hitboxWidth.setText(String.valueOf(ViewportSettings.viewportHitboxWidth)).dropOffset(); break;
			case "bufferColor": this._convertColorToText(ViewportSettings.bufferColor, this.bufferColor[0], this.bufferColor[1], this.bufferColor[2], this.bufferColor[3], true); break;
			case "camera": this._convertVector3ToText(ViewportSettings.cameraSettings, this.camera[0], this.camera[1], this.camera[2], true); break;
			case "fog": {
				ColorAttribute fog = this._searchForAttribute(ColorAttribute.Fog);
				if (fog != null) this._convertColorToText(fog.color, this.fog[0], this.fog[1], this.fog[2], this.fog[3], true);
			} break;
			case "ambientLight": {
				ColorAttribute ambientLight = this._searchForAttribute(ColorAttribute.AmbientLight);
				if (ambientLight != null) this._convertColorToText(ambientLight.color, this.ambientLight[0], this.ambientLight[1], this.ambientLight[2], this.bufferColor[3], true); //bufferColor's alpha
			} break;
		}
	}
	
	/** FIXME needs refactoring**/ @Deprecated
	private ColorAttribute _searchForAttribute(long type) {
		ColorAttribute attribute = null;
		for (Attribute attribute$ : ViewportSettings.viewportAttributes) {
			if (attribute$.type == type) {
				attribute = (ColorAttribute)attribute$;
				break;
			}
		}
		return attribute;
	}
	
	/** FIXME needs refactoring**/ @Deprecated
	private void _convertColorToText(Color color, STDInputField fieldR, STDInputField fieldG, STDInputField fieldB, STDInputField fieldA, boolean withPointer) {
		if (withPointer) {
			if (!fieldR.isFocused()) fieldR.setTextWithPointer(String.valueOf(color.r)).dropOffset();
			if (!fieldG.isFocused()) fieldG.setTextWithPointer(String.valueOf(color.g)).dropOffset();
			if (!fieldB.isFocused()) fieldB.setTextWithPointer(String.valueOf(color.b)).dropOffset();
			if (!fieldA.isFocused()) fieldA.setTextWithPointer(String.valueOf(color.a)).dropOffset();	
		} else {
			if (!fieldR.isFocused()) fieldR.setText(String.valueOf(color.r));
			if (!fieldG.isFocused()) fieldG.setText(String.valueOf(color.g));
			if (!fieldB.isFocused()) fieldB.setText(String.valueOf(color.b));
			if (!fieldA.isFocused()) fieldA.setText(String.valueOf(color.a));
		}
	}
	
	/** FIXME needs refactoring**/ @Deprecated
	private void _convertVector3ToText(Vector3 vec, STDInputField fieldX, STDInputField fieldY, STDInputField fieldZ, boolean withPointer) {
		if (withPointer) {
			if (!fieldX.isFocused()) fieldX.setText(String.valueOf(vec.x)).dropOffset();
			if (!fieldY.isFocused()) fieldY.setText(String.valueOf(vec.y)).dropOffset();
			if (!fieldZ.isFocused()) fieldZ.setText(String.valueOf(vec.z)).dropOffset();
		} else {
			if (!fieldX.isFocused()) fieldX.setText(String.valueOf(vec.x));
			if (!fieldY.isFocused()) fieldY.setText(String.valueOf(vec.y));
			if (!fieldZ.isFocused()) fieldZ.setText(String.valueOf(vec.z));
		}
	}

	/** FIXME needs refactoring**/ @Deprecated
	private void _convertTextToColor(Color color, STDInputField fieldR, STDInputField fieldG, STDInputField fieldB, STDInputField fieldA) {
		color.set(this._convertTextToFloat(fieldR, color.r), this._convertTextToFloat(fieldG, color.g), this._convertTextToFloat(fieldB, color.b), this._convertTextToFloat(fieldA, color.a));
	}
	
	/** FIXME needs refactoring**/ @Deprecated
	private void _convertTextToVector3(Vector3 vec, STDInputField fieldX, STDInputField fieldY, STDInputField fieldZ) {
		vec.set(this._convertTextToFloat(fieldX, vec.x), this._convertTextToFloat(fieldY, vec.y), this._convertTextToFloat(fieldZ, vec.z));
	}
	
	/** FIXME needs refactoring**/ @Deprecated
	private float _convertTextToFloat(STDInputField field, float failValue) {
		try {
			return Float.valueOf(field.getText());
		} catch (NullPointerException | NumberFormatException e) {
			if (!field.isFocused()) field.setText(String.valueOf(failValue));
			return failValue;
		}
	}
	
	public void resize(int subX, int subY, int subWidth, int subHeight) {}
}
