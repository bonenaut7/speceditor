package by.fxg.speceditor.scenes.format;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.bullet.objects.IPhysObject;
import by.fxg.pilesos.bullet.objects.PhysObject;
import by.fxg.pilesos.decals.SmartDecal;
import by.fxg.pilesos.specpak.PakAssetManager;
import by.fxg.speceditor.scenes.format.ScenesNodeGraph.NodeDecal;
import by.fxg.speceditor.scenes.format.ScenesNodeGraph.NodeHitbox;
import by.fxg.speceditor.scenes.format.ScenesNodeGraph.NodeHitboxMesh;
import by.fxg.speceditor.scenes.format.ScenesNodeGraph.NodeHitboxStack;
import by.fxg.speceditor.scenes.format.ScenesNodeGraph.NodeLight;
import by.fxg.speceditor.scenes.format.ScenesNodeGraph.NodeModel;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

@SuppressWarnings("rawtypes")
public class ScenesRenderGraph {
	public PakAssetManager pakAssetManager;
	public Environment environment;
	public Color bufferClearColor;
	public float cameraFieldOfView, cameraFar, cameraNear;
	
	public Map<Object, String> objectNames;
	public Array<BaseLight> lights;
	public Array<SmartDecal> decals;
	public Array<ModelInstance> modelInstances;
	public Array<IPhysObject> physObjects;
	
	public ScenesRenderGraph(PakAssetManager pakAssetManager, ScenesNodeGraph nodeGraph) {
		this.pakAssetManager = pakAssetManager;
		this.environment = nodeGraph.environment;
		this.bufferClearColor = nodeGraph.bufferClearColor;
		this.cameraFieldOfView = nodeGraph.cameraParameters.x;
		this.cameraFar = nodeGraph.cameraParameters.y;
		this.cameraNear = nodeGraph.cameraParameters.z;
		
		this.objectNames = new HashMap<>();
		this.lights = new Array<>();
		for (NodeLight node : nodeGraph.lights) {
			switch (node.type) {
				case 0: this.lights.add(new PointLight().set(node.color, node.position, node.intensity)); break;
				case 1: this.lights.add(new DirectionalLight().set(node.color, node.position)); break;
				case 2: this.lights.add(new SpotLight().set(node.color, node.position, node.direction, node.intensity, node.cutoffAngle, node.exponent)); break;
			}
			this.objectNames.put(this.lights.get(this.lights.size - 1), node.name);
		}
		this.decals = new Array<>();
		for (NodeDecal node : nodeGraph.decals) {
			if (pakAssetManager.isArchivePresent(node.pakArchive)) {
				Texture texture = pakAssetManager.getLoadAsset(node.pakArchive, node.pakAsset, Texture.class);
				Decal decal = Decal.newDecal(new TextureRegion(texture), true);
				decal.setPosition(node.position);
				decal.setRotation(node.rotation.x, node.rotation.y, node.rotation.z);
				decal.setScale(node.scale.x * (8F/texture.getWidth()), node.scale.y * (8F/texture.getHeight()));
				this.decals.add(new SmartDecal(decal, node.isBillboard));
				this.objectNames.put(this.decals.get(this.decals.size - 1), node.name);
			}
		}
		this.modelInstances = new Array<>();
		for (NodeModel node : nodeGraph.models) {
			if (pakAssetManager.isArchivePresent(node.pakArchive)) {
				ModelInstance modelInstance = null;
				Class<?> type = pakAssetManager.getAssetManager().getAssetType(pakAssetManager.getResolver().formatAssetPath(node.pakArchive, node.pakAsset));
				if (type == Model.class) modelInstance = new ModelInstance(pakAssetManager.getLoadAsset(node.pakArchive, node.pakAsset, Model.class));
				else if (type == SceneAsset.class) modelInstance = new ModelInstance(pakAssetManager.getLoadAsset(node.pakArchive, node.pakAsset, SceneAsset.class).scene.model);
				for (int j = 0; j != node.materials.size; j++) {
					modelInstance.materials.get(j).set(node.materials.get(j));
				}
				modelInstance.transform.setToTranslation(node.position);
				modelInstance.transform.scale(node.scale.x, node.scale.y, node.scale.z);
				modelInstance.transform.rotate(1, 0, 0, node.rotation.x);
				modelInstance.transform.rotate(0, 1, 0, node.rotation.y);
				modelInstance.transform.rotate(0, 0, 1, node.rotation.z);
				this.modelInstances.add(modelInstance);
				this.objectNames.put(this.modelInstances.get(this.modelInstances.size - 1), node.name);
			}
		}
		this.physObjects = new Array<>();
		for (NodeHitbox node : nodeGraph.hitboxes) {
			this.convertHitboxNode(node);
		}
	}
	
