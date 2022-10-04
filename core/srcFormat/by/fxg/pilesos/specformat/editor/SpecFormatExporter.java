package by.fxg.pilesos.specformat.editor;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import by.fxg.pilesos.specformat.graph.SpecDecal;
import by.fxg.pilesos.specformat.graph.SpecGraph;
import by.fxg.pilesos.specformat.graph.SpecHitbox;
import by.fxg.pilesos.specformat.graph.SpecLight;
import by.fxg.pilesos.specformat.graph.SpecModel;
import by.fxg.pilesos.specformat.graph.SpecPointArray;
import by.fxg.speceditor.project.Project;
import by.fxg.speceditor.tools.g3d.TextureLinkedAttribute;

public class SpecFormatExporter {
	private static final byte[] magicNumber = {(byte)0, (byte)0xBA, (byte)0xDF, (byte)0};
	private static final int exportVersion = 1;
	
	public SpecFormatExporter export(Project project, FileHandle outFile, SpecGraph graph) {
		try {
			outFile.file().createNewFile();
			FileOutputStream fos = new FileOutputStream(outFile.file());
			DataOutputStream dos = new DataOutputStream(fos);
			
			int checkID = 0x12BADF00;
			dos.write(magicNumber);
			dos.writeInt(exportVersion);
			
			checkID = this.checkID(dos, checkID); //viewportData & first check
			this.writeColor(dos, graph.bufferClearColor);
			this.writeVector3(dos, graph.cameraSettings);
			dos.writeInt(graph.environmentAttributes.size);
			for (Attribute attribute : graph.environmentAttributes) {
				this.writeAttribute(dos, attribute, project.projectFolder);
			}
			
			checkID = this.checkID(dos, checkID); //hitboxes
			dos.writeInt(graph.hitboxes.size);
			for (SpecHitbox hitbox : graph.hitboxes) {
				this.writeRecursiveHitbox(dos, hitbox);
			}
			
			checkID = this.checkID(dos, checkID); //lights
			dos.writeInt(graph.lights.size);
			for (SpecLight light : graph.lights) {
				dos.writeUTF(light.name);
				dos.writeByte(light.lightType);
				this.writeColor(dos, light.color);
				this.writeVector3(dos, light.position);
				dos.writeFloat(light.intensity);
			}
			
			checkID = this.checkID(dos, checkID); //models
			dos.writeInt(graph.models.size);
			for (SpecModel model : graph.models) {
				dos.writeUTF(model.name);
				dos.writeUTF(model.modelPath == null ? "NO_MODEL" : model.modelPath);
				dos.writeInt(model.materials.size);
				for (Material material : model.materials) {
					dos.writeUTF(material.id);
					dos.writeInt(material.size());
					Iterator<Attribute> iterator = material.iterator(); //poor attributes...
					while (iterator.hasNext()) {
						this.writeAttribute(dos, iterator.next(), project.projectFolder);
					}
				}
				this.writeVector3(dos, model.position);
				this.writeVector3(dos, model.rotation);
				this.writeVector3(dos, model.scale);
			}
			
			checkID = this.checkID(dos, checkID); //decals
			dos.writeInt(graph.decals.size);
			for (SpecDecal decal : graph.decals) {
				dos.writeUTF(decal.name);
				dos.writeUTF(decal.texturePath == null ? "NO_PATH" : decal.texturePath);
				dos.writeBoolean(decal.isBillboard);
				this.writeVector3(dos, decal.position);
				this.writeVector3(dos, decal.rotation);
				this.writeVector2(dos, decal.scale);
			}
			
			checkID = this.checkID(dos, checkID); //points
			dos.writeInt(graph.points.size);
			for (SpecPointArray array : graph.points) {
				dos.writeUTF(array.name);
				dos.writeLong(array.flags);
				dos.writeInt(array.points.length);
				for (Vector3 vector : array.points) {
					this.writeVector3(dos, vector);
				}
			}
			
			dos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
	private void writeRecursiveHitbox(DataOutputStream dataOutputStream, SpecHitbox hitbox) throws IOException {
		dataOutputStream.writeUTF(hitbox.name);
		dataOutputStream.writeLong(hitbox.flags);
		dataOutputStream.writeInt(hitbox.type);
		dataOutputStream.writeUTF(hitbox.localMeshPath == null ? "NO_PATH" : hitbox.localMeshPath);
		dataOutputStream.writeInt(hitbox.localMeshNode);
		this.writeVector3(dataOutputStream, hitbox.position);
		this.writeVector3(dataOutputStream, hitbox.rotation);
		this.writeVector3(dataOutputStream, hitbox.scale);
		if (hitbox.children != null) {
			dataOutputStream.writeInt(hitbox.children.length);
			for (SpecHitbox hitbox$ : hitbox.children) this.writeRecursiveHitbox(dataOutputStream, hitbox$);
		} else dataOutputStream.writeInt(0);
	}
	
	private void writeAttribute(DataOutputStream dataOutputStream, Attribute attribute, FileHandle stripPath) throws IOException {
		dataOutputStream.writeUTF(Attribute.getAttributeAlias(attribute.type));
		if (attribute instanceof BlendingAttribute) {
			BlendingAttribute blendingAttribute = (BlendingAttribute)attribute;
			dataOutputStream.writeBoolean(blendingAttribute.blended);
			dataOutputStream.writeInt(blendingAttribute.sourceFunction);
			dataOutputStream.writeInt(blendingAttribute.destFunction);
			dataOutputStream.writeFloat(blendingAttribute.opacity);
		} else if (attribute instanceof ColorAttribute) {
			this.writeColor(dataOutputStream, ((ColorAttribute)attribute).color);
		} else if (attribute instanceof CubemapAttribute) {
			throw new UnsupportedOperationException("Cubemap not supported");
		} else if (attribute instanceof DepthTestAttribute) {
			DepthTestAttribute depthTestAttribute = (DepthTestAttribute)attribute;
			dataOutputStream.writeBoolean(depthTestAttribute.depthMask);
			dataOutputStream.writeInt(depthTestAttribute.depthFunc);
			dataOutputStream.writeFloat(depthTestAttribute.depthRangeFar);
			dataOutputStream.writeFloat(depthTestAttribute.depthRangeNear);
		} else if (attribute instanceof FloatAttribute) {
			dataOutputStream.writeFloat(((FloatAttribute)attribute).value);
		} else if (attribute instanceof IntAttribute) {
			dataOutputStream.writeInt(((IntAttribute)attribute).value);
		} else if (attribute instanceof DirectionalLightsAttribute) {
			throw new UnsupportedOperationException("DirectionalLights Array not supported");
		} else if (attribute instanceof PointLightsAttribute) {
			throw new UnsupportedOperationException("PointLights Array not supported");
		} else if (attribute instanceof SpotLightsAttribute) {
			throw new UnsupportedOperationException("SpotLights Array not supported");
		} else if (attribute instanceof TextureAttribute) {
			if (attribute instanceof TextureLinkedAttribute) {
				TextureLinkedAttribute textureLinkedAttribute = ((TextureLinkedAttribute)attribute);
				String path = textureLinkedAttribute.texturePath.path();
				path = path.contains(stripPath.path()) ? path.substring(stripPath.path().length() + 1) : path;
				dataOutputStream.writeUTF(textureLinkedAttribute.texturePath != null ? path : "-");
				dataOutputStream.writeBoolean(((TextureLinkedAttribute)attribute).flipX);
				dataOutputStream.writeBoolean(((TextureLinkedAttribute)attribute).flipY);
			} else throw new UnsupportedOperationException("Default TextureAttributes are not supported");
		}
	}
	
	private void writeVector2(DataOutputStream dataOutputStream, Vector2 vector) throws IOException {
		dataOutputStream.writeFloat(vector.x);
		dataOutputStream.writeFloat(vector.y);
	}
	
	private void writeVector3(DataOutputStream dataOutputStream, Vector3 vector) throws IOException {
		dataOutputStream.writeFloat(vector.x);
		dataOutputStream.writeFloat(vector.y);
		dataOutputStream.writeFloat(vector.z);
	}
	
	private void writeColor(DataOutputStream dataOutputStream, Color color) throws IOException {
		dataOutputStream.writeFloat(color.r);
		dataOutputStream.writeFloat(color.g);
		dataOutputStream.writeFloat(color.b);
		dataOutputStream.writeFloat(color.a);
	}
	
	private int checkID(DataOutputStream dataOutputStream, int checkID) throws IOException {
		dataOutputStream.writeInt(checkID);
		return checkID + 1;
	}
}
