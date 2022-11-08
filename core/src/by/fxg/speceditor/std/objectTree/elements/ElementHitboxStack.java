package by.fxg.speceditor.std.objectTree.elements;

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
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

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
		this.elementStack = new ElementStack();
	}
	
	public void addDropdownItems(SpecObjectTree tree, Array<UDAElement> items, boolean allSameType) {
		super.addDropdownItems(tree, items, allSameType);
		
		if (tree.elementSelector.size() == 1) {
			UDAElement add = new UDAElement("hitboxstack.add", "Add");
			add.addElement(new UDAElement("hitboxstack.add.hitboxstack", "Hitbox Stack"));
			add.addElement(new UDAElement("hitboxstack.add.hitbox", "Hitbox"));
			add.addElement(new UDAElement("hitboxstack.add.hitboxmesh", "Mesh Hitbox"));
			items.add(new UDAElement(), add);
		}
	}
	
	/** Used after using one of dropdown items, return true to close dropdown **/
	public boolean processDropdownAction(SpecObjectTree tree, String itemID) {
		switch (itemID) {
			case "hitboxstack.add.hitboxstack": this.elementStack.add(new ElementHitboxStack()); return this.isFolderOpened = true;
			case "hitboxstack.add.hitbox": this.elementStack.add(new ElementHitbox()); return this.isFolderOpened = true;
			case "hitboxstack.add.hitboxmesh": this.elementStack.add(new ElementHitboxMesh()); return this.isFolderOpened = true;
			
			default: return super.processDropdownAction(tree, itemID);
		}
	}
	
	public void draw(SpecObjectTree objectTree, DebugDraw3D draw) {
		tmpMatrix.setToTranslation(this.position);
		tmpMatrix.scale(this.scale.x, this.scale.y, this.scale.z);
		tmpMatrix.rotate(1F, 0F, 0F, this.rotation.x);
		tmpMatrix.rotate(0F, 1F, 0F, this.rotation.y);
		tmpMatrix.rotate(0F, 0F, 1F, this.rotation.z);
		draw.drawer.drawTransform(tmpMatrix, 0.5f);
	}
	
	//Temporary remove gizmos, check todo's for fixing HitboxStack
	public boolean isTransformSupported(GizmoTransformType transformType) { return false; }
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
}
