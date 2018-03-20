package com.warcgenerator.gui.actions.upload;

import com.warcgenerator.core.rest.ServerRequestService;
import com.warcgenerator.core.util.ZipUtils;
import com.warcgenerator.gui.view.WarcGeneratorGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class UploadCorpusAction extends AbstractAction implements Observer {
    private WarcGeneratorGUI view;
    private String corpusZipName;

    public UploadCorpusAction(WarcGeneratorGUI view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = fileChooser.showSaveDialog(view.getMainFrame());
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToUpload = fileChooser.getSelectedFile().getAbsoluteFile();
            corpusZipName = fileToUpload.getName() + ".zip";
            try {
                new ZipUtils(this, fileToUpload.getAbsolutePath(), corpusZipName).pack();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        ServerRequestService serverRequestService = new ServerRequestService();
        serverRequestService.postCorpus(corpusZipName, corpusZipName);
    }

}
