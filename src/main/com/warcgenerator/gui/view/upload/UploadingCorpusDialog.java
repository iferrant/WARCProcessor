package com.warcgenerator.gui.view.upload;

import com.warcgenerator.gui.components.CustomButton;
import com.warcgenerator.gui.components.CustomJDialog;
import com.warcgenerator.gui.components.CustomLabel;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.generate.GeneratingCorpusDialog;

import javax.swing.*;
import java.awt.*;

public class UploadingCorpusDialog extends CustomJDialog {
	private JProgressBar progressBar;
	private WarcGeneratorGUI view;
	private Action cancelUploadCorpusAction;
	private CustomButton cancelBtn;

	public UploadingCorpusDialog(WarcGeneratorGUI view) {
		super(view.getMainFrame(), true);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		setTitle(Messages.getString("UploadingCorpusDialog.title.text"));
		view.addLocaleChangeListener(this);
		this.view = view;
		this.setBounds(0, 0, 360, 127);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 55 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0 };
		getContentPane().setLayout(gridBagLayout);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.gridwidth = 6;
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut_1.gridx = 0;
		gbc_horizontalStrut_1.gridy = 0;
		getContentPane().add(horizontalStrut_1, gbc_horizontalStrut_1);

		CustomLabel lblGenerandoCorpusEn = new CustomLabel();
		lblGenerandoCorpusEn.setName("UploadingCorpusDialog.process.text");
        lblGenerandoCorpusEn.setText(Messages.getString("UploadingCorpusDialog.subtitle.text"));
		view.addLocaleChangeListener(lblGenerandoCorpusEn);
		GridBagConstraints gbc_lblGenerandoCorpusEn = new GridBagConstraints();
		gbc_lblGenerandoCorpusEn.insets = new Insets(0, 0, 5, 5);
		gbc_lblGenerandoCorpusEn.anchor = GridBagConstraints.WEST;
		gbc_lblGenerandoCorpusEn.gridx = 2;
		gbc_lblGenerandoCorpusEn.gridy = 1;
		getContentPane().add(lblGenerandoCorpusEn, gbc_lblGenerandoCorpusEn);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel
				.setIcon(new ImageIcon(
						GeneratingCorpusDialog.class
								.getResource("/com/warcgenerator/gui/resources/img/imagen_gif.gif")));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridheight = 2;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 1;
		getContentPane().add(lblNewLabel, gbc_lblNewLabel);

		CustomLabel stateLbl = new CustomLabel();
		stateLbl.setText(Messages.getString("UploadingCorpusDialog.process.text"));
		view.addLocaleChangeListener(stateLbl);
		GridBagConstraints gbc_stateLbl = new GridBagConstraints();
		gbc_stateLbl.anchor = GridBagConstraints.WEST;
		gbc_stateLbl.insets = new Insets(0, 0, 5, 5);
		gbc_stateLbl.gridx = 2;
		gbc_stateLbl.gridy = 2;
		getContentPane().add(stateLbl, gbc_stateLbl);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setMaximum(100);
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.insets = new Insets(0, 0, 5, 5);
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridwidth = 4;
		gbc_progressBar.gridx = 2;
		gbc_progressBar.gridy = 3;
		getContentPane().add(progressBar, gbc_progressBar);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.gridwidth = 6;
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut.gridx = 0;
		gbc_horizontalStrut.gridy = 4;
		getContentPane().add(horizontalStrut, gbc_horizontalStrut);

		cancelBtn = new CustomButton();
		cancelBtn.setText(Messages.getString("UploadingCorpusDialog.cancelBtn.text"));
		view.addLocaleChangeListener(cancelBtn);
		cancelBtn.addActionListener(e -> cancelUploadCorpusAction.actionPerformed(e));
		GridBagConstraints gbc_cancelBtn = new GridBagConstraints();
		gbc_cancelBtn.gridwidth = 6;
		gbc_cancelBtn.gridx = 0;
		gbc_cancelBtn.gridy = 6;
		getContentPane().add(cancelBtn, gbc_cancelBtn);
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JButton getCancelBtn() {
		return cancelBtn;
	}
}
