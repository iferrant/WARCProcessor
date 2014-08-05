package com.warcgenerator.core.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * General configuration.
 * 
 * This class must be builded from a XML properties file.
 * 
 * @author Miguel Callon
 */
public class AppConfig {
	private OutputConfig outputConfig;
	private Map<String, DataSourceConfig> dataSourceConfigs;
	private WebCrawlerConfig webCrawlerCfgTemplate;
	private String corpusDirPath;
	private String spamDirName;
	private String hamDirName;
	private String domainsLabeledFileName;
	private String domainsNotFoundFileName;
	private Boolean flushOutputDir;
	private String maxDepthOfCrawling;
	private String numCrawlers;
	private String webCrawlerTmpStorePath;
	private String numSites;
	private Boolean onlyActiveSites;
	private Boolean downloadAgain;
	private Boolean ratioIsPercentage;
	private String ratioSpam;
	private String ratioHam;

	public AppConfig() {
		setDataSourceConfigs(new HashMap<String, DataSourceConfig>());
	}

	public void init() {
		corpusDirPath = corpusDirPath.equals("")?
				Constants.AppConfigConstants.CORPUS_DIR_PATH_DEFAULT:corpusDirPath;
		spamDirName = spamDirName.equals("")?
				Constants.AppConfigConstants.SPAM_DIR_NAME_DEFAULT:spamDirName;
		hamDirName = hamDirName.equals("")?
				Constants.AppConfigConstants.HAM_DIR_NAME_DEFAULT:hamDirName;
		domainsLabeledFileName = domainsLabeledFileName.equals("")?
				Constants.AppConfigConstants.DOMAINS_LABELED_FILE_NAME_DEFAULT
					:domainsLabeledFileName;
		domainsNotFoundFileName = domainsNotFoundFileName.equals("")?
				Constants.AppConfigConstants.DOMAINS_NOTFOUND_FILE_NAME_DEFAULT
				:domainsNotFoundFileName;
		flushOutputDir = flushOutputDir.equals("")?
				Constants.AppConfigConstants.FLASH_OUTPUT_DIR_DEFAULT: flushOutputDir;
		maxDepthOfCrawling = maxDepthOfCrawling.equals("")?
				Constants.AppConfigConstants.MAX_DEPTH_OF_CRAWLING_DEFAULT
				:maxDepthOfCrawling;
		numCrawlers = numCrawlers.equals("")?
				Constants.AppConfigConstants.NUM_CRAWLERS_DEFAULT:numCrawlers;
		webCrawlerTmpStorePath = webCrawlerTmpStorePath.equals("")?
				Constants.AppConfigConstants.WEB_CRAWLER_DIR_TMP_STORE_PATH_DEFAULT
				:webCrawlerTmpStorePath;		
		numSites = numSites.equals("")?
				Constants.AppConfigConstants.NUM_SITES_DEFAULT
				:numSites;
		onlyActiveSites = onlyActiveSites.equals("")?
				Constants.AppConfigConstants.ONLY_ACTIVE_SITES_DEFAULT:
					onlyActiveSites;
		downloadAgain = downloadAgain.equals("")?
				Constants.AppConfigConstants.DOWNLOAD_AGAIN_DEFAULT:
					downloadAgain;
		ratioIsPercentage = ratioIsPercentage.equals("")?
				Constants.AppConfigConstants.RATIO_IS_PERCENTAGE_DEFAULT:
					ratioIsPercentage;
		ratioSpam = ratioSpam.equals("")?
				Constants.AppConfigConstants.RATIO_SPAM_DEFAULT:
					ratioSpam;
		ratioHam = ratioHam.equals("")?
				Constants.AppConfigConstants.RATIO_HAM_DEFAULT:
					ratioHam;
		
		// Configure filepaths
		String pathCorpus = corpusDirPath;
		String pathSpam = pathCorpus + File.separator + spamDirName;
		String pathHam = pathCorpus + File.separator + hamDirName;
		String pathDomainsLabelled = pathCorpus + File.separator
				+ domainsLabeledFileName;
		String pathDomainsNotFound = pathCorpus + File.separator
				+ domainsNotFoundFileName;
		outputConfig = new OutputCorpusConfig(pathCorpus, pathSpam, pathHam,
				pathDomainsLabelled, pathDomainsNotFound);

		// Web crawler template config
		webCrawlerCfgTemplate = new WebCrawlerConfig();
		// Set number of crawlers
		Integer numCrawlersToInt = null;
		try {
			numCrawlersToInt = Integer.parseInt(numCrawlers);
			if (numCrawlersToInt < 1) {
				numCrawlersToInt = Integer
					.parseInt(Constants.AppConfigConstants.
								MAX_DEPTH_OF_CRAWLING_DEFAULT);
			}
		} catch (NumberFormatException ex) {
			numCrawlersToInt = Integer
					.parseInt(Constants.AppConfigConstants.
								MAX_DEPTH_OF_CRAWLING_DEFAULT);
		}
		webCrawlerCfgTemplate.setNumberOfCrawlers(numCrawlersToInt);
		
		// Set max depth of crawling
		Integer maxDepthOfCrawlingToInt = null;
		try {
			maxDepthOfCrawlingToInt = Integer.parseInt(maxDepthOfCrawling);
			if (maxDepthOfCrawlingToInt < -1) {
				maxDepthOfCrawlingToInt = Integer
						.parseInt(Constants.AppConfigConstants.
						MAX_DEPTH_OF_CRAWLING_DEFAULT);
			}
		} catch (NumberFormatException ex) {
			maxDepthOfCrawlingToInt = Integer
					.parseInt(Constants.AppConfigConstants.
								MAX_DEPTH_OF_CRAWLING_DEFAULT);
		}
		
		webCrawlerCfgTemplate.setMaxDepthOfCrawling(maxDepthOfCrawlingToInt);
		webCrawlerCfgTemplate.setStorePath(webCrawlerTmpStorePath);
	}

