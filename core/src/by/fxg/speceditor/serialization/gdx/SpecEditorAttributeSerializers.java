package by.fxg.speceditor.serialization.gdx;

import java.util.UUID;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import by.fxg.speceditor.project.assets.ProjectAsset;
import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.std.g3d.attributes.SpecPBRTextureAttribute;
import by.fxg.speceditor.std.g3d.attributes.SpecTextureAttribute;

public class SpecEditorAttributeSerializers {
	public static class SpecTextureAttributeSerializer extends Serializer<SpecTextureAttribute> {
		public void write(Kryo kryo, Output output, SpecTextureAttribute object) {
			output.writeString(Attribute.getAttributeAlias(object.type));
			output.writeBoolean(object.flipX);
			output.writeBoolean(object.flipY);
			if (object.asset != null) {
				output.writeBoolean(true);
				output.writeString(object.asset.getUUID().toString());
			} else output.writeBoolean(false);
		}

		public SpecTextureAttribute read(Kryo kryo, Input input, Class<SpecTextureAttribute> type) {
			SpecTextureAttribute attribute = new SpecTextureAttribute(Attribute.getAttributeType(input.readString()));
			attribute.setFlip(input.readBoolean(), input.readBoolean());
			if (input.readBoolean()) {
				UUID uuid = UUID.fromString(input.readString());
				ProjectAsset<Texture> projectAsset = ProjectAssetManager.INSTANCE.getAsset(Texture.class, uuid);
				if (projectAsset != null) projectAsset.addHandler(attribute);
			}
			return attribute;
		}
	}
	
	public static class SpecPBRTextureAttributeSerializer extends Serializer<SpecPBRTextureAttribute> {
		public void write(Kryo kryo, Output output, SpecPBRTextureAttribute object) {
			output.writeString(Attribute.getAttributeAlias(object.type));
			output.writeBoolean(object.flipX);
			output.writeBoolean(object.flipY);
			if (object.asset != null) {
				output.writeBoolean(true);
				output.writeString(object.asset.getUUID().toString());
			} else output.writeBoolean(false);
		}

		public SpecPBRTextureAttribute read(Kryo kryo, Input input, Class<SpecPBRTextureAttribute> type) {
			SpecPBRTextureAttribute attribute = new SpecPBRTextureAttribute(Attribute.getAttributeType(input.readString()));
			attribute.setFlip(input.readBoolean(), input.readBoolean());
			if (input.readBoolean()) {
				UUID uuid = UUID.fromString(input.readString());
				ProjectAsset<Texture> projectAsset = ProjectAssetManager.INSTANCE.getAsset(Texture.class, uuid);
				if (projectAsset != null) projectAsset.addHandler(attribute);
			}
			return attribute;
		}
	}
}
