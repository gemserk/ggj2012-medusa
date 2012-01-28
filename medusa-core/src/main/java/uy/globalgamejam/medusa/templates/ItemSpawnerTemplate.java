package uy.globalgamejam.medusa.templates;


import uy.globalgamejam.medusa.Events;
import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.tags.Tags;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.MathUtils;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.events.Event;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.reflection.Handles;
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

public class ItemSpawnerTemplate extends EntityTemplateImpl {

	Injector injector;

	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	public static class ItemSpawnerScript extends ScriptJavaImpl {

		Injector injector;
		EntityFactory entityFactory;
		EventManager eventManager;

		float lastObstacleY = 0f;

		Store<Entity> itemStore = new EntityStore(new StoreFactory<Entity>() {

			@Override
			public Entity createObject() {
				return entityFactory.instantiate(injector.getInstance(ItemTemplate.class), new ParametersWrapper() //
						.put("spatial", new SpatialImpl(0f, 0f, 1f, 1f, 0f)) //
						);
			}
		});

		@Override
		public void init(World world, Entity e) {
			lastObstacleY = 0f;
		}

		@Override
		public void update(World world, Entity e) {

			Entity mainCharacter = world.getTagManager().getEntity(Tags.MainCharacter);

			if (mainCharacter == null)
				return;

			Spatial mainCharacterSpatial = Components.getSpatialComponent(mainCharacter).getSpatial();

			if (mainCharacterSpatial.getY() > lastObstacleY) {

				lastObstacleY += 30f;

				int items = MathUtils.random(3, 8);

				for (int i = 0; i < items; i++) {
					float x = MathUtils.random(-5f, 5f);
					float y = MathUtils.random(5f, 10f);

					lastObstacleY += y;

					Entity item = itemStore.get();

					Spatial spatial = Components.getSpatialComponent(item).getSpatial();
					spatial.setPosition(x, lastObstacleY);
					spatial.setAngle(MathUtils.random(0f, 360f));

				}

			}

			for (int i = 0; i < itemStore.size(); i++) {
				Entity item = itemStore.get(i);

				Spatial spatial = Components.getSpatialComponent(item).getSpatial();

				if (spatial.getY() < mainCharacterSpatial.getY() - 10f)
					itemStore.free(item);
			}

		}

		@Handles(ids = Events.ItemGrabbed)
		public void itemGrabbed(Event event) {
			Entity item = (Entity) event.getSource();
			itemStore.free(item);
		}

	}

	@Override
	public void apply(Entity entity) {
		entity.addComponent(new ScriptComponent(injector.getInstance(ItemSpawnerScript.class)));
	}

}
