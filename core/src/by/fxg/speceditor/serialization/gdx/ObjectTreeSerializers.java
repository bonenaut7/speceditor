package by.fxg.speceditor.serialization.gdx;

import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import by.fxg.speceditor.project.assets.ProjectAsset;
import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.objectTree.ElementStack;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.objectTree.elements.ElementDecal;
import by.fxg.speceditor.std.objectTree.elements.ElementFolder;
import by.fxg.speceditor.std.objectTree.elements.ElementHitbox;
import by.fxg.speceditor.std.objectTree.elements.ElementHitboxStack;
import by.fxg.speceditor.std.objectTree.elements.ElementLight;
import by.fxg.speceditor.std.objectTree.elements.ElementLight.ElementLightType;
import by.fxg.speceditor.std.objectTree.elements.ElementModel;
import by.fxg.speceditor.utils.Utils;

public class ObjectTreeSerializers {
	public static class ElementStackSerializer extends Serializer<ElementStack> {
		public void write(Kryo kryo, Output output, ElementStack object) {
			Array<TreeElement> elements = object.getElements();
			output.writeInt(elements.size);
			for (int i = 0; i != elements.size; i++) {
				kryo.writeClassAndObject(output, elements.get(i));
			}
		}

		public ElementStack read(Kryo kryo, Input input, Class<ElementStack> type) {
			ElementStack stack = new ElementStack();
			int size = input.readInt();
			for (int i = 0; i != size; i++) {
				stack.add((TreeElement)kryo.readClassAndObject(input));
			}
			return stack;
		}
	}
	
	public static class ElementFolderSerializer extends Serializer<ElementFolder> {
		public void write(Kryo kryo, Output output, ElementFolder object) {
			output.writeString(object.getName());
			output.writeBoolean(object.isVisible());
			output.writeBoolean(object.isFolderOpened());
			kryo.writeObject(output, object.getFolderStack());
		}

		public ElementFolder read(Kryo kryo, Input input, Class<ElementFolder> type) {
			ElementFolder elementFolder = new ElementFolder(input.readString());
			elementFolder.setVisible(input.readBoolean());
			elementFolder.setFolderOpened(input.readBoolean());
			elementFolder.setFolderStack(kryo.readObject(input, ElementStack.class));
			return elementFolder;
		}
	}
	
	public static class ElementLightSerializer extends Serializer<ElementLight> {
		public void write(Kryo kryo, Output output, ElementLight object) {
			output.writeString(object.getName());
			output.writeBoolean(object.isVisible());
			output.writeString(object.type.name());
			switch (object.type) {
				case POINT: {
					kryo.writeObject(output, object.getLight(PointLight.class).color);
					kryo.writeObject(output, object.getLight(PointLight.class).position);
					output.writeFloat(object.getLight(PointLight.class).intensity);
				} break;
				case SPOT: {
					kryo.writeObject(output, object.getLight(SpotLight.class).color);
					kryo.writeObject(output, object.getLight(SpotLight.class).position);
					kryo.writeObject(output, object.getLight(SpotLight.class).direction);
					output.writeFloat(object.getLight(SpotLight.class).intensity);
					output.writeFloat(object.getLight(SpotLight.class).cutoffAngle);
					output.writeFloat(object.getLight(SpotLight.class).exponent);
				} break;
			}
		}

		public ElementLight read(Kryo kryo, Input input, Class<ElementLight> type) {
			ElementLight elementLight = new ElementLight(input.readString());
			elementLight.setVisible(input.readBoolean());
			elementLight.type = ElementLightType.valueOf(input.readString());
			switch (elementLight.type) {
				case POINT: {
					PointLight pointLight = new PointLight();
					pointLight.color.set(kryo.readObject(input, Color.class));
					pointLight.position.set(kryo.readObject(input, Vector3.class));
					pointLight.intensity = input.readFloat();
					elementLight.setLight(pointLight);
				} break;
				case SPOT: {
					SpotLight spotLight = new SpotLight();
					spotLight.color.set(kryo.readObject(input, Color.class));
					spotLight.position.set(kryo.readObject(input, Vector3.class));
					spotLight.direction.set(kryo.readObject(input, Vector3.class));
					spotLight.intensity = input.readFloat();
					spotLight.cutoffAngle = input.readFloat();
					spotLight.exponent = input.readFloat();
					elementLight.setLight(spotLight);
				} break;
			}
			return elementLight;
		}
	}
	
