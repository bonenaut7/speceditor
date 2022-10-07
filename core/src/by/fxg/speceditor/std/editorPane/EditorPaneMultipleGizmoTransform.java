package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.objectTree.TreeElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMultipleGizmoTransform extends EditorPane {

	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {

		return yOffset;
	}

	public void updatePane(ITreeElementSelector<?> selector) {

	}

	public boolean acceptElement(ITreeElementSelector<?> selector) {
		for (TreeElement element : selector.getIterable()) {
			if (!(element instanceof ITreeElementGizmos)) {
				return false;
			}
		}
		return selector.size() > 1;
	}
}
