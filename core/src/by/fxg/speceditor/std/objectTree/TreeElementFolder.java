package by.fxg.speceditor.std.objectTree;

public abstract class TreeElementFolder extends TreeElement {
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
