package by.fxg.speceditor.std.objectTree;

import java.util.UUID;

import com.badlogic.gdx.utils.Array;

import by.fxg.speceditor.utils.Utils;

public class ElementStack {
	private Array<TreeElement> elements = new Array<>();
	private TreeElement stackElement;
	
	public ElementStack() { this(null); }
	public ElementStack(TreeElement element) {
		this.stackElement = element;
	}
	
	public boolean add(TreeElement element) {
		if (element != null) {
			element.setParent(this.stackElement);
			this.elements.add(element);
			return true;
		}
		return false;
	}
	
	public boolean remove(TreeElement element) {
		if (element != null) {
			element.setParent(null);
			return this.elements.removeValue(element, true);
		}
		return false;
	}
	
	public TreeElement findHere(UUID uuid) {
		for (TreeElement element : this.elements) {
			if (element.uuid.equals(uuid)) return element;
		}
		return null;
	}
	
	public int findIndexHere(UUID uuid) {
		for (int i = 0; i != this.elements.size; i++) {
			if (this.elements.get(i).uuid.equals(uuid)) return i;
		}
		return -1;
	}
	
	public boolean removeElementFromHierarchy(TreeElement element) {
		if (element == null) return false;
		if (element.parent != null && element.parent instanceof ITreeElementFolder) {
			if (((ITreeElementFolder)element.parent).getFolderStack().elements.removeValue(element, true)) {
				element.parent = null;
				return true;
			}
		}
		return this.removeElementFromHierarchy(this, element.uuid);
	}
	
	public boolean removeElementFromHierarchy(UUID uuid) { return this.removeElementFromHierarchy(this, uuid); }
	private boolean removeElementFromHierarchy(ElementStack stack, UUID uuid) {
		for (int i = 0; i != stack.elements.size; i++) {
			if (stack.elements.get(i).uuid.equals(uuid)) {
				stack.elements.removeIndex(i);
				return true;
			}
			if (stack.elements.get(i) instanceof ITreeElementFolder && this.removeElementFromHierarchy(((ITreeElementFolder)stack.elements.get(i)).getFolderStack(), uuid)) {
				return true;
			}
		}
		return false;
	}
	
	public void insertAt(int index, TreeElement element) { this.insertAt(index, element, true); }
	public void insertAt(int index, TreeElement element, boolean removeFromOld) {
		element.setParent(this.stackElement, removeFromOld, false);
		this.elements.insert(index, element);
	}
	
	public void insertAt(int startIndex, Iterable<? extends TreeElement> elements) { this.insertAt(startIndex, elements, null, true); }
	public void insertAt(int startIndex, Iterable<? extends TreeElement> elements, boolean removeFromOld) { this.insertAt(startIndex, elements, null, removeFromOld); }
	public void insertAt(int startIndex, Iterable<? extends TreeElement> elements, ElementStack nullStack, boolean removeFromOld) {
		int _index = startIndex;
		for (TreeElement element : elements) {
			try {
				//FIXME BUG-011020220: Insert not working while <element> is in the array, and <element> index is bigger than target index.
				//P.S. LibGDX Issues|TommyTEttinger: Insert not supposed to do this thing
				if (this.elements.indexOf(element, true) > _index) {					
					this.elements.removeValue(element, true);
					this.elements.reverse();
					this.elements.add(element);
					this.elements.reverse();
					this.elements.insert(_index + 1, element);
				} else this.elements.insert(_index, element);
				element.setParent(nullStack, this.stackElement, removeFromOld, false);
				_index++;
			} catch (IndexOutOfBoundsException indexOutOfBoundsException) {
				Utils.logError(indexOutOfBoundsException, "ElementStack", "IOB at insertAt(int, Iterable<>, boolean). " + indexOutOfBoundsException.getMessage());
			}
		}
	}
	
	public Array<TreeElement> getElements() { 
		return this.elements; 
	}
	
	public void clear() {
		for (TreeElement element : this.elements) {
			if (element instanceof ITreeElementFolder) {
				((ITreeElementFolder)element).getFolderStack().clear();
			}
		}
		this.elements.clear();
	}
}
