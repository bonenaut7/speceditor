package by.fxg.speceditor.utils;

public interface ApplicationTools {
	/** Returns true if allowed app stopping **/
	boolean isAppExitAllowed();
	/** Exits app explicitly, not asking for {@link #isAppExitAllowed()} permission **/
	void exitExplicitly();
}
