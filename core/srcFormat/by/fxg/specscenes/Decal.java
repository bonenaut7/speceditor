package by.fxg.specscenes;

import java.util.UUID;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Decal {
	/** Index of asset that contains image for decal **/
	public UUID assetIndex;
	/** Flag is decal billboarded or not (facing to camera all the time) **/
	public boolean isBillboard;
	/** Transforms **/
	public Vector3 position, rotation;
	/** Scale transform **/
	public Vector2 scale;
}
