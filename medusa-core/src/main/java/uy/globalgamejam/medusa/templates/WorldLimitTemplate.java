package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.tags.Groups;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.GroupComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.resources.ResourceManager;

public class WorldLimitTemplate extends EntityTemplateImpl {

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

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");

		Body body = bodyBuilder.mass(1f) //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.categoryBits(Collisions.Obstacle) //
						.maskBits(Collisions.All) //
						.boxShape(spatial.getWidth(), spatial.getHeight()) //
				) //
				.position(spatial.getX(), spatial.getY()) //
				.type(BodyType.StaticBody) //
				.angle(spatial.getAngle()) //
				.userData(entity) //
				.build();
		
		String spriteId = parameters.get("sprite");
		
		entity.addComponent(new GroupComponent(Groups.Obstacles));

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));

		Sprite sprite = resourceManager.getResourceValue(spriteId);

		entity.addComponent(new SpriteComponent(sprite, 0.5f, 0f, Color.WHITE));
		entity.addComponent(new RenderableComponent(-1, true));
	}

}
