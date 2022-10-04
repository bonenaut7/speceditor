package by.fxg.pilesos.specformat.graph;

import com.badlogic.gdx.math.Vector3;

public class SpecHitbox {
	public String name;
	public long flags;
	public int type = -1; //-1 no convertable, 0 - mesh, 1 - cube
	public Vector3 position;
	public Vector3 rotation;
	public Vector3 scale;
	
	public String localMeshPath = null;
	public int localMeshNode = -3; //-3 - not renderable, -2 storage, -1 - render all, 0...9999 nodes
	
	public SpecHitbox[] children;
	
	public static class SHFlags {
		public static final long NO_COLLISION 		= (long)Math.pow(2, 1);  /*2        *//* No collideable object */
		public static final long NO_FREEZE	 		= (long)Math.pow(2, 4);  /*4        *//* Not freezeable by bullet */
		public static final long OBJECT_STATIC 		= (long)Math.pow(2, 2);  /*8        *//* Static object */
		public static final long OBJECT_DYNAMIC 	= (long)Math.pow(2, 3);  /*16       *//* Dynamic object */
		public static final long RESERVED5	 		= (long)Math.pow(2, 5);  /*32       *//*  */
		public static final long RESERVED6	 		= (long)Math.pow(2, 6);  /*64       *//*  */
		public static final long RESERVED7	 		= (long)Math.pow(2, 7);  /*128      *//*  */
		
		public static final long TRIGGER			= (long)Math.pow(2, 8);  /*256      *//* Trigger */
		public static final long RAYCASTABLE		= (long)Math.pow(2, 9);  /*512      *//* Raycastable */
		public static final long LISTEN_COLLISION	= (long)Math.pow(2, 10); /*1024     *//* Listen for collision */
		public static final long RESERVED11			= (long)Math.pow(2, 11); /*2048     *//*  */
		public static final long RESERVED12			= (long)Math.pow(2, 12); /*4096     *//*  */
		public static final long RESERVED13			= (long)Math.pow(2, 13); /*8192     *//*  */
		public static final long RESERVED14			= (long)Math.pow(2, 14); /*16384    *//*  */
		public static final long RESERVED15			= (long)Math.pow(2, 15); /*32768    *//*  */
		
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
}
