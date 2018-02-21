package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.joml.Vector3f;

import utils.Constants;
import utils.FloatMath;

import javax.swing.JLabel;

import java.awt.Insets;

public class TestbedGui extends JFrame {


	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JLabel[] position, velocity, orientation;

	private final int precision;
	
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
        if (System.getProperty("os.name").equals("Linux")) {
        	ubuntuSiderBar = 105;
        	ubuntuHeader = 44;
        }
        
		setBounds(Constants.AUTOPILOT_GUI_WIDTH - Constants.TESTBED_GUI_WIDTH + ubuntuSiderBar, Constants.AUTOPILOT_GUI_HEIGHT +  2 * ubuntuHeader, Constants.TESTBED_GUI_WIDTH, Constants.TESTBED_GUI_HEIGHT);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new GridBagLayout());
		
		JLabel title = new JLabel("Drone statistics");
		GridBagConstraints gbc_title = GuiUtils.buildGBC(0, 0, GridBagConstraints.CENTER, new Insets(5, 0, 5, 0));
		gbc_title.gridwidth = 3;
		contentPane.add(title, gbc_title);
		
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setPreferredSize(new Dimension(Constants.TESTBED_GUI_WIDTH, 5));
		GridBagConstraints gbc_sep = GuiUtils.buildGBC(0, 1, GridBagConstraints.CENTER, new Insets(3, 0, 3, 0));
		gbc_sep.gridwidth = 3;
		contentPane.add(sep, gbc_sep);
		
		
		position = buildVectorLbl("Position", new String[] {"x", "y", "z"}, 2, false); 
		
		velocity = buildVectorLbl("Velocity", new String[] {"x", "y", "z"} , 6, true);
		
		orientation = buildVectorLbl("Orientation", new String[] {"pitch", "heading", "roll"}, 11, false);

	}
	
	
	private JLabel[] buildVectorLbl(String title, String[] componentLbl, int y, boolean norm) {
		JLabel lbl = new JLabel(title);
		GridBagConstraints gbc_lbl = GuiUtils.buildGBC(0, y, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5));
		gbc_lbl.gridheight = 3;
		contentPane.add(lbl, gbc_lbl);
		
		JLabel[] numbers = new JLabel[(norm ? 4: 3)];  
		for (int i = 0; i < 3; i++) {
			JLabel nbLbl = new JLabel(componentLbl[i] + ": ");
			contentPane.add(nbLbl, GuiUtils.buildGBC(1, y + i, GridBagConstraints.CENTER, new Insets(0, 10, 0, 10)));
			numbers[i] = new JLabel("0");
			contentPane.add(numbers[i], GuiUtils.buildGBC(2, y + i, GridBagConstraints.WEST));
		}
		
		if (norm) {
			JLabel normLbl = new JLabel("norm:");
			contentPane.add(normLbl, GuiUtils.buildGBC(1, y+3, GridBagConstraints.CENTER, new Insets(0, 10, 0, 10)));
			numbers[3] = new JLabel("0");
			contentPane.add(numbers[3], GuiUtils.buildGBC(2, y+3, GridBagConstraints.WEST));
		}
		
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setPreferredSize(new Dimension(Constants.TESTBED_GUI_WIDTH, 5));
		GridBagConstraints gbc_sep = GuiUtils.buildGBC(0, y + (norm ? 4 : 3), GridBagConstraints.CENTER, new Insets(3, 0, 3, 0));
		gbc_sep.gridwidth = 3;
		contentPane.add(sep, gbc_sep);
		
		return numbers;
	}
	
	
	
	
	public void showGUI() {
		pack();
		setVisible(true);
	}
	
	public void update(Vector3f velocity, Vector3f position, float heading, float pitch, float roll) {
	    this.position[0].setText("" + FloatMath.round(position.x, precision));
	    this.position[1].setText("" + FloatMath.round(position.y, precision));
	    this.position[2].setText("" + FloatMath.round(position.z, precision));
		
	    this.velocity[0].setText("" + FloatMath.round(velocity.x, precision));
	    this.velocity[1].setText("" + FloatMath.round(velocity.y, precision));
	    this.velocity[2].setText("" + FloatMath.round(velocity.z, precision));
	    this.velocity[3].setText("" + FloatMath.round(FloatMath.norm(velocity), precision));
		
	    this.orientation[0].setText("" + FloatMath.round(pitch, precision));
	    this.orientation[1].setText("" + FloatMath.round(heading, precision));
	    this.orientation[2].setText("" + FloatMath.round(roll, precision));
	}
	
	
}
