package by.fxg.pilesos.bullet;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class PhysMotionState extends btMotionState {
	private final Matrix4 transform;
	
	public PhysMotionState(Matrix4 matrix) {
		this.transform = matrix;
	}
	
	public Matrix4 getPrimaryTransform() {
		return this.transform;
	}
	
	public void getWorldTransform(final Matrix4 worldTrans) {
		worldTrans.set(this.transform);
	}
	
	public void setWorldTransform(final Matrix4 worldTrans) {
		this.transform.set(worldTrans);
	}
}
