package by.fxg.speceditor;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.bullet.Bullet;

import by.fxg.pilesos.Apparat;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.addon.AddonManager;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.render.RenderManager;
import by.fxg.speceditor.serialization.SpecEditorSerialization;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.utils.ApplicationTools;
import by.fxg.speceditor.utils.Utils;

public class SpecEditor extends Apparat<GInputProcessor> {
	public static boolean DEBUG = false;
	public static SpecEditor get;
	public static Foster fosterNoDraw;
	public ApplicationTools tools;
	
	public ResourceManager resourceManager;
	public RenderManager renderer;
	
	public SpecEditor(ApplicationTools tools, String... args) {
		this.tools = tools;
		this.setProgramArgs(args);
		if (this.hasProgramArgument("-debug")) DEBUG = true;
	}
	
	public void create() {
		this.onCreate(get = this);
		Gdx.app.setLogLevel(DEBUG ? Application.LOG_DEBUG : Application.LOG_ERROR);
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
		Utils.init();
		SpecEditorSerialization.INSTANCE = new SpecEditorSerialization();
		DefaultResources.INSTANCE = new DefaultResources();
		ProjectManager.INSTANCE = new ProjectManager();
		AddonManager.INSTANCE = new AddonManager();
		SpecEditorSerialization.INSTANCE.init();
		//post
		ProjectManager.INSTANCE.postInit();
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
	
	/** Returns preference value from project configuration **/
	public static <TYPE> TYPE getPreference(String name, Class<TYPE> typeClass, TYPE defaultValue) {
		if (!DefaultResources.settings.containsKey("PREFERENCES")) DefaultResources.settings.add("PREFERENCES");
		return DefaultResources.settings.get("PREFERENCES").containsKey(name) ? DefaultResources.settings.get("PREFERENCES").get(name, typeClass) : defaultValue;
	}

	/** Sets preference value in project configuration **/
	public static void setPreference(String name, Object object) {
		if (!DefaultResources.settings.containsKey("PREFERENCES")) DefaultResources.settings.add("PREFERENCES");
		DefaultResources.settings.get("PREFERENCES").put(name, object);
	}
	
	public void dispose() {
		try { DefaultResources.settings.store(); } catch (Exception e) {}
		super.dispose();
		this.resourceManager.assetManager.dispose();
	}
	
	public void resize(int width, int height) {
		super.resize(width, height);
		this.renderer.resize(width, height);
	}
}