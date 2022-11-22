package by.fxg.pilesos.bullet;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class PhysMotionState extends btMotionState {
	protected Matrix4 transform;
	
	public PhysMotionState(Matrix4 transform) {
		this.transform = transform;
	}
	
	public Matrix4 getTransform() {
		return this.transform;
	}
	
	public void getWorldTransform(final Matrix4 worldTrans) {
		worldTrans.set(this.transform);
	}
	
	public void setWorldTransform(final Matrix4 worldTrans) {
		this.transform.set(worldTrans);
	}
}
