package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.LevelGeneratorTemplate.Element;
import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.tags.Tags;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.reflection.Injector;

public class LevelInstantiator extends EntityTemplateImpl {

	EntityFactory entityFactory;
	Injector injector;
	
	
	@Override
	public void apply(Entity entity) {
		final Array<Element> elements = parameters.get("elements");
		entity.addComponent(new ScriptComponent(new ScriptJavaImpl(){
			
			int lastItemGenerated = 0;
			
			@Override
			public void update(World world, Entity e) {
				Entity mainCharacter = world.getTagManager().getEntity(Tags.MainCharacter);

				if (mainCharacter == null)
					return;

				Spatial mainCharacterSpatial = Components.getSpatialComponent(mainCharacter).getSpatial();

				float charX = mainCharacterSpatial.getX();
				
				
				while(lastItemGenerated < elements.size && elements.get(lastItemGenerated).xCoord <= charX){
					Element element = elements.get(lastItemGenerated);
					entityFactory.instantiate(element.entityTemplate, element.parameters);					
					System.out.println("Instantiated: " + lastItemGenerated);
					lastItemGenerated++;
				}
				
				
			}
		}));

	}

}
