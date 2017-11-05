package gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

import datatypes.AutopilotConfig;
import utils.Constants;


public class ConfigSetupGUI extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
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
		int windowWidth = 650; // => 650
		int windowHeight = 300; // => 300
		
		
		
		setModalityType(ModalityType.APPLICATION_MODAL);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				generateConfig();
				setVisible(false);
				dispose();
			}
		});
		
		setBounds((screenWidth - windowWidth)/2, (screenHeight - windowHeight)/2, windowWidth, windowHeight);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel lblSimulationInitialisation = new JLabel("Simulation initialisation");
		lblSimulationInitialisation.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblSimulationInitialisation.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblSimulationInitialisation, BorderLayout.NORTH);
		
		setupFields();
		
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
	}

	/**
	 * Creating the config to be returned.
	 */
	private void generateConfig() {
		config = new AutopilotConfig() {
			public float getWingX() {
				return (float) wingSizeSpinner.getValue() / 4f;
			}
			
			public float getWingMass() {
				return (float) wingMassSpinner.getValue();
			}
			
			public float getWingLiftSlope() {
				return ((Double) wingLiftslopeSpinner.getValue()).floatValue();
			}
			
			public float getVerticalAngleOfView() {
				return (float) (Math.toRadians((int) verFOVSpinner.getValue()));
			}
			
			public float getVerStabLiftSlope() {
				return ((Double) verStabLiftslopeSpinner.getValue()).floatValue();
			}
			
			public float getTailSize() {
				return (float) tailSizeSpinner.getValue();
			}
			
			public float getTailMass() {
				return (float) tailMassSpinner.getValue();
			}
		
			public int getNbRows() {
				return (int) nbRowsSpinner.getValue();
			}
			
			public int getNbColumns() {
				return (int) nbColsSpinner.getValue();
			}
			
			public float getMaxThrust() {
				return (float) maxThrustSpinner.getValue();
			}
			
			public float getMaxAOA() {
				return (float) (Math.toRadians((int) maxAOASpinner.getValue()));
			}
			
			public float getHorizontalAngleOfView() {
				return (float) (Math.toRadians((int) horFOVSpinner.getValue()));
			}
			
			public float getHorStabLiftSlope() {
				return ((Double) horStabLiftslopeSpinner.getValue()).floatValue();
			}
			
			public float getGravity() {
				return ((Double) gravitySpinner.getValue()).floatValue();
			}
			
			public float getEngineMass() {
				return (float) engineMassSpinner.getValue();
			}
			
			public String toString() {
				return getWingX() + " " + getWingMass() + " " + getWingLiftSlope() + " " + getVerticalAngleOfView() + " " + getVerStabLiftSlope() + " "
						+ getTailSize() + " " + getTailMass() + " " + getNbRows() + " " + getNbColumns() + " " + getMaxThrust() + " " + getMaxAOA() + " "
						+ getHorizontalAngleOfView()  + " " + getHorStabLiftSlope() + " " + getGravity() + " " + getEngineMass();
			}
		};
	}

	
	public AutopilotConfig showDialog() {
		
		this.setVisible(true);
		
		return config;
	}
	
	/**
	 * Setting up all panels with their labels and spinners for all attributes.
	 */
	private void setupFields() {
		JPanel centerPanel = new JPanel();
		contentPane.add(centerPanel, BorderLayout.CENTER);
		GridBagLayout gbl_centerPanel = new GridBagLayout();
		gbl_centerPanel.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
		gbl_centerPanel.columnWeights = new double[]{1.0, 1.0, 1.0};
		centerPanel.setLayout(gbl_centerPanel);
		
		// gravity
		JPanel gravityPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) gravityPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_gravityPanel = new GridBagConstraints();
		gbc_gravityPanel.insets = new Insets(0, 0, 5, 0);
		gbc_gravityPanel.fill = GridBagConstraints.BOTH;
		gbc_gravityPanel.gridx = 0;
		gbc_gravityPanel.gridy = 0;
		centerPanel.add(gravityPanel, gbc_gravityPanel);
		
		JLabel lblGravity = new JLabel("Gravity");
		gravityPanel.add(lblGravity);
		
		gravitySpinner = new JSpinner();
		gravitySpinner.setPreferredSize(new Dimension(50, 20));
		gravitySpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_GRAVITY, 0f, 20f, 0.01f));
		gravityPanel.add(gravitySpinner);
		
		
		// wingX
		JPanel wingSizePanel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) wingSizePanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_wingSizePanel = new GridBagConstraints();
		gbc_wingSizePanel.insets = new Insets(0, 0, 5, 0);
		gbc_wingSizePanel.fill = GridBagConstraints.BOTH;
		gbc_wingSizePanel.gridx = 0;
		gbc_wingSizePanel.gridy = 1;
		centerPanel.add(wingSizePanel, gbc_wingSizePanel);
		
		JLabel lblWingSize = new JLabel("Wing span");
		wingSizePanel.add(lblWingSize);
		
		wingSizeSpinner = new JSpinner();
		wingSizeSpinner.setPreferredSize(new Dimension(50, 20));
		wingSizeSpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_WINGX*4, 1f, null, 0.1f));
		wingSizePanel.add(wingSizeSpinner);
		
		
		// tailsize
		JPanel tailSizePanel = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) tailSizePanel.getLayout();
		flowLayout_2.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_tailSizePanel = new GridBagConstraints();
		gbc_tailSizePanel.insets = new Insets(0, 0, 5, 0);
		gbc_tailSizePanel.fill = GridBagConstraints.BOTH;
		gbc_tailSizePanel.gridx = 0;
		gbc_tailSizePanel.gridy = 2;
		centerPanel.add(tailSizePanel, gbc_tailSizePanel);
		
		JLabel lblTailSize = new JLabel("Tail size");
		tailSizePanel.add(lblTailSize);
		
		tailSizeSpinner = new JSpinner();
		tailSizeSpinner.setPreferredSize(new Dimension(50, 20));
		tailSizeSpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_TAILSIZE, 1f, null, 0.1f));
		tailSizePanel.add(tailSizeSpinner);
		
		
		// engine mass
		JPanel engineMass = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) engineMass.getLayout();
		flowLayout_3.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_engineMass = new GridBagConstraints();
		gbc_engineMass.insets = new Insets(0, 0, 5, 0);
		gbc_engineMass.fill = GridBagConstraints.BOTH;
		gbc_engineMass.gridx = 0;
		gbc_engineMass.gridy = 3;
		centerPanel.add(engineMass, gbc_engineMass);
		
		JLabel lblEngineMass = new JLabel("Engine mass");
		engineMass.add(lblEngineMass);
		
		engineMassSpinner = new JSpinner();
		engineMassSpinner.setPreferredSize(new Dimension(50, 20));
		engineMassSpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_ENGINE_MASS, 1f, null, 1f));
		engineMass.add(engineMassSpinner);
		
		
		// wing mass
		JPanel wingMassPanel = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) wingMassPanel.getLayout();
		flowLayout_4.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_wingMassPanel = new GridBagConstraints();
		gbc_wingMassPanel.insets = new Insets(0, 0, 5, 0);
		gbc_wingMassPanel.fill = GridBagConstraints.BOTH;
		gbc_wingMassPanel.gridx = 0;
		gbc_wingMassPanel.gridy = 4;
		centerPanel.add(wingMassPanel, gbc_wingMassPanel);
		
		JLabel lblWingMass = new JLabel("Wing mass");
		wingMassPanel.add(lblWingMass);
		
		wingMassSpinner = new JSpinner();
		wingMassSpinner.setPreferredSize(new Dimension(50, 20));
		wingMassSpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_WING_MASS, 1f, null, 1f));
		wingMassPanel.add(wingMassSpinner);
		
		
		// tail mass
		JPanel tailMassPanel = new JPanel();
		FlowLayout flowLayout_5 = (FlowLayout) tailMassPanel.getLayout();
		flowLayout_5.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_tailMassPanel = new GridBagConstraints();
		gbc_tailMassPanel.insets = new Insets(0, 0, 5, 0);
		gbc_tailMassPanel.fill = GridBagConstraints.BOTH;
		gbc_tailMassPanel.gridx = 1;
		gbc_tailMassPanel.gridy = 0;
		centerPanel.add(tailMassPanel, gbc_tailMassPanel);
		
		JLabel lblTailMass = new JLabel("Tail mass");
		tailMassPanel.add(lblTailMass);
		
		tailMassSpinner = new JSpinner();
		tailMassSpinner.setPreferredSize(new Dimension(50, 20));
		tailMassSpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_TAIL_MASS, 1f, null, 1f));
		tailMassPanel.add(tailMassSpinner);
		
		
		// max thrust
		JPanel maxThrustPanel = new JPanel();
		FlowLayout flowLayout_6 = (FlowLayout) maxThrustPanel.getLayout();
		flowLayout_6.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_maxThrustPanel = new GridBagConstraints();
		gbc_maxThrustPanel.insets = new Insets(0, 0, 5, 0);
		gbc_maxThrustPanel.fill = GridBagConstraints.BOTH;
		gbc_maxThrustPanel.gridx = 1;
		gbc_maxThrustPanel.gridy = 1;
		centerPanel.add(maxThrustPanel, gbc_maxThrustPanel);
		
		JLabel lblMaxThrust = new JLabel("Maximum thrust");
		maxThrustPanel.add(lblMaxThrust);
		
		maxThrustSpinner = new JSpinner();
		maxThrustSpinner.setPreferredSize(new Dimension(80, 20));
		float defaultMaxThrust = (Constants.DEFAULT_TAIL_MASS + Constants.DEFAULT_ENGINE_MASS + Constants.DEFAULT_WING_MASS * 2f) * 0.35f * 1000f;
		maxThrustSpinner.setModel(new SpinnerNumberModel(defaultMaxThrust, 1f, null, 100f));
		maxThrustPanel.add(maxThrustSpinner);
		
		
		// max AOA
		JPanel maxAOAPanel = new JPanel();
		FlowLayout flowLayout_7 = (FlowLayout) maxAOAPanel.getLayout();
		flowLayout_7.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_maxAOAPanel = new GridBagConstraints();
		gbc_maxAOAPanel.insets = new Insets(0, 0, 5, 0);
		gbc_maxAOAPanel.fill = GridBagConstraints.BOTH;
		gbc_maxAOAPanel.gridx = 1;
		gbc_maxAOAPanel.gridy = 2;
		centerPanel.add(maxAOAPanel, gbc_maxAOAPanel);
		
		JLabel lblMaxAOA = new JLabel("Maximum AOA");
		maxAOAPanel.add(lblMaxAOA);
		
		maxAOASpinner = new JSpinner();
		maxAOASpinner.setPreferredSize(new Dimension(50, 20));
		maxAOASpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_MAX_AOA, 45, 90, 1));
		maxAOAPanel.add(maxAOASpinner);
		
		
		// wing liftslope
		JPanel wingLiftslopePanel = new JPanel();
		FlowLayout flowLayout_8 = (FlowLayout) wingLiftslopePanel.getLayout();
		flowLayout_8.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_wingLiftslopePanel = new GridBagConstraints();
		gbc_wingLiftslopePanel.insets = new Insets(0, 0, 5, 0);
		gbc_wingLiftslopePanel.fill = GridBagConstraints.BOTH;
		gbc_wingLiftslopePanel.gridx = 1;
		gbc_wingLiftslopePanel.gridy = 3;
		centerPanel.add(wingLiftslopePanel, gbc_wingLiftslopePanel);
		
		JLabel lblWingLiftslope = new JLabel("Wing liftslope");
		wingLiftslopePanel.add(lblWingLiftslope);
		
		wingLiftslopeSpinner = new JSpinner();
		wingLiftslopeSpinner.setPreferredSize(new Dimension(50, 20));
		wingLiftslopeSpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_WING_LIFTSLOPE, 0f, 1f, 0.01f));
		wingLiftslopePanel.add(wingLiftslopeSpinner);
		
		
		// verstab liftslope
		JPanel verStabLiftslopePanel = new JPanel();
		FlowLayout flowLayout_10 = (FlowLayout) verStabLiftslopePanel.getLayout();
		flowLayout_10.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_verStabLiftslopePanel = new GridBagConstraints();
		gbc_verStabLiftslopePanel.insets = new Insets(0, 0, 5, 0);
		gbc_verStabLiftslopePanel.fill = GridBagConstraints.BOTH;
		gbc_verStabLiftslopePanel.gridx = 1;
		gbc_verStabLiftslopePanel.gridy = 4;
		centerPanel.add(verStabLiftslopePanel, gbc_verStabLiftslopePanel);
		
		JLabel lblVerStabLiftslope = new JLabel("Vertical stabiliser liftslope");
		verStabLiftslopePanel.add(lblVerStabLiftslope);
		
		verStabLiftslopeSpinner = new JSpinner();
		verStabLiftslopeSpinner.setPreferredSize(new Dimension(50, 20));
		verStabLiftslopeSpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_VER_STAB_LIFTSLOPE, 0f, 1f, 0.01f));
		verStabLiftslopePanel.add(verStabLiftslopeSpinner);
		
		
		// horstab liftslope
		JPanel horStabLiftslopePanel = new JPanel();
		FlowLayout flowLayout_9 = (FlowLayout) horStabLiftslopePanel.getLayout();
		flowLayout_9.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_horStabLiftslopePanel = new GridBagConstraints();
		gbc_horStabLiftslopePanel.insets = new Insets(0, 0, 5, 0);
		gbc_horStabLiftslopePanel.fill = GridBagConstraints.BOTH;
		gbc_horStabLiftslopePanel.gridx = 2;
		gbc_horStabLiftslopePanel.gridy = 0;
		centerPanel.add(horStabLiftslopePanel, gbc_horStabLiftslopePanel);
		
		JLabel lblHorStabLiftslope = new JLabel("Horizontal stabilizer liftslope");
		horStabLiftslopePanel.add(lblHorStabLiftslope);
		
		horStabLiftslopeSpinner = new JSpinner();
		horStabLiftslopeSpinner.setPreferredSize(new Dimension(50, 20));
		horStabLiftslopeSpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_VER_STAB_LIFTSLOPE, 0f, 1f, 0.01f));
		horStabLiftslopePanel.add(horStabLiftslopeSpinner);
		
		
		// ver fov
		JPanel verFOVPanel = new JPanel();
		FlowLayout flowLayout_11 = (FlowLayout) verFOVPanel.getLayout();
		flowLayout_11.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_verFOVPanel = new GridBagConstraints();
		gbc_verFOVPanel.insets = new Insets(0, 0, 5, 0);
		gbc_verFOVPanel.fill = GridBagConstraints.BOTH;
		gbc_verFOVPanel.gridx = 2;
		gbc_verFOVPanel.gridy = 1;
		centerPanel.add(verFOVPanel, gbc_verFOVPanel);
		
		JLabel lblVerFOV = new JLabel("Drone camera vertical FOV");
		verFOVPanel.add(lblVerFOV);
		
		verFOVSpinner = new JSpinner();
		verFOVSpinner.setPreferredSize(new Dimension(50, 20));
		verFOVSpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_VER_FOV, 0, 180, 1));
		verFOVPanel.add(verFOVSpinner);
		
		
		// hor fov
		JPanel horFOVPanel = new JPanel();
		FlowLayout flowLayout_12 = (FlowLayout) horFOVPanel.getLayout();
		flowLayout_12.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_horFOVPanel = new GridBagConstraints();
		gbc_horFOVPanel.insets = new Insets(0, 0, 5, 0);
		gbc_horFOVPanel.fill = GridBagConstraints.BOTH;
		gbc_horFOVPanel.gridx = 2;
		gbc_horFOVPanel.gridy = 2;
		centerPanel.add(horFOVPanel, gbc_horFOVPanel);
		
		JLabel lblHorFOV = new JLabel("Drone camera horizontal FOV");
		horFOVPanel.add(lblHorFOV);
		
		horFOVSpinner = new JSpinner();
		horFOVSpinner.setPreferredSize(new Dimension(50, 20));
		horFOVSpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_HOR_FOV, 0, 180, 1));
		horFOVPanel.add(horFOVSpinner);
		
		
		// nb cols
		JPanel nbColsPanel = new JPanel();
		FlowLayout flowLayout_13 = (FlowLayout) nbColsPanel.getLayout();
		flowLayout_13.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_nbColsPanel = new GridBagConstraints();
		gbc_nbColsPanel.insets = new Insets(0, 0, 5, 0);
		gbc_nbColsPanel.fill = GridBagConstraints.BOTH;
		gbc_nbColsPanel.gridx = 2;
		gbc_nbColsPanel.gridy = 3;
		centerPanel.add(nbColsPanel, gbc_nbColsPanel);
		
		JLabel lblNbCols = new JLabel("# columns in drone camera utils.image");
		nbColsPanel.add(lblNbCols);
		
		nbColsSpinner = new JSpinner();
		nbColsSpinner.setPreferredSize(new Dimension(50, 20));
		nbColsSpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_NB_COLS, 100, null, 10));
		nbColsPanel.add(nbColsSpinner);
		
		
		// nb rows
		JPanel nbRowsPanel = new JPanel();
		FlowLayout flowLayout_14 = (FlowLayout) nbRowsPanel.getLayout();
		flowLayout_14.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_nbRowsPanel = new GridBagConstraints();
		gbc_nbRowsPanel.fill = GridBagConstraints.BOTH;
		gbc_nbRowsPanel.gridx = 2;
		gbc_nbRowsPanel.gridy = 4;
		centerPanel.add(nbRowsPanel, gbc_nbRowsPanel);
		
		JLabel lblNbRows = new JLabel("# rows in drone camera utils.image");
		nbRowsPanel.add(lblNbRows);
		
		nbRowsSpinner = new JSpinner();
		nbRowsSpinner.setPreferredSize(new Dimension(50, 20));
		nbRowsSpinner.setModel(new SpinnerNumberModel(Constants.DEFAULT_NB_ROWS, 100, null, 10));
		nbRowsPanel.add(nbRowsSpinner);
	}

}
