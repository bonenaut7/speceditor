package by.fxg.pilesos.specformat.editor;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.GameManager;
import by.fxg.speceditor.hc.elementlist.ElementStack;
import by.fxg.speceditor.hc.elementlist.EnumTransform;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.TreeElement;
import by.fxg.speceditor.hc.elementlist.elements.ElementDecal;
import by.fxg.speceditor.hc.elementlist.elements.ElementFolder;
import by.fxg.speceditor.hc.elementlist.elements.ElementHitbox;
import by.fxg.speceditor.hc.elementlist.elements.ElementLight;
import by.fxg.speceditor.hc.elementlist.elements.ElementMeshHitbox;
import by.fxg.speceditor.hc.elementlist.elements.ElementModel;
import by.fxg.speceditor.hc.elementlist.elements.ElementMultiHitbox;
import by.fxg.speceditor.hc.elementlist.elements.ElementPoint;
import by.fxg.speceditor.hc.elementlist.elements.ElementPointArray;
import by.fxg.speceditor.hc.elementlist.renderables.TERModel_Default;
import by.fxg.speceditor.project.Project;
import by.fxg.speceditor.project.ProjectType;

public class SpecFormatSaver {
	private static final byte[] headerMagic = {(byte)0xFF, (byte)0xBA, (byte)0xDF, (byte)0xFF};
	private static final byte[] saveMagic = {(byte)0xDD, (byte)0xBA, (byte)0xDF, (byte)0xDD};
	private Project project;
	
	public SpecFormatSaver(Project project) {
		this.project = project;
	}
	
