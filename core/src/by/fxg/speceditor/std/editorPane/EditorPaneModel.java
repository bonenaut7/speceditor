package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.api.std.editorPane.EditorPane;
import by.fxg.speceditor.api.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatsel;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.GizmosModule;
import by.fxg.speceditor.std.objecttree.elements.ElementModel;
import by.fxg.speceditor.ui.SpecInterface;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UInputField;
import by.fxg.speceditor.ui.URenderBlock;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneModel extends EditorPane {
	private ElementModel element = null;
	private UInputField modelName, modelPath;
	private UButton buttonSelectModel;
	
	private TransformBlock transform;
	private EditorPaneMatsel matsel;
	
	public EditorPaneModel() {
		this.modelName = new UInputField().setMaxLength(32);
		this.modelPath = new UInputField().setMaxLength(128);
		this.buttonSelectModel = new UButton("Open file");
		this.transform = (TransformBlock)new TransformBlock(this).setDropped(true);
		this.matsel = (EditorPaneMatsel)new EditorPaneMatsel("Material selection").setDropped(true);
	}
	
	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {
		foster.setString("Name:").draw(x + 5, (yOffset -= 10) + 1, Align.left);
		this.modelName.setTransforms(x + (int)foster.getWidth() + 10, yOffset -= 10, width - (int)foster.getWidth() - 15, 15).update();
		this.modelName.render(batch, shape, foster);
		this.element.setName(this.modelName.getText());
		
		foster.setString("EXT Path:").draw(x + 5, (yOffset -= 10) + 1, Align.left);
		this.modelPath.setTransforms(x + (int)foster.getWidth() + 10, yOffset -= 10, width - (int)foster.getWidth() - 15, 15).update();
		this.modelPath.render(batch, shape, foster);
		this.element.localModelHandle = this.modelPath.getText();
		
		foster.setString("Select model:").draw(x + 5, (yOffset -= 10) + 1, Align.left);
		this.buttonSelectModel.setTransforms(x + (int)foster.getWidth() + 10, yOffset -= 10, width - (int)foster.getWidth() - 15, 15).render(shape, foster);
		if (this.buttonSelectModel.isPressed()) {
			try {
				FileHandle handle = Utils.selectFileDialog("Supported models (*.obj; *.gltf)", "obj", "gltf");
				AssetDescriptor<Model> assetDescriptor = new AssetDescriptor<Model>(handle, Model.class);
				if (Game.get.resourceManager.assetManager.isLoaded(assetDescriptor)) {
					Game.get.resourceManager.assetManager.unload(handle.path());
				}
				Game.get.resourceManager.assetManager.load(assetDescriptor);
				Game.get.resourceManager.assetManager.finishLoading();
				if (Game.get.resourceManager.assetManager.isLoaded(assetDescriptor)) {
					this.element.modelHandle = handle;
					this.element.modelInstance = new ModelInstance(Game.get.resourceManager.assetManager.get(assetDescriptor));
					this.matsel.update(this.element.modelInstance.materials);
				}
			} catch (Exception e) {}
		}
		
		yOffset = this.transform.setTransforms(x + 8, width - 16).render(batch, shape, foster, yOffset - 5);
		yOffset = this.matsel.setTransforms(x + 8, width - 16).render(batch, shape, foster, yOffset);
		if (this.matsel.dropdownArea.isFocused()) this.matsel.dropdownArea.render(shape, foster);
		return yOffset;
	}

	public void updatePane(ITreeElementSelector<?> selector) {
		this.element = (ElementModel)selector.get(0);
		this.modelName.setText(this.element.getName());
		this.modelPath.setText(this.element.localModelHandle);
		
		this.transform.updateBlock(this.element);
		this.matsel.update(this.element != null && this.element.modelInstance != null ? this.element.modelInstance.materials : null);
	}

	public boolean acceptElement(ITreeElementSelector<?> selector) {
		return selector.size() == 1 && selector.get(0) instanceof ElementModel;
	}
	
	private class TransformBlock extends URenderBlock {
		private EditorPaneModel parent;
		private UInputField[] position = new UInputField[3], rotation = new UInputField[3], scale = new UInputField[3];
		
		private TransformBlock(EditorPaneModel parent) {
			super("Transforms");
			this.parent = parent;
			
			for (int i = 0; i != 3; i++) this.position[i] = new UInputField().setAllowedCharacters("0123456789-.").setMaxLength(15); //FIXME not sure about 16, check later
			for (int i = 0; i != 3; i++) this.rotation[i] = new UInputField().setAllowedCharacters("0123456789-.").setMaxLength(15);
			for (int i = 0; i != 3; i++) this.scale[i] = new UInputField().setAllowedCharacters("0123456789-.").setMaxLength(15);
		}

		protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
			if (SpecInterface.get.currentFocus instanceof GizmosModule) this.updateGizmoValues();
			
			String[] coords = {"X", "Y", "Z"};
			foster.setString("Position:").draw(this.x, (yOffset -= 22) + 7, Align.left);
//			Original
//			for (int i = 0; i != 3; i++) {
//				foster.setString(coords[i]).draw(this.x + 10, (yOffset -= 10) + 1, Align.left);
//				this.position[i].setTransforms(this.x + (int)foster.getWidth() + 35, yOffset -= 10, this.width - (int)foster.getWidth() - 35, 15).update();
//				this.position[i].render(batch, shape, foster);
//			}
			
			foster.setString(coords[0]);
			int sizePerPart = (this.width - 30 - (int)foster.getWidth() * 3) / 3;
			
			yOffset -= 10;
			for (int i = 0; i != 3; i++) {
				foster.setString(coords[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + 1);
				this.position[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset - 10, sizePerPart, 15).update();
				this.position[i].render(batch, shape, foster);
			}
			if (this.position[0].isFocused() || this.position[1].isFocused() || this.position[2].isFocused()) 
				this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2]);

			foster.setString("Rotation:").draw(this.x, (yOffset -= 25) + 7, Align.left);
			yOffset -= 10;
			for (int i = 0; i != 3; i++) {
				foster.setString(coords[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + 1);
				this.rotation[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset - 10, sizePerPart, 15).update();
				this.rotation[i].render(batch, shape, foster);
			}
			if (this.rotation[0].isFocused() || this.rotation[1].isFocused() || this.rotation[2].isFocused())
				this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2]);

			foster.setString("Scale:").draw(this.x, (yOffset -= 25) + 7, Align.left);
			yOffset -= 10;
			for (int i = 0; i != 3; i++) {
				foster.setString(coords[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + 1);
				this.scale[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset - 10, sizePerPart, 15).update();
				this.scale[i].render(batch, shape, foster);
			}
			if (this.scale[0].isFocused() || this.scale[1].isFocused() || this.scale[2].isFocused())
				this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.SCALE), this.scale[0], this.scale[1], this.scale[2]);
			
			return yOffset;
		}
		
		private void updateBlock(ElementModel model) {
			this.parent._convertVector3ToText(model.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2]);
			this.parent._convertVector3ToText(model.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2]);
			this.parent._convertVector3ToText(model.getTransform(GizmoTransformType.SCALE), this.scale[0], this.scale[1], this.scale[2]);
		}
		
		private void updateGizmoValues() {
			if (this.parent != null && this.parent.element != null) {
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2]);
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2]);
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.SCALE), this.scale[0], this.scale[1], this.scale[2]);
			}
		}
	}
}
