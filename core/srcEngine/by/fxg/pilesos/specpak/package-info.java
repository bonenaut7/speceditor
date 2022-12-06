package by.fxg.pilesos.specpak;

/**
 * SpecPak Asset Management System
 * 
 * Assets are separated in different .pak archive files (PAKs)
 * Every PAK contains pakinfo index file (PAK INDEX) that contains information about assets such as:
 * 	- Name for UUID Asset
 *  - Type for UUID Asset (?)
 *  
 *  PAKs are managed by PakAssetManager (PAK Manager)
 *  After adding PAKs to the PAK Manager, it loads PAK INDEX file from PAK and stores data about
 *   available assets for loading, so in case of loading scene or prefab without having some assets
 *   you will be notified about it in the console with set DEBUG logging mode of LibGDX logger
 * 
 * Scenes projects or Prefabs made in the SpecEditor, or just packed assets can be accessed after
 * 	creating PAK Manager and adding PAKs to it.
 * 
 * */