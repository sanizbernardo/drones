package testbed.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.joml.Vector3f;

import testbed.entities.WorldObject;
import utils.Constants;
import utils.FloatMath;
import utils.GuiUtils;

import javax.swing.JLabel;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TestbedGui extends JFrame {

	private static final long serialVersionUID = 1L;
	private final int precision;
	
	private JPanel contentPane, dronePane, packagePane;
	private JLabel[] position, velocity, orientation;
	
	private MiniMap minimap;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestbedGui frame = new TestbedGui();
					
					frame.showGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public TestbedGui() {
		this.precision = 2;
		
		setTitle("Testbed GUI");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
        int ubuntuSiderBar = 0;
        int ubuntuHeader = 0;
        if (System.getProperty("os.name").contains("Linux")) {
        	ubuntuSiderBar = 105;
        	ubuntuHeader = 44;
        }
        
		setBounds(ubuntuSiderBar, Constants.AUTOPILOT_GUI_HEIGHT +  2 * ubuntuHeader, Constants.AUTOPILOT_GUI_WIDTH, Constants.AUTOPILOT_GUI_HEIGHT * 2);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		dronePane = new JPanel();
		
		
		
		
		
		
		this.minimap = new MiniMap(1500, 2100);
		GridBagConstraints gbc_mm = GuiUtils.buildGBC(0, 16, GridBagConstraints.CENTER);
		gbc_mm.gridwidth = 5;
		contentPane.add(minimap, gbc_mm);
		
	}
	
	
	public void showGUI() {
		pack();
		setVisible(true);
	}
	
	
	public void setDrones(Vector3f drone, float heading) {
		this.minimap.setDrone(drone, heading);
	}
	
	public void update(Vector3f velocity, Vector3f position, float heading, float pitch, float roll) {
	    this.position[0].setText("" + FloatMath.round(position.x, precision));
	    this.position[1].setText("" + FloatMath.round(position.y, precision));
	    this.position[2].setText("" + FloatMath.round(position.z, precision));
		
	    this.velocity[0].setText("" + FloatMath.round(velocity.x, precision));
	    this.velocity[1].setText("" + FloatMath.round(velocity.y, precision));
	    this.velocity[2].setText("" + FloatMath.round(velocity.z, precision));
	    this.velocity[3].setText("" + FloatMath.round(FloatMath.norm(velocity), precision));
		
	    this.orientation[0].setText("" + FloatMath.round(FloatMath.toDegrees(pitch), precision));
	    this.orientation[1].setText("" + FloatMath.round(FloatMath.toDegrees(heading), precision));
	    this.orientation[2].setText("" + FloatMath.round(FloatMath.toDegrees(roll), precision));
	}
	
	
	private class MiniMap extends Component {
		
		private static final long serialVersionUID = 1L;
		
		private final int maxX;
		private final int maxY;
		
		private Vector3f[] cubes;
		
		private Vector3f drone; 
		private float heading;
		
		public MiniMap(int maxX, int maxZ) {			
			this.maxX = maxZ;
			this.maxY = maxX;
			
			this.cubes = new Vector3f[0];
		}
		
		public void setDrone(Vector3f drone, float heading) {
			this.drone = drone;
			this.heading = heading;
		}
		
		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			
			Dimension size = this.getSize();
			
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(2));
			
			g2.drawRect(0, 0, size.width-1, size.height-1);
			
			int xAxis = 20,
				yAxis = (int) (xAxis * this.maxY / this.maxX);
			
			g2.drawLine(size.width/2 - xAxis, size.height/2, size.width/2 + xAxis, size.height/2);
			g2.drawLine(size.width/2, size.height/2 - yAxis, size.width/2, size.height/2 + yAxis);
			
			g2.setColor(Color.BLUE);
			g2.setStroke(new BasicStroke(1));
			
			for (Vector3f cube: this.cubes) {
				int x = (int) (size.width/2 - cube.z/this.maxX * size.width/2),
					y = (int) (size.height/2 + cube.x/this.maxY * size.height/2);
				
				g2.fillRect(x-3, y-3, 6, 6);
			}
			
			if (this.drone != null) {
				
				g2.setColor(Color.RED);
				g2.setStroke(new BasicStroke(3));
				
				float wy = - FloatMath.sin(this.heading) * 35 / this.maxX * size.width/2,
					  wx = FloatMath.cos(this.heading) * 35 / this.maxX * size.width/2,
					  x = size.width/2 - drone.z/this.maxX * size.width/2,
					  y = size.height/2 + drone.x/this.maxY * size.height/2;
				
				g2.drawLine((int) (x - wx), (int) (y - wy), (int) (x + wx), (int) (y + wy));
				
				g2.drawLine((int) (x + wy/2), (int) (y - wx/2),
							(int) (x - wy/2), (int) (y + wx/2));
			}
			
		}
	}
	
}
