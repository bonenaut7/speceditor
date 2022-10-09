package by.fxg.speceditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.bullet.Bullet;

import by.fxg.pilesos.Apparat;
import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.addon.AddonManager;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.render.RenderManager;
import by.fxg.speceditor.screen.ScreenTestUI;
import by.fxg.speceditor.std.STDManager;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.utils.Utils;

public class Game extends Apparat<GInputProcessor> {
	public static boolean DEBUG = false;
	public static Game get;
	public static Storage storage;
	public static Foster fosterNoDraw;
	public BitmapFont appFont, mid, bigFont;
	public ResourceManager resourceManager;
	public RenderManager renderer;
	
	public void create() {
		this.onCreate(get = this);
		if (this.hasProgramArgument("-debug")) DEBUG = true;
		
		Gdx.input.setInputProcessor(super.input = new GInputProcessor());
		Bullet.init();
		SpecInterface.init();
		SpriteStack.DEFAULT_PATH = Gdx.files.internal("assets/");
		this.resourceManager = new ResourceManager();
		Foster.defaultFont = this.appFont = this.resourceManager.generateFont(Gdx.files.internal("assets/font/monogram.ttf"), 16);
		this.mid = this.resourceManager.generateFont(Gdx.files.internal("assets/font/monogram.ttf"), 24);
		this.bigFont = this.resourceManager.generateFont(Gdx.files.internal("assets/font/monogram.ttf"), 32);
		storage = new Storage(this.resourceManager);
		fosterNoDraw = new Foster();
		
		new ProjectManager();
		new STDManager();
		new AddonManager();
		ProjectManager.INSTANCE.postInit();
		STDManager.INSTANCE.postInit();
		AddonManager.INSTANCE.postInit();
		
		
		this.renderer = new RenderManager(this);
		this.input.setCursorCatched(false);
		
		if (this.hasProgramArgument("-UITest")) this.renderer.currentScreen = new ScreenTestUI();
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