package by.fxg.speceditor.std.objectTree;

public interface ITreeElementSelector<T extends TreeElement> {
	int size();
	TreeElement get(int index);
	Iterable<T> getIterable();
	
	/** Returns true if selector contains specified value **/
	boolean isElementSelected(TreeElement element);
	
	/** Returns true if selector contains specified value's parents **/
	boolean isElementOrParentsSelected(TreeElement element);
	
	/** Selects specified value **/
	void selectElement(TreeElement element);
	
	/** Deselects specified value**/
	void deselectElement(TreeElement element);
	
	/** Clears selection **/
	void clearSelection();
}
