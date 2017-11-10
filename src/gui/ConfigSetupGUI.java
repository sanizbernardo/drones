package gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

import datatypes.AutopilotConfig;
import utils.Constants;


public class ConfigSetupGUI extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel spinnerPanel;
	private JSpinner 	gravitySpinner, wingSizeSpinner, tailSizeSpinner, engineMassSpinner, 
						wingMassSpinner, tailMassSpinner, maxThrustSpinner, maxAOASpinner, 
						wingLiftslopeSpinner, verStabLiftslopeSpinner, horStabLiftslopeSpinner,
						verFOVSpinner, horFOVSpinner, nbColsSpinner, nbRowsSpinner;
	
	
	private AutopilotConfig config;

	
	public static void main(String[] args) {
		ConfigSetupGUI dlg = new ConfigSetupGUI();
		System.out.println(dlg.showDialog());
	}

	
	/**
	 * Create the frame.
	 */
	public ConfigSetupGUI() {
		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screen.getWidth();
		int screenHeight = (int) screen.getHeight();
		int windowWidth = 700; // => 650
		int windowHeight = 350; // => 300
		setBounds((screenWidth - windowWidth)/2, (screenHeight - windowHeight)/2, windowWidth, windowHeight);
		
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				generateConfig();
				setVisible(false);
				dispose();
			}
		});
		
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		
		JLabel lblSimulationInitialisation = new JLabel("Simulation initialisation");
		contentPane.add(lblSimulationInitialisation, BorderLayout.NORTH);
		lblSimulationInitialisation.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblSimulationInitialisation.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		JPanel btnPanel = new JPanel();
		contentPane.add(btnPanel, BorderLayout.SOUTH);
		JButton btnStart = new JButton("Start simulation");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				generateConfig();
				setVisible(false);
				dispose();
			}
		});
		btnPanel.add(btnStart);
		
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		
		JPanel inputPanel = new JPanel();
		tabbedPane.addTab("Config", inputPanel);
		inputPanel.setLayout(new BorderLayout(0, 0));
		
		
		spinnerPanel = new JPanel();
		inputPanel.add(spinnerPanel, BorderLayout.CENTER);
		GridBagLayout gbl_fieldsPanel = new GridBagLayout();
		gbl_fieldsPanel.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
		gbl_fieldsPanel.columnWeights = new double[]{1.0, 1.0, 1.0};
		spinnerPanel.setLayout(gbl_fieldsPanel);
		buildFieldsPanel();
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Worldgen", panel);
		
	}
	
	public AutopilotConfig showDialog() {
		this.setVisible(true);
		return config;
	}

	/**
	 * Creating the config to be returned.
	 */
	private void generateConfig() {
		config = new AutopilotConfig() {
			public float getWingX() {return (float) wingSizeSpinner.getValue() / 4f;}
			
			public float getWingMass() {return (float) wingMassSpinner.getValue();}
			
			public float getWingLiftSlope() {return ((Double) wingLiftslopeSpinner.getValue()).floatValue();}
			
			public float getVerticalAngleOfView() {return (float) (Math.toRadians((int) verFOVSpinner.getValue()));}
			
			public float getVerStabLiftSlope() {return ((Double) verStabLiftslopeSpinner.getValue()).floatValue();}
			
			public float getTailSize() {return (float) tailSizeSpinner.getValue();}
			
			public float getTailMass() {return (float) tailMassSpinner.getValue();}
			
			public int getNbRows() {return (int) nbRowsSpinner.getValue();}
			
			public int getNbColumns() {return (int) nbColsSpinner.getValue();}
			
			public float getMaxThrust() {return (float) maxThrustSpinner.getValue();}
			
			public float getMaxAOA() {return (float) (Math.toRadians((int) maxAOASpinner.getValue()));}
			
			public float getHorizontalAngleOfView() {return (float) (Math.toRadians((int) horFOVSpinner.getValue()));}
			
			public float getHorStabLiftSlope() {return ((Double) horStabLiftslopeSpinner.getValue()).floatValue();}
			
			public float getGravity() {return ((Double) gravitySpinner.getValue()).floatValue();}
			
			public float getEngineMass() {return (float) engineMassSpinner.getValue();}
		};
	}
	
	
	private void buildFieldsPanel() {
		gravitySpinner = GuiUtils.buildSpinner(spinnerPanel, "Gravity", 0, 0, Constants.DEFAULT_GRAVITY, 0f, 30f, 0.01f);

		wingSizeSpinner = GuiUtils.buildSpinner(spinnerPanel, "Wing size", 0, 1, Constants.DEFAULT_WINGX * 4, 0f, 50f, 0.1f);
		
		tailSizeSpinner = GuiUtils.buildSpinner(spinnerPanel, "Tail size", 0, 2, Constants.DEFAULT_TAILSIZE, 0f, 50f, 0.1f);

		engineMassSpinner = GuiUtils.buildSpinner(spinnerPanel, "Engine mass", 0, 3, Constants.DEFAULT_ENGINE_MASS, 0f, 500f, 1f);

		wingMassSpinner = GuiUtils.buildSpinner(spinnerPanel, "Wing mass", 0, 4, Constants.DEFAULT_WING_MASS, 0f, 500f, 1f);

		tailMassSpinner = GuiUtils.buildSpinner(spinnerPanel, "Tail mass", 1, 0, Constants.DEFAULT_TAIL_MASS, 0f, 500f, 1f);

		float defaultMaxThrust = (Constants.DEFAULT_TAIL_MASS + Constants.DEFAULT_ENGINE_MASS + Constants.DEFAULT_WING_MASS * 2f) * 0.35f * 1000f;
		maxThrustSpinner = GuiUtils.buildSpinner(spinnerPanel, "Maximum thrust", 1, 1, defaultMaxThrust, 0f, 1000000f, 1000f);

		maxAOASpinner = GuiUtils.buildSpinner(spinnerPanel, "Maximum AOA", 1, 2, Constants.DEFAULT_MAX_AOA, 0, 90, 1);

		wingLiftslopeSpinner = GuiUtils.buildSpinner(spinnerPanel, "Wing liftslope", 1, 3, Constants.DEFAULT_WING_LIFTSLOPE, 0f, 20f, 0.01f);
		
		verStabLiftslopeSpinner = GuiUtils.buildSpinner(spinnerPanel, "Vertical stabiliser liftslope", 1, 4, Constants.DEFAULT_VER_STAB_LIFTSLOPE, 0f, 20f, 0.01f);

		horStabLiftslopeSpinner = GuiUtils.buildSpinner(spinnerPanel, "Horizontal stabilizer liftslope", 2, 0, Constants.DEFAULT_HOR_STAB_LIFTSLOPE, 0f, 20f, 0.01f);

		verFOVSpinner = GuiUtils.buildSpinner(spinnerPanel, "Drone camera vertical FOV", 2, 1, Constants.DEFAULT_VER_FOV, 0, 180, 1);
		
		horFOVSpinner = GuiUtils.buildSpinner(spinnerPanel, "Drone camera horizontal FOV", 2, 2, Constants.DEFAULT_HOR_FOV, 0, 180, 1);		
		
		nbColsSpinner = GuiUtils.buildSpinner(spinnerPanel, "# columns in drone camera image", 2, 3, Constants.DEFAULT_NB_COLS, 0, 800, 1);				
		
		nbRowsSpinner = GuiUtils.buildSpinner(spinnerPanel, "# rows in drone camera image", 2, 4, Constants.DEFAULT_NB_ROWS, 0, 800, 1);
	}

}
