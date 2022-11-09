package by.fxg.speceditor.std.gizmos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.pilesos.graphics.TextureFrameBuffer;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.render.DebugDraw3D;
import by.fxg.speceditor.screen.project.SubscreenViewport;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.utils.Utils;

public class GizmosModule implements IFocusable {
	private PerspectiveCamera camera;
	private TextureFrameBuffer framebuffer;
	private ModelBatch modelBatch;
	private DebugDraw3D debugDraw;
	private GizmoHitbox[] hitboxes = new GizmoHitbox[3];
	private ModelInstance[] gizmoModel = new ModelInstance[3];
	
	/** Current selected tool in Viewport, e.g. It can be null as None tool selected, or Translation tool for example. <br>
	 * Also this enum used for specifying available transform actions in {@link ITreeElementGizmos#isTransformSupported(GizmoTransformType)} **/
	public GizmoTransformType selectedTool = null;
	
	/** Current interaction type **/
	private GizmoInteractType interactType = GizmoInteractType.NONE;
	/** Mouse position from previous frame, used in the position gizmo to stop translation if mouse not changed position **/
	private Vector2 prevMousePosition = new Vector2();
	private Vector3
		tmpVector = new Vector3(),
		gizmoRenderPosition = new Vector3(), //render position, posision+offset
		gizmoClickOffset = new Vector3(), //interact start position mouse click offset, used for offsetting current gizmo
		gizmoStart = new Vector3(); //interact start position, used for scaling gizmo arrows
	

	/** current selected elements that can be interacted with gizmos **/
	private Array<ITreeElementGizmos> elements = new Array<>();
	
	public GizmosModule(PerspectiveCamera camera) {
		this.camera = camera;
		this.framebuffer = new TextureFrameBuffer().flip(false, true);
		this.modelBatch = new ModelBatch();
		this.debugDraw = new DebugDraw3D();
		
		for (int i = 0; i != this.hitboxes.length; i++) {
			this.hitboxes[i] = new GizmoHitbox(i);
			this.debugDraw.world.addCollisionObject(this.hitboxes[i].object);
		}

		TextureRegion gizmoDiffuse = SpriteStack.getTextureRegion("defaults/gizmo.diffuse.png");
		gizmoDiffuse.flip(false, true);
		String[] modelName = {"translate", "rotate", "scale"};
		for (int i = 0; i != this.gizmoModel.length; i++) {
			this.gizmoModel[i] = new ModelInstance(SpecEditor.get.resourceManager.get(Utils.format("defaults/gizmo.", modelName[i], ".obj"), Model.class));
			this.gizmoModel[i].materials.get(0).set(TextureAttribute.createDiffuse(gizmoDiffuse));
		}
	}
	
