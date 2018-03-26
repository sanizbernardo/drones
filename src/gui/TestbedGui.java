package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.joml.Vector3f;

import entities.WorldObject;
import utils.Constants;
import utils.FloatMath;

import javax.swing.JLabel;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TestbedGui extends JFrame {

	private static final long serialVersionUID = 1L;
	private final int precision;
	
	private JPanel contentPane;
	private JLabel[] position, velocity, orientation;
	private JButton pathBtn;
	
	private boolean setPath;
	
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
		this.setPath = false;
		
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
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWeights = new double[] {1,0,0,0,1};
		contentPane.setLayout(gbl);
		
		JLabel title = new JLabel("Drone statistics");
		GridBagConstraints gbc_title = GuiUtils.buildGBC(1, 0, GridBagConstraints.CENTER);
		gbc_title.gridwidth = 3;
		contentPane.add(title, gbc_title);
		
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setPreferredSize(new Dimension(200, 5));
		GridBagConstraints gbc_sep = GuiUtils.buildGBC(1, 1, GridBagConstraints.CENTER, new Insets(3, 0, 3, 0));
		gbc_sep.gridwidth = 3;
		contentPane.add(sep, gbc_sep);
		
		
		position = buildVectorLbl("Position", new String[] {"x", "y", "z"}, 2, false); 
		
		velocity = buildVectorLbl("Velocity", new String[] {"x", "y", "z"} , 6, true);
		
		orientation = buildVectorLbl("Orientation", new String[] {"pitch", "heading", "roll"}, 11, false);
		
		pathBtn = new JButton("Set path");
		GridBagConstraints gbc_btn = GuiUtils.buildGBC(1, 15, GridBagConstraints.CENTER, new Insets(0, 0, 5, 0));
		gbc_btn.gridwidth = 3;
		contentPane.add(pathBtn, gbc_btn);
		
		pathBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setPath = true;
			}
		});
		
		this.minimap = new MiniMap(500, 300, 1500, 2000);
		GridBagConstraints gbc_mm = GuiUtils.buildGBC(0, 16, GridBagConstraints.CENTER);
		gbc_mm.gridwidth = 5;
		contentPane.add(minimap, gbc_mm);
		
	}
	
	
	private JLabel[] buildVectorLbl(String title, String[] componentLbl, int y, boolean norm) {
		JLabel lbl = new JLabel(title);
		GridBagConstraints gbc_lbl = GuiUtils.buildGBC(1, y, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5));
		gbc_lbl.gridheight = 3;
		contentPane.add(lbl, gbc_lbl);
		
		JLabel[] numbers = new JLabel[(norm ? 4: 3)];  
		for (int i = 0; i < 3; i++) {
			JLabel nbLbl = new JLabel(componentLbl[i] + ": ");
			contentPane.add(nbLbl, GuiUtils.buildGBC(2, y + i, GridBagConstraints.CENTER, new Insets(0, 10, 0, 10)));
			numbers[i] = new JLabel("0");
			contentPane.add(numbers[i], GuiUtils.buildGBC(3, y + i, GridBagConstraints.CENTER));
		}
		
		if (norm) {
			JLabel normLbl = new JLabel("norm:");
			contentPane.add(normLbl, GuiUtils.buildGBC(1, y+3, GridBagConstraints.CENTER, new Insets(0, 10, 0, 10)));
			numbers[3] = new JLabel("0");
			contentPane.add(numbers[3], GuiUtils.buildGBC(3, y+3, GridBagConstraints.CENTER));
		}
		
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setPreferredSize(new Dimension(200, 5));
		GridBagConstraints gbc_sep = GuiUtils.buildGBC(1, y + (norm ? 4 : 3), GridBagConstraints.CENTER);
		gbc_sep.gridwidth = 3;
		contentPane.add(sep, gbc_sep);
		
		return numbers;
	}
	
	
	public boolean setPath() {
		return this.setPath;
	}
	
	
	public void showGUI() {
		pack();
		setVisible(true);
	}
	
	public void setCubes(WorldObject[] objects) {
		ArrayList<Vector3f> cubes = new ArrayList<Vector3f>();
		
		for (WorldObject obj: objects) {
			if (obj != null)
				cubes.add(obj.getPosition());
		}
		
		this.minimap.setCubes(cubes.toArray(new Vector3f[0]));
	}
	
	public void setDrone(Vector3f drone, float heading) {
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

		private final Dimension prefSize;
		
		private final int maxX;
		private final int maxY;
		
		private Vector3f[] cubes;
		
		private Vector3f drone; 
		private float heading;
		
		public MiniMap(int width, int height, int maxX, int maxZ) {
			this.prefSize = new Dimension(width, height);
			
			this.maxX = maxZ;
			this.maxY = maxX;
			
			this.cubes = new Vector3f[0];
		}
		
		public void setCubes(Vector3f[] cubes) {
			this.cubes = cubes;
			this.repaint();
		}
		
		public void setDrone(Vector3f drone, float heading) {
			this.drone = drone;
			this.heading = heading;
		}
		
		@Override
		public Dimension getPreferredSize() {
			return prefSize;
		}
		
		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(2));
			
			g2.drawRect(0, 0, prefSize.width-1, prefSize.height-1);
			
			g2.drawLine(prefSize.width/2 - 20, prefSize.height/2, prefSize.width/2 + 20, prefSize.height/2);
			g2.drawLine(prefSize.width/2, prefSize.height/2 - 20, prefSize.width/2, prefSize.height/2 +20);
			
			g2.setColor(Color.BLUE);
			g2.setStroke(new BasicStroke(1));
			
			for (Vector3f cube: this.cubes) {
				int x = (int) (prefSize.width/2 - cube.z/this.maxX * prefSize.width/2),
					y = (int) (prefSize.height/2 + cube.x/this.maxY * prefSize.height/2);
				
				g2.fillRect(x-3, y-3, 6, 6);
			}
			
			if (this.drone != null) {
				
				g2.setColor(Color.RED);
				g2.setStroke(new BasicStroke(3));
				
				float wy = - FloatMath.sin(this.heading) * 35 / this.maxX * prefSize.width/2,
					  wx = FloatMath.cos(this.heading) * 35 / this.maxX * prefSize.width/2,
					  x = prefSize.width/2 - drone.z/this.maxX * prefSize.width/2,
					  y = prefSize.height/2 + drone.x/this.maxY * prefSize.height/2;
				
				g2.drawLine((int) (x - wx), (int) (y - wy), (int) (x + wx), (int) (y + wy));
				
				g2.drawLine((int) (x + wy/2), (int) (y - wx/2),
							(int) (x - wy/2), (int) (y + wx/2));
			}
			
		}
	}
	
}
