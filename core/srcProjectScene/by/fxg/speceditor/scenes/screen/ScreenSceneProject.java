package by.fxg.speceditor.scenes.screen;

import java.awt.Desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.scenes.ScenesProject;
import by.fxg.speceditor.screen.BaseScreen;
import by.fxg.speceditor.screen.gui.GuiAbout;
import by.fxg.speceditor.screen.gui.GuiError;
import by.fxg.speceditor.screen.gui.GuiProjectCloseSave;
import by.fxg.speceditor.screen.gui.GuiProjectExitSave;
import by.fxg.speceditor.screen.project.SubscreenExplorer;
import by.fxg.speceditor.screen.project.SubscreenProjectManager;
import by.fxg.speceditor.screen.project.SubscreenViewport;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UDragArea;
import by.fxg.speceditor.ui.UDropdownArea;
import by.fxg.speceditor.ui.UDropdownArea.IUDropdownAreaListener;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScreenSceneProject extends BaseScreen implements IUDropdownAreaListener {
	public ScenesProject project;
	public SubscreenProjectManager subObjectTree;
	public SubscreenExplorer unnamedUselessModule; //TODO do something with it lol, or at least give it a name
	public SubscreenSceneEditor subEditorPane;
	public SubscreenViewport subViewport;
	
	private UButton dropdownButtonApp;
	private UDropdownArea dropdownArea;
	private UDragArea viewObjectTreeExplorer, viewEditorPaneExplorer, viewAssetSelector;
	private int timer;
	private long nextBackupTime;
	
	public ScreenSceneProject(ScenesProject project) {
		this.project = project;
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		this.dropdownButtonApp = new UButton("Editor", 1, height - 16, 90, 15);
		this.dropdownArea = new UDropdownArea(this, 15);
		//this.dropdownButtonApp = new UDropdownClick("Editor", 1, height - 16, 90, 20, 15, "Close project", "About app"); //change to dropdown lol
		//this.dropdownButtonProject = new UDropdownClick("Project", 92, height - 16, 75, 20, 15, "Save", "Export as", "Open folder", "Backups");
		
		this.updateDimensions(width, height);
		this.subObjectTree = new SubscreenProjectManager(project.objectTree, this.sObjectTreeX, this.sObjectTreeY, this.sObjectTreeW, this.sObjectTreeH);
		this.unnamedUselessModule = new SubscreenExplorer(this.sUUMX, this.sUUMY, this.sUUMW, this.sUUMH);
		this.subEditorPane = new SubscreenSceneEditor(this, this.sEditorX, this.sEditorY, this.sEditorW, this.sEditorH);
		this.subViewport = new SubscreenViewport(project.renderer, project.objectTree, this.sViewportX, this.sViewportY, this.sViewportW, this.sViewportH);
		
		this.viewObjectTreeExplorer = new UDragArea() {
			public void onDrag(int start, int value, boolean disfocus) {
				ScreenSceneProject.this.project.setPreference("screen.view.objectTreeExplorer.width", (float)value / (float)Utils.getWidth());
				ScreenSceneProject.this.resize(Utils.getWidth(), Utils.getHeight());
			}
		}.setTransforms(this.sObjectTreeX + this.sObjectTreeW - 1, 2, 3, height - 25).setParameters(50, this.sEditorX - 50, false);
		this.viewEditorPaneExplorer = new UDragArea() {
			public void onDrag(int start, int value, boolean disfocus) {
				ScreenSceneProject.this.project.setPreference("screen.view.editorPanesEditor.width", (float)value / (float)Utils.getWidth());
				ScreenSceneProject.this.resize(Utils.getWidth(), Utils.getHeight());
			}
		}.setTransforms(this.sEditorX - 3, 2, 3, height - 25).setParameters(this.sObjectTreeW + 100, width - 50, false);
		this.viewAssetSelector = new UDragArea() {
			public void onDrag(int start, int value, boolean disfocus) {
				ScreenSceneProject.this.project.setPreference("screen.view.assetSelector.height", (float)value / (float)Utils.getHeight());
				ScreenSceneProject.this.resize(Utils.getWidth(), Utils.getHeight());
			}
		}.setTransforms(this.sUUMX, this.sUUMY + this.sUUMH, this.sUUMW, 2).setParameters(20, height - 75, true);

		this.nextBackupTime = System.currentTimeMillis() + project.getBackupInterval() * 1000L;
	}
	
	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (this.dropdownButtonApp.isPressed()) {
			Array<UDAElement> array = new Array<>();
			array.add(new UDAElement("project.save", "Save project"));
			UDAElement export = new UDAElement("project.export", "Export as...");
			export.addElement(new UDAElement("project.export.specformat", "Specformat"));
			export.addElement(new UDAElement("project.export.json", "json"));
			array.add(export);
			array.add(new UDAElement());
			UDAElement open = new UDAElement("editor.open", "Open...");
			open.addElement(new UDAElement("editor.open.projectFolder", "Project folder"));
			open.addElement(new UDAElement("editor.open.specEditorFolder", "SpecEditor folder"));
			array.add(open);
			array.add(new UDAElement());
			array.add(new UDAElement("editor.closeProject", "Close project"));
			array.add(new UDAElement("editor.about", "About"));
			array.add(new UDAElement("editor.exit", "Exit"));
			this.dropdownArea.set(foster, array).open(1, height - 18);
		}
		if (!this.dropdownArea.isFocused()) {
			this.subEditorPane.update(batch, shape, foster, this.sEditorX, this.sEditorY, this.sEditorW, this.sEditorH);
			this.subObjectTree.update(batch, shape, foster, this.sObjectTreeX, this.sObjectTreeY, this.sObjectTreeW, this.sObjectTreeH);
			this.unnamedUselessModule.update(batch, shape, foster, this.sUUMX, this.sUUMY, this.sUUMW, this.sUUMH);
			this.subViewport.update(batch, shape, foster, this.sViewportX, this.sViewportY, this.sViewportW, this.sViewportH);
		}
		
		this.viewObjectTreeExplorer.update();
		this.viewAssetSelector.update();
		this.viewEditorPaneExplorer.update();
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
		batch.begin();
		shape.setColor(0.075f, 0.075f, 0.075f, 1f);
		shape.filledRectangle(0, height - 17, width, 17);
		this.dropdownButtonApp.render(shape, foster);
		this.viewObjectTreeExplorer.render(shape);
		this.viewAssetSelector.render(shape);
		this.viewEditorPaneExplorer.render(shape);
		batch.end();
		
		this.subViewport.render(batch, shape, foster, this.sViewportX, this.sViewportY, this.sViewportW, this.sViewportH);
		this.subObjectTree.render(batch, shape, foster, this.sObjectTreeX, this.sObjectTreeY, this.sObjectTreeW, this.sObjectTreeH);
		this.subEditorPane.render(batch, shape, foster, this.sEditorX, this.sEditorY, this.sEditorW, this.sEditorH);
		this.unnamedUselessModule.render(batch, shape, foster, this.sUUMX, this.sUUMY, this.sUUMW, this.sUUMH);
		
		batch.begin();
		this.dropdownArea.render(shape, foster);
		batch.end();
	}
	
	public void onDropdownClick(String id) {
		switch (id) {
			case "project.save": {
				this.project.saveConfiguration();
				if (!this.project.saveProject()) {
					SpecEditor.get.renderer.currentGui = new GuiError("Error on saving project", this.project.io.getLastException());
					Utils.logError(this.project.io.getLastException(), "ScreenSceneProject", "Error happened in saving project process");
				}
			} break;
			
//			FileHandle inputFolder = null;
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
			
			case "editor.open.projectFolder": try { Desktop.getDesktop().open(this.project.getProjectFolder().file()); } catch (Exception e) {} break;
			case "editor.open.specEditorFolder": try { Desktop.getDesktop().open(DefaultResources.appFolder.file()); } catch (Exception e) {} break;
			case "editor.closeProject": SpecEditor.get.renderer.currentGui = new GuiProjectCloseSave(); break;
			case "editor.about": SpecEditor.get.renderer.currentGui = new GuiAbout(); break;
			case "editor.exit": SpecEditor.get.renderer.currentGui = new GuiProjectExitSave(); break;
		}
	}
	
	public void resize(int width, int height) {
		this.dropdownButtonApp.setTransforms(1, height - 16, 90, 15);
		this.updateDimensions(width, height);
		this.subObjectTree.resize(this.sObjectTreeX, this.sObjectTreeY, this.sObjectTreeW, this.sObjectTreeH);
		this.unnamedUselessModule.resize(this.sUUMX, this.sUUMY, this.sUUMW, this.sUUMH);
		this.subEditorPane.resize(this.sEditorX, this.sEditorY, this.sEditorW, this.sEditorH);
		this.subViewport.resize(this.sViewportX, this.sViewportY, this.sViewportW, this.sViewportH);
		this.viewObjectTreeExplorer.setTransforms(this.sObjectTreeX + this.sObjectTreeW - 1, 2, 3, height - 20).setParameters(50, this.sEditorX - 50, false);
		this.viewAssetSelector.setTransforms(this.sUUMX, this.sUUMY + this.sUUMH, this.sUUMW, 2).setParameters(20, height - 75, true);
		this.viewEditorPaneExplorer.setTransforms(this.sEditorX - 3, 2, 3, height - 20).setParameters(this.sObjectTreeW + 100, width - 50, false);
	}

	private void updateDimensions(int width, int height) {
		int blockObjectTreeExplorer = (int)(width * this.project.getPreference("screen.view.objectTreeExplorer.width", float.class, 0.15F));
		int blockAssetSelector = (int)(height * this.project.getPreference("screen.view.assetSelector.height", float.class, 0.025F));
		int blockEditorPanesEditor = (int)(width * this.project.getPreference("screen.view.editorPanesEditor.width", float.class, 0.8F));
		this.sObjectTreeX = 1;
		this.sObjectTreeY = 1;
		this.sObjectTreeW = blockObjectTreeExplorer - 2;
		this.sObjectTreeH = height - 19;
		this.sUUMX = blockObjectTreeExplorer + 1;
		this.sUUMY = 1;
		this.sUUMW = width - blockObjectTreeExplorer - (width - blockEditorPanesEditor) - 2;
		this.sUUMH = blockAssetSelector - 2;
		this.sEditorX = blockEditorPanesEditor + 1;
		this.sEditorY = 1;
		this.sEditorW = width - blockEditorPanesEditor - 3;
		this.sEditorH = height - 19;
		this.sViewportX = blockObjectTreeExplorer + 1;
		this.sViewportY = this.sUUMH + 2;
		this.sViewportW = width - blockObjectTreeExplorer - (width - blockEditorPanesEditor) - 2;
		this.sViewportH = height - 20 - this.sUUMH;
	}
}
