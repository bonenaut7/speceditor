package by.fxg.speceditor.hc.elementlist.elements;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.hc.elementlist.EnumTransform;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.TreeElement;
import by.fxg.speceditor.hc.elementlist.TreeElementRenderable;
import by.fxg.speceditor.hc.elementlist.renderables.TERPoint;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public class ElementPoint extends TreeElement {
	private TreeElementRenderable<ElementPoint> renderable;
	public ElementPointArray parent = null;

	protected Vector3 position = new Vector3();
	
	public ElementPoint() { this("New point"); }
	public ElementPoint(String name) {
		this.name = name;
		this.renderable = new TERPoint(this);
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
		switch(transformType) {
			case TRANSLATE: return this.position;
			default: return localTempVector.set(0, 0, 0);
		}
	}
	
	public Vector3 getOffsetTransform(EnumTransform transformType) {
		if (this.parent != null) {
			switch (transformType) {
				case TRANSLATE: return localTempVector.set(this.parent.getTransform(EnumTransform.TRANSLATE));
				default:
			}
		}
		return localTempVector.set(0, 0, 0);
	}
	
	public boolean isTransformable(EnumTransform transformType) { return transformType == EnumTransform.TRANSLATE; }
	public Sprite getSprite() { return Game.storage.sprites.get(String.format("obj.point.%b", this.isVisible)); }
	public TreeElementRenderable<?> getRenderable() { return this.renderable; }
	public TreeElement clone() {
		ElementPoint element = new ElementPoint(this.name + ".clone");
		element.isVisible = this.isVisible;
		element.position.set(this.position);
		if (this.parent != null) element.parent = this.parent;
		return element;
	}
}
