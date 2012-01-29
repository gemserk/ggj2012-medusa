package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.resources.GameResources;
import uy.globalgamejam.medusa.tags.Tags;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.utils.ParametersWrapper;

public class BackgroundSpawnerTemplate extends EntityTemplateImpl {

	Injector injector;

	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	public static class WorldLimitsSpawnerScript extends ScriptJavaImpl {

		Injector injector;
		EntityFactory entityFactory;
		EventManager eventManager;

		float lastObstacleX = 0f;

		private Entity[] backgrounds;

		@Override
		public void init(World world, Entity e) {

			backgrounds = new Entity[3];

			backgrounds[0] = entityFactory.instantiate(injector.getInstance(StaticSpriteEntityTemplate.class), new ParametersWrapper() //
					.put("spatial", new SpatialImpl(512f * 0, 0f, 512f, 480f, 0f)) //
					.put("spriteId", GameResources.Sprites.Background01) //
					.put("center", new Vector2(0f, 0f)) //
					.put("layer", -1000) //
					);

			backgrounds[1] = entityFactory.instantiate(injector.getInstance(StaticSpriteEntityTemplate.class), new ParametersWrapper() //
					.put("spatial", new SpatialImpl(512f * 1, 0f, 512f, 480f, 0f)) //
					.put("spriteId", GameResources.Sprites.Background02) //
					.put("center", new Vector2(0f, 0f)) //
					.put("layer", -1000) //
					);

			backgrounds[2] = entityFactory.instantiate(injector.getInstance(StaticSpriteEntityTemplate.class), new ParametersWrapper() //
					.put("spatial", new SpatialImpl(512f * 2, 0f, 512f, 480f, 0f)) //
					.put("spriteId", GameResources.Sprites.Background03) //
					.put("center", new Vector2(0f, 0f)) //
					.put("layer", -1000) //
					);
		}

		@Override
		public void update(World world, Entity e) {

			Entity mainCharacter = world.getTagManager().getEntity(Tags.MainCharacter);

			if (mainCharacter == null)
				return;

			Spatial spatial = Components.getSpatialComponent(mainCharacter).getSpatial();

			for (int i = 0; i < backgrounds.length; i++) {

				Entity background = backgrounds[i];
				Spatial backgroundSpatial = Components.getSpatialComponent(background).getSpatial();

				if (backgroundSpatial.getX() + backgroundSpatial.getWidth() < spatial.getX() * 5) {
					float x = backgroundSpatial.getX();
					x += 512f * backgrounds.length;
					backgroundSpatial.setPosition(x, backgroundSpatial.getY());
				}
			}

		}

	}

	@Override
	public void apply(Entity entity) {
		WorldLimitsSpawnerScript worldLimitsSpawnerScript = new WorldLimitsSpawnerScript();
		injector.injectMembers(worldLimitsSpawnerScript);
		entity.addComponent(new ScriptComponent(worldLimitsSpawnerScript));
	}

}
