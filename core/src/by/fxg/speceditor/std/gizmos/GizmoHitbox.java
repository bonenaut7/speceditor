package by.fxg.speceditor.std.gizmos;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;

/** Inner bullet physics class for raycasting with mouse clicks **/
class GizmoHitbox {
	Vector3 tmpVector;
	btCollisionShape shape;
	btCollisionObject object;
	int type;
	
	GizmoHitbox(int type) {
		this.type = type;
		this.object = new btCollisionObject();
		this.object.setCollisionShape(this.shape = new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)));
		this.object.setCollisionFlags(CollisionFlags.CF_STATIC_OBJECT | CollisionFlags.CF_NO_CONTACT_RESPONSE);
		this.object.userData = this;
		this.tmpVector = new Vector3();
		
		switch (this.type) {
			case 0: this.shape.setLocalScaling(new Vector3(2.0f, 0.25f, 0.25f)); break;
			case 1: this.shape.setLocalScaling(new Vector3(0.25f, 2.0f, 0.25f)); break;
			case 2: this.shape.setLocalScaling(new Vector3(0.25f, 0.25f, 2.0f)); break;
		}
	}
	
	/** Updates position for raycast hitbox **/
	public void update(Vector3 position, float scale) {
		Matrix4 temp = this.object.getWorldTransform();
		temp.setToTranslation(position);
		switch (this.type) {
			case 0: {
				this.shape.setLocalScaling(this.tmpVector.set(1.5f, 0.25f, 0.25f).scl(Math.max(scale, 0.01F)));
				temp.translate(0.85f * scale, 0f, 0f);
			} break;
			case 1: {
				this.shape.setLocalScaling(this.tmpVector.set(0.25f, 1.5f, 0.25f).scl(Math.max(scale, 0.01F)));
				temp.translate(0f, 0.85f * scale, 0f);
			} break;
			case 2: {
				this.shape.setLocalScaling(this.tmpVector.set(0.25f, 0.25f, 1.5f).scl(Math.max(scale, 0.01F)));
				temp.translate(0f, 0f, 0.85f * scale);
			} break;
		}
		this.object.setWorldTransform(temp);
	}
}
