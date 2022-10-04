package by.fxg.pilesos.decals;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.decals.GroupStrategy;
import com.badlogic.gdx.utils.Array;

public class DecalDrawer {
	public Array<BaseDecal> decalsToProduce;
	public GroupStrategy groupStrategy;
	public DecalBatch batch;
	
	public DecalDrawer(GroupStrategy groupStrategy) {
		this.decalsToProduce = new Array<>();
		this.batch = new DecalBatch(this.groupStrategy = groupStrategy);
	}
	
	public void draw(Camera camera) {
		for (BaseDecal decal : this.decalsToProduce) {
			if (decal.getDecal() != null) {
				this.batch.add(decal.getDecal(camera));
			}
		}
		this.batch.flush();
	}
}
