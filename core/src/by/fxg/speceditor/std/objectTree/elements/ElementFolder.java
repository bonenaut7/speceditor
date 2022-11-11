package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement;
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
	
	public void addDropdownItems(SpecObjectTree tree, Array<STDDropdownAreaElement> elements, boolean allSameType) {
		super.addDropdownItems(tree, elements, allSameType);
		
		if (tree.elementSelector.size() == 1) {
			elements.add(STDDropdownAreaElement.subwindow("Add element")
				.add(STDDropdownAreaElement.button("folder.add.folder", "Folder"))
				.add(STDDropdownAreaElement.button("folder.add.hitboxstack", "Hitbox Stack"))
				.add(STDDropdownAreaElement.line())
				.add(STDDropdownAreaElement.button("folder.add.model", "Model"))
				.add(STDDropdownAreaElement.button("folder.add.light", "Light"))
				.add(STDDropdownAreaElement.button("folder.add.decal", "Decal"))
				.add(STDDropdownAreaElement.button("folder.add.hitbox", "Hitbox"))
				.add(STDDropdownAreaElement.button("folder.add.hitboxmesh", "Mesh Hitbox"))
			);
		}
	}
	
	/** Used after using one of dropdown items, return true to close dropdown **/
	public boolean processDropdownAction(SpecObjectTree tree, STDDropdownAreaElement element, String id) {
		switch (id) {
			case "folder.add.folder": this.elementStack.add(new ElementFolder()); return this.isFolderOpened = true;
			case "folder.add.hitboxstack": this.elementStack.add(new ElementHitboxStack()); return this.isFolderOpened = true;
			
			case "folder.add.model": this.elementStack.add(new ElementModel()); return this.isFolderOpened = true;
			case "folder.add.light": this.elementStack.add(new ElementLight()); return this.isFolderOpened = true;
			case "folder.add.decal": this.elementStack.add(new ElementDecal()); return this.isFolderOpened = true;
			case "folder.add.hitbox": this.elementStack.add(new ElementHitbox()); return this.isFolderOpened = true;
			case "folder.add.hitboxmesh": this.elementStack.add(new ElementHitboxMesh()); return this.isFolderOpened = true;
			
			default: return super.processDropdownAction(tree, element, id);
		}
	}
	
	public Sprite getObjectTreeSprite() {
		return DefaultResources.INSTANCE.sprites.get(Utils.format("icons/folder.", this.isFolderOpened));
	}
	
	public TreeElement cloneElement() {
		return new ElementFolder(this);
	}
}
