package uy.globalgamejam.medusa.templates.enemies;

import java.util.ArrayList;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.components.ItemComponent;
import uy.globalgamejam.medusa.resources.GameResources;
import uy.globalgamejam.medusa.tags.Groups;

import aurelienribon.box2deditor.FixtureAtlas;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.artemis.components.AnimationComponent;
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
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.gdx.graphics.ShapeUtils;
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
		Animation animation = parameters.get("animation");
		Boolean movingDown = parameters.get("movingDown", Boolean.TRUE);

		Body body = bodyBuilder //
				.position(0f, 0f) //
				.type(BodyType.DynamicBody) //
				.angle(0f) //
				.userData(entity) //
				.build();

		FixtureAtlas obstaclesFixtureAtlas = resourceManager.getResourceValue(GameResources.FixtureAtlas.Obstacles);

		float width = spatial.getWidth();
		float height = spatial.getHeight();

		FixtureDef obstacleFixtureDef = bodyBuilder.fixtureDefBuilder() //
				.categoryBits(Collisions.Enemy) //
				.maskBits((short) (Collisions.Obstacle | Collisions.Enemy)) //
				.restitution(0.5f) //
				.build();

		String fixtureId = parameters.get("fixtureId");

		obstaclesFixtureAtlas.createFixtures(body, fixtureId, width, height, obstacleFixtureDef);

		ArrayList<Fixture> fixtureList = body.getFixtureList();

		Float alignX = parameters.get("alignX",0.5f);
		Float alignY = parameters.get("alignY",0.5f);
		
		
		obstacleFixtureDef = bodyBuilder.fixtureDefBuilder() //
				.categoryBits(Collisions.Enemy) //
				.sensor()
				.maskBits((short) (Collisions.MainCharacter | Collisions.Tail)) //
				.build();


		obstaclesFixtureAtlas.createFixtures(body, fixtureId, width, height, obstacleFixtureDef);

		fixtureList = body.getFixtureList();
		
		ShapeUtils.translateFixtures(fixtureList, -width * alignX, -height * alignY);

		body.setTransform(spatial.getX(), spatial.getY(), spatial.getAngle());

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));
		entity.addComponent(new ItemComponent());

		entity.addComponent(new GroupComponent(Groups.Enemies));

		entity.addComponent(new SpriteComponent(animation.getCurrentFrame(), 0.5f, 0.5f, Color.WHITE));
		entity.addComponent(new AnimationComponent(new Animation[] { animation }));

		entity.addComponent(new RenderableComponent(-1, true));


		entity.addComponent(new LinearVelocityLimitComponent(10f));

		entity.addComponent(new ScriptComponent(new TopDownMovementScript(movingDown),//
				new ScriptJavaImpl() {
					public void update(com.artemis.World world, Entity e) {
						AnimationComponent animationComponent = e.getComponent(AnimationComponent.class);

						Animation currentAnimation = animationComponent.getCurrentAnimation();
						currentAnimation.update(GlobalTime.getDelta());

						SpriteComponent spriteComponent = e.getComponent(SpriteComponent.class);
						spriteComponent.setSprite(currentAnimation.getCurrentFrame());
					}
				}));

	}

}
