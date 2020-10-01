package it.giara.gui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame
{
	
	private JPanel contentPane;
	
	/**
	 * Create the frame.
	 */
	public MainFrame()
	{
		setTitle("PWMO File Editor by Giara ");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 500);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpenFile = new JMenuItem("Open File");
		mnFile.add(mntmOpenFile);
		
		JMenuItem mntmSaveFile = new JMenuItem("Save File");
		mnFile.add(mntmSaveFile);
		
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		
		JMenu mnInfo = new JMenu("Info");
		menuBar.add(mnInfo);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JSlider slider = new JSlider();
		slider.setValue(0);
		slider.setPaintTicks(true);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		
		JLabel previewImage = new JLabel("");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup().addContainerGap()
										.addComponent(slider, GroupLayout.DEFAULT_SIZE, 648, Short.MAX_VALUE)
										.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(lblNewLabel_1))
								.addComponent(previewImage, GroupLayout.DEFAULT_SIZE, 714, Short.MAX_VALUE))
						.addContainerGap()));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addComponent(previewImage, GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(lblNewLabel_1)
								.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));
		contentPane.setLayout(gl_contentPane);
	}
}
