package by.fxg.speceditor.scenes.screen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.viewport.IViewportRenderer;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenSceneEditorModuleViewport extends BaseSubscreen {
	private Vector2 scroll = new Vector2(); //scroll, lastHeight
	private IViewportRenderer viewportRenderer;

	public SubscreenSceneEditorModuleViewport(IViewportRenderer viewportRenderer) {
		this.viewportRenderer = viewportRenderer;
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		if (GDXUtil.isMouseInArea(x, y, width, height) && SpecInterface.isFocused(this)) {
			if (Game.get.getInput().isMouseScrolled(true) && this.scroll.x < this.scroll.y) {
				this.scroll.x = Math.min(this.scroll.x + 50, this.scroll.y);
			} else if (Game.get.getInput().isMouseScrolled(false) && this.scroll.x > 0) {
				this.scroll.x = Math.max(0, this.scroll.x - 50);
			}
		}
		if (this.scroll.x > this.scroll.y) this.scroll.x = this.scroll.y;
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		if (this.viewportRenderer != null && this.viewportRenderer.getEditorPane() != null) {
			batch.flush();
			if (PilesosScissorStack.instance.peekScissors(x, y, width, height)) {
				int paneY = y + (int)this.scroll.x;
				this.scroll.y = Math.max(0.01f, this.viewportRenderer.getEditorPane().updateAndRender(batch, shape, foster, x, paneY, width - 4, height, paneY + height) - paneY - height * 2);
				float yScrollHeight = Interpolation.linear.apply(3, height, Math.min(height / (height + this.scroll.y), 1));
				float yScrollPosition = Interpolation.linear.apply(height - yScrollHeight, 0, Math.min(this.scroll.x / this.scroll.y, 1));
				shape.rectangle(x, y, width - 4, height);
				shape.setColor(1, 1, 1, 0.4f);
				shape.filledRectangle(x + width - 4, y + yScrollPosition + 1, 3, yScrollHeight);
				batch.flush();
				PilesosScissorStack.instance.popScissors();
			}
		} else foster.setString("Editor pane not found for viewport").draw(x + width / 2, y + height / 2 - foster.getHalfHeight());
	}
	
	public void resize(int subX, int subY, int subWidth, int subHeight) {}
}