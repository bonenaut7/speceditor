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

public class PhysObject extends PhysBaseObject {
	protected btCollisionObject object;
	
	//private constructor, currently available to create PhysObject only with Builder
	private PhysObject() {}
	
	public btCollisionObject getObject() {
		return this.object;
	}
	
	public void dispose() {
		if (this.object != null) this.object.release();
		super.dispose();
	}
	
	public static class Builder {
		private String name;
		private btCollisionShape shape;
		
		private Vector3 position = new Vector3(), scale = new Vector3(1, 1, 1);
		private Quaternion rotation = new Quaternion();
		
		private long specFlags;
		private int activationState, collisionFlags, filterMask, filterGroup;
		
		public Builder() {
			this(UUID.randomUUID().toString());
		}
		
		public Builder(String name) {
			this.name = name == null ? UUID.randomUUID().toString() : name;
		}
		
		//Shape
		/** Sets AABB shape if construction info is not set **/
		public Builder setShapeAABB(Vector3 size) {
			return this.setShape(new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)));
		}
		
		/** Sets sphere shape if construction info is not set **/
		public Builder setShapeSphere() {
			return this.setShape(new btSphereShape(0.5f));
		}
		
		/** Sets capsule shape if construction info is not set **/
		public Builder setShapeCapsule() {
			return this.setShape(new btCapsuleShape(0.5f, 1f));
		}
		
		/** Sets shape if construction info is not set **/
		public Builder setShape(btCollisionShape shape) {
			if (this.shape != null) this.shape.release();
			this.shape = shape;
			return this;
		}
		
		//Flags
		/** Returns {@link IPhysObject} flags **/
		public long getSpecFlags() { return this.specFlags; }
		/** Sets {@link IPhysObject} flags **/
		public Builder setSpecFlags(long flags) {
			this.specFlags = flags;
			return this;
		}
		
		/** Returns activation state **/
		public int getActivationState() { return this.activationState; }
		/** Sets activation state **/
		public Builder setActivationState(int activationFlag) {
			this.activationState = activationFlag;
			return this;
		}
		
		/** Returns collision flags **/
		public int getCollisionFlags() { return this.collisionFlags; }
		/** Sets collision flags **/
		public Builder setCollisionFlags(int collisionFlags) {
			this.collisionFlags = collisionFlags;
			return this;
		}
		
		/** Returns collision filter mask **/
		public int getCollisionFilterMask() { return this.filterMask; }
		/** Sets collision filter mask **/
		public Builder setCollisionFilterMask(int filterMask) {
			this.filterMask = filterMask;
			return this;
		}
		
		/** Returns collision filter group **/
		public int getCollisionFilterGroup() { return this.filterGroup; }
		/** Sets collision filter group **/
		public Builder setCollisionFilterGroup(int filterGroups) {
			this.filterGroup = filterGroups;
			return this;
		}
		
		//Transforms
		/** Sets position of object **/
		public Builder setPosition(Vector3 position) {
			this.position.set(position);
			return this;
		}
		
		/** Sets scale of object **/
		public Builder setScale(Vector3 scale) {
			this.scale.set(scale);
			return this;
		}
		
		/** Sets rotation of object **/
		public Builder setRotation(Quaternion rotation) {
			this.rotation.set(rotation);
			return this;
		}
		
		/** Builds PhysObject(btCollisionObject with parameters) **/
		public PhysObject build() {
			if (this.shape == null) this.shape = new btBoxShape(new Vector3(0.5F, 0.5F, 0.5F));
			
			PhysObject physObject = new PhysObject();
			physObject.name = this.name;
			physObject.setFlags(this.specFlags);
			physObject.setFilterMask(this.filterMask);
			physObject.setFilterGroup(this.filterGroup);
			physObject.shape = this.shape;

			physObject.object = new btCollisionObject();
			physObject.object.setCollisionShape(this.shape);
			physObject.object.setCollisionFlags(this.collisionFlags);
			physObject.object.setActivationState(this.activationState);
			physObject.object.userData = physObject;

			//Transforms
			Matrix4 transform = physObject.object.getWorldTransform();
			transform.setToTranslation(this.position);
			transform.rotate(this.rotation);
			transform.scale(this.scale.x, this.scale.x, this.scale.x);
			physObject.object.setWorldTransform(transform);
			return physObject;
		}
	}
}
