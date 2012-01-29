package uy.globalgamejam.medusa.templates.enemies;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.components.ItemComponent;
import uy.globalgamejam.medusa.resources.GameResources;
import uy.globalgamejam.medusa.tags.Groups;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.GroupComponent;
import com.gemserk.commons.artemis.components.LinearVelocityLimitComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.resources.ResourceManager;

public class TopDownEnemyTemplate extends EntityTemplateImpl {

	BodyBuilder bodyBuilder;
	Injector injector;
	ResourceManager<String> resourceManager;
	
	public static class TopDownMovementScript extends ScriptJavaImpl {
		
		float height = 3.25f;
		boolean movingDown;
		
		public TopDownMovementScript(Boolean movingDown) {
			movingDown = movingDown.booleanValue();
		}

		@Override
		public void init(World world, Entity e) {

		}
		
		@Override
		public void update(com.artemis.World world, Entity e) {

			PhysicsComponent physicsComponent = Components.getPhysicsComponent(e);
			
			Body body = physicsComponent.getBody();
			Spatial spatial = Components.getSpatialComponent(e).getSpatial();
			
			if (spatial.getY() > height) {
				movingDown = false;
			} else if (spatial.getY() < -height) {
				movingDown = true;
			}
			
			if (movingDown) {
				body.applyForceToCenter(-10f, 15f);
			} else {
				body.applyForceToCenter(-10f, -15f);
			}
			
		}
		
	}

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
		Boolean movingDown = parameters.get("movingDown", Boolean.TRUE); 

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.categoryBits(Collisions.Enemy) //
						.maskBits((short) (Collisions.Obstacle)) //
						.circleShape(0.25f) //
				) //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.categoryBits(Collisions.Enemy) //
						.sensor()
						.maskBits((short) (Collisions.MainCharacter | Collisions.Tail)) //
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

		entity.addComponent(new GroupComponent(Groups.Enemies));

		Sprite sprite = resourceManager.getResourceValue(GameResources.Sprites.Item);

		entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.RED));
		entity.addComponent(new RenderableComponent(-1, true));

		entity.addComponent(new LinearVelocityLimitComponent(10f));
		
		entity.addComponent(new ScriptComponent(new TopDownMovementScript(movingDown)));
		
	}

}
