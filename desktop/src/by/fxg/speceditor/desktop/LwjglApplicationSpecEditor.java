package by.fxg.speceditor.desktop;

import java.awt.Canvas;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglGraphics;

import by.fxg.speceditor.SpecEditor;

public class LwjglApplicationSpecEditor extends LwjglApplication {
	private final SpecEditor specEditor;
	
	public LwjglApplicationSpecEditor(SpecEditor specEditor, String title, int width, int height) {
		super(specEditor, title, width, height);
		this.specEditor = specEditor;
	}
	
	public LwjglApplicationSpecEditor(SpecEditor specEditor) {
		super(specEditor);
		this.specEditor = specEditor;
	}
	
	public LwjglApplicationSpecEditor(SpecEditor specEditor, LwjglApplicationConfiguration config) {
		super(specEditor, config);
		this.specEditor = specEditor;
	}
	
	public LwjglApplicationSpecEditor(SpecEditor specEditor, Canvas canvas) {
		super(specEditor, canvas);
		this.specEditor = specEditor;
	}
	
	public LwjglApplicationSpecEditor(SpecEditor specEditor, LwjglApplicationConfiguration config, Canvas canvas) {
		super(specEditor, config, canvas);
		this.specEditor = specEditor;
	}
	
	public LwjglApplicationSpecEditor(SpecEditor specEditor, LwjglApplicationConfiguration config, LwjglGraphics graphics) {
		super(specEditor, config, graphics);
		this.specEditor = specEditor;
	}
	
	public void exit() {
		if (SpecEditor.get.tools.isAppExitAllowed()) {
			super.exit();
		}
	}
	
	public void exitExplicitly() {
		super.exit();
	}
}
