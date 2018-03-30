package gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.border.EmptyBorder;

import org.joml.Vector3f;

import interfaces.AutopilotConfig;
import testbed.world.World;
import testbed.world.WorldBuilder;
import utils.Constants;
import utils.GuiUtils;
import utils.Utils;


public class ConfigSetupGUI extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel spinnerPanel;
	
	@SuppressWarnings("unused")
	private JSpinner 	gravitySpinner, wingSizeSpinner, tailSizeSpinner, engineMassSpinner, 
						wingMassSpinner, tailMassSpinner, maxThrustSpinner, maxAOASpinner, 
						wingLiftslopeSpinner, verStabLiftslopeSpinner, horStabLiftslopeSpinner,
						verFOVSpinner, horFOVSpinner, nbColsSpinner, nbRowsSpinner;

	private JComboBox<String> genComboBox;

	private JSpinner[] velSpinners;

	private JSpinner[] posSpinners;

	private Map<String, WorldGen> worldGens;

	private JCheckBox logCheck;

	
	public static void main(String[] args) throws Exception {
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
		int windowHeight = 600; // => 300
		setBounds((screenWidth - windowWidth)/2, (screenHeight - windowHeight)/2, windowWidth, windowHeight);
		
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				dispose();
				System.exit(0);
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
				generateAutoPilotConfig();
				setVisible(false);
				dispose();
			}
		});
		btnPanel.add(btnStart);
		
		SwingUtilities.getRootPane(btnStart).setDefaultButton(btnStart);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		
		// create the worldgen tab
		JPanel worldGenPanel = new JPanel();
		tabbedPane.addTab("Worldgen", worldGenPanel);
		worldGenPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblWorldgen = new JLabel("World generation settings");
		lblWorldgen.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblWorldgen.setHorizontalAlignment(SwingConstants.LEFT);
		worldGenPanel.add(lblWorldgen, BorderLayout.NORTH);
		
		JPanel selectorPanel = new JPanel();
		selectorPanel.setLayout(new BorderLayout(0, 0));
		worldGenPanel.add(selectorPanel, BorderLayout.CENTER);
		
		JPanel genCards = new JPanel();
		CardLayout genCardLayout = new CardLayout(); 
		genCards.setLayout(genCardLayout);
		selectorPanel.add(genCards, BorderLayout.CENTER);
		
		String[] genComboLbls = new String[WorldGen.values().length];
		worldGens = new HashMap<>();
		int i = 0;
		for (WorldGen gen: WorldGen.values()) {
			genCards.add(gen.getContent(), gen.getComboText());
			genComboLbls[i] = gen.getComboText();
			worldGens.put(gen.getComboText(), gen);
			i++;
		}
		
		JPanel comboPanel = new JPanel();
		comboPanel.setLayout(new BorderLayout(0, 0));
		selectorPanel.add(comboPanel, BorderLayout.NORTH);
		
		JPanel anotherPanel = new JPanel();
		anotherPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		comboPanel.add(anotherPanel, BorderLayout.NORTH);
		JLabel selectorLbl = new JLabel("Select world generation method: ");
		anotherPanel.add(selectorLbl);
		
		genComboBox = new JComboBox<String>();
		DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>(genComboLbls);
		genComboBox.setModel(comboBoxModel);
		anotherPanel.add(genComboBox);
		genComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					genCardLayout.show(genCards, (String)e.getItem());
				}
			}
		});
		genComboBox.setSelectedItem(WorldGen.premade.getComboText());
				
		JPanel checkPanel = new JPanel();
		checkPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		logCheck = new JCheckBox("Enable logging?");
		checkPanel.add(logCheck);
		logCheck.setSelected(true);
		comboPanel.add(checkPanel, BorderLayout.SOUTH);
		
		
		// create the drone startup tap
		JPanel dronePanel = new JPanel();
		tabbedPane.addTab("Drone", dronePanel);
		dronePanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblDrone = new JLabel("Drone starting position settings");
		lblDrone.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblDrone.setHorizontalAlignment(SwingConstants.LEFT);
		dronePanel.add(lblDrone, BorderLayout.NORTH);
		
		JPanel startupPanel = new JPanel();
		GridBagLayout gbl_drone = new GridBagLayout();
		gbl_drone.columnWidths = new int[] {150};
		gbl_drone.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
		gbl_drone.rowWeights = new double[] {0.0, 0.0, 0.0, 1.0};
		startupPanel.setLayout(gbl_drone);
		dronePanel.add(startupPanel, BorderLayout.CENTER);
		
		JLabel lblDroneDesc = new JLabel("<html>Set the starting position, velocity and orientation for the drone. "
				+ "<br> Note that this will have no effect if you select a predefined world. </html>");
		GridBagConstraints gbc_lblDroneDesc = GuiUtils.buildGBC(0, 0, GridBagConstraints.NORTHWEST, new Insets(5, 0, 5, 5));
		gbc_lblDroneDesc.gridwidth = 7;
		startupPanel.add(lblDroneDesc, gbc_lblDroneDesc);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = GuiUtils.buildGBC(0, 1, GridBagConstraints.CENTER, new Insets(5, 0, 5, 0));
		gbc_separator.fill = GridBagConstraints.BOTH;
		gbc_separator.gridwidth = 7;
		startupPanel.add(separator, gbc_separator);
		
		posSpinners = GuiUtils.buildTripleInputSpinner(startupPanel, "Starting position", "x: ", "y: ", "z: ", 2, 
						Utils.buildIntArr(-1000, -1000, -1000), Utils.buildIntArr(1000, 1000, 1000), 
						Utils.buildIntArr(0,0,0), Utils.buildIntArr(1,1,1,1));
		
		velSpinners = GuiUtils.buildTripleInputSpinner(startupPanel, "Starting velocity", "x: ", "y: ", "z: ", 3, 
				Utils.buildIntArr(-1000, -1000, -1000), Utils.buildIntArr(1000, 1000, 1000), 
				Utils.buildIntArr(0,0,0), Utils.buildIntArr(1,1,1,1));
	}
	
	public World showDialog() throws Exception {
		this.setVisible(true);
		
		return prepareConfig();
	}

	private World prepareConfig() throws Exception {
		
		World world = worldGens.get(genComboBox.getSelectedItem()).generateWorld();
		
		if (world == null)
			throw new Exception("No worldgen selected");
		
		if (world instanceof WorldBuilder) {
			Vector3f pos = GuiUtils.buildVector(posSpinners),
					 vel = GuiUtils.buildVector(velSpinners);
			
			((WorldBuilder) world).setupDrone(generateAutoPilotConfig(), pos, vel);
		}
		
		if (logCheck.isSelected())
			world.initLogging(generateAutoPilotConfig().getDroneID());
		
		return world; 
	}
	
	private AutopilotConfig generateAutoPilotConfig() {
		return Utils.createDefaultConfig();
	}
	
	
	@SuppressWarnings("unused")
	private void buildSpinners() {
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
