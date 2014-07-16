package com.warcgenerator.core.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.config.DataSourceConfig;
import com.warcgenerator.core.config.OutputCorpusConfig;
import com.warcgenerator.core.config.WebCrawlerConfig;
import com.warcgenerator.core.datasource.DataSource;
import com.warcgenerator.core.datasource.GenericDS;
import com.warcgenerator.core.datasource.IDataSource;
import com.warcgenerator.core.datasource.handler.IDSHandler;
import com.warcgenerator.core.exception.logic.LogicException;
import com.warcgenerator.core.exception.logic.OutCorpusCfgNotFoundException;
import com.warcgenerator.core.helper.ConfigHelper;
import com.warcgenerator.core.helper.FileHelper;
import com.warcgenerator.core.plugin.webcrawler.Crawler4JAdapter;
import com.warcgenerator.core.plugin.webcrawler.IWebCrawler;
import com.warcgenerator.core.plugin.webcrawler.IWebCrawlerHandler;

/**
 * Business logic layer
 * 
 * @author Miguel Callon
 * 
 */
public class AppLogicImpl extends AppLogic implements IAppLogic {
	private List<IDSHandler> dsHandlers;
	private AppConfig config;
	private OutputCorpusConfig outputCorpusConfig;

	public AppLogicImpl(AppConfig config) throws LogicException {
		this.config = config;
		dsHandlers = ConfigHelper.getDSHandlers(config);

		// Create a output corpus with config
		if (config.getOutputConfig() instanceof OutputCorpusConfig) {
			outputCorpusConfig = (OutputCorpusConfig) config.getOutputConfig();
		} else {
			throw new OutCorpusCfgNotFoundException();
		}

		// Corpus Path dirs
		String dirs[] = { outputCorpusConfig.getOutputDir(),
				outputCorpusConfig.getSpamDir(), outputCorpusConfig.getHamDir() };
		System.out.println("Directorios:");
		for (String dir : dirs) {
			System.out.println(dir);
		}

		FileHelper.createDirs(dirs);
	}

	public void generateCorpus() throws LogicException {
		// Generate wars
		IDataSource labeledDS = new GenericDS(new DataSourceConfig(
				outputCorpusConfig.getDomainsLabeledFilePath()));
		IDataSource notFoundDS = new GenericDS(new DataSourceConfig(
				outputCorpusConfig.getDomainsNotFoundFilePath()));		
		
		// Init data structures
		Set<String> urlsSpam = new HashSet<String>();
		Set<String> urlsHam = new HashSet<String>();
		Map<String, IWebCrawlerHandler> webCrawlerHandlersSpam = 
				new HashMap<String, IWebCrawlerHandler>();
		Map<String, IWebCrawlerHandler> webCrawlerHandlersHam = 
				new HashMap<String, IWebCrawlerHandler>();
		Map<String, DataSource> outputDS = 
				new HashMap<String, DataSource>();

		// Get all DSHandlers for each DS
		// First the ham
		for (IDSHandler dsHandler : dsHandlers) {
			System.out.println("dsHandler: " + dsHandler.getDSConfig().isSpam());
			//if (dsHandler.getDSConfig().isSpam()) {
				dsHandler.toHandle(urlsSpam,
						urlsHam,
						webCrawlerHandlersSpam,
						webCrawlerHandlersHam,
						outputDS, labeledDS,
						notFoundDS);
			//}
		}
		// Start crawling urls in batch
		for(String url:urlsSpam) {
			System.out.println("URL SPAM: " + url);
		}
		startWebCrawling(urlsSpam, webCrawlerHandlersSpam);
		// Start crawling urls in batch
		for(String url:urlsHam) {
			System.out.println("URL HAM: " + url);
		}
		startWebCrawling(urlsHam, webCrawlerHandlersHam);

		// Close all output datasources
		for (DataSource aux : outputDS.values()) {
			aux.close();
		}

		
		// Init data structures
		/*urls = new HashSet<String>();
		webCrawlerHandlers = new HashMap<String, IWebCrawlerHandler>();
		outputDS = new HashMap<String, DataSource>();

		// Get all DSHandlers for each DS
		// First the ham
		for (IDSHandler dsHandler : dsHandlers) {
		
			System.out.println("dsHandler2: " + dsHandler.getDSConfig().isSpam());
			if (!dsHandler.getDSConfig().isSpam()) {
				dsHandler.toHandle(urls, webCrawlerHandlers, outputDS, labeledDS,
						notFoundDS);
			}
		}
		// Start crawling urls in batch
		startWebCrawling(urls, webCrawlerHandlers);

		// Close all output datasources
		for (DataSource aux : outputDS.values()) {
			aux.close();
		}*/

		
		
		
		labeledDS.close();
		notFoundDS.close();
	}

	private void startWebCrawling(Set<String> urls,
			Map<String, IWebCrawlerHandler> webCrawlerHandlers) {
		config.getWebCrawlerCfgTemplate().setMaxDepthOfCrawling(0);

		// Initialize web crawler
		WebCrawlerConfig webCrawlerConfig = new WebCrawlerConfig(
				config.getWebCrawlerCfgTemplate());
		webCrawlerConfig.setUrls(urls);
		IWebCrawler webCrawler = new Crawler4JAdapter(webCrawlerConfig,
				webCrawlerHandlers);

		// Start crawler
		webCrawler.start();
	}

}
