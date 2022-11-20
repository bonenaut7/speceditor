package by.fxg.pilesos.bullet;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;

public interface IContactSubListener {
	default void onContactAdded(btManifoldPoint manifoldPoint, btCollisionObject object0, int partID0, btCollisionObject object1, int partID1) {}
	default void onContactStarted(btCollisionObject object0, btCollisionObject object1) {}
	default void onContactProcessed(btManifoldPoint manifoldPoint, btCollisionObject object0, btCollisionObject object1) {}
	default void onContactEnded(btCollisionObject object0, btCollisionObject object1) {}
	default void onContactDestroyed(int manifoldPointUserValue) {}
}
