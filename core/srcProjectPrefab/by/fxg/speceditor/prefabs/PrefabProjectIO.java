package by.fxg.speceditor.prefabs;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.std.objectTree.ElementStack;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.objectTree.elements.ElementFolder;
import by.fxg.speceditor.std.render.IRendererType.ViewportSettings;

public class PrefabProjectIO {
	private PrefabProject project;
	private FileHandle projectFile;
	private Throwable lastException = null;
	
	public PrefabProjectIO(PrefabProject project) {
		this.project = project;
		this.projectFile = project.getProjectFolder().child("data.prj");
	}
	
	/** Returns true if loading was successful **/
	public boolean loadProjectData(ElementStack inputStack) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(this.projectFile.readBytes());
			DataInputStream dis = new DataInputStream(bais);
			ReaderUtil util = new ReaderUtil(this.project.getProjectFolder(), dis);
			
			dis.skip(8); //skip magic and version
			util.readCheckID();
			ViewportSettings.viewportHitboxDepth = dis.readBoolean();
			ViewportSettings.viewportHitboxWidth = dis.readFloat();
			ViewportSettings.bufferColor = util.readColor();
			ViewportSettings.cameraSettings = util.readVector3();
			ViewportSettings.viewportAttributes.add(new BlendingAttribute(1f), FloatAttribute.createAlphaTest(0.5f));
			int attributesSize = dis.readInt();
			for (int i = 0; i != attributesSize; i++) {
				Attribute attribute = util.readAttribute();
				if (!this.hasAttribute(ViewportSettings.viewportAttributes, attribute)) ViewportSettings.viewportAttributes.add(attribute);
			}
			
			util.readCheckID();
			this.readStack(dis, util, null, inputStack);
			
			dis.close();
			bais.close();
			
