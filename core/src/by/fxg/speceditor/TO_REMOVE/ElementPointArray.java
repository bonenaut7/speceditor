package by.fxg.speceditor.TO_REMOVE;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.specformat.graph.SpecPointArray;
import by.fxg.pilesos.specformat.graph.SpecPointArray.Flags;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.objecttree.ElementStack;
import by.fxg.speceditor.std.objecttree.SpecObjectTree;
import by.fxg.speceditor.std.objecttree.TreeElementRenderable;
import by.fxg.speceditor.std.render.DebugDraw3D;
import by.fxg.speceditor.std.render.DebugDraw3D.IDebugDraw;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;

public class ElementPointArray extends __TreeElement implements IDebugDraw, IConvertable<SpecPointArray> {
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

	public void onInteract(SpecObjectTree list, boolean hold, boolean iconTouch) {
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
					list.deselectElement(this);
				} else {
					if (Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) && list.isElementSelected(this)) list.deselectElement(this);
					else list.selectElement(this);
					this.lastClickTime = Game.get.getTick();
				}
			}
		}
	}
	
	public void addDropdownParameters(SpecObjectTree pmoe, Array<__TreeElement> selected, Array<UDAElement> array) {
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
	
	public void processDropdown(SpecObjectTree pmoe, Array<__TreeElement> selected, String key) {
		switch (key) {
			case "basic.hide": {
				for (__TreeElement element : selected) element.setVisible(false);
			} return;
			case "basic.show": {
				for (__TreeElement element : selected) element.setVisible(true);
			} return;
			case "basic.collapse": {
				for (__TreeElement element : selected) element.setOpened(false);
			} return;
			case "basic.open": {
				for (__TreeElement element : selected) element.setOpened(true);
			} return;
			
			case "pointarray.add.point": { ElementPoint point = new ElementPoint(); point.parent = this; this.elementStack.add(point); } return;
		}
	}
	
	public void draw(SpecObjectTree pmoe, DebugDraw3D draw) {
		boolean arraySelected = pmoe.selectedItems.contains(this, true);
		tmpPoints.size = 0;
		for (__TreeElement element : this.elementStack.getElements()) {
			if (element instanceof ElementPoint) {
				tmpPoints.add((ElementPoint)element);
				tmpMatrix.setToTranslation(this.position).translate(element.getTransform(GizmoTransformType.TRANSLATE));
				draw.drawer.drawCone(0.25f, 0.5f, 1, tmpMatrix, pmoe.selectedItems.contains(element, true) || arraySelected ? UColor.pointSelected : UColor.point);
			}
		}
		
		if (Flags.hasFlag(this.flags, Flags.MODE_LINE)) {
			for (int i = 0; i + 1 != tmpPoints.size; i++) {
				tmpVector0.set(this.position).add(tmpPoints.get(i).getTransform(GizmoTransformType.TRANSLATE));
				tmpVector1.set(this.position).add(tmpPoints.get(i + 1).getTransform(GizmoTransformType.TRANSLATE));
				//draw.drawer.drawLine(tmpVector0, tmpVector1, pmoe.selectedItems.contains(tmpPoints.get(i), true) ? UColor.pointSelected : UColor.point, pmoe.selectedItems.contains(tmpPoints.get(i + 1), true) ? UColor.pointSelected : UColor.point);
				draw.drawer.drawLine(tmpVector0, tmpVector1, arraySelected ? UColor.pointSelected : UColor.point);
			}
		} else if (Flags.hasFlag(this.flags, Flags.MODE_LOOP)) {
			for (int i = 0; i + 1 != tmpPoints.size; i++) {
				tmpVector0.set(this.position).add(tmpPoints.get(i).getTransform(GizmoTransformType.TRANSLATE));
				tmpVector1.set(this.position).add(tmpPoints.get(i + 1).getTransform(GizmoTransformType.TRANSLATE));
				draw.drawer.drawLine(tmpVector0, tmpVector1, arraySelected ? UColor.pointSelected : UColor.point);
			}
			tmpVector0.set(this.position).add(tmpPoints.get(tmpPoints.size - 1).getTransform(GizmoTransformType.TRANSLATE));
			tmpVector1.set(this.position).add(tmpPoints.get(0).getTransform(GizmoTransformType.TRANSLATE));
			draw.drawer.drawLine(tmpVector0, tmpVector1, arraySelected ? UColor.pointSelected : UColor.point);
		}
	}
	
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: return this.position;
			default: return localTempVector.set(0, 0, 0);
		}
	}
	
	public boolean isTransformable(GizmoTransformType transformType) { return transformType == GizmoTransformType.TRANSLATE; }
	public Sprite getSprite() { return Game.storage.sprites.get(String.format("obj.pointarray.%b", this.isVisible)); }
	public TreeElementRenderable<?> getRenderable() { return this.renderable; }
	public boolean hasStack() { return true; }
	public boolean stackAccepting(__TreeElement element) { return element instanceof ElementPoint; }
	public ElementStack getStack() { return this.elementStack; }
	
	public __TreeElement clone() {
		ElementPointArray element = new ElementPointArray(this.name + ".clone");
		element.isVisible = this.isVisible;
		element.isOpened = this.isOpened;
		element.flags = this.flags;
		element.position.set(this.position);
		for (__TreeElement element$ : this.elementStack.getElements()) {
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
		for (__TreeElement element : this.elementStack.getElements()) {
			if (element instanceof ElementPoint) points.add(new Vector3(this.position).add(((ElementPoint)element).position));
		}
		pointArray.points = points.toArray(Vector3.class);
		return pointArray;
	}
}
