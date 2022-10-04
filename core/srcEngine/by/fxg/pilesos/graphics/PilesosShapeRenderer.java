package by.fxg.pilesos.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PilesosShapeRenderer extends ShapeRenderer {
	public void roundedRect(float x, float y, float width, float height, float radius){
        super.rect(x + radius, y + radius, width - 2*radius, height - 2*radius);
        super.rect(x + radius, y, width - 2*radius, radius);
        super.rect(x + width - radius, y + radius, radius, height - 2*radius);
        super.rect(x + radius, y + height - radius, width - 2*radius, radius);
        super.rect(x, y + radius, radius, height - 2*radius);
        super.arc(x + radius, y + radius, radius, 180f, 90f);
        super.arc(x + width - radius, y + radius, radius, 270f, 90f);
        super.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
        super.arc(x + radius, y + height - radius, radius, 90f, 90f);
    }
	
	public void roundedRect(float x, float y, float width, float height, float radius, Color first, Color second) {
        super.rect(x + radius, y + radius, width - 2*radius, height - 2*radius, first, second, second, first);
        super.rect(x + radius, y, width - 2*radius, radius, first, second, second, first);
        super.rect(x + width - radius, y + radius, radius, height - 2*radius, second, second, second, second);
        super.rect(x + radius, y + height - radius, width - 2*radius, radius, first, second, second, first);
        super.rect(x, y + radius, radius, height - 2*radius, first, first, first, first);
        super.setColor(first);
        super.arc(x + radius, y + radius, radius, 180f, 90f);
        super.arc(x + radius, y + height - radius, radius, 90f, 90f);
        super.setColor(second);
        super.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
        super.arc(x + width - radius, y + radius, radius, 270f, 90f);
    }
	
	public void roundedRect(float x, float y, float width, float height, float radius, Color one, Color two, Color three, Color four) {
        super.rect(x + radius, y + radius, width - 2*radius, height - 2*radius, two, three, four, one);
        super.rect(x + radius, y, width - 2*radius, radius, two, three, three, two);
        super.rect(x + width - radius, y + radius, radius, height - 2*radius, three, three, four, four);
        super.rect(x + radius, y + height - radius, width - 2*radius, radius, one, four, four, one);
        super.rect(x, y + radius, radius, height - 2*radius, two, two, one, one);
        super.setColor(one);
        super.arc(x + radius, y + height - radius, radius, 90f, 90f);
        super.setColor(two);
        super.arc(x + radius, y + radius, radius, 180f, 90f);
        super.setColor(three);
        super.arc(x + width - radius, y + radius, radius, 270f, 90f);
        super.setColor(four);
        super.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
    }
	
	public void roundedRectBounds(float x, float y, float width, float height, float radius, Color first, Color second) {
        super.rect(x + radius, y, width - 2*radius, radius, first, second, second, first);
        super.rect(x + width - radius, y + radius, radius, height - 2*radius, second, second, second, second);
        super.rect(x + radius, y + height - radius, width - 2*radius, radius, first, second, second, first);
        super.rect(x, y + radius, radius, height - 2*radius, first, first, first, first);
        super.setColor(first);
        super.arc(x + radius, y + radius, radius, 180f, 90f);
        super.arc(x + radius, y + height - radius, radius, 90f, 90f);
        super.setColor(second);
        super.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
        super.arc(x + width - radius, y + radius, radius, 270f, 90f);
    }
	
	public void roundedRectBounds(float x, float y, float width, float height, float radius, Color one, Color two, Color three, Color four) {
        super.rect(x + radius, y, width - 2*radius, radius, two, three, three, two);
        super.rect(x + width - radius, y + radius, radius, height - 2*radius, three, three, four, four);
        super.rect(x + radius, y + height - radius, width - 2*radius, radius, one, four, four, one);
        super.rect(x, y + radius, radius, height - 2*radius, two, two, one, one);
        super.setColor(one);
        super.arc(x + radius, y + height - radius, radius, 90f, 90f);
        super.setColor(two);
        super.arc(x + radius, y + radius, radius, 180f, 90f);
        super.setColor(three);
        super.arc(x + width - radius, y + radius, radius, 270f, 90f);
        super.setColor(four);
        super.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
    }
}
