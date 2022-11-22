package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;

import by.fxg.pilesos.bullet.objects.IPhysObject;
import by.fxg.speceditor.std.objectTree.TreeElement;

public abstract class TreeElementHitbox extends TreeElement {
	public long specFlags;
	public int btCollisionFlags = CollisionFlags.CF_STATIC_OBJECT;
	public int btActivationState = IPhysObject.ACTSTATE_DEACTIVATED;
	public int btFilterMask = IPhysObject.FILTER_ALL;
	public int btFilterGroup = IPhysObject.FILTER_btDEFAULT;
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
