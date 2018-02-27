package entities.ground;

import java.util.List;
import java.util.stream.Collectors;

import entities.WorldObject;
import meshes.Mesh;
import utils.Constants;

public class Ground {
	
	TileManager tm;
	
	public Ground(int x, int y, int size) {
		this.tm = new TileManager(size, Constants.TILE_SIZE);
	}
	
	public Ground(int size) {
		this(0, 0, size);
	}

	public List<WorldObject> getTiles() {
		return tm.getTileList();
	}

}
