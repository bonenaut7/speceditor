package by.fxg.speceditor.scenes.screen;

import java.awt.Desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.scenes.ScenesFormatFileFilter;
import by.fxg.speceditor.scenes.ScenesProject;
import by.fxg.speceditor.scenes.format.ScenesNodeGraphSerializer;
import by.fxg.speceditor.screen.BaseScreen;
import by.fxg.speceditor.screen.gui.GuiAbout;
import by.fxg.speceditor.screen.gui.GuiConfirmation;
import by.fxg.speceditor.screen.gui.GuiError;
import by.fxg.speceditor.screen.gui.GuiProjectCloseSave;
import by.fxg.speceditor.screen.gui.GuiProjectExitSave;
import by.fxg.speceditor.screen.project.SubscreenExplorer;
import by.fxg.speceditor.screen.project.SubscreenProjectManager;
import by.fxg.speceditor.screen.project.SubscreenViewport;
import by.fxg.speceditor.std.ui.ISTDDropdownAreaListener;
import by.fxg.speceditor.std.ui.STDDropdownArea;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.std.viewport.DefaultRenderer;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UDragArea;
import by.fxg.speceditor.utils.SpecFileChooser;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ScreenSceneProject extends BaseScreen implements ISTDDropdownAreaListener {
	public ScenesProject project;
	public SubscreenProjectManager subObjectTree;
	public SubscreenExplorer unnamedUselessModule; //TODO do something with it lol, or at least give it a name
	public SubscreenSceneEditor subEditorPane;
	public SubscreenViewport subViewport;
	
	private UButton dropdownButtonApp;
	private STDDropdownArea dropdownArea;
	private UDragArea viewObjectTreeExplorer, viewEditorPaneExplorer, viewAssetSelector;
	private int timer;
	private long nextBackupTime;
	
	public ScreenSceneProject(ScenesProject project) {
		this.project = project;
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		this.dropdownButtonApp = new UButton("Editor", 1, height - 16, 90, 15);
		this.dropdownArea = new STDDropdownArea(15).setListener(this);
		Array<STDDropdownAreaElement> array = this.dropdownArea.getElementsArrayAsEmpty();
		array.add(STDDropdownAreaElement.button("project.save", "Save project"));
		array.add(STDDropdownAreaElement.subwindow("Export as...")
				.add(STDDropdownAreaElement.button("project.export.specformat", "Specformat"))
				.add(STDDropdownAreaElement.button("project.export.json", "json")));
		array.add(STDDropdownAreaElement.line());
		array.add(STDDropdownAreaElement.subwindow("Open...")
				.add(STDDropdownAreaElement.button("editor.open.projectFolder", "Project folder"))
				.add(STDDropdownAreaElement.button("editor.open.specEditorFolder", "SpecEditor folder")));
		array.add(STDDropdownAreaElement.line());
		array.add(STDDropdownAreaElement.button("editor.closeProject", "Close project"));
		array.add(STDDropdownAreaElement.button("editor.about", "About"));
		array.add(STDDropdownAreaElement.button("editor.exit", "Exit"));
		this.dropdownArea.setElements(array, SpecEditor.fosterNoDraw);
		
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
		this.resize(width, height); //FIXME TODO XXX REMOVE
		if (this.dropdownButtonApp.isPressed()) {
			this.dropdownArea.open(1, height - 18);
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
		shape.update(true);
		shape.setColor(UColor.white);
		shape.filledRectangle(0, 0, width, height);
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
	
	public void onDropdownAreaClick(STDDropdownAreaElement element, String id) {
		switch (id) {
			case "project.save": {
				this.project.saveConfiguration();
				if (!this.project.saveProject()) {
					SpecEditor.get.renderer.currentGui = new GuiError("Error on saving project", this.project.io.getLastException());
					Utils.logError(this.project.io.getLastException(), "ScreenSceneProject", "Error happened in saving project process");
				}
			} break;
			case "project.export.specformat": {
				FileHandle handle = SpecFileChooser.get().setFilter(new ScenesFormatFileFilter()).saveSingle(true, false);
				if (handle != null) {
					if (handle.exists()) {
						SpecEditor.get.renderer.currentGui = new GuiConfirmation("Are you sure you want to overwrite selected file?") {
							public void onConfirm() {
								DefaultRenderer renderer = (DefaultRenderer)project.renderer;
								ScenesNodeGraphSerializer serializer = new ScenesNodeGraphSerializer().setBufferClearColor(renderer.bufferColor).setCameraParameters(renderer.cameraSettings);
								Exception exception = serializer.setEnvironment(renderer.viewportEnvironment).addElementStack(project.objectTree.getStack()).setFile(handle).pack();
								if (exception != null) SpecEditor.get.renderer.currentGui = new GuiError("Error while exporting project", exception);
							}
						};
					} else {
						DefaultRenderer renderer = (DefaultRenderer)this.project.renderer;
						ScenesNodeGraphSerializer serializer = new ScenesNodeGraphSerializer().setBufferClearColor(renderer.bufferColor).setCameraParameters(renderer.cameraSettings);
						Exception exception = serializer.setEnvironment(renderer.viewportEnvironment).addElementStack(this.project.objectTree.getStack()).setFile(handle).pack();
						if (exception != null) SpecEditor.get.renderer.currentGui = new GuiError("Error while exporting project", exception);
					}
				}
			} break;
			case "editor.open.projectFolder": try { Desktop.getDesktop().open(this.project.getProjectFolder().file()); } catch (Exception e) {} break;
			case "editor.open.specEditorFolder": try { Desktop.getDesktop().open(DefaultResources.appFolder.file()); } catch (Exception e) {} break;
			case "editor.closeProject": SpecEditor.get.renderer.currentGui = new GuiProjectCloseSave(); break;
			case "editor.about": SpecEditor.get.renderer.currentGui = new GuiAbout(); break;
			case "editor.exit": SpecEditor.get.renderer.currentGui = new GuiProjectExitSave(); break;
		}
	}
	
	public void resize(int width, int height) {
		int topLine = 17, dragWidth = 4;
		this.dropdownButtonApp.setTransforms(1, height - topLine + 1, 90, 15);
		this.updateDimensions(width, height);
		this.subObjectTree.resize(this.sObjectTreeX, this.sObjectTreeY, this.sObjectTreeW, this.sObjectTreeH);
		this.unnamedUselessModule.resize(this.sUUMX, this.sUUMY, this.sUUMW, this.sUUMH);
		this.subEditorPane.resize(this.sEditorX, this.sEditorY, this.sEditorW, this.sEditorH);
		this.subViewport.resize(this.sViewportX, this.sViewportY, this.sViewportW, this.sViewportH);
		this.viewObjectTreeExplorer.setTransforms(this.sObjectTreeX + this.sObjectTreeW, 0, dragWidth, height - topLine).setParameters(50, this.sEditorX - 50, false);
		this.viewAssetSelector.setTransforms(this.sUUMX, this.sUUMY + this.sUUMH, this.sUUMW, dragWidth).setParameters(20, height - 75, true);
		this.viewEditorPaneExplorer.setTransforms(this.sEditorX - dragWidth, 0, dragWidth, height - topLine).setParameters(this.sObjectTreeW + 100, width - 50, false);
	}

	private void updateDimensions(int width, int height) {
		int topLine = 17, dragWidth = 4, halfDragWidth = dragWidth / 2;
		int DragObjectTree = (int)(width * this.project.getPreference("screen.view.objectTreeExplorer.width", float.class, 0.15F));
		int DragAssetSelector = (int)(height * this.project.getPreference("screen.view.assetSelector.height", float.class, 0.025F));
		int DragEditorPanes = (int)(width * this.project.getPreference("screen.view.editorPanesEditor.width", float.class, 0.8F));
		this.sObjectTreeX = 0;
		this.sObjectTreeY = 0;
		this.sObjectTreeW = DragObjectTree - halfDragWidth;
		this.sObjectTreeH = height - topLine;
		this.sUUMX = DragObjectTree + halfDragWidth;
		this.sUUMY = 0;
		this.sUUMW = DragEditorPanes - DragObjectTree - dragWidth;
		this.sUUMH = DragAssetSelector - halfDragWidth;
		this.sEditorX = DragEditorPanes + halfDragWidth;
		this.sEditorY = 0;
		this.sEditorW = width - DragEditorPanes - halfDragWidth;
		this.sEditorH = height - topLine;
		this.sViewportX = DragObjectTree + halfDragWidth;
		this.sViewportY = DragAssetSelector + halfDragWidth;
		this.sViewportW = DragEditorPanes - DragObjectTree - dragWidth;
		this.sViewportH = height - topLine - DragAssetSelector - halfDragWidth;
	}
}
