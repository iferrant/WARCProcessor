package com.warcgenerator.core.task.generateCorpus;

import com.warcgenerator.core.config.Constants;
import com.warcgenerator.core.config.OutputConfig;
import com.warcgenerator.core.datasource.IDataSource;
import com.warcgenerator.core.helper.ConfigHelper;
import com.warcgenerator.core.task.ITask;
import com.warcgenerator.core.task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;

/**
 * Balance the corpus URLs based on the configuration files
 */
public class BalanceCorpusTask extends Task implements ITask{
    private IDataSource spamDS;
    private IDataSource hamDS;
    private int percentageSpam;
    private int percentageHam;
    private int linesSpam = 0;
    private int linesHam = 0;
    private OutputConfig appConfig;

    private static Logger logger = Logger.getLogger(BalanceCorpusTask.class);

    public BalanceCorpusTask(OutputConfig appConfig, IDataSource spamDS, IDataSource hamDS, int percentageSpam) {
        this.spamDS = spamDS;
        this.hamDS = hamDS;
        this.percentageSpam = percentageSpam;
        this.percentageHam = 100 - percentageSpam;
        this.appConfig = appConfig;
    }

    public void execute() {
        logger.info("Task start");

        try {
            linesSpam = countLines(spamDS.getDataSourceConfig().getFilePath());
            linesHam = countLines(hamDS.getDataSourceConfig().getFilePath());
            saveConfigFileCopy();
        } catch (IOException e) {
            logger.error("" + e.getMessage());
        }

        balanceCorpus();
    }

    @Override
    public void rollback() {

    }

    /**
     * Count number of lines of a file
     * @param filename File path/name
     * @return Number of lines
     * @throws IOException
     */
    private int countLines(String filename) throws IOException{
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

    /**
     * Analize corpus files to know how many URLs are need
     * it to substract to balance the corpus
     */
    private void balanceCorpus() {
        long linesToSubtractHam = 0;
        long linesToSubtractSpam = 0;

        // Balance files based on the different percentages
        // and in the number of lines of each file
        if (percentageSpam == percentageHam) {
            if (linesSpam >= linesHam)
                linesToSubtractSpam = linesSpam - linesHam;
            else
                linesToSubtractHam = linesHam - linesSpam;
        } else if (percentageSpam < percentageHam) {
            long linesAux = (linesHam * percentageSpam) / percentageHam;
            if (linesSpam < linesAux) {
                linesAux = (linesSpam * percentageHam) / percentageSpam;
                linesToSubtractHam = linesHam - linesAux;
            } else {
                linesToSubtractSpam = linesSpam - linesAux;
            }
        } else {
            long linesAux = (linesSpam * percentageHam) / percentageSpam;
            if (linesHam < linesAux) {
                linesAux = (linesHam * percentageSpam) / percentageHam;
                linesToSubtractSpam = linesSpam - linesAux;
            } else {
                linesToSubtractHam = linesHam - linesAux;
            }
        }

        if (linesSpam > 0)
            substractLines(linesSpam - linesToSubtractSpam,
                    spamDS.getDataSourceConfig().getFilePath(),
                    spamDS.getDataSourceConfig().getFilePath() + "_corpus");

        if (linesHam > 0)
            substractLines(linesHam - linesToSubtractHam,
                    hamDS.getDataSourceConfig().getFilePath(),
                    hamDS.getDataSourceConfig().getFilePath() + "_corpus");
    }

    /**
     * Subtract number of lines to balance the corpus
     * @param linesToSubstract Number of lines to subtract
     * @param inputFileName Name or path of the input file generated with all the files
     * @param outputFileName Name or path of the output file
     */
    private void substractLines(long linesToSubstract, String inputFileName, String outputFileName) {
        try {
            File inputFile = new File(inputFileName);
            File outputFile = new File(outputFileName);

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

                String line = null;
                for (int i=0; i<linesToSubstract; i++) {
                    if ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }

            if (inputFile.delete()) {
                if (!outputFile.renameTo(inputFile)) {
                    throw new IOException("Could not rename " + outputFileName + " to " + inputFileName);
                }
            } else {
                throw new IOException("Could not delete original input file " + inputFileName);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Save a copy of the configuration file on the output directory
     */
    private void saveConfigFileCopy() {
        File source = new File(ConfigHelper.getConfigFilePath());
        File dest = new File(appConfig.getOutputDir() + File.separator + Constants.corpusConfigFile);

        try {
            Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
