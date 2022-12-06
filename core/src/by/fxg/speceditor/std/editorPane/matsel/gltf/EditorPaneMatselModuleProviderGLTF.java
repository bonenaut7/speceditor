package by.fxg.speceditor.std.editorPane.matsel.gltf;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatsel;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatselModule;
import by.fxg.speceditor.std.editorPane.matsel.IEditorPaneMatselModuleProvider;

public class EditorPaneMatselModuleProviderGLTF implements IEditorPaneMatselModuleProvider {
	protected Array<Class<? extends Attribute>> bannedAttributes = new Array<>();
	protected Array<EditorPaneMatselModule> modules = new Array<>();
	
	@SafeVarargs
	public EditorPaneMatselModuleProviderGLTF(Class<? extends Attribute>... bannedAttributes) {
		this.bannedAttributes.addAll(bannedAttributes);
		this.modules.addAll(
			//new EditorPaneMatselModuleColorAttribute(),
			//new EditorPaneMatselModuleTextureAttribute(),
			//new EditorPaneMatselModuleFloatAttribute(),
			//new EditorPaneMatselModuleIntAttribute(),
			//new EditorPaneMatselModuleBlendingAttribute(),
			//new EditorPaneMatselModuleDepthTestAttribute()
		);
	}
	
	public Array<EditorPaneMatselModule> getModules() {
		return this.modules;
	}

	public EditorPaneMatselModule getModuleForAttribute(EditorPaneMatsel editorPaneMatsel, Attribute attribute) {
		for (int i = 0; i != this.modules.size; i++) {
			if (this.modules.get(i).acceptAttribute(editorPaneMatsel, attribute)) {
				return this.modules.get(i);
			}
		}
		return null;
	}
	
	public boolean isAttributeAllowed(EditorPaneMatsel editorPaneMatsel, Attribute attribute) {
		return !this.bannedAttributes.contains(attribute.getClass(), true);
	}
}
