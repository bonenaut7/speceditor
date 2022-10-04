package by.fxg.pilesos.bullet.objects;

import java.util.UUID;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;

import by.fxg.pilesos.bullet.PhysMotionState;

public class PhysRigidBody extends PhysBaseObject {
	protected btRigidBody body;
	protected btRigidBodyConstructionInfo info;
	
	private PhysRigidBody() {}
	
	/** Creates PhysRigidBody, applies own userData to object **/
	public PhysRigidBody(String name, btRigidBody body) {
		this.state = new PhysMotionState(new Matrix4());
		this.name = name;
		this.body = body;
		this.body.setMotionState(this.state);
		this.body.userData = this;
		this.shape = body.getCollisionShape();
	}
	
	/** Creates PhysRigidBody with provided info **/
	public PhysRigidBody(String name, btRigidBodyConstructionInfo info, int bulletFlags, long flags) {
		this.state = new PhysMotionState(new Matrix4());
		this.body = new btRigidBody(info);
		this.body.setMotionState(this.state);
		this.body.userData = this;
		this.shape = this.body.getCollisionShape();
		this.name = name;
		this.flags = flags;
	}
	
	public btRigidBody getBody() {
		return this.body;
	}
	
	public btCollisionObject getObject() {
		return this.body;
	}
	
	public void dispose() {
		if (this.body != null) this.body.dispose();
		if (this.info != null) this.info.dispose();
		super.dispose();
	}

	//TODO REORGANIZE, MAKE OBJECT CREATION IN THE BUILD METHOD
	public static class Builder {
		private PhysRigidBody physRigidBody;
		
		public Builder(String name) {
			this.physRigidBody = new PhysRigidBody();
			this.physRigidBody.state = new PhysMotionState(new Matrix4());
			this.physRigidBody.info = new btRigidBodyConstructionInfo(1f, this.physRigidBody.state, null);
			this.physRigidBody.name = (name == null ? UUID.randomUUID().toString() : name);
			this.physRigidBody.body = new btRigidBody(this.physRigidBody.info);
			this.physRigidBody.body.userData = this.physRigidBody;
		}
		
		/** Disposes all old objects, creates new RigidBody **/
		public Builder setConstructionInfo(btRigidBodyConstructionInfo info) {
			if (info != null) {
				if (this.physRigidBody.info != null) this.physRigidBody.info.dispose();
				if (this.physRigidBody.body != null) this.physRigidBody.body.dispose();
				if (this.physRigidBody.shape != null) this.physRigidBody.shape.dispose();
				
				this.physRigidBody.info = info;
				this.physRigidBody.body = new btRigidBody(info);
				this.physRigidBody.body.setMotionState(this.physRigidBody.state);
				this.physRigidBody.body.userData = this.physRigidBody;
				this.physRigidBody.shape = info.getCollisionShape();
			}
			return this;
		}
		
		public Builder setShapeAABB(Vector3 scale) {
			this.setShape(new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)));
			if (scale != null) this.physRigidBody.shape.setLocalScaling(scale);
			return this;
		}
		
		public Builder setShapeSphere(Vector3 scale) {
			this.setShape(new btSphereShape(0.5f));
			if (scale != null) this.physRigidBody.shape.setLocalScaling(scale);
			return this;
		}
		
		public Builder setShapeCapsule(Vector3 scale) {
			this.setShape(new btCapsuleShape(0.5f, 1f));
			if (scale != null) this.physRigidBody.shape.setLocalScaling(scale);
			return this;
		}
		
		public Builder setShape(btCollisionShape shape) {
			if (shape != null) {
				if (this.physRigidBody.shape != null && !this.physRigidBody.shape.isDisposed()) {
					this.physRigidBody.shape.dispose();
				}
				this.physRigidBody.shape = shape;
				this.physRigidBody.body.setCollisionShape(shape);
			}
			return this;
		}
		
		public Builder setShapeSize(Vector3 size) {
			if (size != null && this.physRigidBody.shape != null) {
				this.physRigidBody.shape.setLocalScaling(size);
			}
			return this;
		}
		
		public Builder setCollisionFlags(int bulletFlags) {
			this.physRigidBody.body.setCollisionFlags(bulletFlags);
			return this;
		}
		
		public Builder setObjectFlags(long flags) {
			this.physRigidBody.flags = flags;
			return this;
		}
		
		public Builder addFlag(long flag) {
			this.physRigidBody.addFlag(flag);
			return this;
		}
		
		public Builder removeFlag(long flag) {
			this.physRigidBody.removeFlag(flag);
			return this;
		}
		
		public Builder setPosition(float x, float y, float z) { return this.setPosition(new Vector3(x, y, z)); }
		public Builder setPosition(Vector3 position) {
			if (position != null) {
				Matrix4 transform = this.physRigidBody.body.getWorldTransform();
				transform.setTranslation(position);
				this.physRigidBody.body.setWorldTransform(transform);
			}
			return this;
		}
		
		public Builder setScale(float x, float y, float z) { return this.setScale(new Vector3(x, y, z)); }
		public Builder setScale(Vector3 scale) {
			if (scale != null) {
				Vector3 tmpPosition = new Vector3();
				Quaternion tmpRotation = new Quaternion();
				Matrix4 transform = this.physRigidBody.body.getWorldTransform();
				tmpPosition = transform.getTranslation(tmpPosition);
				tmpRotation = transform.getRotation(tmpRotation);
				transform.setTranslation(tmpPosition);
				transform.rotate(tmpRotation);
				transform.scale(scale.x, scale.y, scale.z);
				this.physRigidBody.body.setWorldTransform(transform);
			}
			return this;
		}
		
		public Builder setRotation(float yaw, float pitch, float roll) { return this.setRotation(new Quaternion().setEulerAngles(yaw, pitch, roll)); }
		public Builder setRotation(Quaternion rotation) {
			if (rotation != null) {
				Vector3 tmpPosition = new Vector3(), tmpScale = new Vector3();
				Matrix4 transform = this.physRigidBody.body.getWorldTransform();
				tmpPosition = transform.getTranslation(tmpPosition);
				tmpScale = transform.getScale(tmpScale);
				transform.setTranslation(tmpPosition);
				transform.rotate(rotation);
				transform.scale(tmpScale.x, tmpScale.y, tmpScale.z);
				this.physRigidBody.body.setWorldTransform(transform);
			}
			return this;
		}
		
		public Builder setMass(float mass) {
			if (this.physRigidBody.shape != null) {
				Vector3 inertia = new Vector3();
				this.physRigidBody.shape.calculateLocalInertia(mass, inertia);
				this.physRigidBody.body.setMassProps(mass, inertia);
			}
			return this;
		}
		
		public btRigidBody getBody() {
			return this.physRigidBody.body;
		}
		
		public PhysRigidBody build() {
			if (this.physRigidBody.flags == 0) this.physRigidBody.flags = IPhysObject.OBJECT_STATIC;
			return this.physRigidBody;
		}
	}
}
