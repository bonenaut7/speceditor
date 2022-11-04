package by.fxg.speceditor.std.objectTree.elements;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import by.fxg.speceditor.DefaultResources;
import by.fxg.speceditor.project.assets.IProjectAssetHandler;
import by.fxg.speceditor.project.assets.ProjectAsset;
import by.fxg.speceditor.project.assets.ProjectAssetManager;
import by.fxg.speceditor.std.g3d.EditDecal;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.TreeElement;
import by.fxg.speceditor.utils.IOUtils;
import by.fxg.speceditor.utils.Utils;

public class ElementDecal extends TreeElement implements ITreeElementGizmos, IProjectAssetHandler<Texture>  {
	private ProjectAsset<Texture> decalAsset = null;
	public EditDecal decal = new EditDecal();
	
	public ElementDecal() { this("New decal"); }
	public ElementDecal(String name) {
		this.displayName = name;
	}
	
	public Vector3 getTransform(GizmoTransformType transformType) {
		switch(transformType) {
			case TRANSLATE: return this.decal.position;
			case ROTATE: return this.decal.rotation;
			default: return gizmoVector.set(0, 0, 0);
		}
	}
	
	public Sprite getObjectTreeSprite() {
		return DefaultResources.INSTANCE.sprites.get("icons/decal");
	}
	
	public boolean isTransformSupported(GizmoTransformType transformType) { return transformType != GizmoTransformType.SCALE; }

	public void onAssetHandlerAdded(ProjectAsset<Texture> asset) {
		if (this.decalAsset != null) this.decalAsset.removeHandlerWithoutNotify(this);
		this.decalAsset = asset;
		this.onAssetLoad(asset);
	}
	
	public void onAssetLoad(ProjectAsset<Texture> asset) {
		this.decal.setTexture(asset.getAsset());
	}
	
	public void onAssetUnload(ProjectAsset<Texture> asset) {
		this.decal.setDefaultDecal();
	}
	
	public void serialize(IOUtils utils, DataOutputStream dos) throws IOException {
		super.serialize(utils, dos);
		if (this.decalAsset != null) {
			 dos.writeBoolean(true);
			 dos.writeUTF(this.decalAsset.getUUID().toString());
		} else dos.writeBoolean(false);
		utils.writeVector2(this.decal.scale);
		dos.writeBoolean(this.decal.isBillboard());
	}
	
	public void deserialize(IOUtils utils, DataInputStream dis) throws IOException {
		super.deserialize(utils, dis);
		if (dis.readBoolean()) {
			UUID uuid = UUID.fromString(dis.readUTF());
			ProjectAsset<Texture> projectAsset = ProjectAssetManager.INSTANCE.getAsset(Texture.class, uuid);
			if (projectAsset != null) projectAsset.addHandler(this);
			else Utils.logDebug("[ElementDecal][Deserialization] Can't find asset `", uuid.toString(), "` with Texture type loaded.");
		}
		this.decal.scale = utils.readVector2();
		this.decal.setBillboard(dis.readBoolean());
	}
}
