package by.fxg.speceditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.bullet.Bullet;

import by.fxg.pilesos.Apparat;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.addon.AddonManager;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.render.RenderManager;
import by.fxg.speceditor.std.STDManager;
import by.fxg.speceditor.std.ui.SpecInterface;

public class SpecEditor extends Apparat<GInputProcessor> {
	public static boolean DEBUG = false;
	public static SpecEditor get;
	public static Foster fosterNoDraw;
	public ResourceManager resourceManager;
	public RenderManager renderer;
	
	public void create() {
		this.onCreate(get = this);
		if (this.hasProgramArgument("-debug")) DEBUG = true;
		Gdx.input.setInputProcessor(super.input = new GInputProcessor());
		this.resourceManager = new ResourceManager();
		this.renderer = new RenderManager(this);
		this.input.setCursorCatched(false);
	}
	
	public void init() {
		fosterNoDraw = new Foster();
		//init
		Bullet.init();
		SpecInterface.init();
		DefaultResources.INSTANCE = new DefaultResources();
		ProjectManager.INSTANCE = new ProjectManager();
		STDManager.INSTANCE = new STDManager();
		AddonManager.INSTANCE = new AddonManager();
		//post
		ProjectManager.INSTANCE.postInit();
		STDManager.INSTANCE.postInit();
		AddonManager.INSTANCE.postInit();
	}
	
	public void update(int width, int height) {
		this.renderer.update(this, width, height);
		if (SpecInterface.INSTANCE != null) SpecInterface.INSTANCE.onUpdate();
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