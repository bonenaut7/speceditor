package by.fxg.speceditor.hc.elementlist;

import java.util.UUID;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.screen.gui.GuiDeletePMOE;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public abstract class TreeElement {
	protected static Vector3 localTempVector = new Vector3();
	protected long lastClickTime = 0;
	protected TreeElement parent = null;
	public final UUID uuid = UUID.randomUUID();
	
	protected String name = "unspecified";
	protected boolean isVisible = true, isOpened = true;
	
	public String getName() { return this.name; }
	public TreeElement setName(String name) { this.name = name; return this; }
	public boolean isVisible() { return this.isVisible; }
	public TreeElement setVisible(boolean visible) { this.isVisible = visible; return this; }
	public boolean isTransformable(EnumTransform transformType) { return false; }
	public Vector3 getTransform(EnumTransform transformType) { return localTempVector.set(0, 0, 0); }
	public Vector3 getOffsetTransform(EnumTransform transformType) { return localTempVector.set(0, 0, 0); }
	
	abstract public Sprite getSprite();
	abstract public TreeElementRenderable<?> getRenderable();
	abstract public TreeElement clone();
	
	/** calling when mouse over object **/
	abstract public void onInteract(PMObjectExplorer list, boolean hold, boolean icon);
	public void onDelete() {}
	
	/** calling when dropdown asking for elements (pre-open) **/
	abstract public void addDropdownParameters(PMObjectExplorer pmoe, Array<TreeElement> selected, Array<UDAElement> array);
	public void addDefaultDropdownParameters(PMObjectExplorer pmoe, Array<TreeElement> selected, Array<UDAElement> array) {
		int cloneType = 0;
		for (TreeElement element : selected) {
			if (cloneType == 0) {
				if (element.hasStack()) cloneType = 1;
				else cloneType = 2;
			}
			if (element.hasStack() && cloneType == 2) { cloneType = -1; break; }
			else if (!element.hasStack() && cloneType == 1) { cloneType = -1; break; }
		}
		
		//array.add(new UDAElement("default.rename", "Rename")); deprecated epta
		if (cloneType > 0) {
			array.add(new UDAElement("default.clone", "Clone"));
		}
		array.add(new UDAElement("default.delete", "Delete"));
		
		//checking for the moveup/down buttons
		TreeElement parent = selected.first().parent;
		if (parent != null && parent.hasStack()) {
			for (TreeElement element : selected) {
				if (element.parent != parent) return;
			}
			array.add(new UDAElement());
			if (parent.parent != null) array.add(new UDAElement("default.move.up", "Move up"));
			UDAElement moveDownTo = new UDAElement("default.movedown", "Move down to");
			for (TreeElement element : parent.getStack().getItems()) { 
				if (element.hasStack() && !selected.contains(element, true)) {
					boolean accept = true;
					for (TreeElement selected$ : selected) {
						if (!element.stackAccepting(selected$)) {
							accept = false;
							break;
						}
					}
					if (accept) moveDownTo.addElement(new UDAElement("default.move.down." + element.uuid.toString(), element.name));
				}
			}
			if (moveDownTo.size() > 0) array.add(moveDownTo);
		}
	}
	
	abstract public void processDropdown(PMObjectExplorer pmoe, Array<TreeElement> selected, String key);
	public void processDefaultDropdown(PMObjectExplorer pmoe, Array<TreeElement> selected, String key) {
		switch (key) {
			case "default.rename": /*open rename gui with array of items and pmo reference*/ return; //TODO
			case "default.clone": {
				for (TreeElement element : selected) {
					if (element.parent != null && element.parent.hasStack()) element.parent.getStack().add(element.clone());
					else pmoe.elementStack.add(element.clone());
				}
			} return;
			case "default.delete": Game.get.renderer.currentGui = new GuiDeletePMOE(pmoe, pmoe.selectedItems); return;
			case "default.move.up": {
				TreeElement grandparent = selected.first().parent != null ? selected.first().parent.parent : null;
				if (grandparent != null) {
					for (TreeElement selected$ : selected) {
						selected$.parent.getStack().remove(selected$);
						grandparent.getStack().add(selected$);
					}
				}
			} return;
		}
		if (key.startsWith("default.move.down.")) {
			TreeElement child = selected.first().parent != null ? selected.first().parent.getStack().find(UUID.fromString(key.substring("default.move.down.".length()))) : null;
			if (child != null && child.hasStack()) {
				for (TreeElement selected$ : selected) {
					selected$.parent.getStack().remove(selected$);
					child.getStack().add(selected$);
				}
			}
		}
	}
	
	// element stack
	public TreeElement getParentElement() { return this.parent; }
	public boolean isStackOpened() { return this.isOpened; }
	public TreeElement setOpened(boolean opened) { this.isOpened = opened; return this; }
	public boolean hasStack() { return false; }		// allows to drop down and store other elements
	public boolean stackAccepting(TreeElement element) { return false; }
	public ElementStack getStack() { return null; }
}
