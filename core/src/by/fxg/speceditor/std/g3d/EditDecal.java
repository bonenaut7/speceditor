package by.fxg.speceditor.std.g3d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import by.fxg.pilesos.decals.BaseDecal;
import by.fxg.speceditor.ResourceManager;

public class EditDecal extends BaseDecal {
	public String localDecalHandle = "";
	public FileHandle decalHandle = null;

	public Vector3 position;
	public Vector3 rotation;
	public Vector2 scale;

	public EditDecal() {
		this.decal = Decal.newDecal(new TextureRegion(ResourceManager.standardDecal), true);
		this.position = new Vector3();
		this.rotation = new Vector3();
		this.scale = new Vector2(0.1f, 0.1f);
		this.isBillboard = false;
	}
	
	public EditDecal setDecal(Decal decal, FileHandle decalHandle) { 
		this.decal = decal;
		this.decalHandle = decalHandle;
		return this;
	}
	
	public Decal getDecal() {
		return this.decal;
	}
	
	public Decal getDecal(Camera camera) {
		this.decal.setPosition(this.position);
		if (this.isBillboard) this.decal.lookAt(camera.position, camera.up); //this.decal.setRotation(camera.direction, camera.up);
		else this.decal.setRotation(this.rotation.x, this.rotation.y, this.rotation.z);
		this.decal.setScale(this.scale.x, this.scale.y);
		return this.decal;
	}
}
