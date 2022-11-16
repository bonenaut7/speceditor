package by.fxg.pilesos.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.bullet.objects.IPhysObject;

public class BasePhysics {
	private int maxSubSteps = 5;
	private float fixedTimeSteps = 1f/60f;
	private Vector3 gravity = new Vector3(0f, -9.8f, 0f);
	
	public btCollisionConfiguration config;
	public btCollisionDispatcher dispatcher;
	public btBroadphaseInterface broadphase;
	public btConstraintSolver solver;
	public btDiscreteDynamicsWorld world;
	public Array<btCollisionObject> objects = new Array<>();

	public BasePhysics(int maxSubSteps, float fixedTimeSteps, Vector3 gravity) {
		this();
		this.maxSubSteps = maxSubSteps;
		this.fixedTimeSteps = fixedTimeSteps;
		this.world.setGravity(this.gravity = gravity);
	}
	
	public BasePhysics() {
		this.config = new btDefaultCollisionConfiguration();
		this.dispatcher = new btCollisionDispatcher(this.config);
		this.broadphase = new btDbvtBroadphase();
		this.solver = new btSequentialImpulseConstraintSolver(); //btNNCGConstraintSolver, btMultiBodyConstraintSolver, btSequentialImpulseConstraintSolver
		this.world = new btDiscreteDynamicsWorld(this.dispatcher, this.broadphase, this.solver, this.config);
		this.world.setGravity(this.gravity);
		this.world.setLatencyMotionStateInterpolation(true);
	}
	
	public void update() {
		this.world.stepSimulation(Gdx.graphics.getDeltaTime(), this.maxSubSteps, this.fixedTimeSteps);
	}
	
	public boolean addObject(IPhysObject object) {
		if (object != null && object.getObject() != null) {
			int filterGroup = object.getFilterGroup();
			if (IPhysObject.hasFlag(object.getFlags(), IPhysObject.RAYCASTABLE)) IPhysObject.addFlag(filterGroup, IPhysObject.FILTER_RAYCASTABLE);
			return this.addObject(object.getObject(), object.getFilterMask(), filterGroup);
		}
		return false;
	}
	
	public boolean addObject(btCollisionObject object, int filterGroup, int filterMask) {
		if (object != null && !this.objects.contains(object, true)) {
			this.objects.add(object);
			if (object instanceof btRigidBody) this.world.addRigidBody((btRigidBody)object, filterGroup, filterMask);
			else this.world.addCollisionObject(object, filterGroup, filterMask);
			return true;
		}
		return false;
	}
	
	public boolean removeObject(IPhysObject object) {
		if (object != null && object.getObject() != null) {
			return this.removeObject(object.getObject());
		}
		return false;
	}
	
	public boolean removeObject(btCollisionObject object) {
		if (object != null && this.objects.contains(object, true)) {
			this.objects.removeValue(object, true);
			if (object instanceof btRigidBody) this.world.removeRigidBody((btRigidBody)object);
			else this.world.removeCollisionObject(object);
			return true;
		}
		return false;
	}
	
	public Vector3 getGravity() { return this.gravity; }
	public BasePhysics setGravity(Vector3 vec) { return this.setGravity(vec.x, vec.y, vec.z); }
	public BasePhysics setGravity(float x, float y, float z) {
		this.world.setGravity(this.gravity.set(x, y, z));
		return this;
	}
	
	public void dispose() {
		this.objects.forEach(this::removeObject);
		if (this.world != null) this.world.release();
		if (this.solver != null) this.solver.release();
		if (this.broadphase != null) this.broadphase.release();
		if (this.dispatcher != null) this.dispatcher.release();
		if (this.config != null) this.config.release();
	}
}
