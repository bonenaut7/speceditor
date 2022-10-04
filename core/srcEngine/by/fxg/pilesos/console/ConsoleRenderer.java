package by.fxg.pilesos.console;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.Pilesos;
import by.fxg.pilesos.PilesosInputImpl;
import by.fxg.pilesos.console.Console.Message;
import by.fxg.pilesos.utils.GDXUtil;

public class ConsoleRenderer {
	private SpriteBatch batch;
	private ShapeRenderer shape;
	public boolean isShown;
	private BitmapFont font;
	
	public ConsoleRenderer(BitmapFont font) {
		font.getRegion().getTexture().setAnisotropicFilter(16.0F);
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.batch = new SpriteBatch();
		this.shape = new ShapeRenderer();
		this.isShown = false;
		this.font = font;
	}
	
	//position
	private int pX = 50, pY = 200, scroll = 0;
	//input field
	private Array<String> prevLines = new Array<>();
	private int prevCounter = 0;
	private boolean isWriting = false;
	private int offset = 0; //TODO: ADD TEXT OFFSET, CTRL+C, CTRL+V, ALLSELECTION
	private String inputText = "";
	
	private boolean handleIdentities = false;
	private int identIndex = -1;
		
	public void render(Camera cam, PilesosInputImpl dip, int screenWidth, int screenHeight) {
		if (dip.isKeyboardDown(Keys.GRAVE, false)) {
			this.isShown = !this.isShown;
			if (this.isShown) this.isWriting = true;
		}
	
		if (this.isShown) {
			int sX = 400, sY = 500;
			Array<Message> msg = Console.get().msgs;
			Array<String> identities = new Array<>();
			Array<String> identitiesReal = new Array<>();
			int height = this.pY + sY + 2;
			
			if (this.inputText.length() > 0) {
				for (String str : Console.get().fields.keySet()) {
					if (str.startsWith(this.inputText)) {
						identities.add(str + " - " + Console.get().getFrom(Console.get().fields.get(str)).desc());
						identitiesReal.add(str);
					}
				}
				for (String str : Console.get().methods.keySet()) {
					if (str.startsWith(this.inputText)) {
						identities.add(str + " - " + Console.get().getFrom(Console.get().methods.get(str)).desc());
						identitiesReal.add(str);
					}
				}
			}

			if (GDXUtil.isMouseInArea(this.pX, this.pY, sX, sY)) {
				if (dip.isMouseScrolled(false) && this.scroll > 0) this.scroll--;
				else if (dip.isMouseScrolled(true) && this.scroll < msg.size - 52) this.scroll++;
			}
			if (dip.isMouseDown(0, false)) {
				if (GDXUtil.isMouseInArea(this.pX + 5, this.pY + 5, sX - 10, 25)) this.isWriting = true;
				else this.isWriting = false;
			}
			if (this.isWriting) {
				if (dip.getCharTypedLast() != null && !dip.getCharTypedLast().equals("`") && this.inputText.length() < 45) {
					if (this.handleIdentities) {
						if (identities.size > 0 && this.identIndex >= 0) this.inputText = identitiesReal.get(this.identIndex);
						this.identIndex = -1;
						this.handleIdentities = false;
					}
					this.inputText += dip.getCharTypedLast();
				}
				if (dip.isKeyboardDown(Keys.BACKSPACE, false) && this.inputText.length() > 0) {
					this.inputText = this.inputText.substring(0, this.inputText.length() - 1);
					this.handleIdentities = false;
					this.identIndex = -1;
				}
				if (dip.isKeyboardDown(Keys.UP, false)) {
					if (this.handleIdentities) {
						if (this.identIndex <= -1) this.handleIdentities = false;
						else this.identIndex--;
					} else {
						if (this.prevCounter > 0) {
							this.prevCounter--; 
							this.inputText = this.prevLines.get(this.prevCounter);
						}
					}
				}
				if (dip.isKeyboardDown(Keys.DOWN, false)) {
					if (this.handleIdentities) {
						if (identities.size > 0 && this.identIndex < identities.size - 1) this.identIndex++;
					} else {
						if (this.prevCounter < this.prevLines.size) {
							this.prevCounter++; 
							this.inputText = this.prevCounter >= this.prevLines.size ? "" : this.prevLines.get(this.prevCounter);
						}
						if (this.prevCounter == this.prevLines.size && identities.size > 0) {
							this.handleIdentities = true;
							this.identIndex++;
						}
					}
				}
				if (dip.isKeyboardDown(Keys.ENTER, false)) {
					if (this.handleIdentities) {
						if (identities.size > 0 && this.identIndex >= 0) this.inputText = identitiesReal.get(this.identIndex);
						this.identIndex = -1;
						this.handleIdentities = false;
					}
					Console.input(this.inputText);
					this.prevLines.add(this.inputText);
					this.prevCounter = this.prevLines.size;
					this.inputText = "";
					this.offset = 0;
					if (msg.size >= 52) this.scroll++;
				}
				if (dip.isKeyboardDown(Keys.TAB, false)) {
					if (this.handleIdentities && identities.size > 0) {
						this.identIndex++;
						if (this.identIndex >= identities.size) this.identIndex = 0;
					} else if (identities.size > 0) {
						this.handleIdentities = true; 
						this.identIndex = 0;
					}
				}
			}
			
			this.shape.begin(ShapeType.Filled);
			this.shape.setColor(0.1F, 0.1F, 0.1F, 1);
			this.shape.rect(this.pX, this.pY, sX, sY);
			this.shape.setColor(0.13F, 0.13F, 0.13F, 1.0F);
			this.shape.rect(this.pX + 5, this.pY + 25, sX - 25, sY - 30);
			this.shape.rect(this.pX + 5, this.pY + 5, sX - 25, 15);
			if (identities.size > 0) {
				this.shape.setColor(0.1F, 0.1F, 0.1F, 0.8F);
				this.shape.rect(this.pX, this.pY - 10 * identities.size - 14, sX, 10 * identities.size + 10);
			}
			this.shape.end();
			this.batch.begin();
			for (int i = this.scroll; i != 52 + this.scroll; i++) {
				if (msg.size > i) {
					this.font.setColor(msg.get(i).r, msg.get(i).g, msg.get(i).b, 1);
					this.font.draw(this.batch, msg.get(i).message, this.pX + 8, height -= 9);
				}
			}
			this.font.setColor(1, 1, 1, 1);
			if (this.handleIdentities && this.identIndex >= 0 && identities.size > 0) this.font.draw(this.batch, identities.get(this.identIndex), this.pX + 10, this.pY + 15);
			else this.font.draw(this.batch, this.inputText, this.pX + 10, this.pY + 15);
			if (this.isWriting && Pilesos.getApp().getTick() % 80 > 40) {
				if (this.handleIdentities && this.identIndex >= 0 && identities.size > 0) this.font.draw(this.batch, "|", this.pX + 10 + identitiesReal.get(this.identIndex).length() * 6, this.pY + 15);
				else this.font.draw(this.batch, "|", this.pX + 10 + this.inputText.length() * 6, this.pY + 15);
			}
			for (int i = 0; i != identities.size; i++) {
				this.font.draw(this.batch, identities.get(i), this.pX + 10, this.pY - 10 - 10 * i);
			}
			this.batch.end();
		}
	}
}