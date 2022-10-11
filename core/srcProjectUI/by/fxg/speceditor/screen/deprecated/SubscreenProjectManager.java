package by.fxg.speceditor.screen.deprecated;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.elements.ElementFolder;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UDropdownClick;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenProjectManager extends BaseSubscreen {
	private SpecObjectTree objectTree;
	private UDropdownClick projectExplorerAddButton;
	
	public SubscreenProjectManager(SpecObjectTree objectTree, int x, int y, int width, int height) {
		this.objectTree = objectTree;
		this.projectExplorerAddButton = new UDropdownClick("Add element", 12, "Folder");
		this.resize(x, y, width, height);
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		this.projectExplorerAddButton.update();
		this.objectTree.update();
		if (this.projectExplorerAddButton.isPressed()) {
			switch (this.projectExplorerAddButton.getVariant()) {
				case 0: this.objectTree.getStack().add(new ElementFolder(null)); break;
//				case 1: this.objectTree.elementStack.add(new ElementMultiHitbox()); break;
			}
		}
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		batch.begin();
		shape.setColor(UColor.background);
		shape.filledRectangle(x, y, width, height);
		shape.setColor(UColor.gray);
		shape.rectangle(x + 1, y + 1, width - 2, height - 2);
		
		foster.setString("Explorer").draw(x + 10, y + height - 11 - foster.getHalfHeight(), Align.left);
		this.objectTree.render(batch, shape, foster);
		this.projectExplorerAddButton.render(shape, foster);
		batch.end();
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {
		this.objectTree.setTransforms(subX + 1, subY + 1, subWidth - 2, subHeight - 23);
		this.projectExplorerAddButton.setTransforms(subX + subWidth - 85, subX + subHeight - 17, 80, 12);
	}
}