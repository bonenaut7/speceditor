package by.fxg.speceditor;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.pilesos.i18n.I18n.I18nPool;
import by.fxg.pilesos.i18n.I18nPoolLoader;
import by.fxg.pilesos.utils.JarUtils;

public class ResourceManager {
	public static Texture standartDiffuse = null;
	public static Texture standartDecal = null;
	
	public static Model standartModel = null;
	
	private final Map<String, Class<?>> assetMarkers = new HashMap<>();
	public AssetManager assetManager;
	public boolean isLoaded = false;
	
	public ResourceManager() {
		this.assetManager = new AssetManager();
		this.assetManager.setLoader(I18nPool.class, ".langpack", new I18nPoolLoader(new InternalFileHandleResolver()));
		this.assetMarkers.put("langpack", I18nPool.class);
		this.assetMarkers.put("obj", Model.class);
		this.assetMarkers.put("png", Texture.class);
		
		this.loadAssetsFrom("assets/");
		this.assetManager.finishLoading();
		this.isLoaded = true;
		
		standartDiffuse = SpriteStack.getTexture("defaults/defaultdiffuse.png");
		standartDecal = SpriteStack.getTexture("defaults/defaultdecal.png");
		standartModel = this.assetManager.get("assets/defaults/defaultmodel.obj");
		standartModel.materials.get(0).set(ColorAttribute.createDiffuse(1, 0, 0, 1));
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
	
	public ResourceManager loadSounds() {
		return this;
	}
	
	public Model getModel(String model, boolean animated) {
		return this.assetManager.get("assets/" + model + (animated ? ".g3db" : ".obj"), Model.class);
	}
	
	public <T> T get(String object, Class<T> clazz) {
		return this.assetManager.get("assets/" + object, clazz);
	}
}