			ViewportSettings.shouldUpdate = true;
			return true;
		} catch (Throwable exception) {
			exception.printStackTrace();
			ViewportSettings.viewportAttributes.add(new BlendingAttribute(1f), FloatAttribute.createAlphaTest(0.5f));
			ViewportSettings.shouldUpdate = true;
			this.lastException = exception;
		}
		return false;
	}
	
	/** Returns true if loading was successful **/
	public boolean writeProjectData(ElementStack outStack) {
		try {
			this.project.getProjectFolder().file().mkdirs();
			this.projectFile.file().createNewFile();
			FileOutputStream fos = new FileOutputStream(this.projectFile.file());
			DataOutputStream dos = new DataOutputStream(fos);
			ReaderUtil util = new ReaderUtil(this.project.getProjectFolder(), dos);
			
			dos.write(0xBADF0002);
			dos.writeInt(0);
			
			util.writeCheckID(); //viewport data
			dos.writeBoolean(ViewportSettings.viewportHitboxDepth);
			dos.writeFloat(ViewportSettings.viewportHitboxWidth);
			util.writeColor(ViewportSettings.bufferColor);
			util.writeVector3(ViewportSettings.cameraSettings);
			dos.writeInt(ViewportSettings.viewportAttributes.size);
			for (Attribute attribute : ViewportSettings.viewportAttributes) {
				util.writeAttribute(attribute);
			}
			
			util.writeCheckID();
			this.writeStack(dos, util, outStack);
			
			dos.close();
			fos.close();
			return true;
		} catch (IOException exception) {
			exception.printStackTrace();
			this.lastException = exception;
		}
		return false;
	}
	
	private void readStack(DataInputStream dis, ReaderUtil util, TreeElement parent, ElementStack stack) throws IOException {
		try {
			int size = dis.readInt();
			for (int i = 0; i != size; i++) {
				int type = dis.readShort();
				switch (type) {
					case -1: break; //unknown
					case 0: { //Folder
						ElementFolder folder = new ElementFolder(dis.readUTF());
						folder.setVisible(dis.readBoolean());
						folder.setFolderOpened(dis.readBoolean());
						this.readStack(dis, util, folder, folder.getFolderStack());
						stack.add(folder);
					} break;
//					case 1: { //Model
//						ElementModel model = new ElementModel(dis.readUTF());
//						model.setVisible(dis.readBoolean());
//						model.localModelHandle = dis.readUTF();
//						try {
//							model.modelHandle = this.project.getProjectFolder().child(dis.readUTF());
//							AssetDescriptor<Model> assetDescriptor = new AssetDescriptor<Model>(model.modelHandle, Model.class);
//							if (!Game.get.resourceManager.assetManager.isLoaded(assetDescriptor)) {
//								Game.get.resourceManager.assetManager.load(assetDescriptor);
//								Game.get.resourceManager.assetManager.finishLoading();
//							}
//							model.modelInstance = new ModelInstance(Game.get.resourceManager.assetManager.get(model.modelHandle.path(), Model.class));
//						} catch (Exception e) {
//							model.modelHandle = null;
//							model.modelInstance = new ModelInstance(ResourceManager.standartModel);
//						}
//						int materialsSize = dis.readInt();
//						String[] materialID = new String[materialsSize];
//						for (int j = 0; j != materialsSize; j++) {
//							dis.readUTF();
//							model.modelInstance.materials.get(j).clear();
//							int materialAttributesSize = dis.readInt();
//							for (int k = 0; k != materialAttributesSize; k++) {
//								model.modelInstance.materials.get(j).set(util.readAttribute());
//							}
//							materialID[j] = model.modelInstance.materials.get(j).id;
//						}
//						((TERModel_Default)model.getRenderable()).setMaterials(materialID);
//						
//						model.getTransform(EnumGizmoTransformType.TRANSLATE).set(util.readVector3());
//						model.getTransform(EnumGizmoTransformType.ROTATE).set(util.readVector3());
//						model.getTransform(EnumGizmoTransformType.SCALE).set(util.readVector3());
//						stack.add(model);
//					} break;
//					case 2: { //Light
//						ElementLight light = new ElementLight(dis.readUTF());
//						light.setVisible(dis.readBoolean());
//						light.light.color.set(util.readColor());
//						light.light.position.set(util.readVector3());
//						light.light.intensity = dis.readFloat();
//						stack.add(light);
//					} break;
//					case 3: { //Hitbox
//						ElementHitbox hitbox = new ElementHitbox(dis.readUTF());
//						hitbox.setVisible(dis.readBoolean());
//						hitbox.type = dis.readInt();
//						hitbox.flags = dis.readLong();
//						hitbox.getTransform(EnumGizmoTransformType.TRANSLATE).set(util.readVector3());
//						hitbox.getTransform(EnumGizmoTransformType.ROTATE).set(util.readVector3());
//						hitbox.getTransform(EnumGizmoTransformType.SCALE).set(util.readVector3());
//						if (parent instanceof ElementMultiHitbox) hitbox.parent = (ElementMultiHitbox)parent;
//						stack.add(hitbox);
//					} break;
//					case 4: {
//						ElementMeshHitbox meshHitbox = new ElementMeshHitbox(dis.readUTF());
//						meshHitbox.setVisible(dis.readBoolean());
//						meshHitbox.flags = dis.readLong();
//						meshHitbox.localModelHandle = dis.readUTF();
//						try {
//							FileHandle handle = this.project.getProjectFolder().child(dis.readUTF());
//							AssetDescriptor<Model> assetDescriptor = new AssetDescriptor<Model>(handle, Model.class);
//							if (!Game.get.resourceManager.assetManager.isLoaded(assetDescriptor)) {
//								Game.get.resourceManager.assetManager.load(assetDescriptor);
//								Game.get.resourceManager.assetManager.finishLoading();
//							}
//							meshHitbox.setModel(handle, Game.get.resourceManager.assetManager.get(handle.path(), Model.class));
//						} catch (Exception e) {
//							meshHitbox.setModel(null, ResourceManager.standartModel);
//						}
//						meshHitbox.nodeUsed = dis.readInt();
//						meshHitbox.changeShape(meshHitbox.nodeUsed + 1);
//						meshHitbox.getTransform(EnumGizmoTransformType.TRANSLATE).set(util.readVector3());
//						meshHitbox.getTransform(EnumGizmoTransformType.ROTATE).set(util.readVector3());
//						meshHitbox.getTransform(EnumGizmoTransformType.SCALE).set(util.readVector3());
//						if (parent instanceof ElementMultiHitbox) meshHitbox.parent = (ElementMultiHitbox)parent;
//						stack.add(meshHitbox);
//					} break;
//					case 5: { //MultiHitbox
//						ElementMultiHitbox multiHitbox = new ElementMultiHitbox(dis.readUTF());
//						multiHitbox.setVisible(dis.readBoolean());
//						multiHitbox.setOpened(dis.readBoolean());
//						multiHitbox.flags = dis.readLong();
//						multiHitbox.getTransform(EnumGizmoTransformType.TRANSLATE).set(util.readVector3());
//						multiHitbox.getTransform(EnumGizmoTransformType.ROTATE).set(util.readVector3());
//						multiHitbox.getTransform(EnumGizmoTransformType.SCALE).set(util.readVector3());
//						this.readStack(dis, util, multiHitbox, multiHitbox.getStack());
//						stack.add(multiHitbox);
//					} break;
//					case 6: { //Decal
//						ElementDecal decal = new ElementDecal(dis.readUTF());
//						decal.setVisible(dis.readBoolean());
//						decal.decal.localDecalHandle = dis.readUTF();
//						try {
//							FileHandle handle = this.project.getProjectFolder().child(dis.readUTF());
//							SpriteStack.remove(handle);
//							decal.decal.setDecal(Decal.newDecal(SpriteStack.getTextureRegion(handle), true), handle);
//						} catch (Exception e) {
//							decal.decal.setDecal(Decal.newDecal(new TextureRegion(ResourceManager.standartDecal), true), null);
//						}
//						decal.decal.setBillboard(dis.readBoolean());
//						decal.decal.position.set(util.readVector3());
//						decal.decal.rotation.set(util.readVector3());
//						decal.decal.scale.set(util.readVector2());
//						stack.add(decal);
//					} break;
//					case 7: { //PointArray
//						ElementPointArray pointArray = new ElementPointArray(dis.readUTF());
//						pointArray.setVisible(dis.readBoolean());
//						pointArray.flags = dis.readLong();
//						pointArray.getTransform(EnumGizmoTransformType.TRANSLATE).set(util.readVector3());
//						this.readStack(dis, util, pointArray, pointArray.getStack());
//						stack.add(pointArray);
//					} break;
//					case 8: { //Point
//						ElementPoint point = new ElementPoint(dis.readUTF());
//						point.setVisible(dis.readBoolean());
//						point.getTransform(EnumGizmoTransformType.TRANSLATE).set(util.readVector3());
//						stack.add(point);
//					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	private void writeStack(DataOutputStream dos, ReaderUtil util, ElementStack stack) throws IOException {
		dos.writeInt(stack.getElements().size);
		for (TreeElement element : stack.getElements()) {
			if (element instanceof ElementFolder) {
				ElementFolder folder = (ElementFolder)element;
				dos.writeShort(0); //Folder
				dos.writeUTF(folder.getName());
				dos.writeBoolean(folder.isVisible());
				dos.writeBoolean(folder.isFolderOpened());
				this.writeStack(dos, util, folder.getFolderStack());
			}
//			else if (element instanceof ElementModel) {
//				ElementModel model = (ElementModel)element;
//				dos.writeShort(1); //Model
//				dos.writeUTF(model.getName());
//				dos.writeBoolean(model.isVisible());
//				dos.writeUTF(model.localModelHandle);
//				dos.writeUTF(this.stripProjectPath(model.modelHandle));
//				dos.writeInt(model.modelInstance.materials.size);
//				for (Material material : model.modelInstance.materials) {
//					dos.writeUTF(material.id);
//					dos.writeInt(material.size());
//					Iterator<Attribute> iterator = material.iterator(); //poor attributes...
//					while (iterator.hasNext()) {
//						util.writeAttribute(iterator.next());
//					}
//				}
//				util.writeVector3(model.getTransform(EnumGizmoTransformType.TRANSLATE));
//				util.writeVector3(model.getTransform(EnumGizmoTransformType.ROTATE));
//				util.writeVector3(model.getTransform(EnumGizmoTransformType.SCALE));
//			} else if (element instanceof ElementLight) {
//				ElementLight light = (ElementLight)element;
//				dos.writeShort(2); //Light
//				dos.writeUTF(light.getName());
//				dos.writeBoolean(light.isVisible());
//				util.writeColor(light.light.color);
//				util.writeVector3(light.light.position);
//				dos.writeFloat(light.light.intensity);
//			} else if (element instanceof ElementHitbox) {
//				ElementHitbox hitbox = (ElementHitbox)element;
//				dos.writeShort(3); //Hitbox
//				dos.writeUTF(hitbox.getName());
//				dos.writeBoolean(hitbox.isVisible());
//				dos.writeInt(hitbox.type);
//				dos.writeLong(hitbox.flags);
//				util.writeVector3(hitbox.getTransform(EnumGizmoTransformType.TRANSLATE));
//				util.writeVector3(hitbox.getTransform(EnumGizmoTransformType.ROTATE));
//				util.writeVector3(hitbox.getTransform(EnumGizmoTransformType.SCALE));
//			} else if (element instanceof ElementMeshHitbox) {
//				ElementMeshHitbox meshHitbox = (ElementMeshHitbox)element;
//				dos.writeShort(4); //MeshHitbox
//				dos.writeUTF(meshHitbox.getName());
//				dos.writeBoolean(meshHitbox.isVisible());
//				dos.writeLong(meshHitbox.flags);
//				dos.writeUTF(meshHitbox.localModelHandle);
//				dos.writeUTF(this.stripProjectPath(meshHitbox.modelHandle));
//				dos.writeInt(meshHitbox.nodeUsed);
//				util.writeVector3(meshHitbox.getTransform(EnumGizmoTransformType.TRANSLATE));
//				util.writeVector3(meshHitbox.getTransform(EnumGizmoTransformType.ROTATE));
//				util.writeVector3(meshHitbox.getTransform(EnumGizmoTransformType.SCALE));
//			} else if (element instanceof ElementMultiHitbox) {
//				ElementMultiHitbox multiHitbox = (ElementMultiHitbox)element;
//				dos.writeShort(5); //MultiHitbox
//				dos.writeUTF(multiHitbox.getName());
//				dos.writeBoolean(multiHitbox.isVisible());
//				dos.writeBoolean(multiHitbox.isStackOpened());
//				dos.writeLong(multiHitbox.flags);
//				util.writeVector3(multiHitbox.getTransform(EnumGizmoTransformType.TRANSLATE));
//				util.writeVector3(multiHitbox.getTransform(EnumGizmoTransformType.ROTATE));
//				util.writeVector3(multiHitbox.getTransform(EnumGizmoTransformType.SCALE));
//				this.writeStack(dos, util, multiHitbox.getStack());
//			} else if (element instanceof ElementDecal) {
//				ElementDecal decal = (ElementDecal)element;
//				dos.writeShort(6); //Decal
//				dos.writeUTF(decal.getName());
//				dos.writeBoolean(decal.isVisible());
//				dos.writeUTF(decal.decal.localDecalHandle);
//				dos.writeUTF(this.stripProjectPath(decal.decal.decalHandle));
//				dos.writeBoolean(decal.decal.isBillboard());
//				util.writeVector3(decal.getTransform(EnumGizmoTransformType.TRANSLATE));
//				util.writeVector3(decal.getTransform(EnumGizmoTransformType.ROTATE));
//				util.writeVector2(decal.decal.scale);
//			} else if (element instanceof ElementPointArray) {
//				ElementPointArray pointArray = (ElementPointArray)element;
//				dos.writeShort(7); //PointArray
//				dos.writeUTF(pointArray.getName());
//				dos.writeBoolean(pointArray.isVisible());
//				dos.writeLong(pointArray.flags);
//				util.writeVector3(pointArray.getTransform(EnumGizmoTransformType.TRANSLATE));
//				this.writeStack(dos, util, pointArray.getStack());
//			} else if (element instanceof ElementPoint) {
//				ElementPoint point = (ElementPoint)element;
//				dos.writeShort(8); //Point
//				dos.writeUTF(point.getName());
//				dos.writeBoolean(point.isVisible());
//				util.writeVector3(point.getTransform(EnumGizmoTransformType.TRANSLATE));
//			}
			else {
				dos.writeShort(-1);
			}
		}
	}
	
	public Throwable getLastException() {
		return this.lastException;
	}
	
	private String stripProjectPath(FileHandle handle) {
		if (handle == null) return "-";
		FileHandle projectHandle = this.project.getProjectFolder();
		return handle.path().contains(projectHandle.path()) ? handle.path().substring(projectHandle.path().length() + 1) : handle.path();
	}
	
	private boolean hasAttribute(Array<Attribute> array, Attribute attribute) {
		for (Attribute attribute$ : array) {
			if (attribute$.type == attribute.type) return true;
		}
		return false;
	}
}