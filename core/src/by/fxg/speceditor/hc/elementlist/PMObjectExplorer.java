package by.fxg.speceditor.hc.elementlist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.graphics.font.Foster;
import by.fxg.pilesos.utils.GDXUtil;
import by.fxg.speceditor.Game;
import by.fxg.speceditor.hc.elementlist.elements.ElementDecal;
import by.fxg.speceditor.hc.elementlist.elements.ElementLight;
import by.fxg.speceditor.hc.elementlist.elements.ElementMultiHitbox;
import by.fxg.speceditor.project.Project;
import by.fxg.speceditor.render.IRendererType;
import by.fxg.speceditor.tools.debugdraw.IDebugDraw;
import by.fxg.speceditor.tools.g3d.IModelProvider;
import by.fxg.speceditor.ui.SpecInterface.UColor;
import by.fxg.speceditor.ui.UDropdownArea;
import by.fxg.speceditor.ui.UDropdownArea.IUDropdownAreaListener;
import by.fxg.speceditor.ui.UDropdownArea.UDAElement;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class PMObjectExplorer implements IUDropdownAreaListener {
	public int x, y, sx, sy;
	public ElementStack elementStack;
	
	private Vector3 scroll; //Scrolls// xScroll, yScroll, scrollAnimation
	private Vector2 outboundSize, renderPosition; //sizes of the trees, idk
	
	public UDropdownArea dropdownArea;
	
	public PMObjectExplorer() { this(0, 0, 0, 0); }
	public PMObjectExplorer(int x, int y, int sx, int sy) {
		this.x = x;
		this.y = y + 4;
		this.sx = sx - 4;
		this.sy = sy - 4;
		this.elementStack = new ElementStack();
		this.scroll = new Vector3();
		this.outboundSize = new Vector2();
		this.renderPosition = new Vector2();
		
		this.dropdownArea = new UDropdownArea(this, 15);
	}
	
	public void update() {
		/*Scroll*/ {
			float outboundX = Math.max(0, this.outboundSize.x - this.sx);
			float outboundY = Math.max(0, this.y - this.renderPosition.y + this.scroll.y - 23 + 5);
			if (GDXUtil.isMouseInArea(this.x, this.y, this.sx, this.sy)) {
				if (Game.get.getInput().isMouseScrolled(true)) {
					if (Game.get.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
						if (this.scroll.x < outboundX) {
							this.scroll.x = Math.min(this.scroll.x + 25, outboundX);
						}
					} else {
						if (this.scroll.y < outboundY) {
							this.scroll.y = Math.min(this.scroll.y + 23, outboundY);
						}
					}
				} else if (Game.get.getInput().isMouseScrolled(false)) {
					if (Game.get.getInput().isKeyboardDown(Keys.SHIFT_LEFT, true)) {
						if (this.scroll.x > 0) {
							this.scroll.x = Math.max(0, this.scroll.x - 25);
						}
					} else {
						if (this.scroll.y > 0) {
							this.scroll.y = Math.max(0, this.scroll.y - 23);
						}
					}
				}
				if (Game.get.getTick() % 3L == 0 && this.scroll.z < 4) {
					this.scroll.z++;
				}
			} else {
				if (Game.get.getTick() % 3L == 0 && this.scroll.z > 0) {
					this.scroll.z--;
				}
			}
			if (!this.outboundSize.isZero()) {
				if (this.scroll.x > outboundX) this.scroll.x = outboundX;
				if (this.scroll.y > outboundY) this.scroll.y = outboundY;
			}
		}
		
		if (Game.get.getInput().isMouseDown(0, false) && GDXUtil.isMouseInArea(this.x, this.y, this.sx, this.sy) && !this.dropdownArea.isOpened()) this.dropItem = true;
		//System.out.println(String.format("Outbound %d%s%.0f & %d%s%.0f", this.sx, (this.sx >= this.outboundSize.x ? ">" : "<"), this.outboundSize.x, this.sy, (this.sy >= this.outboundSize.y ? ">" : "<"), this.outboundSize.y));
	}
	
	public void render(Batch batch, ShapeDrawer shape, Foster foster, boolean allowMouse) {
		/*Tree*/ {
			this.outboundSize.setZero();
			this.renderPosition.set(this.x + 5 - this.scroll.x, this.y + this.sy - 25 + this.scroll.y);
			shape.setColor(1, 1, 1, 0.4f);
			batch.flush();
			Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
			Gdx.gl.glScissor(this.x, this.y, this.sx, this.sy);
			this.drawTree(batch, shape, foster, this.renderPosition, this.elementStack.getItems(), allowMouse);
			batch.flush();
			Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
			if (GDXUtil.isMouseInArea(this.x, this.y, this.sx, this.sy) && allowMouse) shape.rectangle(this.x - 1, this.y - 1 - this.scroll.z, this.sx + 2 + this.scroll.z, this.sy + 2 + this.scroll.z); //overlay
			this.outboundSize.x -= this.x - this.scroll.x - 25; //25 - какой-то ебаный костыль понятие которого я не понял
			this.outboundSize.y = this.y - this.renderPosition.y + this.sy + this.scroll.y - 25;
		}
		
		/*Scrolls*/ {
			float xScrollWidth = Interpolation.linear.apply(0, this.sx, Math.min(this.sx / this.outboundSize.x, 1));
			float xScrollPosition = Interpolation.linear.apply(0, this.sx - xScrollWidth, Math.min(-(this.scroll.x / (this.sx - this.outboundSize.x)), 1));
			shape.filledRectangle(this.x + xScrollPosition, this.y - this.scroll.z, xScrollWidth, this.scroll.z);
			float yScrollHeight = Interpolation.linear.apply(this.scroll.z, this.sy + this.scroll.z, Math.min(this.sy / this.outboundSize.y, 1));
			float yScrollPosition = Interpolation.linear.apply(this.sy - yScrollHeight, 0, Math.min(this.scroll.y / (this.outboundSize.y - this.sy), 1));
			shape.filledRectangle(this.x + this.sx, this.y + yScrollPosition, this.scroll.z - 1, yScrollHeight);
		}
		
		shape.setColor(UColor.gray);
		shape.rectangle(this.x, this.y, this.sx, this.sy);
		shape.rectangle(this.x, this.y - this.scroll.z, this.sx + this.scroll.z, this.sy + this.scroll.z);
		
		/*Dropdown*/ {
			this.dropdownArea.render(shape, foster);
		}
		
		if (this.dropItem) this.selectedItems.clear();
	}
	
	private Array<TreeElement> __subItems;
	private final int __heightOffset = 23; //лишний раз не загружаю JVM и не ебу GC
	private boolean dropItem = false; //clears selectedItems on empty click
	private void drawTree(Batch batch, ShapeDrawer shape, Foster foster, Vector2 vector, Array<TreeElement> iterable, boolean allowMouse) {
		for (int i = 0; i != iterable.size; i++) {
			TreeElement element = iterable.get(i);
			foster.setString(element.getName());
			//x + width text + 10[boundary of objects] + 20[sprite] + 5[text offset of sprite] + 1[whitesquare]
			if (this.outboundSize.x < vector.x + foster.getWidth() + 1) this.outboundSize.x = vector.x + foster.getWidth() + 10 + 20 + 5 + 1; //maximum length search for the scrolls
			if (this.selectedItems.contains(element, false)) {
				shape.setColor(1, 1, 1, 0.2f);
				shape.filledRectangle(vector.x - 1, vector.y - 1, 27 + foster.getWidth(), 22);
				shape.setColor(1, 1, 1, 0.4f);
			}
			if (GDXUtil.isMouseInArea(this.x, this.y, this.sx, this.sy) && !this.dropdownArea.isOpened() && allowMouse) {
				if (GDXUtil.isMouseInArea(vector.x - 1, vector.y - 1, 27 + foster.getWidth(), 22)) { // isMouseOver
					if (Game.get.getInput().isMouseDown(0, true)) {
						element.onInteract(this, !Game.get.getInput().isMouseDown(0, false), GDXUtil.isMouseInArea(vector.x - 1, vector.y - 1, 25, 22));
						this.refreshData();
						this.dropItem = false;
					}
					if (Game.get.getInput().isMouseDown(1, false)) {
						if (!this.isElementSelected(element)) this.elementSelect(element);
						if (!this.selectedItems.isEmpty()) {
							Class<? extends TreeElement> clazz = this.selectedItems.get(0).getClass();
							for (TreeElement element$ : this.selectedItems) {
								if (!element$.getClass().isAssignableFrom(clazz)) {
									clazz = null;
									break;
								}
							}
							Array<UDAElement> types = this.dropdownArea.getElements();
							types.size = 0;
							if (clazz != null) this.selectedItems.get(0).addDropdownParameters(this, this.selectedItems, types);
							else this.selectedItems.get(0).addDefaultDropdownParameters(this, this.selectedItems, types);
							this.dropdownArea.set(foster, types).open();
						}
					}
					
					if (GDXUtil.isMouseInArea(vector.x - 1, vector.y - 1, 25, 22)) { //rendering overlayer
						shape.filledRectangle(vector.x - 1, vector.y - 1, 22, 22);
					} else shape.filledRectangle(vector.x - 1, vector.y - 1, 27 + foster.getWidth(), 22);
				}
			}
			
			element.getSprite().setPosition(vector.x, vector.y);
			element.getSprite().draw(batch);
			foster.setString(element.getName());
			foster.draw(vector.x + 25, vector.y + 14, Align.left);
			vector.add(0, -this.__heightOffset);
			if (element.hasStack() && element.isStackOpened()) {
				vector.add(10, 0);
				this.__subItems = element.getStack().getItems();
				
				int lineOffsetY = 0;
				for (int j = 0; j < this.__subItems.size; j++) {
					shape.line(vector.x, vector.y + this.__heightOffset/3 - (23 * j) - lineOffsetY, vector.x + 10, vector.y + this.__heightOffset/3 - (this.__heightOffset * j) - lineOffsetY); //horizontal folder content lines
					if (j + 1 < this.__subItems.size) {
						if (this.__subItems.get(j).hasStack() && this.__subItems.get(j).isStackOpened()) {
							lineOffsetY += this.__heightOffset * this.getElementSize(this.__subItems.get(j).getStack().getItems()) - this.__heightOffset; //offset for the vertical folder line
						}
					}
				}
				shape.line(vector.x, vector.y - (this.__heightOffset * (this.__subItems.size - 1) - this.__heightOffset/3) - lineOffsetY, vector.x, vector.y + this.__heightOffset); //vertical folder line
				vector.add(10, 0);
				this.drawTree(batch, shape, foster, vector, this.__subItems, allowMouse);
				vector.add(-20, 0);
			}
		}
	}
	
	private int getElementSize(Array<TreeElement> elements) {
		int num = 1;
		for (TreeElement element : elements) {
			if (element.hasStack() && element.isStackOpened()) {
				num += this.getElementSize(element.getStack().getItems());
			} else num++;
		}
		return num;
	}
	
//====================================================================================================================

	public Array<TreeElement> selectedItems = new Array<>();
	
	public boolean isElementSelected(TreeElement element) {
		return this.selectedItems.contains(element, true);
	}
	
	public void elementSelect(TreeElement element) {
		if (!Game.get.getInput().isKeyboardDown(Keys.CONTROL_LEFT, true)) {
			this.selectedItems.clear();
		}
		if (!this.isElementSelected(element)) {
			this.selectedItems.add(element);
		}
	}
	
	public void elementUnselect(TreeElement element) {
		this.selectedItems.removeValue(element, false);
	}

	public void onClick(String key) {
		if (!this.selectedItems.isEmpty()) {
			this.selectedItems.get(0).processDropdown(this, this.selectedItems, key);
			this.selectedItems.get(0).processDefaultDropdown(this, this.selectedItems, key);
		}
		this.dropdownArea.close();
		Project.renderer.update();
	}

	public void refreshData() {
		Project.renderer.clear(true);
		this.searchRenderables(Project.renderer, this.elementStack.getItems(), true);
	}
	
	private void searchRenderables(IRendererType renderer, Array<TreeElement> elements, boolean parentVisible) { 
		for (TreeElement element : elements) {
			if (element != null) {
				if (element instanceof ElementLight) renderer.addLight((ElementLight)element, parentVisible && element.isVisible, this.selectedItems.contains(element, true));
				
				if ((parentVisible && element.isVisible || this.selectedItems.contains(element, true))) {
					if (element instanceof IModelProvider) renderer.addRenderable((IModelProvider)element);
					if (element instanceof IDebugDraw) renderer.addDebugDrawable((IDebugDraw)element);
					if (element instanceof ElementDecal) renderer.addDecal(((ElementDecal)element).decal);
				}
				if (element.hasStack()) {
					if (element instanceof ElementMultiHitbox && (parentVisible && element.isVisible || this.selectedItems.contains(element, true))) {
						for (TreeElement element$ : element.getStack().getItems()) {
							if (element$.isVisible || this.selectedItems.contains(element$, true)) {
								if (element$ instanceof IDebugDraw) renderer.addDebugDrawable((IDebugDraw)element$);
							}
						}
					} else this.searchRenderables(renderer, element.getStack().getItems(), parentVisible ? element.isVisible : parentVisible);
				}
			}
		}
	}
	
//====================================================================================================================

	public PMObjectExplorer setTransforms(int x, int y, int sx, int sy) {
		this.x = x;
		this.y = y + 4;
		this.sx = sx - 4;
		this.sy = sy - 4;
		return this;
	}
}
