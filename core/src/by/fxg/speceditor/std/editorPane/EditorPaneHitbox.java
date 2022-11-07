package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.bullet.objects.IPhysObject;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.GizmosModule;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.objectTree.elements.ElementHitbox;
import by.fxg.speceditor.std.objectTree.elements.ElementHitboxStack;
import by.fxg.speceditor.std.ui.ISTDInputFieldListener;
import by.fxg.speceditor.std.ui.STDInputField;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.ColoredInputField;
import by.fxg.speceditor.ui.NumberCursorInputField;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UDropdownSelectMultiple;
import by.fxg.speceditor.ui.URenderBlock;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EditorPaneHitbox extends EditorPane implements ISTDInputFieldListener {
	private ElementHitbox element = null;
	private STDInputField elementName;
	private TransformBlock transform;
	private SpecFlagsBlock specFlags;
	private BulletFlagsBlock bulletFlags;
	
	public EditorPaneHitbox() {
		this.elementName = new ColoredInputField().setAllowFullfocus(false).setListener(this, "name").setMaxLength(48);

		this.transform = (TransformBlock)new TransformBlock(this).setDropped(true);
		this.specFlags = (SpecFlagsBlock)new SpecFlagsBlock(this).setDropped(true);
		this.bulletFlags = (BulletFlagsBlock)new BulletFlagsBlock(this).setDropped(true);
	}
	
	public int updateAndRender(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height, int yOffset) {
		yOffset -= 8;
		foster.setString("Name:").draw(x + 5, yOffset -= foster.getHeight(), Align.left);
		this.elementName.setTransforms(x + (int)foster.getWidth() + 10, yOffset -= foster.getHalfHeight(), width - (int)foster.getWidth() - 15, 15).setFoster(foster).update();
		this.elementName.render(batch, shape);
		
		yOffset = this.transform.setTransforms(x + 8, width - 16).render(batch, shape, foster, yOffset - 5);
		yOffset = this.specFlags.setTransforms(x + 8, width - 16).render(batch, shape, foster, this.transform.isDropped() ? yOffset - 5 : yOffset);
		yOffset = this.bulletFlags.setTransforms(x + 8, width - 16).render(batch, shape, foster, this.specFlags.isDropped() ? yOffset - 5 : yOffset);
		return yOffset;
	}

	public void whileInputFieldFocused(STDInputField inputField, String id) {
		switch (id) {
			case "name": this.element.setName(this.elementName.getText()); break;
		}
	}
	
	public void updatePane(ITreeElementSelector<?> selector) {
		this.element = (ElementHitbox)selector.get(0);
		this.elementName.setText(this.element.getName());

		this.transform.updateBlock(this.element);
		this.specFlags.updateBlock(this.element);
		this.bulletFlags.updateBlock(this.element);
	}

	public boolean acceptElement(ITreeElementSelector<?> selector) {
		return selector.size() == 1 && selector.get(0) instanceof ElementHitbox;
	}
	
	private class TransformBlock extends URenderBlock implements ISTDInputFieldListener {
		private final String[] coords = {"X", "Y", "Z"};
		private EditorPaneHitbox parent;
		private STDInputField[] position = new STDInputField[3], rotation = new STDInputField[3], scale = new STDInputField[3];
		
		private TransformBlock(EditorPaneHitbox parent) {
			super("Transforms");
			this.parent = parent;

			NumberCursorInputField.Builder builder = (NumberCursorInputField.Builder)new NumberCursorInputField.Builder().setAllowFullfocus(false).setMaxLength(12);
			for (int i = 0; i != 3; i++) this.position[i] = builder.setBackgroundColor(UColor.redblack).setListener(this, "position").build();
			for (int i = 0; i != 3; i++) this.rotation[i] = builder.setBackgroundColor(UColor.greenblack).setListener(this, "rotation").build();
			for (int i = 0; i != 3; i++) this.scale[i] = builder.setBackgroundColor(UColor.blueblack).setListener(this, "scale").build();
			builder.addToLink(this.position).addToLink(this.rotation).addToLink(this.scale).linkFields();
		}

		protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
			if (SpecInterface.INSTANCE.currentFocus instanceof GizmosModule) this.updateGizmoValues();
			int sizePerPart = (this.width - 30 - (int)foster.setString(this.coords[0]).getWidth() * 3) / 3;
			
			foster.setString("Position:").draw(this.x, yOffset -= foster.getHeight(), Align.left);
			yOffset -= 19; //16 size of box + 3 offset
			for (int i = 0; i != 3; i++) {
				foster.setString(this.coords[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
				this.position[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
				this.position[i].render(batch, shape);
			}

			foster.setString("Rotation:").draw(this.x, yOffset -= foster.getHeight() + 5, Align.left);
			yOffset -= 19;
			for (int i = 0; i != 3; i++) {
				foster.setString(this.coords[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
				this.rotation[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
				this.rotation[i].render(batch, shape);
			}

			foster.setString("Scale:").draw(this.x, yOffset -= foster.getHeight() + 5, Align.left);
			yOffset -= 19;
			for (int i = 0; i != 3; i++) {
				foster.setString(this.coords[i]).draw(this.x + 10 + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset + foster.getHalfHeight());
				this.scale[i].setTransforms(this.x + 10 + (int)foster.getWidth() + ((int)foster.getWidth() + sizePerPart + 10) * i, yOffset, sizePerPart, 15).setFoster(foster).update();
				this.scale[i].render(batch, shape);
			}
			return yOffset;
		}
		
		public void whileInputFieldFocused(STDInputField inputField, String id) {
			switch (id) {
				case "position": this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2]); break;
				case "rotation": this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2]); break;
				case "scale": this.parent._convertTextToVector3(this.parent.element.getTransform(GizmoTransformType.SCALE), this.scale[0], this.scale[1], this.scale[2]); break;
			}
		}
		
		public void onInputFieldFocusRemoved(STDInputField inputField, String id) {
			try { inputField.setTextWithPointer(String.valueOf(Float.valueOf(inputField.getText()))).dropOffset(); } catch (Exception e) {}
		}
		
		private void updateBlock(ElementHitbox hitbox) {
			this.parent._convertVector3ToText(hitbox.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2], true);
			this.parent._convertVector3ToText(hitbox.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2], true);
			this.parent._convertVector3ToText(hitbox.getTransform(GizmoTransformType.SCALE), this.scale[0], this.scale[1], this.scale[2], true);
		}
		
		private void updateGizmoValues() {
			if (this.parent != null && this.parent.element != null) {
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.TRANSLATE), this.position[0], this.position[1], this.position[2], false);
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.ROTATE), this.rotation[0], this.rotation[1], this.rotation[2], false);
				this.parent._convertVector3ToText(this.parent.element.getTransform(GizmoTransformType.SCALE), this.scale[0], this.scale[1], this.scale[2], false);
			}
		}
	}
	
	/** XXX SpecFlagsBlock contains 3 extra reserved flags until custom properties will be added **/
	private class SpecFlagsBlock extends URenderBlock {
		private EditorPaneHitbox parent;
		private long[] physObjectMasks = { 
			IPhysObject.ACT_ACTIVE, IPhysObject.ACT_INACTIVE, IPhysObject.ACT_WANT_DEACTIVATE, IPhysObject.ACT_ALWAYS_ACTIVE, IPhysObject.ACT_ALWAYS_INACTIVE, IPhysObject.RESERVED6, IPhysObject.RESERVED7,
			IPhysObject.DISABLE_LISTEN, IPhysObject.RAYCASTABLE, IPhysObject.RESERVED10
		};
		private String[] physObjectMasksNames = {
			"[ACT Flag] Active", "[ACT Flag] Inactive", "[ACT Flag] Wants deactivation", "[ACT Flag] Always active", "[ACT Flag] Always inactive", "Reserved (64)", "Reserved (128)",
			"Disable listen", "[Mask] Raycastable", "Reserved (1024)"
		};
		private UDropdownSelectMultiple maskSelector;
		private UCheckbox linkToParent;
		
		public SpecFlagsBlock(EditorPaneHitbox parent) {
			super("SpecPhys Flags");
			this.parent = parent;
			this.maskSelector = new UDropdownSelectMultiple(15, this.physObjectMasksNames) {
				public void invertSelected(int variant) {
					this.variantValues[variant] = !this.variantValues[variant];
					parent.element.bulletFlags = IPhysObject.invertFlag(parent.element.bulletFlags, physObjectMasks[variant]);
				}
			};
			this.linkToParent = new UCheckbox() {
				public UCheckbox setValue(boolean value) {
					parent.element.linkFlagsToParent[0] = this.value = value;
					return this;
				}
			};
		}

		protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
			foster.setString("Link flags to parent:").draw(this.x, yOffset -= foster.getHeight() + 2, Align.left);
			this.linkToParent.setTransforms(this.x + foster.getWidth() + 5, yOffset -= 2, 12, 12).update();
			this.linkToParent.render(shape);
			if (!this.linkToParent.getValue()) {
				foster.setString("Flags:").draw(this.x, yOffset -= foster.getHeight() + 7, Align.left);
				this.maskSelector.setTransforms(this.x + (int)foster.getWidth() + 5, (yOffset -= 4) - (int)foster.getHalfHeight() + 3, this.width - (int)foster.getWidth() - 5, 14).update(foster);
				this.maskSelector.render(shape, foster);
				if (this.maskSelector.isDropped()) yOffset -= this.physObjectMasksNames.length * 15 + 2;
			} else foster.setString("Flags are linked to the parent").draw(this.x + 5, (yOffset -= 12) + 1, Align.left);
			return yOffset;
		}
		
		private void updateBlock(ElementHitbox hitbox) {
			for (int i = 0; i != this.physObjectMasks.length; i++) this.maskSelector.setVariantSelected(i, IPhysObject.hasFlag(hitbox.specFlags, this.physObjectMasks[i]));
			this.linkToParent.setValue(hitbox.linkFlagsToParent[0]).setEnabled(hitbox.getParent() instanceof ElementHitboxStack);
		}
	}
	
	private class BulletFlagsBlock extends URenderBlock {
		private EditorPaneHitbox parent;
		private long[] 
			collisionFlags = { 1, 2, 16, 4, 8, 32, 64, 128, 256, 512, 1024 }, //CollisionFlags
			collisionFilterGroups = { 1, 2, 4, 8, 16, 32, 128, 256 }; //CollisionFilterGroups + 2 custom 128, 256
		private String[]
			collisionFlagsNames = { "Static object", "Kinematic object", "Character object", "No contact response", "Custom material callback", "Disable debug rendering", 
				"Disable SPU processing", "Contact stiffness damping", "Has custom debug color", "Has friction anchor", "Has collision sound trigger" },
			collisionFilterGroupsNames = { "Default filter", "Static filter", "Kinematic filter", "Debris filter", "Sensor trigger", "Character filter", "Custom (128)", "Custom (256)" };
		private UDropdownSelectMultiple flagsSelector, filterMasksSelector, filterGroupsSelector;
		private UCheckbox linkToParentMasks, linkToParentFilterMasks, allFilterMasks, linkToParentFilterGroups, allFilterGroups;
		
		public BulletFlagsBlock(EditorPaneHitbox parent) {
			super("Bullet Flags");
			this.parent = parent;
			this.flagsSelector = new UDropdownSelectMultiple(15, this.collisionFlagsNames) {
				public void invertSelected(int variant) {
					this.variantValues[variant] = !this.variantValues[variant];
					parent.element.bulletFlags = IPhysObject.invertFlag(parent.element.bulletFlags, collisionFlags[variant]);
				}
			};
			this.filterMasksSelector = new UDropdownSelectMultiple(15, this.collisionFilterGroupsNames) {
				public void invertSelected(int variant) {
					this.variantValues[variant] = !this.variantValues[variant];
					parent.element.bulletFilterMask = IPhysObject.invertFlag(parent.element.bulletFilterMask, collisionFilterGroups[variant]);
				}
			};
			this.filterGroupsSelector = new UDropdownSelectMultiple(15, this.collisionFilterGroupsNames) {
				public void invertSelected(int variant) {
					this.variantValues[variant] = !this.variantValues[variant];
					parent.element.bulletFilterGroup = IPhysObject.invertFlag(parent.element.bulletFilterGroup, collisionFilterGroups[variant]);
				}
			};
			
			this.linkToParentMasks = new UCheckbox() { 
				public UCheckbox setValue(boolean value) {
					parent.element.linkFlagsToParent[1] = this.value = value;
					return this;
				}
			};
			this.linkToParentFilterMasks = new UCheckbox() { 
				public UCheckbox setValue(boolean value) {
					parent.element.linkFlagsToParent[2] = this.value = value;
					return this;
				}
			};
			this.allFilterMasks = new UCheckbox() { 
				public UCheckbox setValue(boolean value) {
					this.value = value;
					parent.element.bulletFilterMask = -parent.element.bulletFilterMask;
					return this;
				}
			};
			this.linkToParentFilterGroups = new UCheckbox() { 
				public UCheckbox setValue(boolean value) {
					parent.element.linkFlagsToParent[3] = this.value = value;
					return this;
				}
			};
			this.allFilterGroups = new UCheckbox() { 
				public UCheckbox setValue(boolean value) {
					this.value = value;
					parent.element.bulletFilterGroup = -parent.element.bulletFilterGroup;
					return this;
				}
			};
		}

		protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
			foster.setString("Link flags to parent:").draw(this.x, yOffset -= foster.getHeight() + 2, Align.left);
			this.linkToParentMasks.setTransforms(this.x + foster.getWidth() + 5, yOffset -= 2, 12, 12).update();
			this.linkToParentMasks.render(shape);
			if (!this.linkToParentMasks.getValue()) {
				foster.setString("Flags:").draw(this.x, yOffset -= foster.getHeight() + 7, Align.left);
				this.flagsSelector.setTransforms(this.x + (int)foster.getWidth() + 5, (yOffset -= 4) - (int)foster.getHalfHeight() + 3, this.width - (int)foster.getWidth() - 5, 14).update(foster);
				if (this.flagsSelector.isDropped()) yOffset -= this.collisionFlagsNames.length * 15 + 2;
			} else foster.setString("Flags are linked to the parent").draw(this.x + 5, (yOffset -= 12) + 1, Align.left);

			shape.line(this.x, yOffset -= 5, this.x + this.width, yOffset);
			
			foster.setString("Link filter masks to parent:").draw(this.x, yOffset -= foster.getHeight() + 7, Align.left);
			this.linkToParentFilterMasks.setTransforms(this.x + foster.getWidth() + 5, yOffset -= 2, 12, 12).update();
			this.linkToParentFilterMasks.render(shape);
			if (!this.linkToParentFilterMasks.getValue()) {
				foster.setString("All masks:").draw(this.x, yOffset -= foster.getHeight() + 7, Align.left);
				this.allFilterMasks.setTransforms(this.x + foster.getWidth() + 5, yOffset -= 2, 12, 12).update();
				this.allFilterMasks.render(shape);
				if (!this.allFilterMasks.getValue()) {
					foster.setString("Masks:").draw(this.x, yOffset -= foster.getHeight() + 7, Align.left);
					this.filterMasksSelector.setTransforms(this.x + (int)foster.getWidth() + 5, (yOffset -= 4) - (int)foster.getHalfHeight() + 3, this.width - (int)foster.getWidth() - 5, 14).update(foster);
					if (this.filterMasksSelector.isDropped()) yOffset -= this.collisionFilterGroupsNames.length * 15 + 2;
				} else foster.setString("All masks selected").draw(this.x + 5, (yOffset -= 12) + 1, Align.left);
			} else foster.setString("Masks are linked to the parent").draw(this.x + 5, (yOffset -= 12) + 1, Align.left);
			
			shape.line(this.x, yOffset -= 5, this.x + this.width, yOffset);
			
			foster.setString("Link filter groups to parent:").draw(this.x, yOffset -= foster.getHeight() + 7, Align.left);
			this.linkToParentFilterGroups.setTransforms(this.x + foster.getWidth() + 5, yOffset -= 2, 12, 12).update();
			this.linkToParentFilterGroups.render(shape);
			if (!this.linkToParentFilterGroups.getValue()) {
				foster.setString("All groups:").draw(this.x, yOffset -= foster.getHeight() + 7, Align.left);
				this.allFilterGroups.setTransforms(this.x + foster.getWidth() + 5, yOffset -= 2, 12, 12).update();
				this.allFilterGroups.render(shape);
				if (!this.allFilterGroups.getValue()) {
					foster.setString("Groups:").draw(this.x, yOffset -= foster.getHeight() + 7, Align.left);
					this.filterGroupsSelector.setTransforms(this.x + (int)foster.getWidth() + 5, (yOffset -= 4) - (int)foster.getHalfHeight() + 3, this.width - (int)foster.getWidth() - 5, 14).update(foster);
					if (this.filterGroupsSelector.isDropped()) yOffset -= this.collisionFilterGroupsNames.length * 15 + 2;
				} else foster.setString("All groups selected").draw(this.x + 5, (yOffset -= 12) + 1, Align.left);
			} else foster.setString("Groups are linked to the parent").draw(this.x + 5, (yOffset -= 12) + 1, Align.left);
			
			if (!this.linkToParentFilterGroups.getValue() && !this.allFilterGroups.getValue()) this.filterGroupsSelector.render(shape, foster);
			if (!this.linkToParentFilterMasks.getValue() && !this.allFilterMasks.getValue()) this.filterMasksSelector.render(shape, foster);
			if (!this.linkToParentMasks.getValue()) this.flagsSelector.render(shape, foster);
			return yOffset;
		}
		
		private void updateBlock(ElementHitbox hitbox) {
			for (int i = 0; i != this.collisionFlags.length; i++) this.flagsSelector.setVariantSelected(i, IPhysObject.hasFlag(hitbox.bulletFlags, this.collisionFlags[i]));
			for (int i = 0; i != this.collisionFilterGroups.length; i++) this.filterMasksSelector.setVariantSelected(i, IPhysObject.hasFlag(hitbox.bulletFilterMask, this.collisionFilterGroups[i]));
			for (int i = 0; i != this.collisionFilterGroups.length; i++) this.filterGroupsSelector.setVariantSelected(i, IPhysObject.hasFlag(hitbox.bulletFilterGroup, this.collisionFilterGroups[i]));
			this.linkToParentMasks.setValue(hitbox.linkFlagsToParent[1]).setEnabled(hitbox.getParent() instanceof ElementHitboxStack);
			this.linkToParentFilterMasks.setValue(hitbox.linkFlagsToParent[2]).setEnabled(hitbox.getParent() instanceof ElementHitboxStack);
			this.allFilterMasks.setValue(this.parent.element.bulletFilterMask < 0).setEnabled(!this.linkToParentFilterMasks.getValue());
			this.linkToParentFilterGroups.setValue(hitbox.linkFlagsToParent[3]).setEnabled(hitbox.getParent() instanceof ElementHitboxStack);
			this.allFilterGroups.setValue(this.parent.element.bulletFilterGroup < 0).setEnabled(!this.linkToParentFilterGroups.getValue());
		}
	}
}
