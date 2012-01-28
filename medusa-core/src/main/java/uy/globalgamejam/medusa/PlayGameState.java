package uy.globalgamejam.medusa;

import org.w3c.dom.Element;

import uy.globalgamejam.medusa.components.Controller;
import uy.globalgamejam.medusa.systems.LightsSystem;
import uy.globalgamejam.medusa.tags.Tags;
import uy.globalgamejam.medusa.templates.AttachedCameraTemplate;
import uy.globalgamejam.medusa.templates.ItemSpawnerTemplate;
import uy.globalgamejam.medusa.templates.MainCharacterTemplate;
import uy.globalgamejam.medusa.templates.ObstacleSpawnerTemplate2;
import uy.globalgamejam.medusa.templates.TouchControllerTemplate;

import box2dLight.RayHandler;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.transitions.sync.Synchronizer;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.events.Event;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.EventManagerImpl;
import com.gemserk.commons.artemis.events.reflection.Handles;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.systems.CameraUpdateSystem;
import com.gemserk.commons.artemis.systems.EventManagerWorldSystem;
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
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.box2d.Contacts;
import com.gemserk.commons.gdx.box2d.Contacts.Contact;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.CameraImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.graphics.Mesh2dBuilder;
import com.gemserk.commons.gdx.graphics.NeatTriangulator;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.graphics.Triangulator;
import com.gemserk.commons.gdx.time.TimeStepProviderGameStateImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.svg.SvgLayerProcessor;
import com.gemserk.commons.svg.inkscape.SvgPath;
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

	private RayHandler rayHandler;
	private OrthographicCamera lightingOrhtographicCamera;

	private com.badlogic.gdx.physics.box2d.World physicsWorld;
	private Camera worldRealCamera;

	@Override
	public void init() {
		Injector injector = this.injector.createChildInjector();

		synchronizer = new Synchronizer();

		float gameScale = Gdx.graphics.getHeight() / 800f;

		normalCamera = new Libgdx2dCameraTransformImpl(0f, 0f);
		normalCamera.zoom(1f);

		worldCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.15f);
		worldCamera.zoom(48f * gameScale);

		worldRealCamera = new CameraImpl(0, 0, 48f * gameScale, 0f);

		RenderLayers renderLayers = new RenderLayers();

		renderLayers.add("World", new RenderLayerSpriteBatchImpl(-500, 500, worldCamera));

		scene = new WorldWrapper(new World());

		physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0f, 0f), false);

		EntityFactory entityFactory = new EntityFactoryImpl(scene.getWorld());
		EventManager eventManager = new EventManagerImpl();

		// lighting stuff

		lightingOrhtographicCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		rayHandler = new RayHandler(physicsWorld);
		// rayHandler.setAmbientLight(0.9f);
		rayHandler.setShadows(false);
		rayHandler.setCulling(false);

		injector.bind("entityFactory", entityFactory);
		injector.bind("eventManager", eventManager);
		injector.bind("physicsWorld", physicsWorld);
		injector.bind("bodyBuilder", new BodyBuilder(physicsWorld));
		injector.bind("synchronizer", synchronizer);
		injector.bind("mesh2dBuilder", new Mesh2dBuilder());
		injector.bind("rayHandler", rayHandler);

		scene.addUpdateSystem(new ScriptSystem());
		scene.addUpdateSystem(new TagSystem());
		scene.addUpdateSystem(new ReflectionRegistratorEventSystem(eventManager));

		scene.addUpdateSystem(new PhysicsSystem(physicsWorld));
		// scene.addUpdateSystem(new LimitLinearVelocitySystem(physicsWorld));
		scene.addUpdateSystem(injector.getInstance(EventManagerWorldSystem.class));

		scene.addRenderSystem(injector.getInstance(LightsSystem.class));

		scene.addRenderSystem(new SpriteUpdateSystem(new TimeStepProviderGameStateImpl(this)));
		scene.addRenderSystem(new CameraUpdateSystem(new TimeStepProviderGameStateImpl(this)));

		scene.addRenderSystem(new Box2dRenderSystem(worldCamera, physicsWorld));
		 scene.addRenderSystem(new RenderableSystem(renderLayers));

		scene.init();

		Controller controller = new Controller();

		entityFactory.instantiate(injector.getInstance(MainCharacterTemplate.class), new ParametersWrapper() //
				.put("spatial", new SpatialImpl(0f, 0f, 1f, 1f, 0f)) //
				.put("controller", controller) //
				);

		entityFactory.instantiate(injector.getInstance(TouchControllerTemplate.class), new ParametersWrapper() //
				.put("controller", controller) //
				.put("camera", worldCamera) //
				);

		entityFactory.instantiate(injector.getInstance(ItemSpawnerTemplate.class));

		entityFactory.instantiate(injector.getInstance(AttachedCameraTemplate.class), new ParametersWrapper() //
				.put("libgdx2dCamera", worldCamera) //
				.put("camera", worldRealCamera) //
				);

		// entityFactory.instantiate(injector.getInstance(ObstacleSpawnerTemplate.class));
		entityFactory.instantiate(injector.getInstance(ObstacleSpawnerTemplate2.class));

		entityFactory.instantiate(new EntityTemplateImpl() {
			@Override
			public void apply(Entity entity) {
				entity.addComponent(new ScriptComponent(new ScriptJavaImpl() {

					public void update(World world, Entity e) {
						Entity mainCharacter = world.getTagManager().getEntity(Tags.MainCharacter);
						if (mainCharacter == null)
							return;
						PhysicsComponent physicsComponent = Components.getPhysicsComponent(mainCharacter);
						Contacts contacts = physicsComponent.getContact();

						score += 10;

						if (!contacts.isInContact())
							return;

						for (int i = 0; i < contacts.getContactCount(); i++) {
							Contact contact = contacts.getContact(i);

							if (contact.getOtherFixture().isSensor())
								continue;

							game.gameOverGameState.getParameters().put("score", score);

							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									game.setGameState(game.gameOverGameState, true);
								}
							});

							// new TransitionBuilder(game, game.gameOverScreen) //
							// .parameter("score", score) //
							// .start();

							return;
						}

					}

					@Handles(ids = Events.ItemGrabbed)
					public void scoreOnItemGrabbed(Event e) {
						score += 10000;
					}

				}));
			}
		});

		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		customDecimalFormat = new CustomDecimalFormat(10);
		score = 0L;

		SvgLayerProcessor boundsLayerProcessor = new SvgLayerProcessor("obstacles") {

			Triangulator triangulator;

			@Override
			protected void handlePathObject(SvgPath svgPath, Element element, Vector2[] vertices) {
				if (!element.hasAttribute("tileId"))
					return;
				triangulator = new NeatTriangulator();
				for (int i = 0; i < vertices.length; i++)
					triangulator.addPolyPoint(vertices[i].x, vertices[i].y);

			}
		};

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

		// lightingOrhtographicCamera.zoom = 1f / worldRealCamera.getZoom();
		// lightingOrhtographicCamera.position.set(worldRealCamera.getX(), worldRealCamera.getY() + lightingOrhtographicCamera.viewportHeight * 0.35f * lightingOrhtographicCamera.zoom, 0f);
		//
		// lightingOrhtographicCamera.update();

		// lightingOrhtographicCamera.view.set(worldCamera.getModelViewMatrix());
		// lightingOrhtographicCamera.projection.set(worldCamera.getProjectionMatrix());
		// lightingOrhtographicCamera.combined.set(worldCamera.getCombinedMatrix());
		//
		// lightingOrhtographicCamera.invProjectionView.set(worldCamera.getCombinedMatrix());
		// lightingOrhtographicCamera.invProjectionView.inv();
		//
		// lightingOrhtographicCamera.frustum.update(lightingOrhtographicCamera.invProjectionView);

		rayHandler.setCombinedMatrix(worldCamera.getCombinedMatrix());
		rayHandler.updateAndRender();

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
