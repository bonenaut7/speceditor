package by.fxg.speceditor.scenes;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ScenesFormatFileFilter extends FileFilter {
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().toLowerCase().endsWith(".ssf");
	}

	public String getDescription() {
		return "Scenes format file (ssf)";
	}
}
