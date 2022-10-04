package by.fxg.pilesos.specformat.loader;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.specformat.graph.SpecDecal;
import by.fxg.pilesos.specformat.graph.SpecGraph;
import by.fxg.pilesos.specformat.graph.SpecHitbox;
import by.fxg.pilesos.specformat.graph.SpecLight;
import by.fxg.pilesos.specformat.graph.SpecModel;
import by.fxg.pilesos.specformat.graph.SpecPointArray;

public class SFSPostLoader {
	private static final byte[] magicNumber = {(byte)0, (byte)0xBA, (byte)0xDF, (byte)0};
	private static final int importVersion = 1;
	
	public SpecGraph loadGraph(FileHandle fileHandle) {
		try {
			SpecGraph graph = new SpecGraph();
			graph.environmentAttributes = new Array<>();
			graph.hitboxes = new Array<>();
			graph.lights = new Array<>();
			graph.models = new Array<>();
			graph.decals = new Array<>();
			graph.points = new Array<>();
			
			if (fileHandle.exists()) {
				ByteArrayInputStream bais = new ByteArrayInputStream(fileHandle.readBytes());
				DataInputStream dis = new DataInputStream(bais);
				
				int checkID = 0x12BADF00;
				byte[] magic = new byte[4];
				dis.read(magic);
				if (!Arrays.equals(magicNumber, magic)) return null;
				int version = dis.readInt();
				if (importVersion < version) return null;
				
				checkID = this.readCheckID(dis, checkID); //viewportData & first check
				graph.bufferClearColor = this.readColor(dis);
				graph.cameraSettings = this.readVector3(dis);
				int environmentAttributesSize = dis.readInt();
				for (int i = 0; i != environmentAttributesSize; i++) {
					graph.environmentAttributes.add(this.readAttribute(dis, fileHandle));
				}
				
				checkID = this.readCheckID(dis, checkID); //hitboxes
				int hitboxesSize = dis.readInt();
				for (int i = 0; i != hitboxesSize; i++) {
					SpecHitbox hitbox = this.readRecursiveHitbox(dis);
					if (hitbox != null) graph.hitboxes.add(hitbox);
				}
				
				checkID = this.readCheckID(dis, checkID); //lights
				int lightsSize = dis.readInt();
				for (int i = 0; i != lightsSize; i++) {
					SpecLight light = new SpecLight();
					light.name = dis.readUTF();
					light.lightType = dis.readUnsignedByte();
					light.color = this.readColor(dis);
					light.position = this.readVector3(dis);
					light.intensity = dis.readFloat();
					graph.lights.add(light);
				}
				
				checkID = this.readCheckID(dis, checkID); //models
				int modelsSize = dis.readInt();
				for (int i = 0; i != modelsSize; i++) {
					SpecModel model = new SpecModel();
					model.name = dis.readUTF();
					model.modelPath = dis.readUTF();
					model.materials = new Array<>();
					int materialsSize = dis.readInt();
					for (int j = 0; j != materialsSize; j++) {
						Material material = new Material(dis.readUTF());
						int materialAttributesSize = dis.readInt();
						for (int k = 0; k != materialAttributesSize; k++) {
							material.set(this.readAttribute(dis, fileHandle.parent()));
						}
						model.materials.add(material);
					}
					model.position = this.readVector3(dis);
					model.rotation = this.readVector3(dis);
					model.scale = this.readVector3(dis);
					if (!model.modelPath.equals("NO_MODEL")) graph.models.add(model);
				}
				
				checkID = this.readCheckID(dis, checkID); //decals
				int decalsSize = dis.readInt();
				for (int i = 0; i != decalsSize; i++) {
					SpecDecal decal = new SpecDecal();
					decal.name = dis.readUTF();
					decal.texturePath = dis.readUTF();
					decal.isBillboard = dis.readBoolean();
					decal.position = this.readVector3(dis);
					decal.rotation = this.readVector3(dis);
					decal.scale = this.readVector2(dis);
					if (!decal.texturePath.equals("NO_PATH")) decal.handle = fileHandle.parent().child(decal.texturePath);
					graph.decals.add(decal);
				}
				
				checkID = this.readCheckID(dis, checkID); //points
				int pointsSize = dis.readInt();
				for (int i = 0; i != pointsSize; i++) {
					SpecPointArray array = new SpecPointArray();
					array.name = dis.readUTF();
					array.flags = dis.readLong();
					array.points = new Vector3[dis.readInt()];
					for (int j = 0; j != array.points.length; j++) {
						array.points[j] = this.readVector3(dis);
					}
					graph.points.add(array);
				}
				
				graph.rootPath = fileHandle.parent();
				
				dis.close();
				bais.close();
			}
			return graph;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private SpecHitbox readRecursiveHitbox(DataInputStream dataInputStream) throws IOException {
		SpecHitbox hitbox = new SpecHitbox();
		hitbox.name = dataInputStream.readUTF();
		hitbox.flags = dataInputStream.readLong();
		hitbox.type = dataInputStream.readInt();
		hitbox.localMeshPath = dataInputStream.readUTF();
		if (hitbox.type == -1 && hitbox.localMeshPath.equals("NO_PATH")) return null;
		hitbox.localMeshNode = dataInputStream.readInt();
		hitbox.position = this.readVector3(dataInputStream);
		hitbox.rotation = this.readVector3(dataInputStream);
		hitbox.scale = this.readVector3(dataInputStream);
		int childrenSize = dataInputStream.readInt();
		Array<SpecHitbox> children = new Array<>();
		for (int i = 0; i != childrenSize; i++) {
			SpecHitbox hitbox$ = this.readRecursiveHitbox(dataInputStream);
			if (hitbox$ != null) children.add(hitbox$);
		}
		hitbox.children = children.toArray(SpecHitbox.class);
		return hitbox;
	}
	
	public Attribute readAttribute(DataInputStream dataInputStream, FileHandle childPath) throws IOException {
		String alias = dataInputStream.readUTF();
		switch (alias) {
			case "blended": {
				BlendingAttribute attribute = new BlendingAttribute();
				attribute.blended = dataInputStream.readBoolean();
				attribute.sourceFunction = dataInputStream.readInt();
				attribute.destFunction = dataInputStream.readInt();
				attribute.opacity = dataInputStream.readFloat();
				return attribute;
			}
			case "diffuseColor":
			case "specularColor":
			case "ambientColor":
			case "emissiveColor":
			case "reflectionColor":
			case "ambientLightColor":
			case "fogColor": {	
				return new ColorAttribute(Attribute.getAttributeType(alias), this.readColor(dataInputStream));
			}
			case "depthStencil": { //DepthTestAttribute
				DepthTestAttribute attribute = new DepthTestAttribute();
				attribute.depthMask = dataInputStream.readBoolean();
				attribute.depthFunc = dataInputStream.readInt();
				attribute.depthRangeFar = dataInputStream.readFloat();
				attribute.depthRangeNear = dataInputStream.readFloat();
				return attribute;
			}
			case "shininess": //FloatAttribute
			case "alphaTest": {
				return new FloatAttribute(Attribute.getAttributeType(alias), dataInputStream.readFloat());
			}
			case "cullface": { //IntAttribute
				return new IntAttribute(Attribute.getAttributeType(alias), dataInputStream.readInt());
			}
			case "diffuseTexture": //TextureLinkedAttribute(TextureAttribute, TA not supported, TLA placed instead)
			case "specularTexture":
			case "bumpTexture":
			case "normalTexture":
			case "ambientTexture":
			case "emissiveTexture":
			case "reflectionTexture": {
				String path = dataInputStream.readUTF();
				FileHandle handle = path.equals("-") ? null : childPath.child(path);
				boolean flipX = dataInputStream.readBoolean(), flipY = dataInputStream.readBoolean();
				if (handle == null) return null;
				TextureRegion region = new TextureRegion(new Texture(handle));
				region.flip(flipX, flipY);
				return new TextureAttribute(Attribute.getAttributeType(alias), region);
			}
			
			case "environmentCubemap": break; //CubemapAttribute. Cubemaps not supported
			case "directionalLights": break; //DirectionalLightsAttribute. Not supported
			case "pointLights": break; //PointLightsAttribute. Not supported
			case "spotLights": break; //SpotLightsAttribute. Not supported
		}
		return null;
	}
	
	private Vector2 readVector2(DataInputStream dataInputStream) throws IOException {
		return new Vector2(dataInputStream.readFloat(), dataInputStream.readFloat());
	}
	
	private Vector3 readVector3(DataInputStream dataInputStream) throws IOException {
		return new Vector3(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat());
	}
	
	private Color readColor(DataInputStream dataInputStream) throws IOException {
		return new Color(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat());
	}
	
	private int readCheckID(DataInputStream dataInputStream, int checkID) throws IOException {
		int id = dataInputStream.readInt();
		if (id != checkID) throw new UnsupportedOperationException("Format error on id: " + id + "/" + checkID);
		return checkID + 1;
	}
}
