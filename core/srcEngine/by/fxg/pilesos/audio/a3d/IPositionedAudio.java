package by.fxg.pilesos.audio.a3d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public interface IPositionedAudio {
	void update(Vector3 listenerPosition, Vector2 listenerRotation);
}
