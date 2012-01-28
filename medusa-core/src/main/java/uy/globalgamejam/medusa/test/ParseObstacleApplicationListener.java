package uy.globalgamejam.medusa.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;

import uy.globalgamejam.medusa.svg.FixturesSvgLayerProcessor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.gemserk.commons.gdx.ApplicationListenerGameStateBasedImpl;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.box2d.FixtureDefBuilder;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.graphics.ImmediateModeRendererUtils;
import com.gemserk.commons.gdx.graphics.Triangulator;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.svg.SvgLayerProcessor;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;

public class ParseObstacleApplicationListener extends ApplicationListenerGameStateBasedImpl {

	public static class ParseObstacleGameState extends GameStateImpl {

		public static <T> T random(Set<T> set) {
			int size = set.size();

			if (size <= 0)
				throw new IllegalStateException("cant get random of empty set");

			int random = MathUtils.random(0, size - 1);

			int i = 0;
			for (T t : set) {
				if (i == random)
					return t;
				i++;
			}

			throw new IllegalStateException("should never happen if the set has elements");
		}

		Libgdx2dCamera worldCamera;
		World box2dWorld;

		Box2DDebugRenderer box2dDebugRenderer;
		BodyBuilder bodyBuilder;

		Map<String, Triangulator> obstacles;

		Vector2[] triangleVertices = new Vector2[3];

		{
			for (int i = 0; i < triangleVertices.length; i++)
				triangleVertices[i] = new Vector2();
		}

		@Override
		public void init() {
			worldCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
			worldCamera.zoom(10f);

			box2dWorld = new World(new Vector2(), false);
			box2dDebugRenderer = new Box2DDebugRenderer();

			bodyBuilder = new BodyBuilder(box2dWorld);

			Document obstaclesDocument = LibgdxResourceBuilder.xmlDocument("data/fixtures/obstacles.svg").build();

			obstacles = new HashMap<String, Triangulator>();

			SvgLayerProcessor obstaclesLayerProcessor = new FixturesSvgLayerProcessor("obstacles", obstacles);
			obstaclesLayerProcessor.process(obstaclesDocument);

			Set<String> keySet = obstacles.keySet();

//			for (String key : keySet) {
//
//				Triangulator triangulator = obstacles.get(key);
//
//				System.out.println("new float[] {");
//
//				for (int j = 0; j < triangulator.getTriangleCount(); j++) {
//
//					// System.out.println("new Vector2[] {");
//
//					for (int p = 0; p < 3; p++) {
//						float[] pt = triangulator.getTrianglePoint(j, p);
//						// triangleVertices[p].set(pt[0], pt[1]);
//						// System.out.println("new Vector2(" + pt[0] + ", " + pt[1] + ")");
//						System.out.print("" + pt[0] + "f, " + pt[1] + "f,");
//					}
//
//					// System.out.println("}");
//
//				}
//
//				System.out.println("}");
//
//			}

			for (int i = 0; i < 5; i++) {
				String obstacleId = random(keySet);
				Triangulator triangulator = obstacles.get(obstacleId);

				FixtureDef[] fixtureDefs = new FixtureDef[triangulator.getTriangleCount()];
				FixtureDefBuilder fixtureDefBuilder = new FixtureDefBuilder();

				for (int j = 0; j < triangulator.getTriangleCount(); j++) {

					for (int p = 0; p < 3; p++) {
						float[] pt = triangulator.getTrianglePoint(j, p);
						triangleVertices[p].set(pt[0], pt[1]);
					}

					fixtureDefs[j] = fixtureDefBuilder //
							.polygonShape(triangleVertices) //
							.restitution(0f) //
							.build();

				}

				Body body = bodyBuilder.mass(1f) //
						.fixtures(fixtureDefs) //
						.position(0, 0) //
						.type(BodyType.StaticBody) //
						.angle(0) //
						.build();

				float x = MathUtils.random(-25f, 25f);
				float y = MathUtils.random(-25f, 25f);

				body.setTransform(x, y, 0f);
			}

		}

		@Override
		public void render() {
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			ImmediateModeRendererUtils.getProjectionMatrix().set(worldCamera.getCombinedMatrix());
			ImmediateModeRendererUtils.drawLine(0, -1000, 0, 1000, Color.GREEN);
			ImmediateModeRendererUtils.drawLine(-1000, 0, 1000, 0, Color.GREEN);

			box2dDebugRenderer.render(box2dWorld, worldCamera.getCombinedMatrix());
		}

		@Override
		public void dispose() {
			box2dWorld.dispose();
			box2dDebugRenderer.dispose();
		}
	}

	private InputDevicesMonitorImpl<String> inputDevicesMonitorImpl;

	@Override
	public void create() {
		setGameState(new ParseObstacleGameState());

		inputDevicesMonitorImpl = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitorImpl, Gdx.input) {
			{
				monitorKeys("restart", Input.Keys.R, Input.Keys.SEARCH);
			}
		};

	}

	@Override
	public void render() {
		super.render();
		inputDevicesMonitorImpl.update();

		if (inputDevicesMonitorImpl.getButton("restart").isReleased()) {
			setGameState(new ParseObstacleGameState());
		}

	}

}