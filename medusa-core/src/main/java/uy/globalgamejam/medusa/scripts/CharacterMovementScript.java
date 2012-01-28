package uy.globalgamejam.medusa.scripts;

import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.Controller;
import uy.globalgamejam.medusa.components.ControllerComponent;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Spatial;

public class CharacterMovementScript extends ScriptJavaImpl {

	private final Vector2 force = new Vector2();

	@Override
	public void update(World world, Entity e) {
		ControllerComponent controllerComponent = Components.getControllerComponent(e);
		Controller controller = controllerComponent.controller;

		SpatialComponent spatialComponent = Components.getSpatialComponent(e);
		Spatial spatial = spatialComponent.getSpatial();

		PhysicsComponent physicsComponent = Components.getPhysicsComponent(e);
		Body body = physicsComponent.getBody();

		
		if (Math.abs(spatial.getY() - controller.desiredY) < 0.5f) {
			spatial.setPosition(spatial.getX(),controller.desiredY);
			Vector2 linearVelocity = body.getLinearVelocity();
			linearVelocity.y = 0f;
			body.setLinearVelocity(linearVelocity);
			return;
		}

		float direction = Math.signum(controller.desiredY - spatial.getY());

		if (Math.signum(body.getLinearVelocity().y) != direction) {
			body.setLinearVelocity(body.getLinearVelocity().x, 0f);
		}

		float newY = spatial.getY() + body.getLinearVelocity().y * GlobalTime.getDelta() * direction;

		if (newY > controller.desiredY && direction > 0) {
			newY = controller.desiredY;
			return;
		}

		if (newY < controller.desiredY && direction < 0) {
			newY = controller.desiredY;
			return;
		}

		force.set(0,direction);
		force.mul(1000f);

		body.applyForceToCenter(force);
	}

}