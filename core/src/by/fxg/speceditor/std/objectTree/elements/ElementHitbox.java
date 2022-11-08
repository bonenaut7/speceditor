package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.math.Vector3;

import by.fxg.speceditor.render.DebugDraw3D;
import by.fxg.speceditor.render.DebugDraw3D.IDebugDraw;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;

public class ElementHitbox extends TreeElementHitbox implements ITreeElementGizmos, IDebugDraw {
	private Vector3 position = new Vector3();
	private Vector3 rotation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	
	public ElementHitbox() { this("New hitbox"); }
	public ElementHitbox(String name) {
		this.displayName = name;
	}
	
	public void draw(SpecObjectTree objectTree, DebugDraw3D draw) {
		boolean isSelected = objectTree.elementSelector.isElementSelected(this);
		tmpVectorMin.set(-0.5F, -0.5F, -0.5F);
		tmpVectorMax.set(0.5F, 0.5F, 0.5F);
		if (this.parent instanceof ElementHitboxStack) {
			ElementHitboxStack parent = (ElementHitboxStack)this.parent;
			tmpVectorMin.scl(parent.getTransform(GizmoTransformType.SCALE)).scl(this.scale);
			tmpVectorMax.scl(parent.getTransform(GizmoTransformType.SCALE)).scl(this.scale);
			tmpMatrix.setToTranslation(parent.getTransform(GizmoTransformType.TRANSLATE));
			tmpMatrix.rotate(1F, 0F, 0F, parent.getTransform(GizmoTransformType.ROTATE).x);
			tmpMatrix.rotate(0F, 1F, 0F, parent.getTransform(GizmoTransformType.ROTATE).y);
			tmpMatrix.rotate(0F, 0F, 1F, parent.getTransform(GizmoTransformType.ROTATE).z);
			tmpMatrix.translate(this.position);
			tmpMatrix.rotate(1F, 0F, 0F, this.rotation.x);
			tmpMatrix.rotate(0F, 1F, 0F, this.rotation.y);
			tmpMatrix.rotate(0F, 0F, 1F, this.rotation.z);
			if (!isSelected) isSelected = objectTree.elementSelector.isElementSelected(this.parent);
		} else {
			tmpVectorMin.scl(this.scale);
			tmpVectorMax.scl(this.scale);
			tmpMatrix.setToTranslation(this.position);
			tmpMatrix.rotate(1F, 0F, 0F, this.rotation.x);
			tmpMatrix.rotate(0F, 1F, 0F, this.rotation.y);
			tmpMatrix.rotate(0F, 0F, 1F, this.rotation.z);
		}
		draw.drawer.drawBox(tmpVectorMin, tmpVectorMax, tmpMatrix, isSelected ? UColor.hitboxSelected : UColor.hitbox);
	}
	
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: return this.position;
			case ROTATE: return this.rotation;
			case SCALE: return this.scale;
			default: return Vector3.Zero;
		}
	}
	
	public TreeElement cloneElement() {
		ElementHitbox elementHitbox = new ElementHitbox(this.getName());
		elementHitbox.specFlags = this.specFlags;
		elementHitbox.bulletFlags = this.bulletFlags;
		elementHitbox.bulletFilterMask = this.bulletFilterMask;
		elementHitbox.bulletFilterGroup = this.bulletFilterGroup;
		System.arraycopy(this.linkFlagsToParent, 0, elementHitbox.linkFlagsToParent, 0, this.linkFlagsToParent.length);
		elementHitbox.position.set(this.position);
		elementHitbox.rotation.set(this.rotation);
		elementHitbox.scale.set(this.scale);
		return elementHitbox;
	}
}
