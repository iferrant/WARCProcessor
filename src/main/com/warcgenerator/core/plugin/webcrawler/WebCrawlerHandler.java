package com.warcgenerator.core.plugin.webcrawler;

import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.datasource.IDataSource;
import com.warcgenerator.core.datasource.common.bean.Country;
import com.warcgenerator.core.datasource.common.bean.DataBean;
import com.warcgenerator.core.datasource.warc.WarcDS;
import com.warcgenerator.core.exception.datasource.DSException;
import com.warcgenerator.core.helper.FileHelper;
import com.warcgenerator.core.helper.LangFilterHelper;
import com.warcgenerator.core.helper.OutputHelper;
import com.warcgenerator.core.task.generateCorpus.state.GenerateCorpusState;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class WebCrawlerHandler implements IWebCrawlerHandler {
	private AppConfig config;
	private IDataSource warcDS;
	private IDataSource domainsSpamDS;
	private IDataSource domainsHamDS;
	private IDataSource domainsLabeledDS;
	private IDataSource domainsNotFoundDS;
	private boolean isSpam;
	private Map<String, DataBean> urls;
	private Set<String> urlsActive;
	private Set<String> urlsNotActive;
	private GenerateCorpusState generateCorpusState;

	private static Logger logger = Logger.getLogger
            (WebCrawlerHandler.class);
	
	public WebCrawlerHandler(AppConfig config, boolean isSpam,
			IDataSource domainsSpamDS, IDataSource domainsHamDS,
			IDataSource domainsNotFoundDS, IDataSource domainsLabeledDS,
			IDataSource warcDS, Map<String, DataBean> urls,
			Set<String> urlsActive, Set<String> urlsNotActive, GenerateCorpusState generateCorpusState) {
		this.config = config;
		this.isSpam = isSpam;
		this.warcDS = warcDS;
		this.domainsSpamDS = domainsSpamDS;
		this.domainsHamDS = domainsHamDS;
		this.domainsLabeledDS = domainsLabeledDS;
		this.domainsNotFoundDS = domainsNotFoundDS;
		this.urls = urls;
		this.urlsActive = urlsActive;
		this.urlsNotActive = urlsNotActive;
		this.generateCorpusState = generateCorpusState;
	}

	/*public void handle(HtmlParseData htmlParseData) {
		String url = FileHelper.normalizeURL(htmlParseData.getUrl());
		
		// Look for Warc Bean containing html of the urls
		DataBean bean = urls.get(url);
		// If bean is null, it means we have not information
		// about this url, so skip url
		if (bean == null) {
			logger.info("URL skipped: " + url);
			return;
		} 
		
		boolean existPreviousWarcBean = checkExistPreviousWarcBean(bean);
		
		if (htmlParseData.getHttpStatus() == null) {
			urlsNotActive.add(htmlParseData.getUrl());
			
			if (!existPreviousWarcBean) {
				OutputHelper.writeNotFound(domainsNotFoundDS,
						htmlParseData.getUrl(), htmlParseData.getHttpStatus(),
						htmlParseData.getHttpStatusDescription());
			}
		} else {
			if (htmlParseData.getUrl() != null
					&& htmlParseData.getHttpStatus() == HttpStatus.SC_OK) {
				if (config.getDownloadAgain()
						|| !existPreviousWarcBean) {
					bean.setData(htmlParseData.getHtml());
				}
				
				// Language filter
				try {
					logger.info("URL: " + bean.getUrl()); 
					if (bean.getDsConfig() == null ||
							LangFilterHelper.checkLanguageAllowed((String)bean.getData(),
							bean.getDsConfig().getCountryList())) {
	
						// Check if the url has data
						if (bean.getData() != null) {
							warcDS.write(bean);
							OutputHelper.writeLabeled(domainsLabeledDS, htmlParseData.getUrl(),
									this.isSpam);
							generateCorpusState.incDomainsCorrectlyLabeled(this.isSpam);
						} else {
							logger.info("URL: " + bean.getUrl() + " doesn't have data.");
						}
							
						urlsActive.add(htmlParseData.getUrl());
					} else {
						// TODO Write in some output file instead of the log
						logger.info("URL Filtered. Available:");
						StringBuffer sb = new StringBuffer();
						for(Country country:bean.getDsConfig().getCountryList()) {
							sb.append(country.getName()).append(" ");
						}
						logger.info(sb.toString());
					}
				} catch (Exception e) {
					throw new DSException(e);
				}
			} else if (htmlParseData.getUrl() != null &&
				//existPreviousWarcBean &&
				(htmlParseData.getHttpStatus() == HttpStatus.SC_MOVED_PERMANENTLY ||
				htmlParseData.getHttpStatus() == HttpStatus.SC_MOVED_TEMPORARILY)) {
		
				if (!config.getFollowRedirect()) {
					if (!existPreviousWarcBean) {
						OutputHelper.writeNotFound(domainsNotFoundDS,
						htmlParseData.getUrl(), htmlParseData.getHttpStatus(),
						htmlParseData.getHttpStatusDescription());
					} else {
						// Check if the url has data
						if (bean.getData() != null) {
							warcDS.write(bean);
							OutputHelper.writeLabeled(domainsLabeledDS, htmlParseData.getUrl(),
									this.isSpam);
						} else {
							logger.info("URL: " + bean.getUrl() + " doesn't have data.");
						}
					}
				}
			} else {
				urlsNotActive.add(htmlParseData.getUrl());
				
				if (!existPreviousWarcBean) {
					OutputHelper.writeNotFound(domainsNotFoundDS,
							htmlParseData.getUrl(), htmlParseData.getHttpStatus(),
							htmlParseData.getHttpStatusDescription());
				}
			}
		}
	}*/

	
	public void handle(HtmlParseData htmlParseData) {
		String url = FileHelper.normalizeURL(htmlParseData.getUrl());
		
		// Look for Warc Bean containing html of the urls
		DataBean bean = urls.get(url);
		// If bean is null, it means we have not information
		// about this url, so skip url
		if (bean == null) {
			logger.info("URL skipped: " + url);
			return;
		} 
		
		boolean existPreviousWarcBean = checkExistPreviousWarcBean(bean);
		
		if (htmlParseData.getHttpStatus() == null) {
			// If it has not been possible to connect with url, add the URL to
			// inactive site list
			addInactiveSites(htmlParseData, bean);
		} else {
			if (htmlParseData.getHttpStatus() == HttpStatus.SC_OK) {
				if (config.getDownloadAgain()
						|| !existPreviousWarcBean) {
					bean.setData(htmlParseData.getHtml());
					bean.setLanguage(getPageLanguage(htmlParseData.getHtml()));
				}
				
				// Language filter
				try {
					logger.info("URL: " + bean.getUrl()); 
					if (LangFilterHelper.checkLanguageAllowed((String)bean.getData(),
							bean.getDsConfig().getCountryList())) {
	
						// Check if the url has data
						if (bean.getData() != null) {
							warcDS.write(bean);
							OutputHelper.writeLabeled(domainsLabeledDS, htmlParseData.getUrl(),
									this.isSpam);
							if (isSpam)
								OutputHelper.writeSpamHam(domainsSpamDS, htmlParseData.getUrl());
							else
								OutputHelper.writeSpamHam(domainsHamDS, htmlParseData.getUrl());
							generateCorpusState.incDomainsCorrectlyLabeled(this.isSpam);
						} else {
							logger.info("URL: " + bean.getUrl() + " doesn't have data.");
						}
							
						urlsActive.add(htmlParseData.getUrl());
					} else {
						// TODO Write in some output file instead of the log
						logger.info("URL Filtered. Available:");
						StringBuffer sb = new StringBuffer();
						for(Country country:bean.getDsConfig().getCountryList()) {
							sb.append(country.getName()).append(" ");
						}
						logger.info(sb.toString());
					}
				} catch (Exception e) {
					throw new DSException(e);
				}
			} else if (htmlParseData.getHttpStatus() == HttpStatus.SC_MOVED_PERMANENTLY ||
				htmlParseData.getHttpStatus() == HttpStatus.SC_MOVED_TEMPORARILY) {
		
				if (!config.getFollowRedirect()) {
					if (!existPreviousWarcBean) {
						OutputHelper.writeNotFound(domainsNotFoundDS,
						htmlParseData.getUrl(), htmlParseData.getHttpStatus(),
						htmlParseData.getHttpStatusDescription());
					} else {
						// Check if the url has data
						if (bean.getData() != null) {
							warcDS.write(bean);
							OutputHelper.writeLabeled(domainsLabeledDS, htmlParseData.getUrl(),
									this.isSpam);
						} else {
							logger.info("URL: " + bean.getUrl() + " doesn't have data.");
						}
					}
				}
			} else {
				// If HTTP status has been a unexpected code, add URL to inactive site list 
				addInactiveSites(htmlParseData, bean);
			}
		}
	}

	/**
	 * Parse and retrieve the page language based on the
	 * "lang" or "xml:lang" attribute of the first html tag
	 *
	 * @param html HTML of the page in String format
	 * @return Language of the page
	 */
	private String getPageLanguage(String html) {
        Element tagLang = Jsoup.parse(html).select("html").first();
        String language = tagLang.attr("lang");
        if (language != null && language.isEmpty()) {
            language = tagLang.attr("xml:lang");
        }

        return language;
    }
	
	private void addInactiveSites(HtmlParseData htmlParseData,
			DataBean bean) {
		urlsNotActive.add(htmlParseData.getUrl());
		if (!checkExistPreviousWarcBean(bean)) {
			OutputHelper.writeNotFound(domainsNotFoundDS,
					htmlParseData.getUrl(), htmlParseData.getHttpStatus(),
					htmlParseData.getHttpStatusDescription());
		}
	}
	
	/**
	 * Check if exist in some warc a html related with the url in databean
	 * @param previousWarcBean
	 * @return
	 */
	private boolean checkExistPreviousWarcBean(DataBean previousWarcBean) {
		boolean existPreviousWarcBean = false;
		if (previousWarcBean != null && 
				previousWarcBean.getTypeDS() != null
					&& previousWarcBean.getTypeDS().equals(WarcDS.DS_TYPE)) {
				existPreviousWarcBean = true;
		}
		return existPreviousWarcBean;
	}
		
}
