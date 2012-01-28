package uy.globalgamejam.medusa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
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

		@Override
		public void create() {
			super.create();
			box2dDebugRenderer = new Box2DDebugRenderer();

			libgdx2dCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.5f);
			libgdx2dCamera.zoom(48f);

			world = new com.badlogic.gdx.physics.box2d.World(new Vector2(), false);
			bodyBuilder = new BodyBuilder(world);

			bodyA = bodyBuilder //
					.fixture(bodyBuilder.fixtureDefBuilder() //
							.circleShape(0.5f) //
							.friction(0f)) //
					.angle(0f) //
					.position(0f, 0f) //
					.type(BodyType.DynamicBody) //
					.build();

			jointBuilder = new JointBuilder(world);

			Body first = bodyA;

			for (int i = 0; i < 10; i++) {

				Vector2 p = first.getPosition();

				Body bodyPart = bodyBuilder //
						.fixture(bodyBuilder.fixtureDefBuilder() //
								.circleShape(0.25f) //
						) //
						.mass(0.01f) //
						.angle(0f) //
						.position(p.x - 1f, 0f) //
						.type(BodyType.DynamicBody) //
						.build();

				// jointBuilder.distanceJoint() //
				// .length(first == bodyA ? 0.75f : 0.5f) //
				// .bodyA(first)//
				// .bodyB(bodyPart) //
				// .build();

				RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
				revoluteJointDef.bodyA = first;
				revoluteJointDef.bodyB = bodyPart;
				revoluteJointDef.collideConnected = false;
				revoluteJointDef.enableLimit = true;
				revoluteJointDef.maxMotorTorque = 1000f;
				revoluteJointDef.enableMotor = false;
				revoluteJointDef.motorSpeed = 10f;
				revoluteJointDef.lowerAngle = -180f * MathUtils.degreesToRadians;
				revoluteJointDef.upperAngle = 180f * MathUtils.degreesToRadians;

				revoluteJointDef.localAnchorA.set(0f, 0f);
				// revoluteJointDef.localAnchorB.set(0.5f, 0f);
				revoluteJointDef.localAnchorB.set(first == bodyA ? 0.75f : 0.5f, 0f);

				world.createJoint(revoluteJointDef);

				first = bodyPart;

			}

		}

		Vector2 position = new Vector2();
		Vector2 tmp = new Vector2();

		@Override
		public void render() {
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			GlobalTime.setDelta(Gdx.graphics.getDeltaTime());

			world.step(0.1f, 3, 3);
			box2dDebugRenderer.render(world, libgdx2dCamera.getCombinedMatrix());

			float y = Gdx.graphics.getHeight() - Gdx.input.getY();
			position.set(0f, y);

			libgdx2dCamera.unproject(position);

			Vector2 bodyPosition = bodyA.getPosition();

			bodyA.applyForceToCenter(2f, 0f);
			if (Gdx.input.isKeyPressed(Keys.UP)) {
				bodyA.applyForceToCenter(0f, 0.5f);
			} else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
				bodyA.applyForceToCenter(0f, -0.5f);
			}

			// Vector2 linearVelocity = bodyA.getLinearVelocity();
			// float speed = 5f;
			//
			// if (linearVelocity.len() > speed) {
			// float v = speed - linearVelocity.len();
			//
			// tmp.set(linearVelocity).nor();
			// tmp.mul(v * bodyA.getMass() / GlobalTime.getDelta());
			//
			// bodyA.applyForceToCenter(tmp);
			// }

			libgdx2dCamera.move(bodyPosition.x, 0f);
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
