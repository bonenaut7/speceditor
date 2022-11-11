package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.ui.NumberCursorInputField;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatselModuleIntAttribute extends EditorPaneMatselModule implements ISTDInputFieldListener {
	private int[] cullFaceModes = {-1, GL20.GL_BACK, GL20.GL_FRONT, GL20.GL_FRONT_AND_BACK};
	private String[] cullFaceModesNames = {"Default", "Back", "Front", "Back and Front"};
	private UDropdownSelectSingle cullFaceType;
	private NumberCursorInputField inputField;
	
	public EditorPaneMatselModuleIntAttribute() {
		this.cullFaceType = new UDropdownSelectSingle(15, this.cullFaceModesNames) {
			public UDropdownSelectSingle setVariantSelected(int variant) {
				this.selectedVariant = variant;
				if (EditorPaneMatselModuleIntAttribute.this.matsel.getSelectedAttribute() != null) {
					((IntAttribute)EditorPaneMatselModuleIntAttribute.this.matsel.getSelectedAttribute()).value = EditorPaneMatselModuleIntAttribute.this.cullFaceModes[variant];
				}
				return this;
			}
		};
		this.inputField = (NumberCursorInputField)new NumberCursorInputField().setAllowFullfocus(false).setMaxLength(12).setListener(this, "value");
	}
	
	public int renderModule(Batch batch, ShapeDrawer shape, Foster foster, int yOffset, int x, int width) {
		IntAttribute attribute = (IntAttribute)this.matsel.getSelectedAttribute();
		if (attribute.type == IntAttribute.CullFace) {
			foster.setString("Type:").draw(x, yOffset -= foster.getHeight() + 2, Align.left);
			this.cullFaceType.setTransforms(x + (int)foster.getWidth() + 5, (yOffset -= 4) - (int)foster.getHalfHeight() + 3, width - (int)foster.getWidth() - 5, 14).update();
			this.cullFaceType.render(shape, foster);
			if (this.cullFaceType.isFocused()) yOffset -= this.cullFaceModesNames.length * 15 + 2;
		} else {
			foster.setString("Value:").draw(x, yOffset -= foster.getHeight() + 4, Align.left);
			this.inputField.setTransforms(x + (int)foster.getWidth() + 5, (yOffset -= 3), width - (int)foster.getWidth() - 5, 15).setFoster(foster).update();
			this.inputField.render(batch, shape);
		}
		return yOffset;
	}

	public void onAttributeCreationPress(Array<STDDropdownAreaElement> elements) {
		elements.add(STDDropdownAreaElement.subwindow("Integer")
			.add(STDDropdownAreaElement.button("default.int.cullFace", "Face culling"))	
		);
	}

	public void onDropdownAreaClick(EditorPaneMatsel matsel, STDDropdownAreaElement element, String id) {
		switch (id) {
			case "default.int.cullFace": matsel.addAttribute(IntAttribute.createCullFace(-1)); break;
		}
	}

	public void onSelect(EditorPaneMatsel matsel, Attribute attribute) {
		this.matsel = matsel;
		if (attribute instanceof IntAttribute) {
			if (attribute.type == IntAttribute.CullFace) {
				for (int i = 0; i != this.cullFaceModes.length; i++) {
					if (((IntAttribute)attribute).value == this.cullFaceModes[i]) {
						this.cullFaceType.setVariantSelected(i);
						break;
					}
				}
			} else {
				this.inputField.setNumber(((IntAttribute)attribute).value).dropOffset();
			}
		}
	}
	
	public boolean acceptAttribute(EditorPaneMatsel matsel, Attribute attribute) {
		return attribute instanceof IntAttribute;
	}
	
	public void whileInputFieldFocused(STDInputField inputField, String id) {
		Attribute attribute = this.matsel.getSelectedAttribute();
		if (attribute instanceof IntAttribute) {
			switch (id) {
				case "value": ((IntAttribute)attribute).value = (int)this.inputField.getTextAsNumber(((IntAttribute)attribute).value); break;
			}
		}
	}
	
	public void onInputFieldFocusRemoved(STDInputField inputField, String id) {
		try { inputField.setTextWithPointer(String.valueOf(Float.valueOf(inputField.getText()))).dropOffset(); } catch (Exception e) {}
	}
}