	public static class ElementDecalSerializer extends Serializer<ElementDecal> {
		public void write(Kryo kryo, Output output, ElementDecal object) {
			output.writeString(object.getName());
			output.writeBoolean(object.isVisible());
			if (object.decalAsset != null) {
				output.writeBoolean(true);
				output.writeString(object.decalAsset.getUUID().toString());
			} else output.writeBoolean(false);
			kryo.writeObject(output, object.decal.position);
			kryo.writeObject(output, object.decal.rotation);
			kryo.writeObject(output, object.decal.scale);
			output.writeBoolean(object.decal.isBillboard());
		}

		public ElementDecal read(Kryo kryo, Input input, Class<ElementDecal> type) {
			ElementDecal elementDecal = new ElementDecal(input.readString());
			elementDecal.setVisible(input.readBoolean());
			if (input.readBoolean()) {
				UUID uuid = UUID.fromString(input.readString());
				ProjectAsset<Texture> projectAsset = ProjectAssetManager.INSTANCE.getAsset(Texture.class, uuid);
				if (projectAsset != null) projectAsset.addHandler(elementDecal);
			}
			elementDecal.decal.position = kryo.readObject(input, Vector3.class);
			elementDecal.decal.rotation = kryo.readObject(input, Vector3.class);
			elementDecal.decal.scale = kryo.readObject(input, Vector2.class);
			elementDecal.decal.setBillboard(input.readBoolean());
			return elementDecal;
		}
	}
	
	public static class ElementModelSerializer extends Serializer<ElementModel> {
		public void write(Kryo kryo, Output output, ElementModel object) {
			output.writeString(object.getName());
			output.writeBoolean(object.isVisible());
			if (object.modelAsset != null) {
				output.writeBoolean(true);
				output.writeString(object.modelAsset.getUUID().toString());
			} else output.writeBoolean(false);
			if (object.modelInstance != null) {
				output.writeBoolean(true);
				output.writeInt(object.modelInstance.materials.size);
				for (int i = 0; i != object.modelInstance.materials.size; i++) {
					kryo.writeObject(output, object.modelInstance.materials.get(i));
				}
			} else output.writeBoolean(false);
			kryo.writeObject(output, object.getTransform(GizmoTransformType.TRANSLATE));
			kryo.writeObject(output, object.getTransform(GizmoTransformType.ROTATE));
			kryo.writeObject(output, object.getTransform(GizmoTransformType.SCALE));
		}

		public ElementModel read(Kryo kryo, Input input, Class<ElementModel> type) {
			ElementModel elementModel = new ElementModel(input.readString());
			elementModel.setVisible(input.readBoolean());
			if (input.readBoolean()) {
				UUID uuid = UUID.fromString(input.readString());
				ProjectAsset<?> projectAsset = ProjectAssetManager.INSTANCE.getAsset(uuid);
				if (projectAsset != null) projectAsset.addHandler(elementModel);
				else Utils.logDebug("[ElementModel][Deserialization] Can't find asset `", uuid.toString(), "` with Unknown type loaded.");
			}
			if (input.readBoolean()) {
				Array<Material> materials = new Array<>();
				int size = input.readInt();
				for (int i = 0; i != size; i++) {
					materials.add(kryo.readObject(input, Material.class));
				}
				Utils.replaceMaterialsInModelInstance(elementModel.modelInstance, materials);
			}
			elementModel.getTransform(GizmoTransformType.TRANSLATE).set(kryo.readObject(input, Vector3.class));
			elementModel.getTransform(GizmoTransformType.ROTATE).set(kryo.readObject(input, Vector3.class));
			elementModel.getTransform(GizmoTransformType.SCALE).set(kryo.readObject(input, Vector3.class));
			return elementModel;
		}
	}
	
