package by.fxg.speceditor.hc.elementlist.elements;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.specformat.graph.SpecModel;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.GameManager;
import by.fxg.speceditor.hc.elementlist.EnumTransform;
import by.fxg.speceditor.hc.elementlist.IConvertable;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.TreeElement;
import by.fxg.speceditor.hc.elementlist.TreeElementRenderable;
import by.fxg.speceditor.hc.elementlist.renderables.TERModel_Default;
import by.fxg.speceditor.hc.elementlist.renderables.TERModel_GLTF;
import by.fxg.speceditor.project.Project;
import by.fxg.speceditor.project.ProjectType;
import by.fxg.speceditor.tools.g3d.IModelProvider;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class ElementModel extends TreeElement implements IModelProvider, IConvertable<SpecModel> {
	private TreeElementRenderable<ElementModel> renderable;
	
	public String localModelHandle = "";
	public FileHandle modelHandle = null;
	public ModelInstance modelInstance;
	private Vector3 position = new Vector3();
	private Vector3 rotation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	
	public ElementModel() { this("New model"); }
	public ElementModel(String name) {
		this.name = name;
		this.modelInstance = new ModelInstance(GameManager.standartModel);
		this.renderable = Project.instance.projectType == ProjectType.DEFAULT ? new TERModel_Default(this) : new TERModel_GLTF(this);
	}
	
	public void onInteract(PMObjectExplorer list, boolean hold, boolean iconTouch) {
		if (!hold) {
			if (iconTouch) {
				if (Game.get.getTick() - this.lastClickTime < 25L) {
					this.lastClickTime = 0;
					this.isVisible = !this.isVisible;
				} else this.lastClickTime = Game.get.getTick();
			} else {
				if (Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) && list.isElementSelected(this)) list.elementUnselect(this);
				else list.elementSelect(this);
			}
		}
	}
	
	public void addDropdownParameters(PMObjectExplorer pmoe, Array<TreeElement> selected, Array<UDAElement> array) {
		if (selected.size == 1) {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide" : "Show"));
		} else {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide all" : "Show all"));
		}
		super.addDefaultDropdownParameters(pmoe, selected, array);
	}
	
	public void processDropdown(PMObjectExplorer pmoe, Array<TreeElement> selected, String key) {
		switch (key) {
			case "basic.hide": {
				for (TreeElement element : selected) element.setVisible(false);
			} return;
			case "basic.show": {
				for (TreeElement element : selected) element.setVisible(true);
			} return;
		}
	}
	
	public IModelProvider applyTransforms() {
		if (this.modelInstance != null) {
			this.modelInstance.transform.setToTranslation(this.position);
			this.modelInstance.transform.scale(this.scale.x, this.scale.y, this.scale.z);
			this.modelInstance.transform.rotate(1f, 0f, 0f, this.rotation.x);
			this.modelInstance.transform.rotate(0f, 1f, 0f, this.rotation.y);
			this.modelInstance.transform.rotate(0f, 0f, 1f, this.rotation.z);
		}
		return this;
	}
	
	public RenderableProvider getDefaultModel() { return this.modelInstance; }
	public SceneAsset getGLTFModel() { return null; }
	
	public Vector3 getTransform(EnumTransform transformType) {
		switch(transformType) {
			case TRANSLATE: return this.position;
			case ROTATE: return this.rotation;
			case SCALE: return this.scale;
			default: return localTempVector.set(0, 0, 0);
		}
	}
	
	public void setLocalHandle(String path) {
		this.localModelHandle = path;
		((TERModel_Default)this.renderable).input[10].setText(path);
	}
	
	public boolean isTransformable(EnumTransform transformType) { return true; }
	public Sprite getSprite() { return Game.storage.sprites.get(String.format("obj.model.%b", this.isVisible)); }
	public TreeElementRenderable<?> getRenderable() { return this.renderable; }
	public TreeElement clone() {
		ElementModel element = new ElementModel(this.name + ".clone");
		element.isVisible = this.isVisible;
		element.localModelHandle = this.localModelHandle;
		element.modelHandle = this.modelHandle;
		element.modelInstance = new ModelInstance(this.modelInstance.model);
		element.position.set(this.position);
		element.rotation.set(this.rotation);
		element.scale.set(this.scale);
		return element;
	}

	public SpecModel convert() {
		SpecModel model = new SpecModel();
		model.name = this.name;
		model.modelPath = this.localModelHandle.length() == 0 ? (this.modelHandle != null ? this.modelHandle.path().substring(Project.instance.projectFolder.path().length() + 1) : null) : this.localModelHandle;
		model.materials = new Array<>(this.modelInstance.materials);
		model.position = new Vector3(this.position);
		model.rotation = new Vector3(this.rotation);
		model.scale = new Vector3(this.scale);
		return model;
	}
}
