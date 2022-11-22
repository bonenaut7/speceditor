package by.fxg.pilesos.bullet.objects;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public interface IPhysObject {
	String getName();
	btCollisionObject getObject();
	btCollisionShape getShape();
	
	long getPhysFlags();
	IPhysObject setPhysFlags(long flags);
	int getFilterMask();
	IPhysObject setFilterMask(int filterMask);
	int getFilterGroup();
	IPhysObject setFilterGroup(int filterGroup);
	
	void dispose();
	
	/*=== Flags next here, implemented here because lower code amount may be used inside classes ===*/
	public static final long 
		DISABLE_LISTEN 		= (long)Math.pow(2, 1),  /*2        *//* Disables object for listen in listener */
		RAYCASTABLE			= (long)Math.pow(2, 2),  /*4        *//* Allows object to be raycasted (64, mask & group) */
		RESERVED03 			= (long)Math.pow(2, 3),  /*8        *//*  */
		RESERVED04  		= (long)Math.pow(2, 4),  /*16       *//*  */
		RESERVED05 			= (long)Math.pow(2, 5),  /*32       *//*  */
		RESERVED06	 		= (long)Math.pow(2, 6),  /*64       *//*  */
		RESERVED07	 		= (long)Math.pow(2, 7),  /*128      *//*  */
		
		RESERVED08			= (long)Math.pow(2, 8),  /*256      *//*  */
		RESERVED09			= (long)Math.pow(2, 9),  /*512      *//*  */
		RESERVED10			= (long)Math.pow(2, 10), /*1024     *//*  */
		RESERVED11			= (long)Math.pow(2, 11), /*2048     *//*  */
		RESERVED12			= (long)Math.pow(2, 12), /*4096     *//*  */
		RESERVED13			= (long)Math.pow(2, 13), /*8192     *//*  */
		RESERVED14			= (long)Math.pow(2, 14), /*16384    *//*  */
		RESERVED15			= (long)Math.pow(2, 15), /*32768    *//*  */
	
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
	
	/** Collision filter **/
	public static final int
		FILTER_ALL				= -1,					/*-1       *//* Default bullet filter, used as all filterGroups */
		FILTER_btDEFAULT		= 1,					/*1        *//* Default bullet filter for default objects */
		FILTER_btSTATIC			= (int)Math.pow(2, 1),  /*2        *//* Default bullet filter for static objects */
		FILTER_btKINEMATIC		= (int)Math.pow(2, 2),  /*4        *//* Default bullet filter for kinematic objects */
		FILTER_btDEBRIS			= (int)Math.pow(2, 3),  /*8        *//* Default bullet filter for debris objects */
		FILTER_btSENSOR			= (int)Math.pow(2, 4),  /*16       *//* Default bullet filter for sensor objects */
		FILTER_btCHARACTER		= (int)Math.pow(2, 5),  /*32       *//* Default bullet filter for character objects */
		FILTER_btRESERVED06		= (int)Math.pow(2, 6),  /*64       *//*  */
		FILTER_btRESERVED07		= (int)Math.pow(2, 7),  /*128      *//*  */
		
		FILTER_RAYCASTABLE		= (int)Math.pow(2, 8),  /*256      *//* Filter for raycastable objects  */
		FILTER_RESERVED09		= (int)Math.pow(2, 9),  /*512      *//*  */
		FILTER_RESERVED10		= (int)Math.pow(2, 10), /*1024     *//*  */
		FILTER_RESERVED11		= (int)Math.pow(2, 11), /*2048     *//*  */
		FILTER_RESERVED12		= (int)Math.pow(2, 12), /*4096     *//*  */
		FILTER_RESERVED13		= (int)Math.pow(2, 13), /*8192     *//*  */
		FILTER_RESERVED14		= (int)Math.pow(2, 14), /*16384    *//*  */
		FILTER_RESERVED15		= (int)Math.pow(2, 15); /*32768    *//*  */
	
	/** Bullet activation state **/
	public static final int
		ACTSTATE_NOT_SET				= 0,
		ACTSTATE_ACTIVE					= 1,
		ACTSTATE_DEACTIVATED			= 2,
		ACTSTATE_WANTS_DEACTIVATION		= 3,
		ACTSTATE_DISABLE_DEACTIVATION	= 4,
		ACTSTATE_DISABLE_SIMULATION		= 5;
	
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