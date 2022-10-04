package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class EditorPaneMatselModule {
	protected EditorPaneMatsel matsel;
	/** Please, return decreased yOffset. XXX **/
	abstract public int renderModule(Batch batch, ShapeDrawer shape, Foster foster, int yOffset, int x, int width);
	
	abstract public void onAttributeCreationPress(Array<UDAElement> elements);
	abstract public void onDropdownClick(EditorPaneMatsel matsel, String id);
	
	public void onSelect(EditorPaneMatsel matsel, Attribute attribute) { this.matsel = matsel; }
	public abstract boolean acceptAttribute(EditorPaneMatsel matsel, Attribute attribute);
}
