package uy.globalgamejam.medusa;

import java.util.ArrayList;

import uy.globalgamejam.medusa.components.Components;
import uy.globalgamejam.medusa.components.Controller;
import uy.globalgamejam.medusa.components.Replay;
import uy.globalgamejam.medusa.components.TailComponent;
import uy.globalgamejam.medusa.resources.GameResources;
import uy.globalgamejam.medusa.scripts.RemoveOldEntitiesScript;
import uy.globalgamejam.medusa.tags.Groups;
import uy.globalgamejam.medusa.tags.Tags;
import uy.globalgamejam.medusa.templates.AttachedCameraTemplate;
import uy.globalgamejam.medusa.templates.WorldLimitsSpawnerTemplate;
import uy.globalgamejam.medusa.templates.KeyboardControllerTemplate;
import uy.globalgamejam.medusa.templates.LevelInstantiator;
import uy.globalgamejam.medusa.templates.ObstacleSpawnerTemplate2;
import uy.globalgamejam.medusa.templates.SnakeCharacterTemplate;
import uy.globalgamejam.medusa.templates.SnakeGhostTemplate;
import uy.globalgamejam.medusa.templates.TailPartTemplate;
import uy.globalgamejam.medusa.templates.WorldLimitsSpawnerTemplate;
import uy.globalgamejam.medusa.templates.SnakeCharacterTemplate.DangerScript;
import uy.globalgamejam.medusa.templates.BackgroundSpawnerTemplate;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.transitions.sync.Synchronizer;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.GroupComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.EventManagerImpl;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.systems.CameraUpdateSystem;
import com.gemserk.commons.artemis.systems.EventManagerWorldSystem;
import com.gemserk.commons.artemis.systems.GroupSystem;
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
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.GameState;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.box2d.Contacts;
import com.gemserk.commons.gdx.box2d.Contacts.Contact;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.CameraImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.graphics.Mesh2dBuilder;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.time.TimeStepProviderGameStateImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.text.CustomDecimalFormat;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.ParametersWrapper;

public class PlayGameState extends GameStateImpl {

	Injector injector;
	Game game;

	Libgdx2dCamera worldCamera;
	Libgdx2dCamera normalCamera;
	Libgdx2dCamera backgroundCamera;

	WorldWrapper scene;

	SpriteBatch spriteBatch;

	Synchronizer synchronizer;

	BitmapFont font;
	CustomDecimalFormat customDecimalFormat;

	long score;

	private com.badlogic.gdx.physics.box2d.World physicsWorld;
	private Camera worldRealCamera;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;
	private GameContentState gameContentState;

	@Override
	public void init() {
		final Injector injector = this.injector.createChildInjector();

		synchronizer = new Synchronizer();

		gameContentState = getParameters().get("gameContentState");

		normalCamera = new Libgdx2dCameraTransformImpl(0f, 0f);
		normalCamera.zoom(1f);

		worldCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.5f);
		float worldScale = gameContentState.worldScale;
		worldCamera.zoom(worldScale);

		backgroundCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f);
		backgroundCamera.zoom(1f);

		worldRealCamera = new CameraImpl(0, 0, worldScale, 0f);

		RenderLayers renderLayers = new RenderLayers();

		renderLayers.add("Background", new RenderLayerSpriteBatchImpl(-5000, -500, backgroundCamera));
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
		injector.bind("replayManager", gameContentState.replayManager);

		scene.addUpdateSystem(new ScriptSystem());
		scene.addUpdateSystem(new TagSystem());
		scene.addUpdateSystem(new GroupSystem());
		scene.addUpdateSystem(new ReflectionRegistratorEventSystem(eventManager));

		scene.addUpdateSystem(new PhysicsSystem(physicsWorld));
		scene.addUpdateSystem(new LimitLinearVelocitySystem(physicsWorld));
		scene.addUpdateSystem(injector.getInstance(EventManagerWorldSystem.class));

		scene.addRenderSystem(new SpriteUpdateSystem(new TimeStepProviderGameStateImpl(this)));
		scene.addRenderSystem(new CameraUpdateSystem(new TimeStepProviderGameStateImpl(this)));

		// scene.addRenderSystem(new Box2dRenderSystem(worldCamera, physicsWorld));
		scene.addRenderSystem(new RenderableSystem(renderLayers));

		scene.init();

		Controller controller = new Controller();

		float y = 0f + MathUtils.random(-gameContentState.maxYCoord * 0.75f, gameContentState.maxYCoord * 0.75f);
		System.out.println("maincharacter.y = " + y);
		Entity mainCharacter = entityFactory.instantiate(injector.getInstance(SnakeCharacterTemplate.class), new ParametersWrapper() //
				.put("spatial", new SpatialImpl(0f, y, 1f, 1f, 0f)) //
				.put("controller", controller) //
				);

		TailComponent tailComponent = Components.getTailComponent(mainCharacter);

		int tailLength = 50;

		for (int i = 0; i < tailLength; i++) {
			Entity tailPart = entityFactory.instantiate(injector.getInstance(TailPartTemplate.class), new ParametersWrapper() //
					.put("spatial", new SpatialImpl(-i - 100, y, 1f, 1f, 0f)) //
					.put("owner", mainCharacter) //
					);
			tailComponent.parts.add(tailPart);
		}

		ArrayList<Replay> replays = gameContentState.replayManager.getReplays();

		for (Replay replay : replays) {
			y = replay.getEntry(0).y;
			y = -100;
			Entity ghostSnake = entityFactory.instantiate(injector.getInstance(SnakeGhostTemplate.class), new ParametersWrapper() //
					.put("spatial", new SpatialImpl(-100f, y, 1f, 1f, 0f)) //
					.put("replay", replay) //
					.put("offset", 1.5f) //
					);

			tailComponent = Components.getTailComponent(ghostSnake);

			for (int i = 0; i < tailLength; i++) {
				Entity tailPart = entityFactory.instantiate(injector.getInstance(TailPartTemplate.class), new ParametersWrapper() //
						.put("spatial", new SpatialImpl(-i - 10, y, 1f, 1f, 0f)) //
						.put("owner", ghostSnake) //
						.put("deadSnake", true));
				tailComponent.parts.add(tailPart);
			}

		}

		// entityFactory.instantiate(injector.getInstance(TouchControllerTemplate.class), new ParametersWrapper() //
		// .put("controller", controller) //
		// .put("camera", worldCamera) //
		// );

		entityFactory.instantiate(injector.getInstance(KeyboardControllerTemplate.class), new ParametersWrapper() //
				.put("controller", controller) //
				);

		entityFactory.instantiate(injector.getInstance(LevelInstantiator.class), //
				new ParametersWrapper().put("elements", gameContentState.elements));

		entityFactory.instantiate(injector.getInstance(AttachedCameraTemplate.class), new ParametersWrapper() //
				.put("libgdx2dCamera", worldCamera) //
				.put("camera", worldRealCamera) //
				);

		entityFactory.instantiate(injector.getInstance(ObstacleSpawnerTemplate2.class));

		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		customDecimalFormat = new CustomDecimalFormat(10);
		score = 0L;

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKey("restartLevel", Keys.R);
				monitorKey("newLevel", Keys.SPACE);
			}
		};

		entityFactory.instantiate(new EntityTemplateImpl() {
			@Override
			public void apply(Entity entity) {
				entity.addComponent(new ScriptComponent( //
						new RemoveOldEntitiesScript(Groups.Enemies, Tags.MainCharacter), //
						new RemoveOldEntitiesScript(Groups.Obstacles, Tags.MainCharacter) //
				));
			}
		});

		entityFactory.instantiate(injector.getInstance(WorldLimitsSpawnerTemplate.class), new ParametersWrapper() //
				.put("spriteId", GameResources.Sprites.BottomBorder) //
				.put("y", -gameContentState.maxYCoord));

		entityFactory.instantiate(injector.getInstance(WorldLimitsSpawnerTemplate.class), new ParametersWrapper() //
				.put("spriteId", GameResources.Sprites.TopBorder) //
				.put("y", gameContentState.maxYCoord));

		entityFactory.instantiate(new EntityTemplateImpl() {
			@Override
			public void apply(Entity entity) {
				entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {
					@Override
					public void update(World world, Entity e) {
						Entity mainCharacter = world.getTagManager().getEntity(Tags.MainCharacter);
						Contacts contacts = Components.getPhysicsComponent(mainCharacter).getContact();
						if (!contacts.isInContact())
							return;

						for (int j = 0; j < contacts.getContactCount(); j++) {
							Contact contact = contacts.getContact(j);

							Entity entity = (Entity) contact.getOtherFixture().getBody().getUserData();
							if (entity == null)
								continue;

							GroupComponent groupComponent = Components.getGroupComponent(entity);

							if (groupComponent == null)
								continue;
							
							TailComponent tailComponent = Components.getTailComponent(mainCharacter);

//							if (!Groups.Obstacles.equals(groupComponent.group) && !Groups.WorldBounds.equals(groupComponent.group))
//								continue;
								
							if(Groups.Enemies.equals(groupComponent.group) && tailComponent.parts.size()>DangerScript.DANGER_TAIL_NUMBER)	
								continue;
							
							
							if(Components.getSpatialComponent(mainCharacter).getPosition().x < 2f)
								gameContentState.init();

							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									GameState gameState = game.getGameState();
									gameState.dispose();
									gameState.getParameters().put("gameContentState", gameContentState);
									gameState.init();
								}
							});

							return;
						}
					}
				}));
			}

		});

		entityFactory.instantiate(new EntityTemplateImpl() {
			@Override
			public void apply(Entity entity) {
				entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {
					@Override
					public void update(World world, Entity e) {
						Entity mainCharacter = world.getTagManager().getEntity(Tags.MainCharacter);

						score = (long) Components.getSpatialComponent(mainCharacter).getPosition().x;

					}
				}));
			}
		});

		entityFactory.instantiate(new EntityTemplateImpl() {
			@Override
			public void apply(Entity entity) {
				entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {
					@Override
					public void update(World world, Entity e) {
						Entity mainCharacter = world.getTagManager().getEntity(Tags.MainCharacter);
						Spatial spatial = Components.getSpatialComponent(mainCharacter).getSpatial();
						backgroundCamera.move(spatial.getX() * 5f, 0f);
					}
				}));
			}
		});

		entityFactory.instantiate(injector.getInstance(BackgroundSpawnerTemplate.class), new ParametersWrapper());

	}

	@Override
	public void update() {
		synchronizer.synchronize(getDelta());
		scene.update(getDeltaInMs());

		inputDevicesMonitor.update();
		if (inputDevicesMonitor.getButton("restartLevel").isReleased()) {
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					GameState gameState = game.getGameState();
					gameState.dispose();
					gameState.getParameters().put("gameContentState", gameContentState);
					gameState.init();
				}
			});
		}

		if (inputDevicesMonitor.getButton("newLevel").isReleased()) {
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					GameState gameState = game.getGameState();
					gameState.dispose();
					gameContentState.init();
					gameState.getParameters().put("gameContentState", gameContentState);
					gameState.init();
				}
			});
		}

	}

	@Override
	public void render() {
		// Gdx.gl.glClearColor(1f, 0.5f, 0.0f, 0f);
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 0f);
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
