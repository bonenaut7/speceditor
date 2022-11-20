package by.fxg.speceditor.std.objectTree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
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
import by.fxg.speceditor.std.ui.UIElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SpecObjectTree extends UIElement implements ISTDDropdownAreaListener, IFocusable {
	public STDDropdownArea dropdownArea;
	public ITreeElementHandler elementHandler;
	public ITreeElementSelector<? extends TreeElement> elementSelector = new DefaultTreeElementSelector();
	
	//parameters
	private final int _sizeExpandIcon = 14, _sizeElementHeight = 20;
	
	//ElementStack of TreeElements inside of UI element
	private ElementStack elementStack = new ElementStack();
	
	//Local clipboard for elements
	private boolean clipboardCut = false;
	private Array<TreeElement> clipboard = new Array<>();
	
	/** [scroll]Scrolls, [canvas]Size of rendered elements in list for scrolls, [renderPosition]Position buffer for elements to render **/
	private Vector2 scroll = new Vector2(), elementsCanvasSize = new Vector2(), renderPosition = new Vector2(); 
	
	//Drag'N'Drop
	private boolean isDragNDropping = false, isDragNDropPossible = false;
	private TreeElement _dragNDropElement;
	private int _dragNDropMove = -1;

	//LMB click control
	private boolean _isClicked, _isClickElementSelectorEmpty, _clickSkip;
	private int _clickX = -1, _clickY = -1;
	private long _expandClickTime, _elementClickTime;
	private TreeElement _clickElement = null;
	
	public SpecObjectTree(int x, int y, int width, int height) { this(); this.setTransforms(x, y, width, height); }
	public SpecObjectTree() {
		this.dropdownArea = new STDDropdownArea(15).setListener(this);
	}
	
	public void update() {
		float boundX = Math.max(0, this.elementsCanvasSize.x - this.width);
		float boundY = Math.max(0, this.y - this.renderPosition.y + this.scroll.y - 23 + 5);
		if (this.isMouseOver()) {
			if (this.getInput().isMouseScrolled(true)) {
				if (this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
					this.scroll.x = MathUtils.clamp(this.scroll.x + (this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) ? boundX / 8 : 25), 0, boundX);
				} else {
					this.scroll.y = MathUtils.clamp(this.scroll.y + (this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) ? boundY / 8 : 23), 0, boundY);
				}
			} else if (SpecEditor.get.getInput().isMouseScrolled(false)) {
				if (this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
					this.scroll.x = MathUtils.clamp(this.scroll.x - (this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) ? boundX / 8 : 25), 0, boundX);
				} else {
					this.scroll.y = MathUtils.clamp(this.scroll.y - (this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) ? boundY / 8 : 23), 0, boundY);
				}
			}
			
			//Element Copy/Cut/Paste
			if (this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) {
				if (this.getInput().isKeyboardDown(Keys.C, false) || this.getInput().isKeyboardDown(Keys.X, false)) { //[Element Copy/Cut]
					if (this.elementSelector.size() > 0) {
						this.clipboard.size = 0;
						for (TreeElement element : this.elementSelector.getIterable()) {
							this.clipboard.add(element);
						}
						this.clipboardCut = this.getInput().isKeyboardDown(Keys.X, false);
					}
				} else if (this.getInput().isKeyboardDown(Keys.V, false)) { //[Element paste]
					if (this.elementSelector.size() == 1 && this.clipboard.size > 0) {
						TreeElement selectedElement = this.elementSelector.get(0);
						ElementStack stackForPaste = selectedElement instanceof ITreeElementFolder ? ((ITreeElementFolder)selectedElement).getFolderStack() : 
							(selectedElement.getParent() instanceof ITreeElementFolder ? ((ITreeElementFolder)selectedElement.getParent()).getFolderStack() : this.elementStack);
						for (int i = 0; i != this.clipboard.size; i++) {
							stackForPaste.set(this.clipboardCut ? this.clipboard.get(i) : this.clipboard.get(i).cloneElement());
						}
						if (this.clipboardCut) this.clipboard.size = 0;
					}
				}
			}
			
			//Element delete
			if (this.getInput().isKeyboardDown(Keys.FORWARD_DEL, false) && this.elementSelector.size() > 0) {
				Array<TreeElement> toDelete = new Array<>();
				for (int i = 0; i != this.elementSelector.size(); i++) toDelete.add(this.elementSelector.get(i));
				SpecEditor.get.renderer.currentGui = new GuiObjectTreeDelete(this, toDelete);
			}
		}
	}
	
	public void render(Batch batch, ShapeDrawer shape, Foster foster) {
		//Tree
		this.elementsCanvasSize.setZero();
		this.renderPosition.set(this.x + 17 - this.scroll.x, this.y + this.height - this._sizeElementHeight - 1 + this.scroll.y); //1+2offset+14triangle, 1 
		batch.flush();
		Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
		Gdx.gl.glScissor(this.x, this.y, this.width, this.height);
		this.drawTree(batch, shape, foster, this.renderPosition, this.elementStack.getElements(), true);
		batch.flush();
		Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
		this.elementsCanvasSize.x = Math.max(0, this.elementsCanvasSize.x - (this.x - this.scroll.x));
		this.elementsCanvasSize.y = this.y - this.renderPosition.y + this.height + this.scroll.y - 21;
			
		//Scroll
		shape.setColor(UColor.elementBoundsClicked);
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
	
	private void drawTree(Batch batch, ShapeDrawer shape, Foster foster, Vector2 position, Array<TreeElement> iterable, boolean isParentVisible) {
		boolean isFolder = false, isDragNDrop = this.isDragNDropping && this.isDragNDropPossible && this.isFocused(), isDragNDropValid = false;

		for (int i = 0; i != iterable.size; i++) {
			TreeElement element = iterable.get(i);
			isFolder = element instanceof ITreeElementFolder;
			foster.setString(element.getName()); //preparing foster to text work
			//[Max width calculation] width + elementHeight(referenced as sprite width, but sprite is equal in dimensions and rely on element height) + (2 border offset + 8 visual offset)
			if (this.elementsCanvasSize.x < position.x + foster.getWidth() + this._sizeElementHeight + 10) this.elementsCanvasSize.x = position.x + foster.getWidth() + this._sizeElementHeight + 10; 
			
			//processing
			if (isMouseInArea(this.x, this.y, this.width, this.height) && SpecInterface.isFocused(null)) {
				if (isMouseInArea(this.x + 1, position.y - 1, this.width - 2, this._sizeElementHeight)) {
					//[LMB Click expand] Small arrow click, Visibility of folders
					if (isFolder && this.getInput().isMouseDown(0, false) && isMouseInArea(position.x - 14, position.y + this._sizeElementHeight / 2 - this._sizeExpandIcon / 2, this._sizeExpandIcon, this._sizeExpandIcon)) {
						if (SpecEditor.get.getTick() - this._expandClickTime > 6L) {
							this.setClick(null, true);
							((ITreeElementFolder)element).setFolderOpened(!((ITreeElementFolder)element).isFolderOpened());
							this._expandClickTime = SpecEditor.get.getTick();
							this._elementClickTime = -1L;
						} else {
							this.setClick(null, true);
						}
					} else if (this.getInput().isMouseDown(0, false)) { //[LMB Click]
						if (this._clickElement == element && isFolder && SpecEditor.get.getTick() - this._elementClickTime < 15L) { //[LMB Click expand]
							this.setClick(element, true);
							((ITreeElementFolder)element).setFolderOpened(!((ITreeElementFolder)element).isFolderOpened());
							this._elementClickTime = -1L;
						} else {
							this.setClick(element);
						}
					} else if (this.getInput().isMouseDown(1, false)) { //[RMB Click]
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
					shape.setColor(this.elementSelector.isElementSelected(element) ? UColor.elementHover : UColor.elementBoundsClicked);
					shape.filledRectangle(this.x + 1, position.y, this.width - 2, this._sizeElementHeight);
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
				if (isDragNDropValid) { //1px offset to down in the first case because of visual bug
					shape.setColor(UColor.white);
					int half = this._sizeElementHeight / 2, quarter = half / 2;
					if (element instanceof ITreeElementFolder) {
						if (isMouseInArea(this.x + 1, position.y + quarter + half, this.width - 2, quarter)) shape.line(this.x + 1, position.y + this._sizeElementHeight - 1, this.x + this.width - 1, position.y + this._sizeElementHeight - 1);
						else if (isMouseInArea(this.x + 1, position.y + quarter, this.width - 2, half)) shape.rectangle(this.x + 1, position.y, this.width - 2, this._sizeElementHeight);
						else if (isMouseInArea(this.x + 1, position.y, this.width - 2, quarter)) shape.line(this.x + 1, position.y, this.x + this.width - 1, position.y);
					} else {
						if (isMouseInArea(this.x + 1, position.y + half, this.width - 2, half)) shape.line(this.x + 1, position.y + this._sizeElementHeight - 1, this.x + this.width - 1, position.y + this._sizeElementHeight - 1); 
						else if (isMouseInArea(this.x + 1, position.y, this.width - 2, half)) shape.line(this.x + 1, position.y, this.x + this.width - 1, position.y);
					}
					
					if (!this.getInput().isMouseDown(0, true)) {
						int step = -1;
						if (element instanceof ITreeElementFolder) {
							step = isMouseInArea(this.x + 1, position.y + quarter + half, this.width - 2, quarter) ? 2 :
								   isMouseInArea(this.x + 1, position.y + quarter, this.width - 2, half) ? 1 :
								   isMouseInArea(this.x + 1, position.y, this.width - 2, quarter) ? 0 : -1;
						} else {
							step = isMouseInArea(this.x + 1, position.y + half, this.width - 2, half) ? 2 :
								   isMouseInArea(this.x + 1, position.y, this.width - 2, half) ? 0 : -1;
						}
						if (step > -1) {
							this._dragNDropMove = step;
							this._dragNDropElement = element;
						}
					}
				}
			}
			
			//element render
			if (isFolder) { //triangle of insides
				 if (isMouseInArea(position.x - 14, position.y + this._sizeElementHeight / 2 - this._sizeExpandIcon / 2, this._sizeExpandIcon, this._sizeExpandIcon) && SpecInterface.isFocused(null)) {
					 shape.setColor(UColor.elementHover);
					 shape.filledRectangle(position.x - 14, position.y + this._sizeElementHeight / 2 - this._sizeExpandIcon / 2, this._sizeExpandIcon, this._sizeExpandIcon);
				 }
				 //TODO make rendering from center
				 shape.setColor(UColor.white);
				 if (((ITreeElementFolder)element).isFolderOpened()) shape.filledTriangle(position.x - 11, position.y + 14, position.x - 3, position.y + 14, position.x - 7, position.y + 5);
				 else shape.filledTriangle(position.x - 9, position.y + 15, position.x - 3, position.y + 10, position.x - 9, position.y + 5);
			}
			
			//visibility icon
			//TODO add visibility icon
			
			if (this.elementSelector.isElementSelected(element)) {
				shape.setColor(UColor.elementHover);
				shape.filledRectangle(this.x + 1, position.y, this.width - 2, this._sizeElementHeight);
			}
			
			int maxSpriteSize = Math.min(this._sizeElementHeight, 16);
			int spriteMargin = (this._sizeElementHeight - maxSpriteSize) / 2;
			Sprite sprite = element.getObjectTreeSprite(); //16x16 sprite with 2px margin
			sprite.setPosition(position.x + spriteMargin, position.y + spriteMargin); //2px margin
			sprite.setScale(1.0F / (sprite.getWidth() / maxSpriteSize)); //force scaling to 16px icon
			
			sprite.draw(batch);
			foster.setString(element.getName()).draw(position.x + maxSpriteSize + 4, position.y + this._sizeElementHeight / 2 - foster.getHalfHeight(), Align.left);
			position.add(0, -this._sizeElementHeight); 
			
			//sub-elements render
			if (isFolder && ((ITreeElementFolder)element).isFolderOpened()) {
				ITreeElementFolder folder = (ITreeElementFolder)element;
				position.add(16, 0);
				this.drawTree(batch, shape, foster, position, folder.getFolderStack().getElements(), isParentVisible ? element.isVisible() : false);
				position.add(-16, 0);
			}
		}
	}
	
	private void setClick(TreeElement element) { this.setClick(element, false); }
	private void setClick(TreeElement element, boolean skipClick) {
		if (!this._isClicked) {
			this._isClicked = true;
			this._clickX = GDXUtil.getMouseX();
			this._clickY = GDXUtil.getMouseY();
			this._elementClickTime = SpecEditor.get.getTick();
			if (!(this._clickSkip = skipClick)) {
				this._clickElement = element;
				this._isClickElementSelectorEmpty = this.elementSelector.size() < 1;
				if (!this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) && !this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true) && this.elementSelector.size() < 2) {
					this.elementSelector.clearSelection();
					this.elementSelector.selectElement(element);
				}
			}
		}
	}
	
	private void updateClick() {
		if (this._isClicked) {
			if (this.getInput().isMouseDown(0, true)) {
				int area0 = 6, area1=area0/2;
				if (!this.isDragNDropping && !isMouseInArea(this._clickX - area1, this._clickY - area1, area0, area0) && this.elementSelector.size() > 0) {
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
			if (!this.getInput().isMouseDown(0, true)) {
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
				SpecInterface.setCursor(this.isDragNDropPossible && isMouseInArea(this.x, this.y, this.width, this.height) ? AppCursor.GRABBING : AppCursor.UNAVAILABLE);
			}
		}
	}
	
	private void onClick() {
		if (!this._clickSkip) {
			if (this._clickElement != null) {
				if (this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) && this.elementSelector.size() > 0) {
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
				} else if (this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) {
					if (!this._isClickElementSelectorEmpty && this.elementSelector.isElementSelected(this._clickElement)) this.elementSelector.deselectElement(this._clickElement);
					else this.elementSelector.selectElement(this._clickElement);
				} else {
					this.elementSelector.clearSelection();
					this.elementSelector.selectElement(this._clickElement);
				}
			} else if (!this.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true) && !this.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) this.elementSelector.clearSelection();
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
				this.dropdownArea.close();
				SpecEditor.get.renderer.currentGui = new GuiObjectTreeDelete(this, toDelete);
			} break;
			default: {
				for (int elementIndex = 0; elementIndex != this.elementSelector.size(); elementIndex++) {
					if (!this.elementSelector.get(elementIndex).processDropdownAction(this, element, id)) {
						return;
					}
				}
				this.dropdownArea.close();
			} break;
		}
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
	
	public SpecObjectTree setTransforms(float x, float y, float width, float height) {
		this.x = (int)x;
		this.y = (int)y + 4;
		this.width = width > 0 ? (int)width - 4 : 0;
		this.height = height > 0 ? (int)height - 4 : 0;
		return this;
	}
	
	private boolean isClicked(float x, float y, float width, float height) {
		return this.isMouseOver(x, y, width, height) && this.getInput().isMouseDown(0, false);
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
