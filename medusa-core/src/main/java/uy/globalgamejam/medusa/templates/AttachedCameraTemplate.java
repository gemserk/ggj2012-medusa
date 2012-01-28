package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.tags.Tags;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.CameraComponent;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.reflection.Injector;

public class AttachedCameraTemplate extends EntityTemplateImpl {

	BodyBuilder bodyBuilder;
	Injector injector;

	public static class FollowEntityScript extends ScriptJavaImpl {
		
		@Override
		public void update(World world, Entity e) {
			Entity mainCharacter = world.getTagManager().getEntity(Tags.MainCharacter);
			
			if (mainCharacter == null)
				return;
			
			SpatialComponent mainCharacterSpatialComponent = Components.getSpatialComponent(mainCharacter);
			Spatial spatial = mainCharacterSpatialComponent.getSpatial();
			
			CameraComponent cameraComponent = Components.getCameraComponent(e);
			
			Camera camera = cameraComponent.getCamera();
			camera.setPosition(spatial.getX(), camera.getY());
		}

	}

	@Override
	public void apply(Entity entity) {
		Camera camera = parameters.get("camera");
		Libgdx2dCamera libgdx2dCamera = parameters.get("libgdx2dCamera");
		
		entity.addComponent(new CameraComponent(libgdx2dCamera, camera));

		entity.addComponent(new ScriptComponent( //
				injector.getInstance(FollowEntityScript.class) //
		));
	}

}
