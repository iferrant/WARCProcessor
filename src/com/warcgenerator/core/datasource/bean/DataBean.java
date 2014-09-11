package com.warcgenerator.core.datasource.bean;

public class DataBean {
	private String filePath;
	private Object data;
	private String url;
	private boolean spam;
	private String typeDS = "";
	
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
}
