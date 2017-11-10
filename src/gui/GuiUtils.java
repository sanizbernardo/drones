package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class GuiUtils {
	
	static BufferedImage addCrossHair(BufferedImage img, int imgx, int imgy, int color) {
		int[] xcoords = new int[] {0, 1, -1, 2, -2 ,3, -3, 1,  1, -1, -1};
		int[] ycoords = new int[] {0, 0,  0, 0,  0, 0,  0, 1, -1,  1, -1};	
		for (int i = 0; i < xcoords.length; i++) {
			if (imgx+xcoords[i] >= 0 && imgx+xcoords[i] < img.getWidth() && imgy + ycoords[i] >= 0 && imgy + ycoords[i] < img.getHeight())
				img.setRGB(imgx + xcoords[i], imgy + ycoords[i], color);
			if (imgx+ycoords[i] >= 0 && imgx+ycoords[i] < img.getWidth() && imgy + xcoords[i] >= 0 && imgy + xcoords[i] < img.getHeight())
				img.setRGB(imgx + ycoords[i], imgy + xcoords[i], color);
		}		
		img.flush();
		return img;
	}
	
	
	static GridBagConstraints buildGBC(int gridx, int gridy, int anchor) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = anchor;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		return gbc;
	}
	
	static GridBagConstraints buildGBC(int gridx, int gridy, int anchor, Insets insets) {
		GridBagConstraints gbc = buildGBC(gridx, gridy, anchor);
		gbc.insets = insets;
		return gbc;
	}


	static JSpinner buildSpinner(JPanel parentPanel, String lblName, int xpos, int ypos, Number defaultValue, Comparable<?> min, Comparable<?> max,  Number stepSize) {
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc = buildGBC(xpos, ypos, GridBagConstraints.CENTER, new Insets(0,0,5,0));
		gbc.fill = GridBagConstraints.BOTH;
		parentPanel.add(panel, gbc);
		
		JLabel lbl = new JLabel(lblName);
		panel.add(lbl);
		
		JSpinner spinner = new JSpinner();
		spinner.setPreferredSize(new Dimension(50, 20));
		spinner.setModel(new SpinnerNumberModel(defaultValue, min, max, stepSize));
		panel.add(spinner);
		return spinner;
	}
	
	
	
	
}
