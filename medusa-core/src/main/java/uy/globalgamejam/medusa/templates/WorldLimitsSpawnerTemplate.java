package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.tags.Tags;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.artemis.utils.EntityStore;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.utils.Store;
import com.gemserk.commons.utils.StoreFactory;
import com.gemserk.componentsengine.utils.ParametersWrapper;

public class WorldLimitsSpawnerTemplate extends EntityTemplateImpl {

	Injector injector;

	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	public static class WorldLimitsSpawnerScript extends ScriptJavaImpl {

		Injector injector;
		EntityFactory entityFactory;
		EventManager eventManager;

		float lastObstacleX = 0f;
		float y;
		
		String spriteId;

		Store<Entity> itemStore = new EntityStore(new StoreFactory<Entity>() {

			@Override
			public Entity createObject() {
				return entityFactory.instantiate(injector.getInstance(WorldLimitTemplate.class), new ParametersWrapper() //
						.put("spatial", new SpatialImpl(0f, 0f, 21.33f, 0.5f, 0f)) //
						.put("sprite", spriteId) //
						);
			}
		});
		
		public WorldLimitsSpawnerScript(String spriteId, float y) {
			this.spriteId = spriteId;
			this.y = y;
		}

		@Override
		public void init(World world, Entity e) {
			lastObstacleX = 0f;

			int items = 5;

			for (int i = 0; i < items; i++) {
				float x = 0f;

				Entity item = itemStore.get();

				Spatial spatial = Components.getSpatialComponent(item).getSpatial();
				spatial.setPosition(x + i * spatial.getWidth(), y);
				spatial.setAngle(0f);
				
				SpriteComponent spriteComponent = Components.getSpriteComponent(item);
				spriteComponent.getCenter().set(0.5f, y < 0 ? 0f : 1f);
				
				lastObstacleX += spatial.getWidth();
			}

		}

		@Override
		public void update(World world, Entity e) {

			Entity mainCharacter = world.getTagManager().getEntity(Tags.MainCharacter);

			if (mainCharacter == null)
				return;

			Spatial mainCharacterSpatial = Components.getSpatialComponent(mainCharacter).getSpatial();

			for (int i = 0; i < itemStore.size(); i++) {
				Entity entity = itemStore.get(i);
				Spatial spatial = Components.getSpatialComponent(entity).getSpatial();

				if (spatial.getX() < mainCharacterSpatial.getX() - 15f) {
					float x = spatial.getX();
					x += itemStore.size() * spatial.getWidth();
					spatial.setPosition(x, spatial.getY());
				}

			}

		}

	}

	@Override
	public void apply(Entity entity) {
		String spriteId = parameters.get("spriteId");
		Float y = parameters.get("y");
		
		WorldLimitsSpawnerScript worldLimitsSpawnerScript = new WorldLimitsSpawnerScript(spriteId, y);
		injector.injectMembers(worldLimitsSpawnerScript);
		entity.addComponent(new ScriptComponent(worldLimitsSpawnerScript));
	}

}
