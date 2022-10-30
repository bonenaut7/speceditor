package by.fxg.speceditor.utils;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.project.ProjectManager;

public class Utils {
	private static final Pattern timePattern = Pattern.compile("([0-9]+)([wdhms])");
	public static final String[] 
		MODELS_EXTENSIONS = {"obj", "g3db", "g3dj", "gltf", "glb"},
		IMAGES_EXTENSIONS = {"png", "jpg", "jpeg", "etc1"};
	public static final String
		MODELS_DESCRIPTION = "Supported models (*.obj; *.g3db; *.g3dj; *.gltf; *.glb)",
		IMAGES_DESCRIPTION = "Supported images (*.png; *.jpg; *.jpeg; *.etc1)";
	//Opens dialog to open some file
	
	public static FileHandle openFileSelectionDialog(String desc, String... extensions) { return openFileSelectionDialog(ProjectManager.currentProject.getProjectFolder(), desc, extensions); }
	public static FileHandle openFileSelectionDialog(FileHandle directory, String desc, String... extensions) {
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
	
	/** example: <br>
	 * 	Target Handle: coreFolder/ <br>
	 *  Absolute Path: C:/projects/coreFolder/images/file9.png <br>
	 *  Returns: coreFolder/images/file9.png as Target Handle's child (or null if not possible) **/
	@Deprecated //FIXME
	public static FileHandle intersectCombinePaths(FileHandle targetHandle, String absolutePath) {
		String targetPath = targetHandle.path(); 
		if (absolutePath.contains(targetPath)) {
			String[] subParts = absolutePath.split(targetPath);
			if (subParts.length > 2) {
				//concurrent path
				//example: [/path/]TARGET[/]TARGET[/path.path]
				
				for (int i = 0; i != subParts.length; i++) {
					//FileHandle child 
				}
			} else {
				//exact path without concurrencies
				//example: [/path/]TARGET[/path.path]
				return targetHandle.child(subParts[subParts.length - 1]);
			}
		}
		return null;
	}
	
	/** Returns parsed time in seconds in format: [1D 2H 3M 4S 5D...] 
	 *  Time units: [S]econds, [M]inutes, [H]ours, [D]ays, [W]eeks
	 * **/
	public static long parseTime(String string) {
		if (string != null) {
			long time = 0L;
			Matcher matcher = timePattern.matcher(string.toLowerCase());
			while (matcher.find()) {
				long timeUnit = Long.parseLong(matcher.group(1));
				switch (matcher.group(2)) {
					case "s": time += timeUnit; break;
					case "m": time += timeUnit * 60L; break;
					case "h": time += timeUnit * 3600L; break;
					case "d": time += timeUnit * 86400L; break;
					case "w": time += timeUnit * 604800L; break;
				}
			}
			return time;
		}
		return -1L;
	}
	
	public static int getWidth() { return SpecEditor.get.width; }
	public static int getHeight() { return SpecEditor.get.height; }
	
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
	
	public static void logInfo(String tag, Object... objects) { System.err.println(format("[INFO] ", tag, ": ", format(objects))); }
	public static void logWarn(String tag, Object... objects) { System.err.println(format("[WARN] ", tag, ": ", format(objects))); }
	public static void logError(Throwable throwable, String tag, Object... objects) {
		System.err.println(format("[ERROR] ", tag, ": ", format(objects)));
		if (SpecEditor.DEBUG && throwable != null) throwable.printStackTrace();
	}
	public static void logDebug(Object... objects) { if (SpecEditor.DEBUG) System.err.println(format("[DEBUG] ", format(objects))); }	
}
