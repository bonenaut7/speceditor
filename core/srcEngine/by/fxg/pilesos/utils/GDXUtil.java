package by.fxg.pilesos.utils;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import by.fxg.pilesos.Pilesos;

public class GDXUtil {
	public static int getMouseX() {
		return Gdx.input.getX();
	}
	
	public static int getMouseY() {
		return Pilesos.getApp().height + -Gdx.input.getY();
	}
	
	public static boolean isMouseIn(float x, float y, float x1, float y1) {
		return x <= Gdx.input.getX() && Gdx.input.getX() <= x1 && y <= (Pilesos.getApp().height + -Gdx.input.getY()) && (Pilesos.getApp().height + -Gdx.input.getY()) <= y1;
	}
	
	public static boolean isMouseInArea(float x, float y, float w, float h) {
		return isMouseIn(x, y, x + w, y + h);
	}
	
	public static boolean isMouseInArea(Rectangle rectangle) {
		return isMouseInArea(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}
	
	public static Random rand = new Random();
	public static boolean getRandom(double chance) {
		rand = new Random();
		return 100.0D * rand.nextDouble() - (100.0D - chance) > 0;
	}
	
	public static String format(Object... objects) {
		StringBuilder builder = new StringBuilder();
		for (Object object : objects) builder.append(object);
		return builder.toString();
	}
	
	public static String pathExtension(String path) {
		int dotIndex = path.lastIndexOf('.');
		if (dotIndex == -1) return "";
		return path.substring(dotIndex + 1);
	}
}
