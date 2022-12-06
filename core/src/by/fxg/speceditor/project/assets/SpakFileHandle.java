package by.fxg.speceditor.project.assets;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.zeroturnaround.zip.ZipUtil;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class SpakFileHandle extends FileHandle {
	protected final File archive;

	public SpakFileHandle(File archive, File file) {
		super(file, FileType.Classpath);
		this.archive = archive;
	}

	public SpakFileHandle(File archive, String fileName) {
		super(fileName.replace('\\', '/'), FileType.Classpath);
		this.archive = archive;
	}

	public FileHandle child(String name) {
		name = name.replace('\\', '/');
		if (this.file.getPath().length() == 0) return new SpakFileHandle(this.archive, new File(name));
		return new SpakFileHandle(this.archive, new File(this.file, name));
	}
	
	public FileHandle sibling(String name) {
		name = name.replace('\\', '/');
		if (this.file.getPath().length() == 0) throw new GdxRuntimeException("Can't get sibling of root.");
		return new SpakFileHandle(this.archive, new File(this.file.getParent(), name));
	}

	public FileHandle parent() {
		File parent = file.getParentFile();
		if (parent == null) {
			if (this.type == FileType.Absolute) parent = new File("/");
			else parent = new File("");
		}
		return new SpakFileHandle(this.archive, parent);
	}

	public InputStream read() {
		return new ByteArrayInputStream(ZipUtil.unpackEntry(this.archive, this.path()));
	}

	public boolean exists() {
		return ZipUtil.containsEntry(this.archive, this.path());
	}
}
