package by.fxg.speceditor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.project.BasicProject;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.project.ProjectSolver;
import by.fxg.speceditor.screen.project.ScreenCreateProject;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UHoldButton;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

/** (TODO) This one sucks, needed to be totally rewritten, low quality and no gui adaptation code **/
public class ScreenSelectProject extends BaseScreen {
	public Array<BasicProject> projects = new Array<>();
	public BasicProject selectedProject = null;
	public int scroll = 0;
	
	public UButton buttonCreateProject, buttonOpenProject;
	public UHoldButton buttonDeleteProject;
	
	public ScreenSelectProject() {
		FileHandle[] candidates = Gdx.files.internal("spec/projects/").list();
		for (FileHandle candidate : candidates) {
			FileHandle projectConfig = candidate.child("project.ini");
			if (candidate.isDirectory() && projectConfig.exists() && !projectConfig.isDirectory()) {
				ProjectSolver solver = ProjectManager.INSTANCE.discoverProject(projectConfig);
				if (solver != null) this.projects.add(solver.preLoadProject(candidate));
			}
		}
		Utils.logDebug("[ScreenSelectProject] Projects discovered to loading: ", this.projects.size, "; candidates: ", candidates.length);
		
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		int ix = width / 2 + 200 / 2 + 10, iy = height / 2 - 100 / 2 + 15;
		this.buttonCreateProject = new UButton("Create", width / 2 - 25, height / 2 - 135, 50, 20);
		this.buttonOpenProject = new UButton("Open", ix + 5, iy + 5, 50, 20).setEnabled(false);
		this.buttonDeleteProject = new UHoldButton("Delete", UHoldButton.NO_KEY, 60, ix + 60, iy + 5, 50, 20).setColor(UColor.redblack).setEnabled(false);
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		this.buttonDeleteProject.update();
		
		if (this.buttonCreateProject.isPressed()) {
			SpecEditor.get.renderer.currentScreen = new ScreenCreateProject(this);
		}
		if (this.selectedProject != null && this.buttonOpenProject.isPressed()) {
			ProjectManager.currentProject = this.selectedProject;
			if (this.selectedProject.loadProject()) {
				this.selectedProject.onProjectOpened();
			}
		}
		if (this.selectedProject != null && this.buttonDeleteProject.isPressed()) {
			FileHandle origin = Gdx.files.absolute(this.selectedProject.getProjectFolder().file().getAbsolutePath());
			if (origin.exists()) {
				FileHandle dest = Gdx.files.absolute(Gdx.files.internal("spec/deletedprojects/").file().getAbsolutePath());
				origin.moveTo(dest);
				origin.deleteDirectory();
			}
			this.projects.removeValue(this.selectedProject, true);
			this.selectedProject = null;
			this.buttonOpenProject.setEnabled(false);
			this.buttonDeleteProject.setEnabled(false);
		}
		
		if (SpecEditor.get.getInput().isMouseScrolled(true) && this.scroll + 16 < this.projects.size) this.scroll++;
		else if (SpecEditor.get.getInput().isMouseScrolled(false) && this.scroll > 0) this.scroll--;
		if (this.projects.size <= this.scroll + 15) this.scroll = Math.max(0, this.projects.size - 16);
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		int psx = 200, psy = 15;
		int px = width / 2 - psx / 2;
		
		int sy = height / 2 + psy * 8;
		shape.setColor(0.75f, 0.75f, 0.75f, 1f);
		shape.rectangle(px - 4, sy - psy * 15 - 4, psx + 8, psy * 16 + 8);
		
		int isx = 200, isy = 100;
		int ix = width / 2 + psx / 2 + 10, iy = height / 2 - isy / 2 + psy;
		if (this.selectedProject != null) {
			int textHeight = iy + isy - 5;
			foster.setString(this.selectedProject.getProjectName()).draw(ix + 5, textHeight -= foster.getHeight(), Align.left);
			foster.setString(String.format("Type: %s", this.selectedProject.getProjectType())).draw(ix + 5, textHeight -= foster.getHeight() + 3, Align.left);
			foster.setString(String.format("Last save: %s", this.selectedProject.getSaveDate())).draw(ix + 5, textHeight -= foster.getHeight() + 3, Align.left);
			shape.rectangle(ix, iy, Math.max(isx, foster.getWidth() + 10), isy);
			if (this.selectedProject.isBackupsEnabled()) foster.setString("Backups: enabled").draw(ix + 5, textHeight -= foster.getHeight() + 3, Align.left);
			else foster.setString("Backups: disabled").draw(ix + 5, iy + isy - 35, Align.left);
			foster.setString(String.format("Dir: %s", this.selectedProject.getProjectFolder().toString())).draw(ix + 5, textHeight -= foster.getHeight() + 3, Align.left);
		} else shape.rectangle(ix, iy, isx, isy);
		
		foster.setString("Select project").draw(px + psx / 2, sy + psy + 20);
		for (int i = 0; i != 16; i++) {
			int idx = this.scroll + i;
			if (this.projects.size > idx) {
				BasicProject project = this.projects.get(idx);
				if (project != null) {
					shape.setColor(0.75f, 0.75f, 0.75f, 1f);
					shape.rectangle(px, sy - psy * i, psx, psy);
					if (this.selectedProject == project) {
						shape.setColor(0.25f, 0.25f, 0.25f, 1f);
						shape.filledRectangle(px, sy - psy * i + 1, psx - 1, psy - 1);
					}
					foster.setString("[" + project.getProjectType() + "] " + project.getProjectName()).draw(px + 5, sy - psy * i + 5, Align.left);
					
					if (SpecEditor.get.getInput().isMouseDown(0, false) && GDXUtil.isMouseInArea(px, sy - psy * i, psx, psy)) {
						this.selectedProject = project;
						this.buttonOpenProject.setEnabled(true);
						this.buttonDeleteProject.setEnabled(true);
					}
				}
			}
		}
		
		this.buttonCreateProject.render(shape, foster);
		this.buttonOpenProject.render(shape, foster);
		this.buttonDeleteProject.render(shape, foster);
		batch.end();
	}

	public void resize(int width, int height) {
		int x = width / 2 + 200 / 2 + 10, y = height / 2 - 100 / 2 + 15;
		this.buttonCreateProject.setTransforms(width / 2 - 25, height / 2 - 135, 50, 20);
		this.buttonOpenProject.setTransforms(x + 5, y + 5, 50, 20);
		this.buttonDeleteProject.setTransforms(x + 60, y + 5, 50, 20);
	}
}
