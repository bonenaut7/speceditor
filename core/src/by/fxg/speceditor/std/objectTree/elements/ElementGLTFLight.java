package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;

import by.fxg.pilesos.decals.SmartDecal;
import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.objectTree.elements.ElementLight.ElementLightType;
import by.fxg.speceditor.utils.Utils;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.lights.SpotLightEx;

public class ElementGLTFLight extends TreeElement implements ITreeElementGizmos {
	public ElementLightType type;
	private BaseLight<?> light;
	public SmartDecal _viewportDecal;
	
	public ElementGLTFLight() { this("New light"); }
	public ElementGLTFLight(String name) {
		this.displayName = name;
		this.light = new PointLightEx().setColor(1, 1, 1, 1).setIntensity(5.0F);
		this.getLight(PointLightEx.class).range = 10.0F;
		this.type = ElementLightType.POINT;
		
		this._viewportDecal = new SmartDecal().setBillboard(true);
		this.setVisible(true);
	}
	
	private ElementGLTFLight(ElementGLTFLight copy) {
		this.displayName = copy.displayName;
		this.visible = copy.visible;
		this.type = copy.type;
		switch (this.type) {
			case POINT: this.light = new PointLightEx().set(copy.getLight(PointLightEx.class)); break;
			case SPOT: this.light = new SpotLightEx().set(copy.getLight(SpotLightEx.class)); break;
		}
		this._viewportDecal = new SmartDecal().setBillboard(true);
		this.setVisible(true);
	}
	
	public <T extends BaseLight<?>> T getLight(Class<T> clazz) {
		return (T)this.light;
	}
	
	public void setLight(BaseLight<?> light) {
		this.light = light;
	}
	
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: {
				switch (this.type) {
					case POINT: this._viewportDecal.getDecal().setPosition(this.getLight(PointLightEx.class).position); return this.getLight(PointLightEx.class).position;
					case SPOT: this._viewportDecal.getDecal().setPosition(this.getLight(SpotLightEx.class).position); return this.getLight(SpotLightEx.class).position;
				}
			} break;
			case ROTATE: {
				if (this.type == ElementLightType.SPOT) return this.getLight(SpotLight.class).direction;
			} break;
		}
		return Vector3.Zero;
	}
	
	public Sprite getObjectTreeSprite() {
		return DefaultResources.INSTANCE.sprites.get("icons/light");
	}
	
	public boolean isTransformSupported(GizmoTransformType transformType) {
		if (transformType == GizmoTransformType.ROTATE) return this.type == ElementLightType.SPOT;
		return transformType == GizmoTransformType.TRANSLATE;
	}
	
	public TreeElement cloneElement() {
		return new ElementGLTFLight(this);
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
		this._viewportDecal.setDecal(Decal.newDecal(SpriteStack.getTextureRegion(Utils.format("defaults/lightdecal_", visible, ".png"))));
		this._viewportDecal.getDecal().setScale(0.0015f, 0.0015f);
	}
}
