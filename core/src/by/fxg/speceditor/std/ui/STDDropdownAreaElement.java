package by.fxg.speceditor.std.ui;

import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class STDDropdownAreaElement {
	protected STDDropdownArea dropdownArea;
	protected Array<STDDropdownAreaElement> elements;
	protected STDDropdownAreaElement shownChild = null;
	protected String elementID, name;
	protected Type type;
	protected int width;
	
	private STDDropdownAreaElement(Type type, String id, String name) {
		this.type = type;
		this.elementID = id;
		this.name = name;
		if (this.type == Type.SUBWINDOW) {
			this.elements = new Array<>();
		}
	}
	
	protected STDDropdownAreaElement setDropdownArea(STDDropdownArea dropdownArea) {
		this.dropdownArea = dropdownArea;
		return this;
	}
	
	public int render(STDDropdownArea area, STDDropdownAreaElement parent, ShapeDrawer shape, Foster foster, int x, int y, int width) {
		switch (this.type) {
			case LINE: {
				shape.setColor(UColor.elementDefaultColor);
				shape.filledRectangle(x, y - 10, width, 9);
				shape.setColor(UColor.elementBoundsClicked);
				//shape.line(x + 1, y - 1, x + 1, y - 10);
				//shape.line(x + width, y - 1, x + width, y - 10);
				shape.line(x + 4, y - 6, x + width - 4, y - 6, 1);
				return 9;
			}
			case BUTTON: {
				shape.setColor(UColor.elementDefaultColor);
				shape.filledRectangle(x, y - area.dropHeight - 1, width, area.dropHeight);
				shape.setColor(UColor.elementBoundsClicked);
				//shape.line(x + 1, y - 1, x + 1, y - area.dropHeight - 1);
				//shape.line(x + width, y - 1, x + width, y - area.dropHeight - 1);
				foster.setString(this.name).draw(x + width / 2, y - area.dropHeight / 2 - foster.getHalfHeight() - 2);
				if (area.isMouseOver(x, y - area.dropHeight - 1, width, area.dropHeight)) {
					shape.setColor(UColor.elementHover);
					shape.filledRectangle(x, y - area.dropHeight - 1, width, area.dropHeight);
					this.setParentShownChildAs(area, parent, null);
					if (area.getInput().isMouseDown(0, false) && area.actionListener != null) {
						area.actionListener.onDropdownAreaClick(area, area.actionListenerID, this, this.elementID);
					}
				}
			} break;
			case SUBWINDOW: {
				shape.setColor(UColor.elementDefaultColor);
				shape.filledRectangle(x, y - area.dropHeight - 1, width, area.dropHeight);
				shape.setColor(UColor.elementBoundsClicked);
				//shape.line(x + 1, y - 1, x + 1, y - area.dropHeight - 1); 
				//shape.line(x + width, y - 1, x + width, y - area.dropHeight - 1);
				foster.setString(this.name).draw(x + width / 2, y - area.dropHeight / 2 - foster.getHalfHeight() - 2);
				if (area.isMouseOver(x, y - area.dropHeight - 1, width, area.dropHeight)) {
					this.updateChildVisibility(area, parent);
					shape.setColor(UColor.elementHover);
					shape.filledRectangle(x, y - area.dropHeight - 1, width, area.dropHeight);
				} else if (parent != null ? parent.shownChild == this : area.shownChild == this) {
					shape.setColor(UColor.elementHover);
					shape.filledRectangle(x, y - area.dropHeight - 1, width, area.dropHeight);
				}
				
				if (parent != null ? parent.shownChild == this : area.shownChild == this) {
					int yPos = y;
					for (int i = 0; i != this.elements.size; i++) {
						yPos -= this.elements.get(i).render(area, this, shape, foster, x + width, yPos, this.width);
					}
					shape.setColor(UColor.elementBoundsClicked);
					shape.rectangle(x + width, yPos - 1, this.width, y - yPos);
					//shape.line(x + width, y - 1, x + width + this.width, y - 1);
					//shape.line(x + width, yPos, x + width + this.width, yPos);
				}
			} break;
		}
		return area.dropHeight;
	}

	public STDDropdownAreaElement add(STDDropdownAreaElement element) { return this.add(element, true); }
	public STDDropdownAreaElement add(STDDropdownAreaElement element, boolean notify) {
		if (this.type == Type.SUBWINDOW && element != null) {
			if (element != null && (notify && this.dropdownArea.actionListener != null ? this.dropdownArea.actionListener.onDropdownAreaAddElement(this.dropdownArea, this.dropdownArea.actionListenerID, this, element) : true)) {
				this.elements.add(element.setDropdownArea(this.dropdownArea));
				if (element.type != Type.LINE) {
					for (int i = 0; i != this.elements.size; i++) {
						STDDropdownAreaElement element$ = this.elements.get(i);
						if (element$.name != null) {
							int size = (int)SpecEditor.fosterNoDraw.setString(element$.name).getWidth() + (element$.type == Type.SUBWINDOW ? 25 : 10);
							if (this.width < size) {
								this.width = size;
							}
						}
					}
				}
			}
		}
		return this;
	}
	
	public String getID() {
		return this.elementID;
	}
	
	public Type getType() {
		return this.type;
	}
	
	private void setParentShownChildAs(STDDropdownArea area, STDDropdownAreaElement parent, STDDropdownAreaElement child) {
		if (parent == null) area.shownChild = child;
		else parent.shownChild = child;
	}
	
	private void updateChildVisibility(STDDropdownArea area, STDDropdownAreaElement parent) {
		if (parent == null) {
			if (area.shownChild != this) {
				area.shownChild = this;
				this.resetChildVisibility();
			}
		} else {
			if (parent.shownChild != this) {
				parent.shownChild = this;
				this.resetChildVisibility();
			}
		}
	}
	
	private void resetChildVisibility() {
		this.shownChild = null;
		for (int i = 0; i != this.elements.size; i++) {
			if (this.elements.get(i).type == Type.SUBWINDOW) {	
				this.elements.get(i).resetChildVisibility();
			}
		}
	}
	
	public static STDDropdownAreaElement line() {
		return new STDDropdownAreaElement(Type.LINE, null, null);
	}
	
	public static STDDropdownAreaElement button(String id, String name) {
		return new STDDropdownAreaElement(Type.BUTTON, id, name);
	}
	
	public static STDDropdownAreaElement subwindow(String name) { return subwindow(null, null, name); }
	public static STDDropdownAreaElement subwindow(String id, String name) { return subwindow(null, id, name); }
	public static STDDropdownAreaElement subwindow(STDDropdownArea area, String name) { return subwindow(area, null, name); }
	public static STDDropdownAreaElement subwindow(STDDropdownArea area, String id, String name) {
		return new STDDropdownAreaElement(Type.SUBWINDOW, id, name).setDropdownArea(area);
	}

	public static enum Type {
		LINE,
		BUTTON,
		SUBWINDOW, //idk how its called
	}
}
