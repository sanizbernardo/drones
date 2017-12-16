package utils;

import entities.meshes.cube.Cube;

public class Cubes {
	
	public static final Cube redCube = new Cube(0,1f);
	public static final Cube greenCube = new Cube(120,1f);
	public static final Cube blueCube = new Cube(240,1f);
	public static final Cube yellowCube = new Cube(60,1f);
	
	public static Cube[] getCubes() {
		return new Cube[] {redCube, greenCube, blueCube};
	}
	
}
