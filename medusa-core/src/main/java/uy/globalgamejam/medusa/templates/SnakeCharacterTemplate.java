package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.Controller;
import uy.globalgamejam.medusa.components.ControllerComponent;
import uy.globalgamejam.medusa.components.EngineComponent;
import uy.globalgamejam.medusa.components.TailComponent;
import uy.globalgamejam.medusa.scripts.CharacterMovementScript;
import uy.globalgamejam.medusa.scripts.EngineScript;
import uy.globalgamejam.medusa.tags.Groups;
import uy.globalgamejam.medusa.tags.Tags;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.GroupComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.box2d.Contacts;
import com.gemserk.commons.gdx.box2d.Contacts.Contact;
import com.gemserk.commons.gdx.games.Physics;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.resources.ResourceManager;

public class SnakeCharacterTemplate extends EntityTemplateImpl {

	BodyBuilder bodyBuilder;
	Injector injector;
	ResourceManager<String> resourceManager;

	public static class MoveTailScript extends ScriptJavaImpl {

		@Override
		public void update(World world, Entity e) {

			PhysicsComponent physicsComponent = Components.getPhysicsComponent(e);
			Spatial spatial = Components.getSpatialComponent(e).getSpatial();

			TailComponent tailComponent = Components.getTailComponent(e);

			float x = spatial.getX();

			Body characterBody = physicsComponent.getBody();

			Vector2 previousPartPosition = characterBody.getPosition();
			float previousPartAngle = characterBody.getAngle();

			int i = 1;

			for (Entity tailPart : tailComponent.parts) {

				Physics physics = Components.getPhysicsComponent(tailPart).getPhysics();
				Body tailBodyPart = physics.getBody();

				float amplitud = 0.02f * i;
				if (amplitud > 0.15f)
					amplitud = 0.15f;

				if (Math.abs(characterBody.getLinearVelocity().y) > 1f)
					amplitud *= 1f / (Math.abs(characterBody.getLinearVelocity().y) * 10f);

				float displacementY = (float) Math.sin(x * 2f + i) * amplitud;

				Vector2 currentPartPosition = tailBodyPart.getPosition();
				float currentPartAngle = tailBodyPart.getAngle();

				tailBodyPart.setTransform(x - 0.2f * i, previousPartPosition.y + displacementY, previousPartAngle);
				tailBodyPart.setLinearVelocity(0f, 0f);
				tailBodyPart.setAngularVelocity(0f);

				previousPartPosition = currentPartPosition;
				previousPartAngle = currentPartAngle;

				i++;
			}

		}

	}

	public static class EatEnemiesScript extends ScriptJavaImpl {

		@Override
		public void update(World world, Entity e) {
			PhysicsComponent physicsComponent = Components.getPhysicsComponent(e);

			Contacts contacts = physicsComponent.getContact();

			if (!contacts.isInContact())
				return;
			
			for (int i = 0; i < contacts.getContactCount(); i++) {
				
				Contact contact = contacts.getContact(i);
				
				Entity entity = (Entity) contact.getOtherFixture().getBody().getUserData();
				
				GroupComponent groupComponent = Components.getGroupComponent(entity);
				
				if (groupComponent == null)
					continue;
				
				if (Groups.Enemies.equals(groupComponent.group))
					entity.delete();
				
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

		// Sprite sprite = resourceManager.getResourceValue(GameResources.Sprites.Character);
		// entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.WHITE));
		// entity.addComponent(new RenderableComponent(0, true));

		Controller controller = parameters.get("controller");
		entity.addComponent(new ControllerComponent(controller));

		entity.addComponent(new EngineComponent(8f));
		entity.addComponent(new TailComponent());

		entity.addComponent(new ScriptComponent( //
				injector.getInstance(CharacterMovementScript.class), //
				injector.getInstance(EngineScript.class), //
				injector.getInstance(EatEnemiesScript.class), //
				injector.getInstance(MoveTailScript.class) //
		));

	}

}
