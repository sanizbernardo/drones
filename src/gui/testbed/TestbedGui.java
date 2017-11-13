package gui.testbed;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.joml.Vector3f;

import javax.swing.JLabel;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;

public class TestbedGui extends JFrame {

	private JPanel contentPane;
	private final JLabel velocityLbl, positionLbl, yawLbl, rollLbl, pitchLbl;

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
		
		setTitle("Testbed GUI");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 167, 211);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));
		
		velocityLbl = new JLabel("Velocity: 0");
		contentPane.add(velocityLbl);
		
		positionLbl = new JLabel("Position: 0");
		contentPane.add(positionLbl);
		
		yawLbl = new JLabel("Yaw: 0");
		contentPane.add(yawLbl);
		
		rollLbl = new JLabel("Roll: 0");
		contentPane.add(rollLbl);
		
		pitchLbl = new JLabel("Pitch: 0");
		contentPane.add(pitchLbl);

		
	}
	
	public void showGUI() {
		setVisible(true);
	}
	
	public void update(Vector3f velocity, Vector3f position, Vector3f orientation) {
		String velocityVal = String.valueOf( Math.round(Math.sqrt(velocity.dot(velocity)) * 10000f) / 10000f );
		velocityLbl.setText("Velocity: " + velocityVal);
		
		positionLbl.setText("Position:" + '\n' +  "x: " + position.x + "\n y:"+ position.y + "\n z:" +  position.z);
		
		rollLbl.setText("Roll: " + Math.toDegrees(orientation.z));
		yawLbl.setText("Yaw: " + Math.toDegrees(orientation.y));
		pitchLbl.setText("Pitch: " + Math.toDegrees(orientation.x));

	}
}
