package com.warcgenerator.gui.view.output;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.gui.actions.output.OutputSaveAction;
import com.warcgenerator.gui.actions.output.OutputSaveAndGenerateAction;
import com.warcgenerator.gui.components.CustomButton;
import com.warcgenerator.gui.components.CustomCheckBox;
import com.warcgenerator.gui.components.CustomJPanel;
import com.warcgenerator.gui.components.CustomLabel;
import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.common.validator.NotNullOREmptyValidator;

public class OutputConfigPanel extends CustomJPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFormattedTextField outputDirTField;
    private JFormattedTextField corpusDirTField;
	private JFormattedTextField spamDirTField;
	private JFormattedTextField hamDirTField;
	private CustomCheckBox chckbxEliminarLaSalida;

	@SuppressWarnings("unused")
	private IAppLogic logic;
	@SuppressWarnings("unused")
	private WarcGeneratorGUI view;

	private OutputSaveAction outputSaveAction;
	private OutputSaveAndGenerateAction outputSaveAndGenerateAction;

	// Create a file chooser
	private JFileChooser fc = new JFileChooser();

	/**
	 * Create the panel.
	 * @param logic Business Logic
	 * @param view WarcGeneratorGUI
	 */
	public OutputConfigPanel(final IAppLogic logic, final WarcGeneratorGUI view) {
		super();
		this.logic = logic;
		this.view = view;

		this.setName("Configuracion de salida");

		outputSaveAction = new OutputSaveAction(logic, view, this);
		outputSaveAndGenerateAction = new OutputSaveAndGenerateAction(logic,
				view, this, outputSaveAction);

		ImageIcon icon = new ImageIcon(
				WarcGeneratorGUI.class
						.getResource("/com/warcgenerator/gui/resources/img/save.png"));

		CustomButton saveAndGenerateBtn = new CustomButton();
		saveAndGenerateBtn.setName("OutputConfigPanel.saveAndGenerateBtn.text"); //$NON-NLS-1$
		view.addLocaleChangeListener(saveAndGenerateBtn);
		saveAndGenerateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputSaveAndGenerateAction.actionPerformed(e);
			}
		});

		CustomButton saveBtn = new CustomButton();
		saveBtn.setName("GeneralConfigPanel.btnNewButton.text"); //$NON-NLS-1$
		view.addLocaleChangeListener(saveBtn);
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		// panel.setBackground(new Color(230, 230, 250));

		add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 363 };
		gbl_panel.rowHeights = new int[] { 0, 36, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		panel.setLayout(gbl_panel);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.gridwidth = 2;
		gbc_horizontalStrut_1.insets = new Insets(5, 0, 5, 0);
		gbc_horizontalStrut_1.gridx = 1;
		gbc_horizontalStrut_1.gridy = 0;
		panel.add(horizontalStrut_1, gbc_horizontalStrut_1);

		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.gridheight = 2;
		gbc_verticalStrut.insets = new Insets(0, 0, 0, 5);
		gbc_verticalStrut.gridx = 0;
		gbc_verticalStrut.gridy = 1;
		panel.add(verticalStrut, gbc_verticalStrut);

		JLabel lblNewLabel_3 = new JLabel(icon);
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 1;
		gbc_lblNewLabel_3.gridy = 1;
		panel.add(lblNewLabel_3, gbc_lblNewLabel_3);

		CustomLabel lblNewLabel = new CustomLabel();
		lblNewLabel.setName("OutputConfigPanel.lblNewLabel.text"); //$NON-NLS-1$
		view.addLocaleChangeListener(lblNewLabel);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 1;
		panel.add(lblNewLabel, gbc_lblNewLabel);

		CustomLabel lblConfigureLasCarpetas = new CustomLabel();
		lblConfigureLasCarpetas
				.setName("OutputConfigPanel.lblConfigureLasCarpetas.text"); //$NON-NLS-1$
		view.addLocaleChangeListener(lblConfigureLasCarpetas);
		lblConfigureLasCarpetas.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblConfigureLasCarpetas = new GridBagConstraints();
		gbc_lblConfigureLasCarpetas.gridwidth = 2;
		gbc_lblConfigureLasCarpetas.anchor = GridBagConstraints.WEST;
		gbc_lblConfigureLasCarpetas.gridx = 1;
		gbc_lblConfigureLasCarpetas.gridy = 2;
		panel.add(lblConfigureLasCarpetas, gbc_lblConfigureLasCarpetas);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(panel_1, BorderLayout.SOUTH);
		panel_1.add(saveBtn);
		panel_1.add(saveAndGenerateBtn);

		JPanel panel_2 = new JPanel();
		add(panel_2, BorderLayout.CENTER);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 190, 82 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 33, 29, 35, 72 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 0.0 };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE, 0.0 };
		panel_2.setLayout(gbl_panel_2);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.gridwidth = 3;
		gbc_horizontalStrut.insets = new Insets(10, 0, 5, 5);
		gbc_horizontalStrut.gridx = 0;
		gbc_horizontalStrut.gridy = 0;
		panel_2.add(horizontalStrut, gbc_horizontalStrut);

		CustomLabel lblNewLabel_1 = new CustomLabel();
		lblNewLabel_1.setName("OutputConfigPanel.lblNewLabel_1.text"); //$NON-NLS-1$ //$NON-NLS-1$
		view.addLocaleChangeListener(lblNewLabel_1);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panel_2.add(lblNewLabel_1, gbc_lblNewLabel_1);

		outputDirTField = new JFormattedTextField("");
		outputDirTField.setEditable(false);
		outputDirTField.setColumns(10);
		GridBagConstraints gbc_outputDirTField = new GridBagConstraints();
		gbc_outputDirTField.fill = GridBagConstraints.BOTH;
		gbc_outputDirTField.insets = new Insets(0, 0, 5, 5);
		gbc_outputDirTField.gridx = 1;
		gbc_outputDirTField.gridy = 1;
		panel_2.add(outputDirTField, gbc_outputDirTField);

		CustomButton examineBtn = new CustomButton();
		examineBtn.setName("OutputConfigPanel.examineBtn.text"); //$NON-NLS-1$
		view.addLocaleChangeListener(examineBtn);
		examineBtn.setIcon(new ImageIcon(OutputConfigPanel.class
				.getResource("/com/warcgenerator/gui/resources/img/find.png")));
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
		GridBagConstraints gbc_examineBtn = new GridBagConstraints();
		gbc_examineBtn.insets = new Insets(0, 0, 5, 5);
		gbc_examineBtn.gridx = 2;
		gbc_examineBtn.gridy = 1;
		panel_2.add(examineBtn, gbc_examineBtn);

        CustomLabel lblCorpusDir = new CustomLabel();
        lblCorpusDir.setName("OutputConfigPanel.lblCorpusDir.text");
        view.addLocaleChangeListener(lblCorpusDir);
        lblCorpusDir.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_lblCorpusDir = new GridBagConstraints();
        gbc_lblCorpusDir.fill = GridBagConstraints.BOTH;
        gbc_lblCorpusDir.insets = new Insets(0, 0, 5, 5);
        gbc_lblCorpusDir.gridx = 0;
        gbc_lblCorpusDir.gridy = 2;
        panel_2.add(lblCorpusDir, gbc_lblCorpusDir);

        corpusDirTField = new JFormattedTextField("");

        NotNullOREmptyValidator corpusValidator = new NotNullOREmptyValidator(
                view.getMainFrame(), corpusDirTField,
                "OutputConfigPanel.corpusDirTField.verifier.text");
        corpusDirTField.setInputVerifier(corpusValidator);
        view.addLocaleChangeListener(corpusValidator);
        corpusDirTField.setColumns(10);
        GridBagConstraints gbc_corpusDirTField = new GridBagConstraints();
        gbc_corpusDirTField.fill = GridBagConstraints.BOTH;
        gbc_corpusDirTField.insets = new Insets(0, 0, 5, 5);
        gbc_corpusDirTField.gridx = 1;
        gbc_corpusDirTField.gridy = 2;
        panel_2.add(corpusDirTField, gbc_corpusDirTField);

		CustomLabel lblNewLabel_2 = new CustomLabel();
		lblNewLabel_2.setName("OutputConfigPanel.lblNewLabel_2.text"); //$NON-NLS-1$
		view.addLocaleChangeListener(lblNewLabel_2);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 3;
		panel_2.add(lblNewLabel_2, gbc_lblNewLabel_2);

		spamDirTField = new JFormattedTextField("");

		NotNullOREmptyValidator validator = new NotNullOREmptyValidator(
				view.getMainFrame(), spamDirTField,
				"OutputConfigPanel.spamDirTField.verifier.text");
		spamDirTField.setInputVerifier(validator);
		view.addLocaleChangeListener(validator);
		spamDirTField.setColumns(10);
		GridBagConstraints gbc_spamDirTField = new GridBagConstraints();
		gbc_spamDirTField.fill = GridBagConstraints.BOTH;
		gbc_spamDirTField.insets = new Insets(0, 0, 5, 5);
		gbc_spamDirTField.gridx = 1;
		gbc_spamDirTField.gridy = 3;
		panel_2.add(spamDirTField, gbc_spamDirTField);

		CustomLabel lblHam = new CustomLabel();
		lblHam.setName("OutputConfigPanel.lblHam.text"); //$NON-NLS-1$
		view.addLocaleChangeListener(lblHam);
		lblHam.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblHam = new GridBagConstraints();
		gbc_lblHam.fill = GridBagConstraints.BOTH;
		gbc_lblHam.insets = new Insets(0, 0, 5, 5);
		gbc_lblHam.gridx = 0;
		gbc_lblHam.gridy = 4;
		panel_2.add(lblHam, gbc_lblHam);

		hamDirTField = new JFormattedTextField("");
		NotNullOREmptyValidator validator2 = new NotNullOREmptyValidator(
				view.getMainFrame(),
				hamDirTField,
				"OutputConfigPanel.hamDirTField.verifier.text");
		hamDirTField.setInputVerifier(validator2);
		view.addLocaleChangeListener(validator2);
		hamDirTField.setColumns(10);
		GridBagConstraints gbc_hamDirTField = new GridBagConstraints();
		gbc_hamDirTField.fill = GridBagConstraints.BOTH;
		gbc_hamDirTField.insets = new Insets(0, 0, 5, 5);
		gbc_hamDirTField.gridx = 1;
		gbc_hamDirTField.gridy = 4;
		panel_2.add(hamDirTField, gbc_hamDirTField);

		chckbxEliminarLaSalida = new CustomCheckBox();
		chckbxEliminarLaSalida
				.setName("OutputConfigPanel.chckbxEliminarLaSalida.text");
		view.addLocaleChangeListener(chckbxEliminarLaSalida);
		chckbxEliminarLaSalida.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_chckbxEliminarLaSalida = new GridBagConstraints();
		gbc_chckbxEliminarLaSalida.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxEliminarLaSalida.gridwidth = 3;
		gbc_chckbxEliminarLaSalida.fill = GridBagConstraints.BOTH;
		gbc_chckbxEliminarLaSalida.gridx = 0;
		gbc_chckbxEliminarLaSalida.gridy = 5;
		panel_2.add(chckbxEliminarLaSalida, gbc_chckbxEliminarLaSalida);

	}

	public JFormattedTextField getOutputDirTField() {
		return outputDirTField;
	}

    public JFormattedTextField getCorpusDirTField() {
        return corpusDirTField;
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

	public void setChckbxEliminarLaSalida(CustomCheckBox chckbxEliminarLaSalida) {
		this.chckbxEliminarLaSalida = chckbxEliminarLaSalida;
	}

	@Override
	public void save() {
		outputSaveAction.actionPerformed(null);
	}
}
