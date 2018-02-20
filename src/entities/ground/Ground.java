package entities.ground;

public class Ground {
	
	public Ground(int x, int y, int size) {
		TileManager tm = new TileManager(size);
	}
	
	public Ground(int size) {
		this(0, 0, size);
	}
	
}
