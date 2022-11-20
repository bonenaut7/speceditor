package by.fxg.speceditor.scenes.format;

import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import by.fxg.speceditor.project.assets.ProjectAsset;
import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.serialization.SpecEditorSerialization;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.objectTree.ElementStack;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.objectTree.elements.ElementDecal;
import by.fxg.speceditor.std.objectTree.elements.ElementFolder;
import by.fxg.speceditor.std.objectTree.elements.ElementHitbox;
import by.fxg.speceditor.std.objectTree.elements.ElementHitboxMesh;
import by.fxg.speceditor.std.objectTree.elements.ElementHitboxStack;
import by.fxg.speceditor.std.objectTree.elements.ElementLight;
import by.fxg.speceditor.std.objectTree.elements.ElementModel;
import by.fxg.speceditor.std.objectTree.elements.TreeElementHitbox;
import by.fxg.speceditor.utils.Utils;

public class ScenesNodeGraphSerializer {
	private ScenesNodeGraph graph = new ScenesNodeGraph();
	private FileHandle outFile = null;
	
	public ScenesNodeGraphSerializer setBufferClearColor(Color color) { return this.setBufferClearColor(color.r, color.g, color.b, color.a); }
	public ScenesNodeGraphSerializer setBufferClearColor(float r, float g, float b, float a) {
		this.graph.bufferClearColor.set(r, g, b, a);
		return this;
	}
	
	public ScenesNodeGraphSerializer setCameraParameters(Vector3 cameraParameters) { return this.setCameraParameters(cameraParameters.x, cameraParameters.y, cameraParameters.z); }
	public ScenesNodeGraphSerializer setCameraParameters(float fieldOfView, float far, float near) {
		this.graph.cameraParameters.set(fieldOfView, far, near);
		return this;
	}
	
	public ScenesNodeGraphSerializer setEnvironment(Attributes environment) {
		this.graph.environment.set(environment);
		return this;
	}
	
	public ScenesNodeGraphSerializer addElementStack(ElementStack stack) {
		this.inspectElementStack(stack);
		return this;
	}
	
	public ScenesNodeGraphSerializer setFile(FileHandle fileHandle) {
		this.outFile = fileHandle.extension().equalsIgnoreCase("ssf") ? fileHandle : fileHandle.parent().child(Utils.format(fileHandle.nameWithoutExtension(), ".ssf"));
		return this;
	}
	
