package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.bullet.objects.IPhysObject;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.objectTree.elements.ElementHitboxStack;
import by.fxg.speceditor.std.objectTree.elements.TreeElementHitbox;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UDropdownSelectMultiple;
import by.fxg.speceditor.ui.URenderBlock;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class EditorPaneTreeElementHitbox extends EditorPane {
	private TreeElementHitbox _element;

	public void updatePane(ITreeElementSelector<?> selector) {
		this._element = (TreeElementHitbox)selector.get(0);
	}
	
	public boolean acceptElement(ITreeElementSelector<?> selector) {
		return selector.get(0) instanceof TreeElementHitbox;
	}
	
	//TODO Add custom properties
	/** XXX SpecFlagsBlock contains 3 extra reserved flags until custom properties will be added **/
	protected class SpecFlagsBlock extends URenderBlock {
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
		
		public SpecFlagsBlock(EditorPaneTreeElementHitbox parent) {
			super("SpecPhys Flags");
			this.maskSelector = new UDropdownSelectMultiple(15, this.physObjectMasksNames) {
				public void invertSelected(int variant) {
					this.variantValues[variant] = !this.variantValues[variant];
					parent._element.bulletFlags = IPhysObject.invertFlag(parent._element.bulletFlags, physObjectMasks[variant]);
				}
			};
			this.linkToParent = new UCheckbox() {
				public UCheckbox setValue(boolean value) {
					parent._element.linkFlagsToParent[0] = this.value = value;
					return this;
				}
			};
		}

		protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
			yOffset -= 8;
			int x = this.x + 5;
			foster.setString("Link flags to parent").draw(x, yOffset -= foster.getHeight(), Align.left);
			this.linkToParent.setTransforms(x + foster.getWidth() + 5, yOffset -= 2, 12, 12).update();
			this.linkToParent.render(shape);
			if (!this.linkToParent.getValue()) {
				foster.setString("Flags").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
				this.maskSelector.setTransforms(x + foster.getWidth() + 5, (yOffset -= 2) - foster.getHalfHeight() + 2, this.width - foster.getWidth() - 15, 14).update(foster);
				this.maskSelector.render(shape, foster);
				if (this.maskSelector.isFocused()) yOffset -= this.maskSelector.getDropHeight();
			} else foster.setString("Flags are linked to the parent").draw(x + 5, (yOffset -= 16) + 2, Align.left);
			return yOffset - 5;
		}
		
		protected void updateBlock(TreeElementHitbox hitbox) {
			for (int i = 0; i != this.physObjectMasks.length; i++) this.maskSelector.setVariantSelected(i, IPhysObject.hasFlag(hitbox.specFlags, this.physObjectMasks[i]));
			this.linkToParent.setValue(hitbox.linkFlagsToParent[0]).setEnabled(hitbox.getParent() instanceof ElementHitboxStack);
		}
	}
	
	protected class BulletFlagsBlock extends URenderBlock {
		private EditorPaneTreeElementHitbox parent;
		private long[] 
			collisionFlags = { 1, 2, 16, 4, 8, 32, 64, 128, 256, 512, 1024 }, //CollisionFlags
			collisionFilterGroups = { 1, 2, 4, 8, 16, 32, 128, 256 }; //CollisionFilterGroups + 2 custom 128, 256
		private String[]
			collisionFlagsNames = { "Static object", "Kinematic object", "Character object", "No contact response", "Custom material callback", "Disable debug rendering", 
				"Disable SPU processing", "Contact stiffness damping", "Has custom debug color", "Has friction anchor", "Has collision sound trigger" },
			collisionFilterGroupsNames = { "Default filter", "Static filter", "Kinematic filter", "Debris filter", "Sensor trigger", "Character filter", "Custom (128)", "Custom (256)" };
		private UDropdownSelectMultiple flagsSelector, filterMasksSelector, filterGroupsSelector;
		private UCheckbox linkToParentMasks, linkToParentFilterMasks, allFilterMasks, linkToParentFilterGroups, allFilterGroups;
		
		public BulletFlagsBlock(EditorPaneTreeElementHitbox parent) {
			super("Bullet Flags");
			this.parent = parent;
			this.flagsSelector = new UDropdownSelectMultiple(15, this.collisionFlagsNames) {
				public void invertSelected(int variant) {
					this.variantValues[variant] = !this.variantValues[variant];
					parent._element.bulletFlags = IPhysObject.invertFlag(parent._element.bulletFlags, collisionFlags[variant]);
				}
			};
			this.filterMasksSelector = new UDropdownSelectMultiple(15, this.collisionFilterGroupsNames) {
				public void invertSelected(int variant) {
					this.variantValues[variant] = !this.variantValues[variant];
					parent._element.bulletFilterMask = IPhysObject.invertFlag(parent._element.bulletFilterMask, collisionFilterGroups[variant]);
				}
			};
			this.filterGroupsSelector = new UDropdownSelectMultiple(15, this.collisionFilterGroupsNames) {
				public void invertSelected(int variant) {
					this.variantValues[variant] = !this.variantValues[variant];
					parent._element.bulletFilterGroup = IPhysObject.invertFlag(parent._element.bulletFilterGroup, collisionFilterGroups[variant]);
				}
			};
			
			this.linkToParentMasks = new UCheckbox() { 
				public UCheckbox setValue(boolean value) {
					parent._element.linkFlagsToParent[1] = this.value = value;
					return this;
				}
			};
			this.linkToParentFilterMasks = new UCheckbox() { 
				public UCheckbox setValue(boolean value) {
					parent._element.linkFlagsToParent[2] = this.value = value;
					return this;
				}
			};
			this.allFilterMasks = new UCheckbox() { 
				public UCheckbox setValue(boolean value) {
					this.value = value;
					parent._element.bulletFilterMask = -parent._element.bulletFilterMask;
					return this;
				}
			};
			this.linkToParentFilterGroups = new UCheckbox() { 
				public UCheckbox setValue(boolean value) {
					parent._element.linkFlagsToParent[3] = this.value = value;
					return this;
				}
			};
			this.allFilterGroups = new UCheckbox() { 
				public UCheckbox setValue(boolean value) {
					this.value = value;
					parent._element.bulletFilterGroup = -parent._element.bulletFilterGroup;
					return this;
				}
			};
		}

		protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
			yOffset -= 8;
			int x = this.x + 5;
			foster.setString("Link to parent").draw(x, yOffset -= foster.getHeight(), Align.left);
			this.linkToParentMasks.setTransforms(x + foster.getWidth() + 5, yOffset -= 2, 12, 12).update();
			this.linkToParentMasks.render(shape);
			if (!this.linkToParentMasks.getValue()) {
				foster.setString("Flags").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
				this.flagsSelector.setTransforms(x + foster.getWidth() + 5, (yOffset -= 3) - foster.getHalfHeight() + 3, this.width - foster.getWidth() - 15, 14).update(foster);
				if (this.flagsSelector.isFocused()) yOffset -= this.flagsSelector.getDropHeight();
			} else foster.setString("Flags are linked to the parent").draw(x + 5, (yOffset -= 12), Align.left);

			shape.line(this.x, yOffset -= 5, this.x + this.width, yOffset);
			
			float longestString = this.parent.getLongestStringWidth(foster, "Link to parent", "All masks");
			foster.setString("Link to parent").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
			this.linkToParentFilterMasks.setTransforms(x + longestString + 5, yOffset -= 2, 12, 12).update();
			this.linkToParentFilterMasks.render(shape);
			if (!this.linkToParentFilterMasks.getValue()) {
				foster.setString("All masks").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
				this.allFilterMasks.setTransforms(x + longestString + 5, yOffset -= 2, 12, 12).update();
				this.allFilterMasks.render(shape);
				if (!this.allFilterMasks.getValue()) {
					foster.setString("Masks").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
					this.filterMasksSelector.setTransforms(x + foster.getWidth() + 5, (yOffset -= 3) - foster.getHalfHeight() + 3, this.width - foster.getWidth() - 15, 14).update(foster);
					if (this.filterMasksSelector.isFocused()) yOffset -= this.filterMasksSelector.getDropHeight();
				} else foster.setString("All masks selected").draw(x + 5, (yOffset -= 17) + 3, Align.left);
			} else foster.setString("Masks are linked to the parent").draw(x + 5, (yOffset -= 12), Align.left);
			
			shape.line(this.x, yOffset -= 5, this.x + this.width, yOffset);
			
			longestString = this.parent.getLongestStringWidth(foster, "Link to parent", "All groups");
			foster.setString("Link to parent").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
			this.linkToParentFilterGroups.setTransforms(x + longestString + 5, yOffset -= 2, 12, 12).update();
			this.linkToParentFilterGroups.render(shape);
			if (!this.linkToParentFilterGroups.getValue()) {
				foster.setString("All groups").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
				this.allFilterGroups.setTransforms(x + longestString + 5, yOffset -= 2, 12, 12).update();
				this.allFilterGroups.render(shape);
				if (!this.allFilterGroups.getValue()) {
					foster.setString("Groups").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
					this.filterGroupsSelector.setTransforms(x + foster.getWidth() + 5, (yOffset -= 3) - foster.getHalfHeight() + 3, this.width - foster.getWidth() - 15, 14).update(foster);
					if (this.filterGroupsSelector.isFocused()) yOffset -= this.filterGroupsSelector.getDropHeight();
				} else foster.setString("All groups selected").draw(x + 5, (yOffset -= 14) + 2, Align.left);
			} else foster.setString("Groups are linked to the parent").draw(x + 5, (yOffset -= 12), Align.left);
			
			if (!this.linkToParentFilterGroups.getValue() && !this.allFilterGroups.getValue()) this.filterGroupsSelector.render(shape, foster);
			if (!this.linkToParentFilterMasks.getValue() && !this.allFilterMasks.getValue()) this.filterMasksSelector.render(shape, foster);
			if (!this.linkToParentMasks.getValue()) this.flagsSelector.render(shape, foster);
			return yOffset - 4;
		}
		
		protected void updateBlock(TreeElementHitbox hitbox) {
			for (int i = 0; i != this.collisionFlags.length; i++) this.flagsSelector.setVariantSelected(i, IPhysObject.hasFlag(hitbox.bulletFlags, this.collisionFlags[i]));
			for (int i = 0; i != this.collisionFilterGroups.length; i++) this.filterMasksSelector.setVariantSelected(i, IPhysObject.hasFlag(hitbox.bulletFilterMask, this.collisionFilterGroups[i]));
			for (int i = 0; i != this.collisionFilterGroups.length; i++) this.filterGroupsSelector.setVariantSelected(i, IPhysObject.hasFlag(hitbox.bulletFilterGroup, this.collisionFilterGroups[i]));
			this.linkToParentMasks.setValue(hitbox.linkFlagsToParent[1]).setEnabled(hitbox.getParent() instanceof ElementHitboxStack);
			this.linkToParentFilterMasks.setValue(hitbox.linkFlagsToParent[2]).setEnabled(hitbox.getParent() instanceof ElementHitboxStack);
			this.allFilterMasks.setValue(this.parent._element.bulletFilterMask < 0).setEnabled(!this.linkToParentFilterMasks.getValue());
			this.linkToParentFilterGroups.setValue(hitbox.linkFlagsToParent[3]).setEnabled(hitbox.getParent() instanceof ElementHitboxStack);
			this.allFilterGroups.setValue(this.parent._element.bulletFilterGroup < 0).setEnabled(!this.linkToParentFilterGroups.getValue());
		}
	}
}
