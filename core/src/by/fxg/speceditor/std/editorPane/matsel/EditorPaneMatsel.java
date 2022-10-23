package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;

import by.fxg.speceditor.ui.URenderBlock;

public abstract class EditorPaneMatsel extends URenderBlock {
	public EditorPaneMatsel(String name) {
		super(name);
	}
	
	abstract public Attributes getSelectedAttributes();
	abstract public Attribute getSelectedAttribute();
	abstract public void addAttribute(Attribute attribute);
}
