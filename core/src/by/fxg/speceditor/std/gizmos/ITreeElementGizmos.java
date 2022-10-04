package by.fxg.speceditor.std.gizmos;

import com.badlogic.gdx.math.Vector3;

public interface ITreeElementGizmos {
	Vector3 gizmoVector = new Vector3();
	
	/** Displays and enables gizmos for current object if returns true **/
	default boolean enableGizmos() { return true; }
	
	/** Returns true if specified type of transform supported **/
	boolean isTransformSupported(GizmoTransformType transformType);
	
	/** Object transform based on specified transform type **/
	Vector3 getTransform(GizmoTransformType transformType);
	
	/** Offset position transform of gizmos. Basically needed when treeObject has parent with own transform, and you need to return parent transform here [XXX refactor]**/
	Vector3 getOffsetTransform(GizmoTransformType transformType);
}
