package by.fxg.speceditor.screen.project;

import org.zeroturnaround.zip.ZipUtil;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.PilesosScissorStack;
import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.specpak.PakArchiveFileFilter;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.project.ProjectManager;
import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.project.assets.SpakArchive;
import by.fxg.speceditor.project.assets.SpakAsset;
import by.fxg.speceditor.screen.gui.GuiConfirmation;
import by.fxg.speceditor.screen.gui.GuiRename;
import by.fxg.speceditor.std.ui.ISTDInterfaceActionListener;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UButton;
import by.fxg.speceditor.ui.UDragArea;
import by.fxg.speceditor.ui.UHoldButton;
import by.fxg.speceditor.ui.UIOptionSelectMultipleList;
import by.fxg.speceditor.utils.BaseSubscreen;
import by.fxg.speceditor.utils.SpecFileChooser;
import by.fxg.speceditor.utils.Utils;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SubscreenExplorer extends BaseSubscreen implements ISTDInterfaceActionListener {
	protected final Class<?>[] assetTypes = {Texture.class, Model.class, SceneAsset.class};
	protected UIOptionSelectMultipleList spakArchiveSelector, assetTypeSelector, assetSelector;
	protected UButton buttonPakCreate, buttonPakImport, buttonPakRename, buttonPakLoadSelected, buttonPakUnloadSelected, buttonPakDelete;
	protected UButton buttonAssetImport, buttonAssetReplace, buttonAssetRename, buttonAssetLoad, buttonAssetUnload, buttonAssetDelete;
	protected UDragArea viewLeftPart, viewMidPart, viewRightPart;
	protected int prevX, prevY, prevWidth, prevHeight;
	
	protected Array<SpakArchive> spakArchives = new Array<>();
	protected Array<SpakAsset<?>> spakAssets = new Array<>();
	
	public SubscreenExplorer(int x, int y, int width, int height) {
		this.spakArchiveSelector = new UIOptionSelectMultipleList().setActionListener(this, "archiveSelector").setSelectionType(UIOptionSelectMultipleList.TYPE_EXPLORER_SELECT);
		this.assetTypeSelector = new UIOptionSelectMultipleList().setActionListener(this, "assetTypeSelector").addOptions("Texture", "Model", "SceneAsset");
		this.assetSelector = new UIOptionSelectMultipleList().setActionListener(this, "assetSelector").setSelectionType(UIOptionSelectMultipleList.TYPE_EXPLORER_SELECT);
		
		this.buttonPakCreate = new UButton("+C").setTooltip("Create PAK Archive");
		this.buttonPakImport = new UButton("+I").setTooltip("Import PAK Archive");
		this.buttonPakRename = new UButton("RN").setTooltip("Rename PAK Archive");
		this.buttonPakLoadSelected = new UButton("LS").setTooltip("Load assets from selected PAK Archives").setEnabled(false);
		this.buttonPakUnloadSelected = new UButton("US").setTooltip("Unload assets from selected PAK Archives").setEnabled(false);
		this.buttonPakDelete = new UButton("DS").setTooltip("Delete selected PAK Archives").setEnabled(false);
		
		this.buttonAssetImport = new UButton("+I").setTooltip("Import asset (Requires single selected PAK)").setEnabled(false);
		this.buttonAssetReplace = new UButton("RP").setTooltip("Replace asset").setEnabled(false);
		this.buttonAssetRename = new UButton("RN").setTooltip("Rename asset").setEnabled(false);
		this.buttonAssetLoad = new UButton("LS").setTooltip("Load asset").setEnabled(false);
		this.buttonAssetUnload = new UButton("US").setTooltip("Unload asset").setEnabled(false);
		this.buttonAssetDelete = new UButton("DS").setTooltip("Delete asset").setEnabled(false);
		
		this.viewLeftPart = new UDragArea(this, "leftPart");
		this.viewMidPart = new UDragArea(this, "midPart");
		this.viewRightPart = new UDragArea(this, "rightPart");
		this.refreshPakArchives();
		this.resize(x, y, width, height);
	}

	public void update(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		if (this.buttonPakCreate.isPressed()) {
			FileHandle handle = SpecFileChooser.getInProjectDirectory().setFilter(new PakArchiveFileFilter()).saveSingle(true, false);
			if (handle != null) {
				if (!handle.extension().equalsIgnoreCase("pak")) handle = handle.parent().child(Utils.format(handle.nameWithoutExtension(), ".pak"));
				ZipUtil.createEmpty(handle.file());
				if (ProjectAssetManager.INSTANCE.addPakArchive(handle)) {
					refreshPakArchives();
				}
			}
		} else if (this.buttonPakImport.isPressed()) {
			FileHandle[] handles = SpecFileChooser.getInProjectDirectory().setFilter(new PakArchiveFileFilter()).openMultiple(true, false);
			if (handles != null && handles.length > 0) {
				for (FileHandle handle : handles) {
					ProjectAssetManager.INSTANCE.addPakArchive(handle);
				}
				refreshPakArchives();
			}
		} else if (this.buttonPakLoadSelected.isPressed()) {
			for (int i = 0; i != this.spakArchives.size; i++) {
				this.spakArchives.get(i).loadAssets();
			}
		} else if (this.buttonPakUnloadSelected.isPressed()) {
			for (int i = 0; i != this.spakArchives.size; i++) {
				this.spakArchives.get(i).unloadAssets();
			}
		} else if (this.buttonPakDelete.isPressed()) {
			SpecEditor.get.renderer.currentGui = new GuiConfirmation(Utils.format("Are you sure you want to remove ", this.spakArchiveSelector.getOptionPositiveValues(), " PAK archives?"), "Assets from PAK Archives will be removed and replaced.") {
				public void onConfirm() {
					int[] indices = spakArchiveSelector.getOptionPositiveValuesIndices();
					for (int i = indices.length; i != 0; i--) {
						if (!ProjectAssetManager.INSTANCE.removePakArchive(spakArchives.get(i - 1).getName())) {
							Utils.logDebugWarn("[Poor chance bug] Archive `", spakArchives.get(i - 1) + "` can't be deleted.");
						}
					}
					refreshPakArchives();
				}
			}.setButton("Delete", UHoldButton.NO_KEY, 60, UColor.redgray);
		}
		
		
		if (this.buttonAssetImport.isPressed()) {
			FileHandle[] handles = SpecFileChooser.getInProjectDirectory().openMultiple(true, false);
			if (handles != null && handles.length > 0) {
				for (FileHandle handle : handles) {
					this.spakArchives.get(this.spakArchiveSelector.getFirstPositiveOptionValue()).addAsset(handle);
				}
				this.refreshAssetManager();
			}
		} else if (this.buttonAssetReplace.isPressed()) {
			//TODO: Asset replacement
		} else if (this.buttonAssetRename.isPressed()) {
			SpakAsset<?> asset = this.spakAssets.get(this.assetSelector.getFirstPositiveOptionValue());
			SpecEditor.get.renderer.currentGui = new GuiRename("               Rename asset               ", asset.getPath()) {
				public void onRenamed(String value) {
					if (Utils.isPathValid(value)) {
						if (asset.getArchive().renameAsset(asset.getPath(), value)) {
							refreshAssetManager();
						}
					}
				}
			}.setAllowedCharacters(Utils.PATH_SYMBOLS).setText(asset.getPath(), 128);
		} else if (this.buttonAssetLoad.isPressed()) {
			int[] indices = this.assetSelector.getOptionPositiveValuesIndices();
			for (int index : indices) this.spakAssets.get(index).load();
		} else if (this.buttonAssetUnload.isPressed()) {
			int[] indices = this.assetSelector.getOptionPositiveValuesIndices();
			for (int index : indices) this.spakAssets.get(index).unload();
		} else if (this.buttonAssetDelete.isPressed()) {
			//TODO: removal of multiple assets
			SpakAsset<?> asset = this.spakAssets.get(this.assetSelector.getFirstPositiveOptionValue());
			SpecEditor.get.renderer.currentGui = new GuiConfirmation("Are you sure you want to remove asset", Utils.format("`", asset.getPath(), "`"), 
					Utils.format("from the `", asset.getArchive().getName(), "` PAK Archive?"), "Asset will be deleted from PAK archive too!") {
				public void onConfirm() {
					asset.getArchive().deleteAsset(asset.getPath());
					refreshAssetManager();
				}
			}.setButton("Delete", UHoldButton.NO_KEY, 60, UColor.redgray);
		}
		
		this.spakArchiveSelector.update();
		this.assetTypeSelector.update();
		this.assetSelector.update();
		this.viewLeftPart.update();
		this.viewMidPart.update();
		this.viewRightPart.update();
	}

	public void render(Batch batch, ShapeDrawer shape, Foster foster, int x, int y, int width, int height) {
		batch.begin();
		shape.setColor(UColor.background);
		shape.filledRectangle(x, y, width, height);
		shape.setColor(UColor.gray);
		shape.rectangle(x + 1, y + 1, width - 2, height - 2);
		batch.flush();
		if (PilesosScissorStack.instance.peekScissors(x + 1, y + 1, width - 2, height - 2)) {
			this.spakArchiveSelector.render(batch, shape, foster);
			this.buttonPakCreate.render(shape, foster);
			this.buttonPakImport.render(shape, foster);
			this.buttonPakRename.render(shape, foster);
			this.buttonPakLoadSelected.render(shape, foster);
			this.buttonPakUnloadSelected.render(shape, foster);
			this.buttonPakDelete.render(shape, foster);
		
			this.assetTypeSelector.render(batch, shape, foster);
			this.assetSelector.render(batch, shape, foster);
			this.buttonAssetImport.render(shape, foster);
			this.buttonAssetReplace.render(shape, foster);
			this.buttonAssetRename.render(shape, foster);
			this.buttonAssetLoad.render(shape, foster);
			this.buttonAssetUnload.render(shape, foster);
			this.buttonAssetDelete.render(shape, foster);

			this.viewLeftPart.render(shape);
			this.viewMidPart.render(shape);
			this.viewRightPart.render(shape);
			
			if (this.assetSelector.getOptionPositiveValues() == 1) {
				int rightPart = (int)(width * ProjectManager.currentProject.getPreference("screen.view.assetSelector.rightPart", float.class, 0.7F));
				int renderX = x + rightPart + 5;
				int renderY = y + height;
				SpakAsset<?> asset = this.spakAssets.get(this.assetSelector.getFirstPositiveOptionValue());
//				if (asset.getType() == Texture.class && asset.isLoaded()) {
//					int previewWidth = (width - minRightPart - 4) / 2;
//					int previewHeight = (height - (textHeight - y)) / 2;
//					int size = previewWidth > previewHeight ? previewHeight : previewWidth;
//					batch.draw((Texture)asset.getAsset(), x + minRightPart + 2 + (width - minRightPart - 4 - size) / 2, y + height - 5 - size, size, size);
//				}
				
				foster.setString(Utils.format("Asset: ", asset.getPath()))						.draw(renderX, renderY -= foster.getFont().getLineHeight(), Align.left);
				foster.setString(Utils.format("Asset Type: ", asset.getType().getSimpleName()))	.draw(renderX, renderY -= foster.getFont().getLineHeight(), Align.left);
				foster.setString(Utils.format("Asset users: ", asset.getAssetHandlersSize()))	.draw(renderX, renderY -= foster.getFont().getLineHeight(), Align.left);
				foster.setString(Utils.format("Asset loaded: ", asset.isLoaded()))				.draw(renderX, renderY -= foster.getFont().getLineHeight(), Align.left);
				foster.setString(Utils.format("PAK Archive: ", asset.getArchive().getName()))	.draw(renderX, renderY -= foster.getFont().getLineHeight(), Align.left);
			} else if (this.assetSelector.getOptionPositiveValues() > 1) {
				int rightPart = (int)(width * ProjectManager.currentProject.getPreference("screen.view.assetSelector.rightPart", float.class, 0.7F));
				int renderX = x + rightPart + 5;
				int renderY = y + height;
				foster.setString(Utils.format("Assets selected: ", this.assetSelector.getOptionPositiveValues())).draw(renderX, renderY -= foster.getFont().getLineHeight(), Align.left);
			}
			batch.flush();
			PilesosScissorStack.instance.popScissors();
		}
		batch.end();
	}

	public void onDragAreaDrag(UDragArea dragArea, String id, int start, int value, boolean stopFocus) {
		switch (id) {
			case "leftPart": ProjectManager.currentProject.setPreference("screen.view.assetSelector.leftPart", (float)value / (float)this.prevWidth); break;
			case "midPart": ProjectManager.currentProject.setPreference("screen.view.assetSelector.midPart", (float)value / (float)this.prevWidth); break;
			case "rightPart": ProjectManager.currentProject.setPreference("screen.view.assetSelector.rightPart", (float)value / (float)this.prevWidth); break;
		}
		this.resize(this.prevX, this.prevY, this.prevWidth, this.prevHeight);
	}
	
	public void onOptionMultipleListAction(UIOptionSelectMultipleList element, String id, int option, boolean value) {
		switch (id) {
			case "archiveSelector":
			case "assetTypeSelector": {
				this.buttonPakRename.setEnabled(this.spakArchiveSelector.getOptionPositiveValues() == 1);
				this.buttonAssetImport.setEnabled(this.spakArchiveSelector.getOptionPositiveValues() == 1);
				this.refreshAssetManager();
			} break;
			case "assetSelector": {
				int selectedOptions = this.assetSelector.getOptionPositiveValues();
				this.buttonAssetReplace.setEnabled(selectedOptions == 1);
				this.buttonAssetRename.setEnabled(selectedOptions == 1);
				this.buttonAssetLoad.setEnabled(selectedOptions == 1);
				this.buttonAssetUnload.setEnabled(selectedOptions == 1);
				this.buttonAssetDelete.setEnabled(selectedOptions == 1);
			} break;
		}
	}
	
	public void resize(int x, int y, int width, int height) {
		this.prevX = x;
		this.prevY = y;
		this.prevWidth = width;
		this.prevHeight = height;
		
		int halfDragSize = 3;
		int leftPart = (int)Math.max(30, width * ProjectManager.currentProject.getPreference("screen.view.assetSelector.leftPart", float.class, 0.25F));
		int midPart = (int)(width * ProjectManager.currentProject.getPreference("screen.view.assetSelector.midPart", float.class, 0.5F));
		int rightPart = (int)(width * ProjectManager.currentProject.getPreference("screen.view.assetSelector.rightPart", float.class, 0.7F));
		
		this.viewLeftPart	.setTransforms(x + leftPart - 2, y + 1, 4, height - 2).setParameters(x, 30, midPart - 30, false);
		this.viewMidPart	.setTransforms(x + midPart - 2, y + 1, 4, height - 2).setParameters(x, leftPart + 30, rightPart - 30, false);
		this.viewRightPart	.setTransforms(x + rightPart - 2, y + 1, 4, height - 2).setParameters(x, midPart + 30, width - 30, false);
		
		this.spakArchiveSelector	.setTransforms(x + 21, y + 1, leftPart - 17 - halfDragSize, height - 2, 16);
		this.buttonPakCreate		.setTransforms(x + 4, y + height - 3 - 16, 15, height - 7 < 16 ? 0 : 15);
		this.buttonPakImport		.setTransforms(x + 4, y + height - 3 - 33, 15, height - 7 < 33 ? 0 : 15);
		this.buttonPakRename		.setTransforms(x + 4, y + height - 3 - 50, 15, height - 7 < 50 ? 0 : 15);
		this.buttonPakLoadSelected	.setTransforms(x + 4, y + height - 3 - 67, 15, height - 7 < 67 ? 0 : 15);
		this.buttonPakUnloadSelected.setTransforms(x + 4, y + height - 3 - 84, 15, height - 7 < 84 ? 0 : 15);
		this.buttonPakDelete		.setTransforms(x + 4, y + height - 3 - 101,15, height - 7 < 101? 0 : 15);
		
		this.assetTypeSelector	.setTransforms(x + leftPart + 2, y + 1, midPart - leftPart - 1, height - 2, 16);
		this.assetSelector		.setTransforms(x + midPart + 21, y + 1, rightPart - midPart - 20, height - 2, 16);
		this.buttonAssetImport	.setTransforms(x + midPart + 4, y + height - 3 - 16, 15, height - 7 < 16 ? 0 : 15);
		this.buttonAssetReplace	.setTransforms(x + midPart + 4, y + height - 3 - 33, 15, height - 7 < 33 ? 0 : 15);
		this.buttonAssetRename	.setTransforms(x + midPart + 4, y + height - 3 - 50, 15, height - 7 < 50 ? 0 : 15);
		this.buttonAssetLoad	.setTransforms(x + midPart + 4, y + height - 3 - 67, 15, height - 7 < 67 ? 0 : 15);
		this.buttonAssetUnload	.setTransforms(x + midPart + 4, y + height - 3 - 84, 15, height - 7 < 84 ? 0 : 15);
		this.buttonAssetDelete	.setTransforms(x + midPart + 4, y + height - 3 - 101,15, height - 7 < 101? 0 : 15);
	}
	
	private void refreshPakArchives() {
		this.spakArchives.size = 0;
		this.spakArchiveSelector.clearOptions();
		for (SpakArchive archive : ProjectAssetManager.INSTANCE.getSpakArchiveMap().values()) {
			if (archive != null) {
				this.spakArchives.add(archive);
				this.spakArchiveSelector.addOptions(archive.getName());
			} else Utils.logDebugWarn("[Poor chance bug] Archive `foreach$i` is null.");
		}
		this.refreshAssetManager();
		
		this.buttonPakLoadSelected.setEnabled(this.spakArchives.size > 0);
		this.buttonPakUnloadSelected.setEnabled(this.spakArchives.size > 0);
		this.buttonPakDelete.setEnabled(this.spakArchives.size > 0);
	}
	
	private void refreshAssetManager() {
		this.spakAssets.size = 0;
		this.assetSelector.clearOptions();
		for (int i = 0; i != this.spakArchiveSelector.getOptionValues().size; i++) {
			if (this.spakArchiveSelector.getOptionValue(i)) {
				for (SpakAsset<?> asset : this.spakArchives.get(i).getAssetsMap().values()) {
					for (int j = 0; j != this.assetTypeSelector.getOptionValues().size; j++) {
						if (this.assetTypeSelector.getOptionValue(j) && asset.getType() == this.assetTypes[j]) {
							this.spakAssets.add(asset);
							this.assetSelector.addOptions(asset.getPath());
							break;
						}
					}
				}
			}
		}
	}
}