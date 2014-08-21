package com.warcgenerator.gui.view.datasources;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.warcgenerator.core.config.DataSourceConfig;
import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.gui.actions.datasource.DSModifyAction;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;

public class DSDetailPanel extends JPanel {
	
	private JTextArea summaryConfigTField;
	private JScrollPane scrollPane;
	private DSModifyAction dsModifyAction;
	
	public void setSummaryText(String text) {
		summaryConfigTField.setText(text);
		summaryConfigTField.updateUI();
		summaryConfigTField.repaint();
		scrollPane.updateUI();
		scrollPane.repaint();
	}

	/**
	 * Create the panel.
	 */
	public DSDetailPanel(IAppLogic logic, 
			WarcGeneratorGUI view,
			DataSourceConfig config) {
		dsModifyAction = new DSModifyAction(logic, view, config); 
		
		setBackground(new Color(230, 230, 250));
		
		JLabel lblNewLabel = new JLabel(Messages.getString("DSDetailPanel.lblNewLabel.text")); //$NON-NLS-1$
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JTextPane txtpnunOrigenDe = new JTextPane();
		txtpnunOrigenDe.setEditable(false);
		txtpnunOrigenDe.setBackground(new Color(230, 230, 250));
		txtpnunOrigenDe.setText(Messages.getString("DSDetailPanel.txtpnunOrigenDe.text")); //$NON-NLS-1$
		
		JButton btnNuevoOrigen = new JButton(Messages.getString("DSDetailPanel.btnNuevoOrigen.text")); //$NON-NLS-1$
		btnNuevoOrigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dsModifyAction.actionPerformed(e);
			}
		});
		
		JButton removeBtn = new JButton(Messages.getString("DSDetailPanel.removeBtn.text")); //$NON-NLS-1$
		removeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		 //Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
		
		scrollPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(23)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(removeBtn)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnNuevoOrigen))
							.addComponent(txtpnunOrigenDe, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
							.addComponent(scrollPane, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 404, GroupLayout.PREFERRED_SIZE)))
					.addGap(23))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(21)
					.addComponent(lblNewLabel)
					.addGap(18)
					.addComponent(txtpnunOrigenDe, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNuevoOrigen)
						.addComponent(removeBtn))
					.addGap(21))
		);
		
		summaryConfigTField = new JTextArea();
		summaryConfigTField.setWrapStyleWord(true);
		scrollPane.setViewportView(summaryConfigTField);
		scrollPane.setBounds(23, 23, 404, 134);
		setLayout(groupLayout);

	}

	
}
