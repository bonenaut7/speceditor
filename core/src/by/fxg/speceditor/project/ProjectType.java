package by.fxg.speceditor.project;

import com.badlogic.gdx.graphics.Camera;

import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.render.DefaultRenderer;
import by.fxg.speceditor.render.IRendererType;

public enum ProjectType {
	DEFAULT,
	GLTF,
	
	PREFAB_DEFAULT,
	PREFAB_GLTF,
	;
	
	public IRendererType createRenderer(PMObjectExplorer pmObjectExplorer, Camera camera) {
		switch (this) {
			case PREFAB_DEFAULT:
			case DEFAULT: return new DefaultRenderer(pmObjectExplorer, camera);
			case PREFAB_GLTF:
			case GLTF: ;
			default: return null;
		}
	}
}
