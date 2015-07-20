package com.warcgenerator.datasources.arff.handler;

import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.datasource.IDataSource;
import com.warcgenerator.core.datasource.common.bean.DataBean;
import com.warcgenerator.core.datasource.common.handler.DSHandler;
import com.warcgenerator.core.datasource.common.handler.IDSHandler;

/**
 * Handle the read about a specific Arff Datasource read operation
 * @author Miguel Callon
 *
 */
public class ArffDSHandler extends DSHandler implements IDSHandler {	
	public ArffDSHandler(IDataSource ds, AppConfig config) {
		super(ds, config);
	}

	@Override
	public void handle(DataBean dataBean) {
		// Not implemented 
	}
}
