package by.fxg.specscenes;

public class HitboxStack extends Hitbox {
	/** In the editor called as isArrayHitbox, if false - should combine all objects into one shape **/
	public boolean isComplexShape;
	/** Stack's children **/
	public Hitbox[] children;
}
