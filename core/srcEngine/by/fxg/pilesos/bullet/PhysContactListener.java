package by.fxg.pilesos.bullet;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.utils.Array;

public class PhysContactListener extends ContactListener {
	private static PhysContactListener instance;
	
	/** Creates main instance of PhysContactListener **/
	public static void create() {  if (instance != null) instance = new PhysContactListener(); }
	
	/** Enables main instance of PhysContactListener if was disabled before **/
	public static void reEnable() { if (instance != null) instance.enable(); }
	
	/** Adds sub-listener to the main contact listener with default flags **/
	public static void addSubListener(IContactSubListener subListener) { addSubListenerTo(subListener, false, true, false, true, false); }
	
	/** Adds sub-listener to the main contact listener with following flags(check method boolean-type arguments) **/
	public static void addSubListenerTo(IContactSubListener subListener, boolean onContactAdded, boolean onContactStarted, boolean onContactProcessed, boolean onContactEnded, boolean onContactDestroyed) {
		if (onContactAdded && !instance.onContactAddedSubListeners.contains(subListener, true)) instance.onContactAddedSubListeners.add(subListener);
		if (onContactStarted && !instance.onContactStartedSubListeners.contains(subListener, true)) instance.onContactStartedSubListeners.add(subListener);
		if (onContactProcessed && !instance.onContactProcessedSubListeners.contains(subListener, true)) instance.onContactProcessedSubListeners.add(subListener);
		if (onContactEnded && !instance.onContactEndedSubListeners.contains(subListener, true)) instance.onContactEndedSubListeners.add(subListener);
		if (onContactDestroyed && !instance.onContactDestroyedSubListeners.contains(subListener, true)) instance.onContactDestroyedSubListeners.add(subListener);
	}
	
	/** Removes subListener completely from main contact listener **/
	public static void removeSubListener(IContactSubListener subListener) {
		instance.onContactAddedSubListeners.removeValue(subListener, true);
		instance.onContactStartedSubListeners.removeValue(subListener, true);
		instance.onContactProcessedSubListeners.removeValue(subListener, true);
		instance.onContactEndedSubListeners.removeValue(subListener, true);
		instance.onContactDestroyedSubListeners.removeValue(subListener, true);
	}
	
	/** Removes subListener from main contact listener methods with following flags(check method boolean-type arguments) **/
	public static void removeSubListenerFrom(IContactSubListener subListener, boolean onContactAdded, boolean onContactStarted, boolean onContactProcessed, boolean onContactEnded, boolean onContactDestroyed) {
		if (onContactAdded) instance.onContactAddedSubListeners.removeValue(subListener, true);
		if (onContactStarted) instance.onContactStartedSubListeners.removeValue(subListener, true);
		if (onContactProcessed) instance.onContactProcessedSubListeners.removeValue(subListener, true);
		if (onContactEnded) instance.onContactEndedSubListeners.removeValue(subListener, true);
		if (onContactDestroyed) instance.onContactDestroyedSubListeners.removeValue(subListener, true);
	}
	
	/*========== private things so no mess with contactlistener ==========*/
	private Array<IContactSubListener> onContactAddedSubListeners;
	private Array<IContactSubListener> onContactStartedSubListeners;
	private Array<IContactSubListener> onContactProcessedSubListeners;
	private Array<IContactSubListener> onContactEndedSubListeners;
	private Array<IContactSubListener> onContactDestroyedSubListeners;
	
	private PhysContactListener() {
		this.onContactAddedSubListeners = new Array<>();
		this.onContactStartedSubListeners = new Array<>();
		this.onContactProcessedSubListeners = new Array<>();
		this.onContactEndedSubListeners = new Array<>();
		this.onContactDestroyedSubListeners = new Array<>();
	} 
	
	//calls per single shape face contact creation (maybe)
	public boolean onContactAdded(btManifoldPoint manifoldPoint, btCollisionObject object0, int partID0, int index0, btCollisionObject object1, int partID1, int index1) {
		// TODO FIXME
		// Indexes are not used in sublisteners. Main point of this decision is that i don't know what are they do
		//  and where they're can be used. If indexes are uservalues, its right decision that they're not used because
		//  user values are usually using as parent objects instead of indexes.
		this.onContactAddedSubListeners.forEach(subListener -> subListener.onContactAdded(manifoldPoint, object0, partID0, object1, partID1));
		return false;
	}
	
	//calls on contact start
	public void onContactStarted(btCollisionObject object0, btCollisionObject object1) {
		this.onContactStartedSubListeners.forEach(subListener -> subListener.onContactStarted(object0, object1));
	}
	
	//calls every tick, calls per single shape face
	public void onContactProcessed(btManifoldPoint manifoldPoint, btCollisionObject object0, btCollisionObject object1) {
		this.onContactProcessedSubListeners.forEach(subListener -> subListener.onContactProcessed(manifoldPoint, object0, object1));
	}
	
	//calls on contact end
	public void onContactEnded(btCollisionObject object0, btCollisionObject object1) {
		this.onContactEndedSubListeners.forEach(subListener -> subListener.onContactEnded(object0, object1));
	}
	
	//works strange
	public void onContactDestroyed(int manifoldPointUserValue) {
		this.onContactDestroyedSubListeners.forEach(subListener -> subListener.onContactDestroyed(manifoldPointUserValue));
	}
}
