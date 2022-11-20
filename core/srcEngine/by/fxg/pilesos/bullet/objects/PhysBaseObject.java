package by.fxg.pilesos.bullet.objects;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public abstract class PhysBaseObject implements IPhysObject {
	protected String name;
	protected long physFlags;
	protected int filterMask, filterGroup;
	protected btCollisionShape shape;
	
	public String getName() {
		return this.name;
	}

	public btCollisionShape getShape() {
		return this.shape;
	}

	public long getPhysFlags() { return this.physFlags; }
	public PhysBaseObject setPhysFlags(long flags) {
		this.physFlags = flags;
		return this;
	}
	
	public int getFilterMask() { return this.filterMask; }
	public PhysBaseObject setFilterMask(int filterMask) {
		this.filterMask = filterMask;
		return this;
	}
	
	public int getFilterGroup() { return this.filterGroup; }
	public PhysBaseObject setFilterGroup(int filterGroup) {
		this.filterGroup = filterGroup;
		return this;
	}
	
	public void dispose() {
		if (this.shape != null) this.shape.release();
	}
}
