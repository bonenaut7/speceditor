package by.fxg.speceditor.TO_REMOVE;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.specformat.graph.SpecHitbox;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.objecttree.ElementStack;
import by.fxg.speceditor.std.objecttree.SpecObjectTree;
import by.fxg.speceditor.std.render.DebugDraw3D;
import by.fxg.speceditor.std.render.DebugDraw3D.IDebugDraw;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public class ElementMultiHitbox extends __TreeElement implements IDebugDraw, IConvertable<SpecHitbox> {
	private static Matrix4 tmpMatrix = new Matrix4();
	
	private TreeElementRenderable<ElementMultiHitbox> renderable;
	private ElementStack elementStack;
	
	public long flags;
	private Vector3 position = new Vector3();
	private Vector3 rotation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	
	public ElementMultiHitbox() { this("New multi hitbox"); }
	public ElementMultiHitbox(String name) {
		this.name = name;
		this.elementStack = new ElementStack(this);
		this.renderable = new TERMultiHitbox(this);
	}

	public void onInteract(SpecObjectTree list, boolean hold, boolean iconTouch) {
		if (!hold) {
			if (iconTouch) {
				if (Game.get.getTick() - this.lastClickTime < 25L) {
					this.lastClickTime = 0;
					this.isVisible = !this.isVisible;
				} else this.lastClickTime = Game.get.getTick();
			} else {
				if (Game.get.getTick() - this.lastClickTime < 25L) {
					this.lastClickTime = 0;
					this.isOpened = !this.isOpened;
					list.deselectElement(this);
				} else {
					if (Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) && list.isElementSelected(this)) list.deselectElement(this);
					else list.selectElement(this);
					this.lastClickTime = Game.get.getTick();
				}
			}
		}
	}
	
	public void addDropdownParameters(SpecObjectTree pmoe, Array<__TreeElement> selected, Array<UDAElement> array) {
		if (selected.size == 1) {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide" : "Show"));
			array.add(new UDAElement(this.isOpened ? "basic.collapse" : "basic.open", this.isVisible ? "Collapse" : "Open"));	
			super.addDefaultDropdownParameters(pmoe, selected, array);
			
			array.add(new UDAElement());
			array.add(new UDAElement("multihitbox.add.hitbox", "Create hitbox"));
			array.add(new UDAElement("multihitbox.add.meshhitbox", "Create mesh hitbox"));
		} else {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide all" : "Show all"));
			array.add(new UDAElement(this.isOpened ? "basic.collapse" : "basic.open", this.isVisible ? "Collapse all" : "Open all"));
		}
	}
	
	public void processDropdown(SpecObjectTree pmoe, Array<__TreeElement> selected, String key) {
		switch (key) {
			case "basic.hide": {
				for (__TreeElement element : selected) element.setVisible(false);
			} return;
			case "basic.show": {
				for (__TreeElement element : selected) element.setVisible(true);
			} return;
			case "basic.collapse": {
				for (__TreeElement element : selected) element.setOpened(false);
			} return;
			case "basic.open": {
				for (__TreeElement element : selected) element.setOpened(true);
			} return;
			
			case "multihitbox.add.hitbox": { ElementHitbox hitbox = new ElementHitbox(); hitbox.parent = this; this.elementStack.add(hitbox); } return;
			case "multihitbox.add.meshhitbox": { ElementMeshHitbox hitbox = new ElementMeshHitbox(); hitbox.parent = this; this.elementStack.add(hitbox); } return;
		}
	}
	
	public void draw(SpecObjectTree pmoe, DebugDraw3D draw) {
		tmpMatrix.setToTranslation(this.position);
		tmpMatrix.scale(this.scale.x, this.scale.y, this.scale.z);
		tmpMatrix.rotate(1F, 0F, 0F, this.rotation.x);
		tmpMatrix.rotate(0F, 1F, 0F, this.rotation.y);
		tmpMatrix.rotate(0F, 0F, 1F, this.rotation.z);
		draw.drawer.drawTransform(tmpMatrix, 0.5f);
	}
	
	//add offsetTransformVector, + add it to model with position*scale
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: return this.position;
			case ROTATE: return this.rotation;
			case SCALE: return this.scale;
			default: return localTempVector.set(0, 0, 0);
		}
	}
	
	public boolean isTransformable(GizmoTransformType transformType) { return true; }
	public Sprite getSprite() { return Game.storage.sprites.get(String.format("obj.hitboxstorage.%b", this.isVisible)); }
	public TreeElementRenderable<?> getRenderable() { return this.renderable; }
	public boolean hasStack() { return true; }
	public boolean stackAccepting(__TreeElement element) { return element instanceof ElementHitbox || element instanceof ElementMeshHitbox; }
	public ElementStack getStack() { return this.elementStack; }
	
	public __TreeElement clone() {
		ElementMultiHitbox element = new ElementMultiHitbox(this.name + ".clone");
		element.isVisible = this.isVisible;
		element.isOpened = this.isOpened;
		element.flags = this.flags;
		element.position.set(this.position);
		element.rotation.set(this.rotation);
		element.scale.set(this.scale);
		for (__TreeElement element$ : this.elementStack.getElements()) {
			if (element$ instanceof ElementHitbox) {
				ElementHitbox hitbox = (ElementHitbox)element$.clone();
				hitbox.parent = element;
				element.elementStack.add(hitbox);
			} else if (element$ instanceof ElementMeshHitbox) {
				ElementMeshHitbox meshHitbox = (ElementMeshHitbox)element$.clone();
				meshHitbox.parent = element;
				element.elementStack.add(meshHitbox);
			}
		}
		return element;
	}

	public SpecHitbox convert() {
		SpecHitbox hitbox = new SpecHitbox();
		hitbox.name = this.name;
		hitbox.type = -2;
		hitbox.flags = this.flags;
		hitbox.position = new Vector3(this.position);
		hitbox.rotation = new Vector3(this.rotation);
		hitbox.scale = new Vector3(this.scale);
		Array<SpecHitbox> children = new Array<>();
		for (__TreeElement element : this.elementStack.getElements()) {
			if (element instanceof ElementHitbox) children.add(((ElementHitbox)element).convert());
			if (element instanceof ElementMeshHitbox) children.add(((ElementMeshHitbox)element).convert());
		}
		hitbox.children = children.toArray(SpecHitbox.class);
		return hitbox;
	}
}
