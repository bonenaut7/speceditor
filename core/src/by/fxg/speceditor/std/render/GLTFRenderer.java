package by.fxg.speceditor.std.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.decals.BaseDecal;
import by.fxg.pilesos.decals.CameraAlphaGroupStrategy;
import by.fxg.pilesos.decals.DecalDrawer;
import by.fxg.pilesos.graphics.TextureFrameBuffer;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.std.g3d.IModelProvider;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.objecttree.SpecObjectTree;
import by.fxg.speceditor.std.objecttree.elements.ElementLight;
import by.fxg.speceditor.std.render.DebugDraw3D.IDebugDraw;
import by.fxg.speceditor.utils.Utils;

public class GLTFRenderer implements IRendererType {
	private Camera camera;
	private SpecObjectTree pmObjectExplorer;
	
	private TextureFrameBuffer frameBuffer;
	private ModelBatch modelBatch;
	private DebugDraw3D debugDraw;
	
	private DecalDrawer editorDecalDrawer; //2nd pass
	private DecalDrawer decalDrawer;
	private Environment environment;
	private Array<IModelProvider> modelProviders;
	private Array<IDebugDraw> debugDrawables;
	
	public GLTFRenderer(SpecObjectTree pmObjectExplorer, Camera camera) {
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

	public void add(Object object) {
		if (object instanceof IDebugDraw) this.debugDrawables.add((IDebugDraw)object);
		if (object instanceof IModelProvider) this.modelProviders.add((IModelProvider)object);
		if (object instanceof BaseDecal) this.decalDrawer.decalsToProduce.add((BaseDecal)object);
	}
	
	public void addLight(ElementLight element, boolean selected) {
		this.editorDecalDrawer.decalsToProduce.add(element._viewportDecal);
		element._viewportDecal.setDecal(Game.storage.decals.get(Utils.format("viewport/light.", selected)));
		element._viewportDecal.getDecal().setScale(0.0015f, 0.0015f);
		element._viewportDecal.getDecal().setPosition(element.getTransform(GizmoTransformType.TRANSLATE));
		this.environment.add(element.getLight(BaseLight.class));
	}

	public void update() {
		this.clear(true);
		this.pmObjectExplorer.refreshTree();
	}

	public void passRender() {
		if (ViewportSettings.shouldUpdate) {
			this.update();
			ViewportSettings.shouldUpdate = false;
		}
		
		this.frameBuffer.capture(ViewportSettings.bufferColor);
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
		
		if (ViewportSettings.viewportHitboxDepth) Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glLineWidth(ViewportSettings.viewportHitboxWidth);
		this.debugDraw.drawer.begin(this.camera);
		for (IDebugDraw debugDraw : this.debugDrawables) debugDraw.draw(this.pmObjectExplorer, this.debugDraw);
		this.debugDraw.drawer.end();
		if (ViewportSettings.viewportHitboxDepth) Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
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
		if (partially) this.environment.set(ViewportSettings.viewportAttributes);
	}
}
