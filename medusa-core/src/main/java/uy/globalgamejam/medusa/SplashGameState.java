package uy.globalgamejam.medusa;

import java.util.ArrayList;

import uy.globalgamejam.medusa.resources.GameResources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.TimeTransition;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.resources.CustomResourceManager;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.resources.progress.TaskQueue;
import com.gemserk.resources.progress.tasks.SimulateLoadingTimeRunnable;

public class SplashGameState extends com.gemserk.commons.gdx.gamestates.LoadingGameState {

	Game game;
	CustomResourceManager<String> resourceManager;
	Injector injector;
	
	Libgdx2dCamera camera;

	ResourceManager<String> internalResourceManager;

	private SpriteBatch spriteBatch;
	private BitmapFont font;

	private Container screen;
	private TimeTransition timeTransition;

	public void setResourceManager(CustomResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public void init() {
		super.init();

		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(1f, 1f, 1f, 1f);
		
		camera = new Libgdx2dCameraTransformImpl();

		internalResourceManager = new ResourceManagerImpl<String>();
		
		screen = new Container("Screen", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		float scaleY = (float) screen.getHeight() / 480f;
		float scaleX = (float) screen.getWidth() / 800f;
		
		Texture.setEnforcePotImages(false);
		
		new LibgdxResourceBuilder(internalResourceManager) {
			{
				texture("SplashScreen", "data/images/splashscreen.png");
				sprite("SplashScreenSprite", "SplashScreen");
			}
		};

		{
			Sprite sprite = internalResourceManager.getResourceValue("SplashScreenSprite");

			screen.add(GuiControls.imageButton(sprite) //
					.position(0f * scaleX, 0f * scaleY) //
					.center(0f, 0f) //
					.size(1024f, 480f).build());
		}

		TaskQueue taskQueue = super.getTaskQueue();

		taskQueue.add(new SimulateLoadingTimeRunnable(0));

		ArrayList<String> registeredResources = resourceManager.getRegisteredResources();
		for (int i = 0; i < registeredResources.size(); i++) {
			final String resourceId = registeredResources.get(i);
			taskQueue.add(new Runnable() {
				@Override
				public void run() {
					Gdx.app.log("medusa", "Loading resource: " + resourceId);
					Resource resource = resourceManager.get(resourceId);
					
					if (resource.isLoaded()) {
						Gdx.app.log("medusa", "Skipping resource, already loaded");
						return;
					}
					
					resource.load();
				}
			}, "Loading - " + resourceId);
		}

		taskQueue.add(new Runnable() {
			@Override
			public void run() {
				Resource<Music> musicResource = resourceManager.get(GameResources.MusicTracks.Game);
				
				Music music = musicResource.get();
				
				music.setLooping(true);		
				music.play();
				
				timeTransition = new TimeTransition();
				timeTransition.start(4f);
			}
		});

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);
	}

	private void mainMenu() {
		Gdx.app.postRunnable(new Runnable() {
			
			@Override
			public void run() {
				game.setGameState(game.playGameState, true);
			}
		});
	}

	@Override
	public void update() {
		if (timeTransition != null) {
			timeTransition.update(getDelta());

			if (timeTransition.isFinished())
				mainMenu();

			if (Gdx.input.justTouched())
				mainMenu();
		}

	}

	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		float percentage = getTaskQueue().getProgress().getPercentage();

		spriteBatch.setProjectionMatrix(camera.getCombinedMatrix());
		
		spriteBatch.begin();
		screen.draw(spriteBatch);

		String currentTaskName = "Loading...";
		if (percentage >= 100f)
			currentTaskName = "Loading complete";

		SpriteBatchUtils.drawMultilineText(spriteBatch, font, currentTaskName, //
				screen.getWidth() * 0.975f, screen.getHeight() * 0.15f, 1f, 0.5f);
		SpriteBatchUtils.drawMultilineText(spriteBatch, font, "" + (int) (percentage) + "%", //
				screen.getWidth() * 0.975f, screen.getHeight() * 0.10f, 1f, 0.5f);
		if (getTaskQueue().isDone())
			SpriteBatchUtils.drawMultilineText(spriteBatch, font, "Touch to continue...", //
					screen.getWidth() * 0.5f, screen.getHeight() * 0.025f, 0.5f, 0.5f);

		spriteBatch.end();

		super.render();
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		spriteBatch = null;
		internalResourceManager.unloadAll();
	}

}
