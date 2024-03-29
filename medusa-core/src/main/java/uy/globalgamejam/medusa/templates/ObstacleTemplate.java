package uy.globalgamejam.medusa.templates;

import java.util.ArrayList;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.resources.GameResources;
import uy.globalgamejam.medusa.tags.Groups;
import aurelienribon.box2deditor.FixtureAtlas;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
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

public class ObstacleTemplate extends EntityTemplateImpl {

	BodyBuilder bodyBuilder;
	Injector injector;
	ResourceManager<String> resourceManager;

	public void setBodyBuilder(BodyBuilder bodyBuilder) {
		this.bodyBuilder = bodyBuilder;
	}

	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");

		Body body = bodyBuilder //
				.position(0f, 0f) //
				.type(BodyType.StaticBody) //
				.angle(0) //
				.userData(entity) //
				.build();

		FixtureAtlas obstaclesFixtureAtlas = resourceManager.getResourceValue(GameResources.FixtureAtlas.Obstacles);

		float width = spatial.getWidth();
		float height = spatial.getHeight();

		FixtureDef obstacleFixtureDef = bodyBuilder.fixtureDefBuilder() //
				.categoryBits(Collisions.Obstacle) //
				.maskBits(Collisions.All) //
				.restitution(0.5f) //
				.build();

		String fixtureId = parameters.get("fixtureId");

		obstaclesFixtureAtlas.createFixtures(body, fixtureId, width, height, obstacleFixtureDef);

		ArrayList<Fixture> fixtureList = body.getFixtureList();

		Float alignX = parameters.get("alignX", 0.5f);
		Float alignY = parameters.get("alignY", 0.5f);

		ShapeUtils.translateFixtures(fixtureList, -width * alignX, -height * alignY);

		body.setTransform(spatial.getX(), spatial.getY(), spatial.getAngle());
		
		entity.addComponent(new GroupComponent(Groups.Obstacles));

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, width, height)));

		Sprite sprite = parameters.get("sprite");

		entity.addComponent(new SpriteComponent(sprite, alignX, alignY, Color.WHITE));
		entity.addComponent(new RenderableComponent(-10, true));
		
		
//		Body bodydebug = bodyBuilder.mass(1f) //
//				.fixture(bodyBuilder.fixtureDefBuilder() //
//						.sensor()//
//						.categoryBits(Collisions.Debug) //
//						.maskBits(Collisions.None) //
//						.boxShape(spatial.getWidth()/2f, spatial.getHeight()/2f)
//				) //
//				.position(spatial.getX(), spatial.getY()) //
//				.type(BodyType.StaticBody) //
//				.angle(spatial.getAngle()) //
//				.userData(null) //
//				.build();
	}

}
