package by.fxg.speceditor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.project.Project;
import by.fxg.speceditor.screen.project.map.ScreenProject;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UHoldButton;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScreenSelectProject extends BaseScreen {
	public Array<Project> projects = new Array<>();
	public Project selectedProject = null;
	public int scroll = 0;
	
	public UButton bCreate, bOpen;
	public UHoldButton bDelete;
	
	public ScreenSelectProject() {
		FileHandle[] candidates = Gdx.files.internal("spec/projects/").list();
		for (FileHandle candidate : candidates) {
			if (candidate.isDirectory()) {
				Project project = Project.discoverProject(candidate);
				if (project != null) this.projects.add(project);
			}
		}
		
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		int ix = width / 2 + 200 / 2 + 10, iy = height / 2 - 100 / 2 + 15;
		this.bCreate = new UButton("Create", width / 2 - 25, height / 2 - 135, 50, 20);
		this.bOpen = new UButton("Open", ix + 5, iy + 5, 50, 20).setEnabled(false);
		this.bDelete = new UHoldButton("Delete", UHoldButton.NO_KEY, 60, ix + 60, iy + 5, 50, 20).setColor(UColor.redblack).setEnabled(false);
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		this.bDelete.update();
		
		if (this.bCreate.isPressed()) {
			Game.get.renderer.currentScreen = new ScreenCreateProject();
		}
		if (this.selectedProject != null && this.bOpen.isPressed()) {
			Project.instance = this.selectedProject;
			Game.get.renderer.currentScreen = new ScreenProject(this.selectedProject);
		}
		if (this.selectedProject != null && this.bDelete.isPressed()) {
			FileHandle origin = Gdx.files.absolute(this.selectedProject.projectFolder.file().getAbsolutePath());
			if (origin.exists()) {
				FileHandle dest = Gdx.files.absolute(Gdx.files.internal("spec/deletedprojects/").file().getAbsolutePath());
				origin.moveTo(dest);
				origin.deleteDirectory();
			}
			this.projects.removeValue(this.selectedProject, true);
			this.selectedProject = null;
			this.bOpen.setEnabled(false);
			this.bDelete.setEnabled(false);
		}
		
		if (Game.get.getInput().isMouseScrolled(true) && this.scroll + 16 < this.projects.size) this.scroll++;
		else if (Game.get.getInput().isMouseScrolled(false) && this.scroll > 0) this.scroll--;
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
			foster.setString(this.selectedProject.projectName).draw(ix + 5, iy + isy - 5, Align.left);
			foster.setString(String.format("Last save: %s", this.selectedProject.lastSaveDate)).draw(ix + 5, iy + isy - 15, Align.left);
			shape.rectangle(ix, iy, Math.max(isx, foster.getWidth() + 10), isy);
			if (this.selectedProject.backupSaving) foster.setString("Backups: enabled").draw(ix + 5, iy + isy - 25, Align.left);
			else foster.setString("Backups: disabled").draw(ix + 5, iy + isy - 25, Align.left);
			foster.setString(String.format("Dir: %s", this.selectedProject.projectFolder.toString())).draw(ix + 5, iy + isy - 35, Align.left);
		} else shape.rectangle(ix, iy, isx, isy);
		
		foster.setString("Select project").draw(px + psx / 2, sy + psy + 20);
		for (int i = 0; i != 16; i++) {
			int idx = this.scroll + i;
			if (this.projects.size > idx) {
				Project project = this.projects.get(idx);
				if (project != null) {
					shape.setColor(0.75f, 0.75f, 0.75f, 1f);
					shape.rectangle(px, sy - psy * i, psx, psy);
					if (this.selectedProject == project) {
						shape.setColor(0.25f, 0.25f, 0.25f, 1f);
						shape.filledRectangle(px, sy - psy * i + 1, psx - 1, psy - 1);
					}
					foster.setString(project.projectName).draw(px + 5, sy - psy * i + 12, Align.left);
					
					if (Game.get.getInput().isMouseDown(0, false) && GDXUtil.isMouseInArea(px, sy - psy * i, psx, psy)) {
						this.selectedProject = project;
						this.bOpen.setEnabled(true);
						this.bDelete.setEnabled(true);
					}
				}
			}
		}
		
		this.bCreate.render(shape, foster);
		this.bOpen.render(shape, foster);
		this.bDelete.render(shape, foster);
		batch.end();
	}

	public void resize(int width, int height) {
		int x = width / 2 + 200 / 2 + 10, y = height / 2 - 100 / 2 + 15;
		this.bCreate.setTransforms(width / 2 - 25, height / 2 - 135, 50, 20);
		this.bOpen.setTransforms(x + 5, y + 5, 50, 20);
		this.bDelete.setTransforms(x + 60, y + 5, 50, 20);
	}
}
