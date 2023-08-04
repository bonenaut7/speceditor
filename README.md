# speceditor3
Set of tools that can assist you in creation of your projects  
Available tools: Scene editor.  
Planning tools: GLTF Scene editor, support for community created tools(through addons)

Project is currently abandoned

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
