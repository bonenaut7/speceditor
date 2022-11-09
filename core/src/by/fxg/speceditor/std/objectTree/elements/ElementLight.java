package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;

import by.fxg.pilesos.decals.BaseDecal;
import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.render.DebugDraw3D;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;

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
	
	private ElementLight(ElementLight copy) {
		this.displayName = copy.displayName;
		this.visible = copy.visible;
		this.type = copy.type;
		switch (this.type) {
			case POINT: this.light = new PointLight().set(copy.getLight(PointLight.class)); break;
			case SPOT: this.light = new SpotLight().set(copy.getLight(SpotLight.class)); break;
		}
		this._viewportDecal = new BaseDecal().setBillboard(true).setDecal(DefaultResources.INSTANCE.standardDecal);
	}
	
	public void draw(SpecObjectTree objectTree, DebugDraw3D draw) {
		if (objectTree.elementSelector.isElementSelected(this)) {
			switch (this.type) {
				case POINT: {
					PointLight pointLight = this.getLight(PointLight.class);
					draw.drawer.drawSphere(pointLight.position, pointLight.intensity / 3.75F, UColor.hitboxSelected);
				} break;
				case SPOT: {
					SpotLight spotLight = this.getLight(SpotLight.class);
					draw.drawer.drawSphere(spotLight.position, spotLight.intensity, UColor.hitboxSelected);
				} break;
			}
		}
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
	
	public TreeElement cloneElement() {
		return new ElementLight(this);
	}
	
	public static enum ElementLightType {
		POINT,
		SPOT;
	}
}
