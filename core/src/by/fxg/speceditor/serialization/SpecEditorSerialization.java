package by.fxg.speceditor.serialization;

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.kryo.Kryo;
import by.fxg.speceditor.std.objectTree.ElementStack;

import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.math.*;
import by.fxg.speceditor.serialization.gdx.GdxAttributeSerializers.*;
import by.fxg.speceditor.serialization.gdx.GdxSerializers.*;
import by.fxg.speceditor.serialization.gdx.GltfAttributeSerializers.*;
import by.fxg.speceditor.serialization.gdx.ObjectTreeSerializers.*;
import by.fxg.speceditor.serialization.gdx.SpecEditorAttributeSerializers.*;
import by.fxg.speceditor.std.g3d.attributes.*;
import by.fxg.speceditor.std.objectTree.elements.*;
import net.mgsx.gltf.scene3d.attributes.*;

public class SpecEditorSerialization {
	public static SpecEditorSerialization INSTANCE;
	public final Kryo kryo;
	
	public SpecEditorSerialization() {
		this.kryo = new Kryo();
		this.kryo.setWarnUnregisteredClasses(true);
	}
	
	public void init() {
		this.registerGdxSerializers(this.kryo);
		this.registerGdxAttributesSerializers(this.kryo);
		this.registerGltfAttributesSerializers(this.kryo);
		this.registerSpecEditorAttributesSerializers(this.kryo);
		this.registerObjectTreeSerializers(this.kryo);
	}
	
	public void registerGdxSerializers(Kryo kryo) {
		kryo.register(Vector2.class, new Vector2Serializer());
		kryo.register(Vector3.class, new Vector3Serializer());
		kryo.register(Quaternion.class, new QuaternionSerializer());
		kryo.register(Matrix4.class, new Matrix4Serializer());
		kryo.register(Color.class, new ColorSerializer());
	}
	
	public void registerGdxAttributesSerializers(Kryo kryo) {
		kryo.register(Attributes.class, new AttributesSerializer());
		kryo.register(Environment.class, new EnvironmentSerializer());
		kryo.register(Material.class, new MaterialSerializer());
		kryo.register(BlendingAttribute.class, new BlendingAttributeSerializer());
		kryo.register(ColorAttribute.class, new ColorAttributeSerializer());
		kryo.register(DepthTestAttribute.class, new DepthTestAttributeSerializer());
		kryo.register(FloatAttribute.class, new FloatAttributeSerializer());
		kryo.register(IntAttribute.class, new IntAttributeSerializer());
	}
	
	public void registerGltfAttributesSerializers(Kryo kryo) {
		kryo.register(FogAttribute.class, new PBRFogAttributeSerializer());
		kryo.register(PBRColorAttribute.class, new PBRColorAttributeSerializer());
		kryo.register(PBRFlagAttribute.class, new PBRFlagAttributeSerializer());
		kryo.register(PBRFloatAttribute.class, new PBRFloatAttributeSerializer());
	}
	
	public void registerSpecEditorAttributesSerializers(Kryo kryo) {
		kryo.register(SpecTextureAttribute.class, new SpecTextureAttributeSerializer());
		kryo.register(SpecPBRTextureAttribute.class, new SpecPBRTextureAttributeSerializer());
	}
	
	public void registerObjectTreeSerializers(Kryo kryo) {
		kryo.register(ElementStack.class, new ElementStackSerializer());
		kryo.register(ElementFolder.class, new ElementFolderSerializer());
		kryo.register(ElementLight.class, new ElementLightSerializer());
		kryo.register(ElementDecal.class, new ElementDecalSerializer());
		kryo.register(ElementModel.class, new ElementModelSerializer());
		kryo.register(ElementHitboxStack.class, new ElementHitboxStackSerializer());
		kryo.register(ElementHitbox.class, new ElementHitboxSerializer());
		kryo.register(ElementHitboxMesh.class, new ElementHitboxMeshSerializer());
	}
}
