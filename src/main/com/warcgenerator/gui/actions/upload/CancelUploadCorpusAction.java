package com.warcgenerator.gui.actions.upload;

import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CancelUploadCorpusAction extends AbstractAction {
	private WarcGeneratorGUI view;
	private UploadCorpusAction uploadAction;

	public CancelUploadCorpusAction(WarcGeneratorGUI view,
                                    UploadCorpusAction uploadAction) {
		this.view = view;
		this.uploadAction = uploadAction;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int userSelection = JOptionPane
				.showConfirmDialog(
						view.getMainFrame(),
						Messages.getString("CancelGenerateCorpusAction.cancel.text"),
						Messages.getString("CancelGenerateCorpusAction.cancel.title.text"),
						JOptionPane.OK_CANCEL_OPTION);
		if (userSelection == JOptionPane.OK_OPTION) {

			uploadAction.cancelCorpusUpload();
		}
	}
}
