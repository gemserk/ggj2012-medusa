package uy.globalgamejam.medusa.templates;


import uy.globalgamejam.medusa.Collisions;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.box2d.Box2dUtils;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.gdx.graphics.Triangulator;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.resources.ResourceManager;

public class ObstacleTemplateSVG extends EntityTemplateImpl {

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
		Triangulator triangulator = parameters.get("triangulator");

		FixtureDef[] fixtureDefs = Box2dUtils.fixturesFromTriangulator(triangulator);

		Body body = bodyBuilder.mass(1f) //
				.fixtures(fixtureDefs) //
				.position(0f, 0f) //
				.type(BodyType.StaticBody) //
				.angle(0f) //
				.userData(entity) //
				.build();

		Box2dUtils.setFilter(body, Collisions.Obstacle, Collisions.All, (short) 0);

		float width = spatial.getWidth();
		float height = spatial.getHeight();

		// ArrayList<Fixture> fixtureList = body.getFixtureList();
		// ShapeUtils.translateFixtures(fixtureList, -width * 0.5f, -height * 0.5f);

		body.setTransform(spatial.getX(), spatial.getY(), spatial.getAngle());

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, width, height)));
	}

}
