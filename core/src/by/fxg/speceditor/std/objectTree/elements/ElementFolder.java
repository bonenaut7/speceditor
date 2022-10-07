package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.std.objectTree.ElementStack;
import by.fxg.speceditor.std.objectTree.ITreeElementFolder;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import by.fxg.speceditor.utils.Utils;

public class ElementFolder extends TreeElement implements ITreeElementFolder {
	protected boolean isFolderOpened = false;
	protected ElementStack folderStack;
	
	public ElementFolder() { this("New folder"); }
	public ElementFolder(String name) {
		this.displayName = name;
		this.folderStack = new ElementStack(this);
	}
	
	public void addDropdownItems(SpecObjectTree tree, Array<UDAElement> items, boolean allSameType) {
		super.addDropdownItems(tree, items, allSameType);
		
		if (tree.elementSelector.size() == 1) {
			UDAElement add = new UDAElement("folder.add", "Add");
			add.addElement(new UDAElement("folder.add.folder", "Folder"));
			add.addElement(new UDAElement("folder.add.model", "Model"));
			add.addElement(new UDAElement("folder.add.light", "Light"));
			items.add(new UDAElement(), add);
		}
	}
	
	/** Used after using one of dropdown items, return true to close dropdown **/
	public boolean processDropdownAction(SpecObjectTree tree, String itemID) {
		switch (itemID) {
			case "folder.add.folder": this.folderStack.add(new ElementFolder("New folder")); return true;
			case "folder.add.model": this.folderStack.add(new ElementModel("New model")); return true;
			case "folder.add.light": this.folderStack.add(new ElementLight("New light")); return true;
			
			default: return super.processDropdownAction(tree, itemID);
		}
	}
	
	public Sprite getObjectTreeSprite() {
		return Game.storage.sprites.get(Utils.format("icons/folder.", this.isFolderOpened));
	}
	
	public boolean isFolderAccepting(TreeElement element) {
		return true;
	}

	public boolean isFolderOpened() {
		return this.isFolderOpened;
	}

	public void setFolderOpened(boolean isFolderOpened) {
		this.isFolderOpened = isFolderOpened;
	}

	public ElementStack getFolderStack() {
		return this.folderStack;
	}
}
