package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.Array;

public interface IEditorPaneMatselModuleProvider {
	Array<EditorPaneMatselModule> getModules();
	EditorPaneMatselModule getModuleForAttribute(EditorPaneMatsel editorPaneMatsel, Attribute attribute);
	default boolean isAttributeAllowed(EditorPaneMatsel editorPaneMatsel, Attribute attribute) { return true; }
}
