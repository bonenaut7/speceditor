package by.fxg.speceditor.std.editorPane.matsel.legacy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.screen.gui.GuiAssetManagerSetSpakUser;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatsel;
import by.fxg.speceditor.std.editorPane.matsel.EditorPaneMatselModule;
import by.fxg.speceditor.std.g3d.attributes.SpecTextureAttribute;
import by.fxg.speceditor.std.ui.STDDropdownArea;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement;
import by.fxg.speceditor.ui.UButton;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatselModuleTextureAttribute extends EditorPaneMatselModule {
	private UButton buttonSelectTexture, buttonFlipTextureX, buttonFlipTextureY;
	
	public EditorPaneMatselModuleTextureAttribute() {
		this.buttonSelectTexture = new UButton("Select asset");
		this.buttonFlipTextureX = new UButton("UV Flip - X");
		this.buttonFlipTextureY = new UButton("UV Flip - Y");
	}
	
	public int renderModule(Batch batch, ShapeDrawer shape, Foster foster, int yOffset, int x, int width) {
		SpecTextureAttribute attribute = (SpecTextureAttribute)this.matsel.getSelectedAttribute();
		foster.setString("Texture:").draw(x, yOffset -= foster.getHeight() + 1, Align.left);
		this.buttonSelectTexture.setTransforms(x + (int)foster.getWidth() + 10, yOffset - (int)foster.getHalfHeight(), width - (int)foster.getWidth() - 10, 12);
		this.buttonSelectTexture.render(shape, foster);
		if (this.buttonSelectTexture.isPressed()) {
			SpecEditor.get.renderer.currentGui = new GuiAssetManagerSetSpakUser(attribute, Texture.class);
		}
		
		foster.setString("UV Flip:").draw(x, (yOffset -= foster.getHeight() + 8) - 1, Align.left);
		int totalWidth = (width - (int)foster.getWidth() - 15) / 2;
		this.buttonFlipTextureX.setTransforms(x + (int)foster.getWidth() + 10, yOffset -= foster.getHalfHeight(), totalWidth, 12).render(shape, foster);
		this.buttonFlipTextureY.setTransforms(x + (int)foster.getWidth() + totalWidth - 5, yOffset, totalWidth, 12).render(shape, foster);
		if (this.buttonFlipTextureX.isPressed()) attribute.flip(true, false);
		if (this.buttonFlipTextureY.isPressed()) attribute.flip(false, true);
		return yOffset;
	}

	public void onAttributeCreationPress(EditorPaneMatsel matsel, STDDropdownArea area, Array<STDDropdownAreaElement> elements) {
		elements.add(STDDropdownAreaElement.subwindow(area, "Texture")
			.add(STDDropdownAreaElement.button("default.texture.diffuse", "Diffuse"))
			.add(STDDropdownAreaElement.button("default.texture.specular", "Specular"))
			.add(STDDropdownAreaElement.button("default.texture.bump", "Bump"))
			.add(STDDropdownAreaElement.button("default.texture.normal", "Normal"))
			.add(STDDropdownAreaElement.button("default.texture.ambient", "Ambient"))
			.add(STDDropdownAreaElement.button("default.texture.emissive", "Emissive"))
			.add(STDDropdownAreaElement.button("default.texture.reflection", "Reflection"))
		);
	}

	public void onDropdownAreaClick(EditorPaneMatsel matsel, STDDropdownAreaElement element, String id) {
		switch (id) {
			case "default.texture.diffuse": matsel.addAttribute(SpecTextureAttribute.createDiffuse(DefaultResources.INSTANCE.standardTexture)); break;
			case "default.texture.specular": matsel.addAttribute(SpecTextureAttribute.createSpecular(DefaultResources.INSTANCE.standardTexture)); break;
			case "default.texture.bump": matsel.addAttribute(SpecTextureAttribute.createBump(DefaultResources.INSTANCE.standardTexture)); break;
			case "default.texture.normal": matsel.addAttribute(SpecTextureAttribute.createNormal(DefaultResources.INSTANCE.standardTexture)); break;
			case "default.texture.ambient": matsel.addAttribute(SpecTextureAttribute.createAmbient(DefaultResources.INSTANCE.standardTexture)); break;
			case "default.texture.emissive": matsel.addAttribute(SpecTextureAttribute.createEmissive(DefaultResources.INSTANCE.standardTexture)); break;
			case "default.texture.reflection": matsel.addAttribute(SpecTextureAttribute.createReflection(DefaultResources.INSTANCE.standardTexture)); break;
		}
	}

	public void onSelect(EditorPaneMatsel matsel, Attribute attribute) {
		super.onSelect(matsel, attribute);
		if (attribute instanceof TextureAttribute && !(attribute instanceof SpecTextureAttribute)) {
			this.matsel.getSelectedAttributes().set(new SpecTextureAttribute((TextureAttribute)attribute));
		}
	}
	
	public boolean acceptAttribute(EditorPaneMatsel matsel, Attribute attribute) {
		return attribute instanceof TextureAttribute;
	}
}
