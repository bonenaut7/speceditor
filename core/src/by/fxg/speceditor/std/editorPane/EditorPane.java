package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class EditorPane {
	/** Combined update and render method. Returns height of rendered elements to calculate scroll value. **/
	abstract public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset);
	
	abstract public void updatePane(ITreeElementSelector<?> selector);
	abstract public boolean acceptElement(ITreeElementSelector<?> selector);
	
	public float getLongestStringWidth(Foster foster, String... strings) {
		float width = 0;
		for (String str : strings) {
			if (foster.setString(str).getWidth() > width) {
				width = foster.getWidth();
			}
		}
		return width;
	}
}