	public static class ElementHitboxStackSerializer extends Serializer<ElementHitboxStack> {
		public void write(Kryo kryo, Output output, ElementHitboxStack object) {
			output.writeString(object.getName());
			output.writeBoolean(object.isVisible());
			output.writeBoolean(object.isFolderOpened());
			kryo.writeObject(output, object.getFolderStack());
			output.writeLong(object.specFlags);
			output.writeLong(object.bulletFlags);
			output.writeLong(object.bulletFilterMask);
			output.writeLong(object.bulletFilterGroup);
			output.writeByte(object.linkFlagsToParent.length);
			for (int i = 0; i != object.linkFlagsToParent.length; i++) output.writeBoolean(object.linkFlagsToParent[i]);
			output.writeBoolean(object.isArrayStack);
			kryo.writeObject(output, object.getTransform(GizmoTransformType.TRANSLATE));
			kryo.writeObject(output, object.getTransform(GizmoTransformType.ROTATE));
			kryo.writeObject(output, object.getTransform(GizmoTransformType.SCALE));
		}

		public ElementHitboxStack read(Kryo kryo, Input input, Class<ElementHitboxStack> type) {
			ElementHitboxStack elementHitboxStack = new ElementHitboxStack(input.readString());
			elementHitboxStack.setVisible(input.readBoolean());
			elementHitboxStack.setFolderOpened(input.readBoolean());
			elementHitboxStack.setFolderStack(kryo.readObject(input, ElementStack.class));
			elementHitboxStack.specFlags = input.readLong();
			elementHitboxStack.bulletFlags = input.readLong();
			elementHitboxStack.bulletFilterMask = input.readLong();
			elementHitboxStack.bulletFilterGroup = input.readLong();
			byte booleanValues = input.readByte();
			for (int i = 0; i != booleanValues && i < elementHitboxStack.linkFlagsToParent.length; i++) elementHitboxStack.linkFlagsToParent[i] = input.readBoolean();
			elementHitboxStack.isArrayStack = input.readBoolean();
			elementHitboxStack.getTransform(GizmoTransformType.TRANSLATE).set(kryo.readObject(input, Vector3.class));
			elementHitboxStack.getTransform(GizmoTransformType.ROTATE).set(kryo.readObject(input, Vector3.class));
			elementHitboxStack.getTransform(GizmoTransformType.SCALE).set(kryo.readObject(input, Vector3.class));
			return elementHitboxStack;
		}
	}
	
	public static class ElementHitboxSerializer extends Serializer<ElementHitbox> {
		public void write(Kryo kryo, Output output, ElementHitbox object) {
			output.writeString(object.getName());
			output.writeBoolean(object.isVisible());
			output.writeLong(object.specFlags);
			output.writeLong(object.bulletFlags);
			output.writeLong(object.bulletFilterMask);
			output.writeLong(object.bulletFilterGroup);
			output.writeByte(object.linkFlagsToParent.length);
			for (int i = 0; i != object.linkFlagsToParent.length; i++) output.writeBoolean(object.linkFlagsToParent[i]);
			kryo.writeObject(output, object.getTransform(GizmoTransformType.TRANSLATE));
			kryo.writeObject(output, object.getTransform(GizmoTransformType.ROTATE));
			kryo.writeObject(output, object.getTransform(GizmoTransformType.SCALE));
		}

		public ElementHitbox read(Kryo kryo, Input input, Class<ElementHitbox> type) {
			ElementHitbox elementHitbox = new ElementHitbox(input.readString());
			elementHitbox.setVisible(input.readBoolean());
			elementHitbox.specFlags = input.readLong();
			elementHitbox.bulletFlags = input.readLong();
			elementHitbox.bulletFilterMask = input.readLong();
			elementHitbox.bulletFilterGroup = input.readLong();
			byte booleanValues = input.readByte();
			for (int i = 0; i != booleanValues && i < elementHitbox.linkFlagsToParent.length; i++) elementHitbox.linkFlagsToParent[i] = input.readBoolean();
			elementHitbox.getTransform(GizmoTransformType.TRANSLATE).set(kryo.readObject(input, Vector3.class));
			elementHitbox.getTransform(GizmoTransformType.ROTATE).set(kryo.readObject(input, Vector3.class));
			elementHitbox.getTransform(GizmoTransformType.SCALE).set(kryo.readObject(input, Vector3.class));
			return elementHitbox;
		}
	}
}
