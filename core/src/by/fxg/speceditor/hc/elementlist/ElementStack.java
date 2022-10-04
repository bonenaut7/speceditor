package by.fxg.speceditor.hc.elementlist;

import java.util.UUID;

import com.badlogic.gdx.utils.Array;

public class ElementStack {
	private Array<TreeElement> elements = new Array<>();
	private TreeElement parent;
	
	public ElementStack() { this(null); }
	public ElementStack(TreeElement element) {
		this.parent = element;
	}
	
	public boolean add(TreeElement element) {
		if (element != null) {
			element.parent = this.parent;
			this.elements.add(element);
			return true;
		}
		return false;
	}
	
	public boolean remove(TreeElement element) { return element != null && element.uuid != null ? this.remove(element.uuid) : false; }
	public boolean remove(String uuid) { return uuid != null ? this.remove(UUID.fromString(uuid)) : false; }
	public boolean remove(UUID uuid) {
		TreeElement element = this.searchFor(uuid);
		if (element != null) {
			if (element.parent != null) {
				return element.parent.getStack().elements.removeValue(element, true);
			}
			return this.elements.removeValue(element, true);
		}
		return false;
	}
	
	public TreeElement find(UUID uuid) {
		for (TreeElement element : this.elements) {
			if (element.uuid.equals(uuid)) return element;
		}
		return null;
	}
	
	public Array<TreeElement> getItems() { return this.elements; }
	
	protected TreeElement searchFor(UUID uuid) { return searchFor(this.elements, uuid); }
	public static TreeElement searchFor(Array<TreeElement> iterable, UUID uuid) {
		TreeElement target = null;
		for (TreeElement element : iterable) {
			if (target != null) break;
			if (element.uuid.compareTo(uuid) == 0) {
				target = element;
				break;
			}
			if (element.hasStack()) {
				target = searchFor(element.getStack().elements, uuid);
			}
		}
		return target;
	}
	
	public void clear() {
		for (TreeElement element : this.elements) {
			if (element.hasStack()) {
				element.getStack().clear();
			}
		}
		this.elements.clear();
	}
}
