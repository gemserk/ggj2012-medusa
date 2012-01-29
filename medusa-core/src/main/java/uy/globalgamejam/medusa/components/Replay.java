package uy.globalgamejam.medusa.components;

import java.util.ArrayList;

public class Replay {

	public static class ReplayEntry {

		/**
		 * Used probably for interpolations.
		 */
		public int time;

		public float x, y;

		public ReplayEntry(int time, float x, float y) {
			this.time = time;
			this.x = x;
			this.y = y;
		}

	}

	private ArrayList<ReplayEntry> replayEntries = new ArrayList<ReplayEntry>();
	public int duration;
	public boolean main;

	public ReplayEntry getEntry(int i) {
		return replayEntries.get(i);
	}
	
	public int getEntriesCount() {
		return replayEntries.size();
	}
	
	public void add(ReplayEntry replayEntry) {
		duration = Math.max(replayEntry.time, duration);
		replayEntries.add(replayEntry);
	}
	

}