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
	}
	
	public PilesosScissorStack setBounds(int idx, int x, int y, int sx, int sy) {
		if (idx >= this.scissors.size) return null;
		this.scissors.get(idx).set(x, y, sx, sy);
		return this;
	}
	
	public int pushScissors() {
		if (this.nextIndex >= this.scissors.size) return -1;
		ScissorStack.pushScissors(this.scissors.get(this.nextIndex));
		this.nextIndex++;
		return this.nextIndex - 1;
	}
	
	public boolean pushScissors(int idx) {
		if (idx >= this.scissors.size) return false;
		return ScissorStack.pushScissors(this.scissors.get(idx));
	}

	public void popScissors() {
		ScissorStack.popScissors();
	}
}
