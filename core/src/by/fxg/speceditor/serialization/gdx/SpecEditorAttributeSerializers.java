package by.fxg.speceditor.serialization.gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.project.assets.SpakAsset;
import by.fxg.speceditor.std.g3d.attributes.SpecPBRTextureAttribute;
import by.fxg.speceditor.std.g3d.attributes.SpecTextureAttribute;
import by.fxg.speceditor.utils.Utils;

public class SpecEditorAttributeSerializers {
	public static class SpecTextureAttributeSerializer extends Serializer<SpecTextureAttribute> {
		public void write(Kryo kryo, Output output, SpecTextureAttribute object) {
			output.writeString(Attribute.getAttributeAlias(object.type));
			output.writeBoolean(object.flipX);
			output.writeBoolean(object.flipY);
			if (object.asset != null) {
				output.writeBoolean(true);
				output.writeString(object.asset.getArchive().getName());
				output.writeString(object.asset.getPath());
			} else output.writeBoolean(false);
		}

		public SpecTextureAttribute read(Kryo kryo, Input input, Class<SpecTextureAttribute> type) {
			SpecTextureAttribute attribute = new SpecTextureAttribute(Attribute.getAttributeType(input.readString()));
			attribute.setFlip(input.readBoolean(), input.readBoolean());
			if (input.readBoolean()) {
				String pakArchive = input.readString();
				String pakAsset = input.readString();
				SpakAsset asset = ProjectAssetManager.INSTANCE.getPakAsset(pakArchive, pakAsset);
				if (asset != null) {
					if (asset.getType() == Texture.class) {
						asset.addUser(attribute);
					} else Utils.logWarn("Deserialization", "SpecTextureAttribute Asset `", pakAsset, "`(`", pakArchive, "`) type incorrect `", asset.getType().getSimpleName(), "`. Required: Texture. Removing asset for now...");
				} else Utils.logWarn("Deserialization", "SpecTextureAttribute can't get asset `", pakAsset, "`(`", pakArchive, "`). Removing asset for now...");
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
				output.writeString(object.asset.getArchive().getName());
				output.writeString(object.asset.getPath());
			} else output.writeBoolean(false);
		}

		public SpecPBRTextureAttribute read(Kryo kryo, Input input, Class<SpecPBRTextureAttribute> type) {
			SpecPBRTextureAttribute attribute = new SpecPBRTextureAttribute(Attribute.getAttributeType(input.readString()));
			attribute.setFlip(input.readBoolean(), input.readBoolean());
			if (input.readBoolean()) {
				String pakArchive = input.readString();
				String pakAsset = input.readString();
				SpakAsset asset = ProjectAssetManager.INSTANCE.getPakAsset(pakArchive, pakAsset);
				if (asset != null) {
					if (asset.getType() == Texture.class) {
						asset.addUser(attribute);
					} else Utils.logWarn("Deserialization", "SpecPBRTextureAttribute's Asset `", pakAsset, "`(`", pakArchive, "`) type incorrect `", asset.getType().getSimpleName(), "`. Required: Texture. Removing asset for now...");
				} else Utils.logWarn("Deserialization", "SpecPBRTextureAttribute can't get asset `", pakAsset, "`(`", pakArchive, "`). Removing asset for now...");
			}
			return attribute;
		}
	}
}
