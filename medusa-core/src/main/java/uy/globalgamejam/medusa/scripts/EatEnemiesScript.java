package uy.globalgamejam.medusa.scripts;

import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.tags.Groups;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.GroupComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.box2d.Contacts;
import com.gemserk.commons.gdx.box2d.Contacts.Contact;

public class EatEnemiesScript extends ScriptJavaImpl {

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