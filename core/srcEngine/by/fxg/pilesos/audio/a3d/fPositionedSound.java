package by.fxg.pilesos.audio.a3d;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class fPositionedSound implements IPositionedAudio {
	private final Sound sound;
	private final Vector3 position;
	
	private long soundID;
	private float baseVolume = 1f;
	private float maxDistance = 10f;	
	
	public fPositionedSound(Sound sound) {
		this.sound = sound;
		this.position = new Vector3();
	}
	
	public fPositionedSound setSoundID(long soundID) {
		this.soundID = soundID;
		return this;
	}
	
	public fPositionedSound setBaseVolume(float baseVolume) {
		this.baseVolume = baseVolume;
		return this;
	}
	
	public fPositionedSound setPosition(float x, float y, float z) {
		this.position.set(x, y, z);
		return this;
	}
	
	public fPositionedSound setPosition(Vector3 vector) {
		this.position.set(vector);
		return this;
	}
	
	public fPositionedSound setMaxDistance(float maxDistance) {
		this.maxDistance = maxDistance;
		return this;
	}
	
	public void update(Vector3 listenerPosition, Vector2 listenerRotation) {
		//(dstAngle - 180 + lookX + 90) / 90 = angle
		float volume = Interpolation.linear.apply(this.baseVolume, 0f, Math.min(this.position.dst(listenerPosition), this.maxDistance) / this.maxDistance);
		float angle = (float)Math.toDegrees(MathUtils.atan2(this.position.z - listenerPosition.z, this.position.x - listenerPosition.x));
		this.sound.setPan(this.soundID, (angle + listenerRotation.x - 90F) / 90F, volume);
	}
}
