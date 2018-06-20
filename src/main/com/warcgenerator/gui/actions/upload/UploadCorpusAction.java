package com.warcgenerator.gui.actions.upload;

import com.warcgenerator.AppWarc;
import com.warcgenerator.core.config.Constants;
import com.warcgenerator.core.config.CorpusSummary;
import com.warcgenerator.core.helper.CorpusValidatorHelper;
import com.warcgenerator.core.helper.XMLConfigHelper;
import com.warcgenerator.core.rest.RequestResponse;
import com.warcgenerator.core.rest.ServerRequestService;
import com.warcgenerator.core.rest.models.Token;
import com.warcgenerator.core.util.ZipUtils;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.upload.UploadingCorpusDialog;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

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
    private DisposableSubscriber disposableSubscriber;

    public UploadCorpusAction(WarcGeneratorGUI view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        uploadingCorpusDialog = new UploadingCorpusDialog(view, this);
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
                    // zip file
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
        final ServerRequestService serverRequestService = new ServerRequestService();
        serverRequestService.refreshToken(new RequestResponse<Token>() {
            @Override
            public void onRequestSuccess(Token response) {
                AppWarc.userGlobal.setToken(response);
                // The response is correct and  the user has a new token
                // so we can upload the corpus to the server
                uploadCorpus(serverRequestService);
            }

            @Override
            public void onRequestFail(String error) {
                showError(new Throwable(error));
            }
        });
    }

    /**
     * Cancels the corpus upload
     */
    public void cancelCorpusUpload() {
        if (disposableSubscriber != null && !disposableSubscriber.isDisposed()) {
            disposableSubscriber.dispose();
            System.out.println("Upload canceled");
            uploadCompleted();
        } else {
            System.out.println("Disposable is null");
        }
    }

    /**
     * Uploads the corpus to the server
     * @param serverRequestService {@link ServerRequestService} service to perform the request
     */
    private void uploadCorpus(ServerRequestService serverRequestService) {
        disposableSubscriber = serverRequestService
                .postCorpus(corpusZipName)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .doOnCancel(this::uploadCompleted)
                .subscribeWith(new DisposableSubscriber<Double>() {
                    @Override
                    public void onNext(Double aDouble) {
                        setProgress(aDouble);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showError(throwable);
                    }

                    @Override
                    public void onComplete() {
                        uploadCompleted();
                    }
                });

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
     * @param throwable {@link Throwable} exception thrown
     */
    private void showError(Throwable throwable) {
        // if the upload is canceled, the InterruptedException is thrown
        // and we don't want to show the error dialog in this situation
        if (!(throwable instanceof InterruptedException)) {
            JOptionPane.showMessageDialog(
                    view.getMainFrame(),
                    Messages.getString("UploadingCorpusDialog.error.text"),
                    Messages.getString("UploadingCorpusDialog.error.title"),
                    JOptionPane.ERROR_MESSAGE);
            uploadingCorpusDialog.setVisible(false);
            new ZipUtils().removeZip(corpusZipName);

            System.err.println(throwable.getMessage());
            throwable.printStackTrace();
        }
    }

    /**
     * Removes the zip file and hides the upload progress dialog
     */
    private void uploadCompleted() {
        new ZipUtils().removeZip(corpusZipName);
        uploadingCorpusDialog.setVisible(false);
        System.out.println("Upload finished");
    }

    private void corpusNotValid(Component component) {
        JOptionPane.showMessageDialog(component, "It's not a valid corpus");
    }

}
