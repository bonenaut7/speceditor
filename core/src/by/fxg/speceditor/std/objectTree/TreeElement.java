package by.fxg.speceditor.std.objectTree;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.project.assets.IProjectAssetHandler;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

/** Basic element of {@link by.fxg.speceditor.std.objectTree.SpecObjectTree}. <br>
 *  Contains: <br>
 *  	{@link #uuid} - unique identifier of element <br>
 *  	{@link #parent} - parent element link <br>
 *  	{@link #displayName} - name <br>
 *  	{@link #visible} - visiblity parameter <br>
 *  <br>
 *  Serialization: elements must have implementation of 
 * 	{@link #serialize(IOUtils, DataOutputStream)} & {@link #deserialize(IOUtils, DataInputStream)}
 * 	methods, and empty constructor for creating empty object for deserialization process. **/
public abstract class TreeElement {
	public final UUID uuid = UUID.randomUUID();
	protected TreeElement parent = null;
	protected String displayName = "undefined";
	protected boolean visible = true;

	public void addDropdownItems(SpecObjectTree tree, Array<UDAElement> items, boolean allSameType) {
		int cloneType = tree.elementSelector.get(0) instanceof ITreeElementFolder ? 1 : 2;
		for (int i = 0; i != tree.elementSelector.size(); i++) {
			if (tree.elementSelector.get(i) instanceof ITreeElementFolder && cloneType == 2) { cloneType = -1; break; }
			else if (!(tree.elementSelector.get(i) instanceof ITreeElementFolder) && cloneType == 1) { cloneType = -1; break; }
		}
		if (cloneType > 0) items.add(new UDAElement("default.clone", "Clone"));
		items.add(new UDAElement("default.delete", "Delete"));
	}
	
	/** Used after using one of dropdown items, return true to close dropdown **/
	public boolean processDropdownAction(SpecObjectTree tree, String itemID) {
		switch (itemID) {
			case "default.clone": {
				TreeElement clone = this.cloneElement();
				if (clone != null) {
					if (this.parent instanceof ITreeElementFolder) {
						((ITreeElementFolder)this.parent).getFolderStack().add(clone);
					} else tree.getStack().add(clone);
				}
			} break;
			//default.delete is being processed in the SpecObjectTree#onDropdownClick!
		}
		return true;
	}
	
	/** Called before object deletion from ObjectTree **/
	public void onDelete() {
		if (this instanceof IProjectAssetHandler) {
			throw new IllegalStateException("Project asset handler objects must override onDelete() method and remove self from asset.");
		}
	}
	
	/** Element cloning default implementation. In case of returning null object won't clone **/
	public TreeElement cloneElement() {
		return null;
	}
	
	/** Default {@link ITreeElementGizmos#getOffsetTransform(GizmoTransformType)} implementation **/
	public Vector3 getOffsetTransform(Vector3 inputVector, GizmoTransformType transformType) {
		if (this.parent != null) {
			if (this.parent instanceof ITreeElementGizmos && ((ITreeElementGizmos)this.parent).isTransformSupported(transformType)) {
				switch (transformType) {
					case SCALE: inputVector.scl(((ITreeElementGizmos)this.parent).getTransform(transformType)); break;
					default: inputVector.add(((ITreeElementGizmos)this.parent).getTransform(transformType)); break;
				}
			}
			return this.parent.getOffsetTransform(inputVector, transformType);
		}
		return inputVector;
	}
	
	public Sprite getObjectTreeSprite() { return DefaultResources.INSTANCE.sprites.get("icons/question"); }
	public TreeElement getParent() { return this.parent; }
	public String getName() { return this.displayName; }
	public boolean isVisible() { return this.visible; }
	
	public void setParent(TreeElement parent) { this.parent = parent; }
	public void setParent(TreeElement parent, boolean removeFromOld, boolean addToNew) { this.setParent(null, parent, removeFromOld, addToNew); }
	public void setParent(ElementStack nullStack, TreeElement parent, boolean removeFromOld, boolean addToNew) {
		if (parent instanceof ITreeElementFolder && !((ITreeElementFolder)parent).isFolderAccepting(this)) return;
		if (removeFromOld) {
			if (this.parent instanceof ITreeElementFolder) ((ITreeElementFolder)this.parent).getFolderStack().remove(this);
			else if (nullStack != null) nullStack.remove(this);
		}
		if (addToNew && parent instanceof ITreeElementFolder) ((ITreeElementFolder)parent).getFolderStack().add(this);
		this.setParent(parent);
	}
	public void setName(String displayName) { this.displayName = displayName; }
	public void setVisible(boolean visible) { this.visible = visible; }
}
