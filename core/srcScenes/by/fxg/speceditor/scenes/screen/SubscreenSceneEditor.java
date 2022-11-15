package by.fxg.speceditor.scenes.screen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenSceneEditor extends BaseSubscreen {
	private final String[] EDITOR_MODES = { "Project", "Viewport", "<PM> Selectable" };
	protected ScreenSceneProject screenProject;
	
	private UDropdownSelectSingle editorModeSelector;
	private ModuleProject moduleProject;
	private SubscreenSceneEditorModuleViewport moduleViewport;
	private SubscreenSceneEditorModuleObjectExplorer moduleObjectExplorer;
	
	public SubscreenSceneEditor(ScreenSceneProject screenProject, int x, int y, int width, int height) {
		this.screenProject = screenProject;
		
		this.editorModeSelector = new UDropdownSelectSingle(12, this.EDITOR_MODES).setVariantSelected(this.EDITOR_MODES.length - 1);
		this.moduleProject = new ModuleProject();
		this.moduleViewport = new SubscreenSceneEditorModuleViewport(this.screenProject.project.renderer);
		this.moduleObjectExplorer = new SubscreenSceneEditorModuleObjectExplorer();
		this.resize(x, y, width, height);
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		if (!this.editorModeSelector.isFocused()) {
			int ix = x + 1, iy = y + 1, iw = width - 2, ih = height - 22;
			switch(this.editorModeSelector.getVariantSelected()) {
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
		
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(x + 1, y + 1, width - 2, height - 2)) {
			foster.setString("Editor").draw(x + 10, y + height - 12 - foster.getHalfHeight(), Align.left);
			
			int ix = x + 1, iy = y + 1, iw = width - 2, ih = height - 22;
			shape.rectangle(ix, iy, iw, ih, 1);
			
			switch(this.editorModeSelector.getVariantSelected()) {
				case 0: this.moduleProject.render(batch, shape, foster, ix, iy, iw, ih); break;
				case 1: this.moduleViewport.render(batch, shape, foster, ix, iy, iw, ih); break;
				case 2: this.moduleObjectExplorer.render(batch, shape, foster, ix, iy, iw, ih); break;
			}

			this.editorModeSelector.render(shape, foster);	
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		batch.end();
	}

	public void updateSelectableEditorPane(ITreeElementSelector<?> treeElementSelector) {
		((SubscreenSceneEditorModuleObjectExplorer)this.moduleObjectExplorer).updateEditorPane(treeElementSelector);
	}
	
	public void resize(int subX, int subY, int subWidth, int subHeight) {
		this.editorModeSelector.setTransforms(subX + subWidth - 155, subY + subHeight - 18, 150, 13, 13);
	}
}