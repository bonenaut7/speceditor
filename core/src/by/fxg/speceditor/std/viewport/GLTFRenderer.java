package by.fxg.speceditor.std.viewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import by.fxg.pilesos.decals.CameraAlphaGroupStrategy;
import by.fxg.pilesos.decals.DecalDrawer;
import by.fxg.pilesos.graphics.TextureFrameBuffer;
import by.fxg.speceditor.render.DebugDraw3D;
import by.fxg.speceditor.render.DebugDraw3D.IDebugDraw;
import by.fxg.speceditor.std.editorPane.EditorPane;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.objectTree.ITreeElementModelProvider;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.elements.ElementDecal;
import by.fxg.speceditor.std.objectTree.elements.ElementGLTFLight;
import by.fxg.speceditor.std.objectTree.elements.ElementLight;

public class GLTFRenderer implements IViewportRenderer {
	private final EditorPaneGLTFViewportRenderer editorPane;
	private PerspectiveCamera camera;
	private SpecObjectTree objectTree;
	private TextureFrameBuffer frameBuffer;
	
	//Preferences
	public Vector3 cameraSettings = new Vector3(67F, 50.0f, 0.1f); //FOV, Far, Near
	public Color bufferColor = new Color(0.12F, 0.12F, 0.12F, 1.0F);
	
	protected boolean featureHitboxDepth = true;
	protected float featureHitboxWidth = 2.0F;
	protected boolean featureRenderGrid = true;
	
	//Scene environment
	protected GLTFRendererScene gltfScene;
	private DecalDrawer decalDrawer;
	private DebugDraw3D debugDraw;
	private Array<IDebugDraw> debugDrawables = new Array<>();
	
	//Renderer environment, features
	protected DecalDrawer editorDecalDrawer; //2nd decal pass

	public GLTFRenderer(SpecObjectTree objectTree) {
		this.camera = new PerspectiveCamera(67, 1, 1);
		this.camera.fieldOfView = this.cameraSettings.x;
		this.camera.far = this.cameraSettings.y;
		this.camera.near = this.cameraSettings.z;
		this.camera.update();
		
		this.objectTree = objectTree;
		this.frameBuffer = new TextureFrameBuffer().flip(false, true);
		this.gltfScene = GLTFRendererScene.create(this, this.camera, 24, 32, 32, 4);
		this.decalDrawer = new DecalDrawer(new CameraAlphaGroupStrategy(this.camera));
		this.debugDraw = new DebugDraw3D();

		this.editorDecalDrawer = new DecalDrawer(new CameraAlphaGroupStrategy(this.camera));
		
		this.editorPane = new EditorPaneGLTFViewportRenderer(this);
	}

	public void add(SpecObjectTree objectTree, Object object, Object... objects) {
		if (object instanceof IDebugDraw) this.debugDrawables.add((IDebugDraw)object);
		if (object instanceof ITreeElementModelProvider) this.gltfScene.treeElementRenderableProviders.add((ITreeElementModelProvider)object);
		if (object instanceof ElementLight) {
			ElementLight element = (ElementLight)object;
			element._viewportDecal.getDecal().setScale(0.0015f, 0.0015f);
			element._viewportDecal.getDecal().setPosition(element.getTransform(GizmoTransformType.TRANSLATE));
			this.editorDecalDrawer.decalsToProduce.add(element._viewportDecal);
			this.gltfScene.environment.add(element.getLight(BaseLight.class));
		} else if (object instanceof ElementGLTFLight) {
			ElementGLTFLight element = (ElementGLTFLight)object;
			element._viewportDecal.getDecal().setScale(0.0015f, 0.0015f);
			element._viewportDecal.getDecal().setPosition(element.getTransform(GizmoTransformType.TRANSLATE));
			this.editorDecalDrawer.decalsToProduce.add(element._viewportDecal);
			this.gltfScene.environment.add(element.getLight(BaseLight.class));
		} else if (object instanceof ElementDecal) {
			 this.decalDrawer.decalsToProduce.add(((ElementDecal)object).decal); 
		}
	}
	
	public void setCameraValues(float fieldOfView, float far, float near) {
		this.cameraSettings.set(fieldOfView, far, near);
		this.camera.fieldOfView = fieldOfView;
		this.camera.far = far;
		this.camera.near = near;
		this.camera.update();
	}

	public void render() {
		this.frameBuffer.capture(this.bufferColor);
		this.gltfScene.update(Gdx.graphics.getDeltaTime());
		this.gltfScene.render();
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		this.decalDrawer.draw(this.camera);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		this.editorDecalDrawer.draw(this.camera);
		
		if (this.featureHitboxDepth) {
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glLineWidth(this.featureHitboxWidth);
			this.debugDraw.drawer.begin(this.camera);
			for (IDebugDraw debugDraw : this.debugDrawables) debugDraw.draw(this.objectTree, this.debugDraw);
			this.debugDraw.drawer.end();
			Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		} else {
			this.debugDraw.drawer.begin(this.camera);
			for (IDebugDraw debugDraw : this.debugDrawables) debugDraw.draw(this.objectTree, this.debugDraw);
			this.debugDraw.drawer.end();
		}
		this.frameBuffer.endCapture();
	}
	
	public TextureRegion getTexture() {
		return this.frameBuffer.getTexture();
	}

	public void reset() {
		this.editorDecalDrawer.decalsToProduce.clear();
		this.decalDrawer.decalsToProduce.clear();
		this.gltfScene.reset();
		this.debugDrawables.clear();
	}

	public EditorPane getEditorPane() {
		return this.editorPane;
	}
	
	public PerspectiveCamera getCamera() {
		return this.camera;
	}
	
	public void writeData(Kryo kryo, Output output) {
		kryo.writeObject(output, this.cameraSettings);
		kryo.writeObject(output, this.bufferColor);
		kryo.writeObjectOrNull(output, this.gltfScene.environment, Environment.class);

		output.writeBoolean(this.featureHitboxDepth);
		output.writeFloat(this.featureHitboxWidth);
		output.writeBoolean(this.featureRenderGrid);
	}
	
	public void readData(Kryo kryo, Input input) {
		this.cameraSettings = kryo.readObject(input, Vector3.class);
		this.camera.fieldOfView = this.cameraSettings.x;
		this.camera.far = this.cameraSettings.y;
		this.camera.near = this.cameraSettings.z;
		this.camera.update();
		
		this.bufferColor = kryo.readObject(input, Color.class);
		this.gltfScene.environment = kryo.readObject(input, Environment.class);
		this.editorPane.setEnvironment(this.gltfScene.environment);
		
		this.featureHitboxDepth = input.readBoolean();
		this.featureHitboxWidth = input.readFloat();
		this.featureRenderGrid = input.readBoolean();
	}
}
