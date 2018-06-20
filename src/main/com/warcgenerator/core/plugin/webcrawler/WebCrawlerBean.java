package com.warcgenerator.core.plugin.webcrawler;

import com.warcgenerator.core.config.OutputCorpusConfig;
import com.warcgenerator.core.datasource.IDataSource;

public class WebCrawlerBean {
	private IDataSource domainsLabeledDS;
	private IDataSource domainsNotFoundDS;
	private boolean isSpam;
	private OutputCorpusConfig outputCorpusConfig;
	
	public WebCrawlerBean(IDataSource domainsLabeledDS,
						  IDataSource domainsNotFoundDS,
						  boolean isSpam,
						  OutputCorpusConfig outputCorpusConfig) {
		this.domainsLabeledDS = domainsLabeledDS;
		this.domainsNotFoundDS = domainsNotFoundDS;
		this.isSpam = isSpam;
		this.setOutputCorpusConfig(outputCorpusConfig);
	}

    public IDataSource getDomainsLabeledDS() {
		return domainsLabeledDS;
	}
	public void setDomainsLabeledDS(IDataSource domainsLabeledDS) {
		this.domainsLabeledDS = domainsLabeledDS;
	}
	public IDataSource getDomainsNotFoundDS() {
		return domainsNotFoundDS;
	}
	public void setDomainsNotFoundDS(IDataSource domainsNotFoundDS) {
		this.domainsNotFoundDS = domainsNotFoundDS;
	}
	public boolean isSpam() {
		return isSpam;
	}
	public void setSpam(boolean isSpam) {
		this.isSpam = isSpam;
	}

	public OutputCorpusConfig getOutputCorpusConfig() {
		return outputCorpusConfig;
	}

	public void setOutputCorpusConfig(OutputCorpusConfig outputCorpusConfig) {
		this.outputCorpusConfig = outputCorpusConfig;
	}
}