	public OutputConfig getOutputConfig() {
		return outputConfig;
	}

	public void setOutputConfig(OutputConfig outputConfig) {
		this.outputConfig = outputConfig;
	}

	public WebCrawlerConfig getWebCrawlerCfgTemplate() {
		return webCrawlerCfgTemplate;
	}

	public void setWebCrawlerCfgTemplate(WebCrawlerConfig webCrawlerCfgTemplate) {
		this.webCrawlerCfgTemplate = webCrawlerCfgTemplate;
	}
	
	public String getSpamDirName() {
		return spamDirName;
	}

	public void setSpamDirName(String spamDirName) {
		this.spamDirName = spamDirName;
	}

	public String getHamDirName() {
		return hamDirName;
	}

	public void setHamDirName(String hamDirName) {
		this.hamDirName = hamDirName;
	}

	public String getDomainsLabeledFileName() {
		return domainsLabeledFileName;
	}

	public void setDomainsLabeledFileName(String domainsLabeledFileName) {
		this.domainsLabeledFileName = domainsLabeledFileName;
	}

	public String getDomainsNotFoundFileName() {
		return domainsNotFoundFileName;
	}

	public void setDomainsNotFoundFileName(String domainsNotFoundFileName) {
		this.domainsNotFoundFileName = domainsNotFoundFileName;
	}

	public String getMaxDepthOfCrawling() {
		return maxDepthOfCrawling;
	}

	public void setMaxDepthOfCrawling(String maxDepthOfCrawling) {
		this.maxDepthOfCrawling = maxDepthOfCrawling;
	}

	public String getWebCrawlerTmpStorePath() {
		return webCrawlerTmpStorePath;
	}

	public void setWebCrawlerTmpStorePath(String webCrawlerTmpStorePath) {
		this.webCrawlerTmpStorePath = webCrawlerTmpStorePath;
	}
	
	public String getCorpusDirPath() {
		return corpusDirPath;
	}

	public void setCorpusDirPath(String corpusDirPath) {
		this.corpusDirPath = corpusDirPath;
	}

	public Boolean getFlushOutputDir() {
		return flushOutputDir;
	}

	public void setFlushOutputDir(Boolean flushOutputDir) {
		this.flushOutputDir = flushOutputDir;
	}
	
	public String getNumCrawlers() {
		return numCrawlers;
	}

	public void setNumCrawlers(String numCrawlers) {
		this.numCrawlers = numCrawlers;
	}
	
	public String getNumSites() {
		return numSites;
	}

	public void setNumSites(String numSites) {
		this.numSites = numSites;
	}

	public Boolean getOnlyActiveSites() {
		return onlyActiveSites;
	}

	public void setOnlyActiveSites(Boolean onlyActiveSites) {
		this.onlyActiveSites = onlyActiveSites;
	}

	public Boolean getDownloadAgain() {
		return downloadAgain;
	}

	public void setDownloadAgain(Boolean downloadAgain) {
		this.downloadAgain = downloadAgain;
	}

	public Boolean getRatioIsPercentage() {
		return ratioIsPercentage;
	}

	public void setRatioIsPercentage(Boolean ratioIsPercentage) {
		this.ratioIsPercentage = ratioIsPercentage;
	}

	public String getRatioSpam() {
		return ratioSpam;
	}

	public void setRatioSpam(String ratioSpam) {
		this.ratioSpam = ratioSpam;
	}

	public String getRatioHam() {
		return ratioHam;
	}

	public void setRatioHam(String ratioHam) {
		this.ratioHam = ratioHam;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("spamDirName:  ").append(spamDirName).append("\n");
		sb.append("hamDirName:  ").append(hamDirName).append("\n");
		sb.append("domainsLabeledFileName:  ").append(domainsLabeledFileName)
				.append("\n");
		sb.append("domainsNotFoundFileName:  ").append(domainsNotFoundFileName)
				.append("\n");
		sb.append("flushOutputDir:  ").append(flushOutputDir)
				.append("\n");
		sb.append("maxDepthOfCrawling:  ").append(maxDepthOfCrawling)
				.append("\n");
		sb.append("numCrawlers:  ").append(numCrawlers)
		.append("\n");
		sb.append("webCrawlerDirTmpStorePath:  ")
				.append(webCrawlerTmpStorePath).append("\n");
		return sb.toString();
	}

	public Map<String, DataSourceConfig> getDataSourceConfigs() {
		return dataSourceConfigs;
	}

	public void setDataSourceConfigs(Map<String, DataSourceConfig> dataSourceConfigs) {
		this.dataSourceConfigs = dataSourceConfigs;
	}
}


