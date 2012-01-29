package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.components.Controller;
import uy.globalgamejam.medusa.components.ControllerComponent;
import uy.globalgamejam.medusa.components.EngineComponent;
import uy.globalgamejam.medusa.components.TailComponent;
import uy.globalgamejam.medusa.scripts.CharacterMovementScript;
import uy.globalgamejam.medusa.scripts.EatEnemiesScript;
import uy.globalgamejam.medusa.scripts.EngineScript;
import uy.globalgamejam.medusa.scripts.MoveTailScript;
import uy.globalgamejam.medusa.scripts.ReplayRecorderScript;
import uy.globalgamejam.medusa.tags.Tags;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.resources.ResourceManager;

public class SnakeCharacterTemplate extends EntityTemplateImpl {

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
						.circleShape(0.5f) //
						.maskBits(Collisions.All) //
				) //
				.type(BodyType.DynamicBody) //
				.position(spatial.getX(), spatial.getY()) //
				.userData(entity) //
				.build();

		entity.addComponent(new TagComponent(Tags.MainCharacter));
		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));

		Controller controller = parameters.get("controller");
		controller.desiredY = spatial.getY();
		entity.addComponent(new ControllerComponent(controller));

		entity.addComponent(new EngineComponent(10f));
		entity.addComponent(new TailComponent());
		
		ReplayRecorderScript replayRecorderScript = new ReplayRecorderScript();
		injector.injectMembers(replayRecorderScript);

		entity.addComponent(new ScriptComponent( //
				injector.getInstance(CharacterMovementScript.class), //
				injector.getInstance(EngineScript.class), //
				injector.getInstance(EatEnemiesScript.class), //
				injector.getInstance(MoveTailScript.class), //
				replayRecorderScript //
		));

	}

}
