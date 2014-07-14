package com.warcgenerator.core.config;

/**
 * File with the output corpus configuration
 * 
 * @author Miguel Callon
 * 
 */
public class OutputCorpusConfig extends OutputConfig {
	private String spamDir;
	private String hamDir;
	private String domainsLabeledFilePath;
	private String domainsNotFoundFilePath;

	public OutputCorpusConfig(String outputDir, String spamDir, String hamDir,
			String domainsLabeledFileName, String domainsNotFoundFilePath) {
		super(outputDir);
		this.spamDir = spamDir;
		this.hamDir = hamDir;
		this.domainsLabeledFilePath = domainsLabeledFileName;
		this.domainsNotFoundFilePath = domainsNotFoundFilePath;
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

	public String getDomainsLabeledFilePath() {
		return domainsLabeledFilePath;
	}

	public void setDomainsLabeledFilePath
		(String domainsLabeledFilePath) {
		this.domainsLabeledFilePath = domainsLabeledFilePath;
	}

	public String getDomainsNotFoundFilePath() {
		return domainsNotFoundFilePath;
	}

	public void setDomainsNotFoundFilePath(String domainsNotFoundFilePath) {
		this.domainsNotFoundFilePath = domainsNotFoundFilePath;
	}
}
