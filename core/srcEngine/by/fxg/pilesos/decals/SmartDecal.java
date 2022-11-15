package by.fxg.pilesos.decals;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

public class SmartDecal {
	protected Decal decal;
	protected boolean isBillboard;
	
	public SmartDecal() {}
	public SmartDecal(Decal decal) { this(decal, true); }
	public SmartDecal(Decal decal, boolean isBillboard) {
		this.decal = decal;
		this.isBillboard = isBillboard;
	}
	
	public SmartDecal setDecal(Decal decal) {
		this.decal = decal;
		return this;
	}
	
	public SmartDecal setBillboard(boolean isBillboard) {
		this.isBillboard = isBillboard;
		return this;
	}
	
	public boolean isBillboard() {
		return this.isBillboard;
	}
	
	public Decal getDecal() {
		return this.decal;
	}
	
	public Decal getDecal(Camera camera) {
		if (this.isBillboard) this.decal.lookAt(camera.position, camera.up); //this.decal.setRotation(camera.direction, camera.up);
		return this.decal;
	}
}
