package by.fxg.speceditor.std;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.std.editorPane.EditorPane;
import by.fxg.speceditor.std.editorPane.EditorPaneDecal;
import by.fxg.speceditor.std.editorPane.EditorPaneHitbox;
import by.fxg.speceditor.std.editorPane.EditorPaneHitboxStack;
import by.fxg.speceditor.std.editorPane.EditorPaneLight;
import by.fxg.speceditor.std.editorPane.EditorPaneModel;
import by.fxg.speceditor.std.editorPane.EditorPaneMultipleGizmoTransform;
import by.fxg.speceditor.std.editorPane.EditorPaneStandardRename;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatsel;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatselModule;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatselModuleBlendingAttribute;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatselModuleColorAttribute;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatselModuleDepthTestAttribute;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatselModuleFloatAttribute;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatselModuleTextureAttribute;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;

public class STDManager {
	public static STDManager INSTANCE;
	private Array<EditorPane> editorPanes = new Array<>();
	private Array<EditorPaneMatselModule> editorPaneMatselModules = new Array<>();
	//editor panes sorted by class types
	
	public STDManager() {
		this.initEditorPanes();
		this.initMatselModules();
	}
	
	public STDManager postInit() {
		//Reversing arrays to order it backwards, so default implementations will be in the end of array.
		this.editorPanes.reverse();
		this.editorPaneMatselModules.reverse();
		return this;
	}
	
//=[EditorPanes]===============================================================================================================================================================================================
	private void initEditorPanes() {
		this.editorPanes.addAll(
			new EditorPaneMultipleGizmoTransform(),
				
			new EditorPaneStandardRename()
			
		);
		
		//per-element ones
		this.editorPanes.addAll(
			new EditorPaneModel(),
			new EditorPaneLight(),
			new EditorPaneDecal(),
			new EditorPaneHitboxStack(),
			new EditorPaneHitbox()
			
		);
	}
	
	public STDManager registerEditorPane(EditorPane editorPane) {
		if (!this.editorPanes.contains(editorPane, true)) this.editorPanes.add(editorPane);
		return this;
	}
	
	public boolean replaceEditorPane(Class<? extends EditorPane> editorPaneClass, EditorPane replacementEditorPane) {
		for (int i = 0; i != this.editorPanes.size; i++) {
			if (this.editorPanes.get(i).getClass() == editorPaneClass) {
				this.editorPanes.set(i, replacementEditorPane);
				return true;
			}
		}
		return false;
	}
	
	public EditorPane searchAvailablePane(ITreeElementSelector<?> treeElementSelector) {
		for (EditorPane editorPane : this.editorPanes) {
			if (editorPane.acceptElement(treeElementSelector)) {
				return editorPane;
			}
		}
		return null;
	}
	
//=[EditorPane Matsel Modules]=================================================================================================================================================================================
	private void initMatselModules() {
		this.editorPaneMatselModules.addAll(
			//Remove attribute module,
			new EditorPaneMatselModuleDepthTestAttribute(),
			new EditorPaneMatselModuleBlendingAttribute(),
			new EditorPaneMatselModuleFloatAttribute(),
			new EditorPaneMatselModuleTextureAttribute(),
			new EditorPaneMatselModuleColorAttribute()
		);
	}
	
	public STDManager registerEditorPaneMatselModule(EditorPaneMatselModule editorPaneMatselModule) {
		if (!this.editorPaneMatselModules.contains(editorPaneMatselModule, true)) this.editorPaneMatselModules.add(editorPaneMatselModule);
		return this;
	}
	
	public boolean replaceEditorPaneMatselModule(Class<? extends EditorPaneMatselModule> editorPaneMatselModuleClass, EditorPaneMatselModule replacementEditorPaneMatselModule) {
		for (int i = 0; i != this.editorPaneMatselModules.size; i++) {
			if (this.editorPaneMatselModules.get(i).getClass() == editorPaneMatselModuleClass) {
				this.editorPaneMatselModules.set(i, replacementEditorPaneMatselModule);
				return true;
			}
		}
		return false;
	}
	
	public EditorPaneMatselModule searchAvailablePaneMatselModule(EditorPaneMatsel editorPaneMatsel, Attribute attribute) {
		for (EditorPaneMatselModule editorPaneMatselModule : this.editorPaneMatselModules) {
			if (editorPaneMatselModule.acceptAttribute(editorPaneMatsel, attribute)) {
				return editorPaneMatselModule;
			}
		}
		return null;
	}
	
	public Array<EditorPaneMatselModule> getEditorPaneMatselModules() {
		return this.editorPaneMatselModules;
	}
}
