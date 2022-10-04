package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.api.std.editorPane.EditorPane;
import by.fxg.speceditor.api.std.objectTree.ITreeElementSelector;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneStandardRename extends EditorPane {
	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {

		return yOffset;
	}

	public void updatePane(ITreeElementSelector<?> selector) {

	}
	
	public boolean acceptElement(ITreeElementSelector<?> selector) {
		return selector.size() == 1;
	}
}
