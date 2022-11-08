package by.fxg.speceditor.std.objectTree.elements;

import by.fxg.speceditor.std.objectTree.ElementStack;
import by.fxg.speceditor.std.objectTree.ITreeElementFolder;
import by.fxg.speceditor.std.objectTree.TreeElement;

public abstract class TreeElementFolder extends TreeElement implements ITreeElementFolder {
	protected boolean isFolderOpened = false;
	protected ElementStack elementStack;
	
	public TreeElementFolder() {
		this.elementStack = new ElementStack();
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
		return this.elementStack;
	}
	
	public void setFolderStack(ElementStack stack) {
		this.elementStack = stack.setParent(this);
		this.elementStack.updateElementsParent();
	}
}
