package by.fxg.speceditor;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting;
import com.badlogic.gdx.graphics.g3d.Model;

import by.fxg.pilesos.PilesosInputImpl;
import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.i18n.I18n.I18nPool;
import by.fxg.pilesos.i18n.I18n;
import by.fxg.pilesos.i18n.I18nPoolLoader;

public class ResourceManager {
	public static ResourceManager INSTANCE;
	private final Map<String, Class<?>> assetMarkers = new HashMap<>();
	
	public static BitmapFont smallFont, mediumFont, bigFont;
	public AssetManager assetManager;
	
	public ResourceManager() {
		INSTANCE = this;
		this.assetManager = new AssetManager();
		this.assetManager.setLoader(I18nPool.class, ".langpack", new I18nPoolLoader(new InternalFileHandleResolver()));
		this.assetMarkers.put("langpack", I18nPool.class);
		this.assetMarkers.put("obj", Model.class);
		this.assetMarkers.put("png", Texture.class);
		
		I18n.setLanguage(System.getProperty("user.language"));
		SpriteStack.DEFAULT_PATH = Gdx.files.local("assets/");
		Foster.defaultFont = smallFont = this.generateFont(Gdx.files.local("assets/font/monogram.ttf"), 16);
		mediumFont = this.generateFont(Gdx.files.local("assets/font/monogram.ttf"), 24);
		bigFont = this.generateFont(Gdx.files.local("assets/font/monogram.ttf"), 32);
		this.loadAssetsFrom("assets/");
	}
	
	public static <T> T get(String object, Class<T> clazz) {
		return INSTANCE.assetManager.get("assets/" + object, clazz);
	}
	
	public static <T> T get(AssetDescriptor<T> descriptor) {
		return INSTANCE.assetManager.get(descriptor);
	}

	public boolean loadAsset(AssetDescriptor<?> descriptor) {
		if (!this.assetManager.isLoaded(descriptor)) {
			this.assetManager.load(descriptor);
			this.assetManager.finishLoadingAsset(descriptor);
			return this.assetManager.isLoaded(descriptor);
		}
		return false;
	}
	
	public boolean unloadAsset(AssetDescriptor<?> descriptor) {
		if (this.assetManager.isLoaded(descriptor)) {
			this.assetManager.unload(descriptor.fileName);
			return !this.assetManager.isLoaded(descriptor);
		}
		return false;
	}
	
	private void loadAssetsFrom(String path) {
		for (FileHandle fh : Gdx.files.local(path).list()) {
			if (!fh.isDirectory()) {
				for (String str : this.assetMarkers.keySet()) {
					if (fh.extension().equalsIgnoreCase(str)) {
						this.assetManager.load(fh.path(), this.assetMarkers.get(str));	
					}
				}
			} else {
				this.loadAssetsFrom(path + fh.name() + "/");
			}
		}
	}
	
	private BitmapFont generateFont(FileHandle fontFile, int size) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.packer = new PixmapPacker(4096, 4096, Format.RGBA8888, 2, false);
		parameter.hinting = Hinting.AutoSlight;
		parameter.flip = false;
		parameter.size = size;
		parameter.characters = PilesosInputImpl.ALLOWED_CHARACTERS;
		parameter.incremental = true;
		return generator.generateFont(parameter);
	}
}
