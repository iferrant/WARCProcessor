package com.warcgenerator.gui.actions.common;

import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.common.LoginDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LoginAction extends AbstractAction{

    private WarcGeneratorGUI view;
    private LoginDialog loginDialog;

    public LoginAction(WarcGeneratorGUI view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        loginDialog = new LoginDialog(view);
        view.updateUI();
        loginDialog.setVisible(true);
    }
}
