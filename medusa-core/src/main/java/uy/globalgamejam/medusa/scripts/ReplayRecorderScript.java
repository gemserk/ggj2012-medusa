package uy.globalgamejam.medusa.scripts;

import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.Replay;
import uy.globalgamejam.medusa.components.Replay.ReplayEntry;
import uy.globalgamejam.medusa.replay.ReplayManager;

import com.artemis.Entity;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Spatial;

public class ReplayRecorderScript extends ScriptJavaImpl {

	private boolean recording;
	private int replayTime;

	private Replay replay;

	private float frameTime = 0f;

	ReplayManager replayManager;

	@Override
	public void init(final com.artemis.World world, final Entity e) {
		recording = true;
		replay = new Replay();
		replayTime = 0;
		
		replayManager.add(replay);
	}

//	@Override
//	public void dispose(World world, Entity e) {
//		replayManager.add(replay);
//	}

	public void update(com.artemis.World world, Entity e) {
		if (!recording)
			return;

		frameTime += GlobalTime.getDelta();

		if (frameTime < 0.1f)
			return;

		frameTime -= 0.1f;

		replayTime += 100;

		SpatialComponent spatialComponent = Components.getSpatialComponent(e);

		if (spatialComponent == null)
			return;

		Spatial spatial = spatialComponent.getSpatial();

		replay.add(new ReplayEntry(replayTime, spatial.getX(), spatial.getY()));
	}

}
