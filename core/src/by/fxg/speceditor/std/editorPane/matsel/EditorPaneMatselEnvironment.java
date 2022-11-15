package by.fxg.speceditor.std.editorPane.matsel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.STDManager;
import by.fxg.speceditor.std.ui.ISTDDropdownAreaListener;
import by.fxg.speceditor.std.ui.STDDropdownArea;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UDropdownSelectSingle;
import by.fxg.speceditor.ui.UHoldButton;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneMatselEnvironment extends EditorPaneMatsel implements ISTDDropdownAreaListener {
	private static final Array<Class<?>> bannedAttributes = new Array<>();
	static {
		bannedAttributes.add(PointLightsAttribute.class, DirectionalLightsAttribute.class, SpotLightsAttribute.class);
		bannedAttributes.add(CubemapAttribute.class);
	}
	
	protected Environment environment;
	protected EditorPaneMatselModule currentModule;
	protected UDropdownSelectSingle selectedAttribute;
	protected UButton buttonAddAttribute;
	protected UHoldButton buttonRemoveAttribute;
	
	/** Rendering is not handled for this element! Use following code where you need! <br>
	 * <code> if (matselObj.dropdownArea.isFocused()) matselObj.dropdownArea.render(shapeDrawerObj, fosterObj); </code> **/
	public STDDropdownArea dropdownArea;
	
	public EditorPaneMatselEnvironment(String name, Environment environment) {
		super(name);
		this.environment = environment;
		this.selectedAttribute = new UDropdownSelectSingle(15, "None") {
			public UDropdownSelectSingle setVariantSelected(int variant) {
				this.selectedVariant = variant;
				EditorPaneMatselEnvironment.this.onAttributeSelect();
				return this;
			}
		};
		this.buttonAddAttribute = new UButton("+");
		this.buttonRemoveAttribute = new UHoldButton("Remove attribute", UHoldButton.NO_KEY, 30).setColor(UColor.redblack);
		this.dropdownArea = new STDDropdownArea(15).setListener(this);
		this.refreshAttributes();
	}
	
	public void setEnvironment(Environment environment) {
		this.environment = environment;
		this.refreshAttributes();
	}
	
	protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
		yOffset -= 5;
		this.buttonAddAttribute.setTransforms(this.x, yOffset -= 14, 14, 14).render(shape, foster);
		if (this.buttonAddAttribute.isPressed()) {
			Array<STDDropdownAreaElement> elements = this.dropdownArea.getElementsArrayAsEmpty();
			STDManager.INSTANCE.getEditorPaneMatselModules().forEach(editorPaneMatselModule -> editorPaneMatselModule.onAttributeCreationPress(elements));
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
					Utils.logError(e, "EditorPaneMatselEnvironment#renderInside", "Inner module caused an exception");
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
					this.environment.remove(attribute.type);
					this.selectedAttribute.setVariantSelected(0);
					this.refreshAttributes();
					this.onAttributeSelect();
				}
			}
		} else yOffset -= this.selectedAttribute.getVariants().length * 15 + 2;
		this.selectedAttribute.update();
		this.selectedAttribute.render(shape, foster);
		return yOffset - 3;
	}

	public void onDropdownAreaClick(STDDropdownAreaElement element, String id) {
		Array<EditorPaneMatselModule> array = STDManager.INSTANCE.getEditorPaneMatselModules();
		for (int i = 0; i != array.size; i++) array.get(i).onDropdownAreaClick(this, element, id);
		this.refreshAttributes();
	}
	
	protected void onAttributeSelect() {
		Attribute attribute = this.getSelectedAttribute();
		EditorPaneMatselModule editorPaneMatselModule = STDManager.INSTANCE.searchAvailablePaneMatselModule(this, attribute);
		if (attribute != null && editorPaneMatselModule != null && editorPaneMatselModule.acceptAttribute(this, attribute)) {
			editorPaneMatselModule.onSelect(this, attribute);
			this.currentModule = editorPaneMatselModule;
		} else this.currentModule = null;
	}
	
	protected void refreshAttributes() {
		Array<String> attributes = new Array<>();
		attributes.add("None");
		if (this.environment != null) {
			for (Attribute attribute : this.environment) {
				//FIXME removing lights attribute because of visual bugs, not used anyway. (Same thing done in the #getSelectedAttribute)
				if (bannedAttributes.contains(attribute.getClass(), true)) continue;
				attributes.add(Utils.format(attribute.getClass().getSimpleName().replace("Attribute", ""), " - ", Attribute.getAttributeAlias(attribute.type)));
			}
		}
		this.selectedAttribute.setVariants(attributes.toArray(String.class));
		if (attributes.size <= this.selectedAttribute.getVariantSelected()) this.selectedAttribute.setVariantSelected(attributes.size - 1);
	}

	public Attributes getSelectedAttributes() {
		return this.environment;
	}

	public Attribute getSelectedAttribute() {
		Attribute attribute = null;
		if (this.environment != null) {
			//FIXME bad way to search for attribute
			int _index = 0;
			for (Attribute attribute$ : this.environment) {
				if (bannedAttributes.contains(attribute$.getClass(), true)) continue;
				if (++_index == this.selectedAttribute.getVariantSelected()) {
					attribute = attribute$;
					break;
				}
			}
		}
		return attribute;
	}
	
	public void addAttribute(Attribute attribute) {
		if (attribute != null) {
			this.environment.set(attribute);
		}
	}
}
