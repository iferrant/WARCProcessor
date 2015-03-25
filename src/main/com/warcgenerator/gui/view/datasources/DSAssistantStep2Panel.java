package com.warcgenerator.gui.view.datasources;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.warcgenerator.core.config.CustomParamConfig;
import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.gui.actions.datasource.DSAsisstantCancelAction;
import com.warcgenerator.gui.actions.datasource.DSAsisstantStep2BackAction;
import com.warcgenerator.gui.actions.datasource.DSAsisstantStep2ContinueAction;
import com.warcgenerator.gui.components.CustomButton;
import com.warcgenerator.gui.components.CustomCheckBox;
import com.warcgenerator.gui.components.CustomJPanel;
import com.warcgenerator.gui.components.CustomLabel;
import com.warcgenerator.gui.components.PropertiesJTable;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;

@SuppressWarnings("serial")
public class DSAssistantStep2Panel extends CustomJPanel {
	private Action dsAssistantStep2BackAction;
	private Action dsAssistantCancelAction;
	private Action dsAssistantStep2ContinueAction;

	private JTable paramsTable;

	public JTable getParamsTable() {
		return paramsTable;
	}

	private JFormattedTextField quantityMaxElemsTField;
	private CustomCheckBox spamEnabledCBox;
	private CustomCheckBox maxElementsEnabledCBox;
	private JRadioButton spamRButtom;
	private JRadioButton hamRButtom;

	/**
	 * Create the panel.
	 */
	public DSAssistantStep2Panel(IAppLogic logic, WarcGeneratorGUI view,
			JPanel parentAssistant) {
		dsAssistantStep2ContinueAction = new DSAsisstantStep2ContinueAction(
				logic, view, this, parentAssistant);
		dsAssistantStep2BackAction = new DSAsisstantStep2BackAction(logic,
				view, parentAssistant);
		dsAssistantCancelAction = new DSAsisstantCancelAction(logic, view,
				parentAssistant);

		setName("DSAssistantStep2Panel");

		ImageIcon icon = new ImageIcon(
				WarcGeneratorGUI.class
						.getResource("/com/warcgenerator/gui/resources/img/database.png"));

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		// panel.setBackground(new Color(230, 230, 250));
		add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 363 };
		gbl_panel.rowHeights = new int[] { 0, 36, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.gridwidth = 2;
		gbc_horizontalStrut.insets = new Insets(5, 0, 5, 0);
		gbc_horizontalStrut.gridx = 1;
		gbc_horizontalStrut.gridy = 0;
		panel.add(horizontalStrut, gbc_horizontalStrut);

		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.gridheight = 2;
		gbc_verticalStrut.insets = new Insets(0, 0, 0, 5);
		gbc_verticalStrut.gridx = 0;
		gbc_verticalStrut.gridy = 1;
		panel.add(verticalStrut, gbc_verticalStrut);

		JLabel lblNewLabel_4 = new JLabel(icon);
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 1;
		gbc_lblNewLabel_4.gridy = 1;
		panel.add(lblNewLabel_4, gbc_lblNewLabel_4);

		CustomLabel lblNewLabel = new CustomLabel();
		lblNewLabel.setName("DSAssistantStep2Panel.lblNewLabel.text");
		view.addLocaleChangeListener(lblNewLabel);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 1;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		CustomLabel label_2 = new CustomLabel();
		label_2.setName("DSAssistantStep2Panel.label_2.text");
		view.addLocaleChangeListener(label_2);
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.anchor = GridBagConstraints.WEST;
		gbc_label_2.gridwidth = 2;
		gbc_label_2.gridx = 1;
		gbc_label_2.gridy = 2;
		panel.add(label_2, gbc_label_2);

		JPanel panel_2 = new JPanel();
		add(panel_2, BorderLayout.CENTER);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0, 137, 54, 60, 39, 25 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0,
				0.0, 0.0 };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0,
				Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_2 = new GridBagConstraints();
		gbc_horizontalStrut_2.gridwidth = 5;
		gbc_horizontalStrut_2.insets = new Insets(10, 0, 5, 5);
		gbc_horizontalStrut_2.gridx = 1;
		gbc_horizontalStrut_2.gridy = 0;
		panel_2.add(horizontalStrut_2, gbc_horizontalStrut_2);

