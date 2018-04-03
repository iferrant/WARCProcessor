package com.warcgenerator.core.config;

/**
 * Represents the data of the config_summary file
 * that is necessary to upload the corpus to the API
 */
public class CorpusSummary {

    private String spamDir;     // Name of the spam directory configured on the 'Output' panel
    private String hamDir;      // Name of the ham directory configured on the 'Output' panel
    private int numSpamPages;   // Number of spam pages as result of the execution of the app
    private int numHamPages;    // Number of ham pages as result of the execution of the app

    public CorpusSummary(String spamDir, String hamDir, int numSpamPages, int numHamPages) {
        this.spamDir = spamDir;
        this.hamDir = hamDir;
        this.numSpamPages = numSpamPages;
        this.numHamPages = numHamPages;
    }

    public String getSpamDir() {
        return spamDir;
    }

    public void setSpamDir(String spamDir) {
        this.spamDir = spamDir;
    }

    public String getHamDir() {
        return hamDir;
    }

    public void setHamDir(String hamDir) {
        this.hamDir = hamDir;
    }

    public int getNumSpamPages() {
        return numSpamPages;
    }

    public void setNumSpamPages(int numSpamPages) {
        this.numSpamPages = numSpamPages;
    }

    public int getNumHamPages() {
        return numHamPages;
    }

    public void setNumHamPages(int numHamPages) {
        this.numHamPages = numHamPages;
    }
}
