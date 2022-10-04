package by.fxg.pilesos.bullet.objects;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

import by.fxg.pilesos.bullet.PhysMotionState;

public abstract class PhysBaseObject implements IPhysObject {
	protected String name;
	protected long flags;
	protected btCollisionShape shape;
	protected PhysMotionState state;
	
	public String getName() {
		return this.name;
	}

	public btCollisionShape getShape() {
		return this.shape;
	}

	public PhysMotionState getState() {
		return this.state;
	}

	public long getFlags() {
		return this.flags;
	}

	public boolean hasFlag(long flag) {
		return (this.flags & flag) == flag;
	}

	public IPhysObject addFlag(long flag) {
		if (!this.hasFlag(flag)) this.flags |= flag;
		return this;
	}

	public IPhysObject removeFlag(long flag) {
		if (this.hasFlag(flag)) this.flags = this.flags & ~flag;
		return this;
	}
	
	public void dispose() {
		if (this.shape != null) this.shape.dispose();
		if (this.state != null) this.state.dispose();
	}
}
