package by.fxg.specscenes;

import com.badlogic.gdx.math.Vector3;

public class Light {
	/** Light type index. Default converter: Point = 0, Directional = 1, Spot = 2 **/
	public int type;
	/** Transforms **/
	public Vector3 position, direction;
	/** Light parameters **/
	public float intensity, cutoffAngle, exponent;
}
