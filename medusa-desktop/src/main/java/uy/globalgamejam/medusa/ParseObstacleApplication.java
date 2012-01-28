package uy.globalgamejam.medusa;


import uy.globalgamejam.medusa.test.FixedTrianglesShapeTestApplicationListener;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gemserk.commons.lwjgl.FocusableLwjglApplicationDelegate;

public class ParseObstacleApplication {

	public static void main(String[] argv) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Obstacle";
		config.width = 800;
		config.height = 600;
		config.fullscreen = false;
		config.useGL20 = false;
		config.useCPUSynch = true;
		config.forceExit = true;
		config.vSyncEnabled = true;

		new LwjglApplication(new FocusableLwjglApplicationDelegate(new FixedTrianglesShapeTestApplicationListener()), config);
//		new LwjglApplication(new FocusableLwjglApplicationDelegate(new ParseObstacleApplicationListener()), config);
	}

}
