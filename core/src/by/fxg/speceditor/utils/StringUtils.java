package by.fxg.speceditor.utils;

import com.badlogic.gdx.files.FileHandle;

public class StringUtils {
	public static String extensionOf(String path) {
		int dotIndex = path.lastIndexOf('.');
		if (dotIndex == -1) return "";
		return path.substring(dotIndex + 1);
	}
	
	/** example: <br>
	 * 	Target Handle: coreFolder/ <br>
	 *  Absolute Path: C:/projects/coreFolder/images/file9.png <br>
	 *  Returns: coreFolder/images/file9.png as Target Handle's child (or null if not possible) **/
	@Deprecated //FIXME implement Utils#intersectCombinePaths(FileHandle, String)
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
}
