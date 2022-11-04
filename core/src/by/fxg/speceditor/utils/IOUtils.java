package by.fxg.speceditor.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
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
import com.badlogic.gdx.utils.Array;

public class IOUtils {
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	private Array<Class<?>> attributesTypes = new Array<>();
	private int writeCheckID = 0x12BADF00;
	private int readCheckID = 0x12BADF00;
	
	public IOUtils(DataOutputStream dataOutputStream) {
		this.dataOutputStream = dataOutputStream;
	}
	
	public IOUtils(DataInputStream dataInputStream) {
		this.dataInputStream = dataInputStream;
	}
	
	public void writeVector2(Vector2 vector) throws IOException {
		this.dataOutputStream.writeFloat(vector.x);
		this.dataOutputStream.writeFloat(vector.y);
	}
	
	public Vector2 readVector2() throws IOException {
		return new Vector2(this.dataInputStream.readFloat(), this.dataInputStream.readFloat());
	}
	
	public void writeVector3(Vector3 vector) throws IOException {
		this.dataOutputStream.writeFloat(vector.x);
		this.dataOutputStream.writeFloat(vector.y);
		this.dataOutputStream.writeFloat(vector.z);
	}
	
	public Vector3 readVector3() throws IOException {
		return new Vector3(this.dataInputStream.readFloat(), this.dataInputStream.readFloat(), this.dataInputStream.readFloat());
	}
	
	public void writeColor(Color color) throws IOException {
		this.dataOutputStream.writeFloat(color.r);
		this.dataOutputStream.writeFloat(color.g);
		this.dataOutputStream.writeFloat(color.b);
		this.dataOutputStream.writeFloat(color.a);
	}
	
	public Color readColor() throws IOException {
		return new Color(this.dataInputStream.readFloat(), this.dataInputStream.readFloat(), this.dataInputStream.readFloat(), this.dataInputStream.readFloat());
	}
	
	public void writeAttribute(Attribute attribute) throws IOException {
		this.dataOutputStream.writeUTF(Attribute.getAttributeAlias(attribute.type));
		if (attribute instanceof BlendingAttribute) {
			BlendingAttribute blendingAttribute = (BlendingAttribute)attribute;
			this.dataOutputStream.writeBoolean(blendingAttribute.blended);
			this.dataOutputStream.writeInt(blendingAttribute.sourceFunction);
			this.dataOutputStream.writeInt(blendingAttribute.destFunction);
			this.dataOutputStream.writeFloat(blendingAttribute.opacity);
		} else if (attribute instanceof ColorAttribute) {
			this.writeColor(((ColorAttribute)attribute).color);
		} else if (attribute instanceof CubemapAttribute) {
			throw new UnsupportedOperationException("Cubemap not supported");
		} else if (attribute instanceof DepthTestAttribute) {
			DepthTestAttribute depthTestAttribute = (DepthTestAttribute)attribute;
			this.dataOutputStream.writeBoolean(depthTestAttribute.depthMask);
			this.dataOutputStream.writeInt(depthTestAttribute.depthFunc);
			this.dataOutputStream.writeFloat(depthTestAttribute.depthRangeFar);
			this.dataOutputStream.writeFloat(depthTestAttribute.depthRangeNear);
		} else if (attribute instanceof FloatAttribute) {
			this.dataOutputStream.writeFloat(((FloatAttribute)attribute).value);
		} else if (attribute instanceof IntAttribute) {
			this.dataOutputStream.writeInt(((IntAttribute)attribute).value);
		} else if (attribute instanceof DirectionalLightsAttribute) {
			throw new UnsupportedOperationException("DirectionalLights Array not supported");
		} else if (attribute instanceof PointLightsAttribute) {
			throw new UnsupportedOperationException("PointLights Array not supported");
		} else if (attribute instanceof SpotLightsAttribute) {
			throw new UnsupportedOperationException("SpotLights Array not supported");
		} else if (attribute instanceof TextureAttribute) {
//			if (attribute instanceof TextureLinkedAttribute) {
//				TextureLinkedAttribute textureLinkedAttribute = ((TextureLinkedAttribute)attribute);
//				FileHandle handle = this.projectFolder;
//				String path = textureLinkedAttribute.texturePath.path();
//				path = path.contains(handle.path()) ? path.substring(handle.path().length() + 1) : path;
//				this.dataOutputStream.writeUTF(textureLinkedAttribute.texturePath != null ? path : "-");
//				this.dataOutputStream.writeBoolean(((TextureLinkedAttribute)attribute).flipX);
//				this.dataOutputStream.writeBoolean(((TextureLinkedAttribute)attribute).flipY);
//			} else throw new UnsupportedOperationException("Default TextureAttributes are not supported");
		}
	}
	
