package by.fxg.speceditor.tools.debugdraw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btNNCGConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;

public class DebugDraw3D {
	public DebugDrawer drawer;
	private btCollisionConfiguration colConfig;
	private btCollisionDispatcher colDispatcher;
	private btBroadphaseInterface broadphase;
	private btConstraintSolver solver;
	public btDiscreteDynamicsWorld world;
	private Array<btCollisionObject> objects = new Array<>();
	
	public DebugDraw3D() {
		this.drawer = new DebugDrawer();
		this.colConfig = new btDefaultCollisionConfiguration();
		this.colDispatcher = new btCollisionDispatcher(this.colConfig);
		this.broadphase = new btDbvtBroadphase();
		this.solver = new btNNCGConstraintSolver();
		this.world = new btDiscreteDynamicsWorld(this.colDispatcher, this.broadphase, this.solver, this.colConfig);
		this.world.setDebugDrawer(this.drawer);
		this.drawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
	}
	
	public void update() {
		this.world.stepSimulation(Gdx.graphics.getDeltaTime());
	}
	
	public void addObject() {
		
	}
}
