package meshes.tile;

import utils.RGBTuple;
import meshes.AbstractMesh;

public class UnitTile extends AbstractMesh {

	RGBTuple color;
	
	public UnitTile(RGBTuple color) {
		this.color = color;
		finalizer();
	}
	
	@Override
	protected void setPositions() {
		this.positions = new float[]{
			0       , 0, 0,          //0
			0.7603f , 0, -0.6495f,   //1
			0.7603f , 0, 0.6495f,    //2
			-0.7603f, 0, 0.6495f,   //3
			-0.7603f, 0, -0.6495f,  //4
			
		};
	}

	@Override
	protected void setColours() {
		this.colours = new float[]{
				color.getRed(), color.getGreen(), color.getBlue(),
				color.getRed(), color.getGreen(), color.getBlue(),
				color.getRed(), color.getGreen(), color.getBlue(),
				color.getRed(), color.getGreen(), color.getBlue(),
				color.getRed(), color.getGreen(), color.getBlue(),
		};
	}

	@Override
	protected void setIndices() {
		this.indices = new int[]{
				1,2,3,
				3,4,1,
		};
	}

}
