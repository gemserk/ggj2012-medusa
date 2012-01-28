package uy.globalgamejam.medusa.components;

import com.artemis.Component;
import com.artemis.Entity;

public class TailPartComponent extends Component {

	public Entity owner;

	public TailPartComponent(Entity owner) {
		this.owner = owner;
	}
}
