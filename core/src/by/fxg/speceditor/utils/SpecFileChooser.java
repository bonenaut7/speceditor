package by.fxg.speceditor.utils;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import by.fxg.pilesos.i18n.I18n;
import by.fxg.speceditor.project.ProjectManager;

/** Builder-like frame to use {@link javax.swing.JFileChooser} **/
public class SpecFileChooser {
	private static final SpecFileChooser instance = new SpecFileChooser();
	private static JFrame frame;
	private static JFileChooser fileChooser;
	private static File userDirectory;

	private SpecFileChooser() {
		frame = new JFrame();
		fileChooser = new JFileChooser();
		userDirectory = new File(System.getProperty("user.home"));
		frame.setAlwaysOnTop(true);
	}
	
	public static SpecFileChooser get() {
		fileChooser.setSelectedFile(null);
		fileChooser.setSelectedFiles(null);
		fileChooser.setFileFilter(null);
		fileChooser.setCurrentDirectory(userDirectory);
		return instance;
	}
	
	public static SpecFileChooser getInProjectDirectory() {
		fileChooser.setSelectedFile(null);
		fileChooser.setSelectedFiles(null);
		fileChooser.setFileFilter(null);
		fileChooser.setCurrentDirectory(ProjectManager.currentProject == null ? userDirectory : ProjectManager.currentProject.getProjectFolder().file());
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
	
	public FileHandle folder() {
		return this.single(JFileChooser.DIRECTORIES_ONLY, I18n.get("speceditor.utils.SpecFileChooser.folder"));
	}
	
	public FileHandle[] folders() {
		return this.multiple(JFileChooser.DIRECTORIES_ONLY, I18n.get("speceditor.utils.SpecFileChooser.folder"));
	}
	
	public FileHandle file() {
		return this.single(JFileChooser.FILES_ONLY, I18n.get("speceditor.utils.SpecFileChooser.file"));
	}
	
	public FileHandle[] files() {
		return this.multiple(JFileChooser.FILES_ONLY, I18n.get("speceditor.utils.SpecFileChooser.file"));
	}
	
	public FileHandle fileAndFolder() {
		return this.single(JFileChooser.FILES_AND_DIRECTORIES, I18n.get("speceditor.utils.SpecFileChooser.file"));
	}
	
	public FileHandle[] filesAndFolders() {
		return this.multiple(JFileChooser.FILES_AND_DIRECTORIES, I18n.get("speceditor.utils.SpecFileChooser.file"));
	}
	
	private FileHandle single(int mode, String title) {
		fileChooser.setFileSelectionMode(mode);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogTitle(title);
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
			return Gdx.files.absolute(fileChooser.getSelectedFile().getAbsolutePath());
		}
		return null;
	}
	
	private FileHandle[] multiple(int mode, String title) {
		fileChooser.setFileSelectionMode(mode);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setDialogTitle(title);
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFiles() != null) {
			FileHandle[] array = new FileHandle[fileChooser.getSelectedFiles().length];
			for (int i = 0; i != array.length; i++) {
				array[i] = Gdx.files.absolute(fileChooser.getSelectedFiles()[i].getAbsolutePath());
			}
			return array;
		}
		return null;
	}
}
