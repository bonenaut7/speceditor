package by.fxg.speceditor.std.objectTree.elements;

import by.fxg.speceditor.std.objectTree.TreeElement;

public abstract class TreeElementHitbox extends TreeElement {
	public long specFlags;
	public long bulletFlags;
	public long bulletFilterMask;
	public long bulletFilterGroup;
	public boolean[] linkFlagsToParent = new boolean[4];
	
	public void setParent(TreeElement parent) {
		super.setParent(parent);
		if (!(parent instanceof ElementHitboxStack)) {
			for (int i = 0; i != this.linkFlagsToParent.length; i++) {
				this.linkFlagsToParent[i] = false;
			}
		}
	}
}
