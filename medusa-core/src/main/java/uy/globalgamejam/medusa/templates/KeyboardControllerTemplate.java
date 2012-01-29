package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.Controller;
import uy.globalgamejam.medusa.components.ControllerComponent;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.input.ButtonMonitor;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;

public class KeyboardControllerTemplate extends EntityTemplateImpl {

	BodyBuilder bodyBuilder;
	Injector injector;

	public class KeyboardControllerScript extends ScriptJavaImpl {
		
		private ButtonMonitor upButtonMonitor;
		private ButtonMonitor leftButtonMonitor;
		
		private float desiredY;

		@Override
		public void init(World world, Entity e) {
			upButtonMonitor = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.UP, Keys.LEFT);
			leftButtonMonitor = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.DOWN, Keys.RIGHT);
			
			ControllerComponent controllerComponent = Components.getControllerComponent(e);
			desiredY = controllerComponent.controller.desiredY;
		}
		
		@Override
		public void update(World world, Entity e) {
			upButtonMonitor.update();
			leftButtonMonitor.update();
			
			ControllerComponent controllerComponent = Components.getControllerComponent(e);
			controllerComponent.controller.desiredY = desiredY;
			
			float speed = 0.15f;
			
			if (upButtonMonitor.isHolded()) 
				desiredY += 1f * speed;
			
			if (leftButtonMonitor.isHolded()) 
				desiredY -= 1f * speed;
		}

	}

	@Override
	public void apply(Entity entity) {
		Controller controller = parameters.get("controller");
		entity.addComponent(new ControllerComponent(controller));
		entity.addComponent(new ScriptComponent( //
				new KeyboardControllerScript() //
		));
	}

}
