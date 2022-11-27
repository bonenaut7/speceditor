package by.fxg.speceditor.std.viewport;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import by.fxg.speceditor.std.objectTree.ITreeElementModelProvider;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.lights.SpotLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.scene.Updatable;
import net.mgsx.gltf.scene3d.shaders.PBRCommon;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.EnvironmentCache;

/** Override class from #SceneManager **/
public class GLTFRendererScene implements Disposable {
	public static GLTFRendererScene create(GLTFRenderer renderer, Camera camera, int bones, int pointLights, int spotLights, int directionalLights) {
		PBRShaderConfig defaultConfig = PBRShaderProvider.createDefaultConfig();
		defaultConfig.numBones = bones;
		defaultConfig.numPointLights = pointLights;
		defaultConfig.numSpotLights = spotLights;
		defaultConfig.numDirectionalLights = directionalLights;
		DepthShader.Config depthConfig = PBRShaderProvider.createDefaultDepthConfig();
		depthConfig.numBones = bones;
		return new GLTFRendererScene(renderer, camera, PBRShaderProvider.createDefault(defaultConfig), PBRShaderProvider.createDefaultDepth(depthConfig));
	}

	// =================================================================================================================================
	public final GLTFRenderer gltfRenderer;
	public final Array<RenderableProvider> renderableProviders = new Array<>();
	public final Array<ITreeElementModelProvider> treeElementRenderableProviders = new Array<>();
	public final Camera camera;
	public ModelBatch batch;
	public ModelBatch depthBatch;
	public SceneSkybox skyBox;
	public Environment environment = new Environment();
	public final EnvironmentCache computedEnvironement = new EnvironmentCache();
	
	private RenderableSorter renderableSorter;
	private PointLightsAttribute pointLights = new PointLightsAttribute();
	private SpotLightsAttribute spotLights = new SpotLightsAttribute();
	private ModelInstance modelGrid;

	public GLTFRendererScene(GLTFRenderer renderer, Camera camera, ShaderProvider shaderProvider, DepthShaderProvider depthShaderProvider) {
		this(renderer, camera, shaderProvider, depthShaderProvider, new SceneRenderableSorter());
	}
	
