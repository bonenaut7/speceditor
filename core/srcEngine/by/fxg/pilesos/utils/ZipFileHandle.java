package by.fxg.pilesos.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ZipFileHandle extends FileHandle {
	protected final ZipFile archive;
	protected final ZipEntry archiveEntry;

	public ZipFileHandle(ZipFile archive, File file) {
		super(file, FileType.Classpath);
		this.archive = archive;
		this.archiveEntry = this.archive.getEntry(file.getPath());
	}

	public ZipFileHandle(ZipFile archive, String fileName) {
		super(fileName.replace('\\', '/'), FileType.Classpath);
		this.archive = archive;
		this.archiveEntry = archive.getEntry(fileName.replace('\\', '/'));
	}

	public FileHandle child(String name) {
		name = name.replace('\\', '/');
		if (this.file.getPath().length() == 0) return new ZipFileHandle(this.archive, new File(name));
		return new ZipFileHandle(this.archive, new File(this.file, name));
	}
	
	public FileHandle sibling(String name) {
		name = name.replace('\\', '/');
		if (this.file.getPath().length() == 0) throw new GdxRuntimeException("Can't get sibling of root.");
		return new ZipFileHandle(this.archive, new File(this.file.getParent(), name));
	}

	public FileHandle parent() {
		File parent = file.getParentFile();
		if (parent == null) {
			if (this.type == FileType.Absolute) parent = new File("/");
			else parent = new File("");
		}
		return new ZipFileHandle(this.archive, parent);
	}

	public InputStream read() {
		try {
			return this.archive.getInputStream(this.archiveEntry);
		} catch (IOException e) {
			throw new GdxRuntimeException("File not found: " + this.file + " (Zip Archive)");
		}
	}

	public boolean exists() {
		return this.archiveEntry != null;
	}

	public long length() {
		return this.archiveEntry.getSize();
	}

	public long lastModified() {
		return this.archiveEntry.getTime();
	}
	
	public ZipFile getZipFile() {
		return this.archive;
	}
}
