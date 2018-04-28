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
		contentPanel.add(droneUI.content);
		
		droneTable = new JTable(new DroneTable(drones));
		droneTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		droneTable.setColumnSelectionAllowed(false);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		droneTable.getColumnModel().getColumn(0).setCellRenderer(renderer);
		droneTable.getColumnModel().getColumn(0).setMaxWidth(35);
		droneTable.getColumnModel().getColumn(3).setMaxWidth(100);
		droneTable.getColumnModel().getColumn(3).setCellRenderer(renderer);
		droneTable.getTableHeader().setFont(droneTable.getTableHeader().getFont().deriveFont(Font.BOLD));
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
		}
	}
	
	
	private class DroneTable extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		
		private List<VirtualDrone> drones;
		
		
		public DroneTable(List<VirtualDrone> drones) {
			this.drones = drones;
		}
		
		
		public String getColumnName(int col) {
			return new String[] {"ID", "DroneId", "Location","Package"}[col];
		}
		
		public int getColumnCount() {
				return 4;
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
				return inputs == null? "": FloatMath.round(inputs.getX(), 2) + ", " +
										   FloatMath.round(inputs.getY(), 2) + ", " +
										   FloatMath.round(inputs.getZ(), 2);
					
			case 3:
				return "TODO";
				
			default:
				return null;
			}
		}
	}
}
