package by.fxg.pilesos.bullet.objects;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

import by.fxg.pilesos.bullet.PhysMotionState;

public interface IPhysObject {
	String getName();
	btCollisionObject getObject();
	btCollisionShape getShape();
	PhysMotionState getState();
	
	default void onRaycast() {}
	
	long getFlags();
	boolean hasFlag(long flag);
	IPhysObject addFlag(long flag);
	IPhysObject removeFlag(long flag);
	
	void dispose();
	
	/*=== Flags next here, implemented here because lower code amount may be used inside classes ===*/
	public static final long 
		NO_COLLISION 		= (long)Math.pow(2, 1),  /*2        *//* No collideable object */
		NO_FREEZE	 		= (long)Math.pow(2, 2),  /*4        *//* Not freezeable by bullet */
		OBJECT_STATIC 		= (long)Math.pow(2, 3),  /*8        *//* Static object */
		OBJECT_DYNAMIC 		= (long)Math.pow(2, 4),  /*16       *//* Dynamic object */
		RESERVED5	 		= (long)Math.pow(2, 5),  /*32       *//*  */
		RESERVED6	 		= (long)Math.pow(2, 6),  /*64       *//*  */
		RESERVED7	 		= (long)Math.pow(2, 7),  /*128      *//*  */
		
		RAYCASTABLE			= (long)Math.pow(2, 8),  /*256      *//* Raycastable */
		RESERVED9			= (long)Math.pow(2, 9),  /*512      *//*  */
		RESERVED10			= (long)Math.pow(2, 10), /*1024     *//*  */
		RESERVED11			= (long)Math.pow(2, 11), /*2048     *//*  */
		RESERVED12			= (long)Math.pow(2, 12), /*4096     *//*  */
		RESERVED13			= (long)Math.pow(2, 13), /*8192     *//*  */
		RESERVED14			= (long)Math.pow(2, 14), /*16384    *//*  */
		DISABLE_LISTEN		= (long)Math.pow(2, 15), /*32768    *//* Disables object for listen in listener */
	
		RESERVED16			= (long)Math.pow(2, 16), /*32768    *//*  */
		RESERVED17			= (long)Math.pow(2, 17), /*65536    *//*  */
		RESERVED18			= (long)Math.pow(2, 18), /*131072   *//*  */
		RESERVED19			= (long)Math.pow(2, 19), /*262144   *//*  */
		RESERVED20			= (long)Math.pow(2, 20), /*524288   *//*  */
		RESERVED21			= (long)Math.pow(2, 21), /*1048576  *//*  */
		RESERVED22			= (long)Math.pow(2, 22), /*2097152  *//*  */
		RESERVED23			= (long)Math.pow(2, 23), /*4194304  *//*  */
		
		RESERVED24			= (long)Math.pow(2, 24), /*8388608  *//*  */
		RESERVED25			= (long)Math.pow(2, 25), /*16777216 *//*  */
		RESERVED26			= (long)Math.pow(2, 26), /*33554432 *//*  */
		RESERVED27			= (long)Math.pow(2, 27), /*67108864 *//*  */
		RESERVED28			= (long)Math.pow(2, 28), /*134217728*//*  */
		RESERVED29			= (long)Math.pow(2, 29), /*268435456*//*  */
		RESERVED30			= (long)Math.pow(2, 30), /*536870914*//*  */
		RESERVED31			= (long)Math.pow(2, 31); /*107374824*//*  */
	
	public static long addFlag(long flags, long flag) {
		if (!hasFlag(flags, flag)) {
			flags |= flag;
		}
		return flags;
	}
	
	public static long removeFlag(long flags, long flag) {
		if (hasFlag(flags, flag)) {
			flags = flags & ~flag;
		}
		return flags;
	}
	
	public static long invertFlag(long flags, long flag) {
		if (hasFlag(flags, flag)) return removeFlag(flags, flag);
		else return addFlag(flags, flag);
	}
	
	public static boolean hasFlag(long flags, long flag) {
		return (flags & flag) == flag;
	}
}
