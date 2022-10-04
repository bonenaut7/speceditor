package by.fxg.speceditor.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.decals.BaseDecal;
import by.fxg.pilesos.decals.CameraAlphaGroupStrategy;
import by.fxg.pilesos.decals.DecalDrawer;
import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.pilesos.graphics.TextureFrameBuffer;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.elements.ElementLight;
import by.fxg.speceditor.project.Project;
import by.fxg.speceditor.tools.debugdraw.DebugDraw3D;
import by.fxg.speceditor.tools.debugdraw.IDebugDraw;
import by.fxg.speceditor.tools.g3d.IModelProvider;

public class GLTFRenderer implements IRendererType {
	private Camera camera;
	private PMObjectExplorer pmObjectExplorer;
	
	private TextureFrameBuffer frameBuffer;
	private ModelBatch modelBatch;
	private DebugDraw3D debugDraw;
	
	private DecalDrawer editorDecalDrawer; //2nd pass
	private DecalDrawer decalDrawer;
	private Environment environment;
	private Array<IModelProvider> modelProviders;
	private Array<IDebugDraw> debugDrawables;
	
	public GLTFRenderer(PMObjectExplorer pmObjectExplorer, Camera camera) {
		this.camera = camera;
		this.pmObjectExplorer = pmObjectExplorer;
		
		this.frameBuffer = new TextureFrameBuffer().flip(false, true);
		this.modelBatch = new ModelBatch();
		this.debugDraw = new DebugDraw3D();
		
		this.editorDecalDrawer = new DecalDrawer(new CameraAlphaGroupStrategy(camera));
		this.decalDrawer = new DecalDrawer(new CameraAlphaGroupStrategy(camera));
		this.environment = new Environment();
		this.modelProviders = new Array<>();
		this.debugDrawables = new Array<>();
	}

	public void addAttribute(Attribute attribute) {}
	public void removeAttribute(Attribute attribute) {}

	public void addDecal(BaseDecal decal) {
		this.decalDrawer.decalsToProduce.add(decal);
	}

	public void addLight(ElementLight element, boolean selected, boolean visible) {
		this.editorDecalDrawer.decalsToProduce.add(element.editorDecal);
		element.editorDecal.setDecal(Decal.newDecal(SpriteStack.getTextureRegion(String.format("defaults/sceneLight_%s_%s.png", selected, visible))));
		element.editorDecal.getDecal().setScale(0.0015f, 0.0015f);
		element.editorDecal.getDecal().setPosition(element.light.position);
		if (selected || visible) this.environment.add(element.light);
	}
	
	public void addRenderable(IModelProvider modelProvider) { 
		this.modelProviders.add(modelProvider);
	}
	
	public void addDebugDrawable(IDebugDraw debugDraw) {
		this.debugDrawables.add(debugDraw); 
	}

	public void update() {
		this.clear(true);
		this.pmObjectExplorer.refreshData();
	}

	public void passRender() {
		this.frameBuffer.capture(Project.instance.bufferColor);
		this.modelBatch.begin(this.camera);
		for (IModelProvider modelProvider : this.modelProviders) {
			this.modelBatch.render(modelProvider.applyTransforms().getDefaultModel(), this.environment);
		}
		this.modelBatch.end();
		this.modelBatch.flush();
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		this.decalDrawer.draw(this.camera);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		this.editorDecalDrawer.draw(this.camera);
		
		if (Project.instance.viewportHitboxDepth) Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glLineWidth(Project.instance.viewportHitboxWidth);
		this.debugDraw.drawer.begin(this.camera);
		for (IDebugDraw debugDraw : this.debugDrawables) debugDraw.draw(this.pmObjectExplorer, this.debugDraw);
		this.debugDraw.drawer.end();
		if (Project.instance.viewportHitboxDepth) Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		this.frameBuffer.endCapture();
	}
	
	public TextureRegion getTexture() {
		return this.frameBuffer.getTexture();
	}

	public void clear(boolean partially) {
		this.editorDecalDrawer.decalsToProduce.clear();
		this.decalDrawer.decalsToProduce.clear();
		this.environment.clear();
		this.modelProviders.clear();
		this.debugDrawables.clear();
		if (partially) this.environment.set(Project.instance.viewportAttributes);
	}
}
