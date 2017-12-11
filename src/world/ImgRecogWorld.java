package world;

import org.joml.Vector3f;

import entities.WorldObject;
import entities.meshes.cube.Cube;
import recognition.ImgRecogPlanner;
import utils.FloatMath;
import utils.Utils;
import utils.IO.MouseInput;

public class ImgRecogWorld extends World {


	public ImgRecogWorld( float x, float y, float z, float dx, float dy, float dz) {
		super(1, false);

		this.x = x;
		this.y = y;
		this.z = z;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
	}

	@Override
	public void setup() {
		config = Utils.createDefaultConfig();
		
		physics.init(config, new Vector3f(0, 0, 0), 0, (float) Math.PI/4, (float) -Math.PI/4, 0);
		
		planner = new ImgRecogPlanner(x, y, z, dx, dy, dz);
		
		Cube redCube = new Cube(0, 1);
		//Cube blueCube = new Cube(0, 0.5f);
		worldObjects = new WorldObject[] {new WorldObject(redCube.getMesh())};
		
		worldObjects[0].setPosition(x, y, z);
		//worldObjects[1].setPosition(3, 4, -4);
		
		
	}
	
	private float x, y, z;
	private float dx, dy, dz;
	
	@Override
	public void update(float interval, MouseInput mouseInput) {

		float oldZ = worldObjects[0].getPosition().z;
		float oldX = worldObjects[0].getPosition().x;
		float oldY = worldObjects[0].getPosition().y;
		worldObjects[0].setPosition(oldX + dx, oldY + dy, oldZ + dz);
		
		if (oldX > 10){
			gameEngine.setLoopShouldExit();
		}
		super.update(interval, mouseInput);
	}

	@Override
	public String getDescription() {
		return "An automated test for the image recognition";
	}
}
