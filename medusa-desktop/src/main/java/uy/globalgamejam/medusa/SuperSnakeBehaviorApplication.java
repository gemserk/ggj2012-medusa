package uy.globalgamejam.medusa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.box2d.JointBuilder;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.lwjgl.FocusableLwjglApplicationDelegate;

public class SuperSnakeBehaviorApplication {

	protected static final Logger logger = LoggerFactory.getLogger(SuperSnakeBehaviorApplication.class);

	public static class SuperSnakeGame extends com.gemserk.commons.gdx.Game {

		private Box2DDebugRenderer box2dDebugRenderer;
		private World world;
		private BodyBuilder bodyBuilder;
		private Libgdx2dCameraTransformImpl libgdx2dCamera;
		private JointBuilder jointBuilder;

		private Body bodyA;

		private Body[] bodies;

		@Override
		public void create() {
			super.create();
			box2dDebugRenderer = new Box2DDebugRenderer();

			libgdx2dCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.35f, Gdx.graphics.getHeight() * 0.5f);
			libgdx2dCamera.zoom(48f);

			world = new com.badlogic.gdx.physics.box2d.World(new Vector2(), false);
			bodyBuilder = new BodyBuilder(world);

			bodyA = bodyBuilder //
					.fixture(bodyBuilder.fixtureDefBuilder() //
							.circleShape(0.25f) //
							.friction(0f)) //
					.angle(0f) //
					.position(0f, 0f) //
					.type(BodyType.DynamicBody) //
					.build();

			bodyCount = 25;
			bodies = new Body[bodyCount + 1];

			bodies[0] = bodyA;

			for (int i = 1; i < bodyCount; i++) {

				bodies[i] = bodyBuilder //
						.fixture(bodyBuilder.fixtureDefBuilder() //
								.circleShape(0.1f) //
								.sensor() //
								.maskBits((short) 0x00) //
								.friction(0f)) //
						.angle(0f) //
						.position(0f, 0f) //
						.type(BodyType.DynamicBody) //
						.build();

			}

		}

		Vector2 position = new Vector2();
		Vector2 tmp = new Vector2();
		private int bodyCount;

		private float displacementY(int elementNumber, float elementDistance, float maxDisplacement, float frameCount) {
			float radians = (float) Math.toRadians((elementNumber * elementDistance) + frameCount / 10f);
			return (float) (maxDisplacement * Math.sin(radians));
		}

		@Override
		public void render() {
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			GlobalTime.setDelta(Gdx.graphics.getDeltaTime());

			world.step(0.1f, 3, 3);

			float y = Gdx.graphics.getHeight() - Gdx.input.getY();
			position.set(0f, y);

			libgdx2dCamera.unproject(position);

			Vector2 previousPartPosition = bodies[0].getPosition();
			float previousPartAngle = bodies[0].getAngle();

			float x = previousPartPosition.x;

			for (int i = 1; i < bodyCount; i++) {
				Body bodyPart = bodies[i];

				// float displacementY = displacementY(i, 0f, 0.5f, x);
				// MathUtils.sin(rad)
				float amplitud = 0.02f * i;

				if (amplitud > 0.15f)
					amplitud = 0.15f;
				
				if (Math.abs(bodyA.getLinearVelocity().y) > 1f)
					amplitud *= 1f / (Math.abs(bodyA.getLinearVelocity().y) * 10f);
				
				
				float displacementY = (float) Math.sin(x * 0.5f + i) * amplitud;

				// if (Math.abs(bodyA.getLinearVelocity().y) > 0.1f)
				// displacementY = 0f;

				Vector2 currentPartPosition = bodyPart.getPosition();
				float currentPartAngle = bodyPart.getAngle();

				bodyPart.setTransform(x - 0.2f * i, previousPartPosition.y + displacementY, previousPartAngle);
				bodyPart.setLinearVelocity(0f, 0f);
				bodyPart.setAngularVelocity(0f);

				previousPartPosition = currentPartPosition;
				previousPartAngle = currentPartAngle;
			}

			bodyA.applyForceToCenter(2f, 0f);
			if (Gdx.input.isKeyPressed(Keys.UP)) {
				bodyA.applyForceToCenter(0f, 2f);
			} else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
				bodyA.applyForceToCenter(0f, -2f);
			}

			Vector2 linearVelocity = bodyA.getLinearVelocity();

			float speed = linearVelocity.len();
			float maxSpeed = 5f;

			if (speed > maxSpeed) {
				float factor = maxSpeed / speed;
				linearVelocity.mul(factor);
				bodyA.setLinearVelocity(linearVelocity);
			}

			previousPartPosition = bodyA.getPosition();

			libgdx2dCamera.move(previousPartPosition.x, 0f);

			box2dDebugRenderer.render(world, libgdx2dCamera.getCombinedMatrix());
		}

	}

	public static void main(String[] argv) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Global Game Jam 2012 - Medusa";
		config.width = 800;
		config.height = 600;
		config.fullscreen = false;
		config.useGL20 = false;
		config.useCPUSynch = true;
		config.forceExit = true;
		config.vSyncEnabled = true;

		new LwjglApplication(new FocusableLwjglApplicationDelegate(new SuperSnakeGame()), config);
	}

}
