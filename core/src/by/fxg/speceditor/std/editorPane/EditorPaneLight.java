package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.GizmosModule;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.objectTree.elements.ElementLight;
import by.fxg.speceditor.std.objectTree.elements.ElementLight.ElementLightType;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.ui.UIElement;
import by.fxg.speceditor.ui.ColoredInputField;
import by.fxg.speceditor.ui.NumberCursorInputField;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.ui.URenderBlock;
import space.earlygrey.shapedrawer.ShapeDrawer;

//TODO add light type change selector
public class EditorPaneLight extends EditorPane implements ISTDInputFieldListener {
	private ElementLight element = null;
	private UDropdownSelectSingle lightType;
	private STDInputField elementName;
	private TransformBlock transform;
	
	public EditorPaneLight() {
		this.lightType = new UDropdownSelectSingle(15, "Point light", "Spot light") {
			public UDropdownSelectSingle setVariantSelected(int variant) {
				if (element != null) {
					switch(variant) {
						case 0: { 
							switch(element.type) {
								case SPOT: {
									element.type = ElementLightType.POINT;
									PointLight pointLight = new PointLight();
									pointLight.setColor(element.getLight(SpotLight.class).color);
									pointLight.setPosition(element.getLight(SpotLight.class).position);
									pointLight.intensity = element.getLight(SpotLight.class).intensity;
									element.setLight(pointLight);
									transform.updateBlock(element);
								} break;
								default:
							}
						} break;
						case 1: { 
							switch(element.type) {
								case POINT: {
									element.type = ElementLightType.SPOT;
									SpotLight spotLight = new SpotLight();
									spotLight.setColor(element.getLight(PointLight.class).color);
									spotLight.setPosition(element.getLight(PointLight.class).position);
									spotLight.intensity = element.getLight(PointLight.class).intensity;
									element.setLight(spotLight);
									transform.updateBlock(element);
								} break;
								default:
							}
						} break;
						default: break;
					}
				}
				return super.setVariantSelected(variant);
			}
		};
		this.elementName = new ColoredInputField().setAllowFullfocus(false).setListener(this, "name").setMaxLength(48);
		this.transform = (TransformBlock)new TransformBlock(this).setDropped(true);
	}
	
	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {
		yOffset -= 8;
		float longestString = this.getLongestStringWidth(foster, "Name", "Type");
		
		foster.setString("Name").draw(x + 5, yOffset -= foster.getHeight(), Align.left);
		this.elementName.setTransforms(x + longestString + 10, yOffset -= foster.getHalfHeight(), width - longestString - 15, 15).setFoster(foster).update();
		this.elementName.render(batch, shape);
		
		foster.setString("Type").draw(x + 5, yOffset -= foster.getHeight() + 6, Align.left);
		this.lightType.setTransforms(x + longestString + 10, (yOffset -= 4) - foster.getHalfHeight() + 3, width - longestString - 15, 14).render(shape, foster);
		if (this.lightType.isFocused()) yOffset -= this.lightType.getDropHeight();
		
		yOffset = this.transform.setTransforms(x + 5, width - 10).render(batch, shape, foster, yOffset - 5);
		this.lightType.update();
		return yOffset;
	}

	public void updatePane(ITreeElementSelector<?> selector) {
		this.element = (ElementLight)selector.get(0);
		this.elementName.setText(this.element.getName());
		
		this.transform.updateBlock(this.element);
	}
	
	public void whileInputFieldFocused(STDInputField inputField, String id) {
		switch (id) {
			case "name": this.element.setName(this.elementName.getText()); break;
		}
	}

	public boolean acceptElement(ITreeElementSelector<?> selector) {
		return selector.size() == 1 && selector.get(0) instanceof ElementLight;
	}
	
	private class TransformBlock extends URenderBlock implements ISTDInputFieldListener {
		private final String[] coords = {"X", "Y", "Z"}, colors = {"R", "G", "B", "A"}, params = {"Intensity", "Cutoff angle", "Exponent"};
		private final Color[] fieldColors = {UColor.redblack, UColor.greenblack, UColor.blueblack, UColor.black025alpha};
		private EditorPaneLight parent;
		private ElementLightType lightType = ElementLightType.POINT;
		private BaseLight<?> light = null;
		private STDInputField[] position = new STDInputField[3], rotation = new STDInputField[3], color = new STDInputField[4], parameters = new STDInputField[3];

		private TransformBlock(EditorPaneLight parent) {
			super("Transforms");
			this.parent = parent;
			
			NumberCursorInputField.Builder builder = (NumberCursorInputField.Builder)new NumberCursorInputField.Builder().setAllowFullfocus(false).setMaxLength(12);
			for (int i = 0; i != 3; i++) this.position[i] = builder.setBackgroundColor(UColor.redblack).setListener(this, "position").build();
			for (int i = 0; i != 3; i++) this.rotation[i] = builder.setBackgroundColor(UColor.greenblack).setListener(this, "rotation").build();
			for (int i = 0; i != 4; i++) this.color[i] = builder.setBackgroundColor(this.fieldColors[i]).setListener(this, "color").build();
			this.parameters[0] = builder.setBackgroundColor(UColor.yellowblack).setListener(this, "intensity").build();
			this.parameters[1] = builder.setBackgroundColor(UColor.yellowblack).setListener(this, "cutoffangle").build();
			this.parameters[2] = builder.setBackgroundColor(UColor.yellowblack).setListener(this, "exponent").build();
			builder.addToLink(this.position).addToLink(this.rotation).addToLink(this.color).addToLink(this.parameters[0]).linkFields(); //other components are not available for every type of light present here
		}

		protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
			if (SpecInterface.INSTANCE.currentFocus instanceof GizmosModule) this.updateGizmoValues();
			int sizePerPart = (this.width - 40 - (int)foster.setString(this.coords[0]).getWidth() * 3) / 3;
			int x = this.x + 5;
			yOffset -= 8;
			
			foster.setString("Position:").draw(x, yOffset -= foster.getHeight(), Align.left);
			yOffset -= 19; //16 size of box + 3 offset
			for (int i = 0; i != 3; i++) {
				foster.setString(this.coords[i]).draw(x + 10 + (foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
				this.position[i].setTransforms(x + 10 + foster.getWidth() + (foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
				this.position[i].render(batch, shape);
			}

			if (this.lightType == ElementLightType.SPOT) {
				foster.setString("Direction:").draw(x, yOffset -= foster.getHeight() + 5, Align.left);
				yOffset -= 19;
				for (int i = 0; i != 3; i++) {
					foster.setString(this.coords[i]).draw(x + 10 + (foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
					this.rotation[i].setTransforms(x + 10 + foster.getWidth() + (foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
					this.rotation[i].render(batch, shape);
				}
			}
			
			sizePerPart = (this.width - 30 - (int)foster.setString(this.colors[0]).getWidth() * 2) / 2;
			foster.setString("Color:").draw(x, yOffset -= foster.getHeight() + 5, Align.left);
			yOffset -= 19;
			for (int i = 0; i != 2; i++) {
				foster.setString(this.colors[i]).draw(x + 10 + (foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
				this.color[i].setTransforms(x + 10 + foster.getWidth() + (foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
				this.color[i].render(batch, shape);
			}
			yOffset -= 16;
			for (int i = 0, k = 2; i != 2; i++, k++) {
				foster.setString(this.colors[k]).draw(x + 10 + (foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
				this.color[k].setTransforms(x + 10 + foster.getWidth() + (foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
				this.color[k].render(batch, shape);
			}

			foster.setString("Parameters:").draw(x, yOffset -= foster.getHeight() + 5, Align.left);
			yOffset -= 19;
			float longestString = this.lightType == ElementLightType.POINT ? foster.setString(this.params[0]).getWidth() : this.parent.getLongestStringWidth(foster, this.params);
			for (int i = 0; i != (this.lightType == ElementLightType.POINT ? 1 : 3); i++) {
				foster.setString(this.params[i]).draw(x + 10, yOffset + foster.getHalfHeight(), Align.left);
				this.parameters[i].setTransforms(x + longestString + 15, yOffset, this.width - longestString - 25, 15).setFoster(foster).update();
				this.parameters[i].render(batch, shape);
				yOffset -= 19;
			}
			return yOffset + 14;
		}
		
		public void whileInputFieldFocused(STDInputField inputField, String id) {
			switch (id) {
				case "position": UIElement._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2]); break;
				case "rotation": UIElement._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2]); break;
				case "color": UIElement._convertTextToColor(this.light.color, this.color[0], this.color[1], this.color[2], this.color[3]);
				case "intensity": {
					if (this.lightType == ElementLightType.POINT) ((PointLight)this.light).intensity = UIElement._convertTextToFloat(this.parameters[0], ((PointLight)this.light).intensity);
					else if (this.lightType == ElementLightType.SPOT) ((SpotLight)this.light).intensity = UIElement._convertTextToFloat(this.parameters[0], ((SpotLight)this.light).intensity);
				} break;
				case "cutoffangle": ((SpotLight)this.light).cutoffAngle = UIElement._convertTextToFloat(this.parameters[1], ((SpotLight)this.light).cutoffAngle); break;
				case "exponent": ((SpotLight)this.light).exponent = UIElement._convertTextToFloat(this.parameters[2], ((SpotLight)this.light).exponent); break;
			}
		}
		
		public void onInputFieldFocusRemoved(STDInputField inputField, String id) {
			try { inputField.setTextWithPointer(String.valueOf(Float.valueOf(inputField.getText()))).dropOffset(); } catch (Exception e) {}
		}
		
		private void updateBlock(ElementLight light) {
			this.lightType = light.type;
			this.light = light.getLight(BaseLight.class);
			UIElement._convertVector3ToText(light.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2], true);
			UIElement._convertVector3ToText(light.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2], true);
			UIElement._convertColorToText(light.getLight(BaseLight.class).color, this.color[0], this.color[1], this.color[2], this.color[3], true);
			if (this.lightType == ElementLightType.POINT) {
				this.parameters[0].setTextWithPointer(String.valueOf(((PointLight)this.light).intensity)).dropOffset();
			} else if (this.lightType == ElementLightType.SPOT) {
				SpotLight spotLight = (SpotLight)this.light;
				this.parameters[0].setTextWithPointer(String.valueOf(spotLight.intensity)).dropOffset();
				this.parameters[1].setTextWithPointer(String.valueOf(spotLight.cutoffAngle)).dropOffset();
				this.parameters[2].setTextWithPointer(String.valueOf(spotLight.exponent)).dropOffset();
			}
		}
		
		private void updateGizmoValues() {
			if (this.parent != null && this.parent.element != null) {
				UIElement._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2], false);
				UIElement._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2], false);
			}
		}
	}
}
