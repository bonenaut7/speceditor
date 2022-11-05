package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.NumberCursorInputField;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatselModuleColorAttribute extends EditorPaneMatselModule implements ISTDInputFieldListener {
	private final String[] colors = {"R", "G", "B", "A"};
	private final Color[] fieldColors = {UColor.redblack, UColor.greenblack, UColor.blueblack, UColor.suboverlay};
	private STDInputField[] color = new STDInputField[4];
	
	public EditorPaneMatselModuleColorAttribute() {
		NumberCursorInputField.Builder builder = (NumberCursorInputField.Builder)new NumberCursorInputField.Builder().setAllowFullfocus(false).setMaxLength(12);
		for (int i = 0; i != 4; i++) this.color[i] = builder.setBackgroundColor(this.fieldColors[i]).setListener(this, Utils.format("color", i)).build();
		builder.addToLink(this.color).linkFields();
	}
	
	public int renderModule(Batch batch, ShapeDrawer shape, Foster foster, int yOffset, int x, int width) {
		int sizePerPart = (width - 20 - (int)foster.setString(this.colors[0]).getWidth() * 2) / 2;
		foster.setString("Color:").draw(x, yOffset -= foster.getHeight(), Align.left);
		yOffset -= 19;
		for (int i = 0; i != 2; i++) {
			foster.setString(this.colors[i]).draw(x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
			this.color[i].setTransforms(x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
			this.color[i].render(batch, shape);
		}
		yOffset -= 16;
		for (int i = 0, k = 2; i != 2; i++, k++) {
			foster.setString(this.colors[k]).draw(x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
			this.color[k].setTransforms(x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
			this.color[k].render(batch, shape);
		}
		return yOffset;
	}
	
	public void whileInputFieldFocused(STDInputField inputField, String id) {
		ColorAttribute attribute = (ColorAttribute)this.matsel.getSelectedAttribute();
		switch (id) {
			case "color0": attribute.color.r = this.color[0].getTextAsNumber(attribute.color.r); break;
			case "color1": attribute.color.g = this.color[1].getTextAsNumber(attribute.color.g); break;
			case "color2": attribute.color.b = this.color[2].getTextAsNumber(attribute.color.b); break;
			case "color3": attribute.color.a = this.color[3].getTextAsNumber(attribute.color.a); break;
		}
	}
	
	public void onInputFieldFocusRemoved(STDInputField inputField, String id) {
		try { inputField.setTextWithPointer(String.valueOf(Float.valueOf(inputField.getText()))).dropOffset(); } catch (Exception e) {}
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
		switch (id) {
			case "default.color.diffuse": matsel.addAttribute(ColorAttribute.createDiffuse(1, 1, 1, 1)); break;
			case "default.color.specular": matsel.addAttribute(ColorAttribute.createSpecular(1, 1, 1, 1)); break;
			case "default.color.ambient": matsel.addAttribute(ColorAttribute.createAmbient(1, 1, 1, 1)); break;
			case "default.color.emissive": matsel.addAttribute(ColorAttribute.createEmissive(1, 1, 1, 1)); break;
			case "default.color.reflection": matsel.addAttribute(ColorAttribute.createReflection(1, 1, 1, 1)); break;
			case "default.color.ambientLight": matsel.addAttribute(ColorAttribute.createAmbientLight(1, 1, 1, 1)); break;
			case "default.color.fog": matsel.addAttribute(ColorAttribute.createFog(1, 1, 1, 1)); break;
		}
	}

	public void onSelect(EditorPaneMatsel matsel, Attribute attribute) {
		super.onSelect(matsel, attribute);
		if (attribute != null && attribute instanceof ColorAttribute) {
			ColorAttribute colorAttribute = (ColorAttribute)attribute;
			this.color[0].setTextWithPointer(String.valueOf(colorAttribute.color.r)).dropOffset();
			this.color[1].setTextWithPointer(String.valueOf(colorAttribute.color.g)).dropOffset();
			this.color[2].setTextWithPointer(String.valueOf(colorAttribute.color.b)).dropOffset();
			this.color[3].setTextWithPointer(String.valueOf(colorAttribute.color.a)).dropOffset();
		}
	}
	
	public boolean acceptAttribute(EditorPaneMatsel matsel, Attribute attribute) {
		return attribute instanceof ColorAttribute;
	}
}
