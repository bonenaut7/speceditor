package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.ui.ISTDInterfaceActionListener;
import by.fxg.speceditor.std.ui.STDDropdownArea;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.ui.UHoldButton;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatselMaterialArray extends EditorPaneMatsel implements ISTDInterfaceActionListener {
	public static IEditorPaneMatselModuleProvider defaultModuleProvider = null;
	
	protected Array<Material> materials = null;
	protected EditorPaneMatselModule currentModule;
	protected UDropdownSelectSingle selectedMaterial, selectedAttribute;
	protected UButton buttonAddAttribute;
	protected UHoldButton buttonRemoveAttribute;
	
	/** Rendering is not handled for this element! Use following code where you need! <br>
	 * <code> if (matselObj.dropdownArea.isFocused()) matselObj.dropdownArea.render(shapeDrawerObj, fosterObj); </code> **/
	public STDDropdownArea dropdownArea;
	
	public EditorPaneMatselMaterialArray(String name) {
		this(defaultModuleProvider, name);
	}
	
	public EditorPaneMatselMaterialArray(IEditorPaneMatselModuleProvider moduleProvider, String name) {
		super(moduleProvider, name);
		this.materials = new Array<>();
		this.selectedMaterial = new UDropdownSelectSingle(15, "None") {
			public UDropdownSelectSingle setVariantSelected(int variant) {
				this.selectedVariant = variant;
				EditorPaneMatselMaterialArray.this.refreshAttributes();
				return this;
			}
		};
		this.selectedAttribute = new UDropdownSelectSingle(15, "None") {
			public UDropdownSelectSingle setVariantSelected(int variant) {
				this.selectedVariant = variant;
				EditorPaneMatselMaterialArray.this.onAttributeSelect();
				return this;
			}
		};
		this.buttonAddAttribute = new UButton("+");
		this.buttonRemoveAttribute = new UHoldButton("Remove attribute", UHoldButton.NO_KEY, 30).setColor(UColor.redblack);
		this.dropdownArea = new STDDropdownArea(15);
		this.dropdownArea.setActionListener(this, "dropdownArea");
		this.refreshAttributes();
	}

	protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
		yOffset -= 5;
		foster.setString("Material").draw(this.x + 5, yOffset -= foster.getHeight() + 5, Align.left);
		this.selectedMaterial.setTransforms(this.x + foster.getWidth() + 10, yOffset - foster.getHalfHeight() + 1, this.width - foster.getWidth() - 10, 14);
		
		if (!this.selectedMaterial.isFocused()) {
			Material material = this.getCurrentMaterial();
			if (material != null) {
				shape.setColor(UColor.gray);
				shape.line(this.x, yOffset -= 7, this.x + this.width, yOffset);
				this.buttonAddAttribute.setTransforms(this.x, yOffset -= 17, 14, 14).render(shape, foster);
				if (this.buttonAddAttribute.isPressed()) {
					Array<STDDropdownAreaElement> elements = this.dropdownArea.getElementsArrayAsEmpty();
					Array<EditorPaneMatselModule> modules = this.moduleProvider.getModules();
					for (int i = 0; i != modules.size; i++) modules.get(i).onAttributeCreationPress(this, this.dropdownArea, elements);
					this.dropdownArea.setElements(elements, foster).open(this.x + 1, yOffset + 3);
				}
				
				foster.setString("Attrib").draw(this.x + 18, yOffset + foster.getHalfHeight(), Align.left);
				this.selectedAttribute.setTransforms(this.x + foster.getWidth() + 22, yOffset, this.width - foster.getWidth() - 22, 14);
				if (!this.selectedAttribute.isFocused()) {
					if (this.currentModule != null) {
						shape.setColor(UColor.gray);
						shape.line(this.x, (yOffset -= 8) + 4, this.x + this.width, yOffset + 4);
						try {
							yOffset = this.currentModule.renderModule(batch, shape, foster, yOffset, this.x + 3, this.width - 6);
						} catch (Exception e) {
							Utils.logError(e, "EditorPaneMatselMaterialArray#renderInside", "Inner module caused an exception");
						}
						yOffset -= 5;
					} else if (this.selectedAttribute.getVariantSelected() > 0) {
						shape.setColor(UColor.gray);
						shape.line(this.x, (yOffset -= 3), this.x + this.width, yOffset);
						foster.setString("Module not found for this attribute").draw(this.x + this.width / 2, yOffset -= foster.getHeight() + 2);
						yOffset -= 4;
					}
					Attribute attribute = this.getSelectedAttribute();
					if (this.selectedAttribute.getVariantSelected() > 0 && attribute != null) {
						shape.setColor(UColor.gray);
						shape.line(this.x, yOffset, this.x + this.width, yOffset);
						this.buttonRemoveAttribute.setTransforms(this.x, yOffset -= 15, this.width, 12).update();
						this.buttonRemoveAttribute.render(shape, foster);
						if (this.buttonRemoveAttribute.isPressed()) {
							if (attribute instanceof Disposable) ((Disposable)attribute).dispose();
							material.remove(attribute.type);
							this.refreshAttributes();
							this.onAttributeSelect();
						}
					}
				} else yOffset -= this.selectedAttribute.getDropHeight();
				this.selectedAttribute.update();
				this.selectedAttribute.render(shape, foster);
			} else yOffset -= 3;
		} else yOffset -= this.selectedMaterial.getDropHeight() + 5;
		this.selectedMaterial.update();
		this.selectedMaterial.render(shape, foster);
		return yOffset - 3;
	}
	
	public void onDropdownAreaClick(STDDropdownArea area, String id, STDDropdownAreaElement element, String elementID) {
		Array<EditorPaneMatselModule> array = this.moduleProvider.getModules();
		for (int i = 0; i != array.size; i++) array.get(i).onDropdownAreaClick(this, element, elementID);
		this.refreshAttributes();
	}
	
	protected void onAttributeSelect() {
		Attribute attribute = this.getSelectedAttribute();
		EditorPaneMatselModule editorPaneMatselModule = this.moduleProvider.getModuleForAttribute(this, attribute);
		if (attribute != null && editorPaneMatselModule != null && editorPaneMatselModule.acceptAttribute(this, attribute)) {
			editorPaneMatselModule.onSelect(this, attribute);
			this.currentModule = editorPaneMatselModule;
		} else this.currentModule = null;
	}
	
	protected void refreshAttributes() {
		Array<String> attributes = new Array<>();
		attributes.add("None");
		if (this.materials != null && this.selectedMaterial.getVariantSelected() > 0) {
			for (Attribute attribute : this.materials.get(this.selectedMaterial.getVariantSelected() - 1)) {
				if (this.moduleProvider.isAttributeAllowed(this, attribute)) {
					attributes.add(Utils.format(attribute.getClass().getSimpleName().replace("Attribute", ""), " - ", Attribute.getAttributeAlias(attribute.type)));
				}
			}
		}
		this.selectedAttribute.setVariants(attributes.toArray(String.class));
		if (attributes.size <= this.selectedAttribute.getVariantSelected()) this.selectedAttribute.setVariantSelected(attributes.size - 1);
	}
	
	public void update(Array<Material> materials) {
		this.selectedMaterial.setVariantSelected(0);
		this.selectedAttribute.setVariantSelected(0);
		this.materials = materials;
		if (materials != null) {
			String[] materialNames = new String[materials.size + 1];
			materialNames[0] = "None";
			for (int i = 0; i != materials.size; i++) materialNames[i + 1] = materials.get(i).id;
			this.selectedMaterial.setVariants(materialNames);
		} else this.selectedMaterial.setVariants("None");
	}

	public Attributes getSelectedAttributes() {
		return this.getCurrentMaterial();
	}

	public Attribute getSelectedAttribute() {
		Attributes selectedAttributes = this.getSelectedAttributes();
		Attribute attribute = null;
		if (selectedAttributes != null) {
			//FIXME bad way to search for attribute
			int _index = 0;
			for (Attribute attribute$ : selectedAttributes) {
				if (this.moduleProvider.isAttributeAllowed(this, attribute$)) {
					if (++_index == this.selectedAttribute.getVariantSelected()) {
						attribute = attribute$;
						break;
					}
				}
			}
		}
		return attribute;
	}
	
	private Material getCurrentMaterial() {
		return this.selectedMaterial.getVariantSelected() > 0 && this.materials.size >= this.selectedMaterial.getVariantSelected() ? this.materials.get(this.selectedMaterial.getVariantSelected() - 1) : null;
	}

	public void addAttribute(Attribute attribute) {
		Material material = this.getCurrentMaterial();
		if (material != null && attribute != null) {
			material.set(attribute);
			this.refreshAttributes();
			int _index = 0;
			for (Attribute attribute$ : material) {
				_index++;
				if (attribute$.type == attribute.type) {
					this.selectedAttribute.setVariantSelected(_index);
					break;
				}	
			}
		}
	}
}
