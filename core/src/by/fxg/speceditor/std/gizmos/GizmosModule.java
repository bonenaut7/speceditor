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
import by.fxg.speceditor.TO_REMOVE.__TreeElement;
import by.fxg.speceditor.api.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.api.std.objectTree.TreeElement;
import by.fxg.speceditor.screen.deprecated.SubscreenViewport;
import by.fxg.speceditor.std.objecttree.SpecObjectTree;
import by.fxg.speceditor.std.render.DebugDraw3D;
import by.fxg.speceditor.ui.SpecInterface;
import by.fxg.speceditor.ui.SpecInterface.AppCursor;

public class GizmosModule {
	private TextureFrameBuffer frameBuffer;
	private ModelBatch modelBatch;
	private DebugDraw3D debugDraw;
	private GizmoHitbox[] hitboxes = new GizmoHitbox[3];
	private ModelInstance xyzShape;
	
	/** Current selected tool in Viewport, e.g. It can be null as None tool selected, or Translation tool for example. <br>
	 * Also this enum used for specifying available transform actions in {@link ITreeElementGizmos#isTransformSupported(GizmoTransformType)} **/
	public GizmoTransformType selectedTool = null;
	
	/** This needs to be removed **/ @Deprecated
	private Vector3 valueVec = new Vector3(), startVec = new Vector3(), clickOffset = new Vector3(), rayTmpVec = new Vector3(), upScaleGizmoVec = new Vector3();
	
	/** Current interaction type **/
	private GizmoInteractType interactType = GizmoInteractType.NONE;
	
