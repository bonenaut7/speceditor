package by.fxg.speceditor.TO_REMOVE;

import java.util.UUID;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.screen.gui.GuiObjectTreeDelete;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.objecttree.ElementStack;
import by.fxg.speceditor.std.objecttree.SpecObjectTree;
import by.fxg.speceditor.std.objecttree.TreeElementRenderable;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public abstract class __TreeElement {
	protected static Vector3 localTempVector = new Vector3();
	protected long lastClickTime = 0;
	protected __TreeElement parent = null;
	public final UUID uuid = UUID.randomUUID();
	
	protected String name = "unspecified";
	protected boolean isVisible = true, isOpened = true;
	
	public String getName() { return this.name; }
	public __TreeElement setName(String name) { this.name = name; return this; }
	public boolean isVisible() { return this.isVisible; }
	public __TreeElement setVisible(boolean visible) { this.isVisible = visible; return this; }
	
	/* For case if anybody using viewport, leaving as it is for now*/
	public boolean isTransformable(GizmoTransformType transformType) { return false; }
	public Vector3 getTransform(GizmoTransformType transformType) { return localTempVector.set(0, 0, 0); }
	public Vector3 getOffsetTransform(GizmoTransformType transformType) { return localTempVector.set(0, 0, 0); }
	
	/** 20x20 Sprite icon in ObjectTree **/ abstract public Sprite getSprite();
	/** TODO FIXME REMOVE THIS FUCKING DUMBASS SHIT SYSTEM **/ abstract public TreeElementRenderable<?> getRenderable();
	abstract public __TreeElement clone();
	
	/** Calls when mouse over object **/
	/** Calls when user interacted with element **/ abstract public void onInteract(SpecObjectTree objectTree, boolean hold, boolean icon); //TODO hold what? icon what?
	/** Calls when elements removes from ObjectTree **/ public void onDelete() {}
	
	/** Calls when dropdown asking for elements (pre-open) **/
	abstract public void addDropdownParameters(SpecObjectTree objectTree, Array<__TreeElement> selected, Array<UDAElement> array);
	public void addDefaultDropdownParameters(SpecObjectTree objectTree, Array<__TreeElement> selected, Array<UDAElement> array) {
		int cloneType = 0;
		for (__TreeElement element : selected) {
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
		__TreeElement parent = selected.first().parent;
		if (parent != null && parent.hasStack()) {
			for (__TreeElement element : selected) {
				if (element.parent != parent) return;
			}
			array.add(new UDAElement());
			if (parent.parent != null) array.add(new UDAElement("default.move.up", "Move up"));
			UDAElement moveDownTo = new UDAElement("default.movedown", "Move down to");
			for (__TreeElement element : parent.getStack().getElements()) { 
				if (element.hasStack() && !selected.contains(element, true)) {
					boolean accept = true;
					for (__TreeElement selected$ : selected) {
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
	
	abstract public void processDropdown(SpecObjectTree pmoe, Array<__TreeElement> selected, String key);
	public void processDefaultDropdown(SpecObjectTree pmoe, Array<__TreeElement> selected, String key) {
		switch (key) {
			case "default.rename": /*open rename gui with array of items and pmo reference*/ return; //TODO
			case "default.clone": {
				for (__TreeElement element : selected) {
					if (element.parent != null && element.parent.hasStack()) element.parent.getStack().add(element.clone());
					else pmoe.elementStack.add(element.clone());
				}
			} return;
			case "default.delete": Game.get.renderer.currentGui = new GuiObjectTreeDelete(pmoe, pmoe.selectedItems); return;
			case "default.move.up": {
				__TreeElement grandparent = selected.first().parent != null ? selected.first().parent.parent : null;
				if (grandparent != null) {
					for (__TreeElement selected$ : selected) {
						selected$.parent.getStack().selfRemove(selected$);
						grandparent.getStack().add(selected$);
					}
				}
			} return;
		}
		if (key.startsWith("default.move.down.")) {
			__TreeElement child = selected.first().parent != null ? selected.first().parent.getStack().findHere(UUID.fromString(key.substring("default.move.down.".length()))) : null;
			if (child != null && child.hasStack()) {
				for (__TreeElement selected$ : selected) {
					selected$.parent.getStack().selfRemove(selected$);
					child.getStack().add(selected$);
				}
			}
		}
	}
	
	// element stack
	public boolean isStackOpened() { return this.isOpened; }
	public __TreeElement setOpened(boolean opened) { this.isOpened = opened; return this; }
	public boolean hasStack() { return false; }		// allows to drop down and store other elements
	public boolean stackAccepting(__TreeElement element) { return false; }
	public ElementStack getStack() { return null; }
}
