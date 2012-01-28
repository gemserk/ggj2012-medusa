package uy.globalgamejam.medusa;

import uy.globalgamejam.medusa.resources.GameResources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.animations.Animation;
import com.gemserk.animation4j.timeline.Builders;
import com.gemserk.animation4j.timeline.Builders.TimelineValueBuilder;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.resources.ResourceManager;

public class GameOverGameState extends GameStateImpl {

	Injector injector;
	Game game;

	SpriteBatch spriteBatch;

	InputDevicesMonitorImpl<String> inputMonitor;

	Container screen;
	Animation animation;

	Vector2 position = new Vector2();
	Sprite itemSprite;

	ResourceManager<String> resourceManager;

	@Override
	public void init() {

		spriteBatch = new SpriteBatch();
		BitmapFont font = new BitmapFont();

		Long score = getParameters().get("score");

		inputMonitor = new InputDevicesMonitorImpl<String>();

		new LibgdxInputMappingBuilder<String>(inputMonitor, Gdx.input) {
			{
				monitorPointerDown("back", 0);
				// monitorKey("back", Keys.ANY_KEY);
			}
		};

		screen = new Container("Screen", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		screen.add(GuiControls.label("Game Over") //
				.id("GameOverLabel") //
				.font(font) //
				.position(screen.getWidth() * 0.5f, screen.getHeight() * 0.6f) //
				.center(0.5f, 0.5f) //
				.build());

		screen.add(GuiControls.label("Your score was " + score + ", touch to play again") //
				.font(font) //
				.position(screen.getWidth() * 0.5f, screen.getHeight() * 0.5f) //
				.center(0.5f, 0.5f) //
				.build());

		itemSprite = resourceManager.getResourceValue(GameResources.Sprites.Item);

		TimelineValueBuilder<Sprite> valueBuilder = Builders.timelineValue(itemSprite, new SpritePositionConverter()); //
		
		valueBuilder.keyFrame(0f, new float[] { 0f, 0f });
		
		for (int i = 1; i < 10; i++) 
			valueBuilder.keyFrame((float)(i * 3), new float[] { MathUtils.random(0f, screen.getWidth()), MathUtils.random(0f, screen.getHeight()) });

		animation = Builders.animation(Builders.timeline().value(valueBuilder)).delay(0f).speed(1f).build();

		animation.start(0, true);

	}

	@Override
	public void update() {

		animation.update(getDelta());

		screen.update();

		inputMonitor.update();

		if (inputMonitor.getButton("back").isReleased())
			game.setGameState(game.playGameState, true);

	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		itemSprite.draw(spriteBatch);
		screen.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
	}

}
