package by.fxg.speceditor.screen.gui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.project.assets.ISpakAssetUser;
import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.project.assets.SpakArchive;
import by.fxg.speceditor.project.assets.SpakAsset;
import by.fxg.speceditor.std.ui.ISTDInterfaceActionListener;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UDragArea;
import by.fxg.speceditor.ui.UHoldButton;
import by.fxg.speceditor.ui.UIOptionSelectMultipleList;
import by.fxg.speceditor.ui.UIOptionSelectSingleList;
import by.fxg.speceditor.utils.Utils;
import space.earlygrey.shapedrawer.ShapeDrawer;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GuiAssetManagerSetSpakUser extends Gui implements ISTDInterfaceActionListener {
	protected final ISpakAssetUser assetUser;
	protected final Array<Class<?>> assetTypes;
	
	protected UIOptionSelectMultipleList spakArchiveSelector;
	protected UIOptionSelectSingleList assetSelector;
	private UDragArea viewLeft, viewRight, viewRightHalfHeight;
	private UButton buttonClose;
	private UHoldButton buttonConfirm;
	
	protected Array<SpakArchive> spakArchives = new Array<>();
	protected Array<SpakAsset<?>> spakAssets = new Array<>();
	
	public GuiAssetManagerSetSpakUser(ISpakAssetUser<?> assetUser, Class<?>... assetTypes) {
		super(null);
		this.assetUser = assetUser;
		this.assetTypes = new Array<>(assetTypes);
		
		this.spakArchiveSelector = new UIOptionSelectMultipleList().setActionListener(this, "archiveSelector").setSelectionType(UIOptionSelectMultipleList.TYPE_EXPLORER_SELECT);
		for (String name : ProjectAssetManager.INSTANCE.getSpakArchiveMap().keySet()) this.spakArchiveSelector.addOptions(name);
		this.assetSelector = new UIOptionSelectSingleList().setActionListener(this, "assetSelector");
		
		this.buttonClose = new UButton("Cancel").setColor(UColor.greenblack);
		this.buttonConfirm = new UHoldButton("Select asset", Keys.ENTER, 20).setColor(UColor.yellowblack);

		this.viewLeft = new UDragArea(this, "left");
		this.viewRight = new UDragArea(this, "right");
		this.viewRightHalfHeight = new UDragArea(this, "rightHalfHeight");
		this.resize(Utils.getWidth(), Utils.getHeight());
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		if (SpecEditor.get.getInput().isKeyboardDown(Keys.ESCAPE, false) || this.buttonClose.isPressed()) this.closeGui();
		if (this.buttonConfirm.isPressed() && this.assetSelector.getSelectedOption() > -1) {
			SpakAsset asset = this.spakAssets.get(this.assetSelector.getSelectedOption());
			if (asset != null && this.assetTypes.contains(asset.getType(), true)) {
				this.setAssetUser(asset);
			}
			this.closeGui();
		}
		this.spakArchiveSelector.update();
		this.assetSelector.update();
		
		this.viewLeft.update();
		this.viewRight.update();
		this.viewRightHalfHeight.update();
		this.buttonConfirm.setEnabled(this.assetSelector.getSelectedOption() > -1);
		this.buttonConfirm.update();
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int width, int height) {
		batch.begin();
		shape.setColor(0, 0, 0, 0.5f);
		shape.filledRectangle(0, 0, width, height);
		
		int boxWidth = (int)(width / 1.5F), boxHeight = (int)(height / 1.5F);
		int x = width / 2 - boxWidth / 2, y = height / 2 - boxHeight / 2;
		int leftPart = (int)(width * SpecEditor.getPreference("gui.GuiAssetManagerSetSpakUser.view.left", float.class, 0.3125F));
		int rightPart = (int)(width * SpecEditor.getPreference("gui.GuiAssetManagerSetSpakUser.view.right", float.class, 0.65F));
		int rightHeight = (int)(height * SpecEditor.getPreference("gui.GuiAssetManagerSetSpakUser.view.rightHalfHeight", float.class, 0.575F));
		shape.setColor(0.12f, 0.12f, 0.12f, 1);
		shape.filledRectangle(x, y, boxWidth, boxHeight);
		shape.setColor(UColor.gray);
		shape.line(x, y + boxHeight - 18, x + boxWidth, y + boxHeight - 18);
		
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(x, y, leftPart - x + 4, boxHeight)) {
			foster.setString("PAK Selector").draw(x + 5, y + boxHeight - foster.getFont().getLineHeight(), Align.left);
			this.spakArchiveSelector.render(batch, shape, foster);
			this.viewLeft.render(shape);
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		if (PilesosScissorStack.instance.peekScissors(leftPart + 4, y, rightPart - leftPart, boxHeight)) {
			foster.setString("Assets").draw(leftPart + 9, y + boxHeight - foster.getFont().getLineHeight(), Align.left);
			this.assetSelector.render(batch, shape, foster);
			this.viewRight.render(shape);
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		if (PilesosScissorStack.instance.peekScissors(rightPart + 4, y + 30, x + boxWidth - rightPart - 4, boxHeight - 30)) {
			foster.setString("Preview & Info").draw(rightPart + 9, y + boxHeight - foster.getFont().getLineHeight(), Align.left);
			if (this.assetSelector.getSelectedOption() > -1) {
				SpakAsset<?> asset = this.spakAssets.get(this.assetSelector.getSelectedOption());
				
				if (asset.getType() == Texture.class) {
					if (asset.isLoaded()) {
						int areaX = rightPart + 5, areaY = rightHeight + 5, areaWidth = x + boxWidth - rightPart - 6, areaHeight = y + boxHeight - rightHeight - 24;
						int drawArea = areaWidth > areaHeight ? areaHeight : areaWidth;
						//shape.setColor(0, 0, 0, 0.25f); shape.filledRectangle(areaX + areaWidth / 2 - drawArea / 2, areaY + areaHeight / 2 - drawArea / 2, drawArea, drawArea); Background for alpha
						batch.draw((Texture)asset.getAsset(), areaX + areaWidth / 2 - drawArea / 2, areaY + areaHeight / 2 - drawArea / 2, drawArea, drawArea);
					} else {
						foster.setString("Asset is not loaded").draw(rightPart + (x + boxWidth - rightPart) / 2, rightHeight + (y + boxHeight - rightHeight - 20) / 2);
					}
				} else {
					foster.setString("Preview unavailable").draw(rightPart + (x + boxWidth - rightPart) / 2, rightHeight + (y + boxHeight - rightHeight - 20) / 2);
				}
				
				int renderY = rightHeight;
				foster.setString(Utils.format("Asset: ", asset.getPath()))						.draw(rightPart + 9, renderY -= foster.getFont().getLineHeight(), Align.left);
				foster.setString(Utils.format("Asset Type: ", asset.getType().getSimpleName()))	.draw(rightPart + 9, renderY -= foster.getFont().getLineHeight(), Align.left);
				foster.setString(Utils.format("Asset users: ", asset.getAssetHandlersSize()))	.draw(rightPart + 9, renderY -= foster.getFont().getLineHeight(), Align.left);
				foster.setString(Utils.format("Asset loaded: ", asset.isLoaded()))				.draw(rightPart + 9, renderY -= foster.getFont().getLineHeight(), Align.left);
				foster.setString(Utils.format("PAK Archive: ", asset.getArchive().getName()))	.draw(rightPart + 9, renderY -= foster.getFont().getLineHeight(), Align.left);
			}
			this.viewRightHalfHeight.render(shape);
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		this.buttonClose.render(shape, foster);
		this.buttonConfirm.render(shape, foster);
		
		shape.setColor(Color.WHITE);
		shape.rectangle(x - 1, y, boxWidth + 2, boxHeight + 1, 2f);
		batch.end();
		this.resize(Utils.getWidth(), Utils.getHeight());
	}
	
	public void onOptionMultipleListAction(UIOptionSelectMultipleList element, String id, int option, boolean value) {
		switch (id) {
			case "archiveSelector": {
				this.spakArchives.size = 0;
				this.spakAssets.size = 0;
				this.assetSelector.clearOptions();
				for (int i = 0; i != element.getOptionValues().size; i++) {
					if (element.getOptionValue(i)) {
						SpakArchive archive = ProjectAssetManager.INSTANCE.getPakArchive(element.getOptions().get(i));
						this.spakArchives.add(archive);
						if (archive != null) {	
							for (SpakAsset<?> asset : archive.getAssetsMap().values()) {
								if (this.assetTypes.contains(asset.getType(), true)) {
									this.spakAssets.add(asset);
									this.assetSelector.addOptions(asset.getPath());
								}
							}
						} else Utils.logDebugWarn("[Poor chance bug] Archive `", element.getOptions().get(i) + "` is null.");
					}
				}
			} break;
		}
	}

	public void onDragAreaDrag(UDragArea dragArea, String id, int start, int value, boolean stopFocus) {
		switch (id) {
			case "left": SpecEditor.setPreference("gui.GuiAssetManagerSetSpakUser.view.left", (float)value / (float)Utils.getWidth()); break;
			case "right": SpecEditor.setPreference("gui.GuiAssetManagerSetSpakUser.view.right", (float)value / (float)Utils.getWidth()); break;
			case "rightHalfHeight": SpecEditor.setPreference("gui.GuiAssetManagerSetSpakUser.view.rightHalfHeight", (float)value / (float)Utils.getHeight()); break;
		}
		this.resize(Utils.getWidth(), Utils.getHeight());
	}
	
	public void resize(int width, int height) {
		int boxWidth = (int)(width / 1.5F), boxHeight = (int)(height / 1.5F);
		int x = width / 2 - boxWidth / 2, y = height / 2 - boxHeight / 2;
		
		int dragSize = 4, halfDragSize = 2;
		int leftPart = (int)(width * SpecEditor.getPreference("gui.GuiAssetManagerSetSpakUser.view.left", float.class, 0.3125F));
		int rightPart = (int)(width * SpecEditor.getPreference("gui.GuiAssetManagerSetSpakUser.view.right", float.class, 0.65F));
		int rightHeight = (int)(height * SpecEditor.getPreference("gui.GuiAssetManagerSetSpakUser.view.rightHalfHeight", float.class, 0.575F));
		
		this.viewLeft.setTransforms(leftPart, y, dragSize, boxHeight).setParameters(x + halfDragSize, rightPart - dragSize, false);
		this.viewRight.setTransforms(rightPart, y, dragSize, boxHeight).setParameters(leftPart + dragSize, x + boxWidth - halfDragSize, false);
		this.viewRightHalfHeight.setTransforms(rightPart + dragSize, rightHeight, x + boxWidth - rightPart - dragSize, dragSize).setParameters(y + halfDragSize + 30, y + boxHeight - halfDragSize - 20, true);
		
		this.spakArchiveSelector.setTransforms(x, y, leftPart - x + 3, boxHeight - 17, 16);
		this.assetSelector.setTransforms(leftPart + dragSize, y, rightPart - leftPart - 1, boxHeight - 17, 16);
		
		this.buttonClose.setTransforms(x + boxWidth - 60, y + 10, 50, 13);
		this.buttonConfirm.setTransforms(x + boxWidth - 150, y + 10, 80, 13);
	}
	
	public void setAssetUser(SpakAsset<?> asset) {
		if (!asset.isLoaded()) asset.load();
		asset.addUser(this.assetUser);
	}
}
