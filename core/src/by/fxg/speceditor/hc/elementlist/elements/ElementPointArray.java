package by.fxg.speceditor.hc.elementlist.elements;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.specformat.graph.SpecPointArray;
import by.fxg.pilesos.specformat.graph.SpecPointArray.Flags;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.hc.elementlist.ElementStack;
import by.fxg.speceditor.hc.elementlist.EnumTransform;
import by.fxg.speceditor.hc.elementlist.IConvertable;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.TreeElement;
import by.fxg.speceditor.hc.elementlist.TreeElementRenderable;
import by.fxg.speceditor.hc.elementlist.renderables.TERPointArray;
import by.fxg.speceditor.tools.debugdraw.DebugDraw3D;
import by.fxg.speceditor.tools.debugdraw.IDebugDraw;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public class ElementPointArray extends TreeElement implements IDebugDraw, IConvertable<SpecPointArray> {
	private static Vector3 tmpVector0 = new Vector3(), tmpVector1 = new Vector3();
	private static Matrix4 tmpMatrix = new Matrix4();
	private static Array<ElementPoint> tmpPoints = new Array<>();
	
	private TreeElementRenderable<ElementPointArray> renderable;
	private ElementStack elementStack;
	
	public long flags;
	private Vector3 position = new Vector3();
	
	public ElementPointArray() { this("New point array"); }
	public ElementPointArray(String name) {
		this.name = name;
		this.elementStack = new ElementStack(this);
		this.renderable = new TERPointArray(this);
	}

	public void onInteract(PMObjectExplorer list, boolean hold, boolean iconTouch) {
		if (!hold) {
			if (iconTouch) {
				if (Game.get.getTick() - this.lastClickTime < 25L) {
					this.lastClickTime = 0;
					this.isVisible = !this.isVisible;
				} else this.lastClickTime = Game.get.getTick();
			} else {
				if (Game.get.getTick() - this.lastClickTime < 25L) {
					this.lastClickTime = 0;
					this.isOpened = !this.isOpened;
					list.elementUnselect(this);
				} else {
					if (Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) && list.isElementSelected(this)) list.elementUnselect(this);
					else list.elementSelect(this);
					this.lastClickTime = Game.get.getTick();
				}
			}
		}
	}
	
	public void addDropdownParameters(PMObjectExplorer pmoe, Array<TreeElement> selected, Array<UDAElement> array) {
		if (selected.size == 1) {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide" : "Show"));
			array.add(new UDAElement(this.isOpened ? "basic.collapse" : "basic.open", this.isVisible ? "Collapse" : "Open"));	
			super.addDefaultDropdownParameters(pmoe, selected, array);
			
			array.add(new UDAElement());
			array.add(new UDAElement("pointarray.add.point", "Create point"));
		} else {
			array.add(new UDAElement(this.isVisible ? "basic.hide" : "basic.show", this.isVisible ? "Hide all" : "Show all"));
			array.add(new UDAElement(this.isOpened ? "basic.collapse" : "basic.open", this.isVisible ? "Collapse all" : "Open all"));
		}
	}
	
	public void processDropdown(PMObjectExplorer pmoe, Array<TreeElement> selected, String key) {
		switch (key) {
			case "basic.hide": {
				for (TreeElement element : selected) element.setVisible(false);
			} return;
			case "basic.show": {
				for (TreeElement element : selected) element.setVisible(true);
			} return;
			case "basic.collapse": {
				for (TreeElement element : selected) element.setOpened(false);
			} return;
			case "basic.open": {
				for (TreeElement element : selected) element.setOpened(true);
			} return;
			
			case "pointarray.add.point": { ElementPoint point = new ElementPoint(); point.parent = this; this.elementStack.add(point); } return;
		}
	}
	
	public void draw(PMObjectExplorer pmoe, DebugDraw3D draw) {
		boolean arraySelected = pmoe.selectedItems.contains(this, true);
		tmpPoints.size = 0;
		for (TreeElement element : this.elementStack.getItems()) {
			if (element instanceof ElementPoint) {
				tmpPoints.add((ElementPoint)element);
				tmpMatrix.setToTranslation(this.position).translate(element.getTransform(EnumTransform.TRANSLATE));
				draw.drawer.drawCone(0.25f, 0.5f, 1, tmpMatrix, pmoe.selectedItems.contains(element, true) || arraySelected ? UColor.pointSelected : UColor.point);
			}
		}
		
		if (Flags.hasFlag(this.flags, Flags.MODE_LINE)) {
			for (int i = 0; i + 1 != tmpPoints.size; i++) {
				tmpVector0.set(this.position).add(tmpPoints.get(i).getTransform(EnumTransform.TRANSLATE));
				tmpVector1.set(this.position).add(tmpPoints.get(i + 1).getTransform(EnumTransform.TRANSLATE));
				//draw.drawer.drawLine(tmpVector0, tmpVector1, pmoe.selectedItems.contains(tmpPoints.get(i), true) ? UColor.pointSelected : UColor.point, pmoe.selectedItems.contains(tmpPoints.get(i + 1), true) ? UColor.pointSelected : UColor.point);
				draw.drawer.drawLine(tmpVector0, tmpVector1, arraySelected ? UColor.pointSelected : UColor.point);
			}
		} else if (Flags.hasFlag(this.flags, Flags.MODE_LOOP)) {
			for (int i = 0; i + 1 != tmpPoints.size; i++) {
				tmpVector0.set(this.position).add(tmpPoints.get(i).getTransform(EnumTransform.TRANSLATE));
				tmpVector1.set(this.position).add(tmpPoints.get(i + 1).getTransform(EnumTransform.TRANSLATE));
				draw.drawer.drawLine(tmpVector0, tmpVector1, arraySelected ? UColor.pointSelected : UColor.point);
			}
			tmpVector0.set(this.position).add(tmpPoints.get(tmpPoints.size - 1).getTransform(EnumTransform.TRANSLATE));
			tmpVector1.set(this.position).add(tmpPoints.get(0).getTransform(EnumTransform.TRANSLATE));
			draw.drawer.drawLine(tmpVector0, tmpVector1, arraySelected ? UColor.pointSelected : UColor.point);
		}
	}
	
	public Vector3 getTransform(EnumTransform transformType) {
		switch(transformType) {
			case TRANSLATE: return this.position;
			default: return localTempVector.set(0, 0, 0);
		}
	}
	
	public boolean isTransformable(EnumTransform transformType) { return transformType == EnumTransform.TRANSLATE; }
	public Sprite getSprite() { return Game.storage.sprites.get(String.format("obj.pointarray.%b", this.isVisible)); }
	public TreeElementRenderable<?> getRenderable() { return this.renderable; }
	public boolean hasStack() { return true; }
	public boolean stackAccepting(TreeElement element) { return element instanceof ElementPoint; }
	public ElementStack getStack() { return this.elementStack; }
	
	public TreeElement clone() {
		ElementPointArray element = new ElementPointArray(this.name + ".clone");
		element.isVisible = this.isVisible;
		element.isOpened = this.isOpened;
		element.flags = this.flags;
		element.position.set(this.position);
		for (TreeElement element$ : this.elementStack.getItems()) {
			if (element$ instanceof ElementPoint) {
				ElementPoint point = (ElementPoint)element$.clone();
				point.parent = element;
				element.elementStack.add(point);
			}
		}
		return element;
	}

	public SpecPointArray convert() {
		SpecPointArray pointArray = new SpecPointArray();
		pointArray.name = this.name;
		pointArray.flags = this.flags;
		Array<Vector3> points = new Array<>();
		for (TreeElement element : this.elementStack.getItems()) {
			if (element instanceof ElementPoint) points.add(new Vector3(this.position).add(((ElementPoint)element).position));
		}
		pointArray.points = points.toArray(Vector3.class);
		return pointArray;
	}
}
