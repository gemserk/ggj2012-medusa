package uy.globalgamejam.medusa;

import java.util.Comparator;

import uy.globalgamejam.medusa.resources.GameResources;
import uy.globalgamejam.medusa.templates.ItemTemplate;
import uy.globalgamejam.medusa.templates.ObstacleTemplate;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.resources.ResourceManager;

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
			System.out.println("Element(obstacle) Created at " + element.xCoord);
		}
		lastX = 0;
		for (int i = 0; i < 20; i++) {
			lastX += MathUtils.random(30, 60);
			Element element = new Element();
			element.xCoord = lastX - 20;
			element.entityTemplate = injector.getInstance(ObstacleTemplate.class);

			int index = randomIndex(sprites);

			Sprite sprite = resourceManager.getResourceValue(sprites[index]);
			String fixtureId = fixtures[index];

			float width = sprite.getWidth() / 48f;
			float height = sprite.getHeight() / 48f;

			element.parameters = new ParametersWrapper() //
					.put("spatial", new SpatialImpl(lastX, 5, width, height, 0)) //
					.put("fixtureId", fixtureId) //
					.put("sprite", sprite) //
			;
			elements.add(element);
			System.out.println("Element(obstacle) Created at " + element.xCoord);
		}
		
		elements.sort(new Comparator<LevelGeneratorTemplate.Element>() {
			
			@Override
			public int compare(Element o1, Element o2) {
				return (int)Math.signum(o1.xCoord - o2.xCoord);
				
			}
		});		
		return elements;
	}

	ResourceManager<String> resourceManager;

	String[] sprites = { GameResources.Sprites.Obstacle0, GameResources.Sprites.Obstacle1 };
	String[] fixtures = { "images/world/obstacle-01.png", "images/world/obstacle-02.png" };

	public <T> T randomElement(T[] elements) {
		if (elements.length == 0)
			return null;
		return elements[randomIndex(elements)];
	}

	public <T> int randomIndex(T[] elements) {
		return MathUtils.random(0, elements.length - 1);
	}
}
