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
		//GdxSerializers
		this.kryo.register(Vector2.class, new Vector2Serializer());
		this.kryo.register(Vector3.class, new Vector3Serializer());
		this.kryo.register(Quaternion.class, new QuaternionSerializer());
		this.kryo.register(Matrix4.class, new Matrix4Serializer());
		this.kryo.register(Color.class, new ColorSerializer());
		
		//GdxAttributeSerializers
		this.kryo.register(Attributes.class, new AttributesSerializer());
		this.kryo.register(Environment.class, new EnvironmentSerializer());
		this.kryo.register(Material.class, new MaterialSerializer());
		this.kryo.register(BlendingAttribute.class, new BlendingAttributeSerializer());
		this.kryo.register(ColorAttribute.class, new ColorAttributeSerializer());
		this.kryo.register(DepthTestAttribute.class, new DepthTestAttributeSerializer());
		this.kryo.register(FloatAttribute.class, new FloatAttributeSerializer());
		this.kryo.register(IntAttribute.class, new IntAttributeSerializer());
		
		//GltfAttributeSerializers
		this.kryo.register(FogAttribute.class, new PBRFogAttributeSerializer());
		this.kryo.register(PBRColorAttribute.class, new PBRColorAttributeSerializer());
		this.kryo.register(PBRFlagAttribute.class, new PBRFlagAttributeSerializer());
		this.kryo.register(PBRFloatAttribute.class, new PBRFloatAttributeSerializer());
		
		//SpecEditorAttributeSerializers
		this.kryo.register(SpecTextureAttribute.class, new SpecTextureAttributeSerializer());
		this.kryo.register(SpecPBRTextureAttribute.class, new SpecPBRTextureAttributeSerializer());
		
		//SpecEditorSerializers
		
		
		//ObjectTreeSerializers
		this.kryo.register(ElementStack.class, new ElementStackSerializer());
		this.kryo.register(ElementFolder.class, new ElementFolderSerializer());
		this.kryo.register(ElementLight.class, new ElementLightSerializer());
		this.kryo.register(ElementDecal.class, new ElementDecalSerializer());
		this.kryo.register(ElementModel.class, new ElementModelSerializer());
		this.kryo.register(ElementHitboxStack.class, new ElementHitboxStackSerializer());
		this.kryo.register(ElementHitbox.class, new ElementHitboxSerializer());
		this.kryo.register(ElementHitboxMesh.class, new ElementHitboxMeshSerializer());
	}
}
