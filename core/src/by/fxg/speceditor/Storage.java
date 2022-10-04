package by.fxg.speceditor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ini4j.Wini;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;

import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.speceditor.ui.SpecInterface.AppCursor;

public class Storage {
	private final File settingsFile;
	public static IAccess access;
	public static Wini settings;
	
	public Map<String, Sprite> sprites = new HashMap<>();
	private Map<AppCursor, Cursor> cursors = new HashMap<>();
	private Cursor defaultCursor;
	
	public Storage(GameManager manager) {
		this.sprites.put("obj.folder.true.false", new Sprite(manager.get("icons/obj.folder.true.false.png", Texture.class)));
		this.sprites.put("obj.folder.true.true", new Sprite(manager.get("icons/obj.folder.true.true.png", Texture.class)));
		this.sprites.put("obj.folder.false.false", new Sprite(manager.get("icons/obj.folder.false.false.png", Texture.class)));
		this.sprites.put("obj.folder.false.true", new Sprite(manager.get("icons/obj.folder.false.true.png", Texture.class)));
		
		this.sprites.put("obj.light.false", new Sprite(manager.get("icons/obj.light.false.png", Texture.class)));
		this.sprites.put("obj.light.true", new Sprite(manager.get("icons/obj.light.true.png", Texture.class)));
		this.sprites.put("obj.decal.false", new Sprite(manager.get("icons/obj.decal.false.png", Texture.class)));
		this.sprites.put("obj.decal.true", new Sprite(manager.get("icons/obj.decal.true.png", Texture.class)));
		this.sprites.put("obj.model.false", new Sprite(manager.get("icons/obj.model.false.png", Texture.class)));
		this.sprites.put("obj.model.true", new Sprite(manager.get("icons/obj.model.true.png", Texture.class)));
		
		this.sprites.put("obj.hitbox.false", new Sprite(manager.get("icons/obj.hitbox.false.png", Texture.class)));
		this.sprites.put("obj.hitbox.true", new Sprite(manager.get("icons/obj.hitbox.true.png", Texture.class)));
		this.sprites.put("obj.meshhitbox.false", new Sprite(manager.get("icons/obj.meshhitbox.false.png", Texture.class)));
		this.sprites.put("obj.meshhitbox.true", new Sprite(manager.get("icons/obj.meshhitbox.true.png", Texture.class)));
		this.sprites.put("obj.hitboxstorage.false", new Sprite(manager.get("icons/obj.hitboxstorage.false.png", Texture.class)));
		this.sprites.put("obj.hitboxstorage.true", new Sprite(manager.get("icons/obj.hitboxstorage.true.png", Texture.class)));
		
		this.sprites.put("obj.point.false", new Sprite(manager.get("icons/obj.point.false.png", Texture.class)));
		this.sprites.put("obj.point.true", new Sprite(manager.get("icons/obj.point.true.png", Texture.class)));
		this.sprites.put("obj.pointarray.false", new Sprite(manager.get("icons/obj.pointarray.false.png", Texture.class)));
		this.sprites.put("obj.pointarray.true", new Sprite(manager.get("icons/obj.pointarray.true.png", Texture.class)));
		
		SpriteStack.getTexture("defaults/sceneLight_false_false.png").setFilter(TextureFilter.Linear, TextureFilter.Linear);
		SpriteStack.getTexture("defaults/sceneLight_false_true.png").setFilter(TextureFilter.Linear, TextureFilter.Linear);
		SpriteStack.getTexture("defaults/sceneLight_true_false.png").setFilter(TextureFilter.Linear, TextureFilter.Linear);
		SpriteStack.getTexture("defaults/sceneLight_true_true.png").setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
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
			if (Game.DEBUG) e.printStackTrace();
		}
		return null;
	}
	
	public Cursor getCursor(AppCursor cursor) {
		if (cursor != null && this.cursors.containsKey(cursor)) return this.cursors.get(cursor);
		return this.defaultCursor;
	}
	
	public static interface IAccess {
		abstract void open(String str);
	}
}
