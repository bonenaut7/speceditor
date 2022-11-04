package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;

import by.fxg.pilesos.decals.BaseDecal;
import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.TreeElement;

public class ElementLight extends TreeElement implements ITreeElementGizmos {
	public ElementLightType type;
	private BaseLight<?> light;
	public BaseDecal _viewportDecal;
	
	public ElementLight() { this("New light"); }
	public ElementLight(String name) {
		this.displayName = name;
		this.light = new PointLight().setColor(1, 1, 1, 1).setIntensity(5.0F);
		this.type = ElementLightType.POINT;
		
		this._viewportDecal = new BaseDecal().setBillboard(true).setDecal(DefaultResources.INSTANCE.standardDecal);
	}
	
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: {
				switch (this.type) {
					case POINT: this._viewportDecal.getDecal().setPosition(this.getLight(PointLight.class).position); return this.getLight(PointLight.class).position;
					case SPOT: this._viewportDecal.getDecal().setPosition(this.getLight(PointLight.class).position); return this.getLight(SpotLight.class).position;
				}
			} break;
			case ROTATE: {
				if (this.type == ElementLightType.SPOT) return this.getLight(SpotLight.class).direction;
			} break;
		}
		return gizmoVector.set(0, 0, 0);
	}
	
	public <T extends BaseLight<?>> T getLight(Class<T> clazz) {
		return (T)this.light;
	}
	
	public void setLight(BaseLight<?> light) {
		this.light = light;
	}
	
	public Sprite getObjectTreeSprite() {
		return DefaultResources.INSTANCE.sprites.get("icons/light");
	}
	
	public boolean isTransformSupported(GizmoTransformType transformType) {
		if (transformType == GizmoTransformType.ROTATE) return this.type == ElementLightType.SPOT;
		return transformType == GizmoTransformType.TRANSLATE;
	}
	
	public static enum ElementLightType {
		POINT,
		SPOT;
	}
}
