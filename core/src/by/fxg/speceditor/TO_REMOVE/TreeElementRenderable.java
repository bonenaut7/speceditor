package by.fxg.speceditor.TO_REMOVE;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class TreeElementRenderable<T extends __TreeElement> {
	public T renderable;
	
	public TreeElementRenderable(T renderable) {
		this.renderable = renderable;
	}
	
	abstract public void update(int x, int y, int width, int height, boolean allowMouse);
	abstract public int render(Batch batch, ShapeDrawer shape, Foster foster, int hOffset, int x, int y, int sx, int sy, boolean allowMouse);
	
	
	public Color drawWheelInput(Color inputColor, int color, ShapeDrawer shape, int x, int y, int width, int height) { return this.drawWheelInput(inputColor, color, shape, x, y, width, height, 0.01f, 0.1f, 1f); }
	public Color drawWheelInput(Color inputColor, int color, ShapeDrawer shape, int x, int y, int width, int height, float ctrlValue, float baseValue, float shiftValue) {
		shape.setColor(GDXUtil.isMouseInArea(x, y, width, height) ? UColor.white : UColor.overlay);
		shape.rectangle(x, y, width, height);
		switch (color) {
			case 0: inputColor.r = this.passMouseWheelInput(inputColor.r, x, y, width, height, ctrlValue, baseValue, shiftValue); break;
			case 1: inputColor.g = this.passMouseWheelInput(inputColor.g, x, y, width, height, ctrlValue, baseValue, shiftValue); break;
			case 2: inputColor.b = this.passMouseWheelInput(inputColor.b, x, y, width, height, ctrlValue, baseValue, shiftValue); break;
			case 3: inputColor.a = this.passMouseWheelInput(inputColor.a, x, y, width, height, ctrlValue, baseValue, shiftValue); break;
		}
		return inputColor;
	}
	
	public Vector3 drawWheelInput(Vector3 inputVec, int axis, ShapeDrawer shape, int x, int y, int width, int height) { return this.drawWheelInput(inputVec, axis, shape, x, y, width, height, 0.01f, 0.1f, 1f); }
	public Vector3 drawWheelInput(Vector3 inputVec, int axis, ShapeDrawer shape, int x, int y, int width, int height, float ctrlValue, float baseValue, float shiftValue) {
		shape.setColor(GDXUtil.isMouseInArea(x, y, width, height) ? UColor.white : UColor.overlay);
		shape.rectangle(x, y, width, height);
		switch (axis) {
			case 0: inputVec.x = this.passMouseWheelInput(inputVec.x, x, y, width, height, ctrlValue, baseValue, shiftValue); break;
			case 1: inputVec.y = this.passMouseWheelInput(inputVec.y, x, y, width, height, ctrlValue, baseValue, shiftValue); break;
			case 2: inputVec.z = this.passMouseWheelInput(inputVec.z, x, y, width, height, ctrlValue, baseValue, shiftValue); break;
		}
		return inputVec;
	}
	
	public Vector2 drawWheelInput(Vector2 inputVec, int axis, ShapeDrawer shape, int x, int y, int width, int height) { return this.drawWheelInput(inputVec, axis, shape, x, y, width, height, 0.01f, 0.1f, 1f); }
	public Vector2 drawWheelInput(Vector2 inputVec, int axis, ShapeDrawer shape, int x, int y, int width, int height, float ctrlValue, float baseValue, float shiftValue) {
		shape.setColor(GDXUtil.isMouseInArea(x, y, width, height) ? UColor.white : UColor.overlay);
		shape.rectangle(x, y, width, height);
		switch (axis) {
			case 0: inputVec.x = this.passMouseWheelInput(inputVec.x, x, y, width, height, ctrlValue, baseValue, shiftValue); break;
			case 1: inputVec.y = this.passMouseWheelInput(inputVec.y, x, y, width, height, ctrlValue, baseValue, shiftValue); break;
		}
		return inputVec;
	}
	
	public float drawWheelInput(float input, ShapeDrawer shape, int x, int y, int width, int height) { return this.drawWheelInput(input, shape, x, y, width, height, 0.01f, 0.1f, 1f); }
	public float drawWheelInput(float input, ShapeDrawer shape, int x, int y, int width, int height, float ctrlValue, float baseValue, float shiftValue) {
		shape.setColor(GDXUtil.isMouseInArea(x, y, width, height) ? UColor.white : UColor.overlay);
		shape.rectangle(x, y, width, height);
		return this.passMouseWheelInput(input, x, y, width, height, ctrlValue, baseValue, shiftValue);
	}
	
	public float passMouseWheelInput(float input, int x, int y, int width, int height) { return this.passMouseWheelInput(input, x, y, width, height, 0.01f, 0.1f, 1f); }
	public float passMouseWheelInput(float input, int x, int y, int width, int height, float ctrlValue, float baseValue, float shiftValue) {
		float tempVal = Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) ? ctrlValue : Game.get.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) ? shiftValue : baseValue;
		if (GDXUtil.isMouseInArea(x, y, width, height) && (Game.get.getInput().isMouseScrolled(true) || Game.get.getInput().isMouseScrolled(false))) {
			input += Game.get.getInput().isMouseScrolled(true) ? -tempVal : tempVal;
			input = MathUtils.round(input * 100f) / 100f;
		}
		return input;
	}
}
