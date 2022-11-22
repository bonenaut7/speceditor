package by.fxg.speceditor.std.editorPane;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import by.fxg.pilesos.bullet.objects.IPhysObject;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.objectTree.elements.ElementHitboxStack;
import by.fxg.speceditor.std.objectTree.elements.TreeElementHitbox;
import by.fxg.speceditor.std.ui.ISTDInterfaceActionListener;
import by.fxg.speceditor.ui.UCheckbox;
import by.fxg.speceditor.ui.UDropdownSelectMultiple;
import by.fxg.speceditor.ui.URenderBlock;
import by.fxg.speceditor.utils.Utils;
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
	protected class SpecFlagsBlock extends URenderBlock implements ISTDInterfaceActionListener {
		private EditorPaneTreeElementHitbox parent;
		private long[] physObjectMasks = { 
			IPhysObject.DISABLE_LISTEN, IPhysObject.RAYCASTABLE, IPhysObject.RESERVED03, IPhysObject.RESERVED04, IPhysObject.RESERVED05
		};
		private String[] physObjectMasksNames = {
			"Disable listen", "[Group] Raycastable", "Reserved (8)", "Reserved (16)", "Reserved (32)"
		};
		private UDropdownSelectMultiple maskSelector;
		private UCheckbox linkToParent;
		
		public SpecFlagsBlock(EditorPaneTreeElementHitbox parent) {
			super("SpecPhys Flags");
			this.parent = parent;
			this.maskSelector = new UDropdownSelectMultiple(15, this.physObjectMasksNames) {
				public void invertSelected(int variant) {
					this.variantValues[variant] = !this.variantValues[variant];
					parent._element.specFlags = IPhysObject.invertFlag(parent._element.specFlags, physObjectMasks[variant]);
				}
			};
			this.linkToParent = new UCheckbox() {
				public UCheckbox setValue(boolean value) {
					parent._element.linkToParent[0] = this.value = value;
					return this;
				}
			};
		}

		protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
			yOffset -= 8;
			int x = this.x + 5;
			foster.setString("Link flags to parent").draw(x, yOffset -= foster.getHeight(), Align.left);
			this.linkToParent.setTransforms(x + foster.getWidth() + 5, yOffset -= 2, 12, 12).update();
			if (SpecEditor.DEBUG) foster.setString(Utils.format(this.parent._element.specFlags)).draw(this.x + this.width - 5, yOffset, Align.right);
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
			this.linkToParent.setValue(hitbox.linkToParent[0]).setEnabled(hitbox.getParent() instanceof ElementHitboxStack);
		}
	}
	
	protected class BulletFlagsBlock extends URenderBlock implements ISTDInterfaceActionListener {
		private EditorPaneTreeElementHitbox parent;
		private int[] 
			collisionFlags = { 1, 2, 16, 4, 8, 32, 64, 128, 256, 512, 1024 }, //CollisionFlags
			filterGroups = { 1, 2, 4, 8, 16, 32, 128, 256 }; //CollisionFilterGroups + 2 custom 128, 256
		private String[]
			collisionFlagsNames = { "Static object", "Kinematic object", "Character object", "No contact response", "Custom material callback", "Disable debug rendering", 
				"Disable SPU processing", "Contact stiffness damping", "Has custom debug color", "Has friction anchor", "Has collision sound trigger" },
			filterGroupsNames = { "Default filter", "Static filter", "Kinematic filter", "Debris filter", "Sensor trigger", "Character filter", "Reserved (128)", "Reserved (256)" };
		private UDropdownSelectMultiple selectorCollisionFlags, selectorFilterMask, selectorFilterGroup;
		private UCheckbox cbParentLinkCollisionFlags, cbParentLinkFilterMask, cbAllFilter_FilterMask, cbParentLinkFilterGroup, cbAllFilter_FilterGroup;
		
		public BulletFlagsBlock(EditorPaneTreeElementHitbox parent) {
			super("Bullet Flags");
			this.parent = parent;
			this.selectorCollisionFlags = new UDropdownSelectMultiple(15, this.collisionFlagsNames).setTransforms(this.x, 0, this.width, 0);
			this.selectorFilterMask = new UDropdownSelectMultiple(15, this.filterGroupsNames).setTransforms(this.x, 0, this.width, 0);
			this.selectorFilterGroup = new UDropdownSelectMultiple(15, this.filterGroupsNames).setTransforms(this.x, 0, this.width, 0);
			this.selectorCollisionFlags.setActionListener(this, "selector.collisionFlags");
			this.selectorFilterMask.setActionListener(this, "selector.filterMask");
			this.selectorFilterGroup.setActionListener(this, "selector.filterGroup");
			
			this.cbParentLinkCollisionFlags = new UCheckbox();
			this.cbParentLinkFilterMask = new UCheckbox();
			this.cbParentLinkFilterGroup = new UCheckbox();
			this.cbAllFilter_FilterMask = new UCheckbox();
			this.cbAllFilter_FilterGroup = new UCheckbox();
			this.cbParentLinkCollisionFlags.setActionListener(this, "parentLink.collisionFlags");
			this.cbParentLinkFilterMask.setActionListener(this, "parentLink.filterMask");
			this.cbParentLinkFilterGroup.setActionListener(this, "parentLink.filterGroup");
			this.cbAllFilter_FilterMask.setActionListener(this, "allFilter.filterMask");
			this.cbAllFilter_FilterGroup.setActionListener(this, "allFilter.filterGroup");
		}

		protected int renderInside(Batch batch, ShapeDrawer shape, Foster foster, int yOffset) {
			yOffset -= 8;
			int x = this.x + 5;
			foster.setString("Link to parent").draw(x, yOffset -= foster.getHeight(), Align.left);
			this.cbParentLinkCollisionFlags.setTransforms(x + foster.getWidth() + 5, yOffset -= 2, 12, 12).update();
			if (SpecEditor.DEBUG) foster.setString(Utils.format(this.parent._element.btCollisionFlags)).draw(this.x + this.width - 5, yOffset, Align.right);
			this.cbParentLinkCollisionFlags.render(shape);
			if (!this.cbParentLinkCollisionFlags.getValue()) {
				foster.setString("Flags").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
				this.selectorCollisionFlags.setTransforms(x + foster.getWidth() + 5, (yOffset -= 3) - foster.getHalfHeight() + 3, this.width - foster.getWidth() - 15, 14).update(foster);
				if (this.selectorCollisionFlags.isFocused()) yOffset -= this.selectorCollisionFlags.getDropHeight();
			} else foster.setString("Flags are linked to the parent").draw(x + 5, (yOffset -= 12), Align.left);

			shape.line(this.x, yOffset -= 5, this.x + this.width, yOffset);
			
			float longestString = this.parent.getLongestStringWidth(foster, "Link to parent", "All masks");
			foster.setString("Link to parent").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
			if (SpecEditor.DEBUG) foster.setString(Utils.format(this.parent._element.btFilterMask)).draw(this.x + this.width - 5, yOffset, Align.right);
			this.cbParentLinkFilterMask.setTransforms(x + longestString + 5, yOffset -= 2, 12, 12).update();
			this.cbParentLinkFilterMask.render(shape);
			if (!this.cbParentLinkFilterMask.getValue()) {
				foster.setString("All masks").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
				this.cbAllFilter_FilterMask.setTransforms(x + longestString + 5, yOffset -= 2, 12, 12).update();
				this.cbAllFilter_FilterMask.render(shape);
				if (!this.cbAllFilter_FilterMask.getValue()) {
					foster.setString("Masks").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
					this.selectorFilterMask.setTransforms(x + foster.getWidth() + 5, (yOffset -= 3) - foster.getHalfHeight() + 3, this.width - foster.getWidth() - 15, 14).update(foster);
					if (this.selectorFilterMask.isFocused()) yOffset -= this.selectorFilterMask.getDropHeight();
				} else foster.setString("All masks selected").draw(x + 5, (yOffset -= 17) + 3, Align.left);
			} else foster.setString("Masks are linked to the parent").draw(x + 5, (yOffset -= 12), Align.left);
			
			shape.line(this.x, yOffset -= 5, this.x + this.width, yOffset);
			
			longestString = this.parent.getLongestStringWidth(foster, "Link to parent", "All groups");
			foster.setString("Link to parent").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
			if (SpecEditor.DEBUG) foster.setString(Utils.format(this.parent._element.btFilterGroup)).draw(this.x + this.width - 5, yOffset, Align.right);
			this.cbParentLinkFilterGroup.setTransforms(x + longestString + 5, yOffset -= 2, 12, 12).update();
			this.cbParentLinkFilterGroup.render(shape);
			if (!this.cbParentLinkFilterGroup.getValue()) {
				foster.setString("All groups").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
				this.cbAllFilter_FilterGroup.setTransforms(x + longestString + 5, yOffset -= 2, 12, 12).update();
				this.cbAllFilter_FilterGroup.render(shape);
				if (!this.cbAllFilter_FilterGroup.getValue()) {
					foster.setString("Groups").draw(x, yOffset -= foster.getHeight() + 7, Align.left);
					this.selectorFilterGroup.setTransforms(x + foster.getWidth() + 5, (yOffset -= 3) - foster.getHalfHeight() + 3, this.width - foster.getWidth() - 15, 14).update(foster);
					if (this.selectorFilterGroup.isFocused()) yOffset -= this.selectorFilterGroup.getDropHeight();
				} else foster.setString("All groups selected").draw(x + 5, (yOffset -= 14) + 2, Align.left);
			} else foster.setString("Groups are linked to the parent").draw(x + 5, (yOffset -= 12), Align.left);
			
			if (!this.cbParentLinkFilterGroup.getValue() && !this.cbAllFilter_FilterGroup.getValue()) this.selectorFilterGroup.render(shape, foster);
			if (!this.cbParentLinkFilterMask.getValue() && !this.cbAllFilter_FilterMask.getValue()) this.selectorFilterMask.render(shape, foster);
			if (!this.cbParentLinkCollisionFlags.getValue()) this.selectorCollisionFlags.render(shape, foster);
			return yOffset - 4;
		}
		
		protected void updateBlock(TreeElementHitbox hitbox) {
			this.updateBlock(1).updateBlock(2).updateBlock(3).updateBlock(4);
		}
		
		public void onCheckboxAction(UCheckbox element, String id) {
			switch (id) {
				case "parentLink.collisionFilter": {
					this.parent._element.linkToParent[1] = element.getValue();
					if (!element.getValue()) this.updateBlock(1);
				} break;
				case "parentLink.activationState": {
					this.parent._element.linkToParent[2] = element.getValue();
					if (!element.getValue()) this.updateBlock(2);	
				} break;
				case "parentLink.filterMask": {
					this.parent._element.linkToParent[3] = element.getValue();
					if (!element.getValue()) this.updateBlock(3);
				} break;
				case "parentLink.filterGroup": {
					this.parent._element.linkToParent[4] = element.getValue();
					if (!element.getValue()) this.updateBlock(4);
				} break;
				case "allFilter.filterMask": {
					if (!element.getValue()) {
						int mask = 0;
						for (int mask$ : this.filterGroups) mask += mask$;
						this.parent._element.btFilterMask = mask;
						this.updateBlock(3);
					} else this.parent._element.btFilterMask = IPhysObject.FILTER_ALL;
				} break;
				case "allFilter.filterGroup": {
					if (!element.getValue()) {
						int mask = 0;
						for (int mask$ : this.filterGroups) mask += mask$;
						this.parent._element.btFilterGroup = mask;
						this.updateBlock(4);
					} else this.parent._element.btFilterGroup = IPhysObject.FILTER_ALL;
				} break;
			}
		}
		
		public void onDropdownSelectMultipleAction(UDropdownSelectMultiple element, String id, int variant) {
			switch (id) {
				case "selector.collisionFlags": this.parent._element.btCollisionFlags = (int)IPhysObject.invertFlag(this.parent._element.btCollisionFlags, this.collisionFlags[variant]); break;
				case "selector.filterMask": this.parent._element.btFilterMask = (int)IPhysObject.invertFlag(this.parent._element.btFilterMask, this.filterGroups[variant]); break;
				case "selector.filterGroup": this.parent._element.btFilterGroup = (int)IPhysObject.invertFlag(this.parent._element.btFilterGroup, this.filterGroups[variant]); break;
			}
		}
		
		private BulletFlagsBlock updateBlock(int type) {
			switch (type) {
				case 1: {
					this.cbParentLinkCollisionFlags.setValue(this.parent._element.linkToParent[1]).setEnabled(this.parent._element.getParent() instanceof ElementHitboxStack);
					for (int i = 0; i != this.collisionFlags.length; i++) {
						this.selectorCollisionFlags.setVariantSelected(i, IPhysObject.hasFlag(this.parent._element.btCollisionFlags, this.collisionFlags[i]));
					}
					this.selectorCollisionFlags.updateDisplayString(SpecEditor.fosterNoDraw);
				} break;
				case 2: {
					
				} break;
				case 3: {
					this.cbParentLinkFilterMask.setValue(this.parent._element.linkToParent[3]).setEnabled(this.parent._element.getParent() instanceof ElementHitboxStack);
					this.cbAllFilter_FilterMask.setValue(this.parent._element.btFilterMask == IPhysObject.FILTER_ALL).setEnabled(!this.cbParentLinkFilterMask.getValue());
					for (int i = 0; i != this.filterGroups.length; i++) {
						this.selectorFilterMask.setVariantSelected(i, IPhysObject.hasFlag(this.parent._element.btFilterMask, this.filterGroups[i]));
					}
					this.selectorFilterMask.updateDisplayString(SpecEditor.fosterNoDraw);
				} break;
				case 4: {
					this.cbParentLinkFilterGroup.setValue(this.parent._element.linkToParent[4]).setEnabled(this.parent._element.getParent() instanceof ElementHitboxStack);
					this.cbAllFilter_FilterGroup.setValue(this.parent._element.btFilterGroup == IPhysObject.FILTER_ALL).setEnabled(!this.cbParentLinkCollisionFlags.getValue());
					for (int i = 0; i != this.filterGroups.length; i++) {
						this.selectorFilterGroup.setVariantSelected(i, IPhysObject.hasFlag(this.parent._element.btFilterGroup, this.filterGroups[i]));
					}
					this.selectorFilterGroup.updateDisplayString(SpecEditor.fosterNoDraw);
				} break;
			}
			return this;
		}
	}
}
