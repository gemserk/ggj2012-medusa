package uy.globalgamejam.medusa;

import uy.globalgamejam.medusa.replay.ReplayManager;
import uy.globalgamejam.medusa.resources.GameResources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.gdx.ApplicationListenerGameStateBasedImpl;
import com.gemserk.commons.gdx.GameState;
import com.gemserk.commons.gdx.GameStateDelegateFixedTimestepImpl;
import com.gemserk.commons.gdx.GameStateDelegateWithInternalStateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.performance.AverageDeltaTimeLogger;
import com.gemserk.commons.performance.DeltaTimeLogger;
import com.gemserk.commons.performance.TimeLogger;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class Game extends ApplicationListenerGameStateBasedImpl {

	// public Screen playScreen;
	// public Screen gameOverScreen;

	ShapeRenderer shapeRenderer;
	private Libgdx2dCamera loggerCamera;

	public GameState playGameState;
	public GameState gameOverGameState;

	TimeLogger deltaTimeLogger;
	TimeLogger averageDeltaTimeLogger;
	TimeLogger accumulatorLogger;

	private GameState fixedTimestep(GameState gameState) {
		return new GameStateDelegateFixedTimestepImpl(gameState) {
			@Override
			public void update() {
				// float t = 0f;
				float frameTime = delta;

				// note: max frame time to avoid spiral of death
				if (frameTime > maxFrameTime)
					frameTime = maxFrameTime;

				accumulator += frameTime;

				float realUpdateCount = 0f;

				while (accumulator >= dt) {
					GlobalTime.setDelta(dt);

					gameState.setDelta(dt);
					gameState.update();

					realUpdateCount += 1f;

					accumulator -= dt;
				}

				accumulatorLogger.deltas.add(realUpdateCount);

				float alpha = accumulator / dt;
				GlobalTime.setAlpha(alpha);

				gameState.setAlpha(alpha);
			}
		};
	}

	private GameState internalState(GameState gameState) {
		return new GameStateDelegateWithInternalStateImpl(gameState);
	}

	@Override
	public void create() {
		Converters.register(Color.class, LibgdxConverters.color());
		Converters.register(Vector2.class, LibgdxConverters.vector2());

		ResourceManager<String> resourceManager = new ResourceManagerImpl<String>();

		GameResources.load(resourceManager);

		Injector injector = new InjectorImpl();

		injector.bind("resourceManager", resourceManager);
		injector.bind("game", this);

		playGameState = injector.getInstance(PlayGameState.class);
		gameOverGameState = injector.getInstance(GameOverGameState.class);

		playGameState = fixedTimestep(internalState(playGameState));
		gameOverGameState = fixedTimestep(internalState(gameOverGameState));

		GameContentState gameContentState = new GameContentState();

		LevelGeneratorTemplate levelGenerator = injector.getInstance(LevelGeneratorTemplate.class);
		
		float scale = 24f;
		float worldScale = scale * 1f;
		gameContentState.worldScale = worldScale;
		
		gameContentState.elements = levelGenerator.generate(Gdx.graphics.getHeight() / (worldScale * 2), worldScale);
		gameContentState.replayManager = new ReplayManager();

		playGameState.getParameters().put("gameContentState", gameContentState);

		setGameState(playGameState);

		// playScreen = new ScreenImpl(playGameState);
		// gameOverScreen = new ScreenImpl(gameOverGameState);
		//
		// setScreen(playScreen);

		// code for performance renderer

		shapeRenderer = new ShapeRenderer();
		loggerCamera = new Libgdx2dCameraTransformImpl(0, 0);

		deltaTimeLogger = new DeltaTimeLogger();
		deltaTimeLogger.enable();

		averageDeltaTimeLogger = new AverageDeltaTimeLogger();
		averageDeltaTimeLogger.enable();

		accumulatorLogger = new TimeLogger() {
			@Override
			protected void registerData() {

			}
		};
		accumulatorLogger.enable();
	}

	@Override
	public void render() {
		super.render();

		shapeRenderer.setProjectionMatrix(loggerCamera.getCombinedMatrix());

		// renderTimeLoggerGraph(Color.RED, deltaTimeLogger, 1f / 60f, 10f * 1000f, Gdx.graphics.getHeight() * 0.8f);
		// // renderTimeLoggerGraph(Color.ORANGE, averageDeltaTimeLogger, 1f / 60f, 10f * 1000f, Gdx.graphics.getHeight() * 0.5f);
		// renderTimeLoggerGraph(Color.GREEN, accumulatorLogger, 0f, 10f, Gdx.graphics.getHeight() * 0.3f);

		averageDeltaTimeLogger.update();
		deltaTimeLogger.update();
	}

	private void renderTimeLoggerGraph(Color color, TimeLogger timeLogger, float midPoint, float scale, float y) {
		FloatArray deltas = timeLogger.getDeltas();
		int width = Gdx.graphics.getWidth();
		int MAX_STEPS = 180;
		int steps = Math.min(MAX_STEPS, deltas.size);
		float lastY = 0;
		float stepX = ((float) width) / MAX_STEPS;
		shapeRenderer.setColor(color);
		shapeRenderer.begin(ShapeType.Line);
		int index = 0;

		for (int i = deltas.size - steps; i < deltas.size; i++) {
			float nextY = y - (midPoint - deltas.get(i)) * scale;
			float x1 = stepX * (index - 1);
			float x2 = stepX * index;
			shapeRenderer.line(x1, lastY, x2, nextY);
			lastY = nextY;
			index++;
		}
		shapeRenderer.end();
	}
}
