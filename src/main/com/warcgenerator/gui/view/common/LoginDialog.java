package com.warcgenerator.gui.view.common;

import com.warcgenerator.AppWarc;
import com.warcgenerator.gui.actions.common.LoginAction;
import com.warcgenerator.gui.components.CustomButton;
import com.warcgenerator.gui.components.CustomJDialog;
import com.warcgenerator.gui.components.CustomLabel;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog to introduce the user credentials
 */
public class LoginDialog extends CustomJDialog{
    private static final String DEFAULT_USER = "jane@warcrepository.com";
    private static final String DEFAULT_PASSWORD = "secret";
    private LoginAction loginAction;
    private JFormattedTextField userEmail;
    private JPasswordField userPassword;
    private CustomLabel lblUserEmail;
    private CustomLabel lblUserPassword;
    private CustomLabel lblWrongCredentials;
    private JLabel loadingLabel;

    public LoginDialog(WarcGeneratorGUI view) {
        super(view.getMainFrame(), true);
        loginAction = new LoginAction(view, this);

        System.out.println(Messages.getString("LoginDialog.title.text"));

        setTitle(Messages.getString("LoginDialog.title.text"));
        this.setBounds(0, 0, 300, 180);

        CustomButton acceptBtn = new CustomButton();
        acceptBtn.setName("LoginDialog.btnAccept.text");
        view.addLocaleChangeListener(acceptBtn);
        acceptBtn.addActionListener(actionEvent -> loginAction.actionPerformed(actionEvent));

        JPanel panel_1 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        add(panel_1, BorderLayout.SOUTH);
        panel_1.add(acceptBtn);

        JPanel panel_2 = new JPanel();
        add(panel_2, BorderLayout.CENTER);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        panel_2.setLayout(gbl_panel_2);

        Component horizontalStrut = Box.createHorizontalStrut(20);
        GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
        gbc_horizontalStrut.gridwidth = 3;
        gbc_horizontalStrut.insets = new Insets(10, 0, 5, 5);
        gbc_horizontalStrut.gridx = 0;
        gbc_horizontalStrut.gridy = 0;
        panel_2.add(horizontalStrut, gbc_horizontalStrut);

        lblUserEmail = new CustomLabel();
        lblUserEmail.setName("LoginDialog.lblUserEmail.text");
        view.addLocaleChangeListener(lblUserEmail);
        lblUserEmail.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_lblUserEmail = new GridBagConstraints();
        gbc_lblUserEmail.fill = GridBagConstraints.BOTH;
        gbc_lblUserEmail.insets = new Insets(0, 0, 5, 5);
        gbc_lblUserEmail.gridx = 0;
        gbc_lblUserEmail.gridy = 0;
        panel_2.add(lblUserEmail, gbc_lblUserEmail);

        String userDefaultEmail =
                AppWarc.userGlobal.getEmail() != null? AppWarc.userGlobal.getEmail(): DEFAULT_USER;
        userEmail = new JFormattedTextField(userDefaultEmail);
        userEmail.setEditable(true);
        userEmail.setColumns(16);
        GridBagConstraints gbc_userEmail = new GridBagConstraints();
        gbc_userEmail.fill = GridBagConstraints.BOTH;
        gbc_userEmail.insets = new Insets(0, 0, 5, 5);
        gbc_userEmail.gridx = 1;
        gbc_userEmail.gridy = 0;
        panel_2.add(userEmail, gbc_userEmail);

        lblUserPassword = new CustomLabel();
        lblUserPassword.setName("LoginDialog.lblUserPassword.text");
        view.addLocaleChangeListener(lblUserPassword);
        lblUserPassword.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_lblUserPassword = new GridBagConstraints();
        gbc_lblUserPassword.fill = GridBagConstraints.BOTH;
        gbc_lblUserPassword.insets = new Insets(0, 0, 5, 5);
        gbc_lblUserPassword.gridx = 0;
        gbc_lblUserPassword.gridy = 1;
        panel_2.add(lblUserPassword, gbc_lblUserPassword);

        String userDefaultPass =
                AppWarc.userGlobal.getPassword() != null? AppWarc.userGlobal.getPassword(): DEFAULT_PASSWORD;
        userPassword = new JPasswordField(userDefaultPass);
        userPassword.setEditable(true);
        userPassword.setColumns(16);
        GridBagConstraints gbc_userPassword = new GridBagConstraints();
        gbc_userPassword.fill = GridBagConstraints.BOTH;
        gbc_userPassword.insets = new Insets(0, 0, 5, 5);
        gbc_userPassword.gridx = 1;
        gbc_userPassword.gridy = 1;
        panel_2.add(userPassword, gbc_userPassword);

        lblWrongCredentials = new CustomLabel();
        lblWrongCredentials.setName("LoginDialog.lblWrongCredentials.text");
        lblWrongCredentials.setForeground(Color.RED);
        view.addLocaleChangeListener(lblWrongCredentials);
        lblWrongCredentials.setHorizontalAlignment(SwingConstants.RIGHT);
        lblWrongCredentials.setVisible(false);
        GridBagConstraints gbc_lblWrongCredentials = new GridBagConstraints();
        gbc_lblWrongCredentials.fill = GridBagConstraints.BOTH;
        gbc_lblWrongCredentials.insets = new Insets(0, 0, 5, 5);
        gbc_lblWrongCredentials.gridx = 1;
        gbc_lblWrongCredentials.gridy = 2;
        panel_2.add(lblWrongCredentials, gbc_lblWrongCredentials);

        loadingLabel = new JLabel("");
        loadingLabel
                .setIcon(new ImageIcon(
                        LoginDialog.class
                                .getResource("/com/warcgenerator/gui/resources/img/imagen_gif.gif")));
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.gridheight = 2;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 1;
        gbc_lblNewLabel.gridy = 1;
        loadingLabel.setVisible(false);
        panel_2.add(loadingLabel, gbc_lblNewLabel);
    }

    /**
     * Show/Hide the loading animation
     * @param visibility True to show the loading animation
     */
    public void loading(Boolean visibility) {
        userEmail.setVisible(!visibility);
        userPassword.setVisible(!visibility);
        lblUserEmail.setVisible(!visibility);
        lblUserPassword.setVisible(!visibility);
        loadingLabel.setVisible(visibility);
    }

    public void showCredentialsError(Boolean show) {
        lblWrongCredentials.setVisible(show);
    }

    public JFormattedTextField getUserEmail() {
        return userEmail;
    }

    public JPasswordField getUserPassword() {
        return userPassword;
    }
}
