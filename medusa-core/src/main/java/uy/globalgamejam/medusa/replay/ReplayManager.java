package uy.globalgamejam.medusa.replay;

import java.util.ArrayList;

import uy.globalgamejam.medusa.components.Replay;

public class ReplayManager {
	
	ArrayList<Replay> replays = new ArrayList<Replay>();
	
	public ArrayList<Replay> getReplays() {
		return replays;
	}
	
	public void add(Replay replay) {
		replays.add(replay);
	}
	
}
