package by.fxg.speceditor.std.g3d;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public interface IModelProvider {
	IModelProvider applyTransforms();
	RenderableProvider getDefaultModel();
	SceneAsset getGLTFModel();
}
