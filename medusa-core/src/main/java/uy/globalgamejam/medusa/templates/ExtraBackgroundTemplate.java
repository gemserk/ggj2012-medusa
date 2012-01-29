package uy.globalgamejam.medusa.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;

public class ExtraBackgroundTemplate extends EntityTemplateImpl {

	@Override
	public void apply(Entity entity) {
		
		
		
		Spatial spatial = parameters.get("spatial");
		Sprite sprite = parameters.get("sprite");
		final Float angularVelocity = parameters.get("angularVelocity");
		
		entity.addComponent(new SpatialComponent(new SpatialImpl(spatial)));


		entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.WHITE));
		entity.addComponent(new RenderableComponent(-10, true));

		entity.addComponent(new ScriptComponent(new ScriptJavaImpl(){
			@Override
			public void update(World world, Entity e) {
				Spatial spatial = Components.getSpatialComponent(e).getSpatial();
				spatial.setAngle(spatial.getAngle() + angularVelocity * GlobalTime.getDelta());
			}
		}));

	}

}
