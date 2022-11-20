package by.fxg.speceditor.scenes.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import by.fxg.speceditor.serialization.gdx.GdxAttributeSerializers.AttributesSerializer;
import by.fxg.speceditor.serialization.gdx.GdxAttributeSerializers.BlendingAttributeSerializer;
import by.fxg.speceditor.serialization.gdx.GdxAttributeSerializers.ColorAttributeSerializer;
import by.fxg.speceditor.serialization.gdx.GdxAttributeSerializers.DepthTestAttributeSerializer;
import by.fxg.speceditor.serialization.gdx.GdxAttributeSerializers.EnvironmentSerializer;
import by.fxg.speceditor.serialization.gdx.GdxAttributeSerializers.FloatAttributeSerializer;
import by.fxg.speceditor.serialization.gdx.GdxAttributeSerializers.IntAttributeSerializer;
import by.fxg.speceditor.serialization.gdx.GdxAttributeSerializers.MaterialSerializer;
import by.fxg.speceditor.serialization.gdx.GdxSerializers.ColorSerializer;
import by.fxg.speceditor.serialization.gdx.GdxSerializers.Matrix4Serializer;
import by.fxg.speceditor.serialization.gdx.GdxSerializers.QuaternionSerializer;
import by.fxg.speceditor.serialization.gdx.GdxSerializers.Vector2Serializer;
import by.fxg.speceditor.serialization.gdx.GdxSerializers.Vector3Serializer;
import by.fxg.speceditor.serialization.gdx.GltfAttributeSerializers.PBRColorAttributeSerializer;
import by.fxg.speceditor.serialization.gdx.GltfAttributeSerializers.PBRFlagAttributeSerializer;
import by.fxg.speceditor.serialization.gdx.GltfAttributeSerializers.PBRFloatAttributeSerializer;
import by.fxg.speceditor.serialization.gdx.GltfAttributeSerializers.PBRFogAttributeSerializer;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFlagAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

public class ScenesNodeGraphDeserializer {
	protected ScenesAssetIndexer assetIndexer;
	protected FileHandle assetsFileHandle, graphFileHandle;
	
	public ScenesNodeGraphDeserializer(FileHandle assetsFileHandle, FileHandle graphFileHandle) {
		this.assetsFileHandle = assetsFileHandle;
		this.graphFileHandle = graphFileHandle;
	}
	
	public ScenesNodeGraphDeserializer(ScenesAssetIndexer assetIndexer, FileHandle graphFileHandle) {
		this.assetIndexer = assetIndexer;
		this.graphFileHandle = graphFileHandle;
	}
	
