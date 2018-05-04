package autopilot.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import autopilot.airports.VirtualDrone;
import autopilot.airports.VirtualPackage;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import utils.FloatMath;

public class AutopilotGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private DroneControlUI droneUI;
	
	private JTable droneTable;
	
	public AutopilotGUI(List<VirtualDrone> drones) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 500, 500);
		setTitle("Autopilot");

		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		setContentPane(contentPanel);
		
		droneUI = new DroneControlUI(drones);
		droneUI.setSelected(0);
		contentPanel.add(droneUI.content);
		
		droneTable = new JTable(new DroneTable(drones));
		droneTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		droneTable.setColumnSelectionAllowed(false);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		droneTable.getColumnModel().getColumn(0).setCellRenderer(renderer);
		droneTable.getColumnModel().getColumn(0).setMaxWidth(35);
		droneTable.getColumnModel().getColumn(2).setCellRenderer(renderer);
		droneTable.getColumnModel().getColumn(4).setMaxWidth(100);
		droneTable.getColumnModel().getColumn(4).setCellRenderer(renderer);
		droneTable.getTableHeader().setFont(droneTable.getTableHeader().getFont().deriveFont(Font.BOLD));
		droneTable.addRowSelectionInterval(0, 0);
		droneTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			private int lastId;
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				
				int newId;
				if (e.getFirstIndex() == lastId)
					newId = e.getLastIndex();
				else
					newId = e.getFirstIndex();
				lastId = newId;
				droneUI.setSelected(newId);
			}
		});
		JScrollPane dronePane = new JScrollPane(droneTable);
		dronePane.setPreferredSize(new Dimension(500, 300));
		contentPanel.add(dronePane);
		
	}
	
	
	public void showGUI() {
		pack();
		setVisible(true);
	}
	
	public void setTask(String task) {
		droneUI.setTask(task);
	}
	
	public boolean manualControl(int droneId) {
		return droneUI.getManual(droneId);
	}
	
	public AutopilotOutputs getOutputs() {
		return droneUI.getOutputs();
	}
	
	public void updateDrones() {
		((DroneTable) droneTable.getModel()).fireTableDataChanged();;
	}
	
	public void updateOutputs() {
		droneUI.updateOutputs();
		
		for (int i = 0; i < droneTable.getRowCount(); i++) {
			((DroneTable) droneTable.getModel()).fireTableCellUpdated(i, 2);
			((DroneTable) droneTable.getModel()).fireTableCellUpdated(i, 3);
		}
	}
	
	
	private class DroneTable extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		
		private List<VirtualDrone> drones;
		
		
		public DroneTable(List<VirtualDrone> drones) {
			this.drones = drones;
		}
		
		
		public String getColumnName(int col) {
			return new String[] {"ID", "DroneId", "Location", "Task", "Target", "Package"}[col];
		}
		
		public int getColumnCount() {
				return 6;
		}

		public int getRowCount() {
			return this.drones.size();
		}
		
		public String getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return "" + row;
				
			case 1:
				AutopilotConfig config = drones.get(row).getConfig();
				return config == null? "": config.getDroneID();

			case 2:
				AutopilotInputs inputs = drones.get(row).getInputs();
				return inputs == null? "": (int) inputs.getX() + ", " +
										   (int) inputs.getY() + ", " +
										   (int) inputs.getZ();
					
			case 3:
				return drones.get(row).getTask();
			
			case 4:
				return "TODO";

			case 5:
				return "TODO";
				
			default:
				return null;
			}
		}
	}
	
	
	private class PackageTable extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		
		private List<VirtualPackage> packages;
		
		public PackageTable(List<VirtualPackage> packages) {
			this.packages = packages;
		}
		
		public String getColumnName(int col) {
			return new String[] {"ID", "From", "To", "Status", "Assigned To"}[col];
		}
		
		public int getColumnCount() {
			return 5;
		}

		public int getRowCount() {
			return packages.size();
		}

		@Override
		public String getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return "" + row;
				
			case 1:
				return "Airport " + packages.get(row).getFromAirport() + ", "
								  + packages.get(row).getFromGate();
			
			case 2:
				return "Airport " + packages.get(row).getToAirport() + ", "
				  				  + packages.get(row).getToGate();
			
			case 3:
				return packages.get(row).getStatus();
			
			case 4:
				return "TODO";
				
			default:
				return "";
			}
		}
		
	}
}
