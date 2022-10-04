package by.fxg.speceditor.utils;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.project.Project;

public class Utils {
	
	//Opens dialog to open some file
	public static FileHandle selectFileDialog(String desc, String... extensions) { return selectFileDialog(Project.instance.projectFolder, desc, extensions); }
	public static FileHandle selectFileDialog(FileHandle directory, String desc, String... extensions) {
		JFrame frame = new JFrame();
		frame.setAlwaysOnTop(true);
		JFileChooser fileChooser = new JFileChooser();
		if (directory != null && directory.exists()) fileChooser.setCurrentDirectory(directory.file());
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new FileNameExtensionFilter(desc, extensions));
		fileChooser.setDialogTitle("Open file");
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
			FileHandle absolute = Gdx.files.absolute(fileChooser.getSelectedFile().getAbsolutePath());
			FileHandle projectFolder = Project.instance.projectFolder;
			if (absolute != null && absolute.path().contains(projectFolder.path())) {
				String[] splitted = absolute.path().split(projectFolder.path());
				return projectFolder.child(splitted[splitted.length - 1]);
			}
		}
		return null;
	}
	
	//???
	public static TextureRegion selectProjectTexture() {
		return null;
	}
	
	public static void logInfo(String tag, String message) {
		System.err.println(String.format("[INFO] %s: %s", tag, message));
	}
	
	public static void logError(Throwable throwable, String tag, String message) {
		System.err.println(String.format("[ERROR] %s: %s", tag, message));
		if (Game.DEBUG && throwable != null) throwable.printStackTrace();
	}
	
	public static void logDebug(String tag, String message) {
		if (Game.DEBUG) System.err.println(String.format("[DEBUG] %s: %s", tag, message));
	}
}