	private void convertHitboxNode(NodeHitbox node) {
		if (node instanceof NodeHitboxStack && ((NodeHitboxStack)node).isArrayHitbox) {
			NodeHitboxStack nodeHitboxStack = (NodeHitboxStack)node;
			if (nodeHitboxStack.children != null && nodeHitboxStack.children.length > 0) {
				for (NodeHitbox node$ : nodeHitboxStack.children) {
					this.convertHitboxNode(node$);
				}
			}
			return;
		}
		PhysObject.Builder builder = new PhysObject.Builder(node.name);
		builder.setShape(this.getHitboxNodeShape(node));
		builder.setPhysFlags(node.specFlags);
		builder.setActivationState(node.bulletActivationState);
		builder.setCollisionFlags(node.bulletFlags);
		builder.setCollisionFilterMask(node.bulletFilterMask);
		builder.setCollisionFilterGroup(node.bulletFilterGroup);
		builder.setPosition(node.position);
		builder.setRotation(node.rotation);
		this.physObjects.add(builder.build());
		this.objectNames.put(this.physObjects.get(this.physObjects.size - 1), node.name);
	}
	
	private btCollisionShape getHitboxNodeShape(NodeHitbox node) {
		if (node instanceof NodeHitboxStack) {
			NodeHitboxStack nodeHitboxStack = (NodeHitboxStack)node;
			if (nodeHitboxStack.children != null && nodeHitboxStack.children.length > 0) {
				btCompoundShape compoundShape = new btCompoundShape();
				for (NodeHitbox node$ : nodeHitboxStack.children) {
					btCollisionShape shape = this.getHitboxNodeShape(node$);
					if (shape != null) {
						Matrix4 localTransform = new Matrix4();
						localTransform.setToTranslation(node$.position);
						localTransform.rotate(1F, 0F, 0F, node$.rotation.x);
						localTransform.rotate(0F, 1F, 0F, node$.rotation.y);
						localTransform.rotate(0F, 0F, 1F, node$.rotation.z);
						shape.setLocalScaling(node$.scale);
						compoundShape.addChildShape(localTransform, shape);
					}
				}
				return compoundShape;
			} else return null;
		} else if (node instanceof NodeHitboxMesh && this.pakAssetManager.isArchivePresent(((NodeHitboxMesh)node).pakArchive)) {
			NodeHitboxMesh nodeHitboxMesh = (NodeHitboxMesh)node;
			Array<Node> modelNodes = null;
			Array<Node> genNodes = new Array<>();
			Class<?> type = this.pakAssetManager.getAssetManager().getAssetType(this.pakAssetManager.getResolver().formatAssetPath(nodeHitboxMesh.pakArchive, nodeHitboxMesh.pakAsset));
			if (type == Model.class) modelNodes = this.pakAssetManager.getLoadAsset(nodeHitboxMesh.pakArchive, nodeHitboxMesh.pakAsset, Model.class).nodes;
			else if (type == SceneAsset.class) modelNodes = this.pakAssetManager.getLoadAsset(nodeHitboxMesh.pakArchive, nodeHitboxMesh.pakAsset, SceneAsset.class).scene.model.nodes;
			if (modelNodes != null && modelNodes.size > 0) {
				for (int i = 0; i < modelNodes.size && i < nodeHitboxMesh.nodes.length; i++) {
					if (nodeHitboxMesh.nodes[i]) genNodes.add(modelNodes.get(i));
				}
				btCollisionShape shape = Bullet.obtainStaticNodeShape(genNodes);
				shape.setLocalScaling(node.scale);
				return shape;
			} else return null;
		} else {
			btCollisionShape shape = new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f));
			shape.setLocalScaling(node.scale);
			return shape;
		}
	}
}
