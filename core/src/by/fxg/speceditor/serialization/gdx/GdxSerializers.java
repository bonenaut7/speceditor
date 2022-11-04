package by.fxg.speceditor.serialization.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class GdxSerializers {
	public static class Vector2Serializer extends Serializer<Vector2> {
		public void write(Kryo kryo, Output output, Vector2 object) {
			output.writeFloat(object.x);
			output.writeFloat(object.y);
		}

		public Vector2 read(Kryo kryo, Input input, Class<Vector2> type) {
			return new Vector2(input.readFloat(), input.readFloat());
		}
	}
	
	public static class Vector3Serializer extends Serializer<Vector3> {
		public void write(Kryo kryo, Output output, Vector3 object) {
			output.writeFloat(object.x);
			output.writeFloat(object.y);
			output.writeFloat(object.z);
		}

		public Vector3 read(Kryo kryo, Input input, Class<Vector3> type) {
			return new Vector3(input.readFloat(), input.readFloat(), input.readFloat());
		}
	}
	
	public static class QuaternionSerializer extends Serializer<Quaternion> {
		public void write(Kryo kryo, Output output, Quaternion object) {
			output.writeFloat(object.x);
			output.writeFloat(object.y);
			output.writeFloat(object.z);
			output.writeFloat(object.w);
		}

		public Quaternion read(Kryo kryo, Input input, Class<Quaternion> type) {
			return new Quaternion(input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat());
		}
	}

	public static class Matrix4Serializer extends Serializer<Matrix4> {
		public void write(Kryo kryo, Output output, Matrix4 object) {
			output.writeShort(object.val.length);
			output.writeFloats(object.val);
		}

		public Matrix4 read(Kryo kryo, Input input, Class<Matrix4> type) {
			return new Matrix4(input.readFloats(input.readShort()));
		}
	}
	
	public static class ColorSerializer extends Serializer<Color> {
		public void write(Kryo kryo, Output output, Color object) {
			output.writeFloat(object.r);
			output.writeFloat(object.g);
			output.writeFloat(object.b);
			output.writeFloat(object.a);
		}

		public Color read(Kryo kryo, Input input, Class<Color> type) {
			return new Color(input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat());
		}
	}
}
