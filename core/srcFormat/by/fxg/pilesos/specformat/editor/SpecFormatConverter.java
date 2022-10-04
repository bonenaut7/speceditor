package by.fxg.pilesos.specformat.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import by.fxg.pilesos.specformat.graph.SpecGraph;
import by.fxg.speceditor.hc.elementlist.ElementStack;
import by.fxg.speceditor.hc.elementlist.PMObjectExplorer;
import by.fxg.speceditor.hc.elementlist.TreeElement;
import by.fxg.speceditor.hc.elementlist.elements.ElementDecal;
import by.fxg.speceditor.hc.elementlist.elements.ElementFolder;
import by.fxg.speceditor.hc.elementlist.elements.ElementHitbox;
import by.fxg.speceditor.hc.elementlist.elements.ElementLight;
import by.fxg.speceditor.hc.elementlist.elements.ElementMeshHitbox;
import by.fxg.speceditor.hc.elementlist.elements.ElementModel;
import by.fxg.speceditor.hc.elementlist.elements.ElementMultiHitbox;
import by.fxg.speceditor.hc.elementlist.elements.ElementPointArray;
import by.fxg.speceditor.project.Project;

public class SpecFormatConverter {
	public static SpecGraph convertToGraph(Project project, PMObjectExplorer explorer, boolean convertInvisible) {
		SpecGraph graph = new SpecGraph();
		graph.bufferClearColor = new Color(project.bufferColor);
		graph.cameraSettings = new Vector3(project.cameraSettings);
		graph.environmentAttributes = new Array<>(project.viewportAttributes);
		graph.models = new Array<>();
		graph.lights = new Array<>();
		graph.decals = new Array<>();
		graph.hitboxes = new Array<>();
		graph.points = new Array<>();
		
		exploreFolder(graph, explorer.elementStack, convertInvisible);
		
		return graph;
	}
	
	private static void exploreFolder(SpecGraph graph, ElementStack stack, boolean convertInvisible) {
		for (TreeElement element : stack.getItems()) {
			if (element.isVisible() || !element.isVisible() && convertInvisible) {
				if (element instanceof ElementFolder) {
					ElementFolder folder = (ElementFolder)element;
					exploreFolder(graph, folder.getStack(), convertInvisible);
				}
				else if (element instanceof ElementModel) graph.models.add(((ElementModel)element).convert());
				else if (element instanceof ElementLight) graph.lights.add(((ElementLight)element).convert());
				else if (element instanceof ElementDecal) graph.decals.add(((ElementDecal)element).convert());
				else if (element instanceof ElementHitbox) graph.hitboxes.add(((ElementHitbox)element).convert());
				else if (element instanceof ElementMeshHitbox) graph.hitboxes.add(((ElementMeshHitbox)element).convert());
				else if (element instanceof ElementMultiHitbox) graph.hitboxes.add(((ElementMultiHitbox)element).convert());
				else if (element instanceof ElementPointArray) graph.points.add(((ElementPointArray)element).convert());
			}
		}
	}
}
