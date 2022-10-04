package by.fxg.speceditor.screen.project.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.elements.ElementFolder;
import by.fxg.speceditor.hc.elementlist.elements.ElementMultiHitbox;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UDropdownClick;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenProjectManager extends BaseSubscreen {
	public ScreenProject parent;
	private boolean init = false;
	
	public PMObjectExplorer objectExplorer;
	private UDropdownClick projectExplorerAddButton;
	
	public SubscreenProjectManager(ScreenProject parent) {
		this.parent = parent;
		this.objectExplorer = new PMObjectExplorer(0, 0, 0, 0);
		this.projectExplorerAddButton = new UDropdownClick("Add element", 0, 0, 0, 0, 15, "Folder", "MultiHitbox");
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		if (!this.init) {
			this.init = true;
			this.resize(x, y, width, height);
		}
		this.projectExplorerAddButton.update();
		if (!this.projectExplorerAddButton.isDropped() && !Game.get.getInput().isCursorCatched() && Game.get.renderer.currentGui == null) {
			this.objectExplorer.update();
		}
		if (this.projectExplorerAddButton.isPressed()) {
			switch (this.projectExplorerAddButton.getVariant()) {
				case 0: this.objectExplorer.elementStack.add(new ElementFolder()); break;
				case 1: this.objectExplorer.elementStack.add(new ElementMultiHitbox()); break;
			}
		}
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		batch.begin();
		shape.setColor(UColor.background);
		shape.filledRectangle(x, y, width, height);
		shape.setColor(UColor.gray);
		shape.rectangle(x + 2, y + 1, width - 3, height - 3, 1);
		foster.setString("Explorer").draw(x + 10, y + height - 10, Align.left);
		this.objectExplorer.setTransforms(x + 5, y + 5, width - 10, height - 30);
		this.objectExplorer.render(batch, shape, foster, !this.projectExplorerAddButton.isDropped() && !Game.get.getInput().isCursorCatched() && Game.get.renderer.currentGui == null);
		this.projectExplorerAddButton.render(shape, foster);
		
		batch.end();
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {
		this.objectExplorer.setTransforms(subX + 10, subX + subHeight - 25 - 400, subWidth - 20, 400);
		this.projectExplorerAddButton.setTransforms(subX + subWidth - 10 - 80, subX + subHeight - 20, 80, 15);
	}
}