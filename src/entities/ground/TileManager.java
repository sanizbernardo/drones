package entities.ground;

import java.util.ArrayList;
import java.util.List;

import utils.RGBTuple;
import meshes.tile.UnitTile;
import entities.WorldObject;

public class TileManager {

	List<WorldObject> tileList;
	
	public TileManager(int size) {
		tileList = new ArrayList<WorldObject>();
		WorldObject testTile = new WorldObject(new UnitTile(new RGBTuple(0.5f, 0.5f, 0.5f)).getMesh());
		testTile.setPosition(0, 0, 0);
		testTile.setScale(10);
		tileList.add(testTile);
	}

	public List<WorldObject> getTileList() {
		return tileList;
	}
	
	
}
