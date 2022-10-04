package by.fxg.speceditor.ui;

import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.render.RenderManager;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UDropdownArea {
	private IUDropdownAreaListener listener;
	private int openX, openY, openSX, openSY, dropSY;
	private boolean isDropped = false;
	private float animation = 1f;
	
	private int selectedID = -1;
	private Array<UDAElement> elements;
	
	public UDropdownArea(int dsy) { this(null, dsy); }
	public UDropdownArea(IUDropdownAreaListener listener, int dsy) {
		this.listener = listener;
		this.dropSY = dsy;
		this.elements = new Array<>();
	}
	
	public void update() {
		if (this.isDropped) {
			if (GDXUtil.isMouseInArea(this.openX, this.openY, this.openSX, this.openSY)) {
				if (this.animation < 2f) this.animation += 0.5f;
			} else {
				if (this.animation > 0f) this.animation -= 0.5f;
			}
		}
	}
	
	public void render(ShapeDrawer shape, Foster foster) {
		if (this.isDropped) {
			int h = 0, y = this.openY + this.openSY - this.dropSY;
			for (int $, i = 0; i != this.elements.size; i++) {
				if (GDXUtil.isMouseInArea(this.openX, y, this.openSX, this.dropSY)) this.selectedID = i;
				h += ($ = this.elements.get(i).render(this, shape, foster, y, this.openSX, 0, i, this.selectedID));
				y -= $;
			}
			y += this.dropSY;
			
			if (Game.get.getInput().isMouseDown(0, false)) this.isDropped = false;
			
			shape.setColor(UColor.overlay);
			shape.rectangle(this.openX, y, this.openSX, h);
			shape.setColor(UColor.select);
			shape.filledRectangle(this.openX, y + h, this.openSX, this.animation);
			shape.filledRectangle(this.openX - this.animation, y - this.animation, this.animation, h + this.animation * 2);
			shape.filledRectangle(this.openX + this.openSX, y - this.animation, this.animation, h + this.animation * 2);
			shape.filledRectangle(this.openX, y - this.animation, this.openSX, this.animation);
		}
	}
	
	public UDropdownArea setListener(IUDropdownAreaListener listener) {
		this.listener = listener;
		return this;
	}
	
	public UDropdownArea set(Foster foster, Array<UDAElement> elements) {
		this.elements = elements;
		float maxWidth = 10;
		for (UDAElement element : elements) {
			if (foster.setString(element.name).getWidth() + (element.elements.size > 0 ? 25 : 10) > maxWidth) {
				maxWidth = foster.getWidth() + (element.elements.size > 0 ? 25 : 10);
			}
		}
		this.openSX = (int)maxWidth;
		this.openSY = this.dropSY * elements.size;
		return this;
	}
	
	public UDropdownArea close() {
		this.isDropped = false;
		return this;
	}
	
	public UDropdownArea open() { return this.open(GDXUtil.getMouseX(), GDXUtil.getMouseY()); }
	public UDropdownArea open(int mx, int my) {
		this.openX = mx;
		this.openY = my - this.openSY;
		this.isDropped = true;
		return this;
	}
	
	public Array<UDAElement> getElements() { return this.elements; }
	public boolean isOpened() { return this.isDropped; }
	
	public interface IUDropdownAreaListener {
		void onClick(String key);
	}
	
	public static class UDAElement {
		private String key, name;
		
		private int sizeX, selectedID = -1;
		private Array<UDAElement> elements = new Array<>();
		
		/** Creates line element(non-clickable) **/
		public UDAElement() { this.key = this.name = ""; this.selectedID = -2; }
		public UDAElement(String key, String name) {
			this.key = key;
			this.name = name;
		}
		
		public int render(UDropdownArea area, ShapeDrawer shape, Foster foster, int y, int sizeX, int offsetX, int id, int selectedID) {
			shape.setColor(UColor.gray);
			if (this.selectedID == -2) {
				shape.setColor(UColor.gray);
				shape.filledRectangle(area.openX + offsetX, y, sizeX, area.dropSY);
				shape.setColor(UColor.overlay);
				shape.line(area.openX + offsetX + 4, y + area.dropSY - 4, area.openX + offsetX + sizeX - 4, y + area.dropSY - 4, 2);
				return 9;
			} else {
				shape.filledRectangle(area.openX + offsetX, y, sizeX, area.dropSY);
				if (GDXUtil.isMouseInArea(area.openX + offsetX, y, sizeX, area.dropSY)) {
					shape.setColor(UColor.suboverlay);
					shape.filledRectangle(area.openX + offsetX, y, sizeX, area.dropSY);
					if (Game.get.getInput().isMouseDown(0, false)) {
						area.listener.onClick(this.key);
					}
				}
				shape.setColor(UColor.overlay);
				shape.rectangle(area.openX + offsetX, y, sizeX, area.dropSY);
				foster.setString(this.name).draw(area.openX + sizeX / 2 + offsetX, y + area.dropSY / 2 + foster.getHalfHeight());
				
				if (this.elements.size > 0) {
					float xp = area.openX + sizeX - 8 + offsetX;
					shape.setColor(UColor.white);
					shape.filledTriangle(xp, y + (area.dropSY / 2 + 6), xp + 6, y + (area.dropSY / 2 + 1), xp, y + (area.dropSY / 2 - 4));
					if (GDXUtil.isMouseInArea(area.openX + offsetX, y, sizeX, area.dropSY) || id == selectedID) {
						for (int i = 0; i != this.elements.size; i++) {
							if (GDXUtil.isMouseInArea(area.openX + offsetX + sizeX, y - area.dropSY * i, this.sizeX, area.dropSY)) this.selectedID = i;
							this.elements.get(i).render(area, shape, foster, y - area.dropSY * i, this.sizeX, offsetX + sizeX, i, this.selectedID);
						}
					}
				}
				return area.dropSY;
			}
		}
		
		public UDAElement addElement(UDAElement element) {
			if (this.selectedID != -2) {
				this.elements.add(element);
				for (UDAElement element$ : this.elements) {
					if (RenderManager.foster.setString(element$.name).getWidth() + (element$.elements.size > 0 ? 25 : 10) > this.sizeX) {
						this.sizeX = (int)RenderManager.foster.getWidth() + (element$.elements.size > 0 ? 25 : 10);
					}
				}
			}
			return this;
		}
		
		public int size() {
			return this.elements.size;
		}
	}
}
