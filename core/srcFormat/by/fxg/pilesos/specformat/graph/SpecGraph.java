package by.fxg.pilesos.specformat.graph;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class SpecGraph {
	public FileHandle rootPath = null; //root path used only in scene loading 
	
	public Color bufferClearColor;
	public Vector3 cameraSettings; //FOV, FAR, NEAR
	public Array<Attribute> environmentAttributes;
	
	public Array<SpecModel> models;
	public Array<SpecLight> lights;
	public Array<SpecDecal> decals;
	public Array<SpecHitbox> hitboxes;
	public Array<SpecPointArray> points;
}
