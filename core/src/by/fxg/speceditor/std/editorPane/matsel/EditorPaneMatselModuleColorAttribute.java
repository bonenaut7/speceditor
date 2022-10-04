package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatselModuleColorAttribute extends EditorPaneMatselModule {

	public EditorPaneMatselModuleColorAttribute() {

	}
	
	public int renderModule(Batch batch, ShapeDrawer shape, Foster foster, int yOffset, int x, int width) {
		ColorAttribute attribute = (ColorAttribute)this.matsel.getCurrentAttribute(this.matsel.getCurrentMaterial());

		return yOffset;
	}

	public void onAttributeCreationPress(Array<UDAElement> elements) {
		UDAElement colorAttributes = new UDAElement("", "Color");
		colorAttributes.addElement(new UDAElement("default.color.diffuse", "Diffuse"));
		colorAttributes.addElement(new UDAElement("default.color.specular", "Specular"));
		colorAttributes.addElement(new UDAElement("default.color.ambient", "Ambient"));
		colorAttributes.addElement(new UDAElement("default.color.emissive", "Emissive"));
		colorAttributes.addElement(new UDAElement("default.color.reflection", "Reflection"));
		colorAttributes.addElement(new UDAElement("default.color.ambientLight", "Ambient Light"));
		colorAttributes.addElement(new UDAElement("default.color.fog", "Fog"));
		elements.add(colorAttributes);
	}

	public void onDropdownClick(EditorPaneMatsel matsel, String id) {
		Material material = matsel.getCurrentMaterial();
		if (material != null) {
			switch (id) {
				case "default.color.diffuse": material.set(ColorAttribute.createDiffuse(1, 1, 1, 1)); break;
				case "default.color.specular": material.set(ColorAttribute.createSpecular(1, 1, 1, 1)); break;
				case "default.color.ambient": material.set(ColorAttribute.createAmbient(1, 1, 1, 1)); break;
				case "default.color.emissive": material.set(ColorAttribute.createEmissive(1, 1, 1, 1)); break;
				case "default.color.reflection": material.set(ColorAttribute.createReflection(1, 1, 1, 1)); break;
				case "default.color.ambientLight": material.set(ColorAttribute.createAmbientLight(1, 1, 1, 1)); break;
				case "default.color.fog": material.set(ColorAttribute.createFog(1, 1, 1, 1)); break;
			}
		}
	}

	
	public boolean acceptAttribute(EditorPaneMatsel matsel, Attribute attribute) {
		return attribute instanceof ColorAttribute;
	}
}
