package by.fxg.speceditor.screen.project.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.TO_REMOVE.ModulePMOE;
import by.fxg.speceditor.TO_REMOVE.ModuleProject;
import by.fxg.speceditor.TO_REMOVE.ModuleViewport;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

@Deprecated /** (TODO) DO NOT USE, MUST BE REMOVED **/
public class SubscreenEditor extends BaseSubscreen {
	private final String[] EDITOR_MODES = {"Project", "Viewport",/* "World",*/ "<PM> Selectable"};
	
	public ScreenProject parent;
	
	private UDropdownSelectSingle editorModeSelector;
	private BaseSubscreen moduleProject, moduleViewport, moduleWorld, modulePMOE;
	
	public SubscreenEditor(ScreenProject parent, int x, int y, int width, int height) {
		this.parent = parent;

		this.editorModeSelector = new UDropdownSelectSingle(x + 40, y + height - 30, 100, 12, 12, this.EDITOR_MODES).setSelectedVariant(this.EDITOR_MODES.length - 1);
		this.moduleProject = new ModuleProject();
		this.moduleViewport = new ModuleViewport(this);
		//this.moduleWorld = new ModuleWorld(this);
		this.modulePMOE = new ModulePMOE(this);
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		if (!this.editorModeSelector.isDropped()) {
			int ix = x + 2, iy = y + 2, iw = width - 4, ih = height - 30 - 4;
			switch(this.editorModeSelector.getVariant()) {
				case 0: this.moduleProject.update(batch, shape, foster, ix, iy, iw, ih); break;
				case 1: this.moduleViewport.update(batch, shape, foster, ix, iy, iw, ih); break;
				//case 2: this.moduleWorld.update(batch, shape, foster, ix, iy, iw, ih); break;
				case 2: this.modulePMOE.update(batch, shape, foster, ix, iy, iw, ih); break;
			}
		}
		this.editorModeSelector.update();
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		batch.begin();
		shape.setColor(UColor.background);
		shape.filledRectangle(x, y, width, height);
		shape.setColor(UColor.gray);
		shape.rectangle(x + 2, y + 1, width - 3, height - 3, 1);
		
		foster.setString("Editor").draw(x + 5, y + height - 5, Align.left);
		foster.setString("Mode:").draw(x + 7, y + height - 20, Align.left);
		
		
		int ix = x + 4, iy = y + 3, iw = width - 7, ih = height - 30 - 7;
		shape.rectangle(ix, iy, iw, ih, 1);
		
		switch(this.editorModeSelector.getVariant()) {
			case 0: this.moduleProject.render(batch, shape, foster, ix, iy, iw, ih); break;
			case 1: this.moduleViewport.render(batch, shape, foster, ix, iy, iw, ih); break;
			//case 2: this.moduleWorld.render(batch, shape, foster, ix, iy, iw, ih); break;
			case 2: this.modulePMOE.render(batch, shape, foster, ix, iy, iw, ih); break;
		}

		this.editorModeSelector.render(shape, foster);
		batch.end();
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {
		this.editorModeSelector.setTransforms(subX + 40, subY + subHeight - 30, 100, 12);
	}
}