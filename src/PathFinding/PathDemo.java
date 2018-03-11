package PathFinding;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.*;
	
@SuppressWarnings("serial")
public class PathDemo extends JFrame {
	
	public ArrayList<float[]> path;
	public ArrayList<float[]> cubeLocs;
	
	
	public PathDemo(ArrayList<float[]> path, ArrayList<float[]> cubeLocs) {
		this.path = path;
		this.cubeLocs = cubeLocs;
		
		this.setSize(1600, 1000);
		this.setTitle("Graphic representation");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.add(new DrawStuff(), BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	private class DrawStuff extends JComponent {
		
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON);
						
			g2.setPaint(Color.ORANGE);

			g2.fillOval((int)(0+800), (int)(0+500), 5, 5);
			
			float scale = 1.5f;
			
			for (int i=0;i<path.size()-1;i++) {
				Shape line = new Line2D.Float(path.get(i)[0]/scale+800, path.get(i)[2]/scale+500, 
						path.get(i+1)[0]/scale+800, path.get(i+1)[2]/scale+500);
				if (i == 0)
					g2.setPaint(Color.green);
				else if (i == path.size()-2)
					g2.setPaint(Color.red);
				else
					g2.setPaint(Color.black);
				g2.draw(line);
				
				g2.setPaint(Color.BLUE);
				g2.fillOval((int)(path.get(i)[0]/scale+800), (int)(path.get(i)[2]/scale+500), 5, 5);

			}
			for (int i=0;i<cubeLocs.size();i++){
				g2.setPaint(Color.YELLOW);
				g2.fillOval((int)(cubeLocs.get(i)[0]/scale+800), (int)(cubeLocs.get(i)[2]/scale+500), 5, 5);
			}
		}
	}

}
