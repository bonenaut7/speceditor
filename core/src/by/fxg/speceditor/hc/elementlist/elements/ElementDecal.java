package by.fxg.speceditor.hc.elementlist.elements;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.SpriteStack;
import by.fxg.pilesos.specformat.graph.SpecDecal;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.hc.elementlist.EnumTransform;
import by.fxg.speceditor.hc.elementlist.IConvertable;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.TreeElement;
import by.fxg.speceditor.hc.elementlist.TreeElementRenderable;
import by.fxg.speceditor.hc.elementlist.renderables.TERDecal;
import by.fxg.speceditor.project.Project;
import by.fxg.speceditor.tools.g3d.EditDecal;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public class ElementDecal extends TreeElement implements IConvertable<SpecDecal> {
	private TreeElementRenderable<ElementDecal> renderable;
	
	public EditDecal decal = new EditDecal();
	
	public ElementDecal() { this("New decal"); }
	public ElementDecal(String name) {
		this.name = name;
		this.isVisible = true;
		this.renderable = new TERDecal(this);
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
			case TRANSLATE: return this.decal.position;
			case ROTATE: return this.decal.rotation;
			default: return localTempVector.set(0, 0, 0);
		}
	}
	
	public void setLocalHandle(String path) {
		this.decal.localDecalHandle = path;
		((TERDecal)this.renderable).input[9].setText(path);
	}
	
	public boolean isTransformable(EnumTransform transformType) { return transformType != EnumTransform.SCALE; }
	public Sprite getSprite() { return Game.storage.sprites.get(String.format("obj.decal.%b", this.isVisible)); }
	public TreeElementRenderable<?> getRenderable() { return this.renderable; }
	public TreeElement clone() {
		ElementDecal element = new ElementDecal(this.name + ".clone");
		element.isVisible = this.isVisible;
		element.decal.localDecalHandle = this.decal.localDecalHandle;
		element.decal.decalHandle = this.decal.decalHandle;
		element.decal.setBillboard(this.decal.isBillboard());
		element.decal.position.set(this.decal.position);
		element.decal.rotation.set(this.decal.rotation);
		element.decal.scale.set(this.decal.scale);
		if (this.decal.decalHandle != null) element.decal.setDecal(Decal.newDecal(new TextureRegion(SpriteStack.getTexture(this.decal.decalHandle)), true), this.decal.decalHandle);
		return element;
	}

	public SpecDecal convert() {
		SpecDecal decal = new SpecDecal();
		decal.name = this.name;
		decal.isBillboard = this.decal.isBillboard();
		decal.texturePath = this.decal.localDecalHandle.length() == 0 ? (this.decal.decalHandle != null ? this.decal.decalHandle.path().substring(Project.instance.projectFolder.path().length() + 1) : null) : this.decal.localDecalHandle;
		decal.position = new Vector3(this.decal.position);
		decal.rotation = new Vector3(this.decal.rotation);
		decal.scale = new Vector2(this.decal.scale);
		return decal;
	}
}
