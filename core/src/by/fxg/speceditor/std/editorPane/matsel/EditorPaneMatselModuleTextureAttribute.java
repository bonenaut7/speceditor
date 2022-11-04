package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.std.g3d.attributes.SpecTextureAttribute;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import by.fxg.speceditor.utils.SpecFileChooser;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatselModuleTextureAttribute extends EditorPaneMatselModule {
	private UButton buttonSelectTexture, buttonFlipTextureX, buttonFlipTextureY;
	
	public EditorPaneMatselModuleTextureAttribute() {
		this.buttonSelectTexture = new UButton("Open file");
		this.buttonFlipTextureX = new UButton("UV Flip - X");
		this.buttonFlipTextureY = new UButton("UV Flip - Y");
	}
	
	public int renderModule(Batch batch, ShapeDrawer shape, Foster foster, int yOffset, int x, int width) {
		SpecTextureAttribute attribute = (SpecTextureAttribute)this.matsel.getSelectedAttribute();
		foster.setString("Texture:").draw(x, yOffset -= foster.getHeight() + 1, Align.left);
		this.buttonSelectTexture.setTransforms(x + (int)foster.getWidth() + 10, yOffset - (int)foster.getHalfHeight(), width - (int)foster.getWidth() - 10, 12);
		this.buttonSelectTexture.render(shape, foster);
		if (this.buttonSelectTexture.isPressed()) {
			FileHandle handle = SpecFileChooser.getInProjectDirectory().setFilter(Utils.FILENAMEFILTER_IMAGES).file();
			if (handle != null) ProjectAssetManager.INSTANCE.getLoadAsset(Texture.class, handle).addHandler(attribute);
		}
		
		foster.setString("UV Flip:").draw(x, (yOffset -= foster.getHeight() + 8) - 1, Align.left);
		int totalWidth = (width - (int)foster.getWidth() - 15) / 2;
		this.buttonFlipTextureX.setTransforms(x + (int)foster.getWidth() + 10, yOffset -= foster.getHalfHeight(), totalWidth, 12).render(shape, foster);
		this.buttonFlipTextureY.setTransforms(x + (int)foster.getWidth() + totalWidth - 5, yOffset, totalWidth, 12).render(shape, foster);
		if (this.buttonFlipTextureX.isPressed()) attribute.setFlip(true, false);
		if (this.buttonFlipTextureY.isPressed()) attribute.setFlip(false, true);
		return yOffset;
	}

	public void onAttributeCreationPress(Array<UDAElement> elements) {
		UDAElement textureAttributes = new UDAElement("", "Texture");
		textureAttributes.addElement(new UDAElement("default.texture.diffuse", "Diffuse"));
		textureAttributes.addElement(new UDAElement("default.texture.specular", "Specular"));
		textureAttributes.addElement(new UDAElement("default.texture.bump", "Bump"));
		textureAttributes.addElement(new UDAElement("default.texture.normal", "Normal"));
		textureAttributes.addElement(new UDAElement("default.texture.ambient", "Ambient"));
		textureAttributes.addElement(new UDAElement("default.texture.emissive", "Emissive"));
		textureAttributes.addElement(new UDAElement("default.texture.reflection", "Reflection"));
		elements.add(textureAttributes);
	}

	public void onDropdownClick(EditorPaneMatsel matsel, String id) {
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
