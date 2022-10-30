package by.fxg.speceditor.ui;

import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.render.RenderManager;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class UDropdownArea implements IFocusable {
	private IUDropdownAreaListener listener;
	private int x, y, width, height, dropHeight;
	
	private long tickOpen = -1L;
	private int selectedID = -1;
	private Array<UDAElement> elements;
	
	public UDropdownArea(int dropHeight) { this(null, dropHeight); }
	public UDropdownArea(IUDropdownAreaListener listener, int dropHeight) {
		this.listener = listener;
		this.dropHeight = dropHeight;
		this.elements = new Array<>();
	}
	
	public void render(ShapeDrawer shape, Foster foster) {
		if (this.isFocused()) {
			int h = 0, y = this.y + this.height - this.dropHeight;
			for (int $, i = 0; i != this.elements.size; i++) {
				if (GDXUtil.isMouseInArea(this.x, y, this.width, this.dropHeight)) this.selectedID = i;
				h += ($ = this.elements.get(i).render(this, shape, foster, y, this.width, 0, i, this.selectedID));
				y -= $;
			}
			y += this.dropHeight;
			
			if (this.tickOpen < SpecEditor.get.getTick() && SpecEditor.get.getInput().isMouseDown(0, false)) this.close();
			
			shape.setColor(UColor.overlay);
			shape.rectangle(this.x, y, this.width, h);
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
		this.width = (int)maxWidth;
		this.height = this.dropHeight * elements.size;
		return this;
	}
	
	public UDropdownArea close() {
		this.setFocused(false);
		return this;
	}
	
	public UDropdownArea open() { return this.open(GDXUtil.getMouseX(), GDXUtil.getMouseY()); }
	public UDropdownArea open(int mx, int my) {
		this.x = mx;
		this.y = my - this.height;
		this.setFocused(true);
		this.tickOpen = SpecEditor.get.getTick();
		return this;
	}
	
	public Array<UDAElement> getElements() { return this.elements; }
	
	public interface IUDropdownAreaListener {
		void onDropdownClick(String id);
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
		
		public int render(UDropdownArea area, ShapeDrawer shape, Foster foster, int y, int targetWidth, int xOffset, int id, int selectedID) {
			shape.setColor(UColor.gray);
			if (this.selectedID == -2) {
				shape.setColor(UColor.gray);
				shape.filledRectangle(area.x + xOffset, y + area.dropHeight - 9, targetWidth, 9);
				shape.setColor(UColor.overlay);
				shape.line(area.x + xOffset + 4, y + area.dropHeight - 4, area.x + xOffset + targetWidth - 4, y + area.dropHeight - 4, 2);
				return 9;
			} else {
				shape.filledRectangle(area.x + xOffset, y, targetWidth, area.dropHeight);
				if (GDXUtil.isMouseInArea(area.x + xOffset + 1, y + 1, targetWidth - 2, area.dropHeight - 2)) {
					shape.setColor(UColor.suboverlay);
					shape.filledRectangle(area.x + xOffset, y, targetWidth, area.dropHeight);
					if (SpecEditor.get.getInput().isMouseDown(0, false)) {
						area.listener.onDropdownClick(this.key);
					}
				}
				shape.setColor(UColor.overlay);
				shape.rectangle(area.x + xOffset, y, targetWidth, area.dropHeight);
				foster.setString(this.name).draw(area.x + targetWidth / 2 + xOffset, y + area.dropHeight / 2 - foster.getHalfHeight());
				
				if (this.elements.size > 0) {
					float xp = area.x + targetWidth - 8 + xOffset;
					shape.setColor(UColor.white);
					shape.filledTriangle(xp, y + (area.dropHeight / 2 + 6), xp + 6, y + (area.dropHeight / 2 + 1), xp, y + (area.dropHeight / 2 - 4));
					//FIXME IDK HOW TF THIS WORKS, IT'S STILL SELECTING 2 ITEMS I'M FUCKING HATE IT
					if (GDXUtil.isMouseInArea(area.x + xOffset + 1, y + 1, targetWidth - 2, area.dropHeight - 2) || id == selectedID) {
						int yPosition = y;
						for (int i = 0; i != this.elements.size; i++) {
							if (GDXUtil.isMouseInArea(area.x + xOffset + targetWidth, y - area.dropHeight * i, this.sizeX, area.dropHeight)) this.selectedID = i;
							yPosition -= this.elements.get(i).render(area, shape, foster, yPosition, this.sizeX, xOffset + targetWidth, i, this.selectedID);
						}
					}
				}
				return area.dropHeight;
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
