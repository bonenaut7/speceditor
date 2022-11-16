package by.fxg.speceditor.std.objectTree.elements;

import java.util.Arrays;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.render.DebugDraw3D;
import by.fxg.speceditor.render.DebugDraw3D.IDebugDraw;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.ElementStack;
import by.fxg.speceditor.std.objectTree.ITreeElementFolder;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement;

public class ElementHitboxStack extends TreeElementHitbox implements ITreeElementFolder, ITreeElementGizmos, IDebugDraw {
	private boolean isFolderOpened = false;
	private ElementStack elementStack;
	
	public boolean isArrayStack = true; //means will be objects split after export or be combined, false = combine, true = keep as split objects
	private Vector3 position = new Vector3();
	private Vector3 rotation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	
	public ElementHitboxStack() { this("New hitbox stack"); }
	public ElementHitboxStack(String name) {
		this.displayName = name;
		this.elementStack = new ElementStack().setParent(this);
	}
	
	private ElementHitboxStack(ElementHitboxStack copy) {
		this.displayName = copy.displayName;
		this.visible = copy.visible;
		this.specFlags = copy.specFlags;
		this.btCollisionFlags = copy.btCollisionFlags;
		this.btFilterMask = copy.btFilterMask;
		this.btFilterGroup = copy.btFilterGroup;
		this.linkToParent = Arrays.copyOf(copy.linkToParent, copy.linkToParent.length);
		this.isFolderOpened = copy.isFolderOpened;
		this.elementStack = copy.elementStack.clone(this);
		this.isArrayStack = copy.isArrayStack;
		this.position.set(copy.position);
		this.rotation.set(copy.rotation);
		this.scale.set(copy.scale);
	}
	
	public void addDropdownItems(SpecObjectTree tree, Array<STDDropdownAreaElement> elements, boolean allSameType) {
		super.addDropdownItems(tree, elements, allSameType);
		
		if (tree.elementSelector.size() == 1) {
			elements.add(STDDropdownAreaElement.subwindow("Add element")
				.add(STDDropdownAreaElement.button("hitboxstack.add.hitboxstack", "Hitbox Stack"))
				.add(STDDropdownAreaElement.button("hitboxstack.add.hitbox", "Hitbox"))
				.add(STDDropdownAreaElement.button("hitboxstack.add.hitboxmesh", "Mesh Hitbox"))
			);
		}
	}
	
	/** Used after using one of dropdown items, return true to close dropdown **/
	public boolean processDropdownAction(SpecObjectTree tree, STDDropdownAreaElement element, String id) {
		switch (id) {
			case "hitboxstack.add.hitboxstack": this.elementStack.add(new ElementHitboxStack()); return this.isFolderOpened = true;
			case "hitboxstack.add.hitbox": this.elementStack.add(new ElementHitbox()); return this.isFolderOpened = true;
			case "hitboxstack.add.hitboxmesh": this.elementStack.add(new ElementHitboxMesh()); return this.isFolderOpened = true;
			
			default: return super.processDropdownAction(tree, element, id);
		}
	}
	
	public void draw(SpecObjectTree objectTree, DebugDraw3D draw) {
		tmpMatrix.setToTranslation(this.getOffsetTransform(tmpVector.setZero(), GizmoTransformType.TRANSLATE).add(this.position));
		this.getOffsetTransform(tmpVector.setZero(), GizmoTransformType.ROTATE).add(this.rotation);
		tmpMatrix.rotate(1, 0, 0, tmpVector.x).rotate(0, 1, 0, tmpVector.y).rotate(0, 1, 1, tmpVector.z);
		this.getOffsetTransform(tmpVector.set(1, 1, 1), GizmoTransformType.SCALE).scl(this.scale);
		tmpMatrix.scale(tmpVector.x, tmpVector.y, tmpVector.z);
		draw.drawer.drawTransform(tmpMatrix, 0.5f);
	}
	
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: return this.position;
			case ROTATE: return this.rotation;
			case SCALE: return this.scale;
			default: return Vector3.Zero;
		}
	}
	
	public boolean isFolderAccepting(TreeElement element) { return element instanceof ElementHitbox || element instanceof ElementHitboxStack || element instanceof ElementHitboxMesh; }
	public boolean isFolderOpened() { return this.isFolderOpened; }
	public void setFolderOpened(boolean isFolderOpened) { this.isFolderOpened = isFolderOpened; }
	public ElementStack getFolderStack() { return this.elementStack; }
	public void setFolderStack(ElementStack stack) {
		this.elementStack = stack.setParent(this);
		this.elementStack.updateElementsParent();
	}
	
	public TreeElement cloneElement() {
		return new ElementHitboxStack(this);
	}
}
