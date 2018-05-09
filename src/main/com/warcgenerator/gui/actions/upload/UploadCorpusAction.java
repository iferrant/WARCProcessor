package com.warcgenerator.gui.actions.upload;

import com.warcgenerator.core.config.Constants;
import com.warcgenerator.core.config.CorpusSummary;
import com.warcgenerator.core.helper.CorpusValidatorHelper;
import com.warcgenerator.core.helper.XMLConfigHelper;
import com.warcgenerator.core.rest.ServerRequestService;
import com.warcgenerator.core.util.ZipUtils;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.upload.UploadingCorpusDialog;
import io.reactivex.schedulers.Schedulers;

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
    private JFileChooser fileChooser;
    private UploadingCorpusDialog uploadingCorpusDialog;

    public UploadCorpusAction(WarcGeneratorGUI view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        uploadingCorpusDialog = new UploadingCorpusDialog(view);
        fileChooser = new JFileChooser();
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

                    new ZipUtils().pack(this, fileToUpload.getAbsolutePath(), corpusZipName);
                    // Show upload progress bar
                    uploadingCorpusDialog.setVisible(true);
                } else {
                    corpusNotValid(fileChooser);
                }
            }  catch (IOException ex) {
                corpusNotValid(fileChooser);
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        ServerRequestService serverRequestService = new ServerRequestService();
        serverRequestService
                .postCorpus(corpusZipName)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .subscribe(
                        this::setProgress,
                        error -> showError(error.getMessage()),
                        this::uploadCompleted);
    }

    /**
     * Sets the upload progress on the dialog progress bar
     * @param progress Double with upload progress
     */
    private void setProgress(double progress) {
        if (uploadingCorpusDialog != null) {
            uploadingCorpusDialog.getProgressBar().setValue((int) (100 * progress));
        }
    }

    /**
     * Shows an error if something goes wrong while the corpus is uploading
     * @param message Error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(
                view.getMainFrame(),
                Messages.getString("UploadingCorpusDialog.error.text"),
                Messages.getString("UploadingCorpusDialog.error.title"),
                JOptionPane.ERROR_MESSAGE);
        uploadingCorpusDialog.setVisible(false);
        new ZipUtils().removeZip(corpusZipName);
    }

    /**
     *
     */
    private void uploadCompleted() {
        new ZipUtils().removeZip(corpusZipName);
        uploadingCorpusDialog.setVisible(false);
        System.out.println("Upload completed");
    }

    private void corpusNotValid(Component component) {
        JOptionPane.showMessageDialog(component, "It's not a valid corpus");
    }

}
