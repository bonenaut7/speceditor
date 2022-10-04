package by.fxg.speceditor.desktop;

import java.nio.IntBuffer;

import javax.swing.UIManager;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.utils.PlatformIntegration;

public class DesktopLauncher {
	public static void main (String[] args) {
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { }
		
		Game.platformIntegration = new DesktopIntegration();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1366;
		config.height = 768;
		//config.resizable = false; //POHUI
		config.title = "SpecEditor";
		new LwjglApplication(new Game().setProgramArgs(args), config);
	}
	
	public static class DesktopIntegration extends PlatformIntegration {
		private static Cursor emptyCursor;
		
		public DesktopIntegration() {
			FEATURE_APP_CURSOR = true;
		}
		
		public void onUpdate() {
			try {
				if (emptyCursor == null) {
					if (Mouse.isCreated()) {
						int min = org.lwjgl.input.Cursor.getMinCursorSize();
						IntBuffer tmp = BufferUtils.createIntBuffer(min * min);
						emptyCursor = new org.lwjgl.input.Cursor(min, min, min / 2, min / 2, 1, tmp, null);
					}
				} else if (Mouse.isInsideWindow()) {
					Mouse.setNativeCursor(emptyCursor);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
