package by.fxg.speceditor.std.g3d;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;

public interface ITreeElementModelProvider {
	ITreeElementModelProvider applyTransforms();
	RenderableProvider getRenderableProvider();
}
