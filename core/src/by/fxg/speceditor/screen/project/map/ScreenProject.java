package by.fxg.speceditor.screen.project.map;

import java.awt.Desktop;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.specformat.editor.SpecFormatConverter;
import by.fxg.pilesos.specformat.editor.SpecFormatExporter;
import by.fxg.speceditor.project.Project;
import by.fxg.speceditor.screen.BaseScreen;
import by.fxg.speceditor.ui.UDropdownClick;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScreenProject extends BaseScreen {
	public UDropdownClick udcApp;
	public UDropdownClick udcProject;
	
	public SubscreenProjectManager subProjectManager;
	public SubscreenExplorer subExplorer;
	public SubscreenEditor subEditor;
	public SubscreenViewport subViewport;

	public Project project;
	
	public ScreenProject(Project project) {
		this.project = project;
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		this.udcApp = new UDropdownClick("Editor", 1, height - 21, 90, 20, 20, "Project select", "About");
		this.udcProject = new UDropdownClick("Project", 92, height - 21, 75, 20, 20, "Save", "Export as", "Open folder", "Backups");
		
		int ddLine = 22, sProjManW = width / 6, sEditorW = width / 5, sExplorerH = height / 4;
		this.subProjectManager = new SubscreenProjectManager(this);
		this.subExplorer = new SubscreenExplorer(this);
		this.subEditor = new SubscreenEditor(this, width - sEditorW, 0, sEditorW, height - ddLine);
		this.subViewport = new SubscreenViewport(this, sProjManW, sExplorerH, width - sProjManW - sEditorW, height - sExplorerH - ddLine);
		
		project.loadProject(this.subProjectManager.objectExplorer);
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (!this.udcApp.isDropped() && !this.udcProject.isDropped()) {
			int ddLine = 22;
			int sProjManW = width / 6;
			int sEditorW = width / 5;
			int sExplorerH = height / 4;
			
			this.subProjectManager.update(batch, shape, foster, 0, 0, sProjManW, height - ddLine);
			this.subEditor.update(batch, shape, foster, width - sEditorW, 0, sEditorW, height - ddLine);
			this.subExplorer.update(batch, shape, foster, sProjManW, 0, width - sProjManW - sEditorW, sExplorerH);
			this.subViewport.update(batch, shape, foster, sProjManW, sExplorerH, width - sProjManW - sEditorW, height - sExplorerH - ddLine);
		}
		this.udcApp.update();
		this.udcProject.update();
		
		if (this.udcApp.isPressed()) {
				switch (this.udcApp.getVariant()) {
				case 0: {
				} break;
				case 1: {
				} break;
			}
		}
		if (this.udcProject.isPressed()) {
				switch (this.udcProject.getVariant()) {
				case 0: {
					this.project.saveHeader();
					this.project.saveProject(this.subProjectManager.objectExplorer);
				} break;
				case 1: {
					FileHandle inputFolder = null;
					JFrame frame = new JFrame();
					frame.setAlwaysOnTop(true);
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setMultiSelectionEnabled(false);
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fileChooser.setDialogTitle("Select directory to export scene");
					fileChooser.setCurrentDirectory(Project.instance.projectFolder.parent().parent().file());
					if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
						inputFolder = Gdx.files.absolute(fileChooser.getSelectedFile().getAbsolutePath());
						SpecFormatExporter exporter = new SpecFormatExporter();
						exporter.export(this.project, inputFolder.child("scene.sfs"), SpecFormatConverter.convertToGraph(this.project, this.subProjectManager.objectExplorer, true));
					}
				} break;
				case 2: {
					try { Desktop.getDesktop().open(Project.instance.projectFolder.file()); } catch (Exception e) {}
				} break;
			}
		}
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		int ddLine = 22;
		int sProjManW = width / 6;
		int sEditorW = width / 5;
		int sExplorerH = height / 4;
		
		this.subViewport.render(batch, shape, foster, sProjManW, sExplorerH, width - sProjManW - sEditorW, height - sExplorerH - ddLine);
		
		this.subProjectManager.render(batch, shape, foster, 0, 0, sProjManW, height - ddLine);
		this.subEditor.render(batch, shape, foster, width - sEditorW, 0, sEditorW, height - ddLine);
		this.subExplorer.render(batch, shape, foster, sProjManW, 0, width - sProjManW - sEditorW, sExplorerH);
		
		batch.begin();
		shape.setColor(0.075f, 0.075f, 0.075f, 1f);
		shape.filledRectangle(0, height - ddLine, width, ddLine);
		this.udcApp.render(shape, foster);
		this.udcProject.render(shape, foster);
		
		batch.end();
	}

	public void resize(int width, int height) {
		int ddLine = 22, sProjManW = width / 6, sEditorW = width / 5, sExplorerH = height / 4;
		this.udcApp.setTransforms(1, height - 21, 90, 20);
		this.udcProject.setTransforms(92, height - 21, 75, 20);
		
		this.subProjectManager.resize(0, 0, sProjManW, height - ddLine);
		this.subEditor.resize(width - sEditorW, 0, sEditorW, height - ddLine);
		this.subExplorer.resize(sProjManW, 0, width - sProjManW - sEditorW, sExplorerH);
		this.subViewport.resize(sProjManW, sExplorerH, width - sProjManW - sEditorW, height - sExplorerH - ddLine);
	}
}