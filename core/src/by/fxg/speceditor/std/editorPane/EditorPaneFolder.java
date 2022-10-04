package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.api.std.editorPane.EditorPane;
import by.fxg.speceditor.api.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.api.std.objectTree.TreeElement;
import by.fxg.speceditor.std.objecttree.elements.ElementFolder;
import by.fxg.speceditor.ui.UInputField;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneFolder extends EditorPane {
	private TreeElement folder = null;
	private UInputField folderName;
	
	public EditorPaneFolder() {
		this.folderName = new UInputField().setMaxLength(32);
	}
	
	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {
		foster.setString("Name:").draw(x + 5, (yOffset -= 10) + 1, Align.left);
		this.folderName.setTransforms(x + (int)foster.getWidth() + 10, yOffset -= 10, width - (int)foster.getWidth() - 15, 15).update();
		this.folderName.render(batch, shape, foster);
		this.folder.setName(this.folderName.getText());
		return yOffset;
	}

	public void updatePane(ITreeElementSelector<?> selector) {
		this.folder = selector.get(0);
		this.folderName.setText(this.folder.getName());
	}

	public boolean acceptElement(ITreeElementSelector<?> selector) {
		return selector.size() == 1 && selector.get(0) instanceof ElementFolder;
	}
}
