package by.fxg.speceditor.desktop;

import javax.swing.UIManager;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import by.fxg.speceditor.SpecEditor;

public class DesktopLauncher {
	public static void main (String[] args) {
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { }
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1366;
		config.height = 768;
		config.title = "SpecEditor";
		//config.vSyncEnabled=false;config.foregroundFPS=99999; 10.11.2022, 1.2-1.5k fps (<1ms per frame)
		SpecEditor specEditor = new SpecEditor(new SpecEditorApplicationTools(), args);
		new LwjglApplicationSpecEditor(specEditor, config);
	}
}
