package by.fxg.speceditor.scenes.screen;

import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ModuleProject extends BaseSubscreen {
	public ModuleProject() {
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {

	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		foster.setString("Project settings\ncurrently not\nimplemented").draw(x + width / 2, y + height / 2 - foster.getHalfHeight());
		//TODO implement Project settings module
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {
	}
}
