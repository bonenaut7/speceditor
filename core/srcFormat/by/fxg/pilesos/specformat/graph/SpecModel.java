package by.fxg.pilesos.specformat.graph;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class SpecModel {
	public String name;
	public String modelPath;
	public Array<Material> materials;
	public Vector3 position;
	public Vector3 rotation;
	public Vector3 scale;
}