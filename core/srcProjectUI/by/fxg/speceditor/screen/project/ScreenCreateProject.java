package by.fxg.speceditor.screen.project;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.project.ProjectSolver;
import by.fxg.speceditor.screen.BaseScreen;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.ColoredInputField;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.utils.BaseSubscreen;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScreenCreateProject extends BaseScreen implements ISTDInputFieldListener {
	private final BaseScreen parent;
	private Array<ProjectSolver> solversAvailable = new Array<>();
	private Array<ProjectSolver> displaySolvers = new Array<>();
	
	private ColoredInputField solverSortInput;
	private UButton buttonReturn;
	
	private float scroll = 0;
	private int selectedSolverIndex = -1;
	private BaseSubscreen selectedSolverSubscreen = null;
	
	public ScreenCreateProject(BaseScreen parent) {
		this.parent = parent;
		this.solverSortInput = (ColoredInputField)new ColoredInputField().setListener(this, "solverSortingInput");
		this.buttonReturn = new UButton("Return");
		
		for (ProjectSolver solver : ProjectManager.INSTANCE.getSolvers()) {
			if (solver.isAbleToCreateProject()) {
				this.solversAvailable.add(solver);
			}
		}
		this.displaySolvers.addAll(this.solversAvailable);
		this.resize(Utils.getWidth(), Utils.getHeight());
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		this.solverSortInput.update();
		if (this.buttonReturn.isPressed()) {
			Game.get.renderer.currentScreen = this.parent;
		}
		
		if (this.selectedSolverIndex > -1 && this.selectedSolverSubscreen != null) {
			this.selectedSolverSubscreen.update(batch, shape, foster, width / 5 + 1, 2, width - width / 5 - 3, height - 5);
		}
		
		if (Game.get.getInput().isMouseDown(0, false) && GDXUtil.isMouseInArea(5, 22, width / 5 - 12, height - 60)) {
			this.selectedSolverIndex = (height - 40 - GDXUtil.getMouseY() + (int)this.scroll) / 20;
			if (this.displaySolvers.size <= this.selectedSolverIndex) this.selectedSolverIndex = -1;
		}
		
		if (Game.get.getInput().isMouseScrolled(true)) {
			this.scroll += 100;
			this.scroll = MathUtils.clamp(this.scroll, 0, Math.max(this.displaySolvers.size * 20 - (height - 61), 0));
		} else if (Game.get.getInput().isMouseScrolled(false)) {
			this.scroll -= 100;
			this.scroll = MathUtils.clamp(this.scroll, 0, Math.max(this.displaySolvers.size * 20 - (height - 61), 0));
		}
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		float sbWidth = width / 5;
		
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(0, 0, sbWidth, height)) {
			foster.setString("Select project solver").draw(sbWidth / 2, height - 15);
			foster.setString("Name ").draw(10, height - 30, Align.left);
			this.solverSortInput.setFoster(foster).render(batch, shape);
			this.buttonReturn.render(shape, foster);
			
			shape.setColor(UColor.gray);
			shape.rectangle(3, 2, sbWidth - 5, height - 5);
			shape.rectangle(5, 22, sbWidth - 12, height - 61);
			batch.flush();
			int scrollableHeight = height - 59;
			if (PilesosScissorStack.instance.peekScissors(4, 23, sbWidth - 12, scrollableHeight)) {
				for (int i = 0; i != this.displaySolvers.size; i++) {
					int y = scrollableHeight - 20 * i + (int)this.scroll;
					foster.setString(this.displaySolvers.get(i).getDisplayName()).draw(7, y + 7, Align.left);
					shape.line(5, y, sbWidth - 7, y);
					if (this.selectedSolverIndex == i) {
						shape.setColor(UColor.overlay);
						shape.filledRectangle(5, y + 1, sbWidth - 7, 19);
						shape.setColor(UColor.gray);
					}
					if (GDXUtil.isMouseInArea(4, 23, sbWidth - 12, scrollableHeight) && GDXUtil.isMouseInArea(5, y, sbWidth - 7, 19)) {
						shape.setColor(UColor.overlay);
						shape.filledRectangle(5, y + 1, sbWidth - 7, 19);
						shape.setColor(UColor.gray);
					}
				}
				batch.flush();
				PilesosScissorStack.instance.popScissors();
			}
			if (this.displaySolvers.size * 20 > scrollableHeight) {
				float totalSize = this.displaySolvers.size * 20f;
				float maxScroll = totalSize - (height - 61);
				float scrollSize = Interpolation.linear.apply(10, scrollableHeight, MathUtils.clamp((float)scrollableHeight / totalSize, 0.0F, 1.0F));
				float scrollPosition = Interpolation.linear.apply(scrollableHeight - scrollSize, 0, 1.0F - MathUtils.clamp((maxScroll - this.scroll) / maxScroll, 0, 1.0F));
				shape.filledRectangle(sbWidth - 6, 22 + scrollPosition, 2, scrollSize - 1);
			}
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		if (PilesosScissorStack.instance.peekScissors(sbWidth, 0, width - sbWidth, height)) {
			int containerWidth = width - (int)sbWidth - 3;
			shape.rectangle(sbWidth + 1, 2, containerWidth, height - 5);
			if (this.selectedSolverIndex > -1) {
				if (this.selectedSolverSubscreen != null) {
					this.selectedSolverSubscreen.render(batch, shape, foster, (int)sbWidth + 1, 2, containerWidth, height - 5);
				} else foster.setString("This project solver isn't able to create projects").draw(sbWidth + (width - sbWidth) / 2, height / 2);
			} else foster.setString("Choose project solver to create project").draw(sbWidth + (width - sbWidth) / 2, height / 2);
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		batch.end();
	}
	
	public void onInputFieldTextChanged(STDInputField inputField, String id, String textAdded) {
		this.displaySolvers.size = 0;
		for (ProjectSolver solver : this.solversAvailable) {
			if (solver.getDisplayName().startsWith(inputField.getText())) {
				this.displaySolvers.add(solver);
			}
		}
		this.scroll = MathUtils.clamp(this.scroll, 0, Math.max(this.displaySolvers.size * 20 - (Utils.getHeight() - 61), 0));
	}

	public void resize(int width, int height) {
		Foster foster = Game.fosterNoDraw;
		this.solverSortInput.setTransforms(15 + (int)foster.setString("Name").getWidth(), Math.max(0, height - 34), (int)Math.max(0, width / 5 - 25 - foster.getWidth()), 14);
		this.buttonReturn.setTransforms(5, 5, width / 5 - 10, 15);
		
		this.scroll = MathUtils.clamp(this.scroll, 0, Math.max(0, this.displaySolvers.size * 20 - height - 59));
	}
}