	public void update(SubscreenViewport screenViewport, int x, int y, int width, int height) {
		//SpecInterface checks if no of other elements are selected to not mess up with interface, and it's needed for elements to have at least one (selected in ObjectTree) element
		if (GDXUtil.isMouseInArea(x, y, width, height) && !this.elements.isEmpty()) {
			if (this.isFocused()) {
				SpecInterface.setCursor(AppCursor.GRABBING);
				switch (this.selectedTool) {
					case TRANSLATE: this.processTranslation(this.camera, x, y, width, height); break;
					case ROTATE: this.processRotation(this.camera, x, y, width, height); break;
					case SCALE: this.processScaling(this.camera, x, y, width, height); break;
				}
			} else if (SpecInterface.isFocused(null)) {
				//just waiting for click here
				float mx = Interpolation.linear.apply(0, Utils.getWidth(), (GDXUtil.getMouseX() - x) / (float)width);
				float my = Interpolation.linear.apply(0, Utils.getHeight(), 1f - ((GDXUtil.getMouseY() - y) / (float)height));
				Ray ray = this.camera.getPickRay(mx, my);
				GizmoInteractType hitType = this.isRayCastedGizmo(ray);
				if (hitType != GizmoInteractType.NONE) {
					SpecInterface.setCursor(AppCursor.GRAB);
					if (SpecEditor.get.getInput().isMouseDown(0, false)) {
						callback.getHitPointWorld(this.tmpVector);
						this.gizmoStart.set(this.gizmoRenderPosition);
						ray.getEndPoint(this.gizmoClickOffset, this.camera.position.dst(this.gizmoRenderPosition));
						this.gizmoClickOffset.sub(this.gizmoRenderPosition);
						this.interactType = hitType;
						this.setFocused(true);
					}
				}
			}
		}
//		screen-space skip, for future
//		int mx = GDXUtil.getMouseX();
//		int my = GDXUtil.getMouseY();
//		if (mx < x) { Gdx.input.setCursorPosition(x + width, Gdx.graphics.getHeight() - my); this.prevMousePosition.x = x + width; }
//		else if (mx > x + width) { Gdx.input.setCursorPosition(x, Gdx.graphics.getHeight() - my); this.prevMousePosition.x = x; }
//		if (my < y) { Gdx.input.setCursorPosition(mx, Gdx.graphics.getHeight() - y - height); this.prevMousePosition.y = Gdx.graphics.getHeight() - y - height; }
//		else if (my > y + height) { Gdx.input.setCursorPosition(mx, Gdx.graphics.getHeight() - y); this.prevMousePosition.y = Gdx.graphics.getHeight() - y; }
		if (this.selectedTool != null) {
			this.updateGizmosPosition();
			float scale = this.camera.position.dst(this.isFocused() ? this.gizmoStart : this.gizmoRenderPosition) / 6.725F;
			for (GizmoHitbox gizmoHitbox : this.hitboxes) gizmoHitbox.update(this.gizmoRenderPosition, scale);
			this.gizmoModel[this.selectedTool.ordinal()].transform.setToTranslation(this.gizmoRenderPosition).scale(scale, scale, scale);
			this.debugDraw.update();
		}
	}
	
