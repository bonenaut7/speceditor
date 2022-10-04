package by.fxg.pilesos.specformat.graph;

import com.badlogic.gdx.math.Vector3;

public class SpecPointArray {
	public String name;
	public long flags;
	public Vector3[] points;
	
	public static class Flags {
		public static final long MODE_LOOP 			= (long)Math.pow(2, 1);  /*2        *//* Points are looped */
		public static final long MODE_LINE 			= (long)Math.pow(2, 2);  /*4        *//* Points are lined */
		
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
