package uy.globalgamejam.medusa.scripts;


import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.games.Spatial;

public class RemoveOldEntitiesScript extends ScriptJavaImpl {

	private final String group;
	private final String tag;

	public RemoveOldEntitiesScript(String group, String tag) {
		this.group = group;
		this.tag = tag;
	}
	
	@Override
	public void update(World world, Entity e) {
		
		ImmutableBag<Entity> entities = world.getGroupManager().getEntities(group);
		Entity target = world.getTagManager().getEntity(tag);
		
		Spatial spatial = Components.getSpatialComponent(target).getSpatial();
		
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			
			Spatial entitySpatial = Components.getSpatialComponent(entity).getSpatial();
			
			if (entitySpatial.getX() < spatial.getX() - 20f) {
				Gdx.app.log("medusa", "removing entity from group " + group);
				entity.delete();
			}
			
		}
		

	}

}