package world;

import org.joml.Vector3f;

import entities.WorldObject;
import entities.meshes.cube.Cube;
import recognition.ImgRecogPlanner;
import utils.IO.MouseInput;

public class ImgRecogWorld extends World {

	public ImgRecogWorld( float x, float y, float z, float dx, float dy, float dz) {
		super(1, false, true);
		super.planner = new ImgRecogPlanner(x, y, z, dx, dy, dz);
		this.x = x;
		this.y = y;
		this.z = z;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
	}

	@Override
	public void setup() {
		Cube redCube = new Cube(0, 1);
		worldObjects = new WorldObject[] {new WorldObject(redCube.getMesh())};
		
		worldObjects[0].setPosition(x, y, z);
		
		drone.setOrientation(new Vector3f(45,-45,0));
	}
	
	private float x, y, z;
	private float dx, dy, dz;
	private boolean ended = false;

	
	public void update(float interval, MouseInput mouseInput) {
		
		// update x, y z
		
		float oldZ = worldObjects[0].getPosition().z;
		float oldX = worldObjects[0].getPosition().x;
		float oldY = worldObjects[0].getPosition().y;
		worldObjects[0].setPosition(oldX + dx, oldY + dy, oldZ + dz);
		//System.out.println(worldObjects[0].getPosition());
		if (oldZ < -40){
			gameEngine.setLoopShouldExit();
		}
		super.update(interval, mouseInput);
	}

}
