package autopilot.gui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import autopilot.airports.VirtualDrone;
import interfaces.AutopilotConfig;
import javax.swing.JLabel;

public class AutopilotGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private DroneControlUI droneUI;
	
	public AutopilotGUI(List<VirtualDrone> drones) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 500, 500);
		setTitle("Autopilot");

		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		setContentPane(contentPanel);
		
		this.droneUI = new DroneControlUI(drones);
		contentPanel.add(droneUI.content);
		
		
		
		
	}
	
	
	public void showGUI() {
		pack();
		setVisible(true);
	}
	
	public void setTask(String task) {
		droneUI.setTask(task);
	}
	
	public boolean manualControl() {
		return droneUI.getManual();
	}
}
