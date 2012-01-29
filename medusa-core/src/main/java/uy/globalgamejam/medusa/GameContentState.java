package uy.globalgamejam.medusa;

import uy.globalgamejam.medusa.LevelGeneratorTemplate.Element;
import uy.globalgamejam.medusa.replay.ReplayManager;

import com.badlogic.gdx.utils.Array;

public class GameContentState {
	
	Array<Element> elements;
	ReplayManager replayManager;
	
	public float worldScale;
	public float maxYCoord;
	
	LevelGeneratorTemplate levelGenerator;

	public void init() {
		elements = levelGenerator.generate(maxYCoord, worldScale);
		replayManager = new ReplayManager();
	}

}
