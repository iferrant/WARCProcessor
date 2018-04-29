package com.warcgenerator.core.task.generateCorpus;

import com.warcgenerator.core.datasource.IDataSource;
import com.warcgenerator.core.task.ITask;
import com.warcgenerator.core.task.Task;

import com.warcgenerator.core.task.generateCorpus.balancer.CustomWarcWriter;
import com.warcgenerator.core.task.generateCorpus.state.GenerateCorpusState;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Balance the corpus URLs based on the configuration files
 */
public class BalanceCorpusTask extends Task implements ITask{
    private IDataSource spamDS;
    private IDataSource hamDS;
    private int percentageSpam;
    private int percentageHam;
    private float balancedNumHamRecords = 0;
    private float balancedNumSpamRecords = 0;
    private GenerateCorpusState generateCorpusState;

    private static Logger logger = Logger.getLogger(BalanceCorpusTask.class);

    public BalanceCorpusTask(GenerateCorpusState generateCorpusState, IDataSource spamDS, IDataSource hamDS, int percentageSpam) {
        this.spamDS = spamDS;
        this.hamDS = hamDS;
        this.percentageSpam = percentageSpam;
        this.percentageHam = 100 - percentageSpam;
        this.generateCorpusState = generateCorpusState;
    }

    public void execute() {
        logger.info("Task start");

        balanceCorpus(generateCorpusState.getNumUrlSpamCorrectlyLabeled(),
                generateCorpusState.getNumUrlHamCorrectlyLabeled());

        CustomWarcWriter writeSpam =
                new CustomWarcWriter(new File(spamDS.getDataSourceConfig().getFilePath()),
                        (int) balancedNumSpamRecords);
        writeSpam.writeWarcs();
        CustomWarcWriter writeHam =
                new CustomWarcWriter(new File(hamDS.getDataSourceConfig().getFilePath()),
                        (int) balancedNumHamRecords);
        writeHam.writeWarcs();
    }

    @Override
    public void rollback() {

    }

    /**
     * Calculates the number of records of each folder
     */
    private void balanceCorpus(int recordsSpam, int recordsHam) {
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
    }

}
