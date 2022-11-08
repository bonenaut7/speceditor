package by.fxg.speceditor.std.gizmos;

import com.badlogic.gdx.math.Vector3;

public interface ITreeElementGizmos {
	Vector3 gizmoVector = new Vector3();
	
	/** Displays and enables gizmos for current object if returns true **/
	default boolean enableGizmos() { return true; }
	
	/** Returns true if specified type of transform supported **/
	default boolean isTransformSupported(GizmoTransformType transformType) { return true; }
	
	/** Object transform based on specified transform type **/
	default Vector3 getTransform(GizmoTransformType transformType) { return Vector3.Zero; }
	
	/** Offset position transform of gizmos. Basically needed when treeObject has parent with own transform, and you need to return parent transform here [XXX refactor]**/
	default Vector3 getOffsetTransform(GizmoTransformType transformType) { return Vector3.Zero; }
}
