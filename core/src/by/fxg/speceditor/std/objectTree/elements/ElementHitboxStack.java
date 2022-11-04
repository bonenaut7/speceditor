package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.render.DebugDraw3D;
import by.fxg.speceditor.render.DebugDraw3D.IDebugDraw;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.ElementStack;
import by.fxg.speceditor.std.objectTree.ITreeElementFolder;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import by.fxg.speceditor.utils.Utils;

public class ElementHitboxStack extends TreeElement implements ITreeElementFolder, ITreeElementGizmos, IDebugDraw {
	protected boolean isFolderOpened = false;
	protected ElementStack folderStack;
	
	public long flags;
	public boolean isArrayStack = false; //means will be objects split after export or be combined, false = combine, true = keep as split objects
	private Vector3 position = new Vector3();
	private Vector3 rotation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	
	public ElementHitboxStack() { this("New hitbox stack"); }
	public ElementHitboxStack(String name) {
		this.displayName = name;
		this.folderStack = new ElementStack(this);
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
			case "hitboxstack.add.hitboxstack": this.folderStack.add(new ElementHitboxStack()); return true;
			case "hitboxstack.add.hitbox": this.folderStack.add(new ElementHitbox()); return true;
			case "hitboxstack.add.hitboxmesh": this.folderStack.add(new ElementHitboxMesh()); return true;
			
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
	
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: return this.position;
			case ROTATE: return this.rotation;
			case SCALE: return this.scale;
			default: return gizmoVector.set(0, 0, 0);
		}
	}
	
	public Sprite getObjectTreeSprite() {
		return DefaultResources.INSTANCE.sprites.get(Utils.format("icons/question"));
	}

	public boolean isTransformSupported(GizmoTransformType transformType) {
		return true;
	}
	
	public boolean isFolderAccepting(TreeElement element) { return element instanceof ElementHitbox || element instanceof ElementHitboxStack || element instanceof ElementHitboxMesh; }
	public boolean isFolderOpened() { return this.isFolderOpened; }
	public void setFolderOpened(boolean isFolderOpened) { this.isFolderOpened = isFolderOpened; }
	public ElementStack getFolderStack() { return this.folderStack; }
	public void setFolderStack(ElementStack stack) { this.folderStack = stack; }
}
