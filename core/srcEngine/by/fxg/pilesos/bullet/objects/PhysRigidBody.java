package by.fxg.pilesos.bullet.objects;

import java.util.UUID;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape;
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
		
		private long physFlags;
		private int activationState = IPhysObject.ACTSTATE_ACTIVE, collisionFlags, filterMask = IPhysObject.FILTER_ALL, filterGroup = IPhysObject.FILTER_btDEFAULT;
		private boolean calculateInertia = true;
		private float mass = 1f;
		
		public Builder() {
			this.setName(null);
		}
		
		public Builder(String name) {
			this.setName(name);
		}
		
		/** Sets object name **/
		public Builder setName(String name) {
			this.name = name == null ? UUID.randomUUID().toString() : name;
			return this;
		}
		
		public Builder setPhysMotionState(PhysMotionState physMotionState) {
			this.physMotionState = physMotionState;
			return this;
		}
		
		//Shape
		/** Sets box shape if construction info is not set **/ public Builder setShapeBox() { return this.setShapeBox(0.5F, 0.5F, 0.5F); }
		/** Sets box shape if construction info is not set **/ public Builder setShapeBox(float x, float y, float z) { return this.setShapeBox(new Vector3(x, y, z)); }
		/** Sets box shape if construction info is not set **/ public Builder setShapeBox(Vector3 vec) { return this.setShape(new btBoxShape(vec)); }
		
		/** Sets capsule shape if construction info is not set **/ public Builder setShapeCapsule() { return this.setShapeCapsule(0.5F, 1.0F); }
		/** Sets capsule shape if construction info is not set **/ public Builder setShapeCapsule(float radius, float height) { return this.setShape(new btCapsuleShape(radius, height)); }
		
		/** Sets cone shape if construction info is not set **/ public Builder setShapeCone() { return this.setShapeCone(0.5F, 0.5F); }
		/** Sets cone shape if construction info is not set **/ public Builder setShapeCone(float radius, float height) { return this.setShape(new btConeShape(radius, height)); }
		
		/** Sets cylinder shape if construction info is not set **/ public Builder setShapeCylinder() { return this.setShapeCylinder(0.5F, 0.5F, 0.5F); }
		/** Sets cylinder shape if construction info is not set **/ public Builder setShapeCylinder(float x, float y, float z) { return this.setShapeCylinder(new Vector3(x, y, z)); }
		/** Sets cylinder shape if construction info is not set **/ public Builder setShapeCylinder(Vector3 halfSize) { return this.setShape(new btCylinderShape(halfSize)); }
		
		/** Sets plane shape if construction info is not set **/ public Builder setShapePlane() { return this.setShapePlane(Vector3.Y, 0.5F); }
		/** Sets plane shape if construction info is not set **/ public Builder setShapePlane(Vector3 normal, float constant) { return this.setShape(new btStaticPlaneShape(normal, constant)); }
		
		/** Sets sphere shape if construction info is not set **/ public Builder setShapeSphere() { return this.setShapeSphere(0.5F); }
		/** Sets sphere shape if construction info is not set **/ public Builder setShapeSphere(float radius) { return this.setShape(new btSphereShape(radius)); }
		
		/** Sets shape if construction info is not set **/
		public Builder setShape(btCollisionShape shape) {
			if (this.shape != null) this.shape.release();
			this.shape = shape;
			return this;
		}
		
		/** Sets mass value for further inertia calculation **/
		public Builder setMass(float mass, boolean calculateInertia) {
			this.mass = mass;
			this.calculateInertia = calculateInertia;
			return this;
		}
		
		//Flags
		/** Returns {@link IPhysObject} flags **/
		public long getPhysFlags() { return this.physFlags; }
		/** Sets {@link IPhysObject} flags **/
		public Builder setPhysFlags(long flags) {
			this.physFlags = flags;
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
		public Builder setPosition(float x, float y, float z) {
			this.position.set(x, y, z);
			return this;
		}
		
		/** Sets position of object **/
		public Builder setPosition(Vector3 position) {
			this.position.set(position);
			return this;
		}
		
		/** Sets scale of object **/
		public Builder setScale(float x, float y, float z) {
			this.scale.set(x, y, z);
			return this;
		}
		
		/** Sets scale of object **/
		public Builder setScale(Vector3 scale) {
			this.scale.set(scale);
			return this;
		}
		
		/** Sets rotation of object(in euler angles) **/
		public Builder setRotation(float pitch, float yaw, float roll) {
			this.rotation.setEulerAngles(pitch, yaw, roll);
			return this;
		}
		
		/** Sets rotation of object(in euler angles) **/
		public Builder setRotation(Vector3 rotation) {
			this.rotation.setEulerAngles(rotation.y, rotation.x, rotation.z);
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
			physObject.setPhysFlags(this.physFlags);
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
			if (this.calculateInertia) {
				Vector3 inertia = new Vector3();
				physObject.shape.calculateLocalInertia(this.mass, inertia);
				physObject.body.setMassProps(this.mass, inertia);
			}
			
			//Transforms
			Matrix4 transform = physObject.body.getWorldTransform();
			transform.setToTranslation(this.position);
			transform.rotate(this.rotation);
			transform.scale(this.scale.x, this.scale.x, this.scale.x);
			physObject.body.setWorldTransform(transform);
			return physObject;
		}
		
		public void reset() {
			this.shape = null;
			this.physMotionState = null;
			this.constructionInfo = null;
			this.position = new Vector3();
			this.rotation = new Quaternion();
			this.scale = new Vector3();
			this.physFlags = this.activationState = this.collisionFlags = this.filterMask = this.filterGroup = 0;
			this.mass = 0;
		}
	}
}
