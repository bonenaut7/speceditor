package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.ResourceManager;
import by.fxg.speceditor.std.g3d.TextureLinkedAttribute;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
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
		Material material = this.matsel.getCurrentMaterial();
		TextureLinkedAttribute attribute = (TextureLinkedAttribute)this.matsel.getCurrentAttribute(material);
		foster.setString("Texture:").draw(x, yOffset -= 3, Align.left);
		this.buttonSelectTexture.setTransforms(x + (int)foster.getWidth() + 10, yOffset - 10, width - (int)foster.getWidth() - 10, 12);
		this.buttonSelectTexture.render(shape, foster);
		if (this.buttonSelectTexture.isPressed()) {
			try {
				FileHandle handle = Utils.selectFileDialog("Supported images (*.png; *.jpg)", "png", "jpg");
				attribute = new TextureLinkedAttribute(attribute.type, new Texture(handle), handle);
				material.set(attribute);
			} catch (Exception e) { e.printStackTrace(); }
		}
		
		foster.setString("UV Flip:").draw(x, yOffset -= 17, Align.left);
		int totalWidth = (width - (int)foster.getWidth() - 15) / 2;
		this.buttonFlipTextureX.setTransforms(x + (int)foster.getWidth() + 10, (yOffset -= 10), totalWidth, 12).render(shape, foster);
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
		Material material = matsel.getCurrentMaterial();
		if (material != null) {
			switch (id) {
				case "default.texture.diffuse": material.set(TextureLinkedAttribute.createDiffuse(ResourceManager.standartDiffuse)); break;
				case "default.texture.specular": material.set(TextureLinkedAttribute.createSpecular(ResourceManager.standartDiffuse)); break;
				case "default.texture.bump": material.set(TextureLinkedAttribute.createBump(ResourceManager.standartDiffuse)); break;
				case "default.texture.normal": material.set(TextureLinkedAttribute.createNormal(ResourceManager.standartDiffuse)); break;
				case "default.texture.ambient": material.set(TextureLinkedAttribute.createAmbient(ResourceManager.standartDiffuse)); break;
				case "default.texture.emissive": material.set(TextureLinkedAttribute.createEmissive(ResourceManager.standartDiffuse)); break;
				case "default.texture.reflection": material.set(TextureLinkedAttribute.createReflection(ResourceManager.standartDiffuse)); break;
			}
		}
	}

	public void onSelect(EditorPaneMatsel matsel, Attribute attribute) {
		super.onSelect(matsel, attribute);
		if (attribute instanceof TextureAttribute && !(attribute instanceof TextureLinkedAttribute)) {
			this.matsel.getCurrentMaterial().set(new TextureLinkedAttribute((TextureAttribute)attribute));
		}
	}
	
	public boolean acceptAttribute(EditorPaneMatsel matsel, Attribute attribute) {
		return attribute instanceof TextureAttribute;
	}
}
