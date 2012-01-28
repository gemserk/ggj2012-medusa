package uy.globalgamejam.medusa.templates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;

import uy.globalgamejam.medusa.resources.GameResources;
import uy.globalgamejam.medusa.svg.FixturesSvgLayerProcessor;
import uy.globalgamejam.medusa.tags.Tags;

import com.artemis.Entity;
import com.artemis.World;
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
import com.gemserk.commons.gdx.graphics.Triangulator;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.svg.SvgLayerProcessor;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.resources.ResourceManager;

public class ObstacleSpawnerTemplate2 extends EntityTemplateImpl {

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

		public static <T> T random(Set<T> set) {
			int size = set.size();

			if (size <= 0)
				throw new IllegalStateException("cant get random of empty set");

			int random = MathUtils.random(0, size - 1);

			int i = 0;
			for (T t : set) {
				if (i == random)
					return t;
				i++;
			}

			throw new IllegalStateException("should never happen if the set has elements");
		}

		@Override
		public void init(World world, Entity e) {

			obstacles = new ArrayList<Entity>();

			EntityTemplate obstacleTemplate = injector.getInstance(ObstacleTemplate2.class);

			Document obstaclesDocument = resourceManager.getResourceValue(GameResources.XmlDocuments.Obstacles);
			Map<String, Triangulator> obstacleFixtures = new HashMap<String, Triangulator>();

			SvgLayerProcessor obstaclesLayerProcessor = new FixturesSvgLayerProcessor("obstacles", obstacleFixtures);
			obstaclesLayerProcessor.process(obstaclesDocument);

			for (int i = 0; i < 10; i++) {
				float x = MathUtils.random(-5f, 5f);
				float y = 20f * (i + 1);

				float angle = MathUtils.random(0f, 360f);

				String obstacleId = random(obstacleFixtures.keySet());
				Triangulator triangulator = obstacleFixtures.get(obstacleId);

				Entity obstacle = entityFactory.instantiate(obstacleTemplate, new ParametersWrapper() //
						.put("spatial", new SpatialImpl(x, y, 1f, 1f, angle)) //
						.put("triangulator", triangulator) //
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
