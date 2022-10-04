package by.fxg.speceditor.screen.project.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

import by.fxg.libtweaks.AWTScreenshot;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.GInputProcessor;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.project.Project;
import by.fxg.speceditor.screen.project.map.viewport.GizmosModule;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenViewport extends BaseSubscreen {
	public ScreenProject parent;
	
	private UButton[] toolButtons = new UButton[4];
	public PerspectiveCamera camera;
	public GizmosModule gizmosModule;
	
	private Vector2 lookVector = new Vector2();
	private Vector3 posVector = new Vector3(), tmpVector = new Vector3();
	private Matrix4 tmpMatrix = new Matrix4();
	
	public SubscreenViewport(ScreenProject parent, int x, int y, int width, int height) {
		this.parent = parent;
		
		String[] text = {"None", "Translate", "Rotate", "Scale"};
		for (int i = 0; i != this.toolButtons.length; i++) {
			this.toolButtons[i] = new UButton(text[i], 0, 0, 0, 0);
		}
		
		this.camera = new PerspectiveCamera(67F, width - 6, height - 6);
		this.camera.fieldOfView = parent.project.cameraSettings.x;
		this.camera.far = parent.project.cameraSettings.y;
		this.camera.near = parent.project.cameraSettings.z;
		this.camera.update();
		
		this.gizmosModule = new GizmosModule();
		Project.renderer = parent.project.projectType.createRenderer(parent.subProjectManager.objectExplorer, this.camera);
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		GInputProcessor input = Game.get.getInput();
		if (input.isCursorCatched()) {
			if (input.isKeyboardDown(Keys.ESCAPE, false) || input.isKeyboardDown(Keys.SYM, false)) {
				input.setCursorCatched(false);
				Gdx.input.setCursorPosition(x + width / 2, Gdx.graphics.getHeight() - (y + height / 2));
			}
			
			float moveSpeed = 0.1f;
			if (input.isKeyboardDown(Keys.W, true)) this.tmpVector.add(0.0F, 0.0F, moveSpeed);
			if (input.isKeyboardDown(Keys.S, true)) this.tmpVector.add(0.0F, 0.0F, -moveSpeed);
			if (input.isKeyboardDown(Keys.A, true)) this.tmpVector.add(moveSpeed, 0.0F, 0.0F);
			if (input.isKeyboardDown(Keys.D, true)) this.tmpVector.add(-moveSpeed, 0.0F, 0.0F);
			if (input.isKeyboardDown(Keys.SHIFT_LEFT, true)) this.posVector.add(0.0F, -moveSpeed * 2, 0.0F);
			else if (input.isKeyboardDown(Keys.SPACE, true)) this.posVector.add(0.0F, moveSpeed * 2, 0.0F);
			if (!this.tmpVector.isZero()) {
				this.tmpVector.rotate(-this.lookVector.x, 0.0F, -1.0F, 0.0F);
				this.posVector.add(this.tmpVector);
			}

			this.tmpMatrix = new Matrix4().setFromEulerAngles(this.lookVector.x, -this.lookVector.y, 0.0F);
			this.camera.fieldOfView = this.parent.project.cameraSettings.x;
			this.camera.far = this.parent.project.cameraSettings.y;
			this.camera.near = this.parent.project.cameraSettings.z;
			this.camera.direction.set(0, 0, 1);
			this.camera.up.set(0, 1, 0);
			this.camera.rotate(this.tmpMatrix);
			this.camera.position.set(this.posVector);
			
			this.tmpVector.setZero();
			this.camera.update();
		} else if (input.isMouseDown(2, false) && GDXUtil.isMouseInArea(x, y, width, height) && !Game.get.getInput().isCursorCatched()) {
			 input.setCursorCatched(true);
		}
		
		for (int i = 0; i != this.toolButtons.length; i++) {
			if (this.toolButtons[i].isPressed()) {
				this.gizmosModule.toolType = i - 1;
			}
		}
		
		this.gizmosModule.update(this, x, y, width, height);
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		Project.renderer.passRender();
		this.gizmosModule.passRender(this.camera);
		
		batch.begin();
		shape.setColor(UColor.background);
		shape.filledRectangle(x, y, width, height);
		shape.setColor(UColor.gray);
		shape.rectangle(x + 2, y + 1, width - 3, height - 3, 1);
		batch.draw(Project.renderer.getTexture(), x + 3, y + 3, width - 6, height - 6);
		batch.draw(this.gizmosModule.getTexture(), x + 3, y + 3, width - 6, height - 6);
		
		shape.filledRectangle(x + 2, y + height - 12, 50, 10);
		foster.setString("Viewport").draw(x + 4, y + height - 3, Align.left);
		if (Game.get.getInput().isCursorCatched()) {
			shape.filledRectangle(x + 52, y + height - 12, 50, 10);
			foster.setString("captured").draw(x + 54, y + height - 3, Align.left);
		}
		
		//shape.setColor(0.12f, 0.12f, 0.12f, 0.5f);
		//shape.filledRectangle(x + 4, y + height - 33 - 132, 30, 132);
		for (int i = this.toolButtons.length, j = 0; i != 0; i--, j++) {
			this.toolButtons[i - 1].setTransforms(x + width - 64 - 62 * j, y + height - 16, 60, 12).render(shape, foster);
		}
		shape.setColor(UColor.overlay);
		shape.filledRectangle(x + width - 186 - 2 + 62 * this.gizmosModule.toolType, y + height - 16, 60, 12);
		batch.end();
	}
	
	public void applyCameraMovement(float dx, float dy) {
		this.lookVector.add(dx / 10, dy / 10);
		this.lookVector.x %= 360;
		this.lookVector.y %= 360;
		if (this.lookVector.y >= 90F) this.lookVector.y = 90F;
		else if (this.lookVector.y <= -90F) this.lookVector.y = -90F;
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {

	}
}