	/** Processing of gizmo with translation mode **/
	private void processTranslation(Camera camera, int x, int y, int width, int height) {
		int mx = (int)Interpolation.linear.apply(0, Gdx.graphics.getWidth(), (GDXUtil.getMouseX() - x) / (float)width);
		int my = (int)Interpolation.linear.apply(0, Gdx.graphics.getHeight(), 1f - ((GDXUtil.getMouseY() - y) / (float)height));
		if (this.prevMousePosition.x != mx || this.prevMousePosition.y != my) {
			camera.getPickRay(mx, my).getEndPoint(this.tmpVector, camera.position.dst(this.gizmoRenderPosition));
			this.tmpVector.sub(this.gizmoClickOffset).sub(this.gizmoRenderPosition);
			
			if (SpecEditor.get.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) && SpecEditor.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) {
				this.roundVector(this.tmpVector, 10f);
			} else if (SpecEditor.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) {
				this.roundVector(this.tmpVector, 1f);
			} else if (SpecEditor.get.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
				this.roundVector(this.tmpVector, 0.1f);
			}
			
			switch (this.interactType) {
				case TRANSLATE_X: this.tmpVector.set(this.tmpVector.x, 0, 0); break;
				case TRANSLATE_Y: this.tmpVector.set(0, this.tmpVector.y, 0); break;
				case TRANSLATE_Z: this.tmpVector.set(0, 0, this.tmpVector.z); break;
				default: break;
			}
			
			this.gizmoRenderPosition.add(this.tmpVector);
			for (ITreeElementGizmos gizmoElement : this.elements) {
				if (gizmoElement.isTransformSupported(this.selectedTool)) {
					gizmoElement.getTransform(this.selectedTool).add(this.tmpVector);
				}
			}
			this.prevMousePosition.set(mx, my);
		}
		//end of interaction
		if (!SpecEditor.get.getInput().isMouseDown(0, true)) {
			this.setFocused(false);
		}
	}
	
	/** Processing of gizmo with rotation mode **/
	private void processRotation(Camera camera, int x, int y, int width, int height) {
		//this must be done with screen-space maybe
		
		if (!SpecEditor.get.getInput().isMouseDown(0, true)) {
			this.setFocused(false);
		}
	}
	
	/** Processing of gizmo with scaling mode **/
	private void processScaling(Camera camera, int x, int y, int width, int height) {
		
		if (!SpecEditor.get.getInput().isMouseDown(0, true)) {
			this.setFocused(false);
		}
	}
	
	/** Renders gizmo arrows to {@link #framebuffer} **/
	public void passRender(Camera camera) {		
		if (!this.elements.isEmpty()) {
			float prevFarValue = camera.far;
			camera.far = 500.0F;
			camera.update();
			
			this.framebuffer.capture(0f, 0f, 0f, 0f);
			this.modelBatch.begin(camera);
			//XXX [IDEA] add grid rendering if tool is being interacted
			if (this.selectedTool != null) {
				this.modelBatch.render(this.gizmoModel[this.selectedTool.ordinal()]);
			}
			this.modelBatch.end();
			this.debugDraw.drawer.begin(camera);
			this.debugDraw.world.debugDrawWorld();
			this.debugDraw.drawer.end();
			this.framebuffer.endCapture();
			camera.far = prevFarValue;
			camera.update();
		} else {
			this.framebuffer.capture(0f, 0f, 0f, 0f);
			this.framebuffer.endCapture();
		}
	}
	
	/** **/
	public void updateSelectorMode(ITreeElementSelector<?> selector) {
		this.elements.clear();
		if (this.selectedTool != null) {
			for (TreeElement element : selector.getIterable()) {
				if (element instanceof ITreeElementGizmos && ((ITreeElementGizmos)element).isTransformSupported(this.selectedTool)) {
					this.elements.add((ITreeElementGizmos)element);
				}
			}
		}
		this.updateGizmosPosition();
	}
	
	/** Returns rendered gizmo arrows as TextureRegion for further rendering **/
	public TextureRegion getRenderPassTexture() {
		return this.framebuffer.getTexture();
	}
	
	private static final ClosestRayResultCallback callback = new ClosestRayResultCallback(new Vector3(), new Vector3());
	/** Returns {@link GizmoInteractType} if param ray intersected one of gizmo arrows **/
	private GizmoInteractType isRayCastedGizmo(Ray ray) {
		if (this.selectedTool != null) {
			callback.setCollisionObject(null);
		    callback.setClosestHitFraction(1.0F);
		    this.debugDraw.world.rayTest(ray.origin, ray.direction.cpy().scl(8192F).add(ray.origin), callback);
		    if (callback.hasHit()) {
		    	if (callback.getCollisionObject() != null && callback.getCollisionObject().userData != null && this.selectedTool.ordinal() > -1) {
		    		return GizmoInteractType.values()[(((GizmoHitbox)callback.getCollisionObject().userData).type + 1) + this.selectedTool.ordinal() * 3];
		    	}
		    }
		}
		return GizmoInteractType.NONE;
	}
	
	private void updateGizmosPosition() {
		this.gizmoRenderPosition.set(0, 0, 0);
		if (!this.elements.isEmpty()) {
			int positions = 0;
			Vector3 tmp = new Vector3();
			for (ITreeElementGizmos gizmoElement : this.elements) {
				if (gizmoElement.isTransformSupported(GizmoTransformType.TRANSLATE)) {
					this.gizmoRenderPosition.add(gizmoElement.getOffsetTransform(tmp, GizmoTransformType.TRANSLATE).add(gizmoElement.getTransform(GizmoTransformType.TRANSLATE)));
					tmp.setZero();
					positions++;
				}
			}
			this.gizmoRenderPosition.scl(1.0F / positions);
		}
	}
	
	private void roundVector(final Vector3 vector, float precision) {
		vector.x = MathUtils.round(vector.x / precision) * precision;
		vector.y = MathUtils.round(vector.y / precision) * precision;
		vector.z = MathUtils.round(vector.z / precision) * precision;
	}
}
