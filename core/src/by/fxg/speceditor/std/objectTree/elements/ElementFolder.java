package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.std.objectTree.ElementStack;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElementFolder;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import by.fxg.speceditor.utils.Utils;

public class ElementFolder extends TreeElementFolder {
	protected boolean isFolderOpened = false;
	protected ElementStack folderStack;
	
	public ElementFolder() { this("New folder"); }
	public ElementFolder(String name) {
		super();
		this.displayName = name;
	}
	
	public void addDropdownItems(SpecObjectTree tree, Array<UDAElement> items, boolean allSameType) {
		super.addDropdownItems(tree, items, allSameType);
		
		if (tree.elementSelector.size() == 1) {
			UDAElement add = new UDAElement("folder.add", "Add");
			add.addElement(new UDAElement("folder.add.folder", "Folder"));
			add.addElement(new UDAElement("folder.add.hitboxstack", "Hitbox Stack"));
			add.addElement(new UDAElement());
			add.addElement(new UDAElement("folder.add.model", "Model"));
			add.addElement(new UDAElement("folder.add.light", "Light"));
			add.addElement(new UDAElement("folder.add.decal", "Decal"));
			add.addElement(new UDAElement("folder.add.hitbox", "Hitbox"));
			items.add(new UDAElement(), add);
		}
	}
	
	/** Used after using one of dropdown items, return true to close dropdown **/
	public boolean processDropdownAction(SpecObjectTree tree, String itemID) {
		switch (itemID) {
			case "folder.add.folder": this.folderStack.add(new ElementFolder()); return this.isFolderOpened = true;
			case "folder.add.hitboxstack": this.folderStack.add(new ElementHitboxStack()); return this.isFolderOpened = true;
			
			case "folder.add.model": this.folderStack.add(new ElementModel()); return this.isFolderOpened = true;
			case "folder.add.light": this.folderStack.add(new ElementLight()); return this.isFolderOpened = true;
			case "folder.add.decal": this.folderStack.add(new ElementDecal()); return this.isFolderOpened = true;
			case "folder.add.hitbox": this.folderStack.add(new ElementHitbox()); return this.isFolderOpened = true;
			
			default: return super.processDropdownAction(tree, itemID);
		}
	}
	
	public Sprite getObjectTreeSprite() {
		return DefaultResources.INSTANCE.sprites.get(Utils.format("icons/folder.", this.isFolderOpened));
	}
}
