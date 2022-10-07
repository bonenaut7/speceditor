package by.fxg.speceditor.prefabs.screen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.screen.deprecated.ModuleProject;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenPrefabEditor extends BaseSubscreen {
	private final String[] EDITOR_MODES = {"Project", "Viewport", "<PM> Selectable"};
	protected ScreenPrefabProject screenProject;
	
	private UDropdownSelectSingle editorModeSelector;
	private ModuleProject moduleProject;
	private SubscreenPrefabEditorModuleViewport moduleViewport;
	private SubscreenPrefabEditorModuleObjectExplorer moduleObjectExplorer;
	
	public SubscreenPrefabEditor(ScreenPrefabProject screenProject, int x, int y, int width, int height) {
		this.screenProject = screenProject;
		
		this.editorModeSelector = new UDropdownSelectSingle(12, this.EDITOR_MODES).setSelectedVariant(this.EDITOR_MODES.length - 1);
		this.moduleProject = new ModuleProject();
		this.moduleViewport = new SubscreenPrefabEditorModuleViewport(this);
		this.moduleObjectExplorer = new SubscreenPrefabEditorModuleObjectExplorer();
		this.resize(x, y, width, height);
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		if (!this.editorModeSelector.isDropped()) {
			int ix = x + 1, iy = y + 1, iw = width - 2, ih = height - 23;
			switch(this.editorModeSelector.getVariant()) {
				case 0: this.moduleProject.update(batch, shape, foster, ix, iy, iw, ih); break;
				case 1: this.moduleViewport.update(batch, shape, foster, ix, iy, iw, ih); break;
				case 2: this.moduleObjectExplorer.update(batch, shape, foster, ix, iy, iw, ih); break;
			}
		}
		this.editorModeSelector.update();
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		batch.begin();
		shape.setColor(UColor.background);
		shape.filledRectangle(x, y, width, height);
		shape.setColor(UColor.gray);
		shape.rectangle(x + 1, y + 1, width - 2, height - 2);
		
		
		foster.setString("Editor").draw(x + 5, y + height - 7, Align.left);
		foster.setString("Mode:").draw(x + width - 160, y + height - 7, Align.right);
		
		int ix = x + 1, iy = y + 1, iw = width - 2, ih = height - 23;
		shape.rectangle(ix, iy, iw, ih, 1);
		
		switch(this.editorModeSelector.getVariant()) {
			case 0: this.moduleProject.render(batch, shape, foster, ix, iy, iw, ih); break;
			case 1: this.moduleViewport.render(batch, shape, foster, ix, iy, iw, ih); break;
			case 2: this.moduleObjectExplorer.render(batch, shape, foster, ix, iy, iw, ih); break;
		}

		this.editorModeSelector.render(shape, foster);
		batch.end();
	}

	public void updateSelectableEditorPane(ITreeElementSelector<?> treeElementSelector) {
		((SubscreenPrefabEditorModuleObjectExplorer)this.moduleObjectExplorer).updateEditorPane(treeElementSelector);
	}
	
	public void resize(int subX, int subY, int subWidth, int subHeight) {
		this.editorModeSelector.setTransforms(subX + subWidth - 155, subY + subHeight - 17, 150, 12);
	}
}