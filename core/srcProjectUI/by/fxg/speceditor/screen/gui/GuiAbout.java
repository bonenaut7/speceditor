package by.fxg.speceditor.screen.gui;

import java.awt.Desktop;
import java.net.URI;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.screen.BaseScreen;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.ui.UButton;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GuiAbout extends BaseScreen implements IFocusable {
	private UButton buttonGit, buttonClose;
	
	public GuiAbout() {
		this.buttonGit = new UButton("Github") {
			public boolean isMouseOver(int x, int y, int width, int height) {
				return GDXUtil.isMouseInArea(x, y, width, height);
			}
		};
		this.buttonClose = new UButton("Cancel") {
			public boolean isMouseOver(int x, int y, int width, int height) {
				return GDXUtil.isMouseInArea(x, y, width, height);
			}
		};
		this.setFocused(true);
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (this.buttonGit.isPressed()) try { Desktop.getDesktop().browse(new URI("https://github.com/fxgaming/")); } catch (Exception e) {}
		if (SpecEditor.get.getInput().isKeyboardDown(Keys.ESCAPE, false) || this.buttonClose.isPressed()) {
			SpecEditor.get.renderer.currentGui = null;
			this.setFocused(false);
		}
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		shape.setColor(0, 0, 0, 0.5f);
		shape.filledRectangle(0, 0, width, height);
		
		int x = width / 2 - 200, y = height / 2 - 35, boxWidth = 400, boxHeight = 70, textHeight = y + boxHeight - (int)foster.getHeight();
		shape.setColor(0.12f, 0.12f, 0.12f, 1);
		shape.filledRectangle(x, y, boxWidth, boxHeight);
		shape.setColor(1, 1, 1, 1);
		shape.rectangle(x, y, boxWidth, boxHeight, 2f);
		
		foster.setString("SpecEditor 3i - v0.0.0").draw(x + boxWidth / 2, textHeight -= foster.getHeight());
		foster.setString("SpecEditor 3i - it's the third iteration of toolset that can").draw(x + boxWidth / 2, textHeight -= foster.getHeight() + 5);
		foster.setString("assist you in creating any kind of project you want, where").draw(x + boxWidth / 2, textHeight -= foster.getHeight() + 2);
		foster.setString("only single limitation that exist - it's your imagination.").draw(x + boxWidth / 2, textHeight -= foster.getHeight() + 2);
		
		foster.setString("Made by FXG, 2022");
		shape.setColor(1, 1, 1, 0.5F);
		shape.filledRectangle(x + 5, y + 5, foster.getWidth() + 2, 11);
		shape.setColor(1, 0, 0, 0.5F);
		shape.filledRectangle(x + 5, y + 8, foster.getWidth() + 2, 4);
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(x + 5, y + 5, (int)foster.getWidth() + 2, 11)) {
			shape.setColor(0, 0, 0, 0.25F);
			shape.filledRectangle(MathUtils.map(0, 45, x - 6, x + foster.getWidth() + 7, SpecEditor.get.getTick() % 45L), y + 5, 11, 11);
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		foster.draw(x + 6, y + 7, Align.left);
		this.buttonGit.setTransforms(x + boxWidth / 2 - 25, y + 5, 50, 13).render(shape, foster);
		this.buttonClose.setTransforms(x + boxWidth - 55, y + 5, 50, 13).render(shape, foster);
		batch.end();
	}
	
	public void resize(int width, int height) {}
}
