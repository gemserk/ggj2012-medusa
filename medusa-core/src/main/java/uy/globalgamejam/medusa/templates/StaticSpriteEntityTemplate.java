package uy.globalgamejam.medusa.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.FrustumCullingComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.math.MathUtils2;
import com.gemserk.resources.ResourceManager;

public class StaticSpriteEntityTemplate extends EntityTemplateImpl {
	
	private final Vector2 defaultCenter = new Vector2(0.5f, 0.5f);
	
	private ResourceManager<String> resourceManager;
	
	{
		parameters.put("layer", new Integer(0));
	}

	@Override
	public void apply(Entity entity) {
		String spriteId = parameters.get("spriteId");
		Spatial spatial = parameters.get("spatial");
		Integer layer = parameters.get("layer");
		
		Vector2 center = parameters.get("center", defaultCenter);
		Boolean useSpriteSize = parameters.get("useSpriteSize", false);
		
		Sprite sprite = resourceManager.getResourceValue(spriteId);
		if (sprite == null)
			throw new RuntimeException("Failed to instantiate static sprite, " + spriteId + " not found.");
		
		if (useSpriteSize)
			spatial.setSize(sprite.getWidth(), sprite.getHeight());
		
		entity.addComponent(new SpatialComponent(spatial));
		entity.addComponent(new SpriteComponent(sprite, center.x, center.y, Color.WHITE));
		entity.addComponent(new RenderableComponent(layer));
		
		float diagonal = MathUtils2.diagonal(spatial.getWidth(), spatial.getHeight());
		
		entity.addComponent(new FrustumCullingComponent(new Rectangle(-diagonal * center.x, -diagonal * center.y, diagonal, diagonal)));
	}

}
