package by.fxg.speceditor.std.gizmos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.TextureFrameBuffer;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.api.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.api.std.objectTree.TreeElement;
import by.fxg.speceditor.screen.deprecated.SubscreenViewport;
import by.fxg.speceditor.std.render.DebugDraw3D;
import by.fxg.speceditor.ui.SpecInterface;
import by.fxg.speceditor.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.utils.Utils;

public class GizmosModule implements IFocusable {
	private TextureFrameBuffer frameBuffer;
	private ModelBatch modelBatch;
	private DebugDraw3D debugDraw;
	private GizmoHitbox[] hitboxes = new GizmoHitbox[3];
	private ModelInstance xyzShape;
	
	/** Current selected tool in Viewport, e.g. It can be null as None tool selected, or Translation tool for example. <br>
	 * Also this enum used for specifying available transform actions in {@link ITreeElementGizmos#isTransformSupported(GizmoTransformType)} **/
	public GizmoTransformType selectedTool = null;
	
	/** Current interaction type **/
	private GizmoInteractType interactType = GizmoInteractType.NONE;
	
	private Vector3
		_tmpVector = new Vector3(),
		_gizmoRenderPosition = new Vector3(), //render position, posision+offset
		_gizmoClickOffset = new Vector3(), //interact start position mouse click offset, used for offsetting current gizmo
		_gizmoStart = new Vector3(); //interact start position, used for scaling gizmo arrows
	
	/** just keep it as it is, i want to make viewport-locked cursor like in blender later **/
	private Vector2 prevMousePosition = new Vector2();

	/** current selected elements that can be interacted with gizmos **/
	private Array<ITreeElementGizmos> elements = new Array<>();
	
	public GizmosModule() {
		this.frameBuffer = new TextureFrameBuffer().flip(false, true);
		this.modelBatch = new ModelBatch();
		this.debugDraw = new DebugDraw3D();
		
		for (int i = 0; i != this.hitboxes.length; i++) {
			this.hitboxes[i] = new GizmoHitbox(i);
			this.debugDraw.world.addCollisionObject(this.hitboxes[i].object);
		}

		ModelBuilder mb = new ModelBuilder();
		this.xyzShape = new ModelInstance(mb.createXYZCoordinates(3f, 0.125f, 0.375f, 5, GL20.GL_TRIANGLES, new Material(), Usage.Position | Usage.Normal | Usage.ColorPacked));
	}
	
