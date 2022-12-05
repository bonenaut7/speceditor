package by.fxg.pilesos.utils;

import java.util.zip.ZipFile;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class ZipFileHandleResolver implements FileHandleResolver {
	private final ZipFile archive;

	public ZipFileHandleResolver(ZipFile archive) {
		this.archive = archive;
	}

	public FileHandle resolve(String fileName) {
		return new ZipFileHandle(this.archive, fileName);
	}
}
