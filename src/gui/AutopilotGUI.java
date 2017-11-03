package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;

import datatypes.AutopilotOutputs;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JSlider;

public class AutopilotGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private final int imgWidth, imgHeight;
	
	private JLabel lblImage;
	private JPanel topContentPanel;
	private JPanel contentPanel;
	private JPanel sliderPanel;
	private SliderHandler lwSlider, horStabSlider, rwSlider, verStabSlider, thrustSlider;
	private JPanel horSliderPanel;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AutopilotGUI frame = new AutopilotGUI(200,200,1000);
					frame.showGUI();
					frame.lblImage.setIcon(new ImageIcon(ImageIO.read(new File("ss.png"))));;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	/**
	 * Create the frame.
	 */
	public AutopilotGUI(int imgWidth, int imgHeight, int maxThrust) {		
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setTitle("Autopilot");
		topContentPanel = new JPanel();
		topContentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		topContentPanel.setLayout(new BorderLayout(0, 0));
		setContentPane(topContentPanel);
		
		JLabel lblAutopilotGui = new JLabel("Autopilot GUI");
		lblAutopilotGui.setHorizontalAlignment(SwingConstants.CENTER);
		topContentPanel.add(lblAutopilotGui, BorderLayout.NORTH);
		
		contentPanel = new JPanel();
		topContentPanel.add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		contentPanel.setLayout(gbl_panel);
		
		
		lblImage = new JLabel("");
		contentPanel.add(lblImage, buildGBC(0, 0, GridBagConstraints.CENTER, new Insets(5, 5, 5, 5)));
		lblImage.setIcon(new ImageIcon());
		lblImage.setPreferredSize(new Dimension(imgWidth, imgHeight));
		
		sliderPanel = new JPanel();
		contentPanel.add(sliderPanel, buildGBC(1, 0, GridBagConstraints.CENTER));
		GridBagLayout gbl_sliderPanel = new GridBagLayout();
		sliderPanel.setLayout(gbl_sliderPanel);
		
		
		horSliderPanel = new JPanel();
		sliderPanel.add(horSliderPanel, buildGBC(0, 0, GridBagConstraints.CENTER));
		GridBagLayout gbl_horSliderPanel = new GridBagLayout();
		gbl_horSliderPanel.columnWidths = new int[] {60, 60, 60};
		horSliderPanel.setLayout(gbl_horSliderPanel);
		
		
		Hashtable<Integer, JLabel> lblTable = new Hashtable<>();
		for (int i=-90; i<=90; i+=30) {
			JLabel lbl = new JLabel("" + i);
			lbl.setFont(lbl.getFont().deriveFont(9.0f));
			lblTable.put(i, lbl);
		}
		
		
		lwSlider = buildSlider("RW", SwingConstants.VERTICAL, -90, 0, 90, 30, 10, lblTable, false);
		horSliderPanel.add(lwSlider.panel, buildGBC(0, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));
		
		
		horStabSlider = buildSlider("HS", SwingConstants.VERTICAL, -90, 0, 90, 30, 10, lblTable, false);
		horSliderPanel.add(horStabSlider.panel, buildGBC(1, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));
		
		
		rwSlider = buildSlider("LW", SwingConstants.VERTICAL, -90, 0, 90, 30, 10, lblTable, false);
		horSliderPanel.add(rwSlider.panel, buildGBC(2, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5)));
		

		verStabSlider = buildSlider("VS", SwingConstants.HORIZONTAL, -90, 0, 90, 30, 10, lblTable, false); 
		sliderPanel.add(verStabSlider.panel, buildGBC(0, 1, GridBagConstraints.CENTER, new Insets(5, 0, 5, 5)));
		
		
		Hashtable<Integer, JLabel> thrustTable = new Hashtable<>();
		thrustTable.put(0, new JLabel("0"));
		thrustTable.put(maxThrust/2, new JLabel(""+maxThrust/2));
		thrustTable.put(maxThrust, new JLabel(""+maxThrust));
		
		thrustSlider = buildSlider("Thrust", SwingConstants.VERTICAL, 0, 0, maxThrust, maxThrust/10, maxThrust/40, thrustTable, true);
		thrustSlider.slider.setPreferredSize(new Dimension(57, 270));
		GridBagConstraints gbc_thrustSlider = buildGBC(1, 0, GridBagConstraints.CENTER, new Insets(0, 5, 0, 5));
		gbc_thrustSlider.gridheight = 2;
		sliderPanel.add(thrustSlider.panel, gbc_thrustSlider);
	}
	
	
	public void showGUI() {
		pack();
		setVisible(true);
	}
	
	public void updateImage(byte[] image) {
		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_3BYTE_BGR);
		img.getRaster().setDataElements(0, 0, imgWidth, imgHeight, image);
		lblImage.setIcon(new ImageIcon(img));
	}
	
	public void updateOutputs(AutopilotOutputs output) {
		lwSlider.setValue((int) Math.toDegrees(output.getLeftWingInclination()));
		rwSlider.setValue((int) Math.toDegrees(output.getRightWingInclination()));
		horStabSlider.setValue((int) Math.toDegrees(output.getHorStabInclination()));
		verStabSlider.setValue((int) Math.toDegrees(output.getVerStabInclination()));
		thrustSlider.setValue((int) Math.toDegrees(output.getThrust()));
	}

	
	private static SliderHandler buildSlider(String title, int orientation, int min, int val, int max, int majorSpacing, int minorSpacing, Hashtable<Integer, JLabel> lblTable, boolean hasBar) {
		JPanel panel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		panel.setLayout(gbl);
		
		JLabel titleLbl = new JLabel(title);
		panel.add(titleLbl, buildGBC(0, 0, GridBagConstraints.CENTER));
		
		JSlider slider = new JSlider(orientation, min, max, val);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(majorSpacing);
		slider.setMinorTickSpacing(minorSpacing);
		slider.setLabelTable(lblTable);
		slider.setPaintLabels(true);
		if (!hasBar)
			slider.setUI(new NoBarMetalSliderUI());
		panel.add(slider, buildGBC(0, 1, GridBagConstraints.CENTER));
		
		JLabel label = new JLabel("" + val);
		panel.add(label, buildGBC(0, 2, GridBagConstraints.CENTER));
		
		SliderHandler handler = new SliderHandler(slider, panel, label);
		slider.addChangeListener(handler);
		return handler;
	}
	
	private static GridBagConstraints buildGBC(int gridx, int gridy, int anchor) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = anchor;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		return gbc;
	}
	
	private static GridBagConstraints buildGBC(int gridx, int gridy, int anchor, Insets insets) {
		GridBagConstraints gbc = buildGBC(gridx, gridy, anchor);
		gbc.insets = insets;
		return gbc;
	}
	
	
	private static class SliderHandler implements ChangeListener {

		private final JSlider slider;
		private final JPanel panel;
		private final JLabel label;
		private int val;
		public boolean lock = true;
		
		public SliderHandler(JSlider slider, JPanel panel, JLabel label) {
			this.slider = slider;
			this.panel = panel;
			this.label = label;
			this.val = slider.getValue();
		}
		
		public void stateChanged(ChangeEvent e) {
			if (lock) {
				slider.setValue(val);
			} else {
				val = slider.getValue();
				label.setText("" + val);
			}
		}
		
		public void setValue(int val) {
			boolean oldLock = lock;
			lock = false;
			slider.setValue(val);
			lock = oldLock;
		}
	}
	
	/**
	 * shady workaround voor iets dat eigenlijk onnodig is.
	 */
	private static class NoBarMetalSliderUI extends MetalSliderUI {
		
		@Override
		public void paintTrack(Graphics g) {
			super.filledSlider = false;
			super.paintTrack(g);
		}
	}
}
