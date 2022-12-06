package by.fxg.speceditor.scenes.format;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ScenesNodeGraph {
	public Color bufferClearColor = new Color(0, 0, 0, 1);
	public Vector3 cameraParameters = new Vector3(67.0F, 50.0F, 0.01F);
	public Environment environment = new Environment();
	
	public Array<NodeLight> lights = new Array<>();
	public Array<NodeHitbox> hitboxes = new Array<>();
	public Array<NodeDecal> decals = new Array<>();
	public Array<NodeModel> models = new Array<>();
	
	public static class NodeDecal {
		/** Object name **/
		public String name;
		/** Indexes of asset that contains image for decal **/
		public String pakArchive, pakAsset;
		/** Flag is decal billboarded or not (facing to camera all the time) **/
		public boolean isBillboard;
		/** Transforms **/
		public Vector3 position, rotation;
		/** Scale transform **/
		public Vector2 scale;
	}	

	public static class NodeLight {
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
	
	public static class NodeModel {
		/** Object name **/
		public String name;
		/** Indexes of asset that contains model **/
		public String pakArchive, pakAsset;
		/** Materials that must be applied to the model **/
		public Array<Material> materials;
		/** Transforms **/
		public Vector3 position, rotation, scale;
	}
	
	public static class NodeHitbox {
		/** Object name **/
		public String name;
		/** Flags of features implemented by Pilesos for bullet physics **/
		public long specFlags;
		
		/** Bullet physics collision flags **/
		public int bulletFlags;
		/** Bullet physics activation state **/
		public int bulletActivationState;
		/** Bullet physics filter mask **/
		public int bulletFilterMask;
		/** Bullet physics filter group **/
		public int bulletFilterGroup;
		
		/** Transforms **/
		public Vector3 position, rotation, scale;
	}
	
	public static class NodeHitboxMesh extends NodeHitbox {
		/** Indexes of asset that contains mesh data for generating hitbox **/
		public String pakArchive, pakAsset;
		/** Nodes for generating hitbox. <br>
		 * null - not generate, int[] with 0 length - generate from all nodes, in other case use id's flags from the array **/
		public boolean[] nodes;
	}
	
	public static class NodeHitboxStack extends NodeHitbox {
		/** if false - should combine all objects into one shape **/
		public boolean isArrayHitbox;
		/** Stack's children **/
		public NodeHitbox[] children;
	}
}
