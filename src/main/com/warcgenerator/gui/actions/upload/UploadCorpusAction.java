package com.warcgenerator.gui.actions.upload;

import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.common.LoginDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class UploadCorpusAction extends AbstractAction{
    private WarcGeneratorGUI view;
    private IAppLogic logic;
    private LoginDialog loginDialog;

    public UploadCorpusAction(IAppLogic logic,
                              WarcGeneratorGUI view) {
        this.view = view;
        this.logic = logic;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        loginDialog = new LoginDialog(view);
        view.updateUI();
        loginDialog.setVisible(true);
    }
}
