package by.fxg.pilesos.i18n;

import java.util.regex.Pattern;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.i18n.I18n.I18nPool;

public class I18nPoolLoader extends AsynchronousAssetLoader<I18nPool, I18nPoolLoader.MapConfigParameters> {
	public I18nPoolLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@SuppressWarnings("rawtypes")
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, MapConfigParameters parameter) {
		Array<AssetDescriptor> deps = new Array<>();
		return deps;
	}
	
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, MapConfigParameters parameter) {}
	public I18nPool loadSync(AssetManager manager, String fileName, FileHandle file, MapConfigParameters parameter) {
		I18nPool pool = new I18nPool(file.name().split(Pattern.quote("."))[0]);
		String[] lines = file.readString("UTF-8").split(System.lineSeparator());
		for (String line : lines) {
			if (line == null || line.length() == 0 || line.startsWith("#")) continue;
			
			String[] items = line.split(Pattern.quote(":="));
			pool.add(items[0], items[1]);
		}
		I18n.addPool(pool);
		return pool;
	}
	
	static class MapConfigParameters extends AssetLoaderParameters<I18nPool> {}
}
