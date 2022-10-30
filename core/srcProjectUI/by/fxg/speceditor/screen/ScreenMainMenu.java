package by.fxg.speceditor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.ResourceManager;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.project.BasicProject;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.project.ProjectSolver;
import by.fxg.speceditor.screen.project.ScreenCreateProject;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScreenMainMenu extends BaseScreen {
	private UButton buttonCreateProject, buttonSelectProject, buttonImportProject, buttonOpenRecentProject, buttonAddonsList, buttonSettings, buttonExit;
	
	private float scroll = 0;
	private int selectedRecentProject = -1;
	private Array<BasicProject> recentProjects = new Array<>();
	
	public ScreenMainMenu() {
		this.buttonCreateProject = new UButton("Create project");
		this.buttonSelectProject = new UButton("Select project").setEnabled(false); //TODO Select project (with descriptions)
		this.buttonImportProject = new UButton("Import project").setEnabled(false); //TODO Import project
		this.buttonOpenRecentProject = new UButton("Open recent project");
		this.buttonAddonsList = new UButton("Manage addons").setEnabled(false); //TODO Manage addons screen
		this.buttonSettings = new UButton("Settings").setEnabled(false); //TODO Settings
		this.buttonExit = new UButton("Exit");
		this.resize(Utils.getWidth(), Utils.getHeight());
		
		//init recent projects
		for (FileHandle folder : ProjectManager.INSTANCE.getSpecifiedProjects()) {
			FileHandle projectFile = folder.child("project.ini");
			ProjectSolver solver = ProjectManager.INSTANCE.discoverProject(projectFile);
			BasicProject project = solver != null && solver.canLoadProject(projectFile) ? solver.preLoadProject(projectFile) : null;
			if (project != null) this.recentProjects.add(project);
		}
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (SpecEditor.get.getInput().isMouseDown(0, false) && GDXUtil.isMouseInArea(width / 2 + 29, height / 2 - 125, 189, 214)) {
			this.selectedRecentProject = (height / 2 + 89 - GDXUtil.getMouseY() + (int)this.scroll) / 16;
			if (this.recentProjects.size <= this.selectedRecentProject) this.selectedRecentProject = -1;
		}
		
		if (SpecEditor.get.getInput().isMouseScrolled(true)) {
			this.scroll += 100;
			this.scroll = MathUtils.clamp(this.scroll, 0, Math.max(this.recentProjects.size * 16 - 214, 0));
		} else if (SpecEditor.get.getInput().isMouseScrolled(false)) {
			this.scroll -= 100;
			this.scroll = MathUtils.clamp(this.scroll, 0, Math.max(this.recentProjects.size * 16 - 214, 0));
		}
		
		this.buttonOpenRecentProject.setEnabled(this.selectedRecentProject > -1);
		
		if (this.buttonCreateProject.isPressed()) SpecEditor.get.renderer.currentScreen = new ScreenCreateProject(this);
		if (this.buttonSelectProject.isPressed()); //todo
		if (this.buttonImportProject.isPressed()); //todo
		if (this.buttonOpenRecentProject.isPressed() && this.selectedRecentProject > -1 && this.selectedRecentProject < this.recentProjects.size) {
			BasicProject project = this.recentProjects.get(this.selectedRecentProject);
			if (project.loadProject()) {
				ProjectManager.currentProject = project;
				project.onProjectOpened();
				if (this.selectedRecentProject > 0) ProjectManager.INSTANCE.setRecentProject(project.getProjectFolder());
			}
		}
		if (this.buttonAddonsList.isPressed()); //todo
		if (this.buttonSettings.isPressed()); //todo
		if (this.buttonExit.isPressed()) Gdx.app.exit();
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		int halfHeight = height / 2 - 30;
		foster.setFont(ResourceManager.bigFont).setString("SpecEditor").draw(width / 2, height / 2 + 140);
		foster.setString("OR").draw(width / 2, halfHeight);
		foster.setFont(ResourceManager.smallFont).setString("Select menu item").draw(width / 2 - 125, halfHeight + 135);
		foster.setString("Open recent project").draw(width / 2 + 125, halfHeight + 135);
		shape.setColor(UColor.gray);
		shape.rectangle(width / 2 - 225, halfHeight - 125, 200, 250);
		shape.rectangle(width / 2 + 25, halfHeight - 125, 200, 250);
		
		int rbX = width / 2 + 30, rbW = 189;
		int rbY = halfHeight - 95, rbH = 216;
		shape.rectangle(rbX, rbY - 1, rbW, rbH - 1);
		if (this.recentProjects.size * 16 > rbH) {
			float totalSize = this.recentProjects.size * 16f;
			float scrollSize = Interpolation.linear.apply(10, 214, MathUtils.clamp(214f / totalSize, 0.0F, 1.0F));
			float scrollPosition = Interpolation.linear.apply(214 - scrollSize, 0, 1.0F - MathUtils.clamp((totalSize - 214 - this.scroll) / (totalSize - 214), 0, 1.0F));
			shape.filledRectangle(rbX + rbW + 1, rbY + scrollPosition - 1, 3, scrollSize + 2);
		}
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(rbX - 1, rbY, rbW, rbH)) {
			for (int i = 0; i != this.recentProjects.size; i++) {
				int y = rbY + rbH - 17 - i*16 + (int)this.scroll;
				foster.setString(this.recentProjects.get(i).getProjectName()).draw(rbX + 4, y + 4, Align.left);
				shape.line(rbX, y - 1, rbX + rbW, y);
				if (i == this.selectedRecentProject) {
					shape.setColor(UColor.overlay);
					shape.filledRectangle(rbX, y, rbW, 15);
					shape.setColor(UColor.gray);
				}
				if (GDXUtil.isMouseInArea(rbX - 1, rbY, rbW, rbH) && GDXUtil.isMouseInArea(rbX, y, rbW - 1, 15)) {
					shape.setColor(UColor.overlay);
					shape.filledRectangle(rbX, y, rbW, 15);
					shape.setColor(UColor.gray);
				}
			}
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		
		this.buttonCreateProject.render(shape, foster);
		this.buttonSelectProject.render(shape, foster);
		this.buttonImportProject.render(shape, foster);
		this.buttonAddonsList.render(shape, foster);
		this.buttonSettings.render(shape, foster);
		this.buttonExit.render(shape, foster);
		this.buttonOpenRecentProject.render(shape, foster);
		batch.end();
	}

	public void resize(int width, int height) {
		int halfHeight = height / 2 - 30;
		this.buttonCreateProject.setTransforms(width / 2 - 220, halfHeight + 100, 190, 20);
		this.buttonSelectProject.setTransforms(width / 2 - 220, halfHeight + 60, 190, 20);
		this.buttonImportProject.setTransforms(width / 2 - 220, halfHeight + 20, 190, 20);
		this.buttonAddonsList.setTransforms(width / 2 - 220, halfHeight - 20, 190, 20);
		this.buttonSettings.setTransforms(width / 2 - 220, halfHeight - 60, 190, 20);
		this.buttonExit.setTransforms(width / 2 - 220, halfHeight - 120, 190, 20);
		this.buttonOpenRecentProject.setTransforms(width / 2 + 30, halfHeight - 120, 190, 20);
	}
}
