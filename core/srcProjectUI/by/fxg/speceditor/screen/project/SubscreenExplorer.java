package by.fxg.speceditor.screen.project;

import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenExplorer extends BaseSubscreen {
	
	public SubscreenExplorer(int x, int y, int width, int height) {
	
		this.resize(x, y, width, height);
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {

	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		batch.begin();
		shape.setColor(UColor.background);
		shape.filledRectangle(x, y, width, height);
		shape.setColor(UColor.gray);
		shape.rectangle(x + 1, y + 1, width - 2, height - 2);
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(x + 1, y + 1, width - 2, height - 2)) {
			foster.setString("ProjectAsset/Prefab Explorer (not implemented)").draw(x + width / 2, y + height / 2 - foster.getHalfHeight());
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		batch.end();
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {
	}
}