package by.fxg.speceditor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ini4j.Wini;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.speceditor.std.ui.SpecInterface.AppCursor;

public class DefaultResources {
	public static DefaultResources INSTANCE;
	private final File settingsFile;
	public static Wini settings;
	public static FileHandle appFolder, addonsFolder;
	
	public Texture standardTexture = null;
	public Decal standardDecal = null;
	public Model standardModel = null;
	public Map<String, Sprite> sprites = new HashMap<>();
	private Map<AppCursor, Cursor> cursors = new HashMap<>();
	private Cursor defaultCursor;
	
	public DefaultResources() {
		appFolder = Gdx.files.local("spec/");
		addonsFolder = appFolder.child("addons/");
		appFolder.mkdirs();
		addonsFolder.mkdirs();
		
		this.standardTexture = SpriteStack.getTexture("defaults/defaultdiffuse.png");
		this.standardDecal = Decal.newDecal(SpriteStack.getTextureRegion("defaults/defaultdecal.png"), true);
		this.standardModel = ResourceManager.get("defaults/defaultmodel.obj", Model.class);
		this.standardModel.materials.get(0).set(ColorAttribute.createDiffuse(1, 0, 0, 1));
		
		this.sprites.put("icons/question", new Sprite(ResourceManager.get("defaults/icons/question.png", Texture.class)));
		this.sprites.put("icons/folder.false", new Sprite(ResourceManager.get("defaults/icons/folder.closed.png", Texture.class)));
		this.sprites.put("icons/folder.true", new Sprite(ResourceManager.get("defaults/icons/folder.opened.png", Texture.class)));
		
		this.sprites.put("icons/package", new Sprite(ResourceManager.get("defaults/icons/package.png", Texture.class)));
		this.sprites.put("icons/model", new Sprite(ResourceManager.get("defaults/icons/model.png", Texture.class)));
		this.sprites.put("icons/light", new Sprite(ResourceManager.get("defaults/icons/light.png", Texture.class)));
		this.sprites.put("icons/hitbox", new Sprite(ResourceManager.get("defaults/icons/hitbox.png", Texture.class)));
		this.sprites.put("icons/decal", new Sprite(ResourceManager.get("defaults/icons/decal.png", Texture.class)));
		
		SpriteStack.getTextureRegion("defaults/lightdecal_false.png").getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		SpriteStack.getTextureRegion("defaults/lightdecal_true.png").getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		this.defaultCursor = this.createCursor(AppCursor.ARROW, "assets/defaults/cursor/arrow.png", 4, 4);
		this.createCursor(AppCursor.CROSS, "assets/defaults/cursor/cross.png", 16, 16);
		this.createCursor(AppCursor.IBEAM, "assets/defaults/cursor/ibeam.png", 16, 16);
		this.createCursor(AppCursor.GRAB, "assets/defaults/cursor/grab.png", 16, 16);
		this.createCursor(AppCursor.GRABBING, "assets/defaults/cursor/grabbing.png", 16, 16);
		this.createCursor(AppCursor.POINT, "assets/defaults/cursor/point.png", 16, 4);
		this.createCursor(AppCursor.POINTING, "assets/defaults/cursor/pointing.png", 16, 4);
		this.createCursor(AppCursor.UNAVAILABLE, "assets/defaults/cursor/unavailable.png", 16, 16);
		this.createCursor(AppCursor.DEAD, "assets/defaults/cursor/dead.png", 16, 16);
		this.createCursor(AppCursor.HELP, "assets/defaults/cursor/help.png", 16, 16);
		this.createCursor(AppCursor.COLORPICKER, "assets/defaults/cursor/colorpicker.png", 4, 24);
		this.createCursor(AppCursor.PEN, "assets/defaults/cursor/pen.png", 4, 24);
		this.createCursor(AppCursor.ZOOM_IN, "assets/defaults/cursor/zoomin.png", 16, 16);
		this.createCursor(AppCursor.ZOOM_OUT, "assets/defaults/cursor/zoomout.png", 16, 16);
		this.createCursor(AppCursor.RESIZE, "assets/defaults/cursor/resize.png", 16, 16);
		this.createCursor(AppCursor.RESIZE_VERTICAL, "assets/defaults/cursor/resizeVertical.png", 16, 16);
		this.createCursor(AppCursor.RESIZE_HORIZONTAL, "assets/defaults/cursor/resizeHorizontal.png", 16, 16);
		this.createCursor(AppCursor.RESIZE_NESW, "assets/defaults/cursor/resizeNESW.png", 16, 16);
		this.createCursor(AppCursor.RESIZE_NWSE, "assets/defaults/cursor/resizeNWSE.png", 16, 16);
		
		this.settingsFile = Gdx.files.local("spec/settings.ini").file();
		
		try {
			settings = new Wini(this.settingsFile);
		} catch (Exception e) {
			settings = new Wini();
			try {
				settings.store(this.settingsFile);
			} catch (IOException io) {}
		}
	}
	
	public Cursor getCursor(AppCursor cursor) {
		if (cursor != null && this.cursors.containsKey(cursor)) return this.cursors.get(cursor);
		return this.defaultCursor;
	}
	
	private Cursor createCursor(AppCursor type, String internalPath, int xOffset, int yOffset) {
		try {
			Texture texture = new Texture(Gdx.files.internal(internalPath));
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			texture.getTextureData().prepare();
			Pixmap pixmap = texture.getTextureData().consumePixmap();
			this.cursors.put(type, Gdx.graphics.newCursor(pixmap, xOffset, yOffset));
			pixmap.dispose();
			texture.dispose();
			return this.cursors.get(type);
		} catch (Exception e) {
			Gdx.app.getApplicationLogger().error("SpecEditor Storage", String.format("Unable to load cursor for type %s.", type.name()));
			if (SpecEditor.DEBUG) e.printStackTrace();
		}
		return null;
	}
}
