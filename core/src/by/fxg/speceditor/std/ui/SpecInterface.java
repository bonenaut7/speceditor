package by.fxg.speceditor.std.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import by.fxg.speceditor.DefaultResources;

public class SpecInterface {
	public static SpecInterface INSTANCE;
	public IFocusable currentFocus = null;
	private AppCursor _prevCursor = AppCursor.UNAVAILABLE, _cursor = AppCursor.ARROW; 
	
	public void onUpdate() {
		if (this._cursor != this._prevCursor) {
			Gdx.graphics.setCursor(DefaultResources.INSTANCE.getCursor(this._cursor));
			this._prevCursor = this._cursor;
		}
		this._cursor = AppCursor.ARROW;
	}
	
	public static void init() {
		INSTANCE = new SpecInterface();
	}
	
	public static boolean isFocused(Object focusable) {
		return INSTANCE.currentFocus == null || INSTANCE.currentFocus == focusable;
	}
	
	public static void setCursor(AppCursor cursor) {
		INSTANCE._cursor = cursor;
	}
	
	//=[Inner classes area]=//
	
	/** Provides ability to focus-on single object, while focusing using {@link #onFocusAdded()} for new and {@link #onFocusRemoved()} for old objects.**/
	public static interface IFocusable {
		/** Sets focus for current object and resets for old **/
		default void setFocused(boolean value) {
			if (value) {
				if (SpecInterface.INSTANCE.currentFocus != null && SpecInterface.INSTANCE.currentFocus != this) SpecInterface.INSTANCE.currentFocus.onFocusRemoved();
				SpecInterface.INSTANCE.currentFocus = this;
				this.onFocusAdded();
			} else if (SpecInterface.INSTANCE.currentFocus == this) { //thinking about everlasting setFocusing(false) from everywhere, so it won't break logic of other objects
				SpecInterface.INSTANCE.currentFocus.onFocusRemoved();
				SpecInterface.INSTANCE.currentFocus = null;
			}
		}
		
		/** Called when object gets focus **/ default void onFocusAdded() {}
		/** Called when object loses focus **/ default void onFocusRemoved() {}
		
		default boolean isFocused() {
			return SpecInterface.INSTANCE.currentFocus == this;
		}
	}
	
	public static interface IDragNDrop {

	}
	
	/** App cursor types **/
	public static enum AppCursor {
		ARROW,
		CROSS, IBEAM,
		GRAB, GRABBING,
		POINT, POINTING,
		UNAVAILABLE, DEAD, HELP,
		COLORPICKER, PEN,
		ZOOM_IN, ZOOM_OUT,
		RESIZE,
		RESIZE_VERTICAL,
		RESIZE_HORIZONTAL,
		RESIZE_NESW,
		RESIZE_NWSE;
	}
	
	/** Basic colors class **/
	public static class UColor {
		public static final Color 
			white = new Color(1f, 1f, 1f, 1f),
			gray = new Color(0.25f, 0.25f, 0.25f, 1f),
			redblack = new Color(0.35f, 0.25f, 0.25f, 1f),
			greenblack = new Color(0.25f, 0.35f, 0.25f, 1f),
			blueblack = new Color(0.25f, 0.25f, 0.35f, 1f),
			yellowblack = new Color(0.35f, 0.35f, 0.25f, 1f),
			aquablack = new Color(0.25f, 0.35f, 0.35f, 1f),
					
			redgray = new Color(0.55f, 0.25f, 0.25f, 1f),
			greengray = new Color(0.25f, 0.55f, 0.25f, 1f),
			bluegray = new Color(0.25f, 0.25f, 0.55f, 1f),
			yellowgray = new Color(0.55f, 0.55f, 0.25f, 1f),
			aquagray = new Color(0.25f, 0.55f, 0.55f, 1f),
			
			background = new Color(0.12f, 0.12f, 0.12f, 1f),
			black025alpha = new Color(0f, 0f, 0f, 0.25f),

			elementDefaultColor = new Color(0.25f, 0.25f, 0.25f, 1.0f),
			elementIntensiveColor = new Color(1.0f, 1.0f, 1.0f, 1.0f),
			elementHover = new Color(1.0f, 1.0f, 1.0f, 0.2f),
			elementBoundsClicked = new Color(1.0f, 1.0f, 1.0f, 0.5f)
			;
		
		public static final Vector3
			hitbox = new Vector3(0.12f, 0.88f, 0.12f),
			hitboxSelected = new Vector3(0.88f, 0.88f, 0.12f),
			point = new Vector3(0.35f, 0.35f, 0.85f),
			pointSelected = new Vector3(0.75f, 0.35f, 0.85f);
	}
}
