package entities.ground;

import java.util.ArrayList;
import java.util.List;

import utils.RGBTuple;
import utils.Utils;
import meshes.tile.DoubleUnitTile;
import entities.WorldObject;

public class TileManager {

	List<WorldObject> tileList;
	
	public TileManager(int size, int tileSize) {
		tileList = new ArrayList<WorldObject>();
		
		float[] col1 = Utils.toRGB(-105, 1, 0.8f);
		float[] col2 = Utils.toRGB(-105, 1, 0.7f);
		
		RGBTuple light = new RGBTuple(col1[0], col1[1], col1[2]);
		RGBTuple dark = new RGBTuple(col2[0], col2[1], col2[2]);

		
		int val;
		boolean checkZero = false;
		boolean even;
		if (size % 2 == 0) {
			val = size / 2;
			even = true;
		} else {
			val = (size - 1) / 2;
			checkZero = true;
			even = false;
		}
		
		//oneven op de gehele getallen
		
		boolean flip = true;
		for(int i = -val; i <= val; i++) {
			if(i==0 && !checkZero) {continue;}
			
			for(int j = -val; j <= val; j++) {
				if(j==0 && !checkZero) {continue;}
				
				DoubleUnitTile doubleUnitTile = (flip) ? new DoubleUnitTile(light) :  new DoubleUnitTile(dark);
				flip = !flip;
				
				WorldObject tile = new WorldObject(doubleUnitTile.getMesh());

				float iVal = 0;
				if(even) {
					iVal = (i < 0) ? (+0.5f) : -0.5f;
				}

                float jVal = 0;
                if(even) {
                    jVal = (j < 0) ? (+0.5f) : -0.5f;
                }

				tile.setPosition((i + iVal) * tileSize * doubleUnitTile.getSize(),0,(j + jVal) *  tileSize * doubleUnitTile.getSize());
				tile.setScale(tileSize);
				tileList.add(tile);
				
			} if(even) flip = !flip;
		}
		
	}

	public List<WorldObject> getTileList() {
		return tileList;
	}
	
	
}
