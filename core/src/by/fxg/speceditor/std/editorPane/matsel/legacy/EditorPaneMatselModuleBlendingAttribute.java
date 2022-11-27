package by.fxg.speceditor.std.editorPane.matsel.legacy;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
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
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatselModuleBlendingAttribute extends EditorPaneMatselModule implements ISTDInputFieldListener {
	private final int[] 
		blendSrcModes = { GL20.GL_ZERO, GL20.GL_ONE, GL20.GL_SRC_COLOR, GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA, GL20.GL_CONSTANT_COLOR, GL20.GL_CONSTANT_ALPHA }, 
		blendDstModes = { GL20.GL_ZERO, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_ONE_MINUS_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE_MINUS_CONSTANT_COLOR, GL20.GL_ONE_MINUS_CONSTANT_ALPHA };
	private final String[] 
		blendSrcModesNames = { "Zero", "One", "SRC Color", "DST Color", "SRC Alpha", "DST Alpha", "Constant color", "Constant alpha" }, 
		blendDstModesNames = { "Zero", "One", "1 minus SRC Color", "1 minus DST Color", "1 minus SRC Alpha", "1 minus DST Alpha", "1 minus Constant color", "1 minus Constant alpha" };
	
	private NumberCursorInputField opacity;
	private UDropdownSelectSingle blendSrc, blendDst;
	private UCheckbox blended;
	
	public EditorPaneMatselModuleBlendingAttribute() {
		this.opacity = (NumberCursorInputField)new NumberCursorInputField().setAllowFullfocus(false).setMaxLength(12).setListener(this, "opacity");
		this.blendSrc = new UDropdownSelectSingle(15, this.blendSrcModesNames) {
			public UDropdownSelectSingle setVariantSelected(int variant) {
				this.selectedVariant = variant;
				if (EditorPaneMatselModuleBlendingAttribute.this.matsel.getSelectedAttribute() != null) {
					((BlendingAttribute)EditorPaneMatselModuleBlendingAttribute.this.matsel.getSelectedAttribute()).sourceFunction = EditorPaneMatselModuleBlendingAttribute.this.blendSrcModes[variant];
				}
				return this;
			}
		};
		this.blendDst = new UDropdownSelectSingle(15, this.blendDstModesNames) {
			public UDropdownSelectSingle setVariantSelected(int variant) {
				this.selectedVariant = variant;
				if (EditorPaneMatselModuleBlendingAttribute.this.matsel.getSelectedAttribute() != null) {
					((BlendingAttribute)EditorPaneMatselModuleBlendingAttribute.this.matsel.getSelectedAttribute()).destFunction = EditorPaneMatselModuleBlendingAttribute.this.blendDstModes[variant];
				}
				return this;
			}
		};
		this.blended = new UCheckbox();
	}
	
	public int renderModule(Batch batch, ShapeDrawer shape, Foster foster, int yOffset, int x, int width) {
		BlendingAttribute attribute = (BlendingAttribute)this.matsel.getSelectedAttribute();
		foster.setString(I18n.get("speceditor.std.matsel.blending.src")).draw(x, yOffset -= foster.getHeight() + 2, Align.left);
		this.blendSrc.setTransforms(x + (int)foster.getWidth() + 5, (yOffset -= 1) - (int)foster.getHalfHeight(), width - (int)foster.getWidth() - 5, 14).update();
		this.blendSrc.render(shape, foster);
		if (this.blendSrc.isFocused()) yOffset -= this.blendSrcModesNames.length * 15 + 2;
		foster.setString(I18n.get("speceditor.std.matsel.blending.dest")).draw(x, yOffset -= foster.getHeight() + 9, Align.left);
		this.blendDst.setTransforms(x + (int)foster.getWidth() + 5, (yOffset -= 1) - (int)foster.getHalfHeight(), width - (int)foster.getWidth() - 5, 14).update();
		this.blendDst.render(shape, foster);
		if (this.blendDst.isFocused()) yOffset -= this.blendSrcModesNames.length * 15 + 2;
		foster.setString(I18n.get("speceditor.std.matsel.blending.opacity")).draw(x, yOffset -= foster.getHeight() + 10, Align.left);
		this.opacity.setTransforms(x + (int)foster.getWidth() + 5, (yOffset -= 1) - 3, width - (int)foster.getWidth() - 5, 15).setFoster(foster).update();
		this.opacity.render(batch, shape);
		foster.setString(I18n.get("speceditor.std.matsel.blending.blended")).draw(x, yOffset -= foster.getHeight() + 10, Align.left);
		this.blended.setTransforms(x + foster.getWidth() + 5, yOffset -= 2, 12, 12).setValue(attribute.blended).update();
		this.blended.render(shape);
		attribute.blended = this.blended.getValue();
		return yOffset;
	}

	public void onAttributeCreationPress(EditorPaneMatsel matsel, STDDropdownArea area, Array<STDDropdownAreaElement> elements) {
		elements.add(STDDropdownAreaElement.button("default.blending.blending", I18n.get("speceditor.std.matsel.blending.blending.name")));
	}

	public void onDropdownAreaClick(EditorPaneMatsel matsel, STDDropdownAreaElement element, String id) {
		switch (id) {
			case "default.blending.blending": matsel.addAttribute(new BlendingAttribute(true, 0.5F)); break;
		}
	}

	public void onSelect(EditorPaneMatsel matsel, Attribute attribute) {
		this.matsel = matsel;
		if (attribute instanceof BlendingAttribute) {
			BlendingAttribute blendingAttribute = (BlendingAttribute)attribute;
			for (int i = 0; i != this.blendSrcModes.length; i++) {
				if (blendingAttribute.sourceFunction == this.blendSrcModes[i]) {
					this.blendSrc.setVariantSelected(i);
					break;
				}
			}
			for (int i = 0; i != this.blendDstModes.length; i++) {
				if (blendingAttribute.destFunction == this.blendDstModes[i]) {
					this.blendDst.setVariantSelected(i);
					break;
				}
			}
			this.opacity.setNumber(blendingAttribute.opacity).dropOffset();
			this.blended.setValue(blendingAttribute.blended);
		}
	}
	
	public boolean acceptAttribute(EditorPaneMatsel matsel, Attribute attribute) {
		return attribute instanceof BlendingAttribute;
	}
	
	public void whileInputFieldFocused(STDInputField inputField, String id) {
		Attribute attribute = this.matsel.getSelectedAttribute();
		if (attribute instanceof BlendingAttribute) {
			switch (id) {
				case "opacity": ((BlendingAttribute)attribute).opacity = this.opacity.getTextAsNumber(((BlendingAttribute)attribute).opacity); break;
			}
		}
	}
	
	public void onInputFieldFocusRemoved(STDInputField inputField, String id) {
		try { inputField.setTextWithPointer(String.valueOf(Float.valueOf(inputField.getText()))).dropOffset(); } catch (Exception e) {}
	}
}
