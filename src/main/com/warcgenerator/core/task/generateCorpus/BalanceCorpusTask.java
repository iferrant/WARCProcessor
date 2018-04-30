package com.warcgenerator.core.task.generateCorpus;

import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.task.generateCorpus.balancer.CustomWarcWriter;
import com.warcgenerator.core.task.generateCorpus.state.GenerateCorpusState;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Balance the corpus URLs based on the configuration files
 * This object doesn't extends from {@link com.warcgenerator.core.task.Task}
 * and implements the {@link com.warcgenerator.core.task.ITask} interface
 * because can't be executed on the task batch. The warc files need
 * to be close to be read and modified.
 */
public class BalanceCorpusTask {
    private String spamFolderPath;
    private String hamFolderPath;
    private int percentageSpam;
    private int percentageHam;
    private float balancedNumHamRecords = 0;
    private float balancedNumSpamRecords = 0;
    private GenerateCorpusState generateCorpusState;
    private AppConfig appConfig;

    private static Logger logger = Logger.getLogger(BalanceCorpusTask.class);

    public BalanceCorpusTask(GenerateCorpusState generateCorpusState, String spamFolderPath,
                             String hamFolderPath, AppConfig appConfig) {
        this.generateCorpusState = generateCorpusState;
        this.spamFolderPath = spamFolderPath;
        this.hamFolderPath = hamFolderPath;
        this.appConfig = appConfig;
    }

    public void execute() {
        logger.info("Task start");

        setCorpusPercentages();

        balanceCorpus(generateCorpusState.getNumUrlSpamCorrectlyLabeled(),
                generateCorpusState.getNumUrlHamCorrectlyLabeled());

        CustomWarcWriter writeSpam =
                new CustomWarcWriter(new File(spamFolderPath), (int) balancedNumSpamRecords);
        writeSpam.writeWarcs();
        CustomWarcWriter writeHam =
                new CustomWarcWriter(new File(hamFolderPath), (int) balancedNumHamRecords);
        writeHam.writeWarcs();
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
        if (percentageSpam == 0 || percentageHam == 0) {
            balancedNumSpamRecords = 0;
            balancedNumHamRecords = 0;
        }

        generateCorpusState.setNumUrlSpamCorrectlyLabeled((int) balancedNumSpamRecords);
        generateCorpusState.setNumUrlHamCorrectlyLabeled((int) balancedNumHamRecords);
    }

}
