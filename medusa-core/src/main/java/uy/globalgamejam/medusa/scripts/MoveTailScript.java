package uy.globalgamejam.medusa.scripts;

import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.TailComponent;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.games.Physics;
import com.gemserk.commons.gdx.games.Spatial;

public class MoveTailScript extends ScriptJavaImpl {

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