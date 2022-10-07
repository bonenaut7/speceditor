package by.fxg.speceditor;

import org.jrenner.smartfont.SmartFontGenerator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.bullet.Bullet;

import by.fxg.pilesos.Apparat;
import by.fxg.pilesos.PilesosInputImpl;
import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.addon.AddonManager;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.render.RenderManager;
import by.fxg.speceditor.screen.ScreenTestUI;
import by.fxg.speceditor.std.STDManager;
import by.fxg.speceditor.ui.SpecInterface;

public class Game extends Apparat<GInputProcessor> {
	public static boolean DEBUG = false;
	public static Game get;
	public static Storage storage;
	public BitmapFont appFont, bigFont;
	public ResourceManager resourceManager;
	public RenderManager renderer;
	
	public void create() {
		this.onCreate(get = this);
		
		Gdx.input.setInputProcessor(super.input = new GInputProcessor());
		this.input.setCursorCatched(false);
		Bullet.init();
		SpecInterface.init();
		SpriteStack.DEFAULT_PATH = Gdx.files.internal("assets/");
		
		SmartFontGenerator fontGenerator = new SmartFontGenerator();
		Foster.defaultFont = this.appFont = fontGenerator.createFont(PilesosInputImpl.ALLOWED_CHARACTERS, Gdx.files.internal("assets/font/monogram.ttf"), "basefont-small", 16);
		this.bigFont = fontGenerator.createFont(PilesosInputImpl.ALLOWED_CHARACTERS, Gdx.files.internal("assets/font/monogram.ttf"), "basefont-small", 32);
		this.resourceManager = new ResourceManager().loadSounds();
		storage = new Storage(this.resourceManager);
		
		new ProjectManager();
		new STDManager();
		new AddonManager();
		STDManager.INSTANCE.postInit();
		
		this.renderer = new RenderManager(this);
		if (this.hasProgramArgument("-UITest")) this.renderer.currentScreen = new ScreenTestUI();
		if (this.hasProgramArgument("-debug")) DEBUG = true;
	}
	
	public void update(int width, int height) {
		this.renderer.update(this, width, height);
		SpecInterface.get.onUpdate();
	}
	
	public void render(int width, int height) {
		Gdx.gl.glClearColor(0.12f, 0.12f, 0.12f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
		this.renderer.render(this, width, height);
	}
	
	public void dispose() {
		super.dispose();
		this.resourceManager.assetManager.dispose();
	}
	
	public void resize(int width, int height) {
		super.resize(width, height);
		this.renderer.resize(width, height);
		RenderManager.batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}
}