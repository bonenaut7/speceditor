package by.fxg.pilesos;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

import by.fxg.pilesos.graphics.SpriteStack;

public abstract class Apparat<T extends InputProcessor> extends ApplicationAdapter {
	protected String[] programArgs;
	protected long tick = 1;
	protected T input;
	public int width, height;
	
	public void onCreate(Apparat<T> app) {
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		Pilesos.setApp(app);
	}
	
	public void render() {
		this.tick++;
		this.update(this.width, this.height);
		this.render(this.width, this.height);
	}
	
	public void dispose() {
		super.dispose();
		SpriteStack.dispose();
	}
	
	public void stop() {
		Gdx.app.exit();
	}
	
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public Apparat<T> setProgramArgs(String... args) {
		this.programArgs = args;
		return this;
	}
	
	public boolean hasProgramArgument(String arg) {
		for (String arg$ : this.programArgs) {
			if (arg.toLowerCase().equals(arg$.toLowerCase())) return true;
		}
		return false;
	}
	
	public T getInput() { return this.input; }
	public long getTick() { return this.tick; }
	abstract public void render(int width, int height);
	abstract public void update(int width, int height);
}
