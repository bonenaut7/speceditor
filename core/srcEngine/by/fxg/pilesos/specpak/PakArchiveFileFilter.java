package by.fxg.pilesos.specpak;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PakArchiveFileFilter extends FileFilter {
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().toLowerCase().endsWith(".pak");
	}

	public String getDescription() {
		return "PAK Archive (pak)";
	}
}
