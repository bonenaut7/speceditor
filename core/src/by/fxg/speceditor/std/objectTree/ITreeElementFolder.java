package by.fxg.speceditor.std.objectTree;

public interface ITreeElementFolder {
	default boolean isFolderAccepting(TreeElement element) { return true; }
	boolean isFolderOpened();
	void setFolderOpened(boolean opened);
	ElementStack getFolderStack();
	void setFolderStack(ElementStack stack);
}
