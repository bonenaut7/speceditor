package by.fxg.speceditor.hc.elementlist.elements;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.specformat.graph.SpecHitbox;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.hc.elementlist.EnumTransform;
import by.fxg.speceditor.hc.elementlist.IConvertable;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.TreeElement;
import by.fxg.speceditor.hc.elementlist.TreeElementRenderable;
import by.fxg.speceditor.hc.elementlist.renderables.TERHitbox;
import by.fxg.speceditor.tools.debugdraw.DebugDraw3D;
import by.fxg.speceditor.tools.debugdraw.IDebugDraw;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public class ElementHitbox extends TreeElement implements IDebugDraw, IConvertable<SpecHitbox> {
	private static Matrix4 tmpMatrix = new Matrix4();
	private static Vector3 tmpVectorMin = new Vector3(), tmpVectorMax = new Vector3();
	
	private TreeElementRenderable<ElementHitbox> renderable;
	public ElementMultiHitbox parent = null;
	
	public long flags = 0;
	public int type = 0; //0 - cube
	private Vector3 position = new Vector3();
	private Vector3 rotation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	
	public ElementHitbox() { this("New hitbox"); }
	public ElementHitbox(String name) {
		this.name = name;
		this.renderable = new TERHitbox(this);
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
	
	public void draw(PMObjectExplorer pmoe, DebugDraw3D draw) {
		tmpVectorMin.set(-0.5F, -0.5F, -0.5F);
		tmpVectorMax.set(0.5F, 0.5F, 0.5F);
		if (this.parent != null) {
			tmpVectorMin.scl(this.parent.getTransform(EnumTransform.SCALE)).scl(this.scale);
			tmpVectorMax.scl(this.parent.getTransform(EnumTransform.SCALE)).scl(this.scale);
			tmpMatrix.setToTranslation(this.parent.getTransform(EnumTransform.TRANSLATE));
			tmpMatrix.rotate(1F, 0F, 0F, this.parent.getTransform(EnumTransform.ROTATE).x);
			tmpMatrix.rotate(0F, 1F, 0F, this.parent.getTransform(EnumTransform.ROTATE).y);
			tmpMatrix.rotate(0F, 0F, 1F, this.parent.getTransform(EnumTransform.ROTATE).z);
			tmpMatrix.translate(this.position);
			tmpMatrix.rotate(1F, 0F, 0F, this.rotation.x);
			tmpMatrix.rotate(0F, 1F, 0F, this.rotation.y);
			tmpMatrix.rotate(0F, 0F, 1F, this.rotation.z);
		} else {
			tmpVectorMin.scl(this.scale);
			tmpVectorMax.scl(this.scale);
			tmpMatrix.setToTranslation(this.position);
			tmpMatrix.rotate(1F, 0F, 0F, this.rotation.x);
			tmpMatrix.rotate(0F, 1F, 0F, this.rotation.y);
			tmpMatrix.rotate(0F, 0F, 1F, this.rotation.z);
		}
		draw.drawer.drawBox(tmpVectorMin, tmpVectorMax, tmpMatrix, pmoe.selectedItems.contains(this, true) || pmoe.selectedItems.contains(this.parent, true) ? UColor.hitboxSelected : UColor.hitbox);
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
	
	public boolean isTransformable(EnumTransform transformType) { return true; }
	public Sprite getSprite() { return Game.storage.sprites.get(String.format("obj.hitbox.%b", this.isVisible)); }
	public TreeElementRenderable<?> getRenderable() { return this.renderable; }
	public TreeElement clone() {
		ElementHitbox element = new ElementHitbox(this.name + ".clone");
		element.isVisible = this.isVisible;
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
		hitbox.type = this.type;
		hitbox.flags = this.flags;
		hitbox.position = new Vector3(this.position);
		hitbox.rotation = new Vector3(this.rotation);
		hitbox.scale = new Vector3(this.scale);
		return hitbox;
	}
}
