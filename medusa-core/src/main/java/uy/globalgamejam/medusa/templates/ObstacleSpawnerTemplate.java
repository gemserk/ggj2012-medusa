package uy.globalgamejam.medusa.templates;

import java.util.ArrayList;

import uy.globalgamejam.medusa.resources.GameResources;
import uy.globalgamejam.medusa.tags.Tags;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.resources.ResourceManager;

public class ObstacleSpawnerTemplate extends EntityTemplateImpl {

	Injector injector;

	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	public static class SpawnObstacleScript extends ScriptJavaImpl {

		Injector injector;

		ArrayList<Entity> obstacles;

		EntityFactory entityFactory;

		float lastObstacleY = 0f;

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

		@Override
		public void init(World world, Entity e) {

			obstacles = new ArrayList<Entity>();

			EntityTemplate obstacleTemplate = injector.getInstance(ObstacleTemplate.class);

			for (int i = 0; i < 10; i++) {
				float x = MathUtils.random(-5f, 5f);
				float y = 20f * (i + 1);
				float angle = MathUtils.random(0f, 360f);

				float scale = MathUtils.random(0.5f, 1f);

				int index = randomIndex(sprites);

				Sprite sprite = resourceManager.getResourceValue(sprites[index]);
				String fixtureId = fixtures[index];

				float width = scale * sprite.getWidth() / 48f;
				float height = scale * sprite.getHeight() / 48f;

				Entity obstacle = entityFactory.instantiate(obstacleTemplate, new ParametersWrapper() //
						.put("spatial", new SpatialImpl(x, y, width, height, angle)) //
						.put("fixtureId", fixtureId) //
						.put("sprite", sprite) //
						);

				obstacles.add(obstacle);

				lastObstacleY = y;
			}

		}

		@Override
		public void update(World world, Entity e) {

			Entity mainCharacter = world.getTagManager().getEntity(Tags.MainCharacter);

			if (mainCharacter == null)
				return;

			Spatial mainCharacterSpatial = Components.getSpatialComponent(mainCharacter).getSpatial();

			for (int i = 0; i < obstacles.size(); i++) {
				Entity obstacle = obstacles.get(i);

				SpatialComponent spatialComponent = Components.getSpatialComponent(obstacle);
				Spatial spatial = spatialComponent.getSpatial();

				if (spatial.getY() < mainCharacterSpatial.getY() - 10f) {
					float x = MathUtils.random(-5f, 5f);
					float y = lastObstacleY + MathUtils.random(15f, 30f);
					spatial.setPosition(x, y);
					lastObstacleY = y;
				}

			}

		}

	}

	@Override
	public void apply(Entity entity) {
		entity.addComponent(new ScriptComponent(injector.getInstance(SpawnObstacleScript.class)));
	}

}
