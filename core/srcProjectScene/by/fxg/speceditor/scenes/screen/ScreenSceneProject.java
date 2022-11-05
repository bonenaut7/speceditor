package by.fxg.speceditor.scenes.screen;

import java.awt.Desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.scenes.ScenesProject;
import by.fxg.speceditor.screen.BaseScreen;
import by.fxg.speceditor.screen.deprecated.SubscreenExplorer;
import by.fxg.speceditor.screen.deprecated.SubscreenProjectManager;
import by.fxg.speceditor.screen.deprecated.SubscreenViewport;
import by.fxg.speceditor.screen.gui.GuiAbout;
import by.fxg.speceditor.screen.gui.GuiError;
import by.fxg.speceditor.ui.UDropdownClick;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScreenSceneProject extends BaseScreen {
	public ScenesProject project;

	private UDropdownClick dropdownButtonApp, dropdownButtonProject;
	public SubscreenProjectManager subObjectTree;
	public SubscreenExplorer unnamedUselessModule; //TODO do something with it lol, or at least give it a name
	public SubscreenSceneEditor subEditorPane;
	public SubscreenViewport subViewport;
	
	private int timer;
	private long nextBackupTime;
	
	public ScreenSceneProject(ScenesProject project) {
		this.project = project;
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		this.dropdownButtonApp = new UDropdownClick("Editor", 1, height - 21, 90, 20, 20, "Project select", "About");
		this.dropdownButtonProject = new UDropdownClick("Project", 92, height - 21, 75, 20, 20, "Save", "Export as", "Open folder", "Backups");
		
		this.updateDimensions(width, height);
		this.subObjectTree = new SubscreenProjectManager(project.objectTree, this.sObjectTreeX, this.sObjectTreeY, this.sObjectTreeW, this.sObjectTreeH);
		this.unnamedUselessModule = new SubscreenExplorer(this.sUUMX, this.sUUMY, this.sUUMW, this.sUUMH);
		this.subEditorPane = new SubscreenSceneEditor(this, this.sEditorX, this.sEditorY, this.sEditorW, this.sEditorH);
		this.subViewport = new SubscreenViewport(project.renderer, project.objectTree, this.sViewportX, this.sViewportY, this.sViewportW, this.sViewportH);
		
		this.nextBackupTime = System.currentTimeMillis() + project.getBackupInterval() * 1000L;
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (!this.dropdownButtonApp.isDropped() && !this.dropdownButtonProject.isDropped()) {
			this.subEditorPane.update(batch, shape, foster, this.sEditorX, this.sEditorY, this.sEditorW, this.sEditorH);
			this.subObjectTree.update(batch, shape, foster, this.sObjectTreeX, this.sObjectTreeY, this.sObjectTreeW, this.sObjectTreeH);
			this.unnamedUselessModule.update(batch, shape, foster, this.sUUMX, this.sUUMY, this.sUUMW, this.sUUMH);
			this.subViewport.update(batch, shape, foster, this.sViewportX, this.sViewportY, this.sViewportW, this.sViewportH);
		}
		
		if (++this.timer > 59) {
			this.timer = 0;
			if (this.project.isBackupsEnabled() && this.nextBackupTime < System.currentTimeMillis()) {
				this.nextBackupTime = System.currentTimeMillis() + this.project.getBackupInterval() * 1000L;
				this.project.makeBackup();
			}
		}
	}
	
	private int sObjectTreeX, sObjectTreeY, sObjectTreeW, sObjectTreeH, sUUMX, sUUMY, sUUMW, sUUMH;
	private int sEditorX, sEditorY, sEditorW, sEditorH, sViewportX, sViewportY, sViewportW, sViewportH;
	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		this.subViewport.render(batch, shape, foster, this.sViewportX, this.sViewportY, this.sViewportW, this.sViewportH);
		this.subObjectTree.render(batch, shape, foster, this.sObjectTreeX, this.sObjectTreeY, this.sObjectTreeW, this.sObjectTreeH);
		this.subEditorPane.render(batch, shape, foster, this.sEditorX, this.sEditorY, this.sEditorW, this.sEditorH);
		this.unnamedUselessModule.render(batch, shape, foster, this.sUUMX, this.sUUMY, this.sUUMW, this.sUUMH);
		
		batch.begin();
		shape.setColor(0.075f, 0.075f, 0.075f, 1f);
		shape.filledRectangle(0, height - 22, width, 22);
		this.dropdownButtonApp.render(shape, foster);
		this.dropdownButtonProject.render(shape, foster);
		batch.end();
		this.postUpdate();
	}
	
	private void postUpdate() {
		this.dropdownButtonApp.update();
		this.dropdownButtonProject.update();
		if (this.dropdownButtonApp.isPressed()) {
			switch (this.dropdownButtonApp.getVariant()) {
				case 0: break;
				case 1: SpecEditor.get.renderer.currentGui = new GuiAbout(); break;
			}
		}
		if (this.dropdownButtonProject.isPressed()) {
			switch (this.dropdownButtonProject.getVariant()) {
				case 0: {
					this.project.saveConfiguration();
					if (!this.project.saveProject()) {
						SpecEditor.get.renderer.currentGui = new GuiError("Error on saving project", this.project.io.getLastException());
						Utils.logError(this.project.io.getLastException(), "ScreenSceneProject", "Error happened in saving project process");
					}
				} break;
				case 1: {
	//				FileHandle inputFolder = null;
	//				JFrame frame = new JFrame();
	//				frame.setAlwaysOnTop(true);
	//				JFileChooser fileChooser = new JFileChooser();
	//				fileChooser.setMultiSelectionEnabled(false);
	//				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	//				fileChooser.setDialogTitle("Select directory to export scene");
	//				fileChooser.setCurrentDirectory(__$$Project.instance.projectFolder.parent().parent().file());
	//				if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
	//					inputFolder = Gdx.files.absolute(fileChooser.getSelectedFile().getAbsolutePath());
	//					SpecFormatExporter exporter = new SpecFormatExporter();
	//					exporter.export(this.project, inputFolder.child("scene.sfs"), SpecFormatConverter.convertToGraph(this.project, this.subObjectExplorer.objectExplorer, true));
	//				}
					//EXPORT PROJECT
				} break;
				case 2: {
					try { Desktop.getDesktop().open(this.project.getProjectFolder().file()); } catch (Exception e) {}
				} break;
			}
		}
	}

	public void resize(int width, int height) {
		this.dropdownButtonApp.setTransforms(1, height - 21, 90, 20);
		this.dropdownButtonProject.setTransforms(92, height - 21, 75, 20);
		this.updateDimensions(width, height);
		this.subObjectTree.resize(this.sObjectTreeX, this.sObjectTreeY, this.sObjectTreeW, this.sObjectTreeH);
		this.unnamedUselessModule.resize(this.sUUMX, this.sUUMY, this.sUUMW, this.sUUMH);
		this.subEditorPane.resize(this.sEditorX, this.sEditorY, this.sEditorW, this.sEditorH);
		this.subViewport.resize(this.sViewportX, this.sViewportY, this.sViewportW, this.sViewportH);
	}
	
	private void updateDimensions(int width, int height) {
		int leftBlock = width / 6, rightBlock = width / 5;
		this.sObjectTreeX = 1;
		this.sObjectTreeY = 1;
		this.sObjectTreeW = leftBlock - 2;
		this.sObjectTreeH = height - 24;
		this.sUUMX = leftBlock + 1;
		this.sUUMY = 1;
		this.sUUMW = width - leftBlock - rightBlock - 2;
		this.sUUMH = height / 14 - 2;
		this.sEditorX = width - rightBlock + 1;
		this.sEditorY = 1;
		this.sEditorW = rightBlock - 3;
		this.sEditorH = height - 24;
		this.sViewportX = leftBlock + 1;
		this.sViewportY = this.sUUMH + 1;
		this.sViewportW = width - leftBlock - rightBlock - 2;
		this.sViewportH = height - 24 - this.sUUMH;
	}
}
