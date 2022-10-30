package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.GizmosModule;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.objectTree.elements.ElementDecal;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.ColoredInputField;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.URenderBlock;
import by.fxg.speceditor.ui.ColoredInputField.Builder;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneDecal extends EditorPane implements ISTDInputFieldListener {
	private ElementDecal element = null;
	private STDInputField elementName, decalPath;
	private UButton buttonSelectDecal;
	protected UCheckbox billboardDecal;
	
	private TransformBlock transform;
	
	public EditorPaneDecal() {
		this.elementName = new ColoredInputField().setAllowFullfocus(false).setListener(this, "name").setMaxLength(48);
		this.decalPath = new ColoredInputField().setAllowFullfocus(false).setListener(this, "path").setMaxLength(128).setPreviousField(this.elementName);
		this.elementName.setNextField(this.decalPath);
		
		this.buttonSelectDecal = new UButton("Open file");
		this.billboardDecal = new UCheckbox(false);
		this.transform = (TransformBlock)new TransformBlock(this).setDropped(true);
	}
	
	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {
		yOffset -= 8;
		foster.setString("Name:").draw(x + 5, yOffset -= foster.getHeight(), Align.left);
		this.elementName.setTransforms(x + (int)foster.getWidth() + 10, yOffset -= foster.getHalfHeight(), width - (int)foster.getWidth() - 15, 15).setFoster(foster).update();
		this.elementName.render(batch, shape);
		
		foster.setString("EXT Path:").draw(x + 5, yOffset -= foster.getHeight() + 8, Align.left);
		this.decalPath.setTransforms(x + (int)foster.getWidth() + 10, yOffset -= foster.getHalfHeight(), width - (int)foster.getWidth() - 15, 15).setFoster(foster).update();
		this.decalPath.render(batch, shape);
		
		foster.setString("Select decal:").draw(x + 5, yOffset -= foster.getHeight() + 8, Align.left);
		this.buttonSelectDecal.setTransforms(x + (int)foster.getWidth() + 10, yOffset -= foster.getHalfHeight(), width - (int)foster.getWidth() - 15, 15).render(shape, foster);
		if (this.buttonSelectDecal.isPressed()) {
			FileHandle handle = Utils.openFileSelectionDialog(Utils.IMAGES_DESCRIPTION, Utils.IMAGES_EXTENSIONS);
			if (handle != null) ProjectAssetManager.INSTANCE.getLoadAsset(Texture.class, handle).addHandler(this.element);
		}
		
		foster.setString("Billboard decal:").draw(x + 5, yOffset -= foster.getHeight() + 8, Align.left);
		this.billboardDecal.setTransforms(x + (int)foster.getWidth() + 10, yOffset - 2, 12, 12).update();
		this.billboardDecal.render(shape);
		this.element.decal.setBillboard(this.billboardDecal.getValue());
		
		yOffset = this.transform.setTransforms(x + 8, width - 16).render(batch, shape, foster, yOffset - 10);
		return yOffset;
	}

	public void whileInputFieldFocused(STDInputField inputField, String id) {
		switch (id) {
			case "name": this.element.setName(this.elementName.getText()); break;
			case "path": this.element.localDecalHandle = this.decalPath.getText(); break;
		}
	}
	
	public void updatePane(ITreeElementSelector<?> selector) {
		this.element = (ElementDecal)selector.get(0);
		this.elementName.setText(this.element.getName());
		this.decalPath.setText(this.element.localDecalHandle);
		this.billboardDecal.setValue(this.element.decal.isBillboard());
		
		this.transform.updateBlock(this.element);
	}

	public boolean acceptElement(ITreeElementSelector<?> selector) {
		return selector.size() == 1 && selector.get(0) instanceof ElementDecal;
	}
	
	private class TransformBlock extends URenderBlock implements ISTDInputFieldListener {
		private final String[] coords = {"X", "Y", "Z"};
		private EditorPaneDecal parent;
		private STDInputField[] position = new STDInputField[3], rotation = new STDInputField[3], scale = new STDInputField[2];
		
		private TransformBlock(EditorPaneDecal parent) {
			super("Transforms");
			this.parent = parent;

			ColoredInputField.Builder builder = (Builder)new ColoredInputField.Builder().setAllowFullfocus(false).setNumeralInput(true).setMaxLength(12);
			for (int i = 0; i != 3; i++) this.position[i] = builder.setBackgroundColor(UColor.redblack).setListener(this, "position").build();
			for (int i = 0; i != 3; i++) this.rotation[i] = builder.setBackgroundColor(UColor.greenblack).setListener(this, "rotation").build();
			for (int i = 0; i != 2; i++) this.scale[i] = builder.setBackgroundColor(UColor.blueblack).setListener(this, "scale").build();
			builder.addToLink(this.position).addToLink(this.rotation).addToLink(this.scale).linkFields();
		}

		protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
			if (SpecInterface.INSTANCE.currentFocus instanceof GizmosModule) this.updateGizmoValues();
			int sizePerPart = (this.width - 30 - (int)foster.setString(this.coords[0]).getWidth() * 3) / 3;
			
			foster.setString("Position:").draw(this.x, yOffset -= foster.getHeight(), Align.left);
			yOffset -= 19; //16 size of box + 3 offset
			for (int i = 0; i != 3; i++) {
				foster.setString(this.coords[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
				this.position[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
				this.position[i].render(batch, shape);
			}

			foster.setString("Rotation:").draw(this.x, yOffset -= foster.getHeight() + 5, Align.left);
			yOffset -= 19;
			for (int i = 0; i != 3; i++) {
				foster.setString(this.coords[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
				this.rotation[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
				this.rotation[i].render(batch, shape);
			}

			sizePerPart = (this.width - 20 - (int)foster.setString(this.coords[0]).getWidth() * 2) / 2;
			foster.setString("Scale:").draw(this.x, yOffset -= foster.getHeight() + 5, Align.left);
			yOffset -= 19;
			for (int i = 0; i != 2; i++) {
				foster.setString(this.coords[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
				this.scale[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
				this.scale[i].render(batch, shape);
			}
			return yOffset;
		}
		
		public void whileInputFieldFocused(STDInputField inputField, String id) {
			switch (id) {
				case "position": this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2]); break;
				case "rotation": this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2]); break;
				case "scale": this.parent._convertTextToVector2(this.parent.element.decal.scale, this.scale[0], this.scale[1]); break;
			}
		}
		
		public void onInputFieldFocusRemoved(STDInputField inputField, String id) {
			try { inputField.setTextWithPointer(String.valueOf(Float.valueOf(inputField.getText()))).dropOffset(); } catch (Exception e) {}
		}
		
		private void updateBlock(ElementDecal decal) {
			this.parent._convertVector3ToText(decal.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2], true);
			this.parent._convertVector3ToText(decal.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2], true);
			this.parent._convertVector2ToText(decal.decal.scale, this.scale[0], this.scale[1], true);
		}
		
		private void updateGizmoValues() {
			if (this.parent != null && this.parent.element != null) {
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2], false);
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2], false);
			}
		}
	}
}