	public void loadProjectHeader() {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(this.project.projectHeader.readBytes());
			DataInputStream dis = new DataInputStream(bais);
			
			dis.skip(8); //skip magic and version
			this.project.projectType = ProjectType.values()[dis.readUnsignedShort()];
			this.project.projectName = dis.readUTF();
			this.project.lastSaveDate = dis.readUTF();
			this.project.backupSaving = dis.readBoolean();
			
			dis.close();
			bais.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeProjectHeader() {
		try {
			this.project.projectFolder.file().mkdirs();
			this.project.projectHeader.file().createNewFile();
			FileOutputStream fos = new FileOutputStream(this.project.projectHeader.file());
			DataOutputStream dos = new DataOutputStream(fos);
			
			dos.write(headerMagic);
			dos.writeInt(0); //version
			dos.writeShort(this.project.projectType.ordinal());
			dos.writeUTF(this.project.projectName);
			dos.writeUTF(this.project.lastSaveDate);
			dos.writeBoolean(this.project.backupSaving);
			
			dos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadProjectData(PMObjectExplorer pmoe) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(this.project.saveFile.readBytes());
			DataInputStream dis = new DataInputStream(bais);
			ReaderUtil util = new ReaderUtil(this.project, dis);
			dis.skip(8); //skip magic and version
			util.readCheckID();
			this.project.viewportHitboxDepth = dis.readBoolean();
			this.project.viewportHitboxWidth = dis.readFloat();
			this.project.bufferColor = util.readColor();
			this.project.cameraSettings = util.readVector3();
			this.project.viewportAttributes.add(new BlendingAttribute(1f), FloatAttribute.createAlphaTest(0.5f));
			int attributesSize = dis.readInt();
			for (int i = 0; i != attributesSize; i++) {
				Attribute attribute = util.readAttribute();
				if (!this.hasAttribute(this.project.viewportAttributes, attribute)) this.project.viewportAttributes.add(attribute);
			}
			
			util.readCheckID();
			this.readStack(dis, util, null, pmoe.elementStack);
			
			dis.close();
			bais.close();
			
			Project.renderer.update();
		} catch (Exception e) {
			e.printStackTrace();
			this.project.viewportAttributes.add(new BlendingAttribute(1f), FloatAttribute.createAlphaTest(0.5f));
			Project.renderer.update();
		}
	}
	
	public void writeProjectData(PMObjectExplorer pmoe) {
		try {
			this.project.projectFolder.file().mkdirs();
			this.project.saveFile.file().createNewFile();
			FileOutputStream fos = new FileOutputStream(this.project.saveFile.file());
			DataOutputStream dos = new DataOutputStream(fos);
			ReaderUtil util = new ReaderUtil(this.project, dos);
			
			dos.write(saveMagic);
			dos.writeInt(0);
			util.writeCheckID(); //viewportData
			dos.writeBoolean(this.project.viewportHitboxDepth);
			dos.writeFloat(this.project.viewportHitboxWidth);
			util.writeColor(this.project.bufferColor);
			util.writeVector3(this.project.cameraSettings);
			dos.writeInt(this.project.viewportAttributes.size);
			for (Attribute attribute : this.project.viewportAttributes) {
				util.writeAttribute(attribute);
			}
			
			util.writeCheckID(); //pmoe
			this.writeStack(dos, util, pmoe.elementStack);
			
			dos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
						folder.setOpened(dis.readBoolean());
						this.readStack(dis, util, folder, folder.getStack());
						stack.add(folder);
					} break;
					case 1: { //Model
						ElementModel model = new ElementModel(dis.readUTF());
						model.setVisible(dis.readBoolean());
						model.setLocalHandle(dis.readUTF());
						try {
							model.modelHandle = this.project.projectFolder.child(dis.readUTF());
							AssetDescriptor<Model> assetDescriptor = new AssetDescriptor<Model>(model.modelHandle, Model.class);
							if (!Game.get.manager.assetManager.isLoaded(assetDescriptor)) {
								Game.get.manager.assetManager.load(assetDescriptor);
								Game.get.manager.assetManager.finishLoading();
							}
							model.modelInstance = new ModelInstance(Game.get.manager.assetManager.get(model.modelHandle.path(), Model.class));
						} catch (Exception e) {
							model.modelHandle = null;
							model.modelInstance = new ModelInstance(GameManager.standartModel);
						}
						int materialsSize = dis.readInt();
						String[] materialID = new String[materialsSize];
						for (int j = 0; j != materialsSize; j++) {
							dis.readUTF();
							model.modelInstance.materials.get(j).clear();
							int materialAttributesSize = dis.readInt();
							for (int k = 0; k != materialAttributesSize; k++) {
								model.modelInstance.materials.get(j).set(util.readAttribute());
							}
							materialID[j] = model.modelInstance.materials.get(j).id;
						}
						((TERModel_Default)model.getRenderable()).setMaterials(materialID);
						
						model.getTransform(EnumTransform.TRANSLATE).set(util.readVector3());
						model.getTransform(EnumTransform.ROTATE).set(util.readVector3());
						model.getTransform(EnumTransform.SCALE).set(util.readVector3());
						stack.add(model);
					} break;
					case 2: { //Light
						ElementLight light = new ElementLight(dis.readUTF());
						light.setVisible(dis.readBoolean());
						light.light.color.set(util.readColor());
						light.light.position.set(util.readVector3());
						light.light.intensity = dis.readFloat();
						stack.add(light);
					} break;
					case 3: { //Hitbox
						ElementHitbox hitbox = new ElementHitbox(dis.readUTF());
						hitbox.setVisible(dis.readBoolean());
						hitbox.type = dis.readInt();
						hitbox.flags = dis.readLong();
						hitbox.getTransform(EnumTransform.TRANSLATE).set(util.readVector3());
						hitbox.getTransform(EnumTransform.ROTATE).set(util.readVector3());
						hitbox.getTransform(EnumTransform.SCALE).set(util.readVector3());
						if (parent instanceof ElementMultiHitbox) hitbox.parent = (ElementMultiHitbox)parent;
						stack.add(hitbox);
					} break;
					case 4: {
						ElementMeshHitbox meshHitbox = new ElementMeshHitbox(dis.readUTF());
						meshHitbox.setVisible(dis.readBoolean());
						meshHitbox.flags = dis.readLong();
						meshHitbox.setLocalHandle(dis.readUTF());
						try {
							FileHandle handle = this.project.projectFolder.child(dis.readUTF());
							AssetDescriptor<Model> assetDescriptor = new AssetDescriptor<Model>(handle, Model.class);
							if (!Game.get.manager.assetManager.isLoaded(assetDescriptor)) {
								Game.get.manager.assetManager.load(assetDescriptor);
								Game.get.manager.assetManager.finishLoading();
							}
							meshHitbox.setModel(handle, Game.get.manager.assetManager.get(handle.path(), Model.class));
						} catch (Exception e) {
							meshHitbox.setModel(null, GameManager.standartModel);
						}
						meshHitbox.nodeUsed = dis.readInt();
						meshHitbox.changeShape(meshHitbox.nodeUsed + 1);
						meshHitbox.getTransform(EnumTransform.TRANSLATE).set(util.readVector3());
						meshHitbox.getTransform(EnumTransform.ROTATE).set(util.readVector3());
						meshHitbox.getTransform(EnumTransform.SCALE).set(util.readVector3());
						if (parent instanceof ElementMultiHitbox) meshHitbox.parent = (ElementMultiHitbox)parent;
						stack.add(meshHitbox);
					} break;
					case 5: { //MultiHitbox
						ElementMultiHitbox multiHitbox = new ElementMultiHitbox(dis.readUTF());
						multiHitbox.setVisible(dis.readBoolean());
						multiHitbox.setOpened(dis.readBoolean());
						multiHitbox.flags = dis.readLong();
						multiHitbox.getTransform(EnumTransform.TRANSLATE).set(util.readVector3());
						multiHitbox.getTransform(EnumTransform.ROTATE).set(util.readVector3());
						multiHitbox.getTransform(EnumTransform.SCALE).set(util.readVector3());
						this.readStack(dis, util, multiHitbox, multiHitbox.getStack());
						stack.add(multiHitbox);
					} break;
					case 6: { //Decal
						ElementDecal decal = new ElementDecal(dis.readUTF());
						decal.setVisible(dis.readBoolean());
						decal.setLocalHandle(dis.readUTF());
						try {
							FileHandle handle = this.project.projectFolder.child(dis.readUTF());
							SpriteStack.remove(handle);
							decal.decal.setDecal(Decal.newDecal(SpriteStack.getTextureRegion(handle), true), handle);
						} catch (Exception e) {
							decal.decal.setDecal(Decal.newDecal(new TextureRegion(GameManager.standartDecal), true), null);
						}
						decal.decal.setBillboard(dis.readBoolean());
						decal.decal.position.set(util.readVector3());
						decal.decal.rotation.set(util.readVector3());
						decal.decal.scale.set(util.readVector2());
						stack.add(decal);
					} break;
					case 7: { //PointArray
						ElementPointArray pointArray = new ElementPointArray(dis.readUTF());
						pointArray.setVisible(dis.readBoolean());
						pointArray.flags = dis.readLong();
						pointArray.getTransform(EnumTransform.TRANSLATE).set(util.readVector3());
						this.readStack(dis, util, pointArray, pointArray.getStack());
						stack.add(pointArray);
					} break;
					case 8: { //Point
						ElementPoint point = new ElementPoint(dis.readUTF());
						point.setVisible(dis.readBoolean());
						point.getTransform(EnumTransform.TRANSLATE).set(util.readVector3());
						stack.add(point);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeStack(DataOutputStream dos, ReaderUtil util, ElementStack stack) throws IOException {
		dos.writeInt(stack.getItems().size);
		for (TreeElement element : stack.getItems()) {
			if (element instanceof ElementFolder) {
				ElementFolder folder = (ElementFolder)element;
				dos.writeShort(0); //Folder
				dos.writeUTF(folder.getName());
				dos.writeBoolean(folder.isVisible());
				dos.writeBoolean(folder.isStackOpened());
				this.writeStack(dos, util, folder.getStack());
			} else if (element instanceof ElementModel) {
				ElementModel model = (ElementModel)element;
				dos.writeShort(1); //Model
				dos.writeUTF(model.getName());
				dos.writeBoolean(model.isVisible());
				dos.writeUTF(model.localModelHandle);
				dos.writeUTF(this.stripProjectPath(model.modelHandle));
				dos.writeInt(model.modelInstance.materials.size);
				for (Material material : model.modelInstance.materials) {
					dos.writeUTF(material.id);
					dos.writeInt(material.size());
					Iterator<Attribute> iterator = material.iterator(); //poor attributes...
					while (iterator.hasNext()) {
						util.writeAttribute(iterator.next());
					}
				}
				util.writeVector3(model.getTransform(EnumTransform.TRANSLATE));
				util.writeVector3(model.getTransform(EnumTransform.ROTATE));
				util.writeVector3(model.getTransform(EnumTransform.SCALE));
			} else if (element instanceof ElementLight) {
				ElementLight light = (ElementLight)element;
				dos.writeShort(2); //Light
				dos.writeUTF(light.getName());
				dos.writeBoolean(light.isVisible());
				util.writeColor(light.light.color);
				util.writeVector3(light.light.position);
				dos.writeFloat(light.light.intensity);
			} else if (element instanceof ElementHitbox) {
				ElementHitbox hitbox = (ElementHitbox)element;
				dos.writeShort(3); //Hitbox
				dos.writeUTF(hitbox.getName());
				dos.writeBoolean(hitbox.isVisible());
				dos.writeInt(hitbox.type);
				dos.writeLong(hitbox.flags);
				util.writeVector3(hitbox.getTransform(EnumTransform.TRANSLATE));
				util.writeVector3(hitbox.getTransform(EnumTransform.ROTATE));
				util.writeVector3(hitbox.getTransform(EnumTransform.SCALE));
			} else if (element instanceof ElementMeshHitbox) {
				ElementMeshHitbox meshHitbox = (ElementMeshHitbox)element;
				dos.writeShort(4); //MeshHitbox
				dos.writeUTF(meshHitbox.getName());
				dos.writeBoolean(meshHitbox.isVisible());
				dos.writeLong(meshHitbox.flags);
				dos.writeUTF(meshHitbox.localModelHandle);
				dos.writeUTF(this.stripProjectPath(meshHitbox.modelHandle));
				dos.writeInt(meshHitbox.nodeUsed);
				util.writeVector3(meshHitbox.getTransform(EnumTransform.TRANSLATE));
				util.writeVector3(meshHitbox.getTransform(EnumTransform.ROTATE));
				util.writeVector3(meshHitbox.getTransform(EnumTransform.SCALE));
			} else if (element instanceof ElementMultiHitbox) {
				ElementMultiHitbox multiHitbox = (ElementMultiHitbox)element;
				dos.writeShort(5); //MultiHitbox
				dos.writeUTF(multiHitbox.getName());
				dos.writeBoolean(multiHitbox.isVisible());
				dos.writeBoolean(multiHitbox.isStackOpened());
				dos.writeLong(multiHitbox.flags);
				util.writeVector3(multiHitbox.getTransform(EnumTransform.TRANSLATE));
				util.writeVector3(multiHitbox.getTransform(EnumTransform.ROTATE));
				util.writeVector3(multiHitbox.getTransform(EnumTransform.SCALE));
				this.writeStack(dos, util, multiHitbox.getStack());
			} else if (element instanceof ElementDecal) {
				ElementDecal decal = (ElementDecal)element;
				dos.writeShort(6); //Decal
				dos.writeUTF(decal.getName());
				dos.writeBoolean(decal.isVisible());
				dos.writeUTF(decal.decal.localDecalHandle);
				dos.writeUTF(this.stripProjectPath(decal.decal.decalHandle));
				dos.writeBoolean(decal.decal.isBillboard());
				util.writeVector3(decal.getTransform(EnumTransform.TRANSLATE));
				util.writeVector3(decal.getTransform(EnumTransform.ROTATE));
				util.writeVector2(decal.decal.scale);
			} else if (element instanceof ElementPointArray) {
				ElementPointArray pointArray = (ElementPointArray)element;
				dos.writeShort(7); //PointArray
				dos.writeUTF(pointArray.getName());
				dos.writeBoolean(pointArray.isVisible());
				dos.writeLong(pointArray.flags);
				util.writeVector3(pointArray.getTransform(EnumTransform.TRANSLATE));
				this.writeStack(dos, util, pointArray.getStack());
			} else if (element instanceof ElementPoint) {
				ElementPoint point = (ElementPoint)element;
				dos.writeShort(8); //Point
				dos.writeUTF(point.getName());
				dos.writeBoolean(point.isVisible());
				util.writeVector3(point.getTransform(EnumTransform.TRANSLATE));
			} else {
				dos.writeShort(-1);
			}
		}
	}
	
	private String stripProjectPath(FileHandle handle) {
		if (handle == null) return "-";
		FileHandle projectHandle = this.project.projectFolder;
		return handle.path().contains(projectHandle.path()) ? handle.path().substring(projectHandle.path().length() + 1) : handle.path();
	}
	
	private boolean hasAttribute(Array<Attribute> array, Attribute attribute) {
		for (Attribute attribute$ : array) {
			if (attribute$.type == attribute.type) return true;
		}
		return false;
	}
}
