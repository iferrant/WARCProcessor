package com.warcgenerator.gui.actions.upload;

import com.warcgenerator.core.config.Constants;
import com.warcgenerator.core.config.CorpusSummary;
import com.warcgenerator.core.helper.CorpusValidatorHelper;
import com.warcgenerator.core.helper.XMLConfigHelper;
import com.warcgenerator.core.rest.ServerRequestService;
import com.warcgenerator.core.util.ZipUtils;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;

import javax.swing.*;
import java.awt.*;
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

        int userSelection = fileChooser.showDialog(view.getMainFrame(),
                Messages.getString("SelectCorpusFolder.uploadBtn.text"));
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToUpload = fileChooser.getSelectedFile().getAbsoluteFile();
            try {
                // Retrieve the corpus summary data
                CorpusSummary corpusSummary = XMLConfigHelper.getCorpusSummary(
                        fileToUpload.getAbsolutePath() + File.separator + Constants.corpusSummaryFile);
                // Check if the file selected is a corpus based on the corpus summary file
                if (corpusSummary != null
                        && CorpusValidatorHelper.isCorpusFolder(corpusSummary, fileToUpload.getPath())) {
                    corpusZipName = fileToUpload.getName() + ".zip";

                    new ZipUtils(this, fileToUpload.getAbsolutePath(), corpusZipName).pack();
                } else {
                    corpusNotValid(fileChooser);
                }
            }  catch (IOException ex) {
                corpusNotValid(fileChooser);
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        ServerRequestService serverRequestService = new ServerRequestService();
        serverRequestService.postCorpus(corpusZipName, corpusZipName);
    }

    private void corpusNotValid(Component component) {
        JOptionPane.showMessageDialog(component, "It's not a valid corpus");
    }

}
