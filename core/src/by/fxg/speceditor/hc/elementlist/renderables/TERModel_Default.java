package by.fxg.speceditor.hc.elementlist.renderables;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.hc.elementlist.EnumTransform;
import by.fxg.speceditor.hc.elementlist.TreeElementRenderable;
import by.fxg.speceditor.hc.elementlist.elements.ElementModel;
import by.fxg.speceditor.tools.g3d.TextureLinkedAttribute;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.ui.UInputField;
import by.fxg.speceditor.ui.URenderBlock;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class TERModel_Default extends TreeElementRenderable<ElementModel> {
	public URenderBlock[] blocks = new URenderBlock[3];
	public UInputField[] input = new UInputField[11];
	
	protected UButton modelSelectionButton;
	
	protected Array<String> materialSelectionArray = new Array<>();
	protected UDropdownSelectSingle materialSelection, attributeSelection;
	protected UButton[] attributeButtons = new UButton[6]; //open texture, delete, flip button
	
	public TERModel_Default(ElementModel object) {
		super(object);
		
		String numeral = "0123456789-.";
		for (int i = 0; i != this.input.length; i++) {
			Vector3 vector = i < 3 ? renderable.getTransform(EnumTransform.TRANSLATE) : i < 6 ? renderable.getTransform(EnumTransform.ROTATE) : renderable.getTransform(EnumTransform.SCALE);
			this.input[i] = new UInputField(0, 0, 0, 0).setAllowedCharacters(numeral).setMaxLength(10).setText(String.valueOf(i % 3 == 0 ? vector.x : i % 3 == 1 ? vector.y : vector.z));
		}
		this.input[9] = new UInputField(0, 0, 0, 0).setMaxLength(24).setText(renderable.getName());
		this.input[10] = new UInputField(0, 0, 0, 0).setMaxLength(128).setText(renderable.localModelHandle);
		
		this.modelSelectionButton = new UButton("Select model", 0, 0, 0, 0);
		this.materialSelection = new UDropdownSelectSingle(0, 0, 0, 0, 15, "None");
		this.attributeSelection = new UDropdownSelectSingle(0, 0, 0, 0, 15, "None", "[TEX] Diffuse", "[TEX] Emissive", "[TEX] Specular");
		if (renderable.modelInstance != null) {
			this.materialSelectionArray.add("None");
			for (int i = 0; i != renderable.modelInstance.materials.size; i++) {
				this.materialSelectionArray.add(renderable.modelInstance.materials.get(i).id);
			}
			this.materialSelection.setVariants(this.materialSelectionArray.toArray(String.class));
		}
		String[] buttonText = {"Select texture", "Delete attribute", "No flip", "Flip Y", "Flip X", "Flip XY"};
		for (int i = 0; i != this.attributeButtons.length; i++) {
			this.attributeButtons[i] = new UButton(buttonText[i], 0, 0, 0, 0);
		}
		
		this.blocks[0] = new URenderBlock("Parameters", 0, 0, 0) {
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				foster.setString("Name:").draw(this.x, (y -= 10) - 8, Align.left);
				shape.setColor(UColor.suboverlay);
				shape.filledRectangle(this.x + (int)foster.getWidth() + 5, y - 19, this.width - (int)foster.getWidth() - 5, 15);
				input[9].setTransforms(this.x + (int)foster.getWidth() + 5, y -= 19, this.width - (int)foster.getWidth() - 5, 15).update();
				input[9].render(batch, shape, foster);
				shape.setColor(UColor.gray);
				shape.line(this.x, y -= 5, this.x + this.width, y);
				foster.setString("Ext model path:").draw(this.x, y - 8, Align.left);
				shape.setColor(UColor.suboverlay);
				shape.filledRectangle(this.x + (int)foster.getWidth() + 5, y - 19, this.width - (int)foster.getWidth() - 5, 15);
				input[10].setTransforms(this.x + (int)foster.getWidth() + 5, y -= 19, this.width - (int)foster.getWidth() - 5, 15).update();
				input[10].render(batch, shape, foster);
				foster.setString("Model file:").draw(this.x, y -= 5, Align.left);
				modelSelectionButton.setTransforms(this.x + (int)foster.getWidth() + 5, y - 9, this.width - (int)foster.getWidth() - 5, 10);
				modelSelectionButton.render(shape, foster);
				return y;
			}
		}.setDropped(true);
		
		this.blocks[1] = new URenderBlock("Transform", 0, 0, 0) {
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				for (int i = 0; i != 3; i++) {
					switch(i) {
						case 0: foster.setString("Position:"); break;
						case 1: foster.setString("Rotation:"); break;
						case 2: foster.setString("Scale:"); break;
					}
					foster.draw(this.x, y -= 15, Align.left);
					y -= 8;
					for (int j = 0; j != 3; j++) {
						switch(j) {
							case 0: foster.setString("X:"); break;
							case 1: foster.setString("Y:"); break;
							case 2: foster.setString("Z:"); break;
						}
						foster.draw(this.x + 14, y - 9);
						drawWheelInput(renderable.getTransform(EnumTransform.values()[i]), j, shape, this.x + 24, y - 20, 12, 14);
						
						shape.setColor(i == 0 ? UColor.redblack : i == 1 ? UColor.greenblack : UColor.blueblack);
						shape.filledRectangle(this.x + 40, y - 20, this.width - 40, 15);
						input[j + i * 3].setTransforms(this.x + 40, y -= 20, this.width - 40, 15).update();
						input[j + i * 3].render(batch, shape, foster);
					}
				}
				y += 8;
				return y;
			}
		}.setDropped(true);
		
		this.blocks[2] = new URenderBlock("Materials", 0, 0, 0) {
			protected int renderInside(int y, Batch batch, ShapeDrawer shape, Foster foster) {
				foster.setString("Material type:").draw(this.x, y -= 18, Align.left);
				materialSelection.setTransforms(this.x + (int)foster.getWidth() + 10, y - 11, this.width - (int)foster.getWidth() - 10, 15);
				if (materialSelection.getVariant() > 0 && renderable.modelInstance.materials.size > 0) {
					if (!materialSelection.isDropped()) {
						shape.setColor(UColor.gray);
						shape.line(this.x, y -= 15, this.x + this.width, y);
						foster.setString("Attribute type:").draw(this.x, y -= 7, Align.left);
						attributeSelection.setTransforms(this.x + (int)foster.getWidth() + 10, y - 11, this.width - (int)foster.getWidth() - 10, 15);
						if (attributeSelection.getVariant() > 0) {
							if (!attributeSelection.isDropped()) {
								shape.setColor(UColor.gray);
								shape.line(this.x, y -= 15, this.x + this.width, y);
								if (attributeSelection.getVariant() < 4) { //tex attribs
									long attributeID = 99999999999L;
									switch (attributeSelection.getVariant()) {
										case 1: attributeID = TextureAttribute.Diffuse; break;
										case 2: attributeID = TextureAttribute.Emissive; break;
										case 3: attributeID = TextureAttribute.Specular; break;
									}
									Attribute rawAttribute = renderable.modelInstance.materials.get(materialSelection.getVariant() - 1).get(attributeID);
									if (rawAttribute instanceof TextureAttribute && !(rawAttribute instanceof TextureLinkedAttribute)) { //check for invalid attribute for recreation
										renderable.modelInstance.materials.get(materialSelection.getVariant() - 1).set(new TextureLinkedAttribute((TextureAttribute)rawAttribute));
									}
									TextureLinkedAttribute attribute = (TextureLinkedAttribute)renderable.modelInstance.materials.get(materialSelection.getVariant() - 1).get(attributeID);
									if (attribute == null) {
										foster.setString("Attribute not created.").draw(this.x + this.width / 2, y -= 5);
										attributeButtons[0].setTransforms(this.x, (y -= 15) - 10, this.width, 10);
										attributeButtons[0].render(shape, foster);
										if (attributeButtons[0].isPressed()) {
											try {
												FileHandle handle = Utils.selectFileDialog("Supported images (*.png; *.jpg)", "png", "jpg");
												attribute = new TextureLinkedAttribute(Long.valueOf(attributeID), new Texture(handle), handle);
												renderable.modelInstance.materials.get(materialSelection.getVariant() - 1).set(attribute);
											} catch (Exception e) { e.printStackTrace(); }
										}
									} else {
										foster.setString("Texture:").draw(this.x, y -= 5, Align.left);
										attributeButtons[0].setTransforms(this.x + (int)foster.getWidth() + 10, y - 9, this.width - (int)foster.getWidth() - 10, 10);
										attributeButtons[0].render(shape, foster);
										if (attributeButtons[0].isPressed()) {
											try {
												FileHandle handle = Utils.selectFileDialog("Supported images (*.png; *.jpg)", "png", "jpg");
												attribute = new TextureLinkedAttribute(Long.valueOf(attributeID), new Texture(handle), handle);
												renderable.modelInstance.materials.get(materialSelection.getVariant() - 1).set(attribute);
											} catch (Exception e) { e.printStackTrace(); }
										}
										
										foster.setString("UV Flip:").draw(this.x, y -= 15, Align.left);
										for (int i = 0; i != 4; i += 2) { //just less code to draw 2 buttons
											attributeButtons[2 + i].setTransforms(this.x, (y -= 15) - 10, this.width / 2 - 5, 12);
											attributeButtons[2 + i].render(shape, foster);
											attributeButtons[3 + i].setTransforms(this.x + this.width / 2 + 5, y - 10, this.width / 2 - 5, 12);
											attributeButtons[3 + i].render(shape, foster);
										}
										
										//TODO: revamp de cringe
										if (attributeButtons[2].isPressed()) {
											TextureRegion textureRegion = new TextureRegion(attribute.textureDescription.texture);
											textureRegion.flip(false, false);
											renderable.modelInstance.materials.get(materialSelection.getVariant() - 1).set(new TextureLinkedAttribute(Long.valueOf(attributeID), textureRegion, attribute.texturePath).setFlip(false, false));
										} else if (attributeButtons[3].isPressed()) {
											TextureRegion textureRegion = new TextureRegion(attribute.textureDescription.texture);
											textureRegion.flip(false, true);
											renderable.modelInstance.materials.get(materialSelection.getVariant() - 1).set(new TextureLinkedAttribute(Long.valueOf(attributeID), textureRegion, attribute.texturePath).setFlip(false, true));
										} else if (attributeButtons[4].isPressed()) {
											TextureRegion textureRegion = new TextureRegion(attribute.textureDescription.texture);
											textureRegion.flip(true, false);
											renderable.modelInstance.materials.get(materialSelection.getVariant() - 1).set(new TextureLinkedAttribute(Long.valueOf(attributeID), textureRegion, attribute.texturePath).setFlip(true, false));
										} else if (attributeButtons[5].isPressed()) {
											TextureRegion textureRegion = new TextureRegion(attribute.textureDescription.texture);
											textureRegion.flip(true, true);
											renderable.modelInstance.materials.get(materialSelection.getVariant() - 1).set(new TextureLinkedAttribute(Long.valueOf(attributeID), textureRegion, attribute.texturePath).setFlip(true, true));
										}
										
										attributeButtons[1].setTransforms(this.x, (y -= 20) - 10, this.width, 12);
										attributeButtons[1].render(shape, foster);
										if (attributeButtons[1].isPressed()) {
											renderable.modelInstance.materials.get(materialSelection.getVariant() - 1).remove(attributeID);
										}
									}
								}
							} else y -= 50;
						}
						attributeSelection.update();
						attributeSelection.render(shape, foster);
					} else y -= 22;
				}
				materialSelection.update();
				materialSelection.render(shape, foster);
				return y -= 1;
			}
		}.setDropped(true);
	}
	
	public void resetInputFields() {
		this.renderable.setName(this.input[9].getText().length() == 0 ? "Unnamed" : this.input[9].getText());
		this.renderable.localModelHandle = this.input[10].getText();
		for (int i = 0; i != 10; i++) {
			if (i < 9) {
				Vector3 vector = i < 3 ? renderable.getTransform(EnumTransform.TRANSLATE) : i < 6 ? renderable.getTransform(EnumTransform.ROTATE) : renderable.getTransform(EnumTransform.SCALE);
				if (!this.input[i].isFocused()) {
					this.input[i].setText(String.valueOf(i % 3 == 0 ? vector.x : i % 3 == 1 ? vector.y : vector.z));
				} else {
					try {
						vector.set(i % 3 == 0 ? Float.valueOf(this.input[i].getText()) : vector.x, i % 3 == 1 ? Float.valueOf(this.input[i].getText()) : vector.y, i % 3 == 2 ? Float.valueOf(this.input[i].getText()) : vector.z);
					} catch (Exception e) {}
				}
			}
		}
	}
	
	public void update(int x, int y, int width, int height, boolean allowMouse) {
		for (URenderBlock block : this.blocks) block.setTransforms(x + 10, width - 19, 15);
		this.resetInputFields();
		
		if (this.modelSelectionButton.isPressed()) {
			try {
				FileHandle handle = Utils.selectFileDialog("Supported models (*.obj; *.gltf)", "obj", "gltf");
				AssetDescriptor<Model> assetDescriptor = new AssetDescriptor<Model>(handle, Model.class);
				if (Game.get.manager.assetManager.isLoaded(assetDescriptor)) {
					Game.get.manager.assetManager.unload(handle.path());
				}
				Game.get.manager.assetManager.load(assetDescriptor);
				Game.get.manager.assetManager.finishLoading();
				this.updateModel(handle, Game.get.manager.assetManager.get(assetDescriptor));
			} catch (Exception e) {}
		}
	}

	public int render(Batch batch, ShapeDrawer shape, Foster foster, int hOffset, int x, int y, int width, int height, boolean allowMouse) {
		for (URenderBlock block : this.blocks) hOffset = block.render(hOffset, batch, shape, foster);
		return hOffset;
	}

	private void updateModel(FileHandle handle, Model model) {
		this.renderable.modelHandle = handle;
		this.renderable.modelInstance = new ModelInstance(model);
		this.materialSelectionArray.clear();
		this.materialSelectionArray.add("None");
		for (Material material : this.renderable.modelInstance.materials) {
			this.materialSelectionArray.add(material.id);
			material.clear();			
		}
		this.materialSelection.setVariants(this.materialSelectionArray.toArray(String.class));
	}
	
	public void setMaterials(String... materials) {
		this.materialSelectionArray.clear();
		this.materialSelectionArray.add("None");
		this.materialSelectionArray.addAll(materials);
		this.materialSelection.setVariants(this.materialSelectionArray.toArray(String.class));
	}
}
