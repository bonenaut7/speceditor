package by.fxg.speceditor.utils;

import java.text.DecimalFormat;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.project.ProjectManager;

public class Utils {
	
	//Opens dialog to open some file
	public static FileHandle selectFileDialog(String desc, String... extensions) { return selectFileDialog(ProjectManager.currentProject.getProjectFolder(), desc, extensions); }
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
			FileHandle projectFolder = ProjectManager.currentProject.getProjectFolder();
			if (absolute != null && absolute.path().contains(projectFolder.path())) {
				String[] splitted = absolute.path().split(projectFolder.path());
				return projectFolder.child(splitted[splitted.length - 1]);
			}
		}
		return null;
	}
	
	public static int getWidth() { return Game.get.width; }
	public static int getHeight() { return Game.get.height; }
	
	private static final String[] PREDEFINED_DF_FORMATS = {"#", "#.#", "#.##", "#.###", "#.####", "#.#####", "#.######"};
	private static final DecimalFormat PREDEFINED_DF = new DecimalFormat("#");
	public static String dFormat(double value, int symbolsAfterDot) {
		PREDEFINED_DF.applyPattern(PREDEFINED_DF_FORMATS[symbolsAfterDot]);
		return PREDEFINED_DF.format(value);
	}
	
	public static String format(Object... objects) {
		StringBuilder builder = new StringBuilder();
		for (Object object : objects) builder.append(object);
		return builder.toString();
	}
	
	public static void logInfo(String tag, Object... objects) { System.err.println(String.format("[INFO] %s: %s", tag, format(objects))); }
	public static void logWarn(String tag, Object... objects) { System.err.println(String.format("[WARN] %s: %s", tag, format(objects))); }
	public static void logError(Throwable throwable, String tag, Object... objects) {
		System.err.println(String.format("[ERROR] %s: %s", tag, format(objects)));
		if (Game.DEBUG && throwable != null) throwable.printStackTrace();
	}
	public static void logDebug(Object... objects) { if (Game.DEBUG) System.err.println(String.format("[DEBUG] %s", format(objects))); }	
}
