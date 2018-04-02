package com.warcgenerator.core.config;

import java.sql.Timestamp;

/**
 * Store default values to appConfig parameters
 * @author Miguel Callon
 *
 */
public interface Constants {
	String outputCorpusFileExtension = "warc";
	String outputEnconding = "UTF-8";
	String outputContentType = "text/html";
	String configSchemaFilePath = "/config/schema/config.xsd";
	String summarySchemaFilePath = "/config/schema/summary.xsd";
	String defaultConfigXML= "/config/config.xml";
	String dataSourcesTypesXML= "/config/datasources.xml";
	String corpusConfigFile= "corpus_config.xml";

	public interface AppConfigConstants {
		String CORPUS_DIR_PATH_DEFAULT = "RESULT";
		String CORPUS_DIR_NAME_DEFAULT = "corpus_" + new Timestamp(System.currentTimeMillis()).getTime();
		String SPAM_DIR_NAME_DEFAULT = "_spam_";
		String HAM_DIR_NAME_DEFAULT = "_ham_";
		String DOMAINS_LABELED_FILE_NAME_DEFAULT = "domains.labelled";
		String DOMAINS_NOTFOUND_FILE_NAME_DEFAULT = "domains.notFound";
		Boolean FLASH_OUTPUT_DIR_DEFAULT = true;
		Integer MAX_DEPTH_OF_CRAWLING_DEFAULT = 0;
		Integer NUM_CRAWLERS_DEFAULT = 1;
		String WEB_CRAWLER_DIR_TMP_STORE_PATH_DEFAULT = ".";
		Integer NUM_SITES_DEFAULT = 1000;
		Boolean ONLY_ACTIVE_SITES_DEFAULT = true;
		Boolean DOWNLOAD_AGAIN_DEFAULT = true;
		Boolean FOLLOW_REDIRECT_DEFAULT = false;
		Boolean RATIO_IS_PERCENTAGE_DEFAULT = true;
		Integer RATIO_PERCENTAGE_SPAM_DEFAULT = 50;
		Integer RATIO_QUANTITY_SPAM_DEFAULT = 0;
	}
}
