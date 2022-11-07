package by.fxg.specscenes;

import java.util.UUID;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Model {
	/** Index of asset that contains model **/
	public UUID assetIndex;
	/** Materials that must be applied to the model **/
	public Array<Material> materials;
	/** Transforms **/
	public Vector3 position, rotation, scale;
}
