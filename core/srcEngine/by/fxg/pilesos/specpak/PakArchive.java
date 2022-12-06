package by.fxg.pilesos.specpak;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class PakArchive {
	private final String name;
	private final ZipFile zipFile;
	private Array<String> entries = new Array<String>();
	
	protected PakArchive(String name, ZipFile zipFile) {
		this.name = name;
		this.zipFile = zipFile;
		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		while(enumeration.hasMoreElements()) {
			ZipEntry entry = enumeration.nextElement();
			if (!entry.isDirectory()) {
				this.entries.add(entry.getName());
			}
		}
	}

	public boolean containsEntry(String entryName) {
		if (entryName == null) throw new GdxRuntimeException("entryName can't be null!");
		return this.zipFile.getEntry(entryName.replace('\\', '/')) != null;
	}
	
	public String getName() {
		return this.name;
	}
	
	public ZipFile getZipFile() {
		return this.zipFile;
	}
	
	public Array<String> getEntries() { 
		return this.entries;
	}
}
