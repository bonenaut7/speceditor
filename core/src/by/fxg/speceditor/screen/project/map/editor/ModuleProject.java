package by.fxg.speceditor.screen.project.map.editor;

import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.screen.project.map.SubscreenEditor;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ModuleProject extends BaseSubscreen {
	public SubscreenEditor parent;
	
	public ModuleProject(SubscreenEditor parent) {
		this.parent = parent;
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {

	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		shape.setColor(0, 0, 1, .5f);
		shape.filledRectangle(x, y, width, height);
		foster.setString("Project Editor").draw(x + width / 2, y + height / 2);
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {
	}
}
