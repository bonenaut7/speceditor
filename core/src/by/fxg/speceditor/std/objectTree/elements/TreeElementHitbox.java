package by.fxg.speceditor.std.objectTree.elements;

import by.fxg.speceditor.std.objectTree.TreeElement;

public abstract class TreeElementHitbox extends TreeElement {
	public long specFlags;
	public int btCollisionFlags;
	public int btActivationState;
	public int btFilterMask;
	public int btFilterGroup;
	public boolean[] linkToParent = new boolean[5];
	
	public void setParent(TreeElement parent) {
		super.setParent(parent);
		if (!(parent instanceof ElementHitboxStack)) {
			for (int i = 0; i != this.linkToParent.length; i++) {
				this.linkToParent[i] = false;
			}
		}
	}
}
