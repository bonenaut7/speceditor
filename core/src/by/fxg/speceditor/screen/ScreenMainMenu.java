package by.fxg.speceditor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.ResourceManager;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.project.BasicProject;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.project.ProjectSolver;
import by.fxg.speceditor.screen.project.ScreenCreateProject;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UIOptionSelectSingleList;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScreenMainMenu extends BaseScreen {
	private UButton buttonCreateProject, buttonSelectProject, buttonImportProject, buttonOpenRecentProject, buttonAddonsList, buttonSettings, buttonExit;
	private UIOptionSelectSingleList recentProjectList;
	private Array<BasicProject> recentProjects = new Array<>();
	
	public ScreenMainMenu() {
		this.buttonCreateProject = new UButton("Create project");
		this.buttonSelectProject = new UButton("Select project").setEnabled(false); //TODO [UI] Add Select project gui (selector with descriptions)
		this.buttonImportProject = new UButton("Import project").setEnabled(false); //TODO [UI] Add Import project gui
		this.buttonOpenRecentProject = new UButton("Open recent project");
		this.buttonAddonsList = new UButton("Manage addons").setEnabled(false); //TODO Manage addons screen
		this.buttonSettings = new UButton("Settings").setEnabled(false); //TODO [UI] Add Settings gui
		this.buttonExit = new UButton("Exit");
		this.recentProjectList = new UIOptionSelectSingleList().setTextAlign(Align.left);
		this.resize(Utils.getWidth(), Utils.getHeight());
		
		//init recent projects
		for (FileHandle folder : ProjectManager.INSTANCE.getSpecifiedProjects()) {
			ProjectSolver solver = ProjectManager.INSTANCE.discoverProject(folder);
			BasicProject project = solver != null && solver.canLoadProject(folder) ? solver.preLoadProject(folder) : null;
			if (project != null) {
				this.recentProjects.addAll(project);
				this.recentProjectList.addOptions(project.getProjectName());
			}
		}
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		this.recentProjectList.update();
		this.buttonOpenRecentProject.setEnabled(this.recentProjectList.getSelectedOption() > -1);
		
		if (this.buttonCreateProject.isPressed()) SpecEditor.get.renderer.currentScreen = new ScreenCreateProject(this);
		if (this.buttonSelectProject.isPressed()); //todo
		if (this.buttonImportProject.isPressed()); //todo
		if (this.buttonOpenRecentProject.isPressed() && this.recentProjectList.getSelectedOption() > -1 && this.recentProjectList.getSelectedOption() < this.recentProjects.size) {
			BasicProject project = this.recentProjects.get(this.recentProjectList.getSelectedOption());
			ProjectManager.setCurrentProject(project);
			if (project.loadProject()) {
				project.onProjectOpened();
				if (this.recentProjectList.getSelectedOption() > 0) { //set recent project
					ProjectManager.INSTANCE.setRecentProject(project.getProjectFolder());
				}
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
		
		this.recentProjectList.render(batch, shape, foster);
		
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
		this.recentProjectList.setTransforms(width / 2 + 30, halfHeight - 96, 190, 215, 16);
	}
}
