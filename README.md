# speceditor3
Set of tools that can assist you in creation of your projects  
Available tools: Scene editor.  
Planning tools: GLTF Scene editor, support for community created tools(through addons)

Project is currently abandoned
# Roadmap that will be completed if i'm will switch from fucking AMD GPU
* Remake whole UI with ImGui
* Add styles support for ImGui (i promise we need it, it's very important)
* Do something with 'PAK'-shit-ass-ets, maybe external packer would be great idea
* Replace node graphs with flexible and extensive API
* Make better prefabs
* Add support for different compressed textures like S3TC and Block Compression, Basis would be great too
* Add support for different viewports (standalone ones and combined, for example diffuse+collision debug)
* Make better abstraction for IPhysObject, add better flexibility that allows to switch between bullet and something else
* A lot of documentation for all this shit would be great bonus, i forgot what the fuck is bullet masks and filters
* Fix addons and implement BeanShell as addon-scripting because java addons sounds painful without ASM
* Release that thing as 4th iteration of this editor and rename it to 'Spec', just 'Spec'
* REPLACE GIZMOS
* do some commit crimes and celebrate

# Quick start
```java
PakAssetManager pakAssetManager = new PakAssetManager();
pakAssetManager.addPackedAssets("Assets", Gdx.files.classpath("assets/myassets.pak"));
pakAssetManager.queueLoadAssetsFrom("Assets").getAssetManager().finishLoading();
ScenesNodeGraphDeserializer = new ScenesNodeGraphDeserializer(pakAssetManager);
ScenesNodeGraph nodeGraph = deserializer.loadGraph(Gdx.files.classpath("assets/myscene.ssf"));
//init scene with nodeGraph elements or use SceneRenderGraph as followed down below
SceneRenderGraph scene = new SceneRenderGraph(pakAssetManager, nodeGraph);
//in SceneRenderGraph editor objects represented in Arrays: lights, decals, modelInstances, physObjects
```
Or in case of you're using multiple asset paks:
```java
pakAssetManager.addPackedAssets(Gdx.files.classpath("assets/pak0.pak"), Gdx.files.classpath("assets/pak1.pak"));
pakAssetManager.queueLoadAssetsFrom("pak0").queueLoadAssetsFrom("pak1").getAssetManager().finishLoading();
```
But remember: You should use names for asset paks as it's named in the editor, loading multiple assets using files names as packed asset name

# Screenshots
![saf](https://user-images.githubusercontent.com/36343628/205468310-a17a81a2-ae51-4a2f-bfa4-9cd6abaeaf63.png)
![image](https://user-images.githubusercontent.com/36343628/201841428-e7a7bd1b-2d01-490d-a76f-a28b888c69ec.png)
