package world;

import entities.WorldObject;
import entities.meshes.cube.Cube;
import recognition.ImgRecogPlanner;
import utils.IO.MouseInput;

public class ImgRecogWorld extends World {

	ImgRecogWorld() {
		super(1, false, true);
		super.planner = new ImgRecogPlanner();
	}

	@Override
	public void setup() {
		Cube redCube = new Cube(0, 1);
		worldObjects = new WorldObject[] {new WorldObject(redCube.getMesh())};
		
		worldObjects[0].setPosition(0f, 0f, 10f);
	}
	
	private float x, y, z;
	
	public void update(float interval, MouseInput mouseInput) {
		
		// update x, y z
		
		worldObjects[0].setPosition(x, y, z);
		
		super.update(interval, mouseInput);
	}

}
