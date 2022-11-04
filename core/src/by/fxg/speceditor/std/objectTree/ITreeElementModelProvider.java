package by.fxg.speceditor.std.objectTree;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;

public interface ITreeElementModelProvider {
	ITreeElementModelProvider applyTransforms();
	RenderableProvider getRenderableProvider();
}