	public void update(SubscreenViewport screenViewport, int x, int y, int width, int height) {
		//SpecInterface checks if no of other elements are selected to not mess up with interface, and it's needed for elements to have at least one (selected in ObjectTree) element
		if (GDXUtil.isMouseInArea(x, y, width, height) && !this.elements.isEmpty()) {
			if (this.isFocused()) {
				SpecInterface.setCursor(AppCursor.GRABBING);
				switch (this.selectedTool) {
					case TRANSLATE: this.processTranslation(screenViewport.camera, x, y, width, height); break;
					case ROTATE: this.processRotation(screenViewport.camera, x, y, width, height); break;
					case SCALE: this.processScaling(screenViewport.camera, x, y, width, height); break;
				}
			} else if (SpecInterface.isFocused(null)) {
				//just waiting for click here
				this.updateGizmosPosition();
				float mx = Interpolation.linear.apply(0, Utils.getWidth(), (GDXUtil.getMouseX() - x) / (float)width);
				float my = Interpolation.linear.apply(0, Utils.getHeight(), 1f - ((GDXUtil.getMouseY() - y) / (float)height));
				Ray ray = screenViewport.camera.getPickRay(mx, my);
				GizmoInteractType hitType = this.isRayCastedGizmo(ray);
				if (hitType != GizmoInteractType.NONE) {
					SpecInterface.setCursor(AppCursor.GRAB);
					if (Game.get.getInput().isMouseDown(0, false)) {
						callback.getHitPointWorld(this._tmpVector);
						this._gizmoStart.set(this._gizmoRenderPosition);
						ray.getEndPoint(this._gizmoClickOffset, screenViewport.camera.position.dst(this._gizmoRenderPosition));
						this._gizmoClickOffset.sub(this._gizmoRenderPosition);
						//Utils.logDebug("", this._gizmoRenderPosition, "(", this._tmpVector, ") * ", screenViewport.camera.position.dst(this._gizmoRenderPosition), " = ", this._gizmoClickOffset);
						
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
		float scale = screenViewport.camera.position.dst(this.isFocused() ? this._gizmoStart : this._gizmoRenderPosition) / 12.5F;
		for (GizmoHitbox gizmoHitbox : this.hitboxes) gizmoHitbox.update(this._gizmoRenderPosition, scale);
		this.xyzShape.transform.setToTranslation(this._gizmoRenderPosition).scale(scale, scale, scale);
		this.debugDraw.update();
	}
	
	/** Processing of gizmo with translation mode **/
	private void processTranslation(Camera camera, int x, int y, int width, int height) {
		int mx = (int)Interpolation.linear.apply(0, Gdx.graphics.getWidth(), (GDXUtil.getMouseX() - x) / (float)width);
		int my = (int)Interpolation.linear.apply(0, Gdx.graphics.getHeight(), 1f - ((GDXUtil.getMouseY() - y) / (float)height));
		camera.getPickRay(mx, my).getEndPoint(this._tmpVector, camera.position.dst(this._gizmoRenderPosition));
		this._tmpVector.sub(this._gizmoClickOffset).sub(this._gizmoRenderPosition); //FIXME NOT WORKING, cursor is doing anything except single thing needed
		
		if (Game.get.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) && Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) {
			this.roundVector(this._tmpVector, 10f);
		} else if (Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) {
			this.roundVector(this._tmpVector, 1f);
		} else if (Game.get.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
			this.roundVector(this._tmpVector, 0.1f);
		}
		
		switch (this.interactType) {
			case TRANSLATE_X: this._tmpVector.set(this._tmpVector.x, 0, 0); break;
			case TRANSLATE_Y: this._tmpVector.set(0, this._tmpVector.y, 0); break;
			case TRANSLATE_Z: this._tmpVector.set(0, 0, this._tmpVector.z); break;
			default: break;
		}
		
		this._gizmoRenderPosition.add(this._tmpVector);
		for (ITreeElementGizmos gizmoElement : this.elements) {
			if (gizmoElement.isTransformSupported(this.selectedTool)) {
				gizmoElement.getTransform(this.selectedTool).add(this._tmpVector);
			}
		}
		
		//end of interaction
		if (!Game.get.getInput().isMouseDown(0, true)) {
			this.setFocused(false);
		}
	}
	
	/** Processing of gizmo with rotation mode **/
	private void processRotation(Camera camera, int x, int y, int width, int height) {
		//this must be done with screen-space maybe
	}
	
	/** Processing of gizmo with scaling mode **/
	private void processScaling(Camera camera, int x, int y, int width, int height) {
		//i think we can do scaling the same as translation?
	}
	
	/** Renders gizmo arrows to {@link #frameBuffer} **/
	public void passRender(Camera camera) {
		if (!this.elements.isEmpty()) {
			float prevFarValue = camera.far;
			camera.far = 500.0F;
			camera.update();
			
			this.frameBuffer.capture(0f, 0f, 0f, 0f);
			this.modelBatch.begin(camera);
			//TODO Disable rendering of default xyz arrows for every type of tool, add grid rendering if tool is being interacted
			this.modelBatch.render(this.xyzShape);
			this.modelBatch.end();
//			this.debugDraw.drawer.begin(camera);
//			this.debugDraw.world.debugDrawWorld();
//			this.debugDraw.drawer.end();
			this.frameBuffer.endCapture();
			
			camera.far = prevFarValue;
			camera.update();
		} else {
			this.frameBuffer.capture(0f, 0f, 0f, 0f);
			this.frameBuffer.endCapture();
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
	public TextureRegion getTexture() {
		return this.frameBuffer.getTexture();
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
		this._gizmoRenderPosition.set(0, 0, 0);
		if (!this.elements.isEmpty()) {
			int positions = 0;
			for (ITreeElementGizmos gizmoElement : this.elements) {
				if (gizmoElement.isTransformSupported(GizmoTransformType.TRANSLATE)) {
					this._gizmoRenderPosition.add(gizmoElement.getOffsetTransform(GizmoTransformType.TRANSLATE)).add(gizmoElement.getTransform(GizmoTransformType.TRANSLATE));
					positions++;
				}
			}
			this._gizmoRenderPosition.scl(1.0F / positions);
		}
	}
	
	private void roundVector(final Vector3 vector, float precision) {
		vector.x = MathUtils.round(vector.x * precision) / precision;
		vector.y = MathUtils.round(vector.x * precision) / precision;
		vector.z = MathUtils.round(vector.x * precision) / precision;
	}
}
