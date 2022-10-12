package by.fxg.speceditor.prefabs;

import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.std.objectTree.ITreeElementFolder;
import by.fxg.speceditor.std.objectTree.ITreeElementHandler;
import by.fxg.speceditor.std.objectTree.SpecObjectTree;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.std.viewport.IViewportRenderer;

public class PrefabObjectTreeHandler implements ITreeElementHandler {
	private PrefabProject prefabProject;
	
	public PrefabObjectTreeHandler(PrefabProject prefabProject) {
		this.prefabProject = prefabProject;
	}
	
	public boolean onDropdownClick(SpecObjectTree objectTree, String id) {
		//this.prefabProject.renderer.clear(true); //idk check required XXX, makes flick when adding element to objecttree
		return false;
	}
	
	public void onRefresh(SpecObjectTree objectTree) {
		this.prefabProject.projectScreen.subEditorPane.updateSelectableEditorPane(objectTree.elementSelector);
		this.prefabProject.projectScreen.subViewport.gizmosModule.updateSelectorMode(objectTree.elementSelector);

		this.prefabProject.renderer.clear();
		this.searchRenderables(this.prefabProject.renderer, objectTree, objectTree.getStack().getElements(), true);
	}
	
	private void searchRenderables(IViewportRenderer renderer, SpecObjectTree objectTree, Array<TreeElement> elements, boolean parentVisible) { 
		for (TreeElement element : elements) {
			if (element != null) {
				
				
				if ((parentVisible && element.isVisible() || objectTree.elementSelector.isElementSelected(element))) {
//					if (element instanceof IModelProvider || element instanceof IDebugDraw) renderer.add(objectTree, element);
//					if (element instanceof ElementLight) renderer.add(objectTree, element);
					renderer.add(objectTree, element);
					//if (element instanceof ElementDecal) renderer.add(((ElementDecal)element).decal);
				}
				if (element instanceof ITreeElementFolder) {
//					if (element instanceof ElementMultiHitbox && (parentVisible && element.isVisible() || objectTree.selectedItems.contains(element, true))) {
//						for (__TreeElement element$ : element.getStack().getElements()) {
//							if (element$.isVisible() || objectTree.selectedItems.contains(element$, true)) {
//								if (element$ instanceof IDebugDraw) renderer.add(element$);
//							}
//						}
//					} else
					this.searchRenderables(renderer, objectTree, ((ITreeElementFolder)element).getFolderStack().getElements(), parentVisible ? element.isVisible() : parentVisible);
				}
			}
		}
	}
}
