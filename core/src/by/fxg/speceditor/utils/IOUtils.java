package by.fxg.speceditor.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.Iterator;
import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.project.assets.ProjectAsset;
import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.std.g3d.TextureLinkedAttribute;

public class IOUtils {
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	private int writeCheckID = 0x12BADF00;
	private int readCheckID = 0x12BADF00;
	
	/** Очень хитровыебанная система индексирования аттрибутов.
	 * 
	 * 	Индексирование может проходить в несколько этапов,
	 * 	новые индексы добавляются по мере надобности, при этом
	 *  существующие уже есть в списке и не требуются для очередной
	 *  записи.
	 *  
	 *  [Indexing 10 of 10]
	 *  [saving]
	 *  ...
	 *  [Indexing 5 of 8]
	 *  [saving]
	 * **/
	private Array<Class<?>> attributeTypes = new Array<>();
	private Array<Class<?>> attributeTypesToWrite = new Array<>();
	
	public IOUtils(DataOutputStream dataOutputStream) {
		this.dataOutputStream = dataOutputStream;
	}
	
	public IOUtils(DataInputStream dataInputStream) {
		this.dataInputStream = dataInputStream;
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
	
	public void writeCheckID() throws IOException {
		this.dataOutputStream.writeInt(this.writeCheckID);
		this.writeCheckID += 1;
	}
	
	public void readCheckID() throws IOException {
		int id = this.dataInputStream.readInt();
		if (id != this.readCheckID) throw new UnsupportedOperationException("Format error on id: " + id + "/" + this.readCheckID);
		this.readCheckID++;
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
	
	public <TYPE extends Attributes> void writeAttributes(TYPE attributes) throws IOException {
		this.indexAttributes(attributes);
		this.writeAttributeIndexes();
		
		this.dataOutputStream.writeInt(attributes.size());
		Iterator<Attribute> iterator = attributes.iterator();
		while (iterator.hasNext()) {
			Attribute attribute = iterator.next();
			int index = this.attributeTypes.indexOf(attribute.getClass(), true);
			this.dataOutputStream.writeInt(index);
			if (index > -1) {
				this.dataOutputStream.writeUTF(Attribute.getAttributeAlias(attribute.type));
				this.serializeAttribute(attribute);
			}
		}
	}
	
	public <TYPE extends Attributes> void writeAttributesArray(Array<TYPE> attributesArray) throws IOException {
		for (int i = 0; i != attributesArray.size; i++) {
			this.indexAttributes(attributesArray.get(i));
		}
		this.writeAttributeIndexes();
		
		this.dataOutputStream.writeInt(attributesArray.size);
		for (int i = 0; i != attributesArray.size; i++) {
			Attributes attributes = attributesArray.get(i);
			int typeIndex = this.attributeTypes.indexOf(attributes.getClass(), true);
			this.dataOutputStream.writeInt(typeIndex);
			if (typeIndex > -1) {
				if (attributes instanceof Material) this.dataOutputStream.writeUTF(((Material)attributes).id);
				this.dataOutputStream.writeInt(attributes.size());
				Iterator<Attribute> iterator = attributes.iterator();
				while (iterator.hasNext()) {
					Attribute attribute = iterator.next();
					int index = this.attributeTypes.indexOf(attribute.getClass(), true);
					this.dataOutputStream.writeInt(index);
					if (index > -1) {
						this.dataOutputStream.writeUTF(Attribute.getAttributeAlias(attribute.type));
						this.serializeAttribute(attribute);
					}
				}
			}
		}
	}
	
	public <TYPE extends Attributes> void readAttributes(TYPE attributes) throws IOException {
		this.readAttributeIndexes();

		int size = this.dataInputStream.readInt();
		for (int i = 0; i != size; i++) {
			int index = this.dataInputStream.readInt();
			if (index > -1) {
				String attributeAlias = this.dataInputStream.readUTF();
				try {
					Attribute attribute = null;
					try { 
						attribute = (Attribute)this.attributeTypes.get(index).getConstructor(long.class).newInstance(Attribute.getAttributeType(attributeAlias));
					} catch (Exception e) {
						attribute = (Attribute)this.attributeTypes.get(index).newInstance();
					}
					this.deserializeAttribute(attribute);
					attributes.set(attribute);
				} catch (Exception e) {
					Utils.logError(e, "IOUtils#readAttributes");
					continue;
				}
			}
		}
	}
	
	public <TYPE extends Attributes> void readAttributesArray(Array<TYPE> attributesArray) throws IOException {
		this.readAttributeIndexes();

		int arraySize = this.dataInputStream.readInt();
		for (int i = 0; i != arraySize; i++) {
			int typeIndex = this.dataInputStream.readInt();
			if (typeIndex > -1) {
				try {
					Attributes attributes = (Attributes)this.attributeTypes.get(typeIndex).newInstance();
					if (attributes instanceof Material) {
						String materialID = this.dataInputStream.readUTF();
						for (int j = 0; j != attributesArray.size; j++) {
							if (attributesArray.get(j) != null && attributesArray.get(j) instanceof Material && ((Material)attributesArray.get(j)).id.equals(materialID)) {
								attributes = attributesArray.get(j);
								break;
							}
						}
						if (((Material)attributes).id == null) ((Material)attributes).id = materialID;
					}
					int size = this.dataInputStream.readInt();
					for (int j = 0; j != size; j++) {
						int index = this.dataInputStream.readInt();
						if (index > -1) {
							String attributeAlias = this.dataInputStream.readUTF();
							try {
								Attribute attribute = null;
								try { 
									attribute = (Attribute)this.attributeTypes.get(index).getConstructor(long.class).newInstance(Attribute.getAttributeType(attributeAlias));
								} catch (Exception e) {
									attribute = (Attribute)this.attributeTypes.get(index).newInstance();
								}
								this.deserializeAttribute(attribute);
								attributes.set(attribute);
							} catch (Exception e) {
								Utils.logError(e, "IOUtils#readAttributesArray | Attribute");
								continue;
							}
						}
					}
					if (!attributesArray.contains((TYPE)attributes, true)) attributesArray.add((TYPE)attributes);
				} catch (Exception e) {
					Utils.logError(e, "IOUtils#readAttributesArray | Attributes");
					continue;
				}
			}
		}
	}
	
	public void clearCache() {
		this.attributeTypes.clear();
		this.attributeTypesToWrite.clear();
	}
	
	/** This shit must serialize everything, Attributes and Attribute objects, crappy idea **/
	private void indexAttributes(Attributes attributes) {
		if (!this.attributeTypes.contains(attributes.getClass(), true)) {
			this.attributeTypes.add(attributes.getClass());
			this.attributeTypesToWrite.add(attributes.getClass());
		}
		
		Iterator<Attribute> iterator = attributes.iterator();
		while (iterator.hasNext()) {
			Attribute attribute = iterator.next();
			if (!this.attributeTypes.contains(attribute.getClass(), true)) {
				this.attributeTypes.add(attribute.getClass());
				this.attributeTypesToWrite.add(attribute.getClass());
			}
		}
	}
	
	private void writeAttributeIndexes() throws IOException {
		this.dataOutputStream.writeInt(this.attributeTypesToWrite.size);
		for (int i = 0; i != this.attributeTypesToWrite.size; i++) {
			this.dataOutputStream.writeUTF(this.attributeTypesToWrite.get(i).getTypeName());
		}
		this.attributeTypesToWrite.clear();
	}
	
	private void readAttributeIndexes() throws IOException {
		int attributesToRead = this.dataInputStream.readInt();
		for (int i = 0; i != attributesToRead; i++) {
			try {
				this.attributeTypes.add(Class.forName(this.dataInputStream.readUTF()));
			} catch (Exception e) {
				this.attributeTypes.add(null);
			}
		}
	}
	
	private void serializeAttribute(Attribute attribute) throws IOException {
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
		} else if (attribute instanceof TextureAttribute) {
			if (attribute instanceof TextureLinkedAttribute) {
				TextureLinkedAttribute textureLinkedAttribute = ((TextureLinkedAttribute)attribute);
				if (textureLinkedAttribute.asset != null) {
					this.dataOutputStream.writeBoolean(true);
					this.dataOutputStream.writeUTF(textureLinkedAttribute.asset.getUUID().toString());
				} else this.dataOutputStream.writeBoolean(false);
				this.dataOutputStream.writeBoolean(textureLinkedAttribute.flipX);
				this.dataOutputStream.writeBoolean(textureLinkedAttribute.flipY);
			} else throw new UnsupportedOperationException("Default TextureAttributes are not supported");
		}
	}
	
	private void deserializeAttribute(Attribute attribute) throws IOException {
		if (attribute instanceof BlendingAttribute) {
			BlendingAttribute blendingAttribute = new BlendingAttribute();
			blendingAttribute.blended = this.dataInputStream.readBoolean();
			blendingAttribute.sourceFunction = this.dataInputStream.readInt();
			blendingAttribute.destFunction = this.dataInputStream.readInt();
			blendingAttribute.opacity = this.dataInputStream.readFloat();
		} else if (attribute instanceof ColorAttribute) {
			((ColorAttribute)attribute).color.set(this.readColor());
		} else if (attribute instanceof DepthTestAttribute) {
			DepthTestAttribute depthTestAttribute = new DepthTestAttribute();
			depthTestAttribute.depthMask = this.dataInputStream.readBoolean();
			depthTestAttribute.depthFunc = this.dataInputStream.readInt();
			depthTestAttribute.depthRangeFar = this.dataInputStream.readFloat();
			depthTestAttribute.depthRangeNear = this.dataInputStream.readFloat();
		} else if (attribute instanceof FloatAttribute) {
			((FloatAttribute)attribute).value = this.dataInputStream.readFloat();
		} else if (attribute instanceof IntAttribute) {
			((IntAttribute)attribute).value = this.dataInputStream.readInt();
		} else if (attribute instanceof TextureLinkedAttribute) {
			if (attribute instanceof TextureLinkedAttribute) {
				TextureLinkedAttribute textureLinkedAttribute = ((TextureLinkedAttribute)attribute);
				if (this.dataInputStream.readBoolean()) {
					UUID uuid = UUID.fromString(this.dataInputStream.readUTF());
					ProjectAsset<?> projectAsset = ProjectAssetManager.INSTANCE.getAsset(uuid);
					if (projectAsset != null && projectAsset.getType().isAssignableFrom(Texture.class)) ((ProjectAsset<Texture>)projectAsset).addHandler(textureLinkedAttribute);
				}
				textureLinkedAttribute.setFlip(this.dataInputStream.readBoolean(), this.dataInputStream.readBoolean());
			} else throw new UnsupportedOperationException("Default TextureAttributes are not supported");
		}
	}
}
