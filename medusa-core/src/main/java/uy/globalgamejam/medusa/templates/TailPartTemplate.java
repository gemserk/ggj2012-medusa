package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.TailComponent;
import uy.globalgamejam.medusa.components.TailPartComponent;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.box2d.Contacts;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;

public class TailPartTemplate extends EntityTemplateImpl {

	public static class DestroyTailScript extends ScriptJavaImpl {
		
		@Override
		public void update(World world, Entity e) {
			super.update(world, e);
			
			PhysicsComponent physicsComponent = Components.getPhysicsComponent(e);
			
			Contacts contacts = physicsComponent.getContact();
			
			if (!contacts.isInContact())
				return;
			
			TailPartComponent tailPartComponent = Components.getTailPartComponent(e);
			Entity owner = tailPartComponent.owner;
			
			TailComponent tailComponent = Components.getTailComponent(owner);
			
			int indexOf = tailComponent.parts.indexOf(e);
			
			if (indexOf == -1)
				return;
			
			int i = tailComponent.parts.size()- 1;
			
			while (tailComponent.parts.size() > indexOf) {
				Entity tailPart = tailComponent.parts.remove(i);
				i--;
				tailPart.delete();
			}
			
			tailComponent.parts.remove(this);
		}
		
	}

	BodyBuilder bodyBuilder;
	Injector injector;

	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	public void setBodyBuilder(BodyBuilder bodyBuilder) {
		this.bodyBuilder = bodyBuilder;
	}

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");
		Entity owner = parameters.get("owner");

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.circleShape(0.1f) //
						.sensor() //
						.categoryBits(Collisions.Tail)
						.maskBits(Collisions.Enemy) //
				) //
				.type(BodyType.DynamicBody) //
				.position(0f, 0f) //
				.userData(entity) //
				.build();
		
		body.setTransform(spatial.getX(), spatial.getY(), 0f);

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));
		entity.addComponent(new ScriptComponent(new DestroyTailScript()));
		
		entity.addComponent(new TailPartComponent(owner));
	}

}
