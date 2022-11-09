package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import by.fxg.speceditor.utils.Utils;

public class ElementFolder extends TreeElementFolder {
	public ElementFolder() { this("New folder"); }
	public ElementFolder(String name) {
		super();
		this.displayName = name;
	}
	
	private ElementFolder(ElementFolder copy) {
		this.displayName = copy.displayName;
		this.visible = copy.visible;
		this.isFolderOpened = copy.isFolderOpened;
		this.elementStack = copy.elementStack.clone(this);
	}
	
	public void addDropdownItems(SpecObjectTree tree, Array<UDAElement> items, boolean allSameType) {
		super.addDropdownItems(tree, items, allSameType);
		
		if (tree.elementSelector.size() == 1) {
			UDAElement add = new UDAElement("folder.add", "Add element");
			add.addElement(new UDAElement("folder.add.folder", "Folder"));
			add.addElement(new UDAElement("folder.add.hitboxstack", "Hitbox Stack"));
			add.addElement(new UDAElement());
			add.addElement(new UDAElement("folder.add.model", "Model"));
			add.addElement(new UDAElement("folder.add.light", "Light"));
			add.addElement(new UDAElement("folder.add.decal", "Decal"));
			add.addElement(new UDAElement("folder.add.hitbox", "Hitbox"));
			add.addElement(new UDAElement("folder.add.hitboxmesh", "Mesh Hitbox"));
			items.add(new UDAElement(), add);
		}
	}
	
	/** Used after using one of dropdown items, return true to close dropdown **/
	public boolean processDropdownAction(SpecObjectTree tree, String itemID) {
		switch (itemID) {
			case "folder.add.folder": this.elementStack.add(new ElementFolder()); return this.isFolderOpened = true;
			case "folder.add.hitboxstack": this.elementStack.add(new ElementHitboxStack()); return this.isFolderOpened = true;
			
			case "folder.add.model": this.elementStack.add(new ElementModel()); return this.isFolderOpened = true;
			case "folder.add.light": this.elementStack.add(new ElementLight()); return this.isFolderOpened = true;
			case "folder.add.decal": this.elementStack.add(new ElementDecal()); return this.isFolderOpened = true;
			case "folder.add.hitbox": this.elementStack.add(new ElementHitbox()); return this.isFolderOpened = true;
			case "folder.add.hitboxmesh": this.elementStack.add(new ElementHitboxMesh()); return this.isFolderOpened = true;
			
			default: return super.processDropdownAction(tree, itemID);
		}
	}
	
	public Sprite getObjectTreeSprite() {
		return DefaultResources.INSTANCE.sprites.get(Utils.format("icons/folder.", this.isFolderOpened));
	}
	
	public TreeElement cloneElement() {
		return new ElementFolder(this);
	}
}
