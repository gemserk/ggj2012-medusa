package uy.globalgamejam.medusa.components;

import com.artemis.ComponentType;
import com.artemis.ComponentTypeManager;
import com.artemis.Entity;

public class Components extends com.gemserk.commons.artemis.components.Components {
	
	public static final Class<ControllerComponent> controllerComponentClass = ControllerComponent.class;
	public static final ComponentType controllerComponentType = ComponentTypeManager.getTypeFor(controllerComponentClass);

	public static ControllerComponent getControllerComponent(Entity e) {
		return controllerComponentClass.cast(e.getComponent(controllerComponentType));
	}
	
	public static final Class<EngineComponent> engineComponentClass = EngineComponent.class;
	public static final ComponentType engineComponentType = ComponentTypeManager.getTypeFor(engineComponentClass);

	public static EngineComponent getEngineComponent(Entity e) {
		return engineComponentClass.cast(e.getComponent(engineComponentType));
	}
	
	public static final Class<ItemComponent> itemComponentClass = ItemComponent.class;
	public static final ComponentType itemComponentType = ComponentTypeManager.getTypeFor(itemComponentClass);

	public static ItemComponent getItemComponent(Entity e) {
		return itemComponentClass.cast(e.getComponent(itemComponentType));
	}
}
