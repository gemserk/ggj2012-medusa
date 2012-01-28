package uy.globalgamejam.medusa.systems;

import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.LightComponent;
import box2dLight.RayHandler;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.utils.Disposable;

public class LightsSystem extends EntityProcessingSystem implements Disposable {

	RayHandler rayHandler;

	public void setRayHandler(RayHandler rayHandler) {
		this.rayHandler = rayHandler;
	}

	public LightsSystem() {
		super(Components.lightComponentClass);
	}

	// @Override
	// protected void added(Entity e) {
	// LightComponent lightComponent = Components.getLightComponent(e);
	// rayHandler.lightList.add(lightComponent.light);
	// }

	// @Override
	// protected void removed(Entity e) {
	// LightComponent lightComponent = Components.getLightComponent(e);
	// rayHandler.lightList.removeValue(lightComponent.light, true);
	// }

	@Override
	protected void enabled(Entity e) {
		LightComponent lightComponent = Components.getLightComponent(e);
		rayHandler.lightList.add(lightComponent.light);
		lightComponent.light.setActive(true);
	}

	@Override
	protected void disabled(Entity e) {
		LightComponent lightComponent = Components.getLightComponent(e);
		rayHandler.lightList.removeValue(lightComponent.light, true);
		lightComponent.light.setActive(false);
	}
	
	@Override
	protected void end() {
		super.end();
//		rayHandler.updateRays();
	}

	@Override
	protected void process(Entity e) {

	}

	@Override
	public void dispose() {
		rayHandler.dispose();
	}

}
