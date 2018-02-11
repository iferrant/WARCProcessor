package com.warcgenerator.core.datasource.common.bean;

import com.warcgenerator.core.config.DataSourceConfig;

public class DataBean {
	private String filePath;
	private Object data;
	private String url;
	private boolean spam;
	private String typeDS = "";
	private String language;
	private DataSourceConfig dsConfig;
	
	public DataBean() {}
	
	public DataBean(String data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isSpam() {
		return spam;
	}

	public void setSpam(boolean spam) {
		this.spam = spam;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTypeDS() {
		return typeDS;
	}

	public void setTypeDS(String typeDS) {
		this.typeDS = typeDS;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public DataSourceConfig getDsConfig() {
		return dsConfig;
	}

	public void setDsConfig(DataSourceConfig dsConfig) {
		this.dsConfig = dsConfig;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("------ DataBean ------");
		
		sb.append("filePath: ").append(filePath)
		.append(", data: ").append(data)
		.append(", url: ").append(url)
		.append(", spam: ").append(spam);
		
		return sb.toString();
	}
}
