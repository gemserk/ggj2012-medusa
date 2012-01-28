package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.components.ItemComponent;
import uy.globalgamejam.medusa.resources.GameResources;
import box2dLight.RayHandler;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.Components;
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
import com.gemserk.commons.reflection.Injector;
import com.gemserk.resources.ResourceManager;

public class ItemTemplate extends EntityTemplateImpl {

	BodyBuilder bodyBuilder;
	Injector injector;
	ResourceManager<String> resourceManager;

	RayHandler rayHandler;

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
						.categoryBits(Collisions.Item) //
						.maskBits(Collisions.MainCharacter) //
						.circleShape(0.25f) //
				) //
				.position(spatial.getX(), spatial.getY()) //
				.type(BodyType.DynamicBody) //
				.angle(spatial.getAngle()) //
				.userData(entity) //
				.build();

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));
		entity.addComponent(new ItemComponent());

		Sprite sprite = resourceManager.getResourceValue(GameResources.Sprites.Item);

		entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.ORANGE));
		entity.addComponent(new RenderableComponent(-1, true));

		entity.addComponent(new ScriptComponent(injector.getInstance(RotateScript.class)));

//		Light light = new PointLight(rayHandler, 30, Color.ORANGE, 2f, 0f, 0f);
//		light.attachToBody(body, 0f, 0.5f);
//		entity.addComponent(new LightComponent(light));
	}

}
