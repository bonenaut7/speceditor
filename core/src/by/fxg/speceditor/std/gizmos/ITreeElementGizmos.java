package by.fxg.speceditor.std.gizmos;

import com.badlogic.gdx.math.Vector3;

public interface ITreeElementGizmos {
	Vector3 gizmoVector = new Vector3();
	Vector3 tmpVector = new Vector3();
	
	/** Displays and enables gizmos for current object if returns true **/
	default boolean enableGizmos() { return true; }
	
	/** Returns true if specified type of transform supported **/
	default boolean isTransformSupported(GizmoTransformType transformType) { return true; }
	
	/** Object transform based on specified transform type **/
	default Vector3 getTransform(GizmoTransformType transformType) { return Vector3.Zero; }
	
	/** Offset position transform of gizmos. Returns inputVector with getTransform(...) of parent objects **/
	default Vector3 getOffsetTransform(Vector3 inputVector, GizmoTransformType transformType) { return inputVector; }
}
