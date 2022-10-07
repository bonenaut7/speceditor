package by.fxg.speceditor.std.objectTree.impl;

import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.std.objectTree.ITreeElementSelector;
import by.fxg.speceditor.std.objectTree.TreeElement;

public class DefaultTreeElementSelector implements ITreeElementSelector<TreeElement> {
	private Array<TreeElement> array = new Array<>();
	
	public int size() {
		return this.array.size;
	}
	
	public TreeElement get(int index) {
		return this.array.get(index);
	}
	
	public Iterable<TreeElement> getIterable() {
		return this.array;
	}
	
	public boolean isElementSelected(TreeElement element) {
		return this.array.contains(element, true);
	}
	
	public void selectElement(TreeElement element) {
		if (element != null && !this.array.contains(element, true)) {
			this.array.add(element);
		}
	}

	public void deselectElement(TreeElement element) {
		if (element != null) {
			this.array.removeValue(element, true);
		}
	}

	public void clearSelection() {
		this.array.size = 0;
	}	
}