	public ScenesNodeGraph loadGraph() {
		try {
			if (this.assetIndexer == null) {
				this.assetIndexer = new ScenesAssetIndexer(this.assetsFileHandle);
				this.assetIndexer.loadAssets(true);
			}
			
			Kryo kryo = this.createKryo();
			Input input = new Input(new ByteArrayInputStream(this.graphFileHandle.readBytes()));
			ScenesNodeGraph graph = (ScenesNodeGraph)kryo.readClassAndObject(input);
			input.close();
			return graph;
		} catch (ZipException zipException) {
			zipException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		return null;
	}
	
	public ScenesAssetIndexer getAssetIndexer() {
		return this.assetIndexer;
	}
	
	protected Kryo createKryo() {
		Kryo kryo = new Kryo();
		kryo.setWarnUnregisteredClasses(true);
		kryo.register(Vector2.class, new Vector2Serializer());
		kryo.register(Vector3.class, new Vector3Serializer());
		kryo.register(Quaternion.class, new QuaternionSerializer());
		kryo.register(Matrix4.class, new Matrix4Serializer());
		kryo.register(Color.class, new ColorSerializer());
		kryo.register(Attributes.class, new AttributesSerializer());
		kryo.register(Environment.class, new EnvironmentSerializer());
		kryo.register(Material.class, new MaterialSerializer());
		kryo.register(BlendingAttribute.class, new BlendingAttributeSerializer());
		kryo.register(ColorAttribute.class, new ColorAttributeSerializer());
		kryo.register(DepthTestAttribute.class, new DepthTestAttributeSerializer());
		kryo.register(FloatAttribute.class, new FloatAttributeSerializer());
		kryo.register(IntAttribute.class, new IntAttributeSerializer());
		kryo.register(FogAttribute.class, new PBRFogAttributeSerializer());
		kryo.register(PBRColorAttribute.class, new PBRColorAttributeSerializer());
		kryo.register(PBRFlagAttribute.class, new PBRFlagAttributeSerializer());
		kryo.register(PBRFloatAttribute.class, new PBRFloatAttributeSerializer());
		
		kryo.register(TextureAttribute.class, new TextureAttributeDeserializer(this.assetIndexer));
		kryo.register(PBRTextureAttribute.class, new PBRTextureAttributeDeserializer(this.assetIndexer));
		kryo.register(ScenesNodeGraph.class, new ScenesKryoExtension.ScenesGraphSerializer());
		kryo.register(ScenesNodeGraph.NodeDecal.class, new ScenesKryoExtension.ScenesDecalSerializer());
		kryo.register(ScenesNodeGraph.NodeLight.class, new ScenesKryoExtension.ScenesLightSerializer());
		kryo.register(ScenesNodeGraph.NodeModel.class, new ScenesKryoExtension.ScenesModelSerializer());
		kryo.register(ScenesNodeGraph.NodeHitbox.class, new ScenesKryoExtension.ScenesHitboxSerializer());
		kryo.register(ScenesNodeGraph.NodeHitboxMesh.class, new ScenesKryoExtension.ScenesHitboxMeshSerializer());
		kryo.register(ScenesNodeGraph.NodeHitboxStack.class, new ScenesKryoExtension.ScenesHitboxStackSerializer());
		return kryo;
	}
	
	protected static class TextureAttributeDeserializer extends Serializer<TextureAttribute> {
		private ScenesAssetIndexer assetIndexer;
		protected TextureAttributeDeserializer(ScenesAssetIndexer assetIndexer) {
			this.assetIndexer = assetIndexer;
		}
		
		public void write(Kryo kryo, Output output, TextureAttribute object) {}
		public TextureAttribute read(Kryo kryo, Input input, Class<TextureAttribute> type) {
			TextureAttribute attribute = new TextureAttribute(Attribute.getAttributeType(input.readString()));
			boolean flipX = input.readBoolean();
			boolean flipY = input.readBoolean();
			if (input.readBoolean()) {
				UUID uuid = UUID.fromString(input.readString());
				TextureRegion region = new TextureRegion(this.assetIndexer.getAsset(Texture.class, uuid));
				region.flip(flipX, flipY);
				attribute.set(region);
			}
			return attribute;
		}
	}
	
	protected static class PBRTextureAttributeDeserializer extends Serializer<PBRTextureAttribute> {
		private ScenesAssetIndexer assetIndexer;
		protected PBRTextureAttributeDeserializer(ScenesAssetIndexer assetIndexer) {
			this.assetIndexer = assetIndexer;
		}
		
		public void write(Kryo kryo, Output output, PBRTextureAttribute object) {}
		public PBRTextureAttribute read(Kryo kryo, Input input, Class<PBRTextureAttribute> type) {
			String attributeType = input.readString();
			boolean flipX = input.readBoolean();
			boolean flipY = input.readBoolean();
			if (input.readBoolean()) {
				UUID uuid = UUID.fromString(input.readString());
				TextureRegion region = new TextureRegion(this.assetIndexer.getAsset(Texture.class, uuid));
				region.flip(flipX, flipY);
				return new PBRTextureAttribute(Attribute.getAttributeType(attributeType), region);
			}
			return null;
		}
	}
}
