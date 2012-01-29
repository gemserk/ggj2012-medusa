package uy.globalgamejam.medusa.templates.enemies;

import java.util.ArrayList;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.components.ItemComponent;
import uy.globalgamejam.medusa.resources.GameResources;
import uy.globalgamejam.medusa.tags.Groups;

import aurelienribon.box2deditor.FixtureAtlas;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
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
import com.gemserk.commons.gdx.graphics.ShapeUtils;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.resources.ResourceManager;

public class SimpleEnemyTemplate extends EntityTemplateImpl {

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
		Sprite sprite = parameters.get("sprite");
		sprite.getWidth();
		
		
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
		
		
//		Body body = bodyBuilder //
//				.fixture(bodyBuilder.fixtureDefBuilder() //
//						.categoryBits(Collisions.Enemy) //
//						.maskBits((short) (Collisions.Obstacle)) //
//						.circleShape(spatial.getWidth()/2) //
//				) //
//				.fixture(bodyBuilder.fixtureDefBuilder() //
//						.categoryBits(Collisions.Enemy) //
//						.sensor()
//						.maskBits((short) (Collisions.MainCharacter | Collisions.Tail)) //
//						.circleShape(0.25f) //
//				) //
//				.position(0f, 0f) //
//				.type(BodyType.DynamicBody) //
//				.angle(0f) //
//				.userData(entity) //
//				.build();
//
//		body.setTransform(spatial.getX(), spatial.getY(), spatial.getAngle());

		body.setLinearVelocity(parameters.get("initialVelocity",new Vector2()));
		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new ItemComponent());

		entity.addComponent(new GroupComponent(Groups.Enemies));
		
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));

		entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.WHITE));
		entity.addComponent(new RenderableComponent(-1, true));

	}

}