	//gRP - render pos+offset, gS = start of interact, gE - end of interact
	private Vector3
		_tmpVector = new Vector3(),
		_gizmoRenderPosition = new Vector3(), //render position, posision+offset
		_gizmoClickOffset = new Vector3(), //interact start position mouse click offset/mouseClickOffset from origin, gizmoStart-gizmoClickOffset/gizmoEnd-gizmoClickOffset
		_gizmoStart = new Vector3(), //interact start position
		_gizmoEnd = new Vector3(); //interact end position
	
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
		this.xyzShape = new ModelInstance(mb.createXYZCoordinates(3f, 0.1f, 0.25f, 5, GL20.GL_TRIANGLES, new Material(), Usage.Position | Usage.Normal | Usage.ColorPacked));
	}
	
	public void update(SubscreenViewport screenViewport, int x, int y, int width, int height) {
		//SpecInterface checks if no of other elements are selected to not mess up with interface, and it's needed for elements to have at least one (selected in ObjectTree) element
		if (SpecInterface.isFocused(this) && !this.elements.isEmpty()) {
			
			//_tmpVector.set(this._gizmoRenderPosition).
			for (GizmoHitbox gizmoHitbox : this.hitboxes) gizmoHitbox.updatePosition(this.upScaleGizmoVec);
			this.xyzShape.transform.setToTranslation(this._gizmoRenderPosition);
		}
		
//		if (this.element != null && this.toolType > -1) {
//			if (this.toolType == 0) {
//				this.upScaleGizmoVec.set(this.valueVec).add(this.element.getOffsetTransform(GizmoTransformType.TRANSLATE));
//			} else if (this.element.isTransformable(GizmoTransformType.TRANSLATE)) {
//				this.upScaleGizmoVec.set(this.element.getTransform(GizmoTransformType.TRANSLATE)).add(this.element.getOffsetTransform(GizmoTransformType.TRANSLATE));
//			}
//			for (GizmoHitbox gizmoHitbox : this.hitboxes) gizmoHitbox.updatePosition(this.upScaleGizmoVec);
//			this.xyzShape.transform.setToTranslation(this.upScaleGizmoVec);
//		}
		//element update
//		SpecObjectTree objectTree = screenViewport.objectTree;
//		if (objectTree.selectedItems.size == 1) {
//			if (objectTree.selectedItems.get(0) != this.element) { 
//				this.holdingType = EnumGizmoType.NONE;
//				this.element = objectTree.selectedItems.get(0);
//				if (this.toolType > -1) {
//					if (this.element != null) {
//						EnumGizmoTransformType transformType = EnumGizmoTransformType.values()[this.toolType];
//						if (this.element.isTransformable(transformType)) {
//							this.valueVec.set(this.element.getTransform(transformType));
//						}
//					}
//				}
//			}
//		} else this.element = null;
		
		//Its working! Cursor changes while its targeted to the any of gizmos
//		if (GDXUtil.isMouseInArea(x, y, width, height) && this.element != null && this.toolType > -1) {
//			if (this.element.isTransformable(EnumTransform.values()[this.toolType])) {
//				float mx = Interpolation.linear.apply(0, Gdx.graphics.getWidth(), (GDXUtil.getMouseX() - x) / (float)width);
//				float my = Interpolation.linear.apply(0, Gdx.graphics.getHeight(), 1f - ((GDXUtil.getMouseY() - y) / (float)height));
//				Ray ray = screenViewport.camera.getPickRay(mx, my);
//				EnumGizmoType hitType = this.isRayCastedGizmo(ray);
//				if (hitType != EnumGizmoType.NONE) {
//					SpecInterface.setCursor(AppCursor.GRAB);
//				}
//			}
//		}
		
		//on click
//		if (Game.get.getInput().isMouseDown(0, false) && GDXUtil.isMouseInArea(x, y, width, height) && this.element != null && this.toolType > -1) {
//			GizmoTransformType transformType = GizmoTransformType.values()[this.toolType];
//			if (this.element.isTransformable(transformType)) {
//				float mx = Interpolation.linear.apply(0, Gdx.graphics.getWidth(), (GDXUtil.getMouseX() - x) / (float)width);
//				float my = Interpolation.linear.apply(0, Gdx.graphics.getHeight(), 1f - ((GDXUtil.getMouseY() - y) / (float)height));
//				Ray ray = screenViewport.camera.getPickRay(mx, my);
//				GizmoInteractType hitType = this.isRayCastedGizmo(ray);
//				if (hitType != GizmoInteractType.NONE) {
//					this.holdingType = hitType;
//					this.startVec.set(this.valueVec);
//					this.prevMousePosition.set(GDXUtil.getMouseX(), GDXUtil.getMouseY());
//					this.clickOffset = ray.getEndPoint(this.clickOffset.set(this.valueVec), screenViewport.camera.position.dst(this.clickOffset));
//					this.clickOffset.sub(this.startVec);
//				}
//			}
//		}
//		
//		//hold click update
//		if (this.holdingType != GizmoInteractType.NONE && this.element != null) {
//			if (Game.get.getInput().isMouseDown(0, true)) {
//				SpecInterface.setCursor(AppCursor.GRABBING);
//				int mx = GDXUtil.getMouseX();
//				int my = GDXUtil.getMouseY();
//				if (mx < x) { Gdx.input.setCursorPosition(x + width, Gdx.graphics.getHeight() - my); this.prevMousePosition.x = x + width; }
//				else if (mx > x + width) { Gdx.input.setCursorPosition(x, Gdx.graphics.getHeight() - my); this.prevMousePosition.x = x; }
//				if (my < y) { Gdx.input.setCursorPosition(mx, Gdx.graphics.getHeight() - y - height); this.prevMousePosition.y = Gdx.graphics.getHeight() - y - height; }
//				else if (my > y + height) { Gdx.input.setCursorPosition(mx, Gdx.graphics.getHeight() - y); this.prevMousePosition.y = Gdx.graphics.getHeight() - y; }
//				
//				if (mx != this.prevMousePosition.x || my != this.prevMousePosition.y) {
//					switch (this.toolType) {
//						case 0: this.processTranslation(screenViewport.camera, x, y, width, height); break;
//						case 1: this.processRotation(screenViewport.camera, x, y, width, height); break;
//						case 2: this.processScaling(screenViewport.camera, x, y, width, height); break;
//					}
//				}
//				this.prevMousePosition.set(mx, my);
//			} else this.holdingType = GizmoInteractType.NONE;
//		} else if (this.element != null && this.toolType > -1) {
//			this.valueVec.set(this.element.getTransform(GizmoTransformType.values()[this.toolType]));
//		}
		
		this.debugDraw.update();
	}
	
	/** Processing of gizmo with translation mode **/
	private void processTranslation(Camera camera, int x, int y, int width, int height) {
		float mx = Interpolation.linear.apply(0, Gdx.graphics.getWidth(), (GDXUtil.getMouseX() - x) / (float)width);
		float my = Interpolation.linear.apply(0, Gdx.graphics.getHeight(), 1f - ((GDXUtil.getMouseY() - y) / (float)height));
		Ray ray = camera.getPickRay((int)mx, (int)my);
		
//		this.rayTmpVec.set(this.valueVec);
//		this.rayTmpVec = ray.getEndPoint(this.rayTmpVec, camera.position.dst(this.rayTmpVec));
//		this.rayTmpVec.sub(this.clickOffset);
//		
//		if (Game.get.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
//			this.roundVector(this.rayTmpVec, 1f);
//		} else if (Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) {
//			this.roundVector(this.rayTmpVec, 10f);
//		}
//		
//		switch (this.holdingType) {
//			case TRANSLATE_X: this.valueVec.set(this.rayTmpVec.x, this.startVec.y, this.startVec.z); break;
//			case TRANSLATE_Y: this.valueVec.set(this.startVec.x, this.rayTmpVec.y, this.startVec.z); break;
//			case TRANSLATE_Z: this.valueVec.set(this.startVec.x, this.startVec.y, this.rayTmpVec.z); break;
//			default: break;
//		}
//		this.element.getTransform(GizmoTransformType.TRANSLATE).set(this.valueVec);
	}
	
	/** Processing of gizmo with rotation mode **/
	private void processRotation(Camera camera, int x, int y, int width, int height) {
//		float mx = Interpolation.linear.apply(0, Gdx.graphics.getWidth(), (GDXUtil.getMouseX() - x) / (float)width);
//		float my = Interpolation.linear.apply(0, Gdx.graphics.getHeight(), 1f - ((GDXUtil.getMouseY() - y) / (float)height));
//		Ray ray = camera.getPickRay(mx, my);
//		this.rayTmpVec = ray.getEndPoint(this.rayTmpVec.set(this.startVec.setZero()), camera.position.dst(this.startVec));
//		
//		if (Game.get.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
//			this.rayTmpVec.scl(45f);
//			this.rayTmpVec.x = MathUtils.round(this.rayTmpVec.x / 45F) * 45F;
//			this.rayTmpVec.y = MathUtils.round(this.rayTmpVec.y / 45F) * 45F;
//			this.rayTmpVec.z = MathUtils.round(this.rayTmpVec.z / 45F) * 45F;
//		} else if (Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) {
//			this.roundVector(this.rayTmpVec, 10f);
//		} else {
//			this.rayTmpVec.scl(10f);
//		}
//		
//		switch (this.holdingType) {
//			case ROTATE_X: this.valueVec.x = Math.max(Math.min(360F, this.rayTmpVec.x), -360F); break;
//			case ROTATE_Y: this.valueVec.y = Math.max(Math.min(360F, this.rayTmpVec.y), -360F); break;
//			case ROTATE_Z: this.valueVec.z = Math.max(Math.min(360F, this.rayTmpVec.z), -360F); break;
//			default:
//		}
//		this.element.getTransform(GizmoTransformType.ROTATE).set(this.valueVec);
	}
	
	/** Processing of gizmo with scaling mode **/
	private void processScaling(Camera camera, int x, int y, int width, int height) {
//		float mx = Interpolation.linear.apply(0, Gdx.graphics.getWidth(), (GDXUtil.getMouseX() - x) / (float)width);
//		float my = Interpolation.linear.apply(0, Gdx.graphics.getHeight(), 1f - ((GDXUtil.getMouseY() - y) / (float)height));
//		Ray ray = camera.getPickRay(mx, my);
//		this.rayTmpVec = ray.getEndPoint(this.rayTmpVec.set(this.valueVec), camera.position.dst(this.valueVec));
//		if (Game.get.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
//			this.roundVector(this.rayTmpVec, 1f);
//		} else if (Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) {
//			this.roundVector(this.rayTmpVec, 10f);
//		}
//		switch (this.holdingType) {
//			case SCALE_X: this.valueVec.x = Math.max(this.rayTmpVec.x, 0F); break;
//			case SCALE_Y: this.valueVec.y = Math.max(this.rayTmpVec.y, 0F); break;
//			case SCALE_Z: this.valueVec.z = Math.max(this.rayTmpVec.z, 0F); break;
//			default:
//		}
//		this.element.getTransform(GizmoTransformType.SCALE).set(this.valueVec);
	}
	
	private static final ClosestRayResultCallback callback = new ClosestRayResultCallback(new Vector3(), new Vector3());
	/** Returns {@link GizmoInteractType} if param ray intersected one of gizmo arrows **/
	private GizmoInteractType isRayCastedGizmo(Ray ray) {
		if (this.selectedTool != null) {
			callback.setRayFromWorld(ray.origin);
			callback.setRayToWorld(ray.direction.cpy().scl(8192F).add(ray.origin));
			callback.setCollisionObject(null);
		    callback.setClosestHitFraction(1f);
		    this.debugDraw.world.rayTest(ray.origin, ray.direction.cpy().scl(8192F).add(ray.origin), callback);
		    if (callback.hasHit()) {
		    	if (callback.getCollisionObject() != null && callback.getCollisionObject().userData != null && this.selectedTool.ordinal() > -1) {
		    		return GizmoInteractType.values()[(((GizmoHitbox)callback.getCollisionObject().userData).type + 1) + (this.selectedTool.ordinal() + 1) * 3];
		    	}
		    }
		}
		return GizmoInteractType.NONE;
	}
	
	private void roundVector(final Vector3 vector, float precision) {
		vector.x = MathUtils.round(vector.x * precision) / precision;
		vector.y = MathUtils.round(vector.x * precision) / precision;
		vector.z = MathUtils.round(vector.x * precision) / precision;
	}
	
	/** Renders gizmo arrows to {@link #frameBuffer} **/
	public void passRender(Camera camera) {
		if (!this.elements.isEmpty()) {
			this.frameBuffer.capture(0f, 0f, 0f, 0f);
			this.modelBatch.begin(camera);
			this.modelBatch.render(this.xyzShape);
			this.modelBatch.end();
			this.frameBuffer.endCapture();
		} else {
			this.frameBuffer.capture(0f, 0f, 0f, 0f);
			this.frameBuffer.endCapture();
		}
	}
	
	/** **/
	public void updateSelectorMode(ITreeElementSelector<?> selector, GizmoTransformType type) {
		this.elements.clear();
		if (type != null) {
			for (TreeElement element : selector.getIterable()) {
				if (element instanceof ITreeElementGizmos && ((ITreeElementGizmos)element).isTransformSupported(type)) {
					this.elements.add((ITreeElementGizmos)element);
				}
			}
		}
		
		if (!this.elements.isEmpty()) {
			this._gizmoRenderPosition.set(0, 0, 0);
			//vector-sum rescaling positioning
//			int positions = 0;
//			for (ITreeElementGizmos gizmoElement : this.elements) {
//				if (gizmoElement.isTransformSupported(GizmoTransformType.TRANSLATE)) {
//					this._gizmoRenderPosition.add(gizmoElement.getOffsetTransform(GizmoTransformType.TRANSLATE)).add(gizmoElement.getTransform(GizmoTransformType.TRANSLATE));
//					positions++;
//				}
//			}
//			this._gizmoRenderPosition.scl(1.0F / positions);
			
			//per-vector rescaling positioning
			Array<Vector3> vectors = new Array<>();
			this.elements.forEach(gizmoElement -> {
				if (gizmoElement.isTransformSupported(GizmoTransformType.TRANSLATE)) {
					vectors.add(gizmoElement.getOffsetTransform(GizmoTransformType.TRANSLATE).add(gizmoElement.getTransform(GizmoTransformType.TRANSLATE)));
				}
			});
			float scaleModifier = 1.0F / vectors.size;
			for (Vector3 vec : vectors) this._gizmoRenderPosition.add(vec.scl(scaleModifier));
		}
	}
	
	/** Returns rendered gizmo arrows as TextureRegion for further rendering **/
	public TextureRegion getTexture() {
		return this.frameBuffer.getTexture();
	}
}