	public GLTFRendererScene(GLTFRenderer renderer, Camera camera, ShaderProvider shaderProvider, DepthShaderProvider depthShaderProvider, RenderableSorter renderableSorter) {
		this.gltfRenderer = renderer;
		this.camera = camera;
		this.renderableSorter = renderableSorter;

		this.batch = new ModelBatch(shaderProvider, renderableSorter);
		this.depthBatch = new ModelBatch(depthShaderProvider);
		this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4F, 0.4F, 0.4F, 1.0F));
		
		ModelBuilder mb = new ModelBuilder();
		this.modelGrid = new ModelInstance(mb.createLineGrid(1000, 1000, 1F, 1F, new Material(), Usage.Position | Usage.Normal | Usage.ColorUnpacked));
		this.modelGrid.materials.get(0).set(PBRColorAttribute.createDiffuse(1, 1, 1, 0.33F), new BlendingAttribute(0.5F), PBRFloatAttribute.createAlphaTest(0.1F));
	}

	/** should be called in order to perform light culling, skybox update and animations.
	 *  @param delta */
	public void update(float delta) {
		if (this.camera != null) {
			this.updateEnvironment();
			for (RenderableProvider renderableProvider : this.renderableProviders) {
				if (renderableProvider instanceof Updatable) {
					((Updatable)renderableProvider).update(this.camera, delta);
				}
			}
			if (this.skyBox != null) this.skyBox.update(this.camera, delta);
		}
	}

	protected void updateEnvironment() {
		this.computedEnvironement.setCache(this.environment);
		this.pointLights.lights.clear();
		this.spotLights.lights.clear();
		if (this.environment != null) {
			for (Attribute attribute : this.environment) {
				if (attribute instanceof PointLightsAttribute) {
					this.pointLights.lights.addAll(((PointLightsAttribute)attribute).lights);
					this.computedEnvironement.replaceCache(this.pointLights);
				} else if (attribute instanceof SpotLightsAttribute) {
					this.spotLights.lights.addAll(((SpotLightsAttribute)attribute).lights);
					this.computedEnvironement.replaceCache(this.spotLights);
				} else {
					this.computedEnvironement.set(attribute);
				}
			}
		}
		this.cullLights();
	}

	protected void cullLights() {
		PointLightsAttribute pointLightsAttribute = environment.get(PointLightsAttribute.class, PointLightsAttribute.Type);
		if (pointLightsAttribute != null) {
			for (PointLight light : pointLightsAttribute.lights) {
				if (light instanceof PointLightEx) {
					PointLightEx lightEx = (PointLightEx) light;
					if (lightEx.range != null && !this.camera.frustum.sphereInFrustum(lightEx.position, lightEx.range)) {
						this.pointLights.lights.removeValue(lightEx, true);
					}
				}
			}
		}
		SpotLightsAttribute spotLightsAttribute = environment.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
		if (spotLightsAttribute != null) {
			for (SpotLight light : spotLightsAttribute.lights) {
				if (light instanceof SpotLightEx) {
					SpotLightEx lightEx = (SpotLightEx)light;
					if (lightEx.range != null && !this.camera.frustum.sphereInFrustum(lightEx.position, lightEx.range)) {
						this.spotLights.lights.removeValue(lightEx, true);
					}
				}
			}
		}
	}

	/** render all scenes. because shadows use frame buffers, if you need to render scenes to a frame buffer,
	 *  you should instead first call {@link #renderShadows()}, bind your frame buffer and then call {@link #renderColors()} */
	public void render() {
		this.renderShadows();
		this.renderColors();
	}

	/** Render shadows only to interal frame buffers. (useful when you're using your
	 *  own frame buffer to render scenes) */
	@SuppressWarnings("deprecation")
	public void renderShadows() {
		DirectionalLight light = getFirstDirectionalLight();
		if (light instanceof DirectionalShadowLight) {
			DirectionalShadowLight shadowLight = (DirectionalShadowLight) light;
			shadowLight.begin();
			renderDepth(shadowLight.getCamera());
			shadowLight.end();
			this.environment.shadowMap = shadowLight;
		} else {
			this.environment.shadowMap = null;
		}
	}

	/** Render only depth (packed 32 bits), usefull for post processing effects. You
	 *  typically render it to a FBO with depth enabled. */
	private void renderDepth(Camera camera) {
		this.depthBatch.begin(camera);
		this.depthBatch.render(this.renderableProviders);
		for (ITreeElementModelProvider modelProvider : this.treeElementRenderableProviders) {
			this.batch.render(modelProvider.applyTransforms().getRenderableProvider());
		}
		this.depthBatch.end();
	}

	/** Render colors only. You should call {@link #renderShadows()} before. (useful
	 *  when you're using your own frame buffer to render scenes) */
	public void renderColors() {
		PBRCommon.enableSeamlessCubemaps();
		this.computedEnvironement.shadowMap = this.environment.shadowMap;
		this.batch.begin(this.camera);
		if (this.gltfRenderer.featureRenderGrid) this.batch.render(this.modelGrid, this.computedEnvironement);
		this.batch.render(this.renderableProviders, this.computedEnvironement);
		for (ITreeElementModelProvider modelProvider : this.treeElementRenderableProviders) {
			this.batch.render(modelProvider.applyTransforms().getRenderableProvider(), this.computedEnvironement);
		}
		if (this.skyBox != null) this.batch.render(this.skyBox);
		this.batch.end();
	}

	public void updateViewport(float width, float height) {
		if (this.camera != null) {
			this.camera.viewportWidth = width;
			this.camera.viewportHeight = height;
			this.camera.update(true);
		}
	}
	
	public void reset() {
		this.renderableProviders.clear();
		this.treeElementRenderableProviders.clear();
		this.pointLights.lights.clear();
		this.spotLights.lights.clear();
		DirectionalLightsAttribute attribute = environment.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
		if (attribute != null) attribute.lights.clear();
	}
	
	//=================================================================
	
	public DirectionalLight getFirstDirectionalLight() {
		DirectionalLightsAttribute attribute = environment.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
		if (attribute != null) {
			for (DirectionalLight light : attribute.lights) {
				if (light instanceof DirectionalLight) {
					return (DirectionalLight)light;
				}
			}
		}
		return null;
	}
	
	public ModelBatch getBatch() {
		return this.batch;
	}
	
	public void addScene(Scene scene) { this.addScene(scene, true);	}
	public void addScene(Scene scene, boolean appendLights) {
		this.renderableProviders.add(scene);
		if (appendLights) {
			for (Entry<Node, BaseLight> entry : scene.lights) {
				this.environment.add(entry.value);
			}
		}
	}
	
	public void removeScene(Scene scene) {
		this.renderableProviders.removeValue(scene, true);
		for (Entry<Node, BaseLight> entry : scene.lights) {
			this.environment.remove(entry.value);
		}
	}
	
	public void setSkyBox(SceneSkybox skyBox) {
		this.skyBox = skyBox;
	}

	public void setShaderProvider(ShaderProvider shaderProvider) {
		this.batch.dispose();
		this.batch = new ModelBatch(shaderProvider, renderableSorter);
	}

	public void setDepthShaderProvider(DepthShaderProvider depthShaderProvider) {
		this.depthBatch.dispose();
		this.depthBatch = new ModelBatch(depthShaderProvider);
	}
	
	@Override
	public void dispose() {
		this.batch.dispose();
		this.depthBatch.dispose();
	}
}
