package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.objectTree.elements.ElementFolder;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.ui.ColoredInputField;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneFolder extends EditorPane implements ISTDInputFieldListener {
	private TreeElement folder = null;
	private STDInputField elementName;

	public EditorPaneFolder() {
		this.elementName = new ColoredInputField().setAllowFullfocus(false).setListener(this, "name").setMaxLength(48);
	}
	
	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {
		yOffset -= 8;
		foster.setString("Name:").draw(x + 5, yOffset -= foster.getHeight(), Align.left);
		this.elementName.setTransforms(x + (int)foster.getWidth() + 10, yOffset -= foster.getHalfHeight(), width - (int)foster.getWidth() - 15, 15).setFoster(foster).update();
		this.elementName.render(batch, shape);
		return yOffset;
	}
	
	public void whileFocused(STDInputField inputField, String id) {
		switch (id) {
			case "name": this.folder.setName(this.elementName.getText()); break;
		}
	}

	public void updatePane(ITreeElementSelector<?> selector) {
		this.folder = selector.get(0);
		this.elementName.setText(this.folder.getName());
	}

	public boolean acceptElement(ITreeElementSelector<?> selector) {
		return selector.size() == 1 && selector.get(0) instanceof ElementFolder;
	}
}
