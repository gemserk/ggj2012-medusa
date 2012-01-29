package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.Controller;
import uy.globalgamejam.medusa.components.ControllerComponent;
import uy.globalgamejam.medusa.components.EngineComponent;
import uy.globalgamejam.medusa.components.TailComponent;
import uy.globalgamejam.medusa.resources.GameResources.Sprites;
import uy.globalgamejam.medusa.scripts.CharacterMovementScript;
import uy.globalgamejam.medusa.scripts.EatEnemiesScript;
import uy.globalgamejam.medusa.scripts.EngineScript;
import uy.globalgamejam.medusa.scripts.MoveTailScript;
import uy.globalgamejam.medusa.scripts.ReplayRecorderScript;
import uy.globalgamejam.medusa.tags.Tags;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
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
	
	public static class SnakeStatus extends Component{
		boolean vulnerable = false;
		
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

		spatial.setSize(2.5f, 2.5f);
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

		Sprite sprite = resourceManager.getResourceValue(Sprites.Cabeza);

		SpriteComponent spriteComponent = new SpriteComponent(sprite, 0.5f, 0.5f, Color.WHITE);
		spriteComponent.setUpdateRotation(false);
		entity.addComponent(spriteComponent);
		entity.addComponent(new RenderableComponent(5, true));

		ReplayRecorderScript replayRecorderScript = new ReplayRecorderScript();
		injector.injectMembers(replayRecorderScript);

		entity.addComponent(new ScriptComponent( //
				injector.getInstance(CharacterMovementScript.class), //
				injector.getInstance(EngineScript.class), //
				injector.getInstance(EatEnemiesScript.class), //
				injector.getInstance(MoveTailScript.class), //
				replayRecorderScript, //
				injector.getInstance(DangerScript.class)
		));

	}
	
	public static class DangerScript extends ScriptJavaImpl{
		public static final int DANGER_TAIL_NUMBER = 15;

		@Override
		public void update(World world, Entity e) {
			
			TailComponent tailComponent = Components.getTailComponent(e);
			if(tailComponent.parts.size()>DANGER_TAIL_NUMBER)
				return;
			
			SpriteComponent spriteComponent = Components.getSpriteComponent(e);
			SpatialComponent spatialComponent = Components.getSpatialComponent(e);
			
			
			
			
			Color color = spriteComponent.getColor();
			color.b = (((spatialComponent.getSpatial().getX()*60) % 255) + 50)/255;
			color.clamp();
			
		}
	}
}
