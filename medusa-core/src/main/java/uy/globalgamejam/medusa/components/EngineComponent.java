package uy.globalgamejam.medusa.components;

import com.artemis.Component;

public class EngineComponent extends Component {
	
	public float speed = 10f;
	public float maxSpeed;
	
	public EngineComponent(float maxSpeed) {
		this.maxSpeed = maxSpeed;
		this.speed = maxSpeed;
	}
	
	public EngineComponent() {
		this(10f);
	}
	
}
