package by.fxg.speceditor.std.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.std.objectTree.elements.ElementLight;

public interface IRendererType {
	void addAttribute(Attribute attribute);
	void removeAttribute(Attribute attribute);
	
	void add(Object element);
	void addLight(ElementLight light, boolean selected);
	
	void update();
	void passRender();
	TextureRegion getTexture();
	
	void clear(boolean partially);
	
	public static class ViewportSettings {
		public static Vector3 cameraSettings = new Vector3(); 				//FOV, far, near
		public static Color bufferColor = new Color(); 						//RGBA
		public static Array<Attribute> viewportAttributes = new Array<>(); 	//Environment attributes
		public static boolean viewportHitboxDepth = true;
		public static float viewportHitboxWidth = 1f;
		
		public static boolean shouldUpdate = false;
	}
}
