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
			this.elements.removeValue(element, true);
			element.setParent(null);
			return true;
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
	
	public void insertAt(int index, TreeElement element) { this.insertAt(index, element, null, true); }
	public void insertAt(int index, TreeElement element, boolean removeFromOld) { this.insertAt(index, element, null, removeFromOld); }
	public void insertAt(int index, TreeElement element, ElementStack nullStack, boolean removeFromOld) {
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
	
	//TODO remove
	public boolean selfRemove(TreeElement element) { return element != null && element.uuid != null ? this.selfRemove(element.uuid) : false; }
	public boolean selfRemove(String uuid) { return uuid != null ? this.selfRemove(UUID.fromString(uuid)) : false; }
	public boolean selfRemove(UUID uuid) {
		TreeElement element = this.searchFor(uuid);
		if (element != null) {
			if (element.getParent() instanceof ITreeElementFolder && ((ITreeElementFolder)element.getParent()).getFolderStack().elements.removeValue(element, true) || this.elements.removeValue(element, true)) {
				element.setParent(null);
				return true;
			}
		}
		return false;
	}

	protected TreeElement searchFor(UUID uuid) { return searchFor(this.elements, uuid); }
	public static TreeElement searchFor(Array<TreeElement> iterable, UUID uuid) {
		TreeElement target = null;
		for (TreeElement element : iterable) {
			if (target != null) break;
			if (element.uuid.compareTo(uuid) == 0) {
				target = element;
				break;
			}
			if (element instanceof ITreeElementFolder) {
				target = searchFor(((ITreeElementFolder)element).getFolderStack().elements, uuid);
			}
		}
		return target;
	}
}
