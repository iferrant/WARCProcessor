package com.warcgenerator.gui.view.common;

import com.warcgenerator.AppWarc;
import com.warcgenerator.gui.components.CustomButton;
import com.warcgenerator.gui.components.CustomJDialog;
import com.warcgenerator.gui.components.CustomLabel;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog to introduce the user credentials
 */
public class LoginDialog extends CustomJDialog{
    private static final String DEFAULT_USER = "demo";
    private static final String DEFAULT_PASSWORD = "demo";
    private JFormattedTextField userEmail;
    private JPasswordField userPassword;

    public LoginDialog(WarcGeneratorGUI view) {
        super(view.getMainFrame(), true);

        System.out.println(Messages.getString("LoginDialog.title.text"));

        setTitle(Messages.getString("LoginDialog.title.text"));
        this.setBounds(0, 0, 300, 180);

        CustomButton acceptBtn = new CustomButton();
        acceptBtn.setName("LoginDialog.btnAccept.text");
        view.addLocaleChangeListener(acceptBtn);
        acceptBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Request token
                AppWarc.userGlobal.setEmail(userEmail.getValue().toString());
                AppWarc.userGlobal.setPassword(new String(userPassword.getPassword()));
            }
        });

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

        CustomLabel lblUserEmail = new CustomLabel();
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

        CustomLabel lblUserPassword = new CustomLabel();
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
    }
}
