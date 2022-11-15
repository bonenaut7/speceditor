package by.fxg.speceditor.serialization.gdx;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
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
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class GdxAttributeSerializers {
	public static boolean SERIALIZE_NOT_REGISTERED_ATTRIBUTES = false;
	static Array<Attribute> tmpAttributeArray = new Array<>();
	
	public static class AttributesSerializer extends Serializer<Attributes> {
		public void write(Kryo kryo, Output output, Attributes object) {
			Iterator<Attribute> iterator = object.iterator();
			if (SERIALIZE_NOT_REGISTERED_ATTRIBUTES) {
				output.writeInt(object.size());
				while (iterator.hasNext()) {
					kryo.writeClassAndObject(output, iterator.next());
				}
			} else {
				tmpAttributeArray.size = 0;
				while (iterator.hasNext()) {
					tmpAttributeArray.add(iterator.next());
					if (kryo.getClassResolver().getRegistration(tmpAttributeArray.get(tmpAttributeArray.size - 1).getClass()) == null) {
						tmpAttributeArray.removeIndex(tmpAttributeArray.size - 1);
					}
				}
				output.writeInt(tmpAttributeArray.size);
				for (int i = 0; i != tmpAttributeArray.size; i++) {
					kryo.writeClassAndObject(output, tmpAttributeArray.get(i));
				}
			}
		}

		public Attributes read(Kryo kryo, Input input, Class<Attributes> type) {
			Attributes attributes = new Attributes();
			Object tmp = null;
			int size = input.readInt();
			for (int i = 0; i != size; i++) {
				if ((tmp = kryo.readClassAndObject(input)) instanceof Attribute) {
					attributes.set((Attribute)tmp);
				}
			}
			return attributes;
		}
	}
	
	/** Shadow maps not supported :( FIXME Add shadowmap support for EnvironmentSerializer **/
	public static class EnvironmentSerializer extends Serializer<Environment> {
		public void write(Kryo kryo, Output output, Environment object) {
			Iterator<Attribute> iterator = object.iterator();
			if (SERIALIZE_NOT_REGISTERED_ATTRIBUTES) {
				output.writeInt(object.size());
				while (iterator.hasNext()) {
					kryo.writeClassAndObject(output, iterator.next());
				}
			} else {
				tmpAttributeArray.size = 0;
				while (iterator.hasNext()) {
					tmpAttributeArray.add(iterator.next());
					if (kryo.getClassResolver().getRegistration(tmpAttributeArray.get(tmpAttributeArray.size - 1).getClass()) == null) {
						tmpAttributeArray.removeIndex(tmpAttributeArray.size - 1);
					}
				}
				output.writeInt(tmpAttributeArray.size);
				for (int i = 0; i != tmpAttributeArray.size; i++) {
					kryo.writeClassAndObject(output, tmpAttributeArray.get(i));
				}
			}
		}

		public Environment read(Kryo kryo, Input input, Class<Environment> type) {
			Environment environment = new Environment();
			Object tmp = null;
			int size = input.readInt();
			for (int i = 0; i != size; i++) {
				if ((tmp = kryo.readClassAndObject(input)) instanceof Attribute) {
					environment.set((Attribute)tmp);
				}
			}
			return environment;
		}
	}
	
	public static class MaterialSerializer extends Serializer<Material> {
		public void write(Kryo kryo, Output output, Material object) {
			output.writeString(object.id);
			Iterator<Attribute> iterator = object.iterator();
			if (SERIALIZE_NOT_REGISTERED_ATTRIBUTES) {
				output.writeInt(object.size());
				while (iterator.hasNext()) {
					kryo.writeClassAndObject(output, iterator.next());
				}
			} else {
				tmpAttributeArray.size = 0;
				while (iterator.hasNext()) {
					tmpAttributeArray.add(iterator.next());
					if (kryo.getClassResolver().getRegistration(tmpAttributeArray.get(tmpAttributeArray.size - 1).getClass()) == null) {
						tmpAttributeArray.removeIndex(tmpAttributeArray.size - 1);
					}
				}
				output.writeInt(tmpAttributeArray.size);
				for (int i = 0; i != tmpAttributeArray.size; i++) {
					kryo.writeClassAndObject(output, tmpAttributeArray.get(i));
				}
			}
		}

		public Material read(Kryo kryo, Input input, Class<Material> type) {
			Material material = new Material(input.readString());
			Object tmp = null;
			int size = input.readInt();
			for (int i = 0; i != size; i++) {
				if ((tmp = kryo.readClassAndObject(input)) instanceof Attribute) {
					material.set((Attribute)tmp);
				}
			}
			return material;
		}
	}
	
	// ATTRIBUTES
	
	public static class BlendingAttributeSerializer extends Serializer<BlendingAttribute> {
		public void write(Kryo kryo, Output output, BlendingAttribute object) {
			output.writeBoolean(object.blended);
			output.writeInt(object.sourceFunction);
			output.writeInt(object.destFunction);
			output.writeFloat(object.opacity);
		}

		public BlendingAttribute read(Kryo kryo, Input input, Class<BlendingAttribute> type) {
			return new BlendingAttribute(input.readBoolean(), input.readInt(), input.readInt(), input.readFloat());
		}
	}
	
	public static class ColorAttributeSerializer extends Serializer<ColorAttribute> {
		public void write(Kryo kryo, Output output, ColorAttribute object) {
			output.writeString(Attribute.getAttributeAlias(object.type));
			kryo.writeObject(output, object.color);
		}

		public ColorAttribute read(Kryo kryo, Input input, Class<ColorAttribute> type) {
			return new ColorAttribute(Attribute.getAttributeType(input.readString()), kryo.readObject(input, Color.class));
		}
	}
	
	public static class DepthTestAttributeSerializer extends Serializer<DepthTestAttribute> {
		public void write(Kryo kryo, Output output, DepthTestAttribute object) {
			output.writeInt(object.depthFunc);
			output.writeFloat(object.depthRangeNear);
			output.writeFloat(object.depthRangeFar);
			output.writeBoolean(object.depthMask);
		}

		public DepthTestAttribute read(Kryo kryo, Input input, Class<DepthTestAttribute> type) {
			return new DepthTestAttribute(input.readInt(), input.readFloat(), input.readFloat(), input.readBoolean());
		}
	}
	
	public static class FloatAttributeSerializer extends Serializer<FloatAttribute> {
		public void write(Kryo kryo, Output output, FloatAttribute object) {
			output.writeString(Attribute.getAttributeAlias(object.type));
			output.writeFloat(object.value);
		}

		public FloatAttribute read(Kryo kryo, Input input, Class<FloatAttribute> type) {
			return new FloatAttribute(Attribute.getAttributeType(input.readString()), input.readFloat());
		}
	}
	
	public static class IntAttributeSerializer extends Serializer<IntAttribute> {
		public void write(Kryo kryo, Output output, IntAttribute object) {
			output.writeString(Attribute.getAttributeAlias(object.type));
			output.writeInt(object.value);
		}

		public IntAttribute read(Kryo kryo, Input input, Class<IntAttribute> type) {
			return new IntAttribute(Attribute.getAttributeType(input.readString()), input.readInt());
		}
	}
	
	@Deprecated
	public static class PointLightsAttributeSerializer extends Serializer<PointLightsAttribute> {
		public void write(Kryo kryo, Output output, PointLightsAttribute object) {}
		public PointLightsAttribute read(Kryo kryo, Input input, Class<PointLightsAttribute> type) { return null; }
	}
	
	@Deprecated
	public static class DirectionalLightsAttributeSerializer extends Serializer<DirectionalLightsAttribute> {
		public void write(Kryo kryo, Output output, DirectionalLightsAttribute object) {}
		public DirectionalLightsAttribute read(Kryo kryo, Input input, Class<DirectionalLightsAttribute> type) { return null; }
	}
	
	@Deprecated
	public static class SpotLightsAttributeSerializer extends Serializer<SpotLightsAttribute> {
		public void write(Kryo kryo, Output output, SpotLightsAttribute object) {}
		public SpotLightsAttribute read(Kryo kryo, Input input, Class<SpotLightsAttribute> type) { return null; }
	}
	
	// NOT IMPLEMENTED | Textures are not intended to be serialized *skull_emoji*
	
	@Deprecated //TODO search convenient way to serialize textures for cubemaps, implement Cubemaps serialization
	public static class CubemapAttributeSerializer extends Serializer<CubemapAttribute> {
		public void write(Kryo kryo, Output output, CubemapAttribute object) {}
		public CubemapAttribute read(Kryo kryo, Input input, Class<CubemapAttribute> type) { return null; }
	}
	
	@Deprecated
	public static class TextureAttributeSerializer extends Serializer<TextureAttribute> {
		public void write(Kryo kryo, Output output, TextureAttribute object) {}
		public TextureAttribute read(Kryo kryo, Input input, Class<TextureAttribute> type) { return null; }
	}
}
