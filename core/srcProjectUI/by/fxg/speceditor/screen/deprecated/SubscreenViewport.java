package by.fxg.speceditor.screen.deprecated;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.GInputProcessor;
import by.fxg.speceditor.GInputProcessor.IMouseController;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.GizmosModule;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.viewport.IViewportRenderer;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.utils.BaseSubscreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenViewport extends BaseSubscreen implements IMouseController {
	private IViewportRenderer renderer;
	private SpecObjectTree objectTree;
	public GizmosModule gizmosModule;
	
	private Vector2 lookVector = new Vector2();
	private Vector3 posVector = new Vector3(), tmpVector = new Vector3();
	private Matrix4 tmpMatrix = new Matrix4();
	
	@Deprecated
	private UButton[] toolButtons = new UButton[4];
	
	public SubscreenViewport(IViewportRenderer renderer, SpecObjectTree objectTree, int x, int y, int width, int height) {
		this.renderer = renderer;
		this.objectTree = objectTree;
		
		String[] text = {"None", "Translate", "Rotate", "Scale"};
		for (int i = 0; i != this.toolButtons.length; i++) {
			this.toolButtons[i] = new UButton(text[i], 0, 0, 0, 0);
		}
		
		this.resize(x, y, width, height);
		this.gizmosModule = new GizmosModule(this.renderer.getCamera());
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

			PerspectiveCamera camera = this.renderer.getCamera();
			this.tmpMatrix = new Matrix4().setFromEulerAngles(this.lookVector.x, -this.lookVector.y, 0.0F);
			camera.direction.set(0, 0, 1);
			camera.up.set(0, 1, 0);
			camera.rotate(this.tmpMatrix);
			camera.position.set(this.posVector);
			this.renderer.updateCamera();
			
			this.tmpVector.setZero();
		} else if (input.isMouseDown(2, false) && GDXUtil.isMouseInArea(x, y, width, height) && !Game.get.getInput().isCursorCatched()) {
			 input.setCursorCatched(true);
			 GInputProcessor.mouseController = this;
		} else this.renderer.updateCamera();

		for (int i = 0; i != this.toolButtons.length; i++) {
			if (this.toolButtons[i].isPressed()) {
				if (i == 0) this.gizmosModule.selectedTool = null;
				else this.gizmosModule.selectedTool = GizmoTransformType.values()[i - 1];
				this.gizmosModule.updateSelectorMode(this.objectTree.elementSelector);
			}
		}
		
		this.gizmosModule.update(this, x, y, width, height);
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		this.renderer.render();
		this.gizmosModule.passRender(this.renderer.getCamera());
		
		batch.begin();
		shape.setColor(UColor.background);
		shape.filledRectangle(x, y, width, height);
		shape.setColor(UColor.gray);
		shape.rectangle(x + 1, y + 1, width - 2, height - 2);
		batch.draw(this.renderer.getTexture(), x + 3, y + 3, width - 6, height - 6);
		batch.draw(this.gizmosModule.getRenderPassTexture(), x + 3, y + 3, width - 6, height - 6);
		
		shape.filledRectangle(x + 2, y + height - 12, 50, 10);
		foster.setString("Viewport").draw(x + 4, y + height - 10, Align.left);
		if (Game.get.getInput().isCursorCatched()) {
			shape.filledRectangle(x + 52, y + height - 12, 50, 10);
			foster.setString("captured").draw(x + 54, y + height - 10, Align.left);
		}
		
		shape.setColor(0.12f, 0.12f, 0.12f, 0.5f);
		shape.filledRectangle(x + 4, y + height - 33 - 132, 30, 132);
		for (int i = this.toolButtons.length, j = 0; i != 0; i--, j++) {
			this.toolButtons[i - 1].setTransforms(x + width - 64 - 62 * j, y + height - 16, 60, 12).render(shape, foster);
		}
		shape.setColor(UColor.overlay);
		int offset = this.gizmosModule.selectedTool == null ? -1 : this.gizmosModule.selectedTool.ordinal();
		shape.filledRectangle(x + width - 186 - 2 + 62 * offset, y + height - 16, 60, 12);
		batch.end();
	}
	
	public void onMouseInput(float dx, float dy) {
		this.lookVector.add(dx / 10, dy / 10);
		this.lookVector.x %= 360;
		this.lookVector.y %= 360;
		if (this.lookVector.y >= 90F) this.lookVector.y = 90F;
		else if (this.lookVector.y <= -90F) this.lookVector.y = -90F;
	}

	public void resize(int subX, int subY, int subWidth, int subHeight) {
		PerspectiveCamera camera = this.renderer.getCamera();
		camera.viewportWidth = subWidth - 6;
		camera.viewportHeight = subHeight - 6;
		this.renderer.updateCamera();
	}
}
