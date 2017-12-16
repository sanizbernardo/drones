package gui.testbed;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.joml.Vector3f;

import javax.swing.JLabel;

import java.awt.GridLayout;

public class TestbedGui extends JFrame {


	private static final long serialVersionUID = 1L;
	
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
		setBounds(30, 500, 200, 350);
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
	
	public void update(Vector3f velocity, Vector3f position, float heading, float pitch, float roll) {
	    double precision = 2;
	    double prec = Math.pow(10, precision);
	    
		String velocityVal = String.valueOf( precision(Math.sqrt(velocity.dot(velocity)), prec) );
		velocityLbl.setText("Velocity: " + velocityVal);
		
		positionLbl.setText("<html>Position:" + '\n' +  " x: " + precision(position.x, prec)
														+ "<br>" +"\n y:"+ precision(position.y, prec) 
														+ "<br>" +  "\n z:" +  precision(position.z, prec) + "</html>");
		
		rollLbl.setText("Roll: " + precision( Math.toDegrees(roll), prec) );
		yawLbl.setText("Heading: " + precision( Math.toDegrees(heading), prec));
		pitchLbl.setText("Pitch: " + precision(Math.toDegrees(pitch), prec));

	}
	
	public double precision(double value, double precision) {
		return Math.round(value * precision) / precision;
	}
	
	
}
