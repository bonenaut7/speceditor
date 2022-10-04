package by.fxg.speceditor.screen.project.map.viewport;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;

class GizmoHitbox {
	btCollisionShape shape;
	btCollisionObject object;
	int type;
	
	GizmoHitbox(int type) {
		this.type = type;
		this.object = new btCollisionObject();
		this.object.setCollisionShape(this.shape = new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)));
		this.object.setCollisionFlags(CollisionFlags.CF_STATIC_OBJECT | CollisionFlags.CF_NO_CONTACT_RESPONSE);
		this.object.userData = this;
		
		switch (this.type) {
			case 0: this.shape.setLocalScaling(new Vector3(2.75f, 0.25f, 0.25f)); break;
			case 1: this.shape.setLocalScaling(new Vector3(0.25f, 2.75f, 0.25f)); break;
			case 2: this.shape.setLocalScaling(new Vector3(0.25f, 0.25f, 2.75f)); break;
		}
	}
	
	public void updatePosition(Vector3 position) {
		Matrix4 temp = this.object.getWorldTransform();
		temp.setToTranslation(position);
		switch (this.type) {
			case 0: temp.translate(1.45f, 0f, 0f); break;
			case 1: temp.translate(0f, 1.45f, 0f); break;
			case 2: temp.translate(0f, 0f, 1.45f); break;
		}
		this.object.setWorldTransform(temp);
	}
}
