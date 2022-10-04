package by.fxg.pilesos.graphics;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteStack {
	private static Map<String, Texture> textureList = new HashMap<>();
	private static Map<String, TextureRegion> textureRegionList = new HashMap<>();
	
	public static FileHandle DEFAULT_PATH = Gdx.files.internal("");
	public static TextureFilter DEFAULT_MIN_FILTER = TextureFilter.Linear; //MipMapLinearNearest
	public static TextureFilter DEFAULT_MAG_FILTER = TextureFilter.Linear;
	public static boolean DEFAULT_REGION_NEWOBJECT = false;
	public static boolean DEFAULT_APPLY_FILTERS = false;
	
	public static Texture getTexture(String path) { return getTexture(DEFAULT_PATH.child(path)); }
	public static Texture getTexture(FileHandle handle) {
		if (!textureList.containsKey(handle.path())) {
			textureList.put(handle.path(), new Texture(handle));
			if (DEFAULT_APPLY_FILTERS) textureList.get(handle.path()).setFilter(DEFAULT_MIN_FILTER, DEFAULT_MAG_FILTER);
		}
		return textureList.get(handle.path());
	}
	
	public static TextureRegion getTextureRegion(String path) { return getTextureRegion(DEFAULT_PATH.child(path), DEFAULT_REGION_NEWOBJECT); }
	public static TextureRegion getTextureRegion(String path, boolean newObject) { return getTextureRegion(DEFAULT_PATH.child(path), newObject); }
	public static TextureRegion getTextureRegion(FileHandle handle) { return getTextureRegion(handle, DEFAULT_REGION_NEWOBJECT); }
	public static TextureRegion getTextureRegion(FileHandle handle, boolean newObject) {
		if (newObject) return new TextureRegion(getTexture(handle));
		if (!textureRegionList.containsKey(handle.path())) {
			textureRegionList.put(handle.path(), new TextureRegion(getTexture(handle)));
		}
		return textureRegionList.get(handle.path());
	}
	
	public static void remove(String path) { remove(DEFAULT_PATH.child(path)); }
	public static void remove(FileHandle handle) { 
		if (textureList.containsKey(handle.path())) textureList.remove(handle.path());
		if (textureRegionList.containsKey(handle.path())) textureRegionList.remove(handle.path());
	}
	
	
	public static void dispose() {
		for (Texture t : textureList.values()) {
			t.dispose();
		}
		for (TextureRegion tr : textureRegionList.values()) {
			tr.getTexture().dispose();
		}
	}
}
