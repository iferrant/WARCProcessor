package com.warcgenerator.gui.view.output;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.gui.actions.output.OutputSaveAction;
import com.warcgenerator.gui.actions.output.OutputSaveAndGenerateAction;
import com.warcgenerator.gui.components.CustomJPanel;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;

public class OutputConfigPanel extends CustomJPanel {
	private JFormattedTextField outputDirTField;
	private JFormattedTextField spamDirTField;
	private JFormattedTextField hamDirTField;
	private JCheckBox chckbxEliminarLaSalida;
	
	private IAppLogic logic;
	private WarcGeneratorGUI view;
	
	private OutputSaveAction outputSaveAction;
	private OutputSaveAndGenerateAction outputSaveAndGenerateAction;
	
	// Create a file chooser
	private JFileChooser fc = new JFileChooser();
	
	/**
	 * Create the panel.
	 */
	public OutputConfigPanel(final IAppLogic logic, final WarcGeneratorGUI view) {
		super();
		this.logic = logic;
		this.view = view;
		
		this.setName("Configuracion de salida");
		
		outputSaveAction = new OutputSaveAction(logic, view, this);
		outputSaveAndGenerateAction = new OutputSaveAndGenerateAction(
				logic, view, this, outputSaveAction);
		
		ImageIcon icon = new ImageIcon(WarcGeneratorGUI.class.getResource("/com/warcgenerator/gui/resources/img/save.png"));
		
		setBackground(new Color(230, 230, 250));
		
		JLabel lblNewLabel = new JLabel("Carpeta de salida"); //$NON-NLS-1$
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JTextPane txtpnunOrigenDe = new JTextPane();
		txtpnunOrigenDe.setEditable(false);
		txtpnunOrigenDe.setOpaque(false);
		txtpnunOrigenDe.setBackground(new Color(255, 255, 255, 0));
		txtpnunOrigenDe.setText("Configure las carpetas de salida del corpus a generar."); //$NON-NLS-1$
		
		JButton saveAndGenerateBtn = new JButton("Guardar y generar"); //$NON-NLS-1$
		saveAndGenerateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputSaveAndGenerateAction.actionPerformed(e);
			}
		});
		
		JLabel lblNewLabel_1 = new JLabel(Messages.getString("OutputConfigPanel.lblNewLabel_1.text")); //$NON-NLS-1$ //$NON-NLS-1$
		
		outputDirTField = new JFormattedTextField("");
		outputDirTField.setColumns(10);
		
		 //Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
		
		JButton saveBtn = new JButton(Messages.getString("GeneralConfigPanel.btnNewButton.text")); //$NON-NLS-1$
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		
		JLabel lblNewLabel_2 = new JLabel(Messages.getString("OutputConfigPanel.lblNewLabel_2.text")); //$NON-NLS-1$
		
		JLabel lblHam = new JLabel(Messages.getString("OutputConfigPanel.lblHam.text")); //$NON-NLS-1$
		
		spamDirTField = new JFormattedTextField("");
		spamDirTField.setColumns(10);
		
		hamDirTField = new JFormattedTextField("");
		hamDirTField.setColumns(10);
		
		JButton examineBtn = new JButton("Examinar"); //$NON-NLS-1$
		examineBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(OutputConfigPanel.this);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					outputDirTField.setValue(file.getAbsolutePath());
				} 
			}
		});
		
		JLabel lblNewLabel_3 = new JLabel(icon); //$NON-NLS-1$
		
		chckbxEliminarLaSalida = new JCheckBox(Messages.getString("OutputConfigPanel.chckbxEliminarLaSalida.text")); //$NON-NLS-1$
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(23)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel_3)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(183)
							.addComponent(saveBtn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(saveAndGenerateBtn))
						.addComponent(txtpnunOrigenDe, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel_1)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(lblNewLabel_2)
										.addComponent(lblHam))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(hamDirTField)
										.addComponent(spamDirTField)
										.addComponent(outputDirTField, GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE))))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(examineBtn)))
					.addGap(49))
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(129, Short.MAX_VALUE)
					.addComponent(chckbxEliminarLaSalida)
					.addGap(45))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(21)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_3))
					.addGap(18)
					.addComponent(txtpnunOrigenDe, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(outputDirTField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(examineBtn))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2)
						.addComponent(spamDirTField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHam)
						.addComponent(hamDirTField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(chckbxEliminarLaSalida)
					.addPreferredGap(ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(saveBtn)
						.addComponent(saveAndGenerateBtn))
					.addGap(22))
		);
		setLayout(groupLayout);

	}

	public JFormattedTextField getOutputDirTField() {
		return outputDirTField;
	}

	public JFormattedTextField getSpamDirTField() {
		return spamDirTField;
	}

	public JFormattedTextField getHamDirTField() {
		return hamDirTField;
	}
	
	public JCheckBox getChckbxEliminarLaSalida() {
		return chckbxEliminarLaSalida;
	}

	public void setChckbxEliminarLaSalida(JCheckBox chckbxEliminarLaSalida) {
		this.chckbxEliminarLaSalida = chckbxEliminarLaSalida;
	}

	@Override
	public void save() {
		outputSaveAction.actionPerformed(null);
	}
}
