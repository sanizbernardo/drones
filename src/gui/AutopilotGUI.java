package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JSlider;
import java.awt.GridLayout;

public class AutopilotGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private final int imgWidth, imgHeight;
	
	private JLabel lblImage;
	private JPanel contentPane;
	private JPanel contentPanel;
	private JPanel sliderPanel;
	private JSlider lwSlider;
	private JSlider horStabSlider;
	private JSlider rwSlider;
	private JSlider verStabSlider;
	private JPanel horSliderPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AutopilotGUI frame = new AutopilotGUI(200,200);
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
	public AutopilotGUI(int imgWidth, int imgHeight) {		
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setTitle("Autopilot");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JLabel lblAutopilotGui = new JLabel("Autopilot GUI");
		lblAutopilotGui.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblAutopilotGui, BorderLayout.NORTH);
		
		contentPanel = new JPanel();
		contentPane.add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWeights = new double[]{1.0};
		gbl_panel.rowWeights = new double[]{0.0};
		contentPanel.setLayout(gbl_panel);
		
		lblImage = new JLabel("");
		GridBagConstraints gbc_lblImage = new GridBagConstraints();
		gbc_lblImage.insets = new Insets(0, 0, 5, 0);
		gbc_lblImage.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblImage.gridx = 0;
		gbc_lblImage.gridy = 0;
		contentPanel.add(lblImage, gbc_lblImage);
		lblImage.setIcon(new ImageIcon());
		lblImage.setPreferredSize(new Dimension(imgWidth, imgHeight));
		
		sliderPanel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		contentPanel.add(sliderPanel, gbc_panel);
		GridBagLayout gbl_sliderPanel = new GridBagLayout();
		gbl_sliderPanel.columnWeights = new double[]{0.0};
		gbl_sliderPanel.rowWeights = new double[]{0.0, 0.0};
		sliderPanel.setLayout(gbl_sliderPanel);
		
		horSliderPanel = new JPanel();
		GridBagConstraints gbc_horSliderPanel = new GridBagConstraints();
		gbc_horSliderPanel.fill = GridBagConstraints.BOTH;
		gbc_horSliderPanel.gridx = 0;
		gbc_horSliderPanel.gridy = 0;
		sliderPanel.add(horSliderPanel, gbc_horSliderPanel);
		GridBagLayout gbl_horSliderPanel = new GridBagLayout();
		gbl_horSliderPanel.columnWidths = new int[] {20, 60, 60, 60};
		gbl_horSliderPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_horSliderPanel.rowWeights = new double[]{0.0};
		horSliderPanel.setLayout(gbl_horSliderPanel);
		
		Hashtable<Integer, JLabel> lblTable = new Hashtable<>();
		for (int i=-90; i<=90; i+=30) {
			lblTable.put(i, new JLabel("" + i));
		}
		
		lwSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
		GridBagConstraints gbc_lwSlider = new GridBagConstraints();
		gbc_lwSlider.anchor = GridBagConstraints.NORTHWEST;
		gbc_lwSlider.insets = new Insets(0, 0, 0, 5);
		gbc_lwSlider.gridx = 1;
		gbc_lwSlider.gridy = 0;
		horSliderPanel.add(lwSlider, gbc_lwSlider);
		lwSlider.setPaintTicks(true);
		lwSlider.setMajorTickSpacing(15);
		lwSlider.setMinorTickSpacing(5);
		lwSlider.setLabelTable(lblTable);
		lwSlider.setPaintLabels(true);
		
		
		horStabSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
		GridBagConstraints gbc_horStabSlider = new GridBagConstraints();
		gbc_horStabSlider.anchor = GridBagConstraints.NORTHWEST;
		gbc_horStabSlider.insets = new Insets(0, 0, 0, 5);
		gbc_horStabSlider.gridx = 2;
		gbc_horStabSlider.gridy = 0;
		horSliderPanel.add(horStabSlider, gbc_horStabSlider);
		horStabSlider.setPaintTicks(true);
		horStabSlider.setMajorTickSpacing(15);
		horStabSlider.setMinorTickSpacing(5);
		horStabSlider.setLabelTable(lblTable);
		horStabSlider.setPaintLabels(true);
		
		
		rwSlider = new JSlider(SwingConstants.VERTICAL, 0, 10, 5);
		GridBagConstraints gbc_rwSlider = new GridBagConstraints();
		gbc_rwSlider.anchor = GridBagConstraints.NORTHWEST;
		gbc_rwSlider.gridx = 3;
		gbc_rwSlider.gridy = 0;
		horSliderPanel.add(rwSlider, gbc_rwSlider);
		

		verStabSlider = new JSlider(SwingConstants.HORIZONTAL, -90, 90, 0);
		GridBagConstraints gbc_verStabSlider = new GridBagConstraints();
		gbc_verStabSlider.insets = new Insets(0, 0, 5, 0);
		gbc_verStabSlider.fill = GridBagConstraints.BOTH;
		gbc_verStabSlider.gridx = 0;
		gbc_verStabSlider.gridy = 1;
		sliderPanel.add(verStabSlider, gbc_verStabSlider);
		verStabSlider.setPaintTicks(true);
		verStabSlider.setMajorTickSpacing(15);
		verStabSlider.setMinorTickSpacing(5);
		verStabSlider.setLabelTable(lblTable);
		verStabSlider.setPaintLabels(true);
		
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

}
