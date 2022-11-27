package by.fxg.speceditor.std.viewport;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import by.fxg.speceditor.std.editorPane.EditorPane;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;

public interface IViewportRenderer {
	/** Adds object to renderer **/
	void add(SpecObjectTree objectTree, Object object, Object... args);
	/** Clears data from renderer **/
	void reset();
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
	
	default void resize(int x, int y, int width, int height) {}
	
	/** Method needed to write settings of ViewportRenderer to the project data **/
	default void writeData(Kryo kryo, Output output) {}
	/** Method needed to read settings of ViewportRenderer from the project data **/
	default void readData(Kryo kryo, Input input) {}
}
