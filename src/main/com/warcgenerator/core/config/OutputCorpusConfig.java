package com.warcgenerator.core.config;

import java.io.File;

/**
 * File with the output corpus configuration
 * 
 * @author Miguel Callon
 * 
 */
public class OutputCorpusConfig extends OutputConfig {
	private String corpusDir;
	private String spamDir;
	private String hamDir;
	private String domainsLabeledFilePath;
	private String domainsNotFoundFilePath;

	public OutputCorpusConfig() {
		super();
	}
	
	public OutputCorpusConfig(String outputDir, String corpusDir, String spamDir, String hamDir,
			String domainsLabeledFileName, String domainsNotFoundFilePath) {
		super(outputDir);
		this.corpusDir = corpusDir;
		this.spamDir = spamDir;
		this.hamDir = hamDir;
		this.domainsLabeledFilePath = domainsLabeledFileName;
		this.domainsNotFoundFilePath = domainsNotFoundFilePath;
	}

	@Override
	public String getOutputDir() {
		return getCorpusDir();
	}

	public String getCorpusDir() {
		return corpusDir;
	}

	public void setCorpusDir(String corpusDir) {
		this.corpusDir = corpusDir;
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
