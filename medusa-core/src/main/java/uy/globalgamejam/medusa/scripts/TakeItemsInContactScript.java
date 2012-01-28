package uy.globalgamejam.medusa.scripts;


import uy.globalgamejam.medusa.Events;
import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.ItemComponent;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.box2d.Contacts;
import com.gemserk.commons.gdx.box2d.Contacts.Contact;

public class TakeItemsInContactScript extends ScriptJavaImpl {

	// could be a system

	EventManager eventManager;

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	public void update(World world, Entity e) {
		PhysicsComponent physicsComponent = Components.getPhysicsComponent(e);

		Contacts contacts = physicsComponent.getContact();

		if (!contacts.isInContact())
			return;

		for (int i = 0; i < contacts.getContactCount(); i++) {
			Contact contact = contacts.getContact(i);
			Entity item = (Entity) contact.getOtherFixture().getBody().getUserData();

			ItemComponent itemComponent = Components.getItemComponent(item);
			if (itemComponent == null)
				continue;

			eventManager.registerEvent(Events.ItemGrabbed, item);

			// item.delete();
		}

	}

}