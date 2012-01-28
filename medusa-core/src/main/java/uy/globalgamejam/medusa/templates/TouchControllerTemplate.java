package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.Controller;
import uy.globalgamejam.medusa.components.ControllerComponent;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.input.AnalogInputMonitor;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;

public class TouchControllerTemplate extends EntityTemplateImpl {

	BodyBuilder bodyBuilder;
	Injector injector;

	public class TouchControllerScript extends ScriptJavaImpl {

		final Vector2 position = new Vector2();

		AnalogInputMonitor xCoordinateMonitor;

		Libgdx2dCamera camera;

		public TouchControllerScript(Libgdx2dCamera camera) {
			this.camera = camera;
		}

		@Override
		public void init(World world, Entity e) {
			xCoordinateMonitor = LibgdxInputMappingBuilder.pointerXCoordinateMonitor(Gdx.input, 0);
		}

		@Override
		public void update(World world, Entity e) {
			xCoordinateMonitor.update();

			ControllerComponent controllerComponent = Components.getControllerComponent(e);

//			controllerComponent.controller.direction = 0f;

			// Entity mainCharacter = world.getTagManager().getEntity(Tags.MainCharacter);
			//
			// SpatialComponent mainCharacterSpatialComponent = Components.getSpatialComponent(mainCharacter);
			// Spatial spatial = mainCharacterSpatialComponent.getSpatial();

			position.set(xCoordinateMonitor.getValue(), 0f);

			camera.unproject(position);

			controllerComponent.controller.desiredX = position.x;

			// // camera.project(position);
			//
			// // System.out.println(position.x);
			// int direction = (int) (xCoordinateMonitor.getValue() - position.x);
			//
			// // System.out.println(direction);
			//
			// if (Math.abs(direction) > 10)
			// controllerComponent.controller.direction = direction;
			//
			// // controllerComponent.controller.direction *= 1f;
		}

	}

	@Override
	public void apply(Entity entity) {
		Controller controller = parameters.get("controller");
		Libgdx2dCamera camera = parameters.get("camera");

		entity.addComponent(new ControllerComponent(controller));
		entity.addComponent(new ScriptComponent( //
				new TouchControllerScript(camera) //
		));
	}

}
