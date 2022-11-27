package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.ui.STDDropdownArea;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class EditorPaneMatselModule {
	protected EditorPaneMatsel matsel;
	/** Please, return decreased yOffset. XXX docs **/
	abstract public int renderModule(Batch batch, ShapeDrawer shape, Foster foster, int yOffset, int x, int width);
	
	abstract public void onAttributeCreationPress(EditorPaneMatsel matsel, STDDropdownArea area, Array<STDDropdownAreaElement> elements);
	abstract public void onDropdownAreaClick(EditorPaneMatsel matsel, STDDropdownAreaElement element, String id);
	
	public void onSelect(EditorPaneMatsel matsel, Attribute attribute) { this.matsel = matsel; }
	public abstract boolean acceptAttribute(EditorPaneMatsel matsel, Attribute attribute);
}
