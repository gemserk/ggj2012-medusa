package uy.globalgamejam.medusa.scripts;

import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.EngineComponent;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;

public class EngineScript extends ScriptJavaImpl {
	
	// could be a system

	private final Vector2 force = new Vector2();

	@Override
	public void update(World world, Entity e) {
		EngineComponent engineComponent = Components.getEngineComponent(e);

		PhysicsComponent physicsComponent = Components.getPhysicsComponent(e);
		Body body = physicsComponent.getBody();

		if (body.getLinearVelocity().y > engineComponent.maxSpeed)
			return;

		force.set(0f, engineComponent.speed);

		body.applyForceToCenter(force);
	}

}