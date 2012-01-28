package uy.globalgamejam.medusa;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gemserk.animation4j.converters.TypeConverter;

public class SpritePositionConverter implements TypeConverter<Sprite> {

	@Override
	public int variables() {
		return 2;
	}

	@Override
	public float[] copyFromObject(Sprite sprite, float[] x) {
		if (x == null)
			x = new float[variables()];
		x[0] = sprite.getX();
		x[1] = sprite.getY();
		return x;
	}

	@Override
	public Sprite copyToObject(Sprite sprite, float[] x) {
		sprite.setPosition(x[0], x[1]);
		return sprite;
	}

}
