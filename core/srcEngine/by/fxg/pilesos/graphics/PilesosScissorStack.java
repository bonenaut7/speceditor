package by.fxg.pilesos.graphics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;	

public class PilesosScissorStack {
	public static PilesosScissorStack instance;
	private Array<Rectangle> scissors;
	private int nextIndex = 0;
	
	public PilesosScissorStack(int amount) {
		instance = this;
		this.scissors = new Array<>(amount);
		for (int i = 0; i != amount; i++) {
			this.scissors.add(new Rectangle());
		}
		this.nextIndex = amount - 1;
	}

	public boolean peekScissors(float x, float y, float width, float height) {
		if (this.nextIndex > 0 && ScissorStack.pushScissors(this.scissors.get(this.nextIndex).set(x, y, width, height))) {
			this.nextIndex--;
			return true;
		}
		return false;
	}
	
	public void popScissors() {
		this.nextIndex++;
		ScissorStack.popScissors();
	}
}
