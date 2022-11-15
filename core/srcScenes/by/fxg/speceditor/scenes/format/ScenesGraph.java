package by.fxg.speceditor.scenes.format;

import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ScenesGraph {
	public Color bufferClearColor = new Color(0, 0, 0, 1);
	public Vector3 cameraParameters = new Vector3(67.0F, 50.0F, 0.01F);
	public Environment environment = new Environment();
	
	public Array<Light> lights = new Array<>();
	public Array<Hitbox> hitboxes = new Array<>();
	public Array<Decal> decals = new Array<>();
	public Array<Model> models = new Array<>();
	
	public static class Decal {
		/** Object name **/
		public String name;
		/** Index of asset that contains image for decal **/
		public UUID assetIndex;
		/** Flag is decal billboarded or not (facing to camera all the time) **/
		public boolean isBillboard;
		/** Transforms **/
		public Vector3 position, rotation;
		/** Scale transform **/
		public Vector2 scale;
	}	

	public static class Light {
		/** Object name **/
		public String name;
		/** Light type index. Default converter: Point = 0, Directional = 1, Spot = 2 **/
		public int type;
		/** Light color **/
		public Color color;
		/** Transforms **/
		public Vector3 position, direction;
		/** Light parameters **/
		public float intensity, cutoffAngle, exponent;
	}
	
	public static class Model {
		/** Object name **/
		public String name;
		/** Index of asset that contains model **/
		public UUID assetIndex;
		/** Materials that must be applied to the model **/
		public Array<Material> materials;
		/** Transforms **/
		public Vector3 position, rotation, scale;
	}
	
	public static class Hitbox {
		/** Object name **/
		public String name;
		/** Flags of features implemented by Pilesos for bullet physics **/
		public long specFlags;
		/** Bullet physics collision flags **/
		public long bulletFlags;
		/** Bullet physics filter mask **/
		public long bulletFilterMask;
		/** Bullet physics filter group **/
		public long bulletFilterGroup;
		/** Transforms **/
		public Vector3 position, rotation, scale;
	}
	
	public static class HitboxMesh extends Hitbox {
		/** Index of asset that contains mesh data for generating hitbox **/
		public UUID assetIndex;
		/** Nodes for generating hitbox. <br>
		 * null - not generate, int[] with 0 length - generate from all nodes, in other case use id's flags from the array **/
		public boolean[] nodes;
	}
	
	public static class HitboxStack extends Hitbox {
		/** if false - should combine all objects into one shape **/
		public boolean isArrayHitbox;
		/** Stack's children **/
		public Hitbox[] children;
	}

}
