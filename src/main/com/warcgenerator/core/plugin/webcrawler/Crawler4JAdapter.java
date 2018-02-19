package com.warcgenerator.core.plugin.webcrawler;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.config.OutputWarcConfig;
import com.warcgenerator.core.config.WebCrawlerConfig;
import com.warcgenerator.core.datasource.DataSource;
import com.warcgenerator.core.datasource.common.bean.DataBean;
import com.warcgenerator.core.datasource.warc.WarcDS;
import com.warcgenerator.core.exception.plugin.PluginException;
import com.warcgenerator.core.helper.FileHelper;
import com.warcgenerator.core.helper.ReadURLHelper;
import com.warcgenerator.core.task.generateCorpus.state.GenerateCorpusState;
import com.warcgenerator.core.task.generateCorpus.state.GenerateCorpusStates;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler4JAdapter extends WebCrawler implements IWebCrawler {
	private AppConfig appConfig;
	@SuppressWarnings("unused")
	private GenerateCorpusState generateCorpusState;
	private CustomCrawlController controller;
	private Map<String, IWebCrawlerHandler> handlers;
	private WebCrawlerBean webCrawlerBean;
	private int numberOfCrawlers;
	private Map<String, com.warcgenerator.core.plugin.webcrawler.HtmlParseData> parseDataMap;
	private Map<String, DataSource> outputDS;
	private Map<String, DataBean> urls;
	private Set<String> urlsActive;
	private Set<String> urlsNotActive;

	private static Logger logger = Logger.getLogger(Crawler4JAdapter.class);

	// Currently it doesn't use any filter
	private final static Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g"
					+ "|png|tiff?|mid|mp2|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	// Allow any urls
	private static Set<String> urlsAllowed;

	public Crawler4JAdapter() {
		parseDataMap = new LinkedHashMap<String, com.warcgenerator.core.plugin.webcrawler.HtmlParseData>();
		handlers = new LinkedHashMap<String, IWebCrawlerHandler>();
	}

	public Crawler4JAdapter(
			AppConfig appConfig,
			GenerateCorpusState generateCorpusState,
			WebCrawlerConfig configWC,
			WebCrawlerBean webCrawlerBean,
			Map<String, DataSource> outputDS,
			Map<String, DataBean> urls,
			Set<String> urlsActive,
			Set<String> urlsNotActive
			) throws PluginException {
		super();
		this.appConfig = appConfig;
		this.generateCorpusState = generateCorpusState;
		this.handlers = new LinkedHashMap<String, IWebCrawlerHandler>();
		this.webCrawlerBean = webCrawlerBean;
		this.outputDS = outputDS;
		this.urls = urls;
		this.urlsActive = urlsActive;
		this.urlsNotActive = urlsNotActive;
		
		numberOfCrawlers = configWC.getNumberOfCrawlers();

		// Ursls to avoid exit from the seed urls domains
		urlsAllowed = urls.keySet();		
		
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(configWC.getStorePath());
		config.setFollowRedirects(configWC.isFollowRedirect());
		
		// To be polite
		config.setPolitenessDelay(200);
		
		/*
		 * You can set the maximum crawl depth here. The default value is -1 for
		 * unlimited depth
		 */
		config.setMaxDepthOfCrawling(configWC.getMaxDepthOfCrawling());
		
		/*
		 * Instantiate the controller for this crawl.
		 */
		/*CustomPageFetcher pageFetcher = new CustomPageFetcher(generateCorpusState,
				config);*/
		CustomPageFetcher pageFetcher = new CustomPageFetcher(generateCorpusState,
				config, appConfig, urls);
		
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,
				pageFetcher);

		try {
			controller = new CustomCrawlController(config, pageFetcher,
					robotstxtServer);
		} catch (Exception e) {
			throw new PluginException(e);
		}
	}
	
	public void addSeed (String url) {
		/*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		controller.addSeed(url);
	}

	public void close() {
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(false);
		envConfig.setLocking(false);

		File envHome = new File(controller.getConfig().getCrawlStorageFolder()
				+ "/frontier");

		Environment env = new Environment(envHome, envConfig);
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setTransactional(true);
		dbConfig.setDeferredWrite(true);

		try {
			for (String dbName : env.getDatabaseNames()) {
				env.removeDatabase(null, dbName);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		env.cleanLog();
		env.close();
	}

	public void stop() {
		generateCorpusState.setState(
				GenerateCorpusStates.CANCELLING_PROCESS);
		controller.shutdown();
	}

	@SuppressWarnings("unchecked")
	public void start() {
		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		controller.start(Crawler4JAdapter.class, numberOfCrawlers);

		controller.waitUntilFinish();
		
		if (!controller.isShuttingDown()) {
			List<Object> crawlersLocalData = controller.getCrawlersLocalData();
			for (Object localData : crawlersLocalData) {
				if (localData instanceof Collection<?>) {
					for (com.warcgenerator.core.plugin.webcrawler.HtmlParseData parseData : (Collection<com.warcgenerator.core.plugin.webcrawler.HtmlParseData>) localData) {
						IWebCrawlerHandler handler = handlers.get(FileHelper
								.getDomainNameFromURL(parseData.getUrl()));
	
						DataSource warcDS = outputDS.get(FileHelper
								.getDomainNameFromURL(parseData.getUrl()));
						
						if (warcDS == null) {
							StringBuilder warcFileName = new StringBuilder();
	
							StringBuilder outputWarcPath = new StringBuilder();
							if (webCrawlerBean.isSpam()) {
								outputWarcPath.append(webCrawlerBean
										.getOutputCorpusConfig().getSpamDir());
							} else {
								outputWarcPath.append(webCrawlerBean
										.getOutputCorpusConfig().getHamDir());
							}
							outputWarcPath.append(File.separator);
	
							warcFileName.append(outputWarcPath.toString())
									.append(FileHelper.getOutputFileName(parseData
											.getUrl()));
	
							warcDS = new WarcDS(new OutputWarcConfig(
									webCrawlerBean.isSpam(),
									warcFileName.toString()));
	
							outputDS.put(FileHelper.getDomainNameFromURL(parseData
									.getUrl()), warcDS);
						}
	
						if (handler == null) {
							handler = new WebCrawlerHandler(appConfig,
									webCrawlerBean.isSpam(),
									webCrawlerBean.getDomainsSpamDS(),
									webCrawlerBean.getDomainsHamDS(),
									webCrawlerBean.getDomainsNotFoundDS(),
									webCrawlerBean.getDomainsLabeledDS(), warcDS,
									
									urls, urlsActive, urlsNotActive, generateCorpusState);
							handlers.put(FileHelper.getDomainNameFromURL(parseData
									.getUrl()), handler);
						}
	
						handler.handle(parseData);
					}
				}
			}
		}
	}

	public Object getMyLocalData() {
		return parseDataMap.values();
	}
	
	
	/**
     * This function is called before processing of the page's URL
     * It can be overridden by subclasses for tweaking of the url before processing it.
     * For example, http://abc.com/def?a=123 - http://abc.com/def
     *
     * @param curURL current URL which can be tweaked before processing
     * @return tweaked WebURL
     */
    protected WebURL handleUrlBeforeProcess(WebURL curURL) {
      getParseData(curURL.getURL());
      return curURL;
    }

	/**
	   * Classes that extends WebCrawler should overwrite this function to tell the
	   * crawler whether the given url should be crawled or not. The following
	   * default implementation indicates that all urls should be included in the crawl.
	   *
	   * @param url
	   *            the url which we are interested to know whether it should be
	   *            included in the crawl or not.
	   * @param referringPage
	   *           The Page in which this url was found.
	   * @return if the url should be included in the crawl it returns true,
	   *         otherwise false is returned.
	   */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();

		// Avoid that the crawler go out from the source domains.
		boolean domainAllowed = false;
		for (Iterator<String> it = urlsAllowed.iterator(); it.hasNext()
				&& !domainAllowed;) {
			String urlAllowed = it.next();

			String domain = FileHelper.getURLWithoutParams(urlAllowed);
			if (href.startsWith(domain)) {
				domainAllowed = true;
			}
		}

		return !FILTERS.matcher(href).matches() && domainAllowed;
	}

	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode,
			String statusDescription) {
		com.warcgenerator.core.plugin.webcrawler.HtmlParseData parseData = getParseData(webUrl
				.getURL());
		
		CustomPageFetcher customPFetcher = ((CustomPageFetcher) this.myController
				.getPageFetcher());

		GenerateCorpusState generateCorpusState = customPFetcher.getGenerateCorpusState();
		generateCorpusState.incWebsVisited();
		generateCorpusState.setCurrentUrlCrawled(webUrl.getURL());
		generateCorpusState.setState(GenerateCorpusStates.CRAWLING_URLS);

		parseData.setUrl(webUrl.getURL());
		parseData.setHttpStatus(statusCode);
		parseData.setHttpStatusDescription(statusDescription);

		// If this url is a child from other URL
		// use the parent dsConfig
		Map<String, DataBean> urls =  customPFetcher.getUrls();
		String parentURL = webUrl.getParentUrl();
		if (parentURL != null) {
			DataBean parentDataBean = urls.get(parentURL);
			
			if (parentDataBean != null) {
				DataBean dataBean = new DataBean();
				dataBean.setUrl(webUrl.getURL());
				dataBean.setDsConfig(parentDataBean.getDsConfig());
				dataBean.setSpam(parentDataBean.isSpam());
				
				
				AppConfig appConfig = customPFetcher.getAppConfig();
				
				int maxURLs = 0;
				if (dataBean.isSpam()) {
					maxURLs = ReadURLHelper.getMaxSitesSpam(appConfig);
				} else {
					maxURLs = ReadURLHelper.getMaxSitesHam(appConfig);
				}
				
				ReadURLHelper.addUrl(urls, dataBean, generateCorpusState, maxURLs);
			}
		}
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		// Check status
		String url = page.getWebURL().getURL();
		logger.info("URL: " + url);
		com.warcgenerator.core.plugin.webcrawler.HtmlParseData parseData = getParseData(url);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			parseData.setHtml(html);
			parseData.setText(text);
			parseData.setContentType(page.getContentType());

			logger.info("Text length: " + text.length());
			logger.info("Html length: " + html.length());
			logger.info("Number of outgoing links: " + links.size());
		}
	}

	private com.warcgenerator.core.plugin.webcrawler.HtmlParseData getParseData(
			String url) {
		com.warcgenerator.core.plugin.webcrawler.HtmlParseData parseData = parseDataMap
				.get(url);
		if (parseData == null) {
			parseData = new com.warcgenerator.core.plugin.webcrawler.HtmlParseData();
			parseData.setUrl(url);
			parseDataMap.put(url, parseData);
		}
		return parseData;
	}
}