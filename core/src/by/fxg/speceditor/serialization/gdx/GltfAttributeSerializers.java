package by.fxg.speceditor.serialization.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import net.mgsx.gltf.scene3d.attributes.FogAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFlagAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

public class GltfAttributeSerializers {
	public static class PBRFogAttributeSerializer extends Serializer<FogAttribute> {
		public void write(Kryo kryo, Output output, FogAttribute object) {
			output.writeString(Attribute.getAttributeAlias(object.type));
			output.writeFloat(object.value.x);
			output.writeFloat(object.value.y);
			output.writeFloat(object.value.z);
		}

		public FogAttribute read(Kryo kryo, Input input, Class<FogAttribute> type) {
			return new FogAttribute(Attribute.getAttributeType(input.readString())).set(input.readFloat(), input.readFloat(), input.readFloat());
		}
	}
	
	public static class PBRColorAttributeSerializer extends Serializer<PBRColorAttribute> {
		public void write(Kryo kryo, Output output, PBRColorAttribute object) {
			output.writeString(Attribute.getAttributeAlias(object.type));
			kryo.writeObject(output, object.color);
		}

		public PBRColorAttribute read(Kryo kryo, Input input, Class<PBRColorAttribute> type) {
			return new PBRColorAttribute(Attribute.getAttributeType(input.readString()), kryo.readObject(input, Color.class));
		}
	}
	
	public static class PBRFlagAttributeSerializer extends Serializer<PBRFlagAttribute> {
		public void write(Kryo kryo, Output output, PBRFlagAttribute object) {
			output.writeString(Attribute.getAttributeAlias(object.type));
		}

		public PBRFlagAttribute read(Kryo kryo, Input input, Class<PBRFlagAttribute> type) {
			return new PBRFlagAttribute(Attribute.getAttributeType(input.readString()));
		}
	}
	
	public static class PBRFloatAttributeSerializer extends Serializer<PBRFloatAttribute> {
		public void write(Kryo kryo, Output output, PBRFloatAttribute object) {
			output.writeString(Attribute.getAttributeAlias(object.type));
			output.writeFloat(object.value);
		}

		public PBRFloatAttribute read(Kryo kryo, Input input, Class<PBRFloatAttribute> type) {
			return new PBRFloatAttribute(Attribute.getAttributeType(input.readString()), input.readFloat());
		}
	}
	
	// NOT IMPLEMENTED | Textures are not intended to be serialized *skull_emoji*
	
	@Deprecated
	public static class PBRTextureAttributeSerializer extends Serializer<PBRTextureAttribute> {
		public void write(Kryo kryo, Output output, PBRTextureAttribute object) {}
		public PBRTextureAttribute read(Kryo kryo, Input input, Class<PBRTextureAttribute> type) { return null; }
	}
	
	@Deprecated //TODO
	public static class PBRCubemapAttributeSerializer extends Serializer<PBRCubemapAttribute> {
		public void write(Kryo kryo, Output output, PBRCubemapAttribute object) {}
		public PBRCubemapAttribute read(Kryo kryo, Input input, Class<PBRCubemapAttribute> type) { return null; }
	}
}
