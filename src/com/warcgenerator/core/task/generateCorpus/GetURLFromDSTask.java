package com.warcgenerator.core.task.generateCorpus;

import java.util.Map;

import com.warcgenerator.core.common.GenerateCorpusState;
import com.warcgenerator.core.common.GenerateCorpusStates;
import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.config.DataSourceConfig;
import com.warcgenerator.core.datasource.bean.DataBean;
import com.warcgenerator.core.task.ITask;
import com.warcgenerator.core.task.Task;

public class GetURLFromDSTask extends Task implements ITask {
	private GenerateCorpusState generateCorpusState;
	private AppConfig config;
	private Map<String, DataBean> urlsSpam, urlsHam;
	
	public GetURLFromDSTask(
			AppConfig config,
			GenerateCorpusState generateCorpusState,
			Map<String, DataBean> urlsSpam, Map<String, DataBean> urlsHam) {
		this.generateCorpusState = generateCorpusState;
		this.config = config;
		this.urlsSpam = urlsSpam;
		this.urlsHam = urlsHam;
	}
	
	public void execute() {
		generateCorpusState
				.setState(GenerateCorpusStates.GETTING_URLS_FROM_DS);
		// Get all DSHandlers for each DS
		// First the ham
		for (DataSourceConfig dsConfig : config.getDataSourceConfigs()
				.values()) {
			getUrls(dsConfig, urlsSpam, urlsHam);
		}
	}
	
	public void rollback() {
		// Rollback
		System.out.println("Rollback GetURLFromDSTask");
	}
	
	private void getUrls(DataSourceConfig dsConfig, Map<String, DataBean> urlsSpam,
			Map<String, DataBean> urlsHam) {
		if (dsConfig.getHandler() != null) {
			dsConfig.getHandler().toHandle(urlsSpam, urlsHam);
		}
		for (DataSourceConfig dsChild : dsConfig.getChildren()) {
			getUrls(dsChild, urlsSpam, urlsHam);
		}
	}
}
