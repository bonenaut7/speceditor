package by.fxg.speceditor.hc.elementlist.elements;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.decals.BaseDecal;
import by.fxg.pilesos.specformat.graph.SpecLight;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.hc.elementlist.EnumTransform;
import by.fxg.speceditor.hc.elementlist.IConvertable;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.TreeElement;
import by.fxg.speceditor.hc.elementlist.TreeElementRenderable;
import by.fxg.speceditor.hc.elementlist.renderables.TERLight;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public class ElementLight extends TreeElement implements IConvertable<SpecLight> {
	private TreeElementRenderable<ElementLight> renderable;
	
	public PointLight light = new PointLight();
	public BaseDecal editorDecal = new BaseDecal();
	
	public ElementLight() { this("New light"); }
	public ElementLight(String name) {
		this.name = name;
		this.isVisible = true;
		
		this.light.color.set(1, 1, 1, 1);
		this.editorDecal.setBillboard(true);
		this.renderable = new TERLight(this);
	}
	
	public void onInteract(PMObjectExplorer list, boolean hold, boolean iconTouch) {
		if (!hold) {
			if (iconTouch) {
				if (Game.get.getTick() - this.lastClickTime < 25L) {
					this.lastClickTime = 0;
					this.isVisible = !this.isVisible;
				} else this.lastClickTime = Game.get.getTick();
			} else {
				if (Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) && list.isElementSelected(this)) list.elementUnselect(this);
				else list.elementSelect(this);
			}
		}
	}
	
	public void addDropdownParameters(PMObjectExplorer pmoe, Array<TreeElement> selected, Array<UDAElement> array) {
		if (selected.size == 1) {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide" : "Show"));
		} else {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide all" : "Show all"));
		}
		super.addDefaultDropdownParameters(pmoe, selected, array);
	}
	
	public void processDropdown(PMObjectExplorer pmoe, Array<TreeElement> selected, String key) {
		switch (key) {
			case "basic.hide": {
				for (TreeElement element : selected) element.setVisible(false);
			} return;
			case "basic.show": {
				for (TreeElement element : selected) element.setVisible(true);
			} return;
		}
	}
	
	public Vector3 getTransform(EnumTransform transformType) {
		if (this.editorDecal.getDecal() != null) this.editorDecal.getDecal().setPosition(this.light.position);
		switch(transformType) {
			case TRANSLATE: return this.light.position;
			default: return localTempVector.set(0, 0, 0);
		}
	}
	
	public boolean isTransformable(EnumTransform transformType) { return transformType == EnumTransform.TRANSLATE; }
	public Sprite getSprite() { return Game.storage.sprites.get(String.format("obj.light.%b", this.isVisible)); }
	public TreeElementRenderable<?> getRenderable() { return this.renderable; }
	public TreeElement clone() {
		ElementLight element = new ElementLight(this.name + ".clone");
		element.isVisible = this.isVisible;
		element.light.set(this.light);
		return element;
	}

	public SpecLight convert() {
		SpecLight light = new SpecLight();
		light.name = this.name;
		light.lightType = 0;
		light.color = new Color(this.light.color);
		light.position = new Vector3(this.light.position);
		light.intensity = this.light.intensity;
		return light;
	}
}
