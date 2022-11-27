package by.fxg.speceditor.std.editorPane.matsel.legacy;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.i18n.I18n;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatsel;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatselModule;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDDropdownArea;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.ui.NumberCursorInputField;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatselModuleFloatAttribute extends EditorPaneMatselModule implements ISTDInputFieldListener {
	private NumberCursorInputField inputField;
	
	public EditorPaneMatselModuleFloatAttribute() {
		this.inputField = (NumberCursorInputField)new NumberCursorInputField().setAllowFullfocus(false).setMaxLength(12).setListener(this, "value");
	}
	
	public int renderModule(Batch batch, ShapeDrawer shape, Foster foster, int yOffset, int x, int width) {
		foster.setString(I18n.get("speceditor.std.matsel.float.value")).draw(x, yOffset -= foster.getHeight() + 4, Align.left);
		this.inputField.setTransforms(x + (int)foster.getWidth() + 5, (yOffset -= 3), width - (int)foster.getWidth() - 5, 15).setFoster(foster).update();
		this.inputField.render(batch, shape);
		return yOffset;
	}

	public void onAttributeCreationPress(EditorPaneMatsel matsel, STDDropdownArea area, Array<STDDropdownAreaElement> elements) {
		elements.add(STDDropdownAreaElement.subwindow(area, I18n.get("speceditor.std.matsel.float.name"))
			.add(STDDropdownAreaElement.button("default.float.alphaTest", I18n.get("speceditor.std.matsel.float.alphaTest.name")))
			.add(STDDropdownAreaElement.button("default.float.shininess", I18n.get("speceditor.std.matsel.float.shininess.name")))
		);
	}

	public void onDropdownAreaClick(EditorPaneMatsel matsel, STDDropdownAreaElement element, String id) {
		switch (id) {
			case "default.float.alphaTest": matsel.addAttribute(FloatAttribute.createAlphaTest(0.5F)); break;
			case "default.float.shininess": matsel.addAttribute(FloatAttribute.createShininess(0.5F)); break;
		}
	}

	public void onSelect(EditorPaneMatsel matsel, Attribute attribute) {
		this.matsel = matsel;
		if (attribute instanceof FloatAttribute) {
			this.inputField.setNumber(((FloatAttribute)attribute).value).dropOffset();
		}
	}
	
	public boolean acceptAttribute(EditorPaneMatsel matsel, Attribute attribute) {
		return attribute instanceof FloatAttribute;
	}
	
	public void whileInputFieldFocused(STDInputField inputField, String id) {
		Attribute attribute = this.matsel.getSelectedAttribute();
		if (attribute instanceof FloatAttribute) {
			switch (id) {
				case "value": ((FloatAttribute)attribute).value = this.inputField.getTextAsNumber(((FloatAttribute)attribute).value); break;
			}
		}
	}
	
	public void onInputFieldFocusRemoved(STDInputField inputField, String id) {
		try { inputField.setTextWithPointer(String.valueOf(Float.valueOf(inputField.getText()))).dropOffset(); } catch (Exception e) {}
	}
}
