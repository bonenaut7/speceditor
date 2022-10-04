package by.fxg.speceditor.TO_REMOVE;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UInputField;
import by.fxg.speceditor.ui.URenderBlock;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class TERFolder extends TreeElementRenderable<ElementFolder> {
	public URenderBlock[] blocks = new URenderBlock[1];

	protected UInputField nameInput;
	
	public TERFolder(ElementFolder object) {
		super(object);
		this.nameInput = new UInputField().setMaxLength(24).setText(renderable.getName());

		this.blocks[0] = new URenderBlock("Parameters", 0, 0, 0) {
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				foster.setString("Name:").draw(this.x, (y -= 10) - 8, Align.left);
				shape.setColor(UColor.suboverlay);
				shape.filledRectangle(this.x + (int)foster.getWidth() + 5, y - 19, this.width - (int)foster.getWidth() - 5, 15);
				nameInput.setTransforms(this.x + (int)foster.getWidth() + 5, y -= 19, this.width - (int)foster.getWidth() - 5, 15).update();
				nameInput.render(batch, shape, foster);
				return y + 8;
			}
		}.setDropped(true);
	}
	
	public void resetInputFields() {
		this.renderable.setName(this.nameInput.getText().length() == 0 ? "Unnamed" : this.nameInput.getText());
	}
	
	public void update(int x, int y, int width, int height, boolean allowMouse) {
		for (URenderBlock block : this.blocks) block.setTransforms(x + 10, width - 20, 15);
		this.resetInputFields();
	}

	public int render(Batch batch, ShapeDrawer shape, Foster foster, int hOffset, int x, int y, int width, int height, boolean allowMouse) {
		for (URenderBlock block : this.blocks) hOffset = block.render(hOffset, batch, shape, foster);
		return hOffset;
	}
}
