package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class AutopilotGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private final int imgWidth, imgHeight;
	
	private JLabel lblImage;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AutopilotGUI frame = new AutopilotGUI(200,200);
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
		
		lblImage = new JLabel("");
		lblImage.setIcon(new ImageIcon());
		lblImage.setPreferredSize(new Dimension(imgWidth, imgHeight));
		contentPane.add(lblImage, BorderLayout.CENTER);
		
		JLabel lblAutopilotGui = new JLabel("Autopilot GUI");
		lblAutopilotGui.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblAutopilotGui, BorderLayout.NORTH);
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
