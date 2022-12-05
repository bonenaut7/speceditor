package by.fxg.speceditor.utils;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import by.fxg.pilesos.i18n.I18n;
import by.fxg.speceditor.project.ProjectManager;

/** Builder-like frame to use {@link javax.swing.JFileChooser} **/
public class SpecFileChooser {
	public static final int
		TITLE_SAVE = 0,
		TITLE_OPEN = 1,
		TITLE_FILE = 2,
		TITLE_FOLDER = 3;
	
	private static final SpecFileChooser instance = new SpecFileChooser();
	private static JFrame frame;
	private static JFileChooser fileChooser;
	private String title = null;
	
	private SpecFileChooser() {
		frame = new JFrame();
		fileChooser = new JFileChooser();
		frame.setAlwaysOnTop(true);
	}
	
	public static SpecFileChooser get() {
		fileChooser.setSelectedFile(null);
		fileChooser.setSelectedFiles(null);
		fileChooser.resetChoosableFileFilters();
		instance.title = null;
		return instance;
	}
	
	public static SpecFileChooser getInProjectDirectory() {
		fileChooser.setSelectedFile(null);
		fileChooser.setSelectedFiles(null);
		fileChooser.resetChoosableFileFilters();
		fileChooser.setCurrentDirectory(ProjectManager.currentProject == null ? null : ProjectManager.currentProject.getProjectFolder().file());
		instance.title = null;
		return instance;
	}
	
	public SpecFileChooser setFilter(FileFilter filter) {
		fileChooser.setFileFilter(filter);
		return this;
	}
	
	public SpecFileChooser setDirectory(FileHandle handle) {
		fileChooser.setCurrentDirectory(handle.file());
		return this;
	}
	
	public SpecFileChooser setTitle(String title) {
		fileChooser.setDialogTitle(this.title = title);
		return this;
	}
	
	public FileHandle openSingle(boolean allowFiles, boolean allowFolders) {
		if (this.title == null) fileChooser.setDialogTitle(Utils.format(this.getString(TITLE_OPEN), " ", this.getString(!allowFiles && allowFolders ? TITLE_FOLDER : TITLE_FILE)));
		fileChooser.setFileSelectionMode(allowFiles && allowFolders ? JFileChooser.FILES_AND_DIRECTORIES : allowFolders ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
			return Gdx.files.absolute(fileChooser.getSelectedFile().getAbsolutePath());
		}
		return null;
	}
	
	public FileHandle[] openMultiple(boolean allowFiles, boolean allowFolders) {
		if (this.title == null) fileChooser.setDialogTitle(Utils.format(this.getString(TITLE_OPEN), " ", this.getString(!allowFiles && allowFolders ? TITLE_FOLDER : TITLE_FILE)));
		fileChooser.setFileSelectionMode(allowFiles && allowFolders ? JFileChooser.FILES_AND_DIRECTORIES : allowFolders ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFiles() != null) {
			FileHandle[] array = new FileHandle[fileChooser.getSelectedFiles().length];
			for (int i = 0; i != array.length; i++) {
				array[i] = Gdx.files.absolute(fileChooser.getSelectedFiles()[i].getAbsolutePath());
			}
			return array;
		}
		return null;
	}
	
	public FileHandle saveSingle(boolean allowFiles, boolean allowFolders) {
		if (this.title == null) fileChooser.setDialogTitle(Utils.format(this.getString(TITLE_SAVE), " ", this.getString(!allowFiles && allowFolders ? TITLE_FOLDER : TITLE_FILE)));
		fileChooser.setFileSelectionMode(allowFiles && allowFolders ? JFileChooser.FILES_AND_DIRECTORIES : allowFolders ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
			return Gdx.files.absolute(fileChooser.getSelectedFile().getAbsolutePath());
		}
		return null;
	}
	
	private String getString(int code) {
		switch (code) {
			case TITLE_SAVE: return I18n.get("speceditor.utils.SpecFileChooser.save");
			case TITLE_OPEN: return I18n.get("speceditor.utils.SpecFileChooser.open");
			case TITLE_FILE: return I18n.get("speceditor.utils.SpecFileChooser.file");
			case TITLE_FOLDER: return I18n.get("speceditor.utils.SpecFileChooser.folder");
		}
		return "-";
	}
}
