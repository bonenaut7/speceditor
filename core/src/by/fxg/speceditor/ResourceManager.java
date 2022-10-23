package by.fxg.speceditor;

import java.util.HashMap;
import java.util.Map;

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
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Disposable;

import by.fxg.pilesos.PilesosInputImpl;
import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.pilesos.i18n.I18n.I18nPool;
import by.fxg.pilesos.i18n.I18nPoolLoader;
import by.fxg.pilesos.utils.JarUtils;

public class ResourceManager {
	public static ResourceManager INSTANCE;
	private final Map<String, Class<?>> assetMarkers = new HashMap<>();
	
	public static Texture standardTexture = null;
	public static Texture standardDecal = null;
	public static Model standardModel = null;
	
	public AssetManager assetManager;
	
	public ResourceManager() {
		INSTANCE = this;
		this.assetManager = new AssetManager();
		this.assetManager.setLoader(I18nPool.class, ".langpack", new I18nPoolLoader(new InternalFileHandleResolver()));
		this.assetMarkers.put("langpack", I18nPool.class);
		this.assetMarkers.put("obj", Model.class);
		this.assetMarkers.put("png", Texture.class);
		
		this.loadAssetsFrom("assets/");
		this.assetManager.finishLoading();
		
		standardTexture = SpriteStack.getTexture("defaults/defaultdiffuse.png");
		standardDecal = SpriteStack.getTexture("defaults/defaultdecal.png");
		standardModel = this.assetManager.get("assets/defaults/defaultmodel.obj");
		standardModel.materials.get(0).set(ColorAttribute.createDiffuse(1, 0, 0, 1));
	}
	
	private void loadAssetsFrom(String path) {
		for (FileHandle fh : JarUtils.listFromJarIfNecessary(path, true)) {
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
	
	public <T> T get(String object, Class<T> clazz) {
		return this.assetManager.get("assets/" + object, clazz);
	}
	
	public <T> T get(AssetDescriptor<T> descriptor) {
		return this.assetManager.get(descriptor);
	}
	
	public BitmapFont generateFont(FileHandle fontFile, int size) {
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
