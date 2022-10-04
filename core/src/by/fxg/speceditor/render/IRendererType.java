package by.fxg.speceditor.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;

import by.fxg.pilesos.decals.BaseDecal;
import by.fxg.speceditor.hc.elementlist.elements.ElementLight;
import by.fxg.speceditor.tools.debugdraw.IDebugDraw;
import by.fxg.speceditor.tools.g3d.IModelProvider;

public interface IRendererType{
	
	void addAttribute(Attribute attribute);
	void removeAttribute(Attribute attribute);
	
	void addDecal(BaseDecal decal);
	void addLight(ElementLight light, boolean selected, boolean visible);
	void addRenderable(IModelProvider modelProvider);
	void addDebugDrawable(IDebugDraw debugDraw);
	
	void update();
	void passRender();
	TextureRegion getTexture();
	
	void clear(boolean partially);
}
