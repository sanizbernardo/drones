package testbed.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.joml.Vector3f;

import interfaces.AutopilotConfig;
import testbed.Physics;
import testbed.entities.airport.Airport;
import testbed.entities.packages.Package;
import testbed.world.helpers.DroneHelper;
import utils.Constants;

public class TestbedGui extends JFrame {

	private static final long serialVersionUID = 1L;
		
	private PackageTable packageTable;
	
	private MiniMap minimap;
	
	public TestbedGui(DroneHelper helper, List<Airport> airports) {		
		setTitle("Testbed GUI");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
        int ubuntuSiderBar = 0;
        int ubuntuHeader = 0;
        if (System.getProperty("os.name").contains("Linux")) {
        	ubuntuSiderBar = 105;
        	ubuntuHeader = 44;
        }
        
		setBounds(ubuntuSiderBar, 2 * ubuntuHeader, Constants.TESTBED_GUI_WIDTH, Constants.TESTBED_GUI_HEIGHT);
		JPanel contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		
		JScrollPane dronePane = new JScrollPane(new JTable(new DroneTable(helper)));
		dronePane.setPreferredSize(new Dimension(Constants.TESTBED_GUI_WIDTH, Constants.TESTBED_GUI_HEIGHT/3));
		contentPane.add(dronePane);
		
		packageTable = new PackageTable();
		JScrollPane packagePane = new JScrollPane(new JTable(packageTable));
		packagePane.setPreferredSize(new Dimension(Constants.TESTBED_GUI_WIDTH, Constants.TESTBED_GUI_HEIGHT/3));
		contentPane.add(packagePane);
		
				
		minimap = new MiniMap(2000, 2000, helper, airports);
		minimap.setPreferredSize(new Dimension(Constants.TESTBED_GUI_WIDTH, Constants.TESTBED_GUI_HEIGHT/3));
		contentPane.add(minimap);
	}
	
	
	public void showGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				pack();
				setVisible(true);
			}
		});
	}
	
	
	public void addPackage(Package pack) {
		packageTable.addPackage(pack);
	}
	
	
	public void setActiveDrone(int activeDrone) {
		minimap.setActiveDrone(activeDrone);
	}
	
	
	private class DroneTable extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		
		private DroneHelper helper;
		
		
		public DroneTable(DroneHelper helper) {
			this.helper = helper;
		}
		
		
		public String getColumnName(int col) {
			return new String[] {"ID","Drone", "Location","Package"}[col];
		}
		
		public int getColumnCount() {
				return 4;
		}

		public int getRowCount() {
			return this.helper.getMaxNbDrones();
		}
		
		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return "" + row;
				
			case 1:
				AutopilotConfig config = helper.getDroneConfig(row);
				return config == null? "": config.getDroneID();

			case 2:
				Physics physics = helper.getDronePhysics(row);
				return physics == null? "": physics.getAirport() == null? "In the air.": "At airport " +
										    physics.getAirportNb() + ", " + physics.getAirportLocoationDesc(); 
			case 3:
				return "TODO";
				
			default:
				return null;
			}
		}
		
	}
	
	
	private class PackageTable extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private List<Package> packages;
		
		
		public PackageTable() {
			packages = new ArrayList<>();
		}
		
		
		public void addPackage(Package pack) {
			this.packages.add(pack);
		}
		
		public String getColumnName(int col) {
			return new String[] {"Package Nb", "From", "To", "Status"}[col];
		}
		
		public int getColumnCount() {
			return 4;
		}

		public int getRowCount() {
			return packages.size();
		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return row;
				
			case 1:
				return "Airport " + packages.get(row).getFromAirport() + ", Gate " + packages.get(row).getFromGate();
				
			case 2:
				return "Airport " + packages.get(row).getDestAirport() + ", Gate " + packages.get(row).getDestGate();
			
			case 3:
				return packages.get(row).getStatusDesc();
			
			default:
				return null;
			}
		}
	}
	
	
	private class MiniMap extends Component {
		
		private static final long serialVersionUID = 1L;
		
		private final int maxX;
		private final int maxY;
		
		private int activeDrone;
		
		private DroneHelper helper;
		private List<Vector3f> airports;
		
		public MiniMap(int maxX, int maxZ, DroneHelper helper, List<Airport> airports) {			
			this.maxX = maxZ;
			this.maxY = maxX;
			
			this.helper = helper;
			this.airports = airports.stream().map(a -> a.getPosition()).collect(Collectors.toList());
		}
		
		public void setActiveDrone(int droneId) {
			this.activeDrone = droneId;
			this.repaint();
		}
		
		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			
			Dimension size = this.getSize();
			
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(2));
			
			g2.drawRect(0, 0, size.width-1, size.height-1);
			
			g2.setColor(Color.BLUE);
			g2.setStroke(new BasicStroke(1));
			
			for (Vector3f port: this.airports) {
				int x = (int) (size.width/2 - port.z/this.maxX * size.width/2),
					y = (int) (size.height/2 + port.x/this.maxY * size.height/2);
				
				g2.fillRect(x-6, y-6, 12, 12);
			}
			
			for (int drone: helper.droneIds.values()) {
				if (drone == activeDrone) 
					g2.setColor(Color.GREEN);
				else
					g2.setColor(Color.RED);
				
				Vector3f pos = helper.getDronePhysics(drone).getPosition();
				int x = (int) (size.width/2 - pos.z/this.maxX * size.width/2),
					y = (int) (size.height/2 + pos.x/this.maxY * size.height/2);
					
				g2.fillRect(x-3, y-3, 6, 6);
			}
		}
	}
	
}
