package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.components.Controller;
import uy.globalgamejam.medusa.components.ControllerComponent;
import uy.globalgamejam.medusa.components.EngineComponent;
import uy.globalgamejam.medusa.resources.GameResources;
import uy.globalgamejam.medusa.scripts.CharacterMovementScript;
import uy.globalgamejam.medusa.scripts.EngineScript;
import uy.globalgamejam.medusa.scripts.TakeItemsInContactScript;
import uy.globalgamejam.medusa.tags.Tags;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.resources.ResourceManager;

public class MainCharacterTemplate extends EntityTemplateImpl {

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

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.polygonShape(new Vector2[] { //
								new Vector2(0.5f, -0.5f), //
										new Vector2(0f, 0.5f), //
										new Vector2(-0.5f, -0.5f), //
								}) //
						.maskBits(Collisions.All) //
				) //
				.type(BodyType.DynamicBody) //
				.position(spatial.getX(), spatial.getY()) //
				.userData(entity) //
				.build();

		entity.addComponent(new TagComponent(Tags.MainCharacter));
		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));

		Sprite sprite = resourceManager.getResourceValue(GameResources.Sprites.Character);

		entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.WHITE));
		entity.addComponent(new RenderableComponent(0, true));

		Controller controller = parameters.get("controller");
		entity.addComponent(new ControllerComponent(controller));

		entity.addComponent(new EngineComponent());

		entity.addComponent(new ScriptComponent( //
				injector.getInstance(CharacterMovementScript.class), //
				injector.getInstance(TakeItemsInContactScript.class), //
				injector.getInstance(EngineScript.class) //
		));

//		Light light = new ConeLight(rayHandler, 50, new Color(1f, 1f, 1f, 0.5f), 20f, 0f, 0f, 90f, 30f);
//		light.attachToBody(body, 0f, 0.5f);
//		entity.addComponent(new LightComponent(light));
	}

}
