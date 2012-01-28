package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.components.ItemComponent;
import uy.globalgamejam.medusa.resources.GameResources;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.resources.ResourceManager;

public class BasicEnemyTemplate extends EntityTemplateImpl {

	BodyBuilder bodyBuilder;
	Injector injector;
	ResourceManager<String> resourceManager;

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	public void setBodyBuilder(BodyBuilder bodyBuilder) {
		this.bodyBuilder = bodyBuilder;
	}

	public static class RotateScript extends ScriptJavaImpl {

		@Override
		public void update(World world, Entity e) {
			Spatial spatial = Components.getSpatialComponent(e).getSpatial();
			spatial.setAngle(spatial.getAngle() + 45f * GlobalTime.getDelta());
		}

	}

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");

		Body body = bodyBuilder.mass(1f) //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.sensor() //
						.categoryBits(Collisions.Enemy) //
						.maskBits((short)(Collisions.MainCharacter | Collisions.Tail)) //
						.circleShape(0.25f) //
				) //
				.position(0f, 0f) //
				.type(BodyType.DynamicBody) //
				.angle(0f) //
				.userData(entity) //
				.build();
		
		body.setTransform(spatial.getX(), spatial.getY(), spatial.getAngle());

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));
		entity.addComponent(new ItemComponent());

		Sprite sprite = resourceManager.getResourceValue(GameResources.Sprites.Item);

		entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.ORANGE));
		entity.addComponent(new RenderableComponent(-1, true));
		
	}

}
