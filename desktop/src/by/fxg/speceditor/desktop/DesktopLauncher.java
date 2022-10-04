package by.fxg.speceditor.desktop;

import javax.swing.UIManager;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import by.fxg.speceditor.Game;

public class DesktopLauncher {
	public static void main (String[] args) {
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { }
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1366;
		config.height = 768;
		//config.resizable = false; //POHUI
		config.title = "SpecEditor";
		new LwjglApplication(new Game().setProgramArgs(args), config);
	}
}
