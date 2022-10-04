package by.fxg.pilesos.decals;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

public class BaseDecal {
	protected Decal decal;
	protected boolean isVisible, isBillboard;
	
	public BaseDecal() {}
	public BaseDecal(Decal decal) { this(decal, true, true); }
	public BaseDecal(Decal decal, boolean isVisible) { this(decal, isVisible, true); }
	public BaseDecal(Decal decal, boolean isVisible, boolean isBillboard) {
		this.decal = decal;
		this.isVisible = isVisible;
		this.isBillboard = isBillboard;
	}
	
	public BaseDecal setVisible(boolean isVisible) { this.isVisible = isVisible; return this; }
	public boolean isVisible() { return this.isVisible; }
	
	public BaseDecal setBillboard(boolean isBillboard) { this.isBillboard = isBillboard; return this; }
	public boolean isBillboard() { return this.isBillboard; }

	public BaseDecal setDecal(Decal decal) { this.decal = decal; return this; }
	
	public Decal getDecal() { return this.decal; }
	public Decal getDecal(Camera camera) {
		if (this.isBillboard) this.decal.lookAt(camera.position, camera.up); //this.decal.setRotation(camera.direction, camera.up);
		return this.decal;
	}
}
