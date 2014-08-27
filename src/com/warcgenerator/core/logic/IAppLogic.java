package com.warcgenerator.core.logic;

import java.util.Collection;
import java.util.Observer;

import com.warcgenerator.core.common.GenerateCorpusState;
import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.config.DataSourceConfig;

/**
 * Business logic layer interface
 * 
 * @author Miguel Callon
 *
 */
public interface IAppLogic {
	// Callback messages
	String APP_LOGIC_UPDATED_CALLBACK = "APP_LOGIC_UPDATED_CALLBACK";
	
	void saveAppConfig(String path);
	void loadAppConfig(String path);
	void updateAppConfig(AppConfig appConfig);
	AppConfig getAppConfig();
	Collection<DataSourceConfig> getDataSourceTypesList();
	DataSourceConfig getDataSourceType(String type);
	Collection<DataSourceConfig> getDataSourceConfigList();
	void addDataSourceConfig(DataSourceConfig dsConfig);
	void removeDataSourceConfig(Integer id);
	void stopGenerateCorpus();
	void generateCorpus(GenerateCorpusState generateCorpusState);
	void addObserver(Observer obs);
	void deleteObserver(Observer obs);
}
