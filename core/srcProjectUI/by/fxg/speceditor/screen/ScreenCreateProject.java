package by.fxg.speceditor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UInputField;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScreenCreateProject extends BaseScreen {
	public UButton bCreate, bBack;
	public UInputField iNameField;
	public UCheckbox cBackup;
	
	public ScreenCreateProject() {
		int x = Gdx.graphics.getWidth() / 2 - 250 / 2, y = Gdx.graphics.getHeight() / 2 - 100;
		this.bCreate = new UButton("Create", x + 130, y + 5, 100, 20);
		this.bBack = new UButton("Back", x + 20, y + 5, 100, 20);
		this.iNameField = new UInputField(x + 84, y + 200 - 22, 160, 18).setMaxLength(32);
		this.cBackup = new UCheckbox(false, x + 98, y + 160, 12, 12);
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		this.bCreate.setEnabled(this.iNameField.getText().length() > 2);
		this.iNameField.update();
		this.cBackup.update();
		
		if (this.bCreate.isPressed()) {
			//__$$Project.createProject(this.iNameField.getText(), this.cBackup.getValue());
			Game.get.renderer.currentScreen = new ScreenSelectProject();
		}
		if (this.bBack.isPressed()) {
			Game.get.renderer.currentScreen = new ScreenSelectProject();
		}
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		int wsx = 250, wsy = 200;
		int wx = width / 2 - wsx / 2, wy = height / 2 - wsy / 2;
		shape.setColor(1, 1, 1, 1);
		shape.rectangle(wx, wy, wsx, wsy);
		foster.setString("Project name").draw(wx + 7, wy + wsy - 9, Align.left);
		foster.setString("Enable backups").draw(wx + 7, wy + wsy - 30, Align.left);
		
		this.bCreate.render(shape, foster);
		this.bBack.render(shape, foster);
		this.iNameField.render(batch, shape, foster);
		this.cBackup.render(shape);
		batch.end();
	}

	public void resize(int width, int height) {
		int x = width / 2 - 250 / 2, y = height / 2 - 100;
		this.bCreate.setTransforms(x + 130, y + 5, 100, 20);
		this.bBack.setTransforms(x + 20, y + 5, 100, 20);
		this.iNameField.setTransforms(x + 84, y + 200 - 22, 160, 18);
		this.cBackup.setTransforms(x + 98, y + 160, 12, 12);
	}
}