	public Attribute readAttribute() throws IOException {
		String alias = this.dataInputStream.readUTF();
		switch (alias) {
			case "blended": {
				BlendingAttribute attribute = new BlendingAttribute();
				attribute.blended = this.dataInputStream.readBoolean();
				attribute.sourceFunction = this.dataInputStream.readInt();
				attribute.destFunction = this.dataInputStream.readInt();
				attribute.opacity = this.dataInputStream.readFloat();
				return attribute;
			}
			case "diffuseColor":
			case "specularColor":
			case "ambientColor":
			case "emissiveColor":
			case "reflectionColor":
			case "ambientLightColor":
			case "fogColor": {	
				return new ColorAttribute(Attribute.getAttributeType(alias), this.readColor());
			}
			case "depthStencil": { //DepthTestAttribute
				DepthTestAttribute attribute = new DepthTestAttribute();
				attribute.depthMask = this.dataInputStream.readBoolean();
				attribute.depthFunc = this.dataInputStream.readInt();
				attribute.depthRangeFar = this.dataInputStream.readFloat();
				attribute.depthRangeNear = this.dataInputStream.readFloat();
				return attribute;
			}
			case "shininess": //FloatAttribute
			case "alphaTest": {
				return new FloatAttribute(Attribute.getAttributeType(alias), this.dataInputStream.readFloat());
			}
			case "cullface": { //IntAttribute
				return new IntAttribute(Attribute.getAttributeType(alias), this.dataInputStream.readInt());
			}
			case "diffuseTexture": //TextureLinkedAttribute(TextureAttribute, TA not supported, TLA placed instead)
			case "specularTexture":
			case "bumpTexture":
			case "normalTexture":
			case "ambientTexture":
			case "emissiveTexture":
			case "reflectionTexture": {
//				String path = this.dataInputStream.readUTF();
//				FileHandle handle = path.equals("-") ? null : this.projectFolder.child(path);
//				TextureRegion region = new TextureRegion(handle == null ? ResourceManager.standardTexture : new Texture(handle));
//				boolean flipX = this.dataInputStream.readBoolean(), flipY = this.dataInputStream.readBoolean();
//				region.flip(flipX, flipY);
//				return new TextureLinkedAttribute(Attribute.getAttributeType(alias), region, handle).setFlip(flipX, flipY);
			}
			
			case "environmentCubemap": break; //CubemapAttribute. Cubemaps not supported
			case "directionalLights": break; //DirectionalLightsAttribute. Not supported due to lights saving from ObjectTree
			case "pointLights": break; //PointLightsAttribute. Not supported due to lights saving from ObjectTree
			case "spotLights": break; //SpotLightsAttribute. Not supported due to lights saving from ObjectTree
		}
		return null;
	}
	
	/* save in mass-arrays(Array with AttributeS) or in single AttributeS
	Iterator<Attribute> iterator = attributes.iterator();
	while (iterator.hasNext()) {
		Attribute attribute = iterator.next();
		if (!types.contains(attribute.getClass(), true)) {
			types.add(attribute.getClass());
		}
	}
	*/
	
	public Array<Class<?>> getAttributesTypes() {
		return this.attributesTypes;
	}
	
	public void writeCheckID() throws IOException {
		this.dataOutputStream.writeInt(this.writeCheckID);
		this.writeCheckID += 1;
	}
	
	public void readCheckID() throws IOException {
		int id = this.dataInputStream.readInt();
		if (id != this.readCheckID) throw new UnsupportedOperationException("Format error on id: " + id + "/" + this.readCheckID);
		this.readCheckID++;
	}
	
	/** Calculate the length, in bytes, of a <code>String</code> in Utf8 format. <br>
	 *  See also: {@link DataOutputStream#writeUTF(String)}
	 *  @param value The <code>String</code> to measure
	 *  @param start String index at which to begin count
	 *  @param sum Starting Utf8 byte count
	 *  @throws UTFDataFormatException if result would exceed 65535 */
	public int getUTFlength(String value, int start, int sum) throws UTFDataFormatException {
		int len = value.length();
		for (int i = start; i < len && sum <= 65535; ++i) {
			char c = value.charAt(i);
			if (c >= '\u0001' && c <= '\u007f')	sum += 1;
			else if (c == '\u0000' || (c >= '\u0080' && c <= '\u07ff')) sum += 2;
			else sum += 3;
		}
		if (sum > 65535) throw new UTFDataFormatException();
		return sum;
	}
}