	/** Returns exception of something gone wrong, or null if everything ok **/
	public Exception pack() {
		try {
			if (this.outFile == null) throw new NullPointerException("Out file is null");
			FileHandle assetsFile = this.outFile.parent().child(Utils.format(this.outFile.nameWithoutExtension(), ".assets"));
			if (!this.outFile.exists()) this.outFile.file().createNewFile();
			if (!assetsFile.exists()) assetsFile.file().createNewFile();
			
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(assetsFile.file()));
			for (ProjectAsset<?> projectAsset : ProjectAssetManager.INSTANCE.getAssetMap().values()) {
				if (projectAsset.getAssetHandlersSize() > 0) {
					FileHandle handle = projectAsset.getFile();
					zos.putNextEntry(new ZipEntry(Utils.format(projectAsset.getUUID().toString(), ".", handle.extension())));
					zos.write(handle.readBytes());
					zos.closeEntry();
				}
			}
			zos.close();
			
			if (this.outFile.extension().equalsIgnoreCase("json")) {
				//TODO add json exporting
			} else {
				Kryo kryo = this.createKryo();
				Output output = new Output(new FileOutputStream(this.outFile.file()));
				kryo.writeClassAndObject(output, this.graph);
				output.close();
			}
			return null;
		} catch (Exception e) {
			Utils.logError(e, "SpecScenesSerializer", "Something gone wrong while exporting scenes project");
			return e;
		}
	}
	
	//======================================================================================================================================================================//
	
	private void addToGraph(Object object) {
		if (object == null) return;
		if (object instanceof ScenesNodeGraph.NodeDecal) this.graph.decals.add((ScenesNodeGraph.NodeDecal)object);
		else if (object instanceof ScenesNodeGraph.NodeLight) this.graph.lights.add((ScenesNodeGraph.NodeLight)object);
		else if (object instanceof ScenesNodeGraph.NodeModel) this.graph.models.add((ScenesNodeGraph.NodeModel)object);
		else if (object instanceof ScenesNodeGraph.NodeHitbox) this.graph.hitboxes.add((ScenesNodeGraph.NodeHitbox)object);
	}
	
	private void inspectElementStack(ElementStack stack) {
		Array<TreeElement> elements = stack.getElements();
		for (int i = 0; i != elements.size; i++) {
			this.addToGraph(this.convertElement(elements.get(i)));
		}
	}
	
	private Object convertElement(TreeElement element) {
		if (element instanceof ElementFolder) {
			this.inspectElementStack(((ElementFolder)element).getFolderStack());
		} else if (element instanceof ElementDecal) {
			ElementDecal elementDecal = (ElementDecal)element;
			if (elementDecal.decalAsset == null) return null;
			ScenesNodeGraph.NodeDecal decal = new ScenesNodeGraph.NodeDecal();
			decal.name = elementDecal.getName();
			decal.assetIndex = elementDecal.decalAsset.getUUID();
			decal.isBillboard = elementDecal.decal.isBillboard();
			decal.position = elementDecal.decal.position;
			decal.rotation = elementDecal.decal.rotation;
			decal.scale = elementDecal.decal.scale;
			return decal;
		} else if (element instanceof ElementLight) {
			ElementLight elementLight = (ElementLight)element;
			ScenesNodeGraph.NodeLight light = new ScenesNodeGraph.NodeLight();
			light.name = elementLight.getName();
			light.color = elementLight.getLight(BaseLight.class).color;
			switch (elementLight.type) {
				case POINT: {
					light.type = 0;
					light.intensity = elementLight.getLight(PointLight.class).intensity;
				} break;
				case SPOT: {
					light.type = 1;
					light.intensity = elementLight.getLight(SpotLight.class).intensity;
					light.cutoffAngle = elementLight.getLight(SpotLight.class).cutoffAngle;
					light.exponent = elementLight.getLight(SpotLight.class).exponent;
				} break;
				default:
			}
			light.position = elementLight.getTransform(GizmoTransformType.TRANSLATE);
			light.direction = elementLight.getTransform(GizmoTransformType.ROTATE);
			return light;
		} else if (element instanceof ElementModel) {
			ElementModel elementModel = (ElementModel)element;
			if (elementModel.modelAsset == null) return null;
			ScenesNodeGraph.NodeModel model = new ScenesNodeGraph.NodeModel();
			model.name = elementModel.getName();
			model.assetIndex = elementModel.modelAsset.getUUID();
			model.materials = elementModel.modelInstance.materials;
			model.position = elementModel.getTransform(GizmoTransformType.TRANSLATE);
			model.rotation = elementModel.getTransform(GizmoTransformType.ROTATE);
			model.scale = elementModel.getTransform(GizmoTransformType.SCALE);
			return model;
		} else if (element instanceof TreeElementHitbox) {
			return this.convertHitboxElements(element, 0, 0, 0, 0, 0);
		}
		return null;
	}
	
	private Object convertHitboxElements(TreeElement treeElement, long parentSpecFlags, int parentCollisionFlags, int parentActivationState, int parentFilterMask, int parentFilterGroup) {
		if (treeElement instanceof ElementHitbox) {
			ElementHitbox element = (ElementHitbox)treeElement;
			ScenesNodeGraph.NodeHitbox hitbox = new ScenesNodeGraph.NodeHitbox();
			hitbox.name = element.getName();
			hitbox.specFlags = element.linkToParent[0] ? parentSpecFlags : element.specFlags;
			hitbox.bulletFlags = element.linkToParent[1] ? parentCollisionFlags : element.btCollisionFlags;
			hitbox.bulletActivationState = element.linkToParent[2] ? parentActivationState : element.btActivationState;
			hitbox.bulletFilterMask = element.linkToParent[3] ? parentFilterMask : element.btFilterMask;
			hitbox.bulletFilterGroup = element.linkToParent[4] ? parentFilterGroup : element.btFilterGroup;
			
			hitbox.position = element.getTransform(GizmoTransformType.TRANSLATE);
			hitbox.rotation = element.getTransform(GizmoTransformType.ROTATE);
			hitbox.scale = element.getTransform(GizmoTransformType.SCALE);
			return hitbox;
		} else if (treeElement instanceof ElementHitboxMesh) {
			ElementHitboxMesh element = (ElementHitboxMesh)treeElement;
			if (element.modelAsset == null) return null;
			ScenesNodeGraph.NodeHitboxMesh hitboxMesh = new ScenesNodeGraph.NodeHitboxMesh();
			hitboxMesh.name = element.getName();
			hitboxMesh.specFlags = element.linkToParent[0] ? parentSpecFlags : element.specFlags;
			hitboxMesh.bulletFlags = element.linkToParent[1] ? parentCollisionFlags : element.btCollisionFlags;
			hitboxMesh.bulletActivationState = element.linkToParent[2] ? parentActivationState : element.btActivationState;
			hitboxMesh.bulletFilterMask = element.linkToParent[3] ? parentFilterMask : element.btFilterMask;
			hitboxMesh.bulletFilterGroup = element.linkToParent[4] ? parentFilterGroup : element.btFilterGroup;
			
			hitboxMesh.position = element.getTransform(GizmoTransformType.TRANSLATE);
			hitboxMesh.rotation = element.getTransform(GizmoTransformType.ROTATE);
			hitboxMesh.scale = element.getTransform(GizmoTransformType.SCALE);
			hitboxMesh.assetIndex = element.modelAsset.getUUID();
			hitboxMesh.nodes = element.nodes;
			return hitboxMesh;
		} else if (treeElement instanceof ElementHitboxStack) {
			ElementHitboxStack element = (ElementHitboxStack)treeElement;
			Array<TreeElement> elements = element.getFolderStack().getElements();
			ScenesNodeGraph.NodeHitboxStack hitboxStack = new ScenesNodeGraph.NodeHitboxStack();
			hitboxStack.name = element.getName();
			hitboxStack.specFlags = element.linkToParent[0] ? parentSpecFlags : element.specFlags;
			hitboxStack.bulletFlags = element.linkToParent[1] ? parentCollisionFlags : element.btCollisionFlags;
			hitboxStack.bulletActivationState = element.linkToParent[2] ? parentActivationState : element.btActivationState;
			hitboxStack.bulletFilterMask = element.linkToParent[3] ? parentFilterMask : element.btFilterMask;
			hitboxStack.bulletFilterGroup = element.linkToParent[4] ? parentFilterGroup : element.btFilterGroup;
			
			hitboxStack.position = element.getTransform(GizmoTransformType.TRANSLATE);
			hitboxStack.rotation = element.getTransform(GizmoTransformType.ROTATE);
			hitboxStack.scale = element.getTransform(GizmoTransformType.SCALE);
			hitboxStack.isArrayHitbox = element.isArrayStack;
			
			Array<ScenesNodeGraph.NodeHitbox> hitboxes = new Array<>();
			Object object;
			for (int i = 0; i != elements.size; i++) {
				object = this.convertHitboxElements(elements.get(i), hitboxStack.specFlags, hitboxStack.bulletFlags, hitboxStack.bulletActivationState, hitboxStack.bulletFilterMask, hitboxStack.bulletFilterGroup);
				if (object != null) hitboxes.add((ScenesNodeGraph.NodeHitbox)object);
			}
			hitboxStack.children = hitboxes.toArray(ScenesNodeGraph.NodeHitbox.class);
			return hitboxStack;
		}
		return null;
	}
	
	private Kryo createKryo() {
		Kryo kryo = new Kryo();
		kryo.setWarnUnregisteredClasses(true);
		SpecEditorSerialization.INSTANCE.registerGdxSerializers(kryo);
		SpecEditorSerialization.INSTANCE.registerGdxAttributesSerializers(kryo);
		SpecEditorSerialization.INSTANCE.registerGltfAttributesSerializers(kryo);
		SpecEditorSerialization.INSTANCE.registerSpecEditorAttributesSerializers(kryo);
		
		//Extension
		kryo.register(ScenesNodeGraph.class, new ScenesKryoExtension.ScenesGraphSerializer());
		kryo.register(ScenesNodeGraph.NodeDecal.class, new ScenesKryoExtension.ScenesDecalSerializer());
		kryo.register(ScenesNodeGraph.NodeLight.class, new ScenesKryoExtension.ScenesLightSerializer());
		kryo.register(ScenesNodeGraph.NodeModel.class, new ScenesKryoExtension.ScenesModelSerializer());
		kryo.register(ScenesNodeGraph.NodeHitbox.class, new ScenesKryoExtension.ScenesHitboxSerializer());
		kryo.register(ScenesNodeGraph.NodeHitboxMesh.class, new ScenesKryoExtension.ScenesHitboxMeshSerializer());
		kryo.register(ScenesNodeGraph.NodeHitboxStack.class, new ScenesKryoExtension.ScenesHitboxStackSerializer());
		return kryo;
	}
}
