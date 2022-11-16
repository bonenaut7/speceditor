package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.project.assets.ProjectAsset;
import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.GizmosModule;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.objectTree.elements.ElementHitboxMesh;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.ui.UIElement;
import by.fxg.speceditor.ui.ColoredInputField;
import by.fxg.speceditor.ui.NumberCursorInputField;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UDropdownSelectMultiple;
import by.fxg.speceditor.ui.URenderBlock;
import by.fxg.speceditor.utils.SpecFileChooser;
import by.fxg.speceditor.utils.Utils;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import space.earlygrey.shapedrawer.ShapeDrawer;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EditorPaneHitboxMesh extends EditorPaneTreeElementHitbox implements ISTDInputFieldListener {
	private ElementHitboxMesh element = null;
	private STDInputField elementName;
	private UButton buttonSelectModel;
	private UDropdownSelectMultiple nodesSelector;
	
	private TransformBlock transform;
	private SpecFlagsBlock specFlags;
	private BulletFlagsBlock bulletFlags;
	
	public EditorPaneHitboxMesh() {
		this.elementName = new ColoredInputField().setAllowFullfocus(false).setListener(this, "name").setMaxLength(48);
		this.buttonSelectModel = new UButton("Open file");
		this.nodesSelector = new UDropdownSelectMultiple(15, "None") {
			public void invertSelected(int variant) {
				element.nodes[variant] = this.variantValues[variant] = !this.variantValues[variant];
			}
		};
		
		this.transform = (TransformBlock)new TransformBlock(this).setDropped(true);
		this.specFlags = (SpecFlagsBlock)new SpecFlagsBlock(this).setDropped(true);
		this.bulletFlags = (BulletFlagsBlock)new BulletFlagsBlock(this).setDropped(true);
	}
	
	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {
		yOffset -= 8;
		float longestString = this.getLongestStringWidth(foster, "Name", "Mesh", "Nodes");
		foster.setString("Name").draw(x + 5, yOffset -= foster.getHeight(), Align.left);
		this.elementName.setTransforms(x + longestString + 10, yOffset -= foster.getHalfHeight(), width - longestString - 15, 15).setFoster(foster).update();
		this.elementName.render(batch, shape);
		
		foster.setString("Mesh").draw(x + 5, yOffset -= foster.getHeight() + 8, Align.left);
		this.buttonSelectModel.setTransforms(x + longestString + 10, yOffset -= foster.getHalfHeight(), width - longestString - 15, 15).render(shape, foster);
		if (this.buttonSelectModel.isPressed()) {
			FileHandle handle = SpecFileChooser.getInProjectDirectory().setFilter(Utils.FILENAMEFILTER_MODELS).openSingle(true, false);
			if (handle != null) {
				ProjectAsset projectAsset = null;
				if (handle.extension().equalsIgnoreCase("gltf") || handle.extension().equalsIgnoreCase("glb")) {
					projectAsset = ProjectAssetManager.INSTANCE.getLoadAsset(SceneAsset.class, handle);
				} else projectAsset = ProjectAssetManager.INSTANCE.getLoadAsset(Model.class, handle);
				projectAsset.addHandler(this.element);
				this.element.generateMesh();
				if (this.element.model != null) {
					String[] nodes = new String[this.element.model.nodes.size];
					for (int i = 0 ; i != this.element.model.nodes.size; i++) nodes[i] = this.element.model.nodes.get(i).id;
					this.nodesSelector.setVariants(nodes, this.element.nodes);
				} else this.nodesSelector.setVariants(new String[]{"None available"}, new boolean[]{false});
			}
		}
		
		foster.setString("Nodes").draw(x + 5, yOffset -= foster.getHeight() + 6, Align.left);
		this.nodesSelector.setTransforms(x + longestString + 10, (yOffset -= 4) - foster.getHalfHeight() + 3, width - longestString - 15, 14).update(foster);
		if (this.nodesSelector.isFocused()) yOffset -= this.nodesSelector.getDropHeight();
		
		yOffset = this.transform.setTransforms(x + 5, width - 10).render(batch, shape, foster, yOffset - 5);
		yOffset = this.specFlags.setTransforms(x + 5, width - 10).render(batch, shape, foster, yOffset - 5);
		yOffset = this.bulletFlags.setTransforms(x + 5, width - 10).render(batch, shape, foster, yOffset - 5);
		this.nodesSelector.render(shape, foster);
		return yOffset;
	}

	public void whileInputFieldFocused(STDInputField inputField, String id) {
		switch (id) {
			case "name": this.element.setName(this.elementName.getText()); break;
		}
	}
	
	public void updatePane(ITreeElementSelector<?> selector) {
		super.updatePane(selector);
		this.element = (ElementHitboxMesh)selector.get(0);
		this.elementName.setText(this.element.getName());
		if (this.element.model != null) {
			String[] nodes = new String[this.element.model.nodes.size];
			for (int i = 0 ; i != this.element.model.nodes.size; i++) nodes[i] = this.element.model.nodes.get(i).id;
			this.nodesSelector.setVariants(nodes, this.element.nodes).updateDisplayString(SpecEditor.fosterNoDraw);
		} else this.nodesSelector.setVariants(new String[]{"None available"}, new boolean[]{false}).updateDisplayString(SpecEditor.fosterNoDraw);
		
		this.transform.updateBlock(this.element);
		this.specFlags.updateBlock(this.element);
		this.bulletFlags.updateBlock(this.element);
	}

	public boolean acceptElement(ITreeElementSelector<?> selector) {
		return selector.size() == 1 && selector.get(0) instanceof ElementHitboxMesh;
	}
	
	private class TransformBlock extends URenderBlock implements ISTDInputFieldListener {
		private final String[] coords = {"X", "Y", "Z"};
		private EditorPaneHitboxMesh parent;
		private STDInputField[] position = new STDInputField[3], rotation = new STDInputField[3], scale = new STDInputField[3];
		
		private TransformBlock(EditorPaneHitboxMesh parent) {
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
				case "position": UIElement._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2]); break;
				case "rotation": UIElement._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2]); break;
				case "scale": UIElement._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.SCALE), this.scale[0], this.scale[1], this.scale[2]); break;
			}
		}
		
		public void onInputFieldFocusRemoved(STDInputField inputField, String id) {
			try { inputField.setTextWithPointer(String.valueOf(Float.valueOf(inputField.getText()))).dropOffset(); } catch (Exception e) {}
		}
		
		private void updateBlock(ElementHitboxMesh model) {
			UIElement._convertVector3ToText(model.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2], true);
			UIElement._convertVector3ToText(model.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2], true);
			UIElement._convertVector3ToText(model.getTransform(GizmoTransformType.SCALE), this.scale[0], this.scale[1], this.scale[2], true);
		}
		
		private void updateGizmoValues() {
			if (this.parent != null && this.parent.element != null) {
				UIElement._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2], false);
				UIElement._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2], false);
				UIElement._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.SCALE), this.scale[0], this.scale[1], this.scale[2], false);
			}
		}
	}
}
