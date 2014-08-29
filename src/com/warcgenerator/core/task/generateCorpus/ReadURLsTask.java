package com.warcgenerator.core.task.generateCorpus;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.warcgenerator.core.common.GenerateCorpusState;
import com.warcgenerator.core.common.GenerateCorpusStates;
import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.config.OutputCorpusConfig;
import com.warcgenerator.core.config.WebCrawlerConfig;
import com.warcgenerator.core.datasource.DataSource;
import com.warcgenerator.core.datasource.IDataSource;
import com.warcgenerator.core.datasource.bean.DataBean;
import com.warcgenerator.core.plugin.webcrawler.Crawler4JAdapter;
import com.warcgenerator.core.plugin.webcrawler.IWebCrawler;
import com.warcgenerator.core.plugin.webcrawler.WebCrawlerBean;
import com.warcgenerator.core.task.ITask;
import com.warcgenerator.core.task.Task;

public class ReadURLsTask extends Task implements ITask {
	private GenerateCorpusState generateCorpusState;
	private AppConfig config;
	private Map<String, DataBean> urls;
	private IDataSource labeledDS, notFoundDS;
	private OutputCorpusConfig outputCorpusConfig;
	private IWebCrawler webCrawler;
	private Map<String, DataSource> outputDS;
	private Set<String> urlsActive;
	private Set<String> urlsNotActive;
	
	private static Logger logger = Logger.getLogger
            (ReadURLsTask.class);
	
	public ReadURLsTask(AppConfig config,
			OutputCorpusConfig outputCorpusConfig,
			GenerateCorpusState generateCorpusState,
			Map<String, DataSource> outputDS,
			IDataSource labeledDS,
			IDataSource notFoundDS,
			Map<String, DataBean> urls, 
			Set<String> urlsActive,
			Set<String> urlsNotActive) {
		this.config = config;
		this.outputCorpusConfig = outputCorpusConfig;
		this.generateCorpusState = generateCorpusState;
		this.outputDS = outputDS;
		this.labeledDS = labeledDS;
		this.notFoundDS = notFoundDS;
		this.urls = urls;
		this.urlsActive = urlsActive;
		this.urlsNotActive = urlsNotActive;
	}
	
	public void execute() {
		logger.info("Task start: ReadSpamTask");
		
		if (urls.size() > 0) {
			generateCorpusState.setState(GenerateCorpusStates.READING_URLS);
			generateCorpusState.setWebsToVisitTotal(urls.size());
			WebCrawlerBean webCrawlerBean = new WebCrawlerBean(labeledDS,
					notFoundDS, true, outputCorpusConfig);
			
			WebCrawlerConfig webCrawlerConfig = new WebCrawlerConfig(
					config.getWebCrawlerCfgTemplate());
			webCrawlerConfig.setUrls(urls.keySet());
			IWebCrawler webCrawler = new Crawler4JAdapter(
					config,
					generateCorpusState,
					webCrawlerConfig, 
					webCrawlerBean, 
					outputDS, 
					urls,
					urlsActive,
					urlsNotActive);
	
			// Start crawler
			webCrawler.start();
		}
		
		logger.info("Task end: ReadSpamTask");
	}
	
	public void rollback() {
		System.out.println("Rollback ReadSpamTask");
		if (webCrawler != null)
			webCrawler.stop();
	}
}