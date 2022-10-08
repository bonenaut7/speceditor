package by.fxg.speceditor.std.editorPane;

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
import by.fxg.speceditor.ui.ISTDInputFieldListener;
import by.fxg.speceditor.ui.STDInputField;
import by.fxg.speceditor.ui.SpecInterface;
import by.fxg.speceditor.ui.URenderBlock;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneLight extends EditorPane implements ISTDInputFieldListener {
	private ElementLight element = null;
	private STDInputField elementName;
	private TransformBlock transform;
	
	public EditorPaneLight() {
		this.elementName = new STDInputField(null).setAllowFullfocus(false).setListener(this, "name").setMaxLength(32);
		this.transform = (TransformBlock)new TransformBlock(this).setDropped(true);
	}
	
	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {
		foster.setString("Name:").draw(x + 5, (yOffset -= 10) + 1, Align.left);
		this.elementName.setTransforms(x + (int)foster.getWidth() + 10, yOffset -= 10, width - (int)foster.getWidth() - 15, 15).setFoster(foster).update();
		this.elementName.render(batch, shape);
		
		yOffset = this.transform.setTransforms(x + 8, width - 16).render(batch, shape, foster, yOffset - 5);
		return yOffset;
	}

	public void updatePane(ITreeElementSelector<?> selector) {
		this.element = (ElementLight)selector.get(0);
		this.elementName.setText(this.element.getName());
		
		this.transform.updateBlock(this.element);
	}
	
	public void whileFocused(STDInputField inputField, String id) {
		switch (id) {
			case "name": this.element.setName(this.elementName.getText()); break;
		}
	}

	public boolean acceptElement(ITreeElementSelector<?> selector) {
		return selector.size() == 1 && selector.get(0) instanceof ElementLight;
	}
	
	private class TransformBlock extends URenderBlock implements ISTDInputFieldListener {
		private final String[] coords = {"X", "Y", "Z"}, colors = {"R", "G", "B", "A"}, params = {"Intensity", "Cutoff angle", "Exponent"};
		private EditorPaneLight parent;
		private ElementLightType lightType = ElementLightType.POINT;
		private BaseLight<?> light = null;
		private STDInputField[] position = new STDInputField[3], rotation = new STDInputField[3], color = new STDInputField[4], parameters = new STDInputField[3];

		private TransformBlock(EditorPaneLight parent) {
			super("Transforms");
			this.parent = parent;
			
			STDInputField.Builder builder = new STDInputField.Builder().setAllowFullfocus(false).setNumeralInput(true).setMaxLength(15); //FIXME not sure about 16, check later
			for (int i = 0; i != 3; i++) this.position[i] = builder.setListener(this, "position").build();
			for (int i = 0; i != 3; i++) this.rotation[i] = builder.setListener(this, "rotation").build();
			for (int i = 0; i != 4; i++) this.color[i] = builder.setListener(this, "scale").build();
			this.parameters[0] = builder.setListener(this, "intensity").build();
			this.parameters[1] = builder.setListener(this, "cutoffangle").build();
			this.parameters[2] = builder.setListener(this, "exponent").build();
			builder.addToLink(this.position).addToLink(this.rotation).addToLink(this.color).addToLink(this.parameters[0]); //other components are not available for every type of light present here
			builder.linkFields();
		}

		protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
			if (SpecInterface.get.currentFocus instanceof GizmosModule) this.updateGizmoValues();
			foster.setString("Position:").draw(this.x, (yOffset -= 22) + 7, Align.left);
			foster.setString(this.coords[0]);
			int sizePerPart = (this.width - 30 - (int)foster.getWidth() * 3) / 3;
			
			yOffset -= 10;
			for (int i = 0; i != 3; i++) {
				foster.setString(this.coords[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + 1);
				this.position[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset - 10, sizePerPart, 15).setFoster(foster).update();
				this.position[i].render(batch, shape);
			}

			if (this.lightType == ElementLightType.SPOT) {
				foster.setString("Rotation:").draw(this.x, (yOffset -= 25) + 7, Align.left);
				yOffset -= 10;
				for (int i = 0; i != 3; i++) {
					foster.setString(this.coords[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + 1);
					this.rotation[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset - 10, sizePerPart, 15).setFoster(foster).update();
					this.rotation[i].render(batch, shape);
				}
			}
			
			foster.setString("Color:").draw(this.x, (yOffset -= 25) + 7, Align.left);
			for (int i = 0; i != 4; i++) {
				foster.setString(this.colors[i]).draw(this.x + 10, (yOffset -= 10) + 1, Align.left);
				this.color[i].setTransforms(this.x + (int)foster.getWidth() + 35, yOffset -= 10, this.width - (int)foster.getWidth() - 35, 15).setFoster(foster).update();
				this.color[i].render(batch, shape);
			}

			foster.setString("Parameters:").draw(this.x, (yOffset -= 25) + 7, Align.left);
			for (int i = 0; i != (this.lightType == ElementLightType.POINT ? 1 : 4); i++) {
				foster.setString(this.params[i]).draw(this.x + 10, (yOffset -= 10) + 1, Align.left);
				this.parameters[i].setTransforms(this.x + (int)foster.getWidth() + 35, yOffset -= 10, this.width - (int)foster.getWidth() - 35, 15).setFoster(foster).update();
				this.parameters[i].render(batch, shape);
			}
			return yOffset;
		}
		
		public void whileFocused(STDInputField inputField, String id) {
			switch (id) {
				case "position": this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2]); break;
				case "rotation": this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2]); break;
				case "color": this.parent._convertTextToColor(this.light.color, this.color[0], this.color[1], this.color[2], this.color[3]);
				case "intensity": {
					if (this.lightType == ElementLightType.POINT) ((PointLight)this.light).intensity = this.parent._convertTextToFloat(this.parameters[0], ((PointLight)this.light).intensity);
					else if (this.lightType == ElementLightType.SPOT) ((SpotLight)this.light).intensity = this.parent._convertTextToFloat(this.parameters[0], ((SpotLight)this.light).intensity);
				} break;
				case "cutoffangle": ((SpotLight)this.light).cutoffAngle = this.parent._convertTextToFloat(this.parameters[1], ((SpotLight)this.light).cutoffAngle); break;
				case "exponent": ((SpotLight)this.light).exponent = this.parent._convertTextToFloat(this.parameters[2], ((SpotLight)this.light).exponent); break;
			}
		}
		
		private void updateBlock(ElementLight light) {
			this.lightType = light.type;
			this.light = light.getLight(BaseLight.class);
			this.parent._convertVector3ToText(light.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2]);
			this.parent._convertVector3ToText(light.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2]);
			this.parent._convertColorToText(light.getLight(BaseLight.class).color, this.color[0], this.color[1], this.color[2], this.color[3]);
		}
		
		private void updateGizmoValues() {
			if (this.parent != null && this.parent.element != null) {
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2]);
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2]);
			}
		}
	}
}
