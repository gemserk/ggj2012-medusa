package uy.globalgamejam.medusa.components;

import box2dLight.Light;

import com.artemis.Component;

public class LightComponent extends Component {
	
	public Light light;

	public LightComponent(Light light) {
		this.light = light;
	}

}
