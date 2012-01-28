package uy.globalgamejam.medusa.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class FixedTrianglesShapeTestApplicationListener extends Game {

	public static class FixedTrianglesShapeTestScreen implements Screen {

		World box2dWorld;

		Box2DDebugRenderer box2dDebugRenderer;
		BodyBuilder bodyBuilder;

		public boolean inited = false;

		Vector2[] triangleVertices = new Vector2[3];

		{
			for (int i = 0; i < triangleVertices.length; i++)
				triangleVertices[i] = new Vector2();
		}

		// non triangulated shapes ->

		float[] shape0 = new float[] { -1.7893324f, -0.17858887f, -0.62075806f, 3.9835205f, 0.5158024f, 3.9034424f, 2.260664f, 1.1341553f, 0.4357643f, -1.283081f, 0.21165466f, -2.4196777f, -0.21004486f, -3.553955f, -0.8037472f, -1.5858154f, };
		float[] shape1 = new float[] { -2.1478376f, 0.002822876f, -2.1251974f, 0.115982056f, -1.5592327f, 2.0855865f, 1.8365536f, 2.923233f, 3.1495914f, 0.7046051f, 2.0403023f, -2.3062592f, -0.99326706f, -2.8722992f, -0.20091629f, -0.65367126f, };
		float[] shape2 = new float[] { -1.4770966f, -0.8211975f, -1.5428867f, 5.030487f, 0.74209213f, 3.993744f, 2.2499485f, 1.8977966f, 0.9065895f, -1.1490784f, 0.32294273f, -3.2503967f, -0.4276123f, -3.6699524f, -0.7739811f, -2.0314026f, };
		float[] shape3 = new float[] { -1.619545f, 0.44400024f, -1.2659588f, 2.8855286f, 0.14226341f, 1.2660217f, 2.4304504f, 1.7566223f, 0.60555077f, -0.66049194f, 0.38144112f, -1.7970886f, -0.0402565f, -2.931366f, -0.6339588f, -0.9632263f, };
		float[] shape4 = new float[] { -2.1486454f, -0.09574509f, -2.0975246f, -0.005168915f, -0.08506584f, 5.0219307f, 2.04529f, 3.2721748f, 2.582529f, -0.98112106f, 1.7438717f, -3.4745293f, 0.8097973f, -1.741375f, -1.5357761f, -1.4338799f, -1.3144646f, -0.5622978f, };
		float[] shape5 = new float[] { -1.7980576f, -0.18188477f, -1.410965f, 1.4425049f, -0.70168686f, 3.2322998f, 0.26040077f, 4.134033f, 1.5065746f, 1.3126221f, 2.0994396f, -1.0297852f, 0.7440052f, -2.9888916f, 0.19403076f, -4.5339355f, -0.89372826f, -1.3869629f, };
		float[] shape6 = new float[] { -2.1395836f, 2.1064281f, -1.3270836f, 0.85642815f, -0.014583588f, 0.41892815f, -1.0458336f, -0.64357185f, -1.9208336f, -0.612566f, -2.6708336f, -1.425066f, -1.3895836f, -1.800066f, -0.4208336f, -2.768816f, 0.3291664f, -1.425066f, 1.3291664f, -1.831316f, 2.2979164f, -0.112565994f, 2.8916664f, -0.237566f, 1.9541664f, 1.231184f, 2.3916664f, 2.668684f, -0.2645836f, 3.574934f, };

		// already triangulated shapes ->

		// float[] shape0 = new float[] { -0.8037472f, -1.5858154f, -0.21004486f, -3.553955f, 0.21165466f, -2.4196777f, 0.4357643f, -1.283081f, 2.260664f, 1.1341553f, 0.5158024f, 3.9034424f, 0.5158024f, 3.9034424f, -0.62075806f, 3.9835205f, -1.7893324f, -0.17858887f, -1.7893324f, -0.17858887f, -0.8037472f, -1.5858154f, 0.21165466f, -2.4196777f, 0.21165466f, -2.4196777f, 0.4357643f, -1.283081f, 0.5158024f, 3.9034424f, 0.5158024f, 3.9034424f, -1.7893324f, -0.17858887f, 0.21165466f, -2.4196777f, };
		// float[] shape1 = new float[] { -0.89372826f, -1.3869629f, 0.19403076f, -4.5339355f, 0.7440052f, -2.9888916f, 0.7440052f, -2.9888916f, 2.0994396f, -1.0297852f, 1.5065746f, 1.3126221f, 1.5065746f, 1.3126221f, 0.26040077f, 4.134033f, -0.70168686f, 3.2322998f, -0.70168686f, 3.2322998f, -1.410965f, 1.4425049f, -1.7980576f, -0.18188477f, -1.7980576f, -0.18188477f, -0.89372826f, -1.3869629f, 0.7440052f, -2.9888916f, 0.7440052f, -2.9888916f, 1.5065746f, 1.3126221f, -0.70168686f, 3.2322998f, -0.70168686f, 3.2322998f, -1.7980576f, -0.18188477f, 0.7440052f, -2.9888916f, };
		// float[] shape2 = new float[] { -0.6339588f, -0.9632263f, -0.0402565f, -2.931366f, 0.38144112f, -1.7970886f, 0.60555077f, -0.66049194f, 2.4304504f, 1.7566223f, 0.14226341f, 1.2660217f, 0.14226341f, 1.2660217f, -1.2659588f, 2.8855286f, -1.619545f, 0.44400024f, -1.619545f, 0.44400024f, -0.6339588f, -0.9632263f, 0.38144112f, -1.7970886f, 0.38144112f, -1.7970886f, 0.60555077f, -0.66049194f, 0.14226341f, 1.2660217f, 0.14226341f, 1.2660217f, -1.619545f, 0.44400024f, 0.38144112f, -1.7970886f, };
		// float[] shape3 = new float[] { -1.3144646f, -0.5622978f, -1.5357761f, -1.4338799f, 0.8097973f, -1.741375f, 0.8097973f, -1.741375f, 1.7438717f, -3.4745293f, 2.582529f, -0.98112106f, 2.582529f, -0.98112106f, 2.04529f, 3.2721748f, -0.08506584f, 5.0219307f, -2.0975246f, -0.005168915f, -2.1486454f, -0.09574509f, -1.3144646f, -0.5622978f, -1.3144646f, -0.5622978f, 0.8097973f, -1.741375f, 2.582529f, -0.98112106f, 2.582529f, -0.98112106f, -0.08506584f, 5.0219307f, -2.0975246f, -0.005168915f, -2.0975246f, -0.005168915f, -1.3144646f, -0.5622978f, 2.582529f, -0.98112106f, };
		// float[] shape4 = new float[] { -0.20091629f, -0.65367126f, -0.99326706f, -2.8722992f, 2.0403023f, -2.3062592f, 2.0403023f, -2.3062592f, 3.1495914f, 0.7046051f, 1.8365536f, 2.923233f, 1.8365536f, 2.923233f, -1.5592327f, 2.0855865f, -2.1251974f, 0.115982056f, -2.1251974f, 0.115982056f, -2.1478376f, 0.002822876f, -0.20091629f, -0.65367126f, -0.20091629f, -0.65367126f, 2.0403023f, -2.3062592f, 1.8365536f, 2.923233f, 1.8365536f, 2.923233f, -2.1251974f, 0.115982056f, -0.20091629f, -0.65367126f, };
		// float[] shape5 = new float[] { -0.7739811f, -2.0314026f, -0.4276123f, -3.6699524f, 0.32294273f, -3.2503967f, 0.9065895f, -1.1490784f, 2.2499485f, 1.8977966f, 0.74209213f, 3.993744f, 0.74209213f, 3.993744f, -1.5428867f, 5.030487f, -1.4770966f, -0.8211975f, -1.4770966f, -0.8211975f, -0.7739811f, -2.0314026f, 0.32294273f, -3.2503967f, 0.32294273f, -3.2503967f, 0.9065895f, -1.1490784f, 0.74209213f, 3.993744f, 0.74209213f, 3.993744f, -1.4770966f, -0.8211975f, 0.32294273f, -3.2503967f, };
		// float[] shape6 = new float[] { -0.2645836f, 3.574934f, -2.1395836f, 2.1064281f, -1.3270836f, 0.85642815f, -1.0458336f, -0.64357185f, -1.9208336f, -0.612566f, -2.6708336f, -1.425066f, -1.3895836f, -1.800066f, -0.4208336f, -2.768816f, 0.3291664f, -1.425066f, 0.3291664f, -1.425066f, 1.3291664f, -1.831316f, 2.2979164f, -0.112565994f, 2.2979164f, -0.112565994f, 2.8916664f, -0.237566f, 1.9541664f, 1.231184f, 1.9541664f, 1.231184f, 2.3916664f, 2.668684f, -0.2645836f, 3.574934f, -0.2645836f, 3.574934f, -1.3270836f, 0.85642815f, -0.014583588f, 0.41892815f, -1.0458336f, -0.64357185f, -2.6708336f, -1.425066f, -1.3895836f, -1.800066f, -1.3895836f, -1.800066f, 0.3291664f, -1.425066f, 2.2979164f, -0.112565994f, 2.2979164f, -0.112565994f, 1.9541664f, 1.231184f, -0.2645836f, 3.574934f, -0.014583588f, 0.41892815f, -1.0458336f, -0.64357185f, -1.3895836f, -1.800066f, 2.2979164f, -0.112565994f, -0.2645836f, 3.574934f, -0.014583588f, 0.41892815f, -0.014583588f, 0.41892815f, -1.3895836f, -1.800066f, 2.2979164f, -0.112565994f, };

//		float[][] shapes = { shape0, shape1, shape2, shape3, shape4, shape5, shape6 };
		float[][] shapes = { shape0  };

		private OrthographicCamera worldCamera;

		@Override
		public void dispose() {
			box2dWorld.dispose();
			box2dDebugRenderer.dispose();
		}

		@Override
		public void hide() {

		}

		@Override
		public void pause() {

		}

		@Override
		public void render(float arg0) {
			if (!inited)
				return;
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			box2dDebugRenderer.render(box2dWorld, worldCamera.combined);
		}

		@Override
		public void resize(int arg0, int arg1) {

		}

		@Override
		public void resume() {
			if (inited)
				return;

			worldCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			worldCamera.zoom = 0.1f;

			worldCamera.update();

			box2dWorld = new World(new Vector2(), false);
			box2dDebugRenderer = new Box2DDebugRenderer();

			bodyBuilder = new BodyBuilder(box2dWorld);
			FixtureDefBuilder fixtureDefBuilder = bodyBuilder.fixtureDefBuilder();

			// for (int i = 0; i < shapes.length; i++) {
			//
			// float[] vertices = shapes[i];
			//
			// FixtureDef[] fixtureDefs = new FixtureDef[vertices.length / 6];
			//
			// for (int j = 0; j < vertices.length; j += 6) {
			//
			// triangleVertices[0].set(vertices[j], vertices[j + 1]);
			// triangleVertices[1].set(vertices[j + 2], vertices[j + 3]);
			// triangleVertices[2].set(vertices[j + 4], vertices[j + 5]);
			//
			// fixtureDefs[j / 6] = fixtureDefBuilder //
			// .polygonShape(triangleVertices) //
			// .restitution(0f) //
			// .build();
			//
			// }
			//
			// Body body = bodyBuilder.mass(1f) //
			// .fixtures(fixtureDefs) //
			// .position(0, 0) //
			// .type(BodyType.StaticBody) //
			// .angle(0) //
			// .build();
			//
			// float x = MathUtils.random(-25f, 25f);
			// float y = MathUtils.random(-25f, 25f);
			//
			// body.setTransform(x, y, 0f);
			//
			// }

			for (int i = 0; i < shapes.length; i++) {

				float[] vertices = shapes[i];

				NeatTriangulator triangulator = triangulate(vertices);

				FixtureDef[] fixtureDefs = new FixtureDef[triangulator.getTriangleCount()];
				
				for (int j = 0; j < triangulator.getTriangleCount(); j++) {

					for (int p = 0; p < 3; p++) {
						float[] pt = triangulator.getTrianglePoint(j, p);
						triangleVertices[p].set(pt[0], pt[1]);
					}
					
//					System.out.println(triangleVertices[0]);
//					System.out.println(triangleVertices[1]);
//					System.out.println(triangleVertices[2]);

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
			newTest();
			inited = true;
		}

		public NeatTriangulator triangulate(float[] vertices) {
			NeatTriangulator triangulator = new NeatTriangulator();
			for (int i = 0; i < vertices.length; i += 2)
				triangulator.addPolyPoint(vertices[i], vertices[i + 1]);
			triangulator.triangulate();
			return triangulator;
		}

		@Override
		public void show() {

		}
		
		private void newTest() {
			HashSet<Integer> triangles = new HashSet<Integer>();
			float[] shape = shapes[0];
			for (int i = 0; i < 30; i++) {
				NeatTriangulator triangulator = triangulate(shape);
				int triangleCount = triangulator.getTriangleCount();
				triangles.add(triangleCount);
			}
			
			Gdx.app.log("TESTTRIANGULATOR", "Triangles: " + triangles);
		}

	}

	@Override
	public void create() {
		FixedTrianglesShapeTestScreen screen = new FixedTrianglesShapeTestScreen();
		setScreen(screen);

		screen.resume();
		screen.show();
	}

	

	@Override
	public void render() {
		super.render();

		if (Gdx.input.justTouched()) {

			Screen previousScreen = getScreen();

			previousScreen.hide();
			previousScreen.dispose();

			FixedTrianglesShapeTestScreen newScreen = new FixedTrianglesShapeTestScreen();

			setScreen(newScreen);

			newScreen.resume();
			newScreen.show();

		}
	}

	// / internal classes only used as utilities

	static class BodyBuilder {

		private BodyDef bodyDef;
		private ArrayList<FixtureDef> fixtureDefs;
		private ArrayList<Object> fixtureUserDatas;
		private Object userData = null;
		private Vector2 position = new Vector2();
		private final World world;
		private float angle;

		FixtureDefBuilder fixtureDefBuilder;

		private MassData massData = new MassData();
		private boolean massSet;

		public BodyBuilder(World world) {
			this.world = world;
			this.fixtureDefBuilder = new FixtureDefBuilder();
			this.fixtureDefs = new ArrayList<FixtureDef>();
			this.fixtureUserDatas = new ArrayList<Object>();
			reset(true);
		}

		public FixtureDefBuilder fixtureDefBuilder() {
			return fixtureDefBuilder;
		}

		private void reset(boolean disposeShapes) {

			if (fixtureDefs != null && disposeShapes) {
				for (int i = 0; i < fixtureDefs.size(); i++) {
					FixtureDef fixtureDef = fixtureDefs.get(i);
					fixtureDef.shape.dispose();
				}
			}

			bodyDef = new BodyDef();
			fixtureDefs.clear();
			fixtureUserDatas.clear();
			angle = 0f;
			userData = null;
			position.set(0f, 0f);
			massSet = false;
		}

		public BodyBuilder type(BodyType type) {
			bodyDef.type = type;
			return this;
		}

		public BodyBuilder bullet() {
			bodyDef.bullet = true;
			return this;
		}

		public BodyBuilder fixedRotation() {
			bodyDef.fixedRotation = true;
			return this;
		}

		public BodyBuilder fixture(FixtureDefBuilder fixtureDef) {
			return fixture(fixtureDef, null);
		}

		public BodyBuilder fixture(FixtureDefBuilder fixtureDef, Object fixtureUserData) {
			fixtureDefs.add(fixtureDef.build());
			fixtureUserDatas.add(fixtureUserData);
			return this;
		}

		public BodyBuilder fixture(FixtureDef fixtureDef) {
			return fixture(fixtureDef, null);
		}

		public BodyBuilder fixture(FixtureDef fixtureDef, Object fixtureUserData) {
			fixtureDefs.add(fixtureDef);
			fixtureUserDatas.add(fixtureUserData);
			return this;
		}

		public BodyBuilder fixtures(FixtureDef[] fixtureDefs) {
			return fixtures(fixtureDefs, null);
		}

		public BodyBuilder fixtures(FixtureDef[] fixtureDefs, Object[] fixtureUserDatas) {
			if (fixtureUserDatas != null && (fixtureDefs.length != fixtureUserDatas.length))
				throw new RuntimeException("length mismatch between fixtureDefs(" + fixtureDefs.length + ") and fixtureUserDatas(" + fixtureUserDatas.length + ")");

			for (int i = 0; i < fixtureDefs.length; i++) {
				this.fixtureDefs.add(fixtureDefs[i]);

				Object fixtureUserData = fixtureUserDatas != null ? fixtureUserDatas[i] : null;
				this.fixtureUserDatas.add(fixtureUserData);
			}

			return this;

		}

		public BodyBuilder mass(float mass) {
			this.massData.mass = mass;
			this.massSet = true;
			return this;
		}

		public BodyBuilder inertia(float intertia) {
			this.massData.I = intertia;
			this.massSet = true;
			return this;
		}

		public BodyBuilder userData(Object userData) {
			this.userData = userData;
			return this;
		}

		public BodyBuilder position(float x, float y) {
			this.position.set(x, y);
			return this;
		}

		public BodyBuilder angle(float angle) {
			this.angle = angle;
			return this;
		}

		public Body build() {
			return build(true);
		}

		public Body build(boolean disposeShapes) {
			Body body = world.createBody(bodyDef);

			for (int i = 0; i < fixtureDefs.size(); i++) {
				FixtureDef fixtureDef = fixtureDefs.get(i);
				Fixture fixture = body.createFixture(fixtureDef);
				fixture.setUserData(fixtureUserDatas.get(i));
			}

			if (massSet) {
				MassData bodyMassData = body.getMassData();

				// massData.center.set(position);
				massData.center.set(bodyMassData.center);
				// massData.I = bodyMassData.I;

				body.setMassData(massData);
			}
			// MassData massData = body.getMassData();
			// massData.mass = mass;
			// massData.I = 1f;

			body.setUserData(userData);
			body.setTransform(position, angle);

			reset(disposeShapes);
			return body;
		}

	}

	static class FixtureDefBuilder {

		FixtureDef fixtureDef;

		public FixtureDefBuilder() {
			reset();
		}

		public FixtureDefBuilder sensor() {
			fixtureDef.isSensor = true;
			return this;
		}

		public FixtureDefBuilder boxShape(float hx, float hy) {
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(hx, hy);
			fixtureDef.shape = shape;
			return this;
		}

		public FixtureDefBuilder boxShape(float hx, float hy, Vector2 center, float angleInRadians) {
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(hx, hy, center, angleInRadians);
			fixtureDef.shape = shape;
			return this;
		}

		public FixtureDefBuilder circleShape(float radius) {
			Shape shape = new CircleShape();
			shape.setRadius(radius);
			fixtureDef.shape = shape;
			return this;
		}

		public FixtureDefBuilder circleShape(Vector2 center, float radius) {
			CircleShape shape = new CircleShape();
			shape.setRadius(radius);
			shape.setPosition(center);
			fixtureDef.shape = shape;
			return this;
		}

		public FixtureDefBuilder polygonShape(Vector2[] vertices) {
			PolygonShape shape = new PolygonShape();
			shape.set(vertices);
			fixtureDef.shape = shape;
			return this;
		}

		public FixtureDefBuilder density(float density) {
			fixtureDef.density = density;
			return this;
		}

		public FixtureDefBuilder friction(float friction) {
			fixtureDef.friction = friction;
			return this;
		}

		public FixtureDefBuilder restitution(float restitution) {
			fixtureDef.restitution = restitution;
			return this;
		}

		public FixtureDefBuilder categoryBits(short categoryBits) {
			fixtureDef.filter.categoryBits = categoryBits;
			return this;
		}

		public FixtureDefBuilder maskBits(short maskBits) {
			fixtureDef.filter.maskBits = maskBits;
			return this;
		}

		private void reset() {
			fixtureDef = new FixtureDef();
		}

		public FixtureDef build() {
			FixtureDef fixtureDef = this.fixtureDef;
			reset();
			return fixtureDef;
		}

	}
}