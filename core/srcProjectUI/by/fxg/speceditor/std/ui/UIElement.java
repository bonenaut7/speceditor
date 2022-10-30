package by.fxg.speceditor.std.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.GInputProcessor;
import by.fxg.speceditor.SpecEditor;

public abstract class UIElement {
	protected int x, y, width, height;
	
	public boolean isMouseOver(int x, int y, int width, int height) {
		return GDXUtil.isMouseInArea(x, y, width, height) && SpecInterface.isFocused(this);
	}
	
	public boolean isMouseOver() {
		return this.isMouseOver(this.x, this.y, this.width, this.height);
	}
	
	public GInputProcessor getInput() {
		return SpecEditor.get.getInput();
	}
	
	public static void _convertColorToText(Color color, STDInputField fieldR, STDInputField fieldG, STDInputField fieldB, STDInputField fieldA, boolean withPointer) {
		if (withPointer) {
			fieldR.setTextWithPointer(String.valueOf(color.r)).dropOffset();
			fieldG.setTextWithPointer(String.valueOf(color.g)).dropOffset();
			fieldB.setTextWithPointer(String.valueOf(color.b)).dropOffset();
			fieldA.setTextWithPointer(String.valueOf(color.a)).dropOffset();	
		} else {
			fieldR.setText(String.valueOf(color.r));
			fieldG.setText(String.valueOf(color.g));
			fieldB.setText(String.valueOf(color.b));
			fieldA.setText(String.valueOf(color.a));
		}
	}
	
	public static void _convertVector3ToText(Vector3 vec, STDInputField fieldX, STDInputField fieldY, STDInputField fieldZ, boolean withPointer) {
		if (withPointer) {
			fieldX.setText(String.valueOf(vec.x)).dropOffset();
			fieldY.setText(String.valueOf(vec.y)).dropOffset();
			fieldZ.setText(String.valueOf(vec.z)).dropOffset();
		} else {
			fieldX.setText(String.valueOf(vec.x));
			fieldY.setText(String.valueOf(vec.y));
			fieldZ.setText(String.valueOf(vec.z));
		}
	}

	public static void _convertVector2ToText(Vector2 vec, STDInputField fieldX, STDInputField fieldY, boolean withPointer) {
		if (withPointer) {
			fieldX.setText(String.valueOf(vec.x)).dropOffset();
			fieldY.setText(String.valueOf(vec.y)).dropOffset();
		} else {
			fieldX.setText(String.valueOf(vec.x));
			fieldY.setText(String.valueOf(vec.y));
		}
	}
	
	public static void _convertTextToColor(Color color, STDInputField fieldR, STDInputField fieldG, STDInputField fieldB, STDInputField fieldA) {
		color.set(fieldR.getTextAsNumber(color.r), fieldG.getTextAsNumber(color.g), fieldB.getTextAsNumber(color.b), fieldA.getTextAsNumber(color.a));
	}
	
	public static void _convertTextToVector3(Vector3 vec, STDInputField fieldX, STDInputField fieldY, STDInputField fieldZ) {
		vec.set(fieldX.getTextAsNumber(vec.x), fieldY.getTextAsNumber(vec.y), fieldZ.getTextAsNumber(vec.z));
	}

	public static void _convertTextToVector2(Vector2 vec, STDInputField fieldX, STDInputField fieldY) {
		vec.set(fieldX.getTextAsNumber(vec.x), fieldY.getTextAsNumber(vec.y));
	}
}