		Component verticalStrut_1 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_1 = new GridBagConstraints();
		gbc_verticalStrut_1.gridheight = 3;
		gbc_verticalStrut_1.insets = new Insets(0, 10, 5, 5);
		gbc_verticalStrut_1.gridx = 0;
		gbc_verticalStrut_1.gridy = 1;
		panel_2.add(verticalStrut_1, gbc_verticalStrut_1);

		JLabel lblNewLabel_1 = new JLabel("Spam:");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 1;
		panel_2.add(lblNewLabel_1, gbc_lblNewLabel_1);

		spamEnabledCBox = new CustomCheckBox();
		spamEnabledCBox.setName("DSAssistantStep2Panel.spamEnabledCBox.text");
		view.addLocaleChangeListener(spamEnabledCBox);
		GridBagConstraints gbc_spamEnabledCBox = new GridBagConstraints();
		gbc_spamEnabledCBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_spamEnabledCBox.insets = new Insets(0, 0, 5, 5);
		gbc_spamEnabledCBox.gridx = 2;
		gbc_spamEnabledCBox.gridy = 1;
		panel_2.add(spamEnabledCBox, gbc_spamEnabledCBox);
		spamEnabledCBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (spamEnabledCBox.isSelected()) {
					spamRButtom.setEnabled(true);
					hamRButtom.setEnabled(true);
				} else {
					spamRButtom.setEnabled(false);
					hamRButtom.setEnabled(false);
				}
			}
		});
		spamRButtom = new JRadioButton(
				Messages.getString("DSAssistantStep2Panel.spamRButtom.text"));
		GridBagConstraints gbc_spamRButtom = new GridBagConstraints();
		gbc_spamRButtom.anchor = GridBagConstraints.WEST;
		gbc_spamRButtom.insets = new Insets(0, 0, 5, 5);
		gbc_spamRButtom.gridx = 3;
		gbc_spamRButtom.gridy = 1;
		panel_2.add(spamRButtom, gbc_spamRButtom);
		spamRButtom.setSelected(true);

		// Disabled buttoms
		spamRButtom.setEnabled(false);

		group.add(spamRButtom);
		hamRButtom = new JRadioButton(
				Messages.getString("DSAssistantStep2Panel.hamRButtom.text"));
		GridBagConstraints gbc_hamRButtom = new GridBagConstraints();
		gbc_hamRButtom.anchor = GridBagConstraints.WEST;
		gbc_hamRButtom.insets = new Insets(0, 0, 5, 5);
		gbc_hamRButtom.gridx = 4;
		gbc_hamRButtom.gridy = 1;
		panel_2.add(hamRButtom, gbc_hamRButtom);
		hamRButtom.setEnabled(false);
		group.add(hamRButtom);

		CustomLabel lblNewLabel_2 = new CustomLabel();
		lblNewLabel_2.setName("DSAssistantStep2Panel.lblNewLabel_2.text");
		view.addLocaleChangeListener(lblNewLabel_2);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 1;
		gbc_lblNewLabel_2.gridy = 2;
		panel_2.add(lblNewLabel_2, gbc_lblNewLabel_2);
		maxElementsEnabledCBox = new CustomCheckBox();
		maxElementsEnabledCBox.setName(
				"DSAssistantStep2Panel.maxElementsEnabledCBox.text");
		view.addLocaleChangeListener(maxElementsEnabledCBox);
		GridBagConstraints gbc_maxElementsEnabledCBox = new GridBagConstraints();
		gbc_maxElementsEnabledCBox.anchor = GridBagConstraints.WEST;
		gbc_maxElementsEnabledCBox.insets = new Insets(0, 0, 5, 5);
		gbc_maxElementsEnabledCBox.gridx = 2;
		gbc_maxElementsEnabledCBox.gridy = 2;
		panel_2.add(maxElementsEnabledCBox, gbc_maxElementsEnabledCBox);
		maxElementsEnabledCBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (maxElementsEnabledCBox.isSelected()) {
					quantityMaxElemsTField.setEnabled(true);
				} else {
					quantityMaxElemsTField.setEnabled(false);
				}
			}
		});

		quantityMaxElemsTField = new JFormattedTextField();
		GridBagConstraints gbc_quantityMaxElemsTField = new GridBagConstraints();
		gbc_quantityMaxElemsTField.fill = GridBagConstraints.HORIZONTAL;
		gbc_quantityMaxElemsTField.insets = new Insets(0, 0, 5, 5);
		gbc_quantityMaxElemsTField.gridx = 3;
		gbc_quantityMaxElemsTField.gridy = 2;
		panel_2.add(quantityMaxElemsTField, gbc_quantityMaxElemsTField);
		quantityMaxElemsTField.setColumns(10);

		// Disabled quantity
		quantityMaxElemsTField.setEnabled(false);

		CustomLabel lblNewLabel_3 = new CustomLabel();
		lblNewLabel_3.setName("DSAssistantStep2Panel.lblNewLabel_3.text");
		view.addLocaleChangeListener(lblNewLabel_3);
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblNewLabel_3.gridx = 1;
		gbc_lblNewLabel_3.gridy = 3;
		panel_2.add(lblNewLabel_3, gbc_lblNewLabel_3);

		JScrollPane scrollPane = new JScrollPane();

		paramsTable = new PropertiesJTable();

		scrollPane.setViewportView(paramsTable);

		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridwidth = 5;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_1.gridx = 2;
		gbc_scrollPane_1.gridy = 3;
		panel_2.add(scrollPane, gbc_scrollPane_1);

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));

		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
		flowLayout.setVgap(10);
		panel_1.add(panel_3, BorderLayout.WEST);

		CustomLabel lblPasoDe = new CustomLabel();
		lblPasoDe.setName("DSAssistantStep2Panel.lblPasoDe.text");
		view.addLocaleChangeListener(lblPasoDe);
		panel_3.add(lblPasoDe);

		JPanel panel_4 = new JPanel();
		panel_1.add(panel_4, BorderLayout.EAST);

		CustomButton btnVolver = new CustomButton();
		btnVolver.setName("DSAssistantStep2Panel.btnVolver.text");
		view.addLocaleChangeListener(btnVolver);
		panel_4.add(btnVolver);
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dsAssistantStep2BackAction.actionPerformed(e);
			}
		});

		CustomButton btnNewButton = new CustomButton();
		btnNewButton.setName("DSAssistantStep2Panel.btnNewButton.text");
		view.addLocaleChangeListener(btnNewButton);
		panel_4.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dsAssistantCancelAction.actionPerformed(e);
			}
		});

		CustomButton btnNuevoOrigen = new CustomButton();
		btnNuevoOrigen.setName("DSAssistantStep2Panel.btnNuevoOrigen.text");
		view.addLocaleChangeListener(btnNuevoOrigen);
		panel_4.add(btnNuevoOrigen);
		btnNuevoOrigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dsAssistantStep2ContinueAction.actionPerformed(e);
			}
		});
	}

	public void setTableModel(Map<String, CustomParamConfig> map) {
		DefaultTableModel model = (DefaultTableModel) paramsTable.getModel();

		// Remove contents
		while (model.getRowCount() > 0) {
			model.removeRow(0);
		}

		for (String key : map.keySet()) {
			CustomParamConfig paramConfig = map.get(key);

			Object obj = null;
			if (paramConfig.getType().equals("java.lang.Boolean")) {
				obj = Boolean.valueOf(paramConfig.getValue());
			} else if (paramConfig.getType().equals("java.io.File")) {
				obj = new File(paramConfig.getValue());
			} else {
				obj = paramConfig.getValue();
			}

			model.addRow(new Object[] { key, obj });
		}
	}

	public JFormattedTextField getQuantityMaxElemsTField() {
		return quantityMaxElemsTField;
	}

	public void setQuantityMaxElemsTField(
			JFormattedTextField quantityMaxElemsTField) {
		this.quantityMaxElemsTField = quantityMaxElemsTField;
	}

	public JCheckBox getSpamEnabledCBox() {
		return spamEnabledCBox;
	}

	public void setSpamEnabledCBox(CustomCheckBox spamEnabledCBox) {
		this.spamEnabledCBox = spamEnabledCBox;
	}

	public JCheckBox getMaxElementsEnabledCBox() {
		return maxElementsEnabledCBox;
	}

	public void setMaxElementsEnabledCBox(CustomCheckBox maxElementsEnabledCBox) {
		this.maxElementsEnabledCBox = maxElementsEnabledCBox;
	}

	public JRadioButton getSpamRButtom() {
		return spamRButtom;
	}

	public void setSpamRButtom(JRadioButton spamRButtom) {
		this.spamRButtom = spamRButtom;
	}

	public JRadioButton getHamRButtom() {
		return hamRButtom;
	}

	public void setHamRButtom(JRadioButton hamRButtom) {
		this.hamRButtom = hamRButtom;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}
}
