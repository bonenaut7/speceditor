package by.fxg.specscenes;

import java.util.UUID;

public class HitboxMesh extends Hitbox {
	/** Index of asset that contains mesh data for generating hitbox **/
	public UUID assetIndex;
	/** Nodes for generating hitbox. <br>
	 * null - not generate, int[] with 0 length - generate from all nodes, in other case use id's from the array **/
	public int[] nodes;
}
