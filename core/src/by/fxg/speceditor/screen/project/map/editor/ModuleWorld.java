package by.fxg.speceditor.screen.project.map.editor;

import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.screen.project.map.SubscreenEditor;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ModuleWorld extends BaseSubscreen {
	public SubscreenEditor parent;
	
	public ModuleWorld(SubscreenEditor parent) {
		this.parent = parent;
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {

	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		foster.setString("Project List").draw(x + width / 2, y + height / 3);
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {}
}
