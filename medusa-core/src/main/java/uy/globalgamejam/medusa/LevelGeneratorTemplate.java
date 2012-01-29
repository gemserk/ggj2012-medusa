package uy.globalgamejam.medusa;

import java.io.ObjectOutputStream.PutField;
import java.util.Comparator;

import uy.globalgamejam.medusa.resources.GameResources;
import uy.globalgamejam.medusa.templates.ItemTemplate;
import uy.globalgamejam.medusa.templates.ObstacleTemplate;
import uy.globalgamejam.medusa.templates.enemies.FixedEnemyTemplate;

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
	Float maxYCoord;
	Float worldScale;

	public Array<Element> generate() {
		
		initializeObstacles();
		float GENERATION_BETWEEN_ELEMENTS = 50;
		
		float distance = 1000;
		final Array<Element> elements = new Array<Element>();

		float lastX = 0;
		while(lastX<distance) {
			lastX += 10;
			Element element = new Element();
			element.xCoord = lastX - GENERATION_BETWEEN_ELEMENTS;
			element.entityTemplate = injector.getInstance(ItemTemplate.class);
			element.parameters = new ParametersWrapper().put("spatial", new SpatialImpl(lastX, 0, 1, 1, 0));
			elements.add(element);
			System.out.println("Element(items) Created at " + element.xCoord);
		}
		lastX = 0;
		while(lastX<distance)  {
			lastX += MathUtils.random(10, 30);
			Element element = new Element();
			element.xCoord = lastX - GENERATION_BETWEEN_ELEMENTS;
			element.entityTemplate = injector.getInstance(ObstacleTemplate.class);

			FixedObstacleDefinition obstacle = randomElement(obstacles);
			
			System.out.println("Sprite: " + obstacle.sprite);
			Sprite sprite = resourceManager.getResourceValue(obstacle.sprite);
			String fixtureId = obstacle.fixture;

			float width = sprite.getWidth() / worldScale;
			float height = sprite.getHeight() / worldScale;
			System.out.println(maxYCoord);
			float y;
			if(obstacle.alignY==0f){
				y = -maxYCoord+0.5f*height;
			} else if(obstacle.alignY==1){
				y = maxYCoord-0.5f*height;
			} else {
				y = obstacle.yCoord;
			}
			
			
			
			element.parameters = new ParametersWrapper() //
					.put("spatial", new SpatialImpl(lastX, y, width, height, obstacle.angle)) //
					.put("fixtureId", fixtureId) //
					.put("sprite", sprite)//
//					.put("alignY", obstacle.alignY);//
					;
			
			elements.add(element);
			System.out.println("Element(obstacle) Created at " + element.xCoord);
		}

		lastX = 0;
		while(lastX<distance)  {
			lastX += MathUtils.random(10, 20);
			Element element = new Element();
			element.xCoord = lastX - GENERATION_BETWEEN_ELEMENTS;
			element.entityTemplate = injector.getInstance(FixedEnemyTemplate.class);

			element.parameters = new ParametersWrapper() //
					.put("spatial", new SpatialImpl(lastX, 5, 1, 1, 0)); //

			elements.add(element);
			System.out.println("Element(fixed enemy) Created at " + element.xCoord);
		}

		elements.sort(new Comparator<LevelGeneratorTemplate.Element>() {

			@Override
			public int compare(Element o1, Element o2) {
				return (int) Math.signum(o1.xCoord - o2.xCoord);

			}
		});
		return elements;
	}



	ResourceManager<String> resourceManager;

	String[] sprites = { /* GameResources.Sprites.Obstacle0, GameResources.Sprites.Obstacle1 , */GameResources.Sprites.Muro1, GameResources.Sprites.Muro2 };
	String[] fixtures = { /* "images/world/obstacle-01.png", "images/world/obstacle-02.png", */"muro1.png", "muro2.png" };

	public class FixedObstacleDefinition {
		public String sprite;
		public String fixture;
		public float alignY;
		public float yCoord;
		public float angle;

		public FixedObstacleDefinition(String sprite, String fixture, float alignY, float yCoord, float angle) {
			this.sprite = sprite;
			this.fixture = fixture;
			this.alignY = alignY;
			this.yCoord = yCoord;
			this.angle = angle;
		}
	}

	Array<FixedObstacleDefinition> obstacles = new Array<FixedObstacleDefinition>();
	
	private void initializeObstacles() {
		
		for (int i = 1; i < 8; i++) {
			obstacles.add(new FixedObstacleDefinition("Muro"+i+"a", "muro" + i + "a.png", 1, 0, 0));
			obstacles.add(new FixedObstacleDefinition("Muro"+i+"b", "muro" + i + "b.png", 1, 0, 0));
			obstacles.add(new FixedObstacleDefinition("Muro"+i+"a", "muro" + i + "a.png",0f, 0, MathUtils.degreesToRadians * 180));
			obstacles.add(new FixedObstacleDefinition("Muro"+i+"b", "muro" + i + "b.png",0f, 0, MathUtils.degreesToRadians * 180));
		}
	}

	public <T> T randomElement(T[] elements) {
		if (elements.length == 0)
			return null;
		return elements[randomIndex(elements)];
	}

	public <T> int randomIndex(T[] elements) {
		return MathUtils.random(0, elements.length - 1);
	}
	
	public <T> T randomElement(Array<T> elements) {
		if (elements.size == 0)
			return null;
		return elements.items[MathUtils.random(0, elements.size - 1)];
	}

}
