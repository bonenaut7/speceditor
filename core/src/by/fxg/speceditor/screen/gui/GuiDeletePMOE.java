package by.fxg.speceditor.screen.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.TreeElement;
import by.fxg.speceditor.render.RenderManager;
import by.fxg.speceditor.screen.BaseScreen;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UHoldButton;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GuiDeletePMOE extends BaseScreen {
	private PMObjectExplorer pmoe;
	private Array<TreeElement> treeElements;
	
	private int totalElements, folders, folderElements;
	private final String[] strings;
	
	private UButton buttonClose;
	private UHoldButton buttonDelete;
	
	public GuiDeletePMOE(PMObjectExplorer pmoe, Array<TreeElement> treeElements) {
		this.pmoe = pmoe;
		this.treeElements = new Array<TreeElement>(treeElements);
		for (TreeElement element : treeElements) {
			this.inspect(element, false);
		}
		
		this.strings = new String[]{
			"Are you sure that you want to delete:",
			String.format("Object explorer elements: %d", this.totalElements + this.folderElements),
			String.format("with: %d folders, and %d items inside them?", this.folders, this.folderElements)
		};
		
		this.init(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public void init(int width, int height) {
		float longestString = 0f;
		for (String str : strings) {
			RenderManager.foster.setString(str);
			if (RenderManager.foster.getWidth() > longestString) longestString = RenderManager.foster.getWidth();
		}
		float boxSizeX = Math.max(longestString + 20, width / 4);
		float boxSizeY = height / 7;
		float x = width / 2 - boxSizeX / 2, y = height / 2 - boxSizeY / 2;
		
		int buttonWidth = ((int)boxSizeX / 2 - 30) / 2;
		
		this.buttonClose = new UButton("Cancel", (int)(x + boxSizeX) - 10 - buttonWidth, (int)y + 10, buttonWidth, 20);
		this.buttonDelete = new UHoldButton("Delete", UHoldButton.NO_KEY, 60, (int)(x + boxSizeX / 2 + 10), (int)y + 10, buttonWidth, 20).setColor(UColor.redgray);
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (Game.get.getInput().isKeyboardDown(Keys.ESCAPE, false) || this.buttonClose.isPressed()) {
			Game.get.renderer.currentGui = null;
		}
		
		this.buttonDelete.update();
		if (this.buttonDelete.isPressed()) {
			for (TreeElement element : this.treeElements) {
				element.onDelete();
				this.pmoe.selectedItems.removeValue(element, true);
				this.pmoe.elementStack.remove(element);
			}
			this.pmoe.refreshData();
			Game.get.renderer.currentGui = null;
		}
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		shape.setColor(0, 0, 0, 0.5f);
		shape.filledRectangle(0, 0, width, height);
				
		float longestString = 0f;
		for (String str : strings) {
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
		
		//int buttonWidth = ((int)boxSizeX / 2 - 30) / 2;
		//this.buttonDelete = new UHoldButton("Delete", UHoldButton.NO_KEY, 60, (int)(x + boxSizeX / 2 + 10), (int)y + 10, buttonWidth, 20).setColor(UColor.redgray);
		
		this.buttonClose.render(shape, foster);
		this.buttonDelete.render(shape, foster);
		batch.end();
	}

	private void inspect(TreeElement element, boolean insideFolder) {
		if (insideFolder) this.folderElements++;
		else this.totalElements++;
		if (element.hasStack()) {
			this.folders++;
			for (TreeElement element$ : element.getStack().getItems()) {
				this.inspect(element$, true);
			}
		}
	}
	
	public void resize(int width, int height) {}
}
