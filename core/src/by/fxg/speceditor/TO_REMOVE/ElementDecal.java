package by.fxg.speceditor.TO_REMOVE;

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
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.std.g3d.EditDecal;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.objecttree.SpecObjectTree;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public class ElementDecal extends __TreeElement implements IConvertable<SpecDecal> {
	private TreeElementRenderable<ElementDecal> renderable;
	
	public EditDecal decal = new EditDecal();
	
	public ElementDecal() { this("New decal"); }
	public ElementDecal(String name) {
		this.name = name;
		this.isVisible = true;
		this.renderable = new TERDecal(this);
	}
	
	public void onInteract(SpecObjectTree list, boolean hold, boolean iconTouch) {
		if (!hold) {
			if (iconTouch) {
				if (Game.get.getTick() - this.lastClickTime < 25L) {
					this.lastClickTime = 0;
					this.isVisible = !this.isVisible;
				} else this.lastClickTime = Game.get.getTick();
			} else {
				if (Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) && list.isElementSelected(this)) list.deselectElement(this);
				else list.selectElement(this);
			}
		}
	}
	
	public void addDropdownParameters(SpecObjectTree pmoe, Array<__TreeElement> selected, Array<UDAElement> array) {
		if (selected.size == 1) {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide" : "Show"));
		} else {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide all" : "Show all"));
		}
		super.addDefaultDropdownParameters(pmoe, selected, array);
	}
	
	public void processDropdown(SpecObjectTree pmoe, Array<__TreeElement> selected, String key) {
		switch (key) {
			case "basic.hide": {
				for (__TreeElement element : selected) element.setVisible(false);
			} return;
			case "basic.show": {
				for (__TreeElement element : selected) element.setVisible(true);
			} return;
		}
	}
	
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: return this.decal.position;
			case ROTATE: return this.decal.rotation;
			default: return localTempVector.set(0, 0, 0);
		}
	}
	
	public boolean isTransformable(GizmoTransformType transformType) { return transformType != GizmoTransformType.SCALE; }
	public Sprite getSprite() { return Game.storage.sprites.get(String.format("obj.decal.%b", this.isVisible)); }
	public TreeElementRenderable<?> getRenderable() { return this.renderable; }
	public __TreeElement clone() {
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
		decal.texturePath = this.decal.localDecalHandle.length() == 0 ? (this.decal.decalHandle != null ? this.decal.decalHandle.path().substring(ProjectManager.currentProject.getProjectFolder().path().length() + 1) : null) : this.decal.localDecalHandle;
		decal.position = new Vector3(this.decal.position);
		decal.rotation = new Vector3(this.decal.rotation);
		decal.scale = new Vector2(this.decal.scale);
		return decal;
	}
}
