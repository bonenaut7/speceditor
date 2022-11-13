package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.GizmosModule;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.objectTree.elements.ElementHitboxStack;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.ColoredInputField;
import by.fxg.speceditor.ui.NumberCursorInputField;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.ui.URenderBlock;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneHitboxStack extends EditorPaneTreeElementHitbox implements ISTDInputFieldListener {
	private ElementHitboxStack element = null;
	private STDInputField elementName;
	private UDropdownSelectSingle stackType;
	private TransformBlock transform;
	private SpecFlagsBlock specFlags;
	private BulletFlagsBlock bulletFlags;
	
	public EditorPaneHitboxStack() {
		this.elementName = new ColoredInputField().setAllowFullfocus(false).setListener(this, "name").setMaxLength(48);
		this.stackType = new UDropdownSelectSingle(15, "Combined Stack", "Hitbox Array");
		
		this.transform = (TransformBlock)new TransformBlock(this).setDropped(true);
		this.specFlags = (SpecFlagsBlock)new SpecFlagsBlock(this).setDropped(true);
		this.bulletFlags = (BulletFlagsBlock)new BulletFlagsBlock(this).setDropped(true);
	}
	
	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {
		yOffset -= 8;
		foster.setString("Name").draw(x + 5, yOffset -= foster.getHeight(), Align.left);
		this.elementName.setTransforms(x + foster.getWidth() + 10, yOffset -= foster.getHalfHeight(), width - foster.getWidth() - 15, 15).setFoster(foster).update();
		this.elementName.render(batch, shape);
		
		yOffset -= 8;
		foster.setString("Type").draw(x + 5, yOffset -= foster.getHeight(), Align.left);
		this.stackType.setTransforms(x + foster.getWidth() + 10, yOffset -= foster.getHalfHeight(), width - foster.getWidth() - 15, 15).render(shape, foster);
		if (this.stackType.isFocused()) yOffset -= this.stackType.getVariants().length * 15;
		this.element.isArrayStack = this.stackType.getVariantSelected() == 1;
		
		yOffset = this.transform.setTransforms(x + 5, width - 10).render(batch, shape, foster, yOffset - 5);
		yOffset = this.specFlags.setTransforms(x + 5, width - 10).render(batch, shape, foster, yOffset - 5);
		yOffset = this.bulletFlags.setTransforms(x + 5, width - 10).render(batch, shape, foster, yOffset - 5);
		this.stackType.update();
		return yOffset;
	}

	public void whileInputFieldFocused(STDInputField inputField, String id) {
		switch (id) {
			case "name": this.element.setName(this.elementName.getText()); break;
		}
	}
	
	public void updatePane(ITreeElementSelector<?> selector) {
		super.updatePane(selector);
		this.element = (ElementHitboxStack)selector.get(0);
		this.elementName.setText(this.element.getName());
		this.stackType.setVariantSelected(this.element.isArrayStack ? 1 : 0);

		this.transform.updateBlock(this.element);
		this.specFlags.updateBlock(this.element);
		this.bulletFlags.updateBlock(this.element);
	}

	public boolean acceptElement(ITreeElementSelector<?> selector) {
		return selector.size() == 1 && selector.get(0) instanceof ElementHitboxStack;
	}
	
	private class TransformBlock extends URenderBlock implements ISTDInputFieldListener {
		private final String[] coords = {"X", "Y", "Z"};
		private EditorPaneHitboxStack parent;
		private STDInputField[] position = new STDInputField[3], rotation = new STDInputField[3], scale = new STDInputField[3];
		
		private TransformBlock(EditorPaneHitboxStack parent) {
			super("Transforms");
			this.parent = parent;

			NumberCursorInputField.Builder builder = (NumberCursorInputField.Builder)new NumberCursorInputField.Builder().setAllowFullfocus(false).setMaxLength(12);
			for (int i = 0; i != 3; i++) this.position[i] = builder.setBackgroundColor(UColor.redblack).setListener(this, "position").build();
			for (int i = 0; i != 3; i++) this.rotation[i] = builder.setBackgroundColor(UColor.greenblack).setListener(this, "rotation").build();
			for (int i = 0; i != 3; i++) this.scale[i] = builder.setBackgroundColor(UColor.blueblack).setListener(this, "scale").build();
			builder.addToLink(this.position).addToLink(this.rotation).addToLink(this.scale).linkFields();
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

			foster.setString("Rotation:").draw(x, yOffset -= foster.getHeight() + 5, Align.left);
			yOffset -= 19;
			for (int i = 0; i != 3; i++) {
				foster.setString(this.coords[i]).draw(x + 10 + (foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
				this.rotation[i].setTransforms(x + 10 + foster.getWidth() + (foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
				this.rotation[i].render(batch, shape);
			}

			foster.setString("Scale:").draw(x, yOffset -= foster.getHeight() + 5, Align.left);
			yOffset -= 19;
			for (int i = 0; i != 3; i++) {
				foster.setString(this.coords[i]).draw(x + 10 + (foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
				this.scale[i].setTransforms(x + 10 + foster.getWidth() + (foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
				this.scale[i].render(batch, shape);
			}
			return yOffset - 5;
		}
		
		public void whileInputFieldFocused(STDInputField inputField, String id) {
			switch (id) {
				case "position": this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2]); break;
				case "rotation": this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2]); break;
				case "scale": this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.SCALE), this.scale[0], this.scale[1], this.scale[2]); break;
			}
		}
		
		public void onInputFieldFocusRemoved(STDInputField inputField, String id) {
			try { inputField.setTextWithPointer(String.valueOf(Float.valueOf(inputField.getText()))).dropOffset(); } catch (Exception e) {}
		}
		
		private void updateBlock(ElementHitboxStack hitboxStack) {
			this.parent._convertVector3ToText(hitboxStack.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2], true);
			this.parent._convertVector3ToText(hitboxStack.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2], true);
			this.parent._convertVector3ToText(hitboxStack.getTransform(GizmoTransformType.SCALE), this.scale[0], this.scale[1], this.scale[2], true);
		}
		
		private void updateGizmoValues() {
			if (this.parent != null && this.parent.element != null) {
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2], false);
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2], false);
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.SCALE), this.scale[0], this.scale[1], this.scale[2], false);
			}
		}
	}
}
