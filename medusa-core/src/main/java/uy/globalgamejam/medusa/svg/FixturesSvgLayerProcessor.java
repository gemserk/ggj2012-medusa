package uy.globalgamejam.medusa.svg;

import java.util.Map;

import org.w3c.dom.Element;

import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.graphics.ShapeUtils;
import com.gemserk.commons.gdx.graphics.Triangulator;
import com.gemserk.commons.svg.SvgLayerProcessor;
import com.gemserk.commons.svg.inkscape.SvgPath;

public class FixturesSvgLayerProcessor extends SvgLayerProcessor {

	Vector2 center = new Vector2();
	Map<String, Triangulator> obstacles;

	public FixturesSvgLayerProcessor(String layer, Map<String, Triangulator> obstacles) {
		super(layer);
		this.obstacles = obstacles;
	}

	@Override
	protected void handlePathObject(SvgPath svgPath, Element element, Vector2[] vertices) {
		ShapeUtils.calculateCenter(vertices, center);
		center.mul(-1f);
		ShapeUtils.translateVertices(vertices, center);
		obstacles.put(svgPath.getId(), ShapeUtils.triangulate(vertices));
		
		// System.out.println("new float[]{ ");
		// for (Vector2 vector2 : vertices) {
		// System.out.print("" + vector2.x + "f, " + vector2.y + "f, ");
		// }
		// System.out.println("");
		// System.out.println("}");
	}
}