package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;

import by.fxg.speceditor.ui.URenderBlock;

public abstract class EditorPaneMatsel extends URenderBlock {
	protected IEditorPaneMatselModuleProvider moduleProvider;
	
	
	public EditorPaneMatsel(IEditorPaneMatselModuleProvider moduleProvider, String name) {
		super(name);
		this.moduleProvider = moduleProvider;
	}
	
	abstract public Attributes getSelectedAttributes();
	abstract public Attribute getSelectedAttribute();
	abstract public void addAttribute(Attribute attribute);
}
