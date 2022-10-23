package by.fxg.speceditor.std.objectTree.elements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import by.fxg.speceditor.Game;
import by.fxg.speceditor.std.g3d.EditDecal;
import by.fxg.speceditor.std.gizmos.GizmoTransformType;
import by.fxg.speceditor.std.gizmos.ITreeElementGizmos;
import by.fxg.speceditor.std.objectTree.TreeElement;

public class ElementDecal extends TreeElement implements ITreeElementGizmos,  {
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
		return Game.storage.sprites.get("icons/decal");
	}
	
	public boolean isTransformSupported(GizmoTransformType transformType) { return transformType != GizmoTransformType.SCALE; }
}
