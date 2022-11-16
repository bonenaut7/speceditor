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
	protected PhysMotionState state;
	
	//private constructor, currently available to create PhysRigidBody only with Builder
	private PhysRigidBody() {}
	
	public btRigidBody getBody() {
		return this.body;
	}
	
	public PhysMotionState getState() {
		return this.state;
	}
	
	public btCollisionObject getObject() {
		return this.body;
	}
	
	public void dispose() {
		if (this.body != null) this.body.release();
		if (this.info != null) this.info.release();
		if (this.state != null) this.state.release();
		super.dispose();
	}
	
	public static class Builder {
		private String name;
		private btCollisionShape shape;
		private PhysMotionState physMotionState;
		private btRigidBodyConstructionInfo constructionInfo;
		
		private Vector3 position = new Vector3(), scale = new Vector3(1, 1, 1);
		private Quaternion rotation = new Quaternion();
		
		private long specFlags;
		private int activationState, collisionFlags, filterMask, filterGroup;
		private float mass;
		
		public Builder() {
			this(UUID.randomUUID().toString());
		}
		
		public Builder(String name) {
			this.name = name == null ? UUID.randomUUID().toString() : name;
		}
		
		public Builder setPhysMotionState(PhysMotionState physMotionState) {
			this.physMotionState = physMotionState;
			return this;
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
		
		/** Sets mass value for further inertia calculation **/
		public Builder setMass(float mass) {
			this.mass = mass;
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
		
		/** Builds PhysRigidBody(btRigidBody with parameters) <br>
		 *  <b>! Warning, this builder is not using {@link btRigidBodyConstructionInfo}'s motion state and shape, you should set them separately !</b> **/
		public PhysRigidBody build() {
			if (this.shape == null) this.shape = new btBoxShape(new Vector3(0.5F, 0.5F, 0.5F));
			if (this.physMotionState == null) this.physMotionState = new PhysMotionState(new Matrix4());
			if (this.constructionInfo == null) this.constructionInfo = new btRigidBodyConstructionInfo(this.mass > 0 ? this.mass : 0, this.physMotionState, this.shape);
			
			PhysRigidBody physObject = new PhysRigidBody();
			physObject.name = this.name;
			physObject.setFlags(this.specFlags);
			physObject.setFilterMask(this.filterMask);
			physObject.setFilterGroup(this.filterGroup);
			physObject.shape = this.shape;
			physObject.state = this.physMotionState;
			physObject.info = this.constructionInfo;

			physObject.body = new btRigidBody(this.constructionInfo);
			physObject.body.setMotionState(this.physMotionState);
			physObject.body.setCollisionShape(this.shape);
			physObject.body.setCollisionFlags(this.collisionFlags);
			physObject.body.setActivationState(this.activationState);
			physObject.body.userData = physObject;
			
			//Inertia calculation
			Vector3 inertia = new Vector3();
			physObject.shape.calculateLocalInertia(this.mass, inertia);
			physObject.body.setMassProps(this.mass, inertia);

			//Transforms
			Matrix4 transform = physObject.body.getWorldTransform();
			transform.setToTranslation(this.position);
			transform.rotate(this.rotation);
			transform.scale(this.scale.x, this.scale.x, this.scale.x);
			physObject.body.setWorldTransform(transform);
			return physObject;
		}
	}
}
