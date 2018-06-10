package com.warcgenerator.core.balancer;

import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.balancer.writer.CustomWarcWriter;
import com.warcgenerator.core.task.generateCorpus.state.GenerateCorpusState;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Balance the corpus URLs based on the configuration files
 * This object doesn't extends from {@link com.warcgenerator.core.task.Task}
 * and implements the {@link com.warcgenerator.core.task.ITask} interface
 * because can't be executed on the task batch. The warc files need
 * to be close to be read and modified.
 */
public class BalanceCorpus {
    private String spamFolderPath;
    private String hamFolderPath;
    private int percentageSpam;
    private int percentageHam;
    private float balancedNumHamRecords = 0;
    private float balancedNumSpamRecords = 0;
    private GenerateCorpusState generateCorpusState;
    private AppConfig appConfig;

    private static Logger logger = Logger.getLogger(BalanceCorpus.class);

    public BalanceCorpus(GenerateCorpusState generateCorpusState, String spamFolderPath,
                         String hamFolderPath, AppConfig appConfig) {
        this.generateCorpusState = generateCorpusState;
        this.spamFolderPath = spamFolderPath;
        this.hamFolderPath = hamFolderPath;
        this.appConfig = appConfig;
    }

    public void execute() {
        logger.info("Task start");

        setCorpusPercentages();

        if (percentageSpam != 0 && percentageHam != 0) {
            balanceCorpus(generateCorpusState.getNumUrlSpamCorrectlyLabeled(),
                    generateCorpusState.getNumUrlHamCorrectlyLabeled());
        } else {
            balancedNumSpamRecords = generateCorpusState.getNumUrlSpamCorrectlyLabeled();
            balancedNumHamRecords = generateCorpusState.getNumUrlHamCorrectlyLabeled();
        }

        CustomWarcWriter writeSpam =
                new CustomWarcWriter(new File(spamFolderPath), (int) balancedNumSpamRecords);
        writeSpam.writeWarcs();
        CustomWarcWriter writeHam =
                new CustomWarcWriter(new File(hamFolderPath), (int) balancedNumHamRecords);
        writeHam.writeWarcs();

        removeOriginalFolders();
        renameResultFolders();
    }

    /**
     * Calculates the spam/ham percentages based on the configuration
     */
    private void setCorpusPercentages() {
        if (appConfig.getRatioIsPercentage()) {
            percentageSpam = appConfig.getRatioPercentageSpam();
        } else {
            percentageSpam = appConfig.getRatioQuantitySpam() * 100 / appConfig.getNumSites();
        }
        percentageHam = 100 - percentageSpam;
    }

    /**
     * Calculates the number of records of each folder
     */
    private void balanceCorpus(int recordsSpam, int recordsHam) {
        balancedNumSpamRecords = recordsSpam;
        balancedNumHamRecords = recordsHam;
        // Balance files based on the different percentages
        // and in the number of lines of each file
        if (percentageSpam == percentageHam) {
            if (recordsSpam >= recordsHam)
                balancedNumSpamRecords = recordsHam;
            else
                balancedNumHamRecords = recordsSpam;
        } else if (percentageSpam < percentageHam) {
            long linesAux = (recordsHam * percentageSpam) / percentageHam;
            if (recordsSpam <= linesAux) {
                balancedNumHamRecords = Math.round((recordsSpam * percentageHam) / percentageSpam);
            } else {
                balancedNumSpamRecords = Math.round(linesAux);
            }
        } else {
            long linesAux = (recordsSpam * percentageHam) / percentageSpam;
            if (recordsHam <= linesAux) {
                balancedNumSpamRecords = Math.round((recordsHam * percentageSpam) / percentageHam);
            } else {
                balancedNumHamRecords = Math.round(linesAux);
            }
        }

        // If one of the percentages is 0, the other one must be 0 also
        if (balancedNumSpamRecords == 0 || balancedNumHamRecords == 0) {
            balancedNumSpamRecords = 0;
            balancedNumHamRecords = 0;
        }

        generateCorpusState.setNumUrlSpamCorrectlyLabeled((int) balancedNumSpamRecords);
        generateCorpusState.setNumUrlHamCorrectlyLabeled((int) balancedNumHamRecords);
    }

    /**
     * Remove original spam and ham folders. This folders can be deleted because they
     * are not balanced. The result of the balance is saved on the *_copy_ directories
     */
    private void removeOriginalFolders() {
        try {
            FileUtils.deleteDirectory(new File(spamFolderPath));
            FileUtils.deleteDirectory(new File(hamFolderPath));
        } catch (IOException e) {
            logger.error("Error deleting original spam/ham folders");
            e.printStackTrace();
        }
    }

    /**
     * Rename the spam and ham folder that have been balanced. This folder are named
     * using the original names and the <code>FOLDER_SUFFIX</code> but the result
     * must be with the original names
     */
    private void renameResultFolders() {
        try {
            // Rename spam folder
            String spamBalancedCorpus = spamFolderPath + CustomWarcWriter.FOLDER_SUFFIX;
            new File(spamBalancedCorpus).renameTo(new File(spamFolderPath));

            // Rename ham folder
            String hamBalancedCorpus = hamFolderPath + CustomWarcWriter.FOLDER_SUFFIX;
            new File(hamBalancedCorpus).renameTo(new File(hamFolderPath));
        } catch (Exception e) {
            logger.error("Error renaming the corpus's spam/ham folders");
            e.printStackTrace();
        }
    }

}
