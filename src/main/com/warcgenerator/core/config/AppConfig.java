package com.warcgenerator.core.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import com.warcgenerator.core.exception.config.validation.RatioQuantityUnexpectedValueException;
import com.warcgenerator.core.util.Validator;

/**
 * General configuration.
 * 
 * This class must be built from a XML properties file.
 * 
 * @author Miguel Callon
 */
public class AppConfig {
	private OutputConfig outputConfig;
	private Map<Integer, DataSourceConfig> dataSourceConfigs;
	private WebCrawlerConfig webCrawlerCfgTemplate;
	private String corpusDirPath;
	private String corpusDirName;
	private String spamDirName;
	private String hamDirName;
	private String domainsLabeledFileName;
	private String domainsNotFoundFileName;
	private Boolean flushOutputDir;
	private Integer maxDepthOfCrawling;
	private Integer numCrawlers;
	private String webCrawlerTmpStorePath;
	private Integer numSites;
	private Boolean onlyActiveSites;
	private Boolean downloadAgain;
	private Boolean followRedirect;
	private Boolean ratioIsPercentage;
	private Integer ratioPercentageSpam;
	private Integer ratioQuantitySpam;

	/**
	 * Default constructor
	 */
	public AppConfig() {
		setDataSourceConfigs(new LinkedHashMap<Integer, DataSourceConfig>());
	}

	/**
	 * Set current appConfig to default values
	 */
	public void init() {
		corpusDirPath = Validator.isNullOrEmpty(corpusDirPath)?
				Constants.AppConfigConstants.CORPUS_DIR_PATH_DEFAULT:corpusDirPath;
		corpusDirName = Validator.isNullOrEmpty(corpusDirName)?
				Constants.AppConfigConstants.CORPUS_DIR_NAME_DEFAULT:corpusDirName;
		spamDirName = Validator.isNullOrEmpty(spamDirName)?
				Constants.AppConfigConstants.SPAM_DIR_NAME_DEFAULT:spamDirName;
		hamDirName = Validator.isNullOrEmpty(hamDirName)?
				Constants.AppConfigConstants.HAM_DIR_NAME_DEFAULT:hamDirName;
		domainsLabeledFileName = Validator.isNullOrEmpty(domainsLabeledFileName)?
				Constants.AppConfigConstants.DOMAINS_LABELED_FILE_NAME_DEFAULT
					:domainsLabeledFileName;
		domainsNotFoundFileName = Validator.isNullOrEmpty(domainsNotFoundFileName)?
				Constants.AppConfigConstants.DOMAINS_NOTFOUND_FILE_NAME_DEFAULT
				:domainsNotFoundFileName;
		flushOutputDir = Validator.isNullOrEmpty(flushOutputDir)?
				Constants.AppConfigConstants.FLASH_OUTPUT_DIR_DEFAULT: flushOutputDir;
		maxDepthOfCrawling = Validator.isNullOrEmpty(maxDepthOfCrawling)?
				Constants.AppConfigConstants.MAX_DEPTH_OF_CRAWLING_DEFAULT
				:maxDepthOfCrawling;
		numCrawlers = Validator.isNullOrEmpty(numCrawlers)?
				Constants.AppConfigConstants.NUM_CRAWLERS_DEFAULT:numCrawlers;
		webCrawlerTmpStorePath = Validator.isNullOrEmpty(webCrawlerTmpStorePath)?
				Constants.AppConfigConstants.WEB_CRAWLER_DIR_TMP_STORE_PATH_DEFAULT
				:webCrawlerTmpStorePath;		
		numSites = Validator.isNullOrEmpty(numSites)?
				Constants.AppConfigConstants.NUM_SITES_DEFAULT
				:numSites;
		onlyActiveSites = Validator.isNullOrEmpty(onlyActiveSites)?
				Constants.AppConfigConstants.ONLY_ACTIVE_SITES_DEFAULT:
					onlyActiveSites;
		downloadAgain = Validator.isNullOrEmpty(downloadAgain)?
				Constants.AppConfigConstants.DOWNLOAD_AGAIN_DEFAULT:
					downloadAgain;
		followRedirect = Validator.isNullOrEmpty(followRedirect)?
				Constants.AppConfigConstants.FOLLOW_REDIRECT_DEFAULT:
					followRedirect;
		ratioIsPercentage = Validator.isNullOrEmpty(ratioIsPercentage)?
				Constants.AppConfigConstants.RATIO_IS_PERCENTAGE_DEFAULT:
					ratioIsPercentage;
		ratioPercentageSpam = Validator.isNullOrEmpty(ratioPercentageSpam)?
				Constants.AppConfigConstants.RATIO_PERCENTAGE_SPAM_DEFAULT:
					ratioPercentageSpam;
		ratioQuantitySpam = Validator.isNullOrEmpty(ratioQuantitySpam)?
				Constants.AppConfigConstants.RATIO_QUANTITY_SPAM_DEFAULT:
					ratioQuantitySpam;
		
		// Configure filepaths
		resetOutputConfig();

		// Web crawler template config
		webCrawlerCfgTemplate = new WebCrawlerConfig();
		// Set number of crawlers
		Integer numCrawlersToInt = null;
		try {
			numCrawlersToInt = numCrawlers;
			if (numCrawlersToInt < 1) {
				numCrawlersToInt = Constants.AppConfigConstants.
								MAX_DEPTH_OF_CRAWLING_DEFAULT;
			}
		} catch (NumberFormatException ex) {
			numCrawlersToInt = Constants.AppConfigConstants.
								MAX_DEPTH_OF_CRAWLING_DEFAULT;
		}
		webCrawlerCfgTemplate.setNumberOfCrawlers(numCrawlersToInt);
		
		// Set max depth of crawling
		Integer maxDepthOfCrawlingToInt = null;
		try {
			maxDepthOfCrawlingToInt = maxDepthOfCrawling;
			if (maxDepthOfCrawlingToInt < -1) {
				maxDepthOfCrawlingToInt = Constants.AppConfigConstants.
						MAX_DEPTH_OF_CRAWLING_DEFAULT;
			}
		} catch (NumberFormatException ex) {
			maxDepthOfCrawlingToInt = Constants.AppConfigConstants.
								MAX_DEPTH_OF_CRAWLING_DEFAULT;
		}
		
		webCrawlerCfgTemplate.setMaxDepthOfCrawling(maxDepthOfCrawlingToInt);
		webCrawlerCfgTemplate.setStorePath(webCrawlerTmpStorePath);
	}
	
