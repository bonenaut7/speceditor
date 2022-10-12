package by.fxg.speceditor.std.viewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.decals.CameraAlphaGroupStrategy;
import by.fxg.pilesos.decals.DecalDrawer;
import by.fxg.pilesos.graphics.TextureFrameBuffer;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.render.DebugDraw3D;
import by.fxg.speceditor.render.DebugDraw3D.IDebugDraw;
import by.fxg.speceditor.std.editorPane.EditorPane;
import by.fxg.speceditor.std.g3d.IModelProvider;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.elements.ElementDecal;
import by.fxg.speceditor.std.objectTree.elements.ElementLight;
import by.fxg.speceditor.utils.Utils;

public class DefaultRenderer implements IViewportRenderer {
	private final EditorPaneDefaultViewportRenderer editorPaneDefaultViewportRenderer;
	private PerspectiveCamera camera;
	private SpecObjectTree objectTree;
	private TextureFrameBuffer frameBuffer;
	
	//Preferences
	protected Vector3 cameraSettings = new Vector3(67F, 50.0f, 0.1f); //FOV, Far, Near
	protected Color bufferColor = new Color(0.12F, 0.12F, 0.12F, 1.0F);
	protected Attributes viewportAttributes = new Attributes() {{
		set(new BlendingAttribute(1f), FloatAttribute.createAlphaTest(0.5f), ColorAttribute.createAmbientLight(0.4f, 0.4f, 0.4f, 1F));
	}};
	
	protected boolean featureHitboxDepth = true;
	protected float featureHitboxWidth = 2.0F;
	protected boolean featureRenderGrid = true;
	
	//Scene environment
	private ModelBatch sceneModelBatch;
	private DecalDrawer sceneDecalDrawer;
	private DebugDraw3D sceneDebugDraw;
	private Environment sceneEnvironment = new Environment();
	private Array<IModelProvider> modelProviders = new Array<>();
	private Array<IDebugDraw> debugDrawables = new Array<>();
	
	//Renderer environment, features
	private DecalDrawer editorDecalDrawer; //2nd decal pass
	private ModelInstance modelGrid;

	public DefaultRenderer(SpecObjectTree objectTree) {
		this.camera = new PerspectiveCamera(67, 1, 1);
		this.objectTree = objectTree;
		this.editorPaneDefaultViewportRenderer = new EditorPaneDefaultViewportRenderer(this);
		this.frameBuffer = new TextureFrameBuffer().flip(false, true);
		
		this.sceneModelBatch = new ModelBatch(new DefaultShaderProvider(new DefaultShader.Config() {{
			this.numPointLights = 16;
		}}));
		this.sceneDecalDrawer = new DecalDrawer(new CameraAlphaGroupStrategy(camera));
		this.sceneDebugDraw = new DebugDraw3D();

		this.editorDecalDrawer = new DecalDrawer(new CameraAlphaGroupStrategy(camera));
		ModelBuilder mb = new ModelBuilder();
		this.modelGrid = new ModelInstance(mb.createLineGrid(1000, 1000, 1F, 1F, new Material(), Usage.Position | Usage.Normal | Usage.ColorPacked));
		this.modelGrid.materials.get(0).set(ColorAttribute.createDiffuse(1, 1, 1, 0.33F), new BlendingAttribute(0.5F), FloatAttribute.createAlphaTest(0.1F));
	}

	public void add(SpecObjectTree objectTree, Object object, Object... objects) {
		if (object instanceof IDebugDraw) this.debugDrawables.add((IDebugDraw)object);
		if (object instanceof IModelProvider) this.modelProviders.add((IModelProvider)object);
		if (object instanceof ElementLight) {
			ElementLight element = (ElementLight)object;
			this.editorDecalDrawer.decalsToProduce.add(element._viewportDecal);
			element._viewportDecal.setDecal(Game.storage.decals.get(Utils.format("viewport/light.", objectTree.elementSelector.isElementSelected(element))));
			element._viewportDecal.getDecal().setScale(0.0015f, 0.0015f);
			element._viewportDecal.getDecal().setPosition(element.getTransform(GizmoTransformType.TRANSLATE));
			this.sceneEnvironment.add(element.getLight(BaseLight.class));
		} else if (object instanceof ElementDecal) {
			 this.sceneDecalDrawer.decalsToProduce.add(((ElementDecal)object).decal); 
		}
	}
	
	public void setCameraValues(float fieldOfView, float far, float near) {
		this.cameraSettings.set(fieldOfView, far, near);
		this.camera.fieldOfView = fieldOfView;
		this.camera.far = this.cameraSettings.y;
		this.camera.near = this.cameraSettings.z;
		this.camera.update();
	}

	public void render() {
		this.frameBuffer.capture(this.bufferColor);
		this.sceneModelBatch.begin(this.camera);
		if (this.featureRenderGrid) this.sceneModelBatch.render(this.modelGrid);
		for (IModelProvider modelProvider : this.modelProviders) {
			this.sceneModelBatch.render(modelProvider.applyTransforms().getDefaultModel(), this.sceneEnvironment);
		}
		this.sceneModelBatch.end();
		this.sceneModelBatch.flush();
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		this.sceneDecalDrawer.draw(this.camera);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		this.editorDecalDrawer.draw(this.camera);
		
		if (this.featureHitboxDepth) {
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glLineWidth(this.featureHitboxWidth);
			this.sceneDebugDraw.drawer.begin(this.camera);
			for (IDebugDraw debugDraw : this.debugDrawables) debugDraw.draw(this.objectTree, this.sceneDebugDraw);
			this.sceneDebugDraw.drawer.end();
			Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		} else {
			this.sceneDebugDraw.drawer.begin(this.camera);
			for (IDebugDraw debugDraw : this.debugDrawables) debugDraw.draw(this.objectTree, this.sceneDebugDraw);
			this.sceneDebugDraw.drawer.end();
		}
		this.frameBuffer.endCapture();
	}
	
	public TextureRegion getTexture() {
		return this.frameBuffer.getTexture();
	}

	public void clear() {
		this.editorDecalDrawer.decalsToProduce.clear();
		this.sceneDecalDrawer.decalsToProduce.clear();
		this.sceneEnvironment.remove(DirectionalLightsAttribute.Type);
		this.sceneEnvironment.remove(PointLightsAttribute.Type);
		this.sceneEnvironment.remove(SpotLightsAttribute.Type);
		this.modelProviders.clear();
		this.debugDrawables.clear();
	}

	public EditorPane getEditorPane() {
		return this.editorPaneDefaultViewportRenderer;
	}
	
	public PerspectiveCamera getCamera() {
		return this.camera;
	}
}
