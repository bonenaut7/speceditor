package by.fxg.speceditor.std.viewport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import by.fxg.speceditor.std.editorPane.EditorPane;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.utils.IOUtils;

public interface IViewportRenderer {
	/** Adds object to renderer **/
	void add(SpecObjectTree objectTree, Object object, Object... args);
	/** Clears data from renderer **/
	void clear();
	/** Called after position and direction set to camera and before <code>camera.update();</code> **/
	default void updateCamera() { this.getCamera().update(); }
	/** Use framebuffer to store rendered info to the color buffer, and then return it in the {@link #getTexture()} method**/
	void render();
	/** Color buffer from framebuffer, returns rendered info **/
	TextureRegion getTexture();
	/** Renderer's camera**/
	PerspectiveCamera getCamera();
	/** Editor pane of the renderer **/
	EditorPane getEditorPane();
	
	/** Method needed to write settings of ViewportRenderer to the project data **/
	default void writeData(IOUtils ioUtils, DataOutputStream dataOutputStream) throws IOException {}
	/** Method needed to read settings of ViewportRenderer from the project data **/
	default void readData(IOUtils ioUtils, DataInputStream dataInputStream) throws IOException {}
}
