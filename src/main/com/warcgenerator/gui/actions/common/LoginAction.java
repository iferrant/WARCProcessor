package com.warcgenerator.gui.actions.common;

import com.warcgenerator.AppWarc;
import com.warcgenerator.core.rest.RequestResponse;
import com.warcgenerator.core.rest.ServerRequestService;
import com.warcgenerator.core.rest.models.Token;
import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.common.LoginDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LoginAction extends AbstractAction {

    private WarcGeneratorGUI view;
    private LoginDialog loginDialog;

    public LoginAction(WarcGeneratorGUI view, LoginDialog loginDialog) {
        this.view = view;
        this.loginDialog = loginDialog;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        AppWarc.userGlobal.setEmail(loginDialog.getUserEmail().getValue().toString());
        AppWarc.userGlobal.setPassword(new String(loginDialog.getUserPassword().getPassword()));

        loginDialog.loading(true);
        loginDialog.showCredentialsError(false);
        ServerRequestService serverRequestService = new ServerRequestService();
        serverRequestService.loginUser(AppWarc.userGlobal, new RequestResponse<Token>() {
            @Override
            public void onRequestSuccess(Token response) {
                AppWarc.userGlobal.setToken(response);
                System.out.println("User token: " + response.getToken());
                loginDialog.setVisible(false);
            }

            @Override
            public void onRequestFail(String error) {
                System.err.println("ERROR: " + error);
                loginDialog.loading(false);
                loginDialog.showCredentialsError(true);
            }
        });
    }
}
