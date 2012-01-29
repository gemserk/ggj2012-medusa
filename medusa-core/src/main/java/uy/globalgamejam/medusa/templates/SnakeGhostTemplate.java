package uy.globalgamejam.medusa.templates;

import uy.globalgamejam.medusa.Collisions;
import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.Replay;
import uy.globalgamejam.medusa.components.Replay.ReplayEntry;
import uy.globalgamejam.medusa.components.TailComponent;
import uy.globalgamejam.medusa.scripts.EatEnemiesScript;
import uy.globalgamejam.medusa.scripts.MoveTailScript;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.animation4j.interpolator.FloatInterpolator;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.resources.ResourceManager;

public class SnakeGhostTemplate extends EntityTemplateImpl {

	BodyBuilder bodyBuilder;
	Injector injector;
	ResourceManager<String> resourceManager;

	public static class ReplayComponent extends Component {

		public Replay replay;

		public ReplayComponent(Replay replay) {
			this.replay = replay;
		}

	}

	public static class ReplayMovementScript extends ScriptJavaImpl {

		// uses the replay data to move the snake

		private float time;

		private ReplayEntry previousReplayEntry;
		private ReplayEntry currentReplayEntry;

		private int currentFrame;

		private boolean finished;

		@Override
		public void init(World world, Entity e) {
			time = 0;
			finished = false;
			currentFrame = 0;
			ReplayComponent replayComponent = e.getComponent(ReplayComponent.class);
			nextReplayFrame(replayComponent.replay);
		}

		public void update(com.artemis.World world, Entity e) {

			SpatialComponent spatialComponent = Components.getSpatialComponent(e);
			Spatial spatial = spatialComponent.getSpatial();
			
			ReplayComponent replayComponent = e.getComponent(ReplayComponent.class);

			if (finished) {
				// removes entity if replay is finished.
				e.delete();
				return;
			}

//			if (Math.abs(currentReplayEntry.x - previousReplayEntry.x) > 1f || Math.abs(currentReplayEntry.y - previousReplayEntry.y) > 1f) {
//				// in this case, do not interpolate and move to the next frame...
//				time = 0;
//				nextReplayFrame(replayComponent.replay);
//				return;
//			}

			float t = (float) time / (float) getTimeBetweenFrames();

			float x = FloatInterpolator.interpolate(previousReplayEntry.x, currentReplayEntry.x, t);
			float y = FloatInterpolator.interpolate(previousReplayEntry.y, currentReplayEntry.y, t);

			spatial.setPosition(x, y);

			time += GlobalTime.getDelta();

			while (time > getTimeBetweenFrames()) {
				time -= getTimeBetweenFrames();
				nextReplayFrame(replayComponent.replay);
			}
		}

		private float getTimeBetweenFrames() {
			return (float) (currentReplayEntry.time - previousReplayEntry.time) * 0.001f;
		}

		private void nextReplayFrame(Replay replay) {
			if (currentFrame + 1 >= replay.getEntriesCount()) {
				finished = true;
				return;
			}
			currentFrame++;
			previousReplayEntry = replay.getEntry(currentFrame - 1);
			currentReplayEntry = replay.getEntry(currentFrame);
		}


	}

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	public void setBodyBuilder(BodyBuilder bodyBuilder) {
		this.bodyBuilder = bodyBuilder;
	}

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");
		Replay replay = parameters.get("replay");

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.circleShape(0.5f) //
						.sensor() //
						.maskBits(Collisions.All) //
				) //
				.type(BodyType.DynamicBody) //
				.position(spatial.getX(), spatial.getY()) //
				.userData(entity) //
				.build();

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));

		entity.addComponent(new TailComponent());

		entity.addComponent(new ReplayComponent(replay));
		entity.addComponent(new ScriptComponent( //
				injector.getInstance(EatEnemiesScript.class), //
				injector.getInstance(MoveTailScript.class), //
				new ReplayMovementScript()));

	}

}
