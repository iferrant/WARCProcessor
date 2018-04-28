package com.warcgenerator.core.task.generateCorpus;

import com.warcgenerator.core.config.OutputCorpusConfig;
import com.warcgenerator.core.datasource.IDataSource;
import com.warcgenerator.core.task.ITask;
import com.warcgenerator.core.task.Task;

import org.apache.log4j.Logger;

/**
 * Balance the corpus URLs based on the configuration files
 */
public class BalanceCorpusTask extends Task implements ITask{
    private IDataSource spamDS;
    private IDataSource hamDS;
    private int percentageSpam;
    private int percentageHam;
    private OutputCorpusConfig outputCorpusConfig;

    private static Logger logger = Logger.getLogger(BalanceCorpusTask.class);

    public BalanceCorpusTask(OutputCorpusConfig outputCorpusConfig, IDataSource spamDS, IDataSource hamDS, int percentageSpam) {
        this.spamDS = spamDS;
        this.hamDS = hamDS;
        this.percentageSpam = percentageSpam;
        this.percentageHam = 100 - percentageSpam;
        this.outputCorpusConfig = outputCorpusConfig;
    }

    public void execute() {
        logger.info("Task start");

    }

    @Override
    public void rollback() {

    }

}
