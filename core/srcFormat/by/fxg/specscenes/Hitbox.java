package by.fxg.specscenes;

import com.badlogic.gdx.math.Vector3;

public class Hitbox {
	/** Flags of features implemented by Pilesos for bullet physics **/
	public long specFlags;
	/** Bullet physics collision flags **/
	public long bulletFlags;
	/** Transforms **/
	public Vector3 position, rotation, scale;
}
