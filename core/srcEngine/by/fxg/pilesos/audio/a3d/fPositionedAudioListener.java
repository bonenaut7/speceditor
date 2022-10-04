package by.fxg.pilesos.audio.a3d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class fPositionedAudioListener {
	private Camera camera = null;
	public final Vector3 listenPosition;
	public final Vector2 listenDirection;
	public Array<IPositionedAudio> soundArray;

	public fPositionedAudioListener() { this(null); }
	public fPositionedAudioListener(Camera camera) {
		this.camera = camera;
		this.listenPosition = new Vector3();
		this.listenDirection = new Vector2();
		this.soundArray = new Array<>();
	}
	
	public void update() {
		if (this.camera != null) this.listenPosition.set(this.camera.position);
		for (IPositionedAudio positionedAudio : this.soundArray) {
			positionedAudio.update(this.listenPosition, this.listenDirection);
		}
	}
	
	public fPositionedAudioListener setCamera(Camera camera) {
		this.camera = camera;
		return this;
	}
}
