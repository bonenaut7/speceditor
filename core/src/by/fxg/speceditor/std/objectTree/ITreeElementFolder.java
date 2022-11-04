package by.fxg.speceditor.std.objectTree;

public interface ITreeElementFolder {
	boolean isFolderAccepting(TreeElement element);
	boolean isFolderOpened();
	void setFolderOpened(boolean isFolderOpened);
	ElementStack getFolderStack();
	void setFolderStack(ElementStack stack);
}