	/** 
	 * Create a new OutputConfig with current parameters of appConfig
	 */
	public void resetOutputConfig() {
		// Configure filepaths
		String pathCorpus = this.corpusDirPath;
		String corpusDirName = pathCorpus + File.separator + this.corpusDirName;
		String pathSpam = corpusDirName + File.separator + this.spamDirName;
		String pathHam = corpusDirName + File.separator + this.hamDirName;
		String pathDomainsLabelled = corpusDirName + File.separator
				+ this.domainsLabeledFileName;
		String pathDomainsNotFound = corpusDirName + File.separator
						+ this.domainsNotFoundFileName;
		setOutputConfig(new OutputCorpusConfig(pathCorpus, corpusDirName, pathSpam, pathHam,
						pathDomainsLabelled, pathDomainsNotFound));
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

	public String getCorpusDirName() {
		return corpusDirName;
	}

	public void setCorpusDirName(String corpusDirName) {
		this.corpusDirName = corpusDirName;
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

	public Integer getMaxDepthOfCrawling() {
		return maxDepthOfCrawling;
	}

	public void setMaxDepthOfCrawling(Integer maxDepthOfCrawling) {
		this.maxDepthOfCrawling = maxDepthOfCrawling;
	}

	public Integer getNumCrawlers() {
		return numCrawlers;
	}

	public void setNumCrawlers(Integer numCrawlers) {
		this.numCrawlers = numCrawlers;
	}

	public Integer getNumSites() {
		return numSites;
	}

	public void setNumSites(Integer numSites) {
		this.numSites = numSites;
	}
	
	public Integer getRatioQuantitySpam() {
		return ratioQuantitySpam;
	}

	public void setRatioQuantitySpam(Integer ratioQuantitySpam) {
		this.ratioQuantitySpam = ratioQuantitySpam;
	}
	
	public Integer getRatioPercentageSpam() {
		return ratioPercentageSpam;
	}

	public void setRatioPercentageSpam(Integer ratioPercentageSpam) {
		this.ratioPercentageSpam = ratioPercentageSpam;
	}
	
	public Map<Integer, DataSourceConfig> getDataSourceConfigs() {
		return dataSourceConfigs;
	}

	public void setDataSourceConfigs(Map<Integer, DataSourceConfig> dataSourceConfigs) {
		this.dataSourceConfigs = dataSourceConfigs;
	}
	
	public Boolean getFollowRedirect() {
		return followRedirect;
	}

	public void setFollowRedirect(Boolean followRedirect) {
		this.followRedirect = followRedirect;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("-- AppConfig -- \n");
		sb.append("corpusDirPath:  ").append(corpusDirPath).append("\n");
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
		sb.append("downloadAgain: ").append(downloadAgain).append("\n");
		sb.append("followRedirect: ").append(followRedirect).append("\n");
		sb.append("\n");
		for (DataSourceConfig dsConfig:dataSourceConfigs.values()) {
			sb.append(dsConfig.toString());
			sb.append("\n");
		}
		sb.append("-- END AppConfig --\n");
		return sb.toString();
	}
	
	/**
	 * Validate appConfig constraints
	 * This method must be invoked before store the current appConfig 
	 * @return true if current appConfig is valid
	 */
	public boolean validate() {
		// Check if ratioQuantity is bigger than numSites
		if (!ratioIsPercentage && numSites < ratioQuantitySpam) {
			throw new RatioQuantityUnexpectedValueException();
		}
		
		return true;
	}
}