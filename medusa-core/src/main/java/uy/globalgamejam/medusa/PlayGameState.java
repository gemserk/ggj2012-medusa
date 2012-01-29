package uy.globalgamejam.medusa;

import uy.globalgamejam.medusa.LevelGeneratorTemplate.Element;
import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.Controller;
import uy.globalgamejam.medusa.components.Replay;
import uy.globalgamejam.medusa.components.Replay.ReplayEntry;
import uy.globalgamejam.medusa.components.TailComponent;
import uy.globalgamejam.medusa.replay.ReplayManager;
import uy.globalgamejam.medusa.templates.AttachedCameraTemplate;
import uy.globalgamejam.medusa.templates.KeyboardControllerTemplate;
import uy.globalgamejam.medusa.templates.LevelInstantiator;
import uy.globalgamejam.medusa.templates.ObstacleSpawnerTemplate2;
import uy.globalgamejam.medusa.templates.SnakeCharacterTemplate;
import uy.globalgamejam.medusa.templates.SnakeGhostTemplate;
import uy.globalgamejam.medusa.templates.TailPartTemplate;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gemserk.animation4j.transitions.sync.Synchronizer;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.EventManagerImpl;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.systems.CameraUpdateSystem;
import com.gemserk.commons.artemis.systems.EventManagerWorldSystem;
import com.gemserk.commons.artemis.systems.LimitLinearVelocitySystem;
import com.gemserk.commons.artemis.systems.PhysicsSystem;
import com.gemserk.commons.artemis.systems.ReflectionRegistratorEventSystem;
import com.gemserk.commons.artemis.systems.RenderLayerSpriteBatchImpl;
import com.gemserk.commons.artemis.systems.RenderableSystem;
import com.gemserk.commons.artemis.systems.ScriptSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.artemis.systems.TagSystem;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.CameraImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.graphics.Mesh2dBuilder;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.time.TimeStepProviderGameStateImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.text.CustomDecimalFormat;
import com.gemserk.componentsengine.utils.ParametersWrapper;

public class PlayGameState extends GameStateImpl {

	Injector injector;
	Game game;

	Libgdx2dCamera worldCamera;
	Libgdx2dCamera normalCamera;

	WorldWrapper scene;

	SpriteBatch spriteBatch;

	Synchronizer synchronizer;

	BitmapFont font;
	CustomDecimalFormat customDecimalFormat;

	long score;

	private com.badlogic.gdx.physics.box2d.World physicsWorld;
	private Camera worldRealCamera;

	@Override
	public void init() {
		final Injector injector = this.injector.createChildInjector();

		synchronizer = new Synchronizer();

		float gameScale = Gdx.graphics.getHeight() / 480f;

		normalCamera = new Libgdx2dCameraTransformImpl(0f, 0f);
		normalCamera.zoom(1f);

		float scale = 24f;

		worldCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.15f, Gdx.graphics.getHeight() * 0.5f);
		worldCamera.zoom(scale * gameScale);

		worldRealCamera = new CameraImpl(0, 0, scale * gameScale, 0f);

		RenderLayers renderLayers = new RenderLayers();

		renderLayers.add("World", new RenderLayerSpriteBatchImpl(-500, 500, worldCamera));

		scene = new WorldWrapper(new World());

		physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0f, 0f), false);

		final EntityFactory entityFactory = new EntityFactoryImpl(scene.getWorld());
		EventManager eventManager = new EventManagerImpl();

		// lighting stuff

		injector.bind("entityFactory", entityFactory);
		injector.bind("eventManager", eventManager);
		injector.bind("physicsWorld", physicsWorld);
		injector.bind("bodyBuilder", new BodyBuilder(physicsWorld));
		injector.bind("synchronizer", synchronizer);
		injector.bind("mesh2dBuilder", new Mesh2dBuilder());
		injector.bind("replayManager", new ReplayManager());

		scene.addUpdateSystem(new ScriptSystem());
		scene.addUpdateSystem(new TagSystem());
		scene.addUpdateSystem(new ReflectionRegistratorEventSystem(eventManager));

		scene.addUpdateSystem(new PhysicsSystem(physicsWorld));
		scene.addUpdateSystem(new LimitLinearVelocitySystem(physicsWorld));
		scene.addUpdateSystem(injector.getInstance(EventManagerWorldSystem.class));

		scene.addRenderSystem(new SpriteUpdateSystem(new TimeStepProviderGameStateImpl(this)));
		scene.addRenderSystem(new CameraUpdateSystem(new TimeStepProviderGameStateImpl(this)));

		scene.addRenderSystem(new Box2dRenderSystem(worldCamera, physicsWorld));
		scene.addRenderSystem(new RenderableSystem(renderLayers));

		scene.init();

		Controller controller = new Controller();

		Entity mainCharacter = entityFactory.instantiate(injector.getInstance(SnakeCharacterTemplate.class), new ParametersWrapper() //
				.put("spatial", new SpatialImpl(0f, 0f, 1f, 1f, 0f)) //
				.put("controller", controller) //
				);

		TailComponent tailComponent = Components.getTailComponent(mainCharacter);

		for (int i = 0; i < 25; i++) {
			Entity tailPart = entityFactory.instantiate(injector.getInstance(TailPartTemplate.class), new ParametersWrapper() //
					.put("spatial", new SpatialImpl(-i, 0f, 1f, 1f, 0f)) //
					.put("owner", mainCharacter) //
					);
			tailComponent.parts.add(tailPart);
		}
		
		Replay replay = new Replay();
		
		replay.duration = 15000;
		replay.add(new ReplayEntry(0, 0f, 2f));
		replay.add(new ReplayEntry(2500, 15f, 2f));
		replay.add(new ReplayEntry(5000, 30f, -7f));
		replay.add(new ReplayEntry(7500, 60f, -7f));

		entityFactory.instantiate(injector.getInstance(SnakeGhostTemplate.class), new ParametersWrapper() //
				.put("spatial", new SpatialImpl(0f, 0f, 1f, 1f, 0f)) //
				.put("replay", replay) //
				);

		// entityFactory.instantiate(injector.getInstance(TouchControllerTemplate.class), new ParametersWrapper() //
		// .put("controller", controller) //
		// .put("camera", worldCamera) //
		// );

		entityFactory.instantiate(injector.getInstance(KeyboardControllerTemplate.class), new ParametersWrapper() //
				.put("controller", controller) //
				);

		// entityFactory.instantiate(injector.getInstance(ItemSpawnerTemplate.class));

		LevelGeneratorTemplate levelGenerator = injector.getInstance(LevelGeneratorTemplate.class);
		Array<Element> elements = levelGenerator.generate();
		System.out.println("Elements: " + elements.size);

		entityFactory.instantiate(injector.getInstance(LevelInstantiator.class), new ParametersWrapper().put("elements", elements));

		entityFactory.instantiate(injector.getInstance(AttachedCameraTemplate.class), new ParametersWrapper() //
				.put("libgdx2dCamera", worldCamera) //
				.put("camera", worldRealCamera) //
				);

		// entityFactory.instantiate(injector.getInstance(ObstacleSpawnerTemplate.class));
		entityFactory.instantiate(injector.getInstance(ObstacleSpawnerTemplate2.class));

		// entityFactory.instantiate(new EntityTemplateImpl() {
		// @Override
		// public void apply(Entity entity) {
		// entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {
		//
		// public void update(World world, Entity e) {
		// Entity mainCharacter = world.getTagManager().getEntity(Tags.MainCharacter);
		// if (mainCharacter == null)
		// return;
		// PhysicsComponent physicsComponent = Components.getPhysicsComponent(mainCharacter);
		// Contacts contacts = physicsComponent.getContact();
		//
		// score += 10;
		//
		// if (!contacts.isInContact())
		// return;
		//
		// for (int i = 0; i < contacts.getContactCount(); i++) {
		// Contact contact = contacts.getContact(i);
		//
		// if (contact.getOtherFixture().isSensor())
		// continue;
		//
		// game.gameOverGameState.getParameters().put("score", score);
		//
		// Gdx.app.postRunnable(new Runnable() {
		// @Override
		// public void run() {
		// game.setGameState(game.gameOverGameState, true);
		// }
		// });
		//
		// // new TransitionBuilder(game, game.gameOverScreen) //
		// // .parameter("score", score) //
		// // .start();
		//
		// return;
		// }
		//
		// }
		//
		// @Handles(ids = Events.ItemGrabbed)
		// public void scoreOnItemGrabbed(Event e) {
		// score += 10000;
		// }
		//
		// }));
		// }
		// });

		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		customDecimalFormat = new CustomDecimalFormat(10);
		score = 0L;

		// entityFactory.instantiate(new EntityTemplateImpl() {
		// @Override
		// public void apply(Entity entity) {
		// entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {
		// final Vector2 position = new Vector2();
		//
		// @Override
		// public void update(World world, Entity e) {
		// if (!Gdx.input.justTouched())
		// return;
		//
		// int x = Gdx.input.getX();
		// int y = Gdx.graphics.getHeight() - Gdx.input.getY();
		//
		// position.set(x, y);
		// worldCamera.unproject(position);
		//
		// entityFactory.instantiate(injector.getInstance(TopDownEnemyTemplate.class), //
		// new ParametersWrapper()
		// .put("spatial", new SpatialImpl(position.x, position.y)) //
		// .put("movingDown", MathUtils.randomBoolean()) //
		// );
		// }
		// }));
		// }
		// });

	}

	@Override
	public void update() {
		synchronizer.synchronize(getDelta());
		scene.update(getDeltaInMs());
		// rayHandler.updateRays();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0f, 0f, 0.5f, 0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		scene.render();

		normalCamera.apply();

		spriteBatch.begin();
		SpriteBatchUtils.drawMultilineText(spriteBatch, font, customDecimalFormat.format((long) score), 20f, Gdx.graphics.getHeight() * 0.95f, 0f, 0.5f);
		spriteBatch.end();
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void dispose() {
		super.dispose();
		spriteBatch.dispose();
		scene.dispose();
	}
}
