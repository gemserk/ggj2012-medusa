package uy.globalgamejam.medusa;

import uy.globalgamejam.medusa.templates.ItemSpawnerTemplate;
import uy.globalgamejam.medusa.templates.ItemTemplate;

import com.badlogic.gdx.utils.Array;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;

public class LevelGeneratorTemplate {

	public class Element {
		public float xCoord;
		public EntityTemplate entityTemplate;
		public Parameters parameters;

	}

	Injector injector;

	public Array<Element> generate() {
		float distance = 100;
		final Array<Element> elements = new Array<Element>();

		float lastX = 0;
		for (int i = 0; i < 20; i++) {
			lastX += 10;
			Element element = new Element();
			element.xCoord = lastX - 10;
			element.entityTemplate = injector.getInstance(ItemTemplate.class);
			element.parameters = new ParametersWrapper().put("spatial", new SpatialImpl(lastX, 0, 1, 1, 0));
			elements.add(element);
			System.out.println("Element Created at " + element.xCoord);
		}
		
		return elements;
	}
}
