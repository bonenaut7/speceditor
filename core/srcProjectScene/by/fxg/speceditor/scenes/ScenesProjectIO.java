package by.fxg.speceditor.scenes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.std.objectTree.ElementStack;
import by.fxg.speceditor.std.objectTree.ITreeElementFolder;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.objectTree.elements.ElementFolder;
import by.fxg.speceditor.std.viewport.IViewportRenderer;
import by.fxg.speceditor.utils.IOUtils;
import by.fxg.speceditor.utils.Utils;

public class ScenesProjectIO {
	/** Feature that replaces incorrect types of serializable folder classes with default folders. <br> Takes more time to save/load and space on disk **/
	public static boolean SERIALIZER_FEATURE_REPLACE_INCORRECT_TYPES_WITH_FOLDERS = true;
	
	private ScenesProject project;
	private FileHandle projectFile;
	private Throwable lastException = null;
	
	public ScenesProjectIO(ScenesProject project) {
		this.project = project;
		this.projectFile = project.getProjectFolder().child("scenes.data");
	}
	
	/** Returns true if loading was successful **/
	public boolean loadProjectData(ProjectAssetManager projectAssetManager, IViewportRenderer viewportRenderer, ElementStack inputStack) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(this.projectFile.readBytes());
			DataInputStream dis = new DataInputStream(bais);
			IOUtils util = new IOUtils(dis);
			
			dis.skip(8); //skip magic and version
			
			
			/** ProjectAssetManager section **/ {
				util.readCheckID();
				projectAssetManager.loadIndexes(dis);
			}
			
			/** Viewport section **/ {
				util.readCheckID();
				viewportRenderer.readData(util, dis);
//				String viewportClassName = dis.readUTF();
//				long viewportData = dis.readLong();
//				if (viewportRenderer.getClass().getName().equals(viewportClassName)) {
//					viewportRenderer.readData(util, dis);
//				} else {
//					try {
//						Class<?> viewportClass = Class.forName(viewportClassName);
//						if (viewportClass != null && viewportClass.getClass().isAssignableFrom(viewportClass)) {
//							viewportRenderer.readData(util, dis);
//						} else {
//							Utils.logWarn("[ScenesProjectIO] Unable to load Viewport data because of incorrect present viewport!");
//							Utils.logDebug("[ScenesProjectIO] Viewport present: ", viewportRenderer.getClass().getTypeName());
//							Utils.logDebug("[ScenesProjectIO] Viewport needed: ", viewportClassName);
//							dis.skip(viewportData);
//						}
//					} catch (ClassNotFoundException classNotFoundException) {
//						Utils.logWarn("[ScenesProjectIO] Unable to load Viewport data because of incorrect present viewport!");
//						Utils.logDebug("[ScenesProjectIO] Viewport present: ", viewportRenderer.getClass().getTypeName());
//						Utils.logDebug("[ScenesProjectIO] Viewport needed: ", viewportClassName);
//						Utils.logError(classNotFoundException, "ScenesProjectIO", "Unable to load viewport data. Viewport class not found. Skipping...");
//						dis.skip(viewportData);
//					}
//				}	
			}
			
			/** ObjectTree section **/ {
				util.readCheckID();
				Array<Class<?>> types = new Array<>();
				int typesSize = dis.readInt();
				for (int i = 0; i != typesSize; i++) {
					try {
						types.add(Class.forName(dis.readUTF()));
					} catch (ClassNotFoundException e) {
						types.add(null);
					}
				}
				this.readStack(types, util, dis, inputStack);
			}
			
