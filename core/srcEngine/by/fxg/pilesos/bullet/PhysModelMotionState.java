package by.fxg.pilesos.bullet;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;

public class PhysModelMotionState extends PhysMotionState {
	protected ModelInstance modelInstance;
	
	public PhysModelMotionState(ModelInstance modelInstance) {
		this(new Matrix4(), modelInstance);
	}
	
	public PhysModelMotionState(Matrix4 transform, ModelInstance modelInstance) {
		super(transform);
		modelInstance.transform = this.transform;
		this.modelInstance = modelInstance;
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
