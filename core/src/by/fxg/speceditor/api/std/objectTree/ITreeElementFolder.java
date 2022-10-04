package by.fxg.speceditor.api.std.objectTree;

import by.fxg.speceditor.std.objecttree.ElementStack;

public interface ITreeElementFolder {
	boolean isFolderAccepting(TreeElement element);
	boolean isFolderOpened();
	void setFolderOpened(boolean isFolderOpened);
	ElementStack getFolderStack();
}
