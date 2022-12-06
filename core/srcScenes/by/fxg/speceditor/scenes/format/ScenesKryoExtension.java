package by.fxg.speceditor.scenes.format;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ScenesKryoExtension {
	public static class ScenesGraphSerializer extends Serializer<ScenesNodeGraph> {
		public void write(Kryo kryo, Output output, ScenesNodeGraph object) {
			kryo.writeObject(output, object.bufferClearColor != null ? object.bufferClearColor : new Color(0, 0, 0, 1));
			kryo.writeObject(output, object.cameraParameters != null ? object.cameraParameters : new Vector3(67.0F, 50.0F, 0.01F));
			kryo.writeObject(output, object.environment != null ? object.environment : new Environment());
			
			output.writeInt(object.lights.size);
			for (int i = 0; i != object.lights.size; i++) {
				kryo.writeClassAndObject(output, object.lights.get(i));
			}
			output.writeInt(object.hitboxes.size);
			for (int i = 0; i != object.hitboxes.size; i++) {
				kryo.writeClassAndObject(output, object.hitboxes.get(i));
			}
			output.writeInt(object.decals.size);
			for (int i = 0; i != object.decals.size; i++) {
				kryo.writeClassAndObject(output, object.decals.get(i));
			}
			output.writeInt(object.models.size);
			for (int i = 0; i != object.models.size; i++) {
				kryo.writeClassAndObject(output, object.models.get(i));
			}
		}

		public ScenesNodeGraph read(Kryo kryo, Input input, Class<ScenesNodeGraph> type) {
			ScenesNodeGraph graph = new ScenesNodeGraph();
			graph.bufferClearColor = kryo.readObject(input, Color.class);
			graph.cameraParameters = kryo.readObject(input, Vector3.class);
			graph.environment = kryo.readObject(input, Environment.class);
			
			int lights = input.readInt();
			for (int i = 0; i != lights; i++) {
				graph.lights.add((ScenesNodeGraph.NodeLight)kryo.readClassAndObject(input));
			}
			int hitboxes = input.readInt();
			for (int i = 0; i != hitboxes; i++) {
				graph.hitboxes.add((ScenesNodeGraph.NodeHitbox)kryo.readClassAndObject(input));
			}
			int decals = input.readInt();
			for (int i = 0; i != decals; i++) {
				graph.decals.add((ScenesNodeGraph.NodeDecal)kryo.readClassAndObject(input));
			}
			int models = input.readInt();
			for (int i = 0; i != models; i++) {
				graph.models.add((ScenesNodeGraph.NodeModel)kryo.readClassAndObject(input));
			}
			return graph;
		}
	}
	
	public static class ScenesDecalSerializer extends Serializer<ScenesNodeGraph.NodeDecal> {
		public void write(Kryo kryo, Output output, ScenesNodeGraph.NodeDecal object) {
			output.writeString(object.name);
			output.writeString(object.pakArchive);
			output.writeString(object.pakAsset);
			output.writeBoolean(object.isBillboard);
			kryo.writeObject(output, object.position);
			kryo.writeObject(output, object.rotation);
			kryo.writeObject(output, object.scale);
		}

		public ScenesNodeGraph.NodeDecal read(Kryo kryo, Input input, Class<ScenesNodeGraph.NodeDecal> type) {
			ScenesNodeGraph.NodeDecal decal = new ScenesNodeGraph.NodeDecal();
			decal.name = input.readString();
			decal.pakArchive = input.readString();
			decal.pakAsset = input.readString();
			decal.isBillboard = input.readBoolean();
			decal.position = kryo.readObject(input, Vector3.class);
			decal.rotation = kryo.readObject(input, Vector3.class);
			decal.scale = kryo.readObject(input, Vector2.class);
			return decal;
		}
	}
	
	public static class ScenesLightSerializer extends Serializer<ScenesNodeGraph.NodeLight> {
		public void write(Kryo kryo, Output output, ScenesNodeGraph.NodeLight object) {
			output.writeString(object.name);
			output.writeInt(object.type);
			switch (object.type) {
				case 0: {
					kryo.writeObject(output, object.color);
					kryo.writeObject(output, object.position);
					output.writeFloat(object.intensity);
				} break;
				case 2: {
					kryo.writeObject(output, object.color);
					kryo.writeObject(output, object.position);
					kryo.writeObject(output, object.direction);
					output.writeFloat(object.intensity);
					output.writeFloat(object.cutoffAngle);
					output.writeFloat(object.exponent);
				} break;
			}
		}

		public ScenesNodeGraph.NodeLight read(Kryo kryo, Input input, Class<ScenesNodeGraph.NodeLight> type) {
			ScenesNodeGraph.NodeLight light = new ScenesNodeGraph.NodeLight();
			light.name = input.readString();
			light.type = input.readInt();
			switch (light.type) {
				case 0: {
					light.color = kryo.readObject(input, Color.class);
					light.position = kryo.readObject(input, Vector3.class);
					light.intensity = input.readFloat();
				} break;
				case 2: {
					light.color = kryo.readObject(input, Color.class);
					light.position = kryo.readObject(input, Vector3.class);
					light.direction = kryo.readObject(input, Vector3.class);
					light.intensity = input.readFloat();
					light.cutoffAngle = input.readFloat();
					light.exponent = input.readFloat();
				} break;
			}
			return light;
		}
	}
	
	public static class ScenesModelSerializer extends Serializer<ScenesNodeGraph.NodeModel> {
		public void write(Kryo kryo, Output output, ScenesNodeGraph.NodeModel object) {
			output.writeString(object.name);
			output.writeString(object.pakArchive);
			output.writeString(object.pakAsset);
			output.writeInt(object.materials.size);
			for (int i = 0; i != object.materials.size; i++) {
				kryo.writeObject(output, object.materials.get(i));
			}
			kryo.writeObject(output, object.position);
			kryo.writeObject(output, object.rotation);
			kryo.writeObject(output, object.scale);
		}

		public ScenesNodeGraph.NodeModel read(Kryo kryo, Input input, Class<ScenesNodeGraph.NodeModel> type) {
			ScenesNodeGraph.NodeModel model = new ScenesNodeGraph.NodeModel();
			model.name = input.readString();
			model.pakArchive = input.readString();
			model.pakAsset = input.readString();
			model.materials = new Array<>();
			int size = input.readInt();
			for (int i = 0; i != size; i++) {
				model.materials.add(kryo.readObject(input, Material.class));
			}
			model.position = kryo.readObject(input, Vector3.class);
			model.rotation = kryo.readObject(input, Vector3.class);
			model.scale = kryo.readObject(input, Vector3.class);
			return model;
		}
	}
	
	public static class ScenesHitboxSerializer extends Serializer<ScenesNodeGraph.NodeHitbox> {
		public void write(Kryo kryo, Output output, ScenesNodeGraph.NodeHitbox object) {
			output.writeString(object.name);
			output.writeLong(object.specFlags);
			output.writeInt(object.bulletFlags);
			output.writeInt(object.bulletActivationState);
			output.writeInt(object.bulletFilterMask);
			output.writeInt(object.bulletFilterGroup);
			kryo.writeObject(output, object.position);
			kryo.writeObject(output, object.rotation);
			kryo.writeObject(output, object.scale);
		}

		public ScenesNodeGraph.NodeHitbox read(Kryo kryo, Input input, Class<ScenesNodeGraph.NodeHitbox> type) {
			ScenesNodeGraph.NodeHitbox hitbox = new ScenesNodeGraph.NodeHitbox();
			hitbox.name = input.readString();
			hitbox.specFlags = input.readLong();
			hitbox.bulletFlags = input.readInt();
			hitbox.bulletActivationState = input.readInt();
			hitbox.bulletFilterMask = input.readInt();
			hitbox.bulletFilterGroup = input.readInt();
			hitbox.position = kryo.readObject(input, Vector3.class);
			hitbox.rotation = kryo.readObject(input, Vector3.class);
			hitbox.scale = kryo.readObject(input, Vector3.class);
			return hitbox;
		}
	}
	
	public static class ScenesHitboxMeshSerializer extends Serializer<ScenesNodeGraph.NodeHitboxMesh> {
		public void write(Kryo kryo, Output output, ScenesNodeGraph.NodeHitboxMesh object) {
			output.writeString(object.name);
			output.writeLong(object.specFlags);
			output.writeInt(object.bulletFlags);
			output.writeInt(object.bulletActivationState);
			output.writeInt(object.bulletFilterMask);
			output.writeInt(object.bulletFilterGroup);
			kryo.writeObject(output, object.position);
			kryo.writeObject(output, object.rotation);
			kryo.writeObject(output, object.scale);
			output.writeString(object.pakArchive);
			output.writeString(object.pakAsset);
			output.writeInt(object.nodes.length);
			for (int i = 0; i != object.nodes.length; i++) {
				output.writeBoolean(object.nodes[i]);
			}
		}

		public ScenesNodeGraph.NodeHitboxMesh read(Kryo kryo, Input input, Class<ScenesNodeGraph.NodeHitboxMesh> type) {
			ScenesNodeGraph.NodeHitboxMesh hitboxMesh = new ScenesNodeGraph.NodeHitboxMesh();
			hitboxMesh.name = input.readString();
			hitboxMesh.specFlags = input.readLong();
			hitboxMesh.bulletFlags = input.readInt();
			hitboxMesh.bulletActivationState = input.readInt();
			hitboxMesh.bulletFilterMask = input.readInt();
			hitboxMesh.bulletFilterGroup = input.readInt();
			hitboxMesh.position = kryo.readObject(input, Vector3.class);
			hitboxMesh.rotation = kryo.readObject(input, Vector3.class);
			hitboxMesh.scale = kryo.readObject(input, Vector3.class);
			hitboxMesh.pakArchive = input.readString();
			hitboxMesh.pakAsset = input.readString();
			hitboxMesh.nodes = new boolean[input.readInt()];
			for (int i = 0; i != hitboxMesh.nodes.length; i++) {
				hitboxMesh.nodes[i] = input.readBoolean();
			}
			return hitboxMesh;
		}
	}
	
	public static class ScenesHitboxStackSerializer extends Serializer<ScenesNodeGraph.NodeHitboxStack> {
		public void write(Kryo kryo, Output output, ScenesNodeGraph.NodeHitboxStack object) {
			output.writeString(object.name);
			output.writeLong(object.specFlags);
			output.writeInt(object.bulletFlags);
			output.writeInt(object.bulletActivationState);
			output.writeInt(object.bulletFilterMask);
			output.writeInt(object.bulletFilterGroup);
			kryo.writeObject(output, object.position);
			kryo.writeObject(output, object.rotation);
			kryo.writeObject(output, object.scale);
			output.writeBoolean(object.isArrayHitbox);
			output.writeInt(object.children.length);
			for (int i = 0; i != object.children.length; i++) {
				kryo.writeClassAndObject(output, object.children[i]);
			}
		}

		public ScenesNodeGraph.NodeHitboxStack read(Kryo kryo, Input input, Class<ScenesNodeGraph.NodeHitboxStack> type) {
			ScenesNodeGraph.NodeHitboxStack hitboxStack = new ScenesNodeGraph.NodeHitboxStack();
			hitboxStack.name = input.readString();
			hitboxStack.specFlags = input.readLong();
			hitboxStack.bulletFlags = input.readInt();
			hitboxStack.bulletActivationState = input.readInt();
			hitboxStack.bulletFilterMask = input.readInt();
			hitboxStack.bulletFilterGroup = input.readInt();
			hitboxStack.position = kryo.readObject(input, Vector3.class);
			hitboxStack.rotation = kryo.readObject(input, Vector3.class);
			hitboxStack.scale = kryo.readObject(input, Vector3.class);
			hitboxStack.isArrayHitbox = input.readBoolean();
			hitboxStack.children = new ScenesNodeGraph.NodeHitbox[input.readInt()];
			for (int i = 0; i != hitboxStack.children.length; i++) {
				hitboxStack.children[i] = (ScenesNodeGraph.NodeHitbox)kryo.readClassAndObject(input);
			}
			return hitboxStack;
		}
	}
}