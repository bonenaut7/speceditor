package by.fxg.speceditor.screen.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.render.RenderManager;
import by.fxg.speceditor.screen.BaseScreen;
import by.fxg.speceditor.std.objectTree.ITreeElementFolder;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UHoldButton;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GuiObjectTreeDelete extends BaseScreen {
	private SpecObjectTree objectTree;
	private Array<TreeElement> treeElements;
	private final IFocusable focusedObject;
	
	private int totalElements, folders, folderElements;
	private final String[] strings;
	
	private UButton buttonClose;
	private UHoldButton buttonDelete;
	
	public GuiObjectTreeDelete(SpecObjectTree pmoe, Array<TreeElement> treeElements) {
		this.objectTree = pmoe;
		this.treeElements = new Array<TreeElement>(treeElements);
		for (TreeElement element : treeElements) {
			this.inspect(element, false);
		}
		
		this.strings = new String[]{
			"Are you sure that you want to delete:",
			String.format("Object explorer elements: %d", this.totalElements + this.folderElements),
			String.format("with: %d folders, and %d items inside them?", this.folders, this.folderElements)
		};
		
		this.buttonClose = new UButton("Cancel");
		this.buttonDelete = new UHoldButton("Delete", UHoldButton.NO_KEY, 60).setColor(UColor.redgray);
		
		this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.focusedObject = SpecInterface.INSTANCE.currentFocus;
		SpecInterface.INSTANCE.currentFocus = null;
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (SpecEditor.get.getInput().isKeyboardDown(Keys.ESCAPE, false) || this.buttonClose.isPressed()) {
			SpecEditor.get.renderer.currentGui = null;
			SpecInterface.INSTANCE.currentFocus = this.focusedObject;
		}
		
		this.buttonDelete.update();
		if (this.buttonDelete.isPressed()) {
			for (TreeElement element : this.treeElements) {
				element.onDelete();
				this.objectTree.getElementSelector().deselectElement(element);
				this.objectTree.getStack().removeElementFromHierarchy(element);
			}
			this.objectTree.refreshTree();
			SpecEditor.get.renderer.currentGui = null;
			SpecInterface.INSTANCE.currentFocus = this.focusedObject;
		}
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		shape.setColor(0, 0, 0, 0.5f);
		shape.filledRectangle(0, 0, width, height);
				
		float longestString = 0f;
		for (String str : this.strings) {
			foster.setString(str);
			if (foster.getWidth() > longestString) longestString = foster.getWidth();
		}
	
		float boxSizeX = Math.max(longestString + 20, width / 4);
		float boxSizeY = height / 7;
		
		float x = width / 2 - boxSizeX / 2, y = height / 2 - boxSizeY / 2;
		shape.setColor(0.12f, 0.12f, 0.12f, 1);
		shape.filledRectangle(x, y, boxSizeX, boxSizeY);
		shape.setColor(1, 1, 1, 1);
		shape.rectangle(x, y, boxSizeX, boxSizeY, 2f);
		
		foster.setString(this.strings[0]).draw(x + boxSizeX / 2, y + boxSizeY - 20);
		foster.setString(this.strings[1]).draw(x + boxSizeX / 2, y + boxSizeY - 40);
		foster.setString(this.strings[2]).draw(x + boxSizeX / 2, y + boxSizeY - 52);

		this.buttonClose.render(shape, foster);
		this.buttonDelete.render(shape, foster);
		batch.end();
	}

	private void inspect(TreeElement element, boolean insideFolder) {
		if (insideFolder) this.folderElements++;
		else this.totalElements++;
		if (element instanceof ITreeElementFolder) {
			this.folders++;
			for (TreeElement element$ : ((ITreeElementFolder)element).getFolderStack().getElements()) {
				this.inspect(element$, true);
			}
		}
	}
	
	public void resize(int width, int height) {
		float longestString = 0f;
		for (String str : this.strings) {
			if (str != null && RenderManager.foster.setString(str).getWidth() > longestString) {
				longestString = RenderManager.foster.getWidth();
			}
		}
		float boxSizeX = Math.max(longestString + 20, width / 4), boxSizeY = height / 7;
		float x = width / 2 - boxSizeX / 2, y = height / 2 - boxSizeY / 2;
		int buttonWidth = ((int)boxSizeX / 2 - 30) / 2;
		this.buttonClose.setTransforms(x + boxSizeX - 10 - buttonWidth, y + 10, buttonWidth, 20);
		this.buttonDelete.setTransforms(x + boxSizeX / 2 + 10, y + 10, buttonWidth, 20);
	}
}
