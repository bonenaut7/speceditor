package by.fxg.speceditor.screen;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.ResourceManager;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.render.RenderManager;
import by.fxg.speceditor.screen.project.ScreenCreateProject;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScreenLoading extends BaseScreen {
	private AssetManager assetManager;
	private long assetLoadingStart = 0;
	private int timer = 0;
	
	public ScreenLoading() {
		this.assetManager = ResourceManager.INSTANCE.assetManager;
		this.assetLoadingStart = System.currentTimeMillis();
		RenderManager.foster.setFont(ResourceManager.bigFont);
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (!this.assetManager.isFinished()) {
			if (this.assetManager.update(33)) { //16.6 - single frame time
				this.assetManager.finishLoading();
				Utils.logDebug("[ScreenLoading] Assets loaded within ", System.currentTimeMillis() - this.assetLoadingStart, "ms.");
				SpecEditor.get.init();
			}
		} else {
			if (++this.timer > 60) {
				SpecEditor editor = SpecEditor.get;
				if (editor.hasProgramArgument("-UITest")) editor.renderer.currentScreen = new ScreenTestUI();
				else if (editor.hasProgramArgument("-edit")) editor.renderer.currentScreen = new ScreenCreateProject(new ScreenSelectProject());
				else editor.renderer.currentScreen = new ScreenMainMenu();
			}
		}
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		shape.setColor(1, 1, 1, Math.max(0.0F, 1.0F - this.timer / 40f));
		shape.rectangle(width / 2 - width / 4, height / 5, width / 2, 10);
		shape.filledRectangle(width / 2 - width / 4, height / 5 + 1, Interpolation.linear.apply(0, width / 2 - 1, this.assetManager.getProgress()), 9);

		if (!this.assetManager.isFinished()) foster.setString("SpecEditor").draw(width / 2, height / 2);
		else foster.setString("SpecEditor").draw(width / 2, Interpolation.exp5Out.apply(height / 2, height / 2 + 140, this.timer / 60f));
		batch.end();
	}

	public void resize(int width, int height) {}
}
