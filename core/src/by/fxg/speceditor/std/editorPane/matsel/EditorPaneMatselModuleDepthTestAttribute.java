package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.ui.NumberCursorInputField;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatselModuleDepthTestAttribute extends EditorPaneMatselModule implements ISTDInputFieldListener {
	private final int[] depthFuncModes = {0, GL20.GL_LESS, GL20.GL_EQUAL, GL20.GL_GREATER, GL20.GL_LEQUAL, GL20.GL_GEQUAL, GL20.GL_NOTEQUAL, GL20.GL_NEVER, GL20.GL_ALWAYS};
	private final String[] depthFuncNames = {"Disable", "Less", "Equal", "Greater", "Less or Equal", "Greater or Equal", "Not equal", "Never", "Always"};
	private UDropdownSelectSingle depthFunc;
	private NumberCursorInputField[] depthRange = new NumberCursorInputField[2];
	private UCheckbox depthMask;
	
	public EditorPaneMatselModuleDepthTestAttribute() {
		this.depthFunc = new UDropdownSelectSingle(15, this.depthFuncNames) {
			public UDropdownSelectSingle setSelectedVariant(int variant) {
				this.selectedVariant = variant;
				if (EditorPaneMatselModuleDepthTestAttribute.this.matsel.getSelectedAttribute() != null) {
					((DepthTestAttribute)EditorPaneMatselModuleDepthTestAttribute.this.matsel.getSelectedAttribute()).depthFunc = EditorPaneMatselModuleDepthTestAttribute.this.depthFuncModes[variant];
				}
				return this;
			}
		};
		NumberCursorInputField.Builder builder = (NumberCursorInputField.Builder)new NumberCursorInputField.Builder().setAllowFullfocus(false).setMaxLength(12);
		this.depthRange[0] = (NumberCursorInputField)builder.setListener(this, "rangeFar").build();
		this.depthRange[1] = (NumberCursorInputField)builder.setListener(this, "rangeNear").build();
		builder.addToLink(this.depthRange).linkFields();
		this.depthMask = new UCheckbox();
	}
	
	public int renderModule(Batch batch, ShapeDrawer shape, Foster foster, int yOffset, int x, int width) {
		DepthTestAttribute attribute = (DepthTestAttribute)this.matsel.getSelectedAttribute();
		foster.setString("Depth Func:").draw(x, yOffset -= foster.getHeight() + 1, Align.left);
		this.depthFunc.setTransforms(x + (int)foster.getWidth() + 5, yOffset - (int)foster.getHalfHeight(), width - (int)foster.getWidth() - 5, 12).update();
		this.depthFunc.render(shape, foster);
		if (this.depthFunc.isDropped()) yOffset -= this.depthFuncModes.length * 15 + 2;
		foster.setString("Far range:").draw(x, yOffset -= foster.getHeight() + 10, Align.left);
		this.depthRange[0].setTransforms(x + (int)foster.getWidth() + 5, (yOffset -= 1) - 3, width - (int)foster.getWidth() - 5, 15).setFoster(foster).update();
		this.depthRange[0].render(batch, shape);
		foster.setString("Near range:").draw(x, yOffset -= foster.getHeight() + 10, Align.left);
		this.depthRange[1].setTransforms(x + (int)foster.getWidth() + 5, (yOffset -= 1) - 3, width - (int)foster.getWidth() - 5, 15).setFoster(foster).update();
		this.depthRange[1].render(batch, shape);
		foster.setString("Depth mask:").draw(x, yOffset -= foster.getHeight() + 10, Align.left);
		this.depthMask.setTransforms(x + foster.getWidth() + 5, yOffset -= 2, 12, 12).setValue(attribute.depthMask).update();
		this.depthMask.render(shape);
		attribute.depthMask = this.depthMask.getValue();
		return yOffset;
	}

	public void onAttributeCreationPress(Array<UDAElement> elements) {
		elements.add(new UDAElement("", "Depth Testing").addElement(new UDAElement("default.depthTest.stencil", "Stencil")));
	}

	public void onDropdownClick(EditorPaneMatsel matsel, String id) {
		switch (id) {
			case "default.depthTest.stencil": matsel.addAttribute(new DepthTestAttribute(true)); break;
		}
	}

	public void onSelect(EditorPaneMatsel matsel, Attribute attribute) {
		this.matsel = matsel;
		if (attribute instanceof DepthTestAttribute) {
			DepthTestAttribute depthTestAttribute = (DepthTestAttribute)attribute;
			for (int i = 0; i != this.depthFuncModes.length; i++) {
				if (depthTestAttribute.depthFunc == this.depthFuncModes[i]) {
					this.depthFunc.setSelectedVariant(i);
					break;
				}
			}
			this.depthRange[0].setNumber(depthTestAttribute.depthRangeFar).dropOffset();
			this.depthRange[1].setNumber(depthTestAttribute.depthRangeNear).dropOffset();
			this.depthMask.setValue(depthTestAttribute.depthMask);
		}
	}
	
	public boolean acceptAttribute(EditorPaneMatsel matsel, Attribute attribute) {
		return attribute instanceof DepthTestAttribute;
	}
	
	public void whileInputFieldFocused(STDInputField inputField, String id) {
		Attribute attribute = this.matsel.getSelectedAttribute();
		if (attribute instanceof DepthTestAttribute) {
			switch (id) {
				case "rangeFar": ((DepthTestAttribute)attribute).depthRangeFar = this.depthRange[0].getTextAsNumber(((DepthTestAttribute)attribute).depthRangeFar); break;
				case "rangeNear": ((DepthTestAttribute)attribute).depthRangeNear = this.depthRange[1].getTextAsNumber(((DepthTestAttribute)attribute).depthRangeNear); break;
			}
		}
	}
	
	public void onInputFieldFocusRemoved(STDInputField inputField, String id) {
		try { inputField.setTextWithPointer(String.valueOf(Float.valueOf(inputField.getText()))).dropOffset(); } catch (Exception e) {}
	}
}
