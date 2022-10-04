package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatselModuleFloatAttribute extends EditorPaneMatselModule {

	public EditorPaneMatselModuleFloatAttribute() {

	}
	
	public int renderModule(Batch batch, ShapeDrawer shape, Foster foster, int yOffset, int x, int width) {
		FloatAttribute attribute = (FloatAttribute)this.matsel.getCurrentAttribute(this.matsel.getCurrentMaterial());

		return yOffset;
	}

	public void onAttributeCreationPress(Array<UDAElement> elements) {
		UDAElement floatAttributes = new UDAElement("", "Float");
		floatAttributes.addElement(new UDAElement("default.float.alphaTest", "Alpha Testing"));
		floatAttributes.addElement(new UDAElement("default.float.shininess", "Shininess"));
		elements.add(floatAttributes);
	}

	public void onDropdownClick(EditorPaneMatsel matsel, String id) {
		Material material = matsel.getCurrentMaterial();
		if (material != null) {
			switch (id) {
				case "default.float.alphaTest": material.set(FloatAttribute.createAlphaTest(0.5F)); break;
				case "default.float.shininess": material.set(FloatAttribute.createShininess(0.5F)); break;
			}
		}
	}

	
	public boolean acceptAttribute(EditorPaneMatsel matsel, Attribute attribute) {
		return attribute instanceof FloatAttribute;
	}
}
