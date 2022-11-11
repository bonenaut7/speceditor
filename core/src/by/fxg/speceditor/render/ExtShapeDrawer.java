package by.fxg.speceditor.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.SideEstimator;

public class ExtShapeDrawer extends ShapeDrawer {
	public ExtShapeDrawer(Batch batch) {
		super(batch);
	}

	public ExtShapeDrawer(Batch batch, TextureRegion region) {
        super(batch, region);
    }

	public ExtShapeDrawer(Batch batch, TextureRegion region, SideEstimator sideEstimator) {
        super(batch, region, sideEstimator);
	}
	
	public void rectangle(float x, float y, float width, float height, float lineWidth, float rotation, JoinType joinType) {
		super.rectangle(x + 1, y, width - 1, height - 1, lineWidth, rotation, joinType);
	}
	
	public void realRectangle(float x, float y, float width, float height) {
		super.rectangle(x, y, width, height, 1, 0, JoinType.POINTY);
	}
}
