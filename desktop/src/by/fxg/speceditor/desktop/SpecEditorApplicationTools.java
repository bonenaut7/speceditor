package by.fxg.speceditor.desktop;

import com.badlogic.gdx.Gdx;

import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.screen.gui.GuiProjectExitSave;
import by.fxg.speceditor.utils.ApplicationTools;

public class SpecEditorApplicationTools implements ApplicationTools {
	public boolean isAppExitAllowed() {
		if (ProjectManager.currentProject != null) {
			SpecEditor.get.renderer.currentGui = new GuiProjectExitSave();
			return false;
		}
		return true;
	}

	public void exitExplicitly() {
		((LwjglApplicationSpecEditor)Gdx.app).exitExplicitly();
	}
}
