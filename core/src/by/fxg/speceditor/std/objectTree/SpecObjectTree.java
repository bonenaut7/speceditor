package by.fxg.speceditor.std.objectTree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.GInputProcessor;
import by.fxg.speceditor.SpecEditor;
import by.fxg.speceditor.screen.gui.GuiObjectTreeDelete;
import by.fxg.speceditor.std.objectTree.impl.DefaultTreeElementSelector;
import by.fxg.speceditor.std.ui.ISTDDropdownAreaListener;
import by.fxg.speceditor.std.ui.STDDropdownArea;
import by.fxg.speceditor.std.ui.STDDropdownAreaElement;
import by.fxg.speceditor.std.ui.SpecInterface;
import by.fxg.speceditor.std.ui.SpecInterface.AppCursor;
import by.fxg.speceditor.std.ui.SpecInterface.IFocusable;
import by.fxg.speceditor.std.ui.SpecInterface.UColor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SpecObjectTree implements ISTDDropdownAreaListener, IFocusable {
	private int x, y, width, height;
	private GInputProcessor input;
	public STDDropdownArea dropdownArea;
	public ITreeElementHandler elementHandler;
	public ITreeElementSelector<? extends TreeElement> elementSelector = new DefaultTreeElementSelector();
	
	private ElementStack elementStack = new ElementStack();
	/** [scroll]Scrolls, [canvas]Size of rendered elements in list for scrolls, [renderPosition]Position buffer for elements to render **/
	private Vector2 scroll = new Vector2(), elementsCanvasSize = new Vector2(), renderPosition = new Vector2(); 
	
	//Drag'N'Drop
	private boolean isDragNDropping = false, isDragNDropPossible = false;
	private TreeElement _dragNDropElement;
	private int _dragNDropMove = -1;

	//LMB click control
	private boolean _isClicked = false, _isClickElementSelectorEmpty, _clickSkip = false;
	private int _clickX = -1, _clickY = -1;
	private long _folderInteractTime = 0L;
	private TreeElement _clickElement = null;
	
	public SpecObjectTree(int x, int y, int width, int height) { this(); this.setTransforms(x, y, width, height); }
	public SpecObjectTree() {
		this.input = SpecEditor.get.getInput();
		this.dropdownArea = new STDDropdownArea(15).setListener(this);
	}
	
	public void update() {
		float outboundX = Math.max(0, this.elementsCanvasSize.x - this.width);
		float outboundY = Math.max(0, this.y - this.renderPosition.y + this.scroll.y - 23 + 5);
		if (GDXUtil.isMouseInArea(this.x, this.y, this.width, this.height) && SpecInterface.isFocused(this)) {
			if (this.input.isMouseScrolled(true)) {
				if (SpecEditor.get.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
					if (this.scroll.x < outboundX) {
						this.scroll.x = Math.min(this.scroll.x + 25, outboundX);
					}
				} else {
					if (this.scroll.y < outboundY) {
						this.scroll.y = Math.min(this.scroll.y + 23, outboundY);
					}
				}
			} else if (SpecEditor.get.getInput().isMouseScrolled(false)) {
				if (SpecEditor.get.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
					if (this.scroll.x > 0) {
						this.scroll.x = Math.max(0, this.scroll.x - 25);
					}
				} else {
					if (this.scroll.y > 0) {
						this.scroll.y = Math.max(0, this.scroll.y - 23);
					}
				}
			}
		}
		if (!this.elementsCanvasSize.isZero()) {
			if (this.scroll.x > outboundX) this.scroll.x = outboundX;
			if (this.scroll.y > outboundY) this.scroll.y = outboundY;
		}
	}
	
	public void render(Batch batch, ShapeDrawer shape, Foster foster) {
		//Tree
		this.elementsCanvasSize.setZero();
		this.renderPosition.set(this.x + 1 - this.scroll.x, this.y + this.height - 21 + this.scroll.y);
		shape.setColor(1, 1, 1, 0.4f);
		batch.flush();
		Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
		Gdx.gl.glScissor(this.x, this.y, this.width, this.height);
		this.drawTree(batch, shape, foster, this.renderPosition, this.elementStack.getElements(), false);
		batch.flush();
		Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
		this.elementsCanvasSize.x = Math.max(0, this.elementsCanvasSize.x - (this.x - this.scroll.x));
		this.elementsCanvasSize.y = this.y - this.renderPosition.y + this.height + this.scroll.y - 21;
			
		//Scroll
		int realHeight = this.height + 3;
		float xScrollWidth = Interpolation.linear.apply(0, this.width, Math.min(this.width / this.elementsCanvasSize.x, 1));
		float xScrollPosition = Interpolation.linear.apply(0, this.width - xScrollWidth, Math.min(-(this.scroll.x / (this.width - this.elementsCanvasSize.x)), 1));
		shape.filledRectangle(this.x + xScrollPosition, this.y - 3, xScrollWidth, 3);
		float yScrollHeight = Interpolation.linear.apply(3, realHeight, Math.min(realHeight / this.elementsCanvasSize.y, 1));
		float yScrollPosition = Interpolation.linear.apply(realHeight - yScrollHeight, 0, Math.min(this.scroll.y / (this.elementsCanvasSize.y - this.height), 1));
		shape.filledRectangle(this.x + this.width, this.y - 3 + yScrollPosition, 3, yScrollHeight);

		//Bounds and dropdown
		shape.setColor(UColor.gray);
		shape.rectangle(this.x, this.y, this.width, this.height);
		shape.rectangle(this.x, this.y - 4, this.width + 4, this.height + 4); //scroll bounds
		this.dropdownArea.render(shape, foster);

		//Passing click if it's hasn't been handled before
		if (this.isClicked(this.x, this.y, this.width, this.height)) this.setClick(null);
		this.updateClick();
	}
	
	private void drawTree(Batch batch, ShapeDrawer shape, Foster foster, Vector2 vector, Array<TreeElement> iterable, boolean isChild) {
		boolean isFolder = false, isDragNDrop = this.isDragNDropping && this.isDragNDropPossible && this.isFocused(), isDragNDropValid = false;
		
		for (int i = 0; i != iterable.size; i++) {
			TreeElement element = iterable.get(i);
			if ((isFolder = element instanceof ITreeElementFolder) && !isChild) vector.add(16, 0); //Adding 16px if folder created with ObjectTree as parent
			foster.setString(element.getName()); //preparing foster to text work
			if (this.elementsCanvasSize.x < vector.x + foster.getWidth() + 26) this.elementsCanvasSize.x = vector.x + foster.getWidth() + 26; //25 - size of box + textWidth
			
			//processing
			if (GDXUtil.isMouseInArea(this.x, this.y, this.width, this.height) && SpecInterface.isFocused(null)) {
				if (this.isClicked(vector.x - 1, vector.y - 1, 25 + foster.getWidth(), 20)) this.setClick(element);
				if (GDXUtil.isMouseInArea(vector.x - 1, vector.y - 1, 25 + foster.getWidth(), 20)) { // isMouseOver
					if (SpecEditor.get.getInput().isMouseDown(1, false)) {
						if (!this.elementSelector.isElementSelected(element)) {
							this.elementSelector.clearSelection();
							this.elementSelector.selectElement(element);
						}
						if (this.elementSelector.size() > 0) {
							Class<? extends TreeElement> typeClass = this.elementSelector.get(0).getClass();
							for (int k = 0; k != this.elementSelector.size(); k++) {
								if (!this.elementSelector.get(k).getClass().isAssignableFrom(typeClass)) {
									typeClass = null;
									break;
								}
							}
							
							Array<STDDropdownAreaElement> elements = this.dropdownArea.getElementsArrayAsEmpty();
							this.elementSelector.get(0).addDropdownItems(this, elements, typeClass != null);
							this.dropdownArea.setElements(elements, foster).open();
						}
					}
					shape.filledRectangle(vector.x - 1, vector.y - 1, 25 + foster.getWidth(), 20);
				} else if (isFolder && this.input.isMouseDown(0, false) && GDXUtil.isMouseInArea(vector.x - 15, vector.y + 2, 14, 14) && SpecInterface.isFocused(null)) {
					//Small arrow click, Visibility of folders
					if (SpecEditor.get.getTick() - this._folderInteractTime > 20L) {
						((ITreeElementFolder)element).setFolderOpened(!((ITreeElementFolder)element).isFolderOpened());
						this._folderInteractTime = SpecEditor.get.getTick();
					}
					this.setClick(null, true);
				}
			}
			
			//Drag'n'Drop render
			if (isDragNDrop) {
				isDragNDropValid = true;
				for (TreeElement element$ : this.elementSelector.getIterable()) {
					if (element == element$ || this.isTreeElementAnyParentsSelected(element)) {
						isDragNDropValid = false;
						break;
					}
				}
				if (isDragNDropValid) {
					float color = shape.getPackedColor();
					shape.setColor(1.0F, 1.0F, 1.0F, 1.0F);
					if (element instanceof ITreeElementFolder) {
						if (GDXUtil.isMouseInArea(vector.x - 1, vector.y + 13, 25 + foster.getWidth(), 7)) {
							shape.line(vector.x + 4, vector.y + 20, vector.x + 20 + foster.getWidth(), vector.y + 20);
						} else if (GDXUtil.isMouseInArea(vector.x - 1, vector.y + 4, 25 + foster.getWidth(), 9)) {
							shape.rectangle(vector.x - 1, vector.y - 1, 25 + foster.getWidth(), 19);
						} else if (GDXUtil.isMouseInArea(vector.x - 1, vector.y - 1, 25 + foster.getWidth(), 6)) {
							shape.line(vector.x + 4, vector.y - 2, vector.x + 20 + foster.getWidth(), vector.y - 2);
						}
					} else {
						if (GDXUtil.isMouseInArea(vector.x - 1, vector.y + 10, 25 + foster.getWidth(), 10)) {
							shape.line(vector.x + 4, vector.y + 20, vector.x + 20 + foster.getWidth(), vector.y + 20);
						} else if (GDXUtil.isMouseInArea(vector.x - 1, vector.y - 1, 25 + foster.getWidth(), 11)) {
							shape.line(vector.x + 4, vector.y - 2, vector.x + 20 + foster.getWidth(), vector.y - 2);
						}
					}
					
					if (!this.input.isMouseDown(0, true)) {
						int move = -1;
						if (element instanceof ITreeElementFolder) move = GDXUtil.isMouseInArea(vector.x - 1, vector.y + 13, 25 + foster.getWidth(), 7) ? 2 : GDXUtil.isMouseInArea(vector.x - 1, vector.y + 4, 25 + foster.getWidth(), 9) ? 1 : GDXUtil.isMouseInArea(vector.x - 1, vector.y - 1, 25 + foster.getWidth(), 6) ? 0 : -1;
						else move = GDXUtil.isMouseInArea(vector.x - 1, vector.y + 10, 25 + foster.getWidth(), 10) ? 2 : GDXUtil.isMouseInArea(vector.x - 1, vector.y - 1, 25 + foster.getWidth(), 11) ? 0 : -1;
						if (move > -1) {
							this._dragNDropMove = move;
							this._dragNDropElement = element;
						}
					}
					shape.setColor(color);
				}
			}
			
			//element render
			if (isFolder) {
				 if (GDXUtil.isMouseInArea(vector.x - 15, vector.y + 2, 14, 14) && SpecInterface.isFocused(null)) shape.filledRectangle(vector.x - 15, vector.y + 2, 14, 14);
				 shape.setColor(1, 1, 1, 1);
				 if (((ITreeElementFolder)element).isFolderOpened()) shape.filledTriangle(vector.x - 12, vector.y + 13, vector.x - 4, vector.y + 13, vector.x - 8, vector.y + 4);
				 else shape.filledTriangle(vector.x - 10, vector.y + 14, vector.x - 4, vector.y + 9, vector.x - 10, vector.y + 4);
				 shape.setColor(1, 1, 1, 0.4f);
			}
			
			if (this.elementSelector.isElementSelected(element)) {
				shape.setColor(1, 1, 1, 0.2f);
				shape.filledRectangle(vector.x - 1, vector.y - 1, 25 + foster.getWidth(), 20);
				shape.setColor(1, 1, 1, 0.4f);
			}
			Sprite sprite = element.getObjectTreeSprite();
			sprite.setPosition(vector.x - (sprite.getWidth() - 16) / 2 + 1, vector.y - (sprite.getHeight() - 16) / 2 + 1);
			sprite.setScale(1.0F / (sprite.getWidth() / 16.0F));
			sprite.draw(batch);
			foster.setString(element.getName()).draw(vector.x + 20, vector.y + 9 - foster.getHalfHeight(), Align.left);
			vector.add(0, -22); 
			
			//sub-elements render
			if (isFolder && ((ITreeElementFolder)element).isFolderOpened()) {
				ITreeElementFolder folder = (ITreeElementFolder)element;
				vector.add(16, 0);
				this.drawTree(batch, shape, foster, vector, folder.getFolderStack().getElements(), true);
				vector.add(-16, 0);
			}
			if (isFolder && !isChild) vector.add(-16, 0);
		}
	}
	
	private void setClick(TreeElement element) { this.setClick(element, false); }
	private void setClick(TreeElement element, boolean skipClick) {
		if (!this._isClicked) {
			this._isClicked = true;
			this._clickX = GDXUtil.getMouseX();
			this._clickY = GDXUtil.getMouseY();
			if (!(this._clickSkip = skipClick)) {
				this._clickElement = element;
				this._isClickElementSelectorEmpty = this.elementSelector.size() < 1;
				if (!this.input.isKeyboardDown(Keys.SHIFT_LEFT, true) && !this.input.isKeyboardDown(Keys.CONTROL_LEFT, true) && this.elementSelector.size() < 2) {
					this.elementSelector.clearSelection();
					this.elementSelector.selectElement(element);
				}
			}
		}
	}
	
	private void updateClick() {
		if (this._isClicked) {
			if (this.input.isMouseDown(0, true)) {
				int area0 = 6, area1=area0/2;
				if (!this.isDragNDropping && !GDXUtil.isMouseInArea(this._clickX - area1, this._clickY - area1, area0, area0) && this.elementSelector.size() > 0) {
					this.setFocused(this.isDragNDropPossible = this.isDragNDropping = true);
					for (int i = 0; i != this.elementSelector.size(); i++) {
						if (this.isTreeElementAnyParentsSelected(this.elementSelector.get(i))) {
							this.isDragNDropPossible = false;
							break;
						}
					}
					this._isClicked = false;
				}
			} else {
				this.onClick();
				this._isClicked = false;
				this._clickSkip = false;
			}
		}
		
		//Drag'n'Drop
		if (this.isDragNDropping) {
			if (!this.input.isMouseDown(0, true)) {
				this.setFocused(this.isDragNDropping = false);
				if (this.isDragNDropPossible && this._dragNDropMove > -1 && this._dragNDropElement != null) {
					boolean isDragNDropValid = true;
					for (TreeElement element$ : this.elementSelector.getIterable()) {
						if (this._dragNDropElement == element$ || this.isTreeElementAnyParentsSelected(this._dragNDropElement)) {
							isDragNDropValid = false;
							break;
						}
					}
					if (isDragNDropValid) {
						if (this._dragNDropMove == 1 && this._dragNDropElement instanceof ITreeElementFolder) {
							for (int i = 0; i != this.elementSelector.size(); i++) {
								this.elementSelector.get(i).setParent(this.elementStack, this._dragNDropElement, true, true);
							}
						} else {
							ElementStack stack = this._dragNDropElement.getParent() instanceof ITreeElementFolder ? ((ITreeElementFolder)this._dragNDropElement.getParent()).getFolderStack() : this.elementStack;
							int index = stack.findIndexHere(this._dragNDropElement.uuid);
							if (index > -1) stack.insertAt(index + (this._dragNDropMove == 2 ? 0 : 1), this.elementSelector.getIterable(), this.elementStack, true);
						}
						this.refreshTree();
					}
				}
			} else {
				SpecInterface.setCursor(this.isDragNDropPossible && GDXUtil.isMouseInArea(this.x, this.y, this.width, this.height) ? AppCursor.GRABBING : AppCursor.UNAVAILABLE);
			}
		}
	}
	
	private void onClick() {
		if (!this._clickSkip) {
			if (this._clickElement != null) {
				if (this.input.isKeyboardDown(Keys.SHIFT_LEFT, true) && this.elementSelector.size() > 0) {
					Array<TreeElement> elements = new Array<>();
					this.getVisibleElements(this.elementStack, elements);
					if (elements.contains(this.elementSelector.get(0), true) && elements.contains(this._clickElement, true)) {
						int startIndex = elements.indexOf(this.elementSelector.get(0), true);
						int endIndex = elements.indexOf(this._clickElement, true);
						if (startIndex > -1 && endIndex > -1) {
							this.elementSelector.clearSelection();
							for (int i = startIndex; i != endIndex + (startIndex > endIndex ? -1 : 1); i += (startIndex > endIndex ? -1 : 1)) {
								this.elementSelector.selectElement(elements.get(i));
							}
						}
					}
				} else if (this.input.isKeyboardDown(Keys.CONTROL_LEFT, true)) {
					if (!this._isClickElementSelectorEmpty && this.elementSelector.isElementSelected(this._clickElement)) this.elementSelector.deselectElement(this._clickElement);
					else this.elementSelector.selectElement(this._clickElement);
				} else {
					this.elementSelector.clearSelection();
					this.elementSelector.selectElement(this._clickElement);
				}
			} else if (!this.input.isKeyboardDown(Keys.SHIFT_LEFT, true) && !this.input.isKeyboardDown(Keys.CONTROL_LEFT, true)) this.elementSelector.clearSelection();
		}
		this.refreshTree();
	}

	@Override
	public void onDropdownAreaClick(STDDropdownAreaElement element, String id) {
		if (this.elementHandler != null && this.elementHandler.onDropdownClick(this, id)) return;
		switch (id) {
			case "default.delete": {
				Array<TreeElement> toDelete = new Array<>();
				for (int i = 0; i != this.elementSelector.size(); i++) toDelete.add(this.elementSelector.get(i));
				SpecEditor.get.renderer.currentGui = new GuiObjectTreeDelete(this, toDelete);
			} break;
			default: {
				boolean closeArea = true;
				for (int elementIndex = 0; elementIndex != this.elementSelector.size(); elementIndex++) {
					if (!this.elementSelector.get(elementIndex).processDropdownAction(this, element, id)) {
						closeArea = false;
					}
				}
				if (!closeArea) return;
			} break;
		}
		this.dropdownArea.close();
	}

	public void refreshTree() {
		if (this.elementHandler != null) {
			this.elementHandler.onRefresh(this);
		}
	}
	
	public ElementStack getStack() { return this.elementStack; }
	public SpecObjectTree setStack(ElementStack elementStack) {
		this.elementStack = elementStack;
		return this;
	}
	
	public ITreeElementHandler getElementHandler() { return this.elementHandler; }
	public SpecObjectTree setHandler(ITreeElementHandler elementHandler) {
		this.elementHandler = elementHandler;
		return this;
	}
	
	public ITreeElementSelector<?> getElementSelector() { return this.elementSelector; }
	public SpecObjectTree setElementSelector(ITreeElementSelector<?> elementSelector) {
		this.elementSelector = elementSelector;
		return this;
	}
	
	public SpecObjectTree setTransforms(int x, int y, int sx, int sy) {
		this.x = x;
		this.y = y + 4;
		this.width = sx - 4;
		this.height = sy - 4;
		return this;
	}
	
	private boolean isClicked(float x, float y, float width, float height) {
		return SpecInterface.isFocused(this) && this.input.isMouseDown(0, false) && GDXUtil.isMouseInArea(x, y, width, height);
	}
	
	//Inspects specified TreeElement's parents for selection, returns false if one of its parents is selected
	private boolean isTreeElementAnyParentsSelected(TreeElement element) {
		if (element.getParent() != null) {
			if (this.elementSelector.isElementSelected(element.getParent())) return true;
			return this.isTreeElementAnyParentsSelected(element.getParent()); //inverted, check for value and maybe place '!' before line
		}
		return false;
	}
	
	//Fills array with displayed elements(elements in opened folders)
	private void getVisibleElements(ElementStack stack, Array<TreeElement> elements) {
		for (TreeElement element : stack.getElements()) {
			elements.add(element);
			if (element instanceof ITreeElementFolder && ((ITreeElementFolder)element).isFolderOpened()) this.getVisibleElements(((ITreeElementFolder)element).getFolderStack(), elements);
		}
	}
}
