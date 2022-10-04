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
	
	protected Array<IPhysObject> raycastable = new Array<>();
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
			if (object.hasFlag(IPhysObject.RAYCASTABLE) && !this.raycastable.contains(object, true)) {
				this.raycastable.add(object);
			}
			return this.addObject(object.getObject());
		}
		return false;
	}
	
	public boolean addObject(btCollisionObject object) {
		if (object != null && !this.objects.contains(object, true)) {
			this.objects.add(object);
			if (object instanceof btRigidBody) this.world.addRigidBody((btRigidBody)object);
			else this.world.addCollisionObject(object);
			return true;
		}
		return false;
	}
	
	public boolean removeObject(IPhysObject object) {
		if (object != null && object.getObject() != null) {
			this.raycastable.removeValue(object, true);
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
	
	public void dispose() {
		for (btCollisionObject object : this.objects) this.removeObject(object);
		if (this.world != null && !this.world.isDisposed()) this.world.dispose();
		if (this.solver != null && !this.solver.isDisposed()) this.solver.dispose();
		if (this.broadphase != null && !this.broadphase.isDisposed()) this.broadphase.dispose();
		if (this.dispatcher != null && !this.dispatcher.isDisposed()) this.dispatcher.dispose();
		if (this.config != null && !this.config.isDisposed()) this.config.dispose();
	}
}
