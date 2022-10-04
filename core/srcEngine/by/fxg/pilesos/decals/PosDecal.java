package by.fxg.pilesos.decals;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PosDecal extends BaseDecal {
	public Vector3 position;
	public Vector3 rotation;
	public Vector2 scale;

	public PosDecal(Decal decal) {
		super.decal = decal;
		this.position = new Vector3();
		this.rotation = new Vector3();
		this.scale = new Vector2(0.1f, 0.1f);
		this.isBillboard = false;
	}
	
	public BaseDecal setBillboard(boolean isBillboard) { this.isBillboard = isBillboard; return this; }
	public boolean isBillboard() { return this.isBillboard; }
	
	public Decal getDecal(Camera camera) {
		this.decal.setPosition(this.position);
		if (this.isBillboard) this.decal.lookAt(camera.position, camera.up); //this.decal.setRotation(camera.direction, camera.up);
		else this.decal.setRotation(this.rotation.x, this.rotation.y, this.rotation.z);
		this.decal.setScale(this.scale.x, this.scale.y);
		return this.decal;
	}
}
