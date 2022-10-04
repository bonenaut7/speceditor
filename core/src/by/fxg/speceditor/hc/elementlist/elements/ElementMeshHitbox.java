package by.fxg.speceditor.hc.elementlist.elements;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.specformat.graph.SpecHitbox;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.GameManager;
import by.fxg.speceditor.hc.elementlist.EnumTransform;
import by.fxg.speceditor.hc.elementlist.IConvertable;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.TreeElement;
import by.fxg.speceditor.hc.elementlist.TreeElementRenderable;
import by.fxg.speceditor.hc.elementlist.renderables.TERMeshHitbox;
import by.fxg.speceditor.project.Project;
import by.fxg.speceditor.tools.debugdraw.DebugDraw3D;
import by.fxg.speceditor.tools.debugdraw.IDebugDraw;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public class ElementMeshHitbox extends TreeElement implements IDebugDraw, IConvertable<SpecHitbox> {
	private static Matrix4 tmpMatrix = new Matrix4();
	
	private TreeElementRenderable<ElementMeshHitbox> renderable;
	public ElementMultiHitbox parent = null;
	
	public String localModelHandle = "";
	public FileHandle modelHandle = null;
	public Model loadedModel = null; //model to use nodes in render
	public int nodeUsed = -2; //-1 to all, -2 to none
	private btCollisionShape shape = null;
	
	public long flags = 0;
	private Vector3 position = new Vector3();
	private Vector3 rotation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	
	public ElementMeshHitbox() { this("New mesh hitbox"); }
	public ElementMeshHitbox(String name) {
		this.name = name;
		this.shape = Bullet.obtainStaticNodeShape(GameManager.standartModel.nodes); //convert to bullet shape parts so can be easily saved
		this.renderable = new TERMeshHitbox(this);
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
	
	public void onDelete() {
		if (this.shape != null) this.shape.dispose();
	}
	
	public void setModel(FileHandle handle, Model model) {
		this.modelHandle = handle;
		String[] variants = new String[model.nodes.size + 1];
		variants[0] = "All nodes";
		for (int i = 0; i != model.nodes.size; i++) variants[i + 1] = model.nodes.get(i).id;
		((TERMeshHitbox)this.renderable).select.setSelectedVariant(0);
		((TERMeshHitbox)this.renderable).select.setVariants(variants);
		this.loadedModel = model;
		this.nodeUsed = -2;
	}
	
	public void changeShape(int variant) {
		if (this.loadedModel != null) {
			if (this.shape != null) {
				this.shape.dispose();
				this.shape = null;
			}
			((TERMeshHitbox)this.renderable).select.setSelectedVariant(Math.min(variant, this.loadedModel.nodes.size));
			this.nodeUsed = variant - 1;
			if (variant == 0) {
				this.shape = Bullet.obtainStaticNodeShape(this.loadedModel.nodes);
			} else {
				if (this.loadedModel.nodes.size >= variant && variant > -1) {
					this.shape = Bullet.obtainStaticNodeShape(this.loadedModel.nodes.get(variant - 1), true);
					this.nodeUsed = variant - 1;
				} else {
					this.shape = Bullet.obtainStaticNodeShape(GameManager.standartModel.nodes);
					this.nodeUsed = -1;
				}
			}
		} else {
			this.nodeUsed = -2;
		}
	}
	
	public void draw(PMObjectExplorer pmoe, DebugDraw3D draw) {
		if (this.shape != null && !this.shape.isDisposed()) {
			if (this.parent != null) {
				tmpMatrix.setToTranslation(this.parent.getTransform(EnumTransform.TRANSLATE));
				tmpMatrix.rotate(1F, 0F, 0F, this.parent.getTransform(EnumTransform.ROTATE).x);
				tmpMatrix.rotate(0F, 1F, 0F, this.parent.getTransform(EnumTransform.ROTATE).y);
				tmpMatrix.rotate(0F, 0F, 1F, this.parent.getTransform(EnumTransform.ROTATE).z);
				tmpMatrix.translate(this.position);
				tmpMatrix.rotate(1F, 0F, 0F, this.rotation.x);
				tmpMatrix.rotate(0F, 1F, 0F, this.rotation.y);
				tmpMatrix.rotate(0F, 0F, 1F, this.rotation.z);
				tmpMatrix.scale(this.parent.getTransform(EnumTransform.SCALE).x, this.parent.getTransform(EnumTransform.SCALE).y, this.parent.getTransform(EnumTransform.SCALE).z);
				tmpMatrix.scale(this.scale.x, this.scale.y, this.scale.z);
			} else {
				tmpMatrix.setToTranslation(this.position);
				tmpMatrix.rotate(1F, 0F, 0F, this.rotation.x);
				tmpMatrix.rotate(0F, 1F, 0F, this.rotation.y);
				tmpMatrix.rotate(0F, 0F, 1F, this.rotation.z);
				tmpMatrix.scale(this.scale.x, this.scale.y, this.scale.z);
			}
			draw.world.debugDrawObject(tmpMatrix, this.shape, pmoe.selectedItems.contains(this, true) || pmoe.selectedItems.contains(this.parent, true) ? UColor.hitboxSelected : UColor.hitbox);
		}
	}
	
	public Vector3 getTransform(EnumTransform transformType) {
		switch(transformType) {
			case TRANSLATE: return this.position;
			case ROTATE: return this.rotation;
			case SCALE: return this.scale;
			default: return localTempVector.set(0, 0, 0);
		}
	}
	
	public Vector3 getOffsetTransform(EnumTransform transformType) {
		if (this.parent != null) {
			switch (transformType) {
				case TRANSLATE: return localTempVector.set(this.parent.getTransform(EnumTransform.TRANSLATE));
				default:
			}
		}
		return localTempVector.set(0, 0, 0);
	}
	
	public void setLocalHandle(String path) {
		this.localModelHandle = path;
		((TERMeshHitbox)this.renderable).input[10].setText(path);
	}
	
	public boolean isTransformable(EnumTransform transformType) { return true; }
	public Sprite getSprite() { return Game.storage.sprites.get(String.format("obj.meshhitbox.%b", this.isVisible)); }
	public TreeElementRenderable<?> getRenderable() { return this.renderable; }
	public TreeElement clone() {
		ElementMeshHitbox element = new ElementMeshHitbox(this.name + ".clone");
		element.isVisible = this.isVisible;
		element.localModelHandle = this.localModelHandle;
		element.modelHandle = this.modelHandle;
		element.loadedModel = this.loadedModel;
		element.nodeUsed = this.nodeUsed;
		element.flags = this.flags;
		element.position.set(this.position);
		element.rotation.set(this.rotation);
		element.scale.set(this.scale);
		if (this.parent != null) element.parent = this.parent;
		return element;
	}
	
	public SpecHitbox convert() {
		SpecHitbox hitbox = new SpecHitbox();
		hitbox.name = this.name;
		hitbox.type = -1;
		hitbox.flags = this.flags;
		hitbox.position = new Vector3(this.position);
		hitbox.rotation = new Vector3(this.rotation);
		hitbox.scale = new Vector3(this.scale);
		hitbox.localMeshPath = this.localModelHandle.length() == 0 ? (this.modelHandle != null ? this.modelHandle.path().substring(Project.instance.projectFolder.path().length() + 1) : null) : this.localModelHandle;
		hitbox.localMeshNode = this.nodeUsed;
		return hitbox;
	}
}
