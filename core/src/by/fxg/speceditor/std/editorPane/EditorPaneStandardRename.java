package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.ui.ColoredInputField;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneStandardRename extends EditorPane implements ISTDInputFieldListener {
	private TreeElement element = null;
	private STDInputField elementName;

	public EditorPaneStandardRename() {
		this.elementName = new ColoredInputField().setAllowFullfocus(false).setListener(this, "name").setMaxLength(48);
	}
	
	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {
		yOffset -= 8;
		foster.setString("Name").draw(x + 5, yOffset -= foster.getHeight(), Align.left);
		this.elementName.setTransforms(x + foster.getWidth() + 10, yOffset -= foster.getHalfHeight(), width - foster.getWidth() - 15, 15).setFoster(foster).update();
		this.elementName.render(batch, shape);
		return yOffset;
	}
	
	public void whileInputFieldFocused(STDInputField inputField, String id) {
		switch (id) {
			case "name": this.element.setName(this.elementName.getText()); break;
		}
	}

	public void updatePane(ITreeElementSelector<?> selector) {
		this.element = selector.get(0);
		this.elementName.setText(this.element.getName());
	}

	public boolean acceptElement(ITreeElementSelector<?> selector) {
		return selector.size() == 1;
	}
}
