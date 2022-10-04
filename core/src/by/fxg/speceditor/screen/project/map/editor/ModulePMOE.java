package by.fxg.speceditor.screen.project.map.editor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.TreeElement;
import by.fxg.speceditor.screen.project.map.SubscreenEditor;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ModulePMOE extends BaseSubscreen {
	public SubscreenEditor parent;

	private Vector3 scroll; //Scrolls// yScrollMax, yScroll, scrollAnimation
	private TreeElement element = null;
	
	public ModulePMOE(SubscreenEditor parent) {
		this.scroll = new Vector3();
		this.parent = parent;
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		PMObjectExplorer pmoe = this.parent.parent.subProjectManager.objectExplorer;
		if (this.element == null) {
			if (pmoe.selectedItems.size == 1) {
				this.element = pmoe.selectedItems.get(0);
			}
		} else {
			if (pmoe.selectedItems.size != 1 || !pmoe.selectedItems.get(0).equals(this.element)) {
				this.element = null;
			}
		}
		
		if (this.element != null) {
			if (this.element.getRenderable() != null) {
				this.element.getRenderable().update(x, y - (int)this.scroll.y, width - (int)this.scroll.z, height, !Game.get.getInput().isCursorCatched() && Game.get.renderer.currentGui == null);
			}
		}
		
		if (GDXUtil.isMouseInArea(x, y, width, height)) {
			if (Game.get.getInput().isMouseScrolled(true)) {
				if (this.scroll.y < this.scroll.x) this.scroll.y = Math.min(this.scroll.y + 50, this.scroll.x);
			} else if (Game.get.getInput().isMouseScrolled(false)) {
				if (this.scroll.y > 0) this.scroll.y = Math.max(0, this.scroll.y - 50);
			}
			
			if (this.scroll.x > 0 && this.scroll.z < 7 && Game.get.getTick() % 3L == 0) this.scroll.z++;
		} else {
			if (this.scroll.z > 0 && Game.get.getTick() % 3L == 0) this.scroll.z--;
		}
		
		if (this.scroll.x < this.scroll.y) this.scroll.y = Math.max(this.scroll.x, 0);
		else if (this.scroll.y < 0) this.scroll.y = 0;
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		if (this.element == null) {
			foster.setString("Select item in the explorer").draw(x + width / 2, y + height / 2);
		} else {
			if (this.element.getRenderable() != null) {
				batch.flush();
				if (PilesosScissorStack.instance.setBounds(2, x, y, width, height).pushScissors(2)) {
					int maxScrollY = -this.element.getRenderable().render(batch, shape, foster, y + height + (int)this.scroll.y, x, y, width - (int)this.scroll.z, height, !Game.get.getInput().isCursorCatched() && Game.get.renderer.currentGui == null);
					this.scroll.x = maxScrollY + (int)this.scroll.y;
					if (this.scroll.x > 0) {
						shape.setColor(1, 1, 1, 0.4f);
						float yScrollHeight = Interpolation.linear.apply((y + height), 2, Math.min(this.scroll.x / (y + height), 1f));
						float yScrollPosition = Interpolation.linear.apply((y + height) - yScrollHeight - 4, 2, Math.min(this.scroll.y / this.scroll.x, 1));
						shape.filledRectangle(x + width - this.scroll.z + 1, y + yScrollPosition, Math.max(this.scroll.z - 3, 0), yScrollHeight);
					}
					batch.flush();
					PilesosScissorStack.instance.popScissors();
				}
			}
		}
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {}
}
