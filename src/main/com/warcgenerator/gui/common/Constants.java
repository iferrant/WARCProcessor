package com.warcgenerator.gui.common;

public interface Constants {
	String APP_NAME = "WARC Processor";
	String APP_VERSION = "4.2 Beta";
	
	String LANGUAGE_MENUITEM_SELECTED_SESSION_KEY =
			"language_menuitem_selected_session_key";
	String DATASOURCE_FORM_SESSION_KEY = "datasource_form_session_key";
	String GUI_CONFIG_SESSION_KEY = "gui_config_session_key";
	String DEFAULT_GUI_CONFIG_XML= "/config/gui-config-default.xml";
	String DEFAULT_DIR_CUSTOM_GUI_CONFIG_XML = "./config/";
	String CUSTOM_GUI_CONFIG_XML = "gui-config.xml";
	String DEFAULT_LOG4J_CONFIG_XML = "/config/log4j-default.xml";
	String LOG4J_CONFIG_XML = "log4j.xml";
	String CONFIG_SCHEMA_FILE_PATH = "/config/schema/gui-config.xsd";
	String FORM_MODIFIED_SESSION_KEY = "form_modified_session_key";
	Integer NUM_MAX_RECENT_CONFIG_FILES = 4;
	
	String SPANISH_LOCALE = "es";
	String SPAIN_COUNTRY_LOCALE = "ES";
	String SPANISH_COMPLETE_LOCALE = SPANISH_LOCALE + "-" +
			SPAIN_COUNTRY_LOCALE;
	String ENGLISH_LOCALE = "en";
	String ENGLAND_COUNTRY_LOCALE = "GB";
	String ENGLISH_COMPLETE_LOCALE = ENGLISH_LOCALE + "-" +
			ENGLAND_COUNTRY_LOCALE;
	String DEFAULT_LOCALE = ENGLISH_LOCALE;
	
	String FILE_CFG_EXTENSION = "wpg";
	String FILTER_FILE_CFG_DESCRIPTION = "fileFilter.fileCfg";
}
