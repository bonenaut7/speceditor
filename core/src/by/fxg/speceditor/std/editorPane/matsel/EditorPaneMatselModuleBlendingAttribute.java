package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatselModuleBlendingAttribute extends EditorPaneMatselModule {

	public EditorPaneMatselModuleBlendingAttribute() {

	}
	
	public int renderModule(Batch batch, ShapeDrawer shape, Foster foster, int yOffset, int x, int width) {
		BlendingAttribute attribute = (BlendingAttribute)this.matsel.getCurrentAttribute(this.matsel.getCurrentMaterial());

		return yOffset;
	}

	public void onAttributeCreationPress(Array<UDAElement> elements) {
		elements.add(new UDAElement("default.blending.blending", "Blending"));
	}

	public void onDropdownClick(EditorPaneMatsel matsel, String id) {
		Material material = matsel.getCurrentMaterial();
		if (material != null) {
			switch (id) {
				case "default.blending.blending": material.set(new BlendingAttribute(0.5F)); break;
			}
		}
	}

	
	public boolean acceptAttribute(EditorPaneMatsel matsel, Attribute attribute) {
		return attribute instanceof BlendingAttribute;
	}
}
