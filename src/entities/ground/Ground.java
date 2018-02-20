package entities.ground;

import java.util.List;
import java.util.stream.Collectors;

import entities.WorldObject;
import meshes.Mesh;

public class Ground {
	
	TileManager tm;
	
	public Ground(int x, int y, int size) {
		this.tm = new TileManager(size);
	}
	
	public Ground(int size) {
		this(0, 0, size);
	}

	public List<WorldObject> getTiles() {
		return tm.getTileList();
	}

}