			dis.close();
			bais.close();
			return true;
		} catch (Throwable exception) {
			exception.printStackTrace();
			this.lastException = exception;
		}
		return false;
	}
	
	/** Returns true if loading was successful **/
	public boolean writeProjectData(ProjectAssetManager projectAssetManager, IViewportRenderer viewportRenderer, ElementStack outputStack) {
		try {
			this.project.getProjectFolder().file().mkdirs();
			this.projectFile.file().createNewFile();
			FileOutputStream fos = new FileOutputStream(this.projectFile.file());
			DataOutputStream dos = new DataOutputStream(fos);
			IOUtils util = new IOUtils(dos);
			
			dos.writeInt(0xBADF05CE); //SCE - scenes format magic
			dos.writeInt(0x00000001); //version
			
			/** ProjectAssetManager section **/ {
				util.writeCheckID();
				projectAssetManager.saveIndexes(dos);
			}
			
			/** Viewport section **/ {
				util.writeCheckID();
				viewportRenderer.writeData(util, dos);
//				dos.writeUTF(viewportRenderer.getClass().getTypeName());
//				
//				ByteArrayOutputStream viewportByteArrayOutputStream = new ByteArrayOutputStream();
//				DataOutputStream viewportDataOutputStream = new DataOutputStream(viewportByteArrayOutputStream);
//				viewportRenderer.writeData(util, viewportDataOutputStream);
//				dos.writeLong(viewportByteArrayOutputStream.size());
//				dos.write(viewportByteArrayOutputStream.toByteArray());
			}
			
			/** ObjectTree section **/ {
				util.writeCheckID();
				Array<Class<?>> types = new Array<>();
				this.scanTypesForSerialization(types, outputStack);
				
				dos.writeInt(types.size);
				for (int i = 0; i != types.size; i++) dos.writeUTF(types.get(i).getTypeName());
				this.writeStack(types, util, dos, outputStack);
			}
			
			dos.close();
			fos.close();
			return true;
		} catch (IOException exception) {
			exception.printStackTrace();
			this.lastException = exception;
		}
		return false;
	}
	
	private void readStack(Array<Class<?>> types, IOUtils util, DataInputStream dis, ElementStack stack) throws IOException {
		byte bitmask;
		int index;
		
		int size = dis.readInt();
		for (int i = 0; i != size; i++) {
			bitmask = dis.readByte();
			if ((bitmask & 2) == 2) { //checking for 1st flag (Serialization)
				if ((index = dis.readInt()) > -1) {
					try {
						TreeElement element = (TreeElement)types.get(index).newInstance();
						element.deserialize(util, dis);
						if ((bitmask & 4) == 4) { //checking for 2nd flag (ElementStack)
							this.readStack(types, util, dis, ((ITreeElementFolder)element).getFolderStack());
						}
						stack.add(element);
						continue; //skipping other part of cycle
					} catch (InstantiationException | IllegalAccessException exception) {
						//halt whole program... (in case if try-catch block in the update loop won't be able to handle this)
						Utils.logError(exception, "ScenesProjectsIO", "Unable to instantiate new TreeElement object. Index: ", index, ", Type: ", types.get(index).getTypeName());
					}
				}
			}
			
			if (SERIALIZER_FEATURE_REPLACE_INCORRECT_TYPES_WITH_FOLDERS && (bitmask & 4) == 4) { //checking for 2nd flag (ElementStack)
				ElementFolder folder = new ElementFolder("[Incorrect folder-type element]");
				this.readStack(types, util, dis, folder.getFolderStack());
				stack.add(folder);
			}
		}
	}
	
	// Bitmask flags: 2^1: serialization, 2^2: contains ElementStack
	private void writeStack(Array<Class<?>> types, IOUtils util, DataOutputStream dos, ElementStack stack) throws IOException {
		byte bitmask;
		int index;
		
		dos.writeInt(stack.getElements().size);
		for (int i = 0; i != stack.getElements().size; i++) {
			bitmask = 0x00;
			TreeElement element = stack.getElements().get(i);
			
			if (element instanceof ITreeElementFolder) bitmask |= 4; //adding 2nd flag if object have ElementStack
			try {
				if (element.getClass().getDeclaredConstructor() != null) {
					bitmask |= 2; //adding 1st flag if we can serialize object
					dos.writeByte(bitmask);
					dos.writeInt(index = types.indexOf(element.getClass(), true));
					if (index > -1) element.serialize(util, dos);
				} else dos.writeByte(bitmask);
			} catch (NoSuchMethodException | SecurityException e) {
				//if constructor is not present or not accessable.
				dos.writeByte(bitmask);
			}
			
			if (SERIALIZER_FEATURE_REPLACE_INCORRECT_TYPES_WITH_FOLDERS && element instanceof ITreeElementFolder) {
				this.writeStack(types, util, dos, ((ITreeElementFolder)element).getFolderStack());
			}
		}
	}
	
	private void scanTypesForSerialization(Array<Class<?>> typesArray, ElementStack stack) throws IOException {
		for (int i = 0; i != stack.getElements().size; i++) {
			Object object = stack.getElements().get(i);
			if (object instanceof TreeElement && !typesArray.contains(object.getClass(), true)) typesArray.add(object.getClass());
			if (object instanceof ITreeElementFolder) this.scanTypesForSerialization(typesArray, ((ITreeElementFolder)object).getFolderStack());	
		}
	}
	
	public Throwable getLastException() {
		return this.lastException;
	}
}