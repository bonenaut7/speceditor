package by.fxg.speceditor.scenes;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.InputChunked;
import com.esotericsoftware.kryo.io.OutputChunked;

import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.serialization.SpecEditorSerialization;
import by.fxg.speceditor.std.objectTree.ElementStack;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.viewport.IViewportRenderer;

public class ScenesProjectIO {
	private ScenesProject project;
	private Throwable lastException = null;
	
	public ScenesProjectIO(ScenesProject project) {
		this.project = project;
	}
	
	/** Returns true if loading was successful **/
	public boolean loadProjectData(FileHandle file, ProjectAssetManager projectAssetManager, IViewportRenderer viewportRenderer, SpecObjectTree objectTree) {
		if (file == null || !file.exists() || file.isDirectory()) return false;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(file.readBytes());
			DataInputStream dis = new DataInputStream(bais);
			Kryo kryo = SpecEditorSerialization.INSTANCE.kryo;
			
			if (dis.readInt() != 0xBADF05CE) { //incorrect file tryin to be loaded ;(
				dis.close();
				bais.close();
				return false;
			}
			int version = dis.readInt();
			
			/** ProjectAssetManager section **/ {
				projectAssetManager.loadIndexes(dis);
			}
			
			InputChunked input = new InputChunked(dis);
			/** Viewport section **/ {
				viewportRenderer.readData(kryo, input);
				input.nextChunks();
			}
			
			/** ObjectTree section **/ {
				objectTree.setStack(kryo.readObject(input, ElementStack.class));
			}
			
			input.close();
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
	public boolean writeProjectData(FileHandle destFile, ProjectAssetManager projectAssetManager, IViewportRenderer viewportRenderer, SpecObjectTree objectTree) {
		if (destFile == null || destFile.exists() && destFile.isDirectory()) return false;
		try {
			destFile.parent().mkdirs();
			destFile.file().createNewFile();
			FileOutputStream fos = new FileOutputStream(destFile.file());
			DataOutputStream dos = new DataOutputStream(fos);
			
			Kryo kryo = SpecEditorSerialization.INSTANCE.kryo;

			dos.writeInt(0xBADF05CE); //SCE - scenes format magic
			dos.writeInt(0x00000001); //version
			
			/** ProjectAssetManager section **/ {
				projectAssetManager.saveIndexes(dos);
			}
			
			//End of usual DataOutputStream manipulations
			OutputChunked output = new OutputChunked(dos);
			/** Viewport section **/ {
				viewportRenderer.writeData(kryo, output);
				output.endChunks();
			}
			
			/** ObjectTree section **/ {
				kryo.writeObject(output, objectTree.getStack());
				output.endChunks();
			}
			
			output.close();
			dos.close();
			fos.close();
			return true;
		} catch (IOException exception) {
			exception.printStackTrace();
			this.lastException = exception;
		}
		return false;
	}

	public Throwable getLastException() {
		return this.lastException;
	}
}