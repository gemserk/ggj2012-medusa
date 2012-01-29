package uy.globalgamejam.medusa;

import java.util.Comparator;

import uy.globalgamejam.medusa.resources.GameResources;
import uy.globalgamejam.medusa.resources.GameResources.Animations;
import uy.globalgamejam.medusa.resources.GameResources.Sprites;
import uy.globalgamejam.medusa.templates.ExtraBackgroundTemplate;
import uy.globalgamejam.medusa.templates.WorldLimitTemplate;
import uy.globalgamejam.medusa.templates.ObstacleTemplate;
import uy.globalgamejam.medusa.templates.enemies.SimpleEnemyTemplate;
import uy.globalgamejam.medusa.templates.enemies.TopDownEnemyTemplate;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.math.MathUtils2;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.resources.ResourceManager;

public class LevelGeneratorTemplate {

	public class Element {
		public float xCoord;
		public Class<? extends EntityTemplate> entityTemplate;
		public Parameters parameters;
	}

	Injector injector;

	ResourceManager<String> resourceManager;

	public Array<Element> generate(float maxYCoord, float worldScale) {

		long iniTime = System.currentTimeMillis();
		
		initializeObstacles();
		float GENERATION_BETWEEN_ELEMENTS = 65;

		float distance = 10000;
		final Array<Element> elements = new Array<Element>();

		Array<Rectangle> boundings = new Array<Rectangle>();
		
		float iniX = 10;
		float lastX = iniX;
		Rectangle oldRectangle = new Rectangle();
		boolean oldTop = false;
		while (lastX < distance) {
			lastX += MathUtils.random(15, 20);
//			lastX += MathUtils.random(3, 3);
			Element element = new Element();
			element.xCoord = lastX - GENERATION_BETWEEN_ELEMENTS;
			element.entityTemplate = ObstacleTemplate.class;

			FixedObstacleDefinition obstacle = randomElement(obstacles);

			System.out.println("Sprite: " + obstacle.sprite);
			Sprite sprite = resourceManager.getResourceValue(obstacle.sprite);
			String fixtureId = obstacle.fixture;

			float width = sprite.getWidth() / worldScale;
			float height = sprite.getHeight() / worldScale;
			System.out.println(maxYCoord);
			float y;
			boolean top = false;
			if (obstacle.alignY == 0f) {
				y = -maxYCoord + 0.5f * height;
				top = false;
			} else if (obstacle.alignY == 1) {
				y = maxYCoord - 0.5f * height;
				top = true;
			} else {
				y = obstacle.yCoord;
			}
			
			Rectangle rectangle = new Rectangle(lastX, y, width, height);
			if(oldTop != top && rectangle.overlaps(oldRectangle))
				continue;
			
			oldTop = top;
			oldRectangle = rectangle;
			boundings.add(rectangle);

			element.parameters = new ParametersWrapper() //
					.put("spatial", new SpatialImpl(lastX, y, width, height, obstacle.angle)) //
					.put("fixtureId", fixtureId) //
					.put("sprite", sprite)//
			// .put("alignY", obstacle.alignY);//
			;

			elements.add(element);
			System.out.println("Element(obstacle) Created at " + element.xCoord);
		}

		lastX = iniX;
		enemy1: while (lastX < distance) {
			lastX += MathUtils.random(10, 20);
			Element element = new Element();
			element.xCoord = lastX - GENERATION_BETWEEN_ELEMENTS;
			
			element.entityTemplate = SimpleEnemyTemplate.class;

			
			Animation animation = resourceManager.getResourceValue(Animations.Enemy1);
			Sprite sprite = animation.getCurrentFrame();
			
			float enemyScale = 1f;
			float width = sprite.getWidth() / (worldScale*enemyScale);
			float height = sprite.getHeight() / (worldScale*enemyScale);
			
			
			float y = MathUtils.random(-maxYCoord, maxYCoord);
			Rectangle rectangle = new Rectangle(lastX, y, width, height);
			for (int i = 0; i < boundings.size; i++) {
				Rectangle bound = boundings.get(i);
				if(bound.overlaps(rectangle) || bound.contains(rectangle) || rectangle.contains(bound) || rectangle.overlaps(bound))
					continue enemy1;
			}
			
			element.parameters = new ParametersWrapper() //
					.put("spatial", new SpatialImpl(lastX, y, width, height, 0))//
					.put("initialVelocity", new Vector2(1, 0).rotate(MathUtils.random(360)).mul(MathUtils.random(5f)))//
					.put("animation", animation)//
					.put("fixtureId", "enemigo1a_1.png") //

			; //

			elements.add(element);
			System.out.println("Element(fixed enemy) Created at " + element.xCoord);
		}
		
		lastX = iniX;
		enemy2: while (lastX < distance) {
			lastX += MathUtils.random(10, 20);
			Element element = new Element();
			element.xCoord = lastX - GENERATION_BETWEEN_ELEMENTS;
			
			element.entityTemplate = TopDownEnemyTemplate.class;

			Animation animation = resourceManager.getResourceValue(Animations.Enemy2);
			Sprite sprite = animation.getCurrentFrame();
			float enemyScale = 1f;
			float width = sprite.getWidth() / (worldScale*enemyScale);
			float height = sprite.getHeight() / (worldScale*enemyScale);
			
			float y = MathUtils.random(-maxYCoord, maxYCoord);
			Rectangle rectangle = new Rectangle(lastX, y, width, height);
			for (int i = 0; i < boundings.size; i++) {
				Rectangle bound = boundings.get(i);
				if(bound.overlaps(rectangle) || bound.contains(rectangle) || rectangle.contains(bound) || rectangle.overlaps(bound))
					continue enemy2;
			}
			element.parameters = new ParametersWrapper() //
					.put("spatial", new SpatialImpl(lastX, y, width, height, 0))//
					.put("movingDown", MathUtils.randomBoolean())//
					.put("animation", animation)//
					.put("fixtureId", "enemigo2a_1.png") //

			; //

			elements.add(element);
			System.out.println("Element(fixed enemy) Created at " + element.xCoord);
		}
		
		lastX = iniX;
		String[] extraElements = {Sprites.Astronauta, Sprites.Nave, Sprites.Satelite}; 
		extra: while (lastX < distance) {
			lastX += MathUtils.random(80,120);
			Element element = new Element();
			element.xCoord = lastX - GENERATION_BETWEEN_ELEMENTS;
			
			element.entityTemplate = ExtraBackgroundTemplate.class;

			String spriteKey = randomElement(extraElements);
			Sprite sprite = resourceManager.getResourceValue(spriteKey);
			float enemyScale = 1f;
			float width = sprite.getWidth() / (worldScale*enemyScale);
			float height = sprite.getHeight() / (worldScale*enemyScale);
			
			float y = MathUtils.random(-maxYCoord, maxYCoord);
			Rectangle rectangle = new Rectangle(lastX, y, width, height);
			for (int i = 0; i < boundings.size; i++) {
				Rectangle bound = boundings.get(i);
				if(bound.overlaps(rectangle) || bound.contains(rectangle) || rectangle.contains(bound) || rectangle.overlaps(bound))
					continue extra;
			}
			element.parameters = new ParametersWrapper() //
					.put("spatial", new SpatialImpl(lastX, y, width, height, 0))//
					.put("angularVelocity", MathUtils.random(0.1f, 1.0f))//
					.put("sprite", sprite)//

			; //

			elements.add(element);
			System.out.println("Element(fixed enemy) Created at " + element.xCoord);
		}

//		lastX = 0;
//		while (lastX < distance) {
//			lastX += MathUtils.random(10, 20);
//			Element element = new Element();
//			element.xCoord = lastX - GENERATION_BETWEEN_ELEMENTS;
//			element.entityTemplate = SimpleEnemyTemplate.class;
//
//			element.parameters = new ParametersWrapper() //
//					.put("spatial", new SpatialImpl(lastX, 5, 1, 1, 0)); //
//
//			elements.add(element);
//			System.out.println("Element(fixed enemy) Created at " + element.xCoord);
//		}

		elements.sort(new Comparator<LevelGeneratorTemplate.Element>() {

			@Override
			public int compare(Element o1, Element o2) {
				return (int) Math.signum(o1.xCoord - o2.xCoord);

			}
		});
		
		System.out.println("TimeToGenerateLevel: " + (System.currentTimeMillis() - iniTime));
		
		return elements;
	}

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
			obstacles.add(new FixedObstacleDefinition("Muro" + i + "a", "muro" + i + "a.png", 1, 0, 0));
			obstacles.add(new FixedObstacleDefinition("Muro" + i + "b", "muro" + i + "b.png", 1, 0, 0));
			obstacles.add(new FixedObstacleDefinition("Muro" + i + "a", "muro" + i + "a.png", 0f, 0, MathUtils.degreesToRadians * 180));
			obstacles.add(new FixedObstacleDefinition("Muro" + i + "b", "muro" + i + "b.png", 0f, 0, MathUtils.degreesToRadians * 180));
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
