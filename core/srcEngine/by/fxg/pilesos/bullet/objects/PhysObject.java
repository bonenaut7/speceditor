package by.fxg.pilesos.bullet.objects;

import java.util.UUID;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

public class PhysObject extends PhysBaseObject {
	protected btCollisionObject object;
	
	private PhysObject() {}
	
	/** Creates PhysObject, applies own userData to object **/
	public PhysObject(String name, btCollisionObject object) {
		this.name = name;
		this.object = object;
		this.object.userData = this;
		this.shape = object.getCollisionShape();
	}
	
	/** Creates static PhysObject with provided shape**/
	public PhysObject(String name, btCollisionShape shape) { this(name, shape, CollisionFlags.CF_STATIC_OBJECT, OBJECT_STATIC); }
	/** Creates static PhysObject with provided shape and flags **/
	public PhysObject(String name, btCollisionShape shape, long flags) { this(name, shape, CollisionFlags.CF_STATIC_OBJECT, flags | OBJECT_STATIC); }
	/** Creates PhysObject with provided shape and flags **/
	public PhysObject(String name, btCollisionShape shape, int bulletFlags, long flags) {
		this.name = name;
		this.object = new btCollisionObject();
		this.object.setCollisionShape(shape);
		this.object.setCollisionFlags(bulletFlags);
		this.object.userData = this;
		this.shape = shape;
		this.flags = flags;
	}
	
	public btCollisionObject getObject() {
		return this.object;
	}
	
	public void dispose() {
		if (this.object != null) this.object.dispose();
		super.dispose();
	}

	
	public static class Builder {
		private PhysObject physObject;
		
		public Builder(String name) {
			this.physObject = new PhysObject();
			this.physObject.name = (name == null ? UUID.randomUUID().toString() : name);
			this.physObject.object = new btCollisionObject();
			this.physObject.object.userData = this.physObject;
		}
		
		public Builder setShapeAABB(Vector3 size) {
			this.setShape(new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)));
			if (size != null) this.physObject.shape.setLocalScaling(size);
			return this;
		}
		
		public Builder setShapeSphere(Vector3 size) {
			this.setShape(new btSphereShape(0.5f));
			if (size != null) this.physObject.shape.setLocalScaling(size);
			return this;
		}
		
		public Builder setShapeCapsule(Vector3 scale) {
			this.setShape(new btCapsuleShape(0.5f, 1f));
			if (scale != null) this.physObject.shape.setLocalScaling(scale);
			return this;
		}
		
		public Builder setShape(btCollisionShape shape) {
			if (shape != null) {
				if (this.physObject.shape != null && !this.physObject.shape.isDisposed()) {
					this.physObject.shape.dispose();
				}
				this.physObject.shape = shape;
				this.physObject.object.setCollisionShape(shape);
			}
			return this;
		}
		
		public Builder setShapeSize(Vector3 size) {
			if (size != null && this.physObject.shape != null) {
				this.physObject.shape.setLocalScaling(size);
			}
			return this;
		}
		
		public Builder setCollisionFlags(int bulletFlags) {
			this.physObject.object.setCollisionFlags(bulletFlags);
			return this;
		}
		
		public Builder setObjectFlags(long flags) {
			this.physObject.flags = flags;
			return this;
		}
		
		public Builder addFlag(long flag) {
			this.physObject.addFlag(flag);
			return this;
		}
		
		public Builder removeFlag(long flag) {
			this.physObject.removeFlag(flag);
			return this;
		}
		
		public Builder setPosition(Vector3 position) {
			if (position != null) {
				Matrix4 transform = this.physObject.object.getWorldTransform();
				transform.setTranslation(position);
				this.physObject.object.setWorldTransform(transform);
			}
			return this;
		}
		
		public Builder setScale(Vector3 scale) {
			if (scale != null) {
				Vector3 tmpPosition = new Vector3();
				Quaternion tmpRotation = new Quaternion();
				Matrix4 transform = this.physObject.object.getWorldTransform();
				tmpPosition = transform.getTranslation(tmpPosition);
				tmpRotation = transform.getRotation(tmpRotation);
				transform.setTranslation(tmpPosition);
				transform.rotate(tmpRotation);
				transform.scale(scale.x, scale.y, scale.z);
				this.physObject.object.setWorldTransform(transform);
			}
			return this;
		}
		
		public Builder setRotation(Quaternion rotation) {
			if (rotation != null) {
				Vector3 tmpPosition = new Vector3(), tmpScale = new Vector3();
				Matrix4 transform = this.physObject.object.getWorldTransform();
				tmpPosition = transform.getTranslation(tmpPosition);
				tmpScale = transform.getScale(tmpScale);
				transform.setTranslation(tmpPosition);
				transform.rotate(rotation);
				transform.scale(tmpScale.x, tmpScale.y, tmpScale.z);
				this.physObject.object.setWorldTransform(transform);
			}
			return this;
		}
		
		public btCollisionObject getBody() {
			return this.physObject.object;
		}
		
		public PhysObject build() {
			if (this.physObject.flags == 0) this.physObject.flags = IPhysObject.OBJECT_STATIC;
			return this.physObject;
		}
	}
}
