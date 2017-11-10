package gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

import datatypes.AutopilotConfig;
import utils.Constants;
import world.World;


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
		
		
		JPanel configPanel = new JPanel();
		tabbedPane.addTab("Config", configPanel);
		configPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblConfig = new JLabel("Autopilot config settings");
		configPanel.add(lblConfig, BorderLayout.NORTH);
		
		
		spinnerPanel = new JPanel();
		configPanel.add(spinnerPanel, BorderLayout.CENTER);
		GridBagLayout gbl_fieldsPanel = new GridBagLayout();
		gbl_fieldsPanel.rowWeights = new double[]{};
		gbl_fieldsPanel.columnWeights = new double[]{};
		spinnerPanel.setLayout(gbl_fieldsPanel);
		buildFieldsPanel();
		
		JPanel worldGenPanel = new JPanel();
		tabbedPane.addTab("Worldgen", worldGenPanel);
		worldGenPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblWorldgen = new JLabel("World generation settings");
		lblWorldgen.setHorizontalAlignment(SwingConstants.LEFT);
		worldGenPanel.add(lblWorldgen, BorderLayout.NORTH);
		
		JPanel selectorPanel = new JPanel();
		selectorPanel.setLayout(new BorderLayout(0, 0));
		worldGenPanel.add(selectorPanel, BorderLayout.CENTER);
		
		JComboBox<String> comboBox = new JComboBox<String>();
		DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>(new String[] {"test", "sec", "lul"});
		comboBox.setModel(comboBoxModel);
		selectorPanel.add(comboBox, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		selectorPanel.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lbl = new JLabel();
		lbl.setText("<html>"+"This setting wil generate a world containing a specified amount of cubes,"
				+ "randomly generated in the specified region in a uniform way." + "</html>");
		panel.add(lbl, BorderLayout.NORTH);
		
		JPanel inputPanel = new JPanel();
		panel.add(inputPanel, BorderLayout.CENTER);
		GridBagLayout gbl_inputPanel = new GridBagLayout();
		gbl_inputPanel.columnWidths = new int[] {150};
		gbl_inputPanel.columnWeights = new double[] {0.0, 0.0, 0.0, 1.0};
		inputPanel.setLayout(gbl_inputPanel);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(5, 0, 5, 0);
		gbc_separator.fill = GridBagConstraints.BOTH;
		gbc_separator.gridwidth = 4;
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 0;
		inputPanel.add(separator, gbc_separator);
		
		JLabel lblnbCubes = new JLabel("Amount of cubes:");
		GridBagConstraints gbc_lblnbCubes = new GridBagConstraints();
		gbc_lblnbCubes.anchor = GridBagConstraints.EAST;
		gbc_lblnbCubes.insets = new Insets(0, 0, 5, 5);
		gbc_lblnbCubes.gridx = 0;
		gbc_lblnbCubes.gridy = 1;
		inputPanel.add(lblnbCubes, gbc_lblnbCubes);
		
		JSpinner nbCubesSpinner = new JSpinner();
		nbCubesSpinner.setModel(new SpinnerNumberModel(200, 0, 10000, 1));
		GridBagConstraints gbc_nbCubesSpinner = new GridBagConstraints();
		gbc_nbCubesSpinner.gridwidth = 3;
		gbc_nbCubesSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_nbCubesSpinner.anchor = GridBagConstraints.WEST;
		gbc_nbCubesSpinner.gridx = 1;
		gbc_nbCubesSpinner.gridy = 1;
		inputPanel.add(nbCubesSpinner, gbc_nbCubesSpinner);
		
		JLabel lblXRange = new JLabel("X-Range:");
		GridBagConstraints gbc_lblXRange = new GridBagConstraints();
		gbc_lblXRange.anchor = GridBagConstraints.EAST;
		gbc_lblXRange.insets = new Insets(0, 0, 5, 5);
		gbc_lblXRange.gridx = 0;
		gbc_lblXRange.gridy = 2;
		inputPanel.add(lblXRange, gbc_lblXRange);
		
		JSpinner xMinSpinner = new JSpinner();
		xMinSpinner.setModel(new SpinnerNumberModel(-100, -1000, 1000, 10));
		GridBagConstraints gbc_xMinSpinner = new GridBagConstraints();
		gbc_xMinSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_xMinSpinner.anchor = GridBagConstraints.WEST;
		gbc_xMinSpinner.gridx = 1;
		gbc_xMinSpinner.gridy = 2;
		inputPanel.add(xMinSpinner, gbc_xMinSpinner);
		
		JLabel lblXRangeSep = new JLabel(" - ");
		GridBagConstraints gbc_lblXRangeSep = new GridBagConstraints();
		gbc_lblXRangeSep.anchor = GridBagConstraints.WEST;
		gbc_lblXRangeSep.gridx = 2;
		gbc_lblXRangeSep.gridy = 2;
		inputPanel.add(lblXRangeSep, gbc_lblXRangeSep);
		
		JSpinner xMaxSpinner = new JSpinner();
		xMaxSpinner.setModel(new SpinnerNumberModel(100, -1000, 1000, 10));
		GridBagConstraints gbc_xMaxSpinner = new GridBagConstraints();
		gbc_xMaxSpinner.anchor = GridBagConstraints.WEST;
		gbc_xMaxSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_xMaxSpinner.gridx = 3;
		gbc_xMaxSpinner.gridy = 2;
		inputPanel.add(xMaxSpinner, gbc_xMaxSpinner);
		
		JLabel lblYRange = new JLabel("Y-Range:");
		GridBagConstraints gbc_lblYRange = new GridBagConstraints();
		gbc_lblYRange.anchor = GridBagConstraints.EAST;
		gbc_lblYRange.insets = new Insets(0, 0, 5, 5);
		gbc_lblYRange.gridx = 0;
		gbc_lblYRange.gridy = 3;
		inputPanel.add(lblYRange, gbc_lblYRange);
		
		JSpinner yMinSpinner = new JSpinner();
		yMinSpinner.setModel(new SpinnerNumberModel(-100, -1000, 1000, 10));
		GridBagConstraints gbc_yMinSpinner = new GridBagConstraints();
		gbc_yMinSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_yMinSpinner.anchor = GridBagConstraints.WEST;
		gbc_yMinSpinner.gridx = 1;
		gbc_yMinSpinner.gridy = 3;
		inputPanel.add(yMinSpinner, gbc_yMinSpinner);
		
		JLabel lblYRangeSep = new JLabel(" - ");
		GridBagConstraints gbc_lblYRangeSep = new GridBagConstraints();
		gbc_lblYRangeSep.anchor = GridBagConstraints.WEST;
		gbc_lblYRangeSep.gridx = 2;
		gbc_lblYRangeSep.gridy = 3;
		inputPanel.add(lblYRangeSep, gbc_lblYRangeSep);
		
		JSpinner yMaxSpinner = new JSpinner();
		yMaxSpinner.setModel(new SpinnerNumberModel(100, -1000, 1000, 10));
		GridBagConstraints gbc_yMaxSpinner = new GridBagConstraints();
		gbc_yMaxSpinner.anchor = GridBagConstraints.WEST;
		gbc_yMaxSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_yMaxSpinner.gridx = 3;
		gbc_yMaxSpinner.gridy = 3;
		inputPanel.add(yMaxSpinner, gbc_yMaxSpinner);
		
		JLabel lblZRange = new JLabel("Z-Range:");
		GridBagConstraints gbc_lblZRange = new GridBagConstraints();
		gbc_lblZRange.anchor = GridBagConstraints.EAST;
		gbc_lblZRange.insets = new Insets(0, 0, 5, 5);
		gbc_lblZRange.gridx = 0;
		gbc_lblZRange.gridy = 4;
		inputPanel.add(lblZRange, gbc_lblZRange);
		
		JSpinner zMinSpinner = new JSpinner();
		zMinSpinner.setModel(new SpinnerNumberModel(-100, -1000, 1000, 10));
		GridBagConstraints gbc_zMinSpinner = new GridBagConstraints();
		gbc_zMinSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_zMinSpinner.anchor = GridBagConstraints.WEST;
		gbc_zMinSpinner.gridx = 1;
		gbc_zMinSpinner.gridy = 4;
		inputPanel.add(zMinSpinner, gbc_zMinSpinner);
		
		JLabel lblZRangeSep = new JLabel(" - ");
		GridBagConstraints gbc_lblZRangeSep = new GridBagConstraints();
		gbc_lblZRangeSep.anchor = GridBagConstraints.WEST;
		gbc_lblZRangeSep.gridx = 2;
		gbc_lblZRangeSep.gridy = 4;
		inputPanel.add(lblZRangeSep, gbc_lblZRangeSep);
		
		JSpinner zMaxSpinner = new JSpinner();
		zMaxSpinner.setModel(new SpinnerNumberModel(100, -1000, 1000, 10));
		GridBagConstraints gbc_zMaxSpinner = new GridBagConstraints();
		gbc_zMaxSpinner.anchor = GridBagConstraints.WEST;
		gbc_zMaxSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_zMaxSpinner.gridx = 3;
		gbc_zMaxSpinner.gridy = 4;
		inputPanel.add(zMaxSpinner, gbc_zMaxSpinner);
		
		
		
		
	}
	
	private static enum worldGens {
		
		random {
			public String getComboText() {return "random cubes";}
			
			public Container getContent() {
				JPanel panel = new JPanel();
				
				
				return panel;
			}

			public World generateWorld() {

				return null;
			}
			
			
		};
		
		
		
		public abstract String getComboText();
		
		public abstract Container getContent();
		
		public abstract World generateWorld(); 
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
