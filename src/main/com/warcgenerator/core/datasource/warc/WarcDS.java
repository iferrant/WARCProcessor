package com.warcgenerator.core.datasource.warc;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import com.warcgenerator.core.util.StringUtil;
import org.apache.log4j.Logger;
import org.archive.io.arc.ARCConstants;
import org.archive.io.warc.WARCWriter;
import org.archive.io.warc.WARCWriterPoolSettings;
import org.archive.uid.RecordIDGenerator;
import org.archive.uid.UUIDGenerator;
import org.archive.util.ArchiveUtils;
import org.archive.util.anvl.ANVLRecord;

import com.warcgenerator.core.config.Constants;
import com.warcgenerator.core.config.DataSourceConfig;
import com.warcgenerator.core.config.OutputWarcConfig;
import com.warcgenerator.core.datasource.DataSource;
import com.warcgenerator.core.datasource.IDataSource;
import com.warcgenerator.core.datasource.common.bean.DataBean;
import com.warcgenerator.core.datasource.warc.ext.WarcRecord;
import com.warcgenerator.core.exception.datasource.CloseException;
import com.warcgenerator.core.exception.datasource.DSException;
import com.warcgenerator.core.exception.datasource.OpenException;
import com.warcgenerator.core.exception.datasource.WriteException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class WarcDS extends DataSource implements IDataSource {
	public static final String DS_TYPE = "WarcDS";
	public static final String URL_TAG = "WarcURLTag";
	public static final String REGEXP_URL_TAG = "RegExpURLAttribute";
	private static final String HEADER_WARC_LANGUAGES = "WARC-All-Languages";
    private static final String HEADER_WARC_CONTENT_TYPES = "WARC-All-Content_types";
    private static final String HEADER_WARC_CONTENT_TYPE = "Content-types";
    private static final String HEADER_WARC_CONTENT_TYPE_VALUE = "application/warc-fields";

	@SuppressWarnings("unused")
	private OutputWarcConfig config;
	private WARCWriter writer;
	private File warc;

	private DataInputStream dis;

	private static Logger logger = Logger.getLogger(WarcDS.class);

	/**
	 * Open a Warc datasource in read mode
	 * 
	 * @param dsConfig DataSourceConfig
	 * @throws DSException If error
	 */
	public WarcDS(DataSourceConfig dsConfig) throws DSException {
		super(dsConfig);
		try {
			logger.info("Openning file: " + dsConfig.getFilePath());
			if (dsConfig.getFilePath().toLowerCase().endsWith(".gz")) {
				dis = new DataInputStream(new GZIPInputStream(
						new FileInputStream(dsConfig.getFilePath())));
			} else {
				dis = new DataInputStream(new FileInputStream(
						dsConfig.getFilePath()));
			}
		} catch (IOException e) {
			throw new OpenException(e);
		}
	}

	/**
	 * Open a Warc datasource in write mode
	 * 
	 * @param config OutputWarcConfig
	 * @throws DSException If error
	 */
	public WarcDS(OutputWarcConfig config) throws DSException {
		this.config = config;

		warc = new File(config.getFileName());

		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(warc));

            ANVLRecord headers = new ANVLRecord();
            headers.addLabelValue(HEADER_WARC_LANGUAGES, "");
            headers.addLabelValue(HEADER_WARC_CONTENT_TYPES, "");

            writer = new WARCWriter(new AtomicInteger(), bos, warc,
					getSettings(false, "", null, new ArrayList<>(1)));

            // Write a warcinfo record with description about how this WARC
            // was made.
            // If you want to write something in the head of warc
            String metadata = "WARCProcessor by SING Group www.sing-group.com";
            ByteArrayInputStream is = new ByteArrayInputStream(metadata.getBytes());

            writer.writeWarcinfoRecord("application/warc-fields", headers, is, metadata.getBytes().length);
		} catch (IOException e) {
			throw new OpenException(e);
		}

	}

	private WARCWriterPoolSettings getSettings(final boolean isCompressed,
			final String prefix, final List<File> arcDirs,
			final List<String> metadata) {
		return new WARCWriterPoolSettings() {
			public List<File> calcOutputDirs() {
				return arcDirs;
			}

			public boolean getCompress() {
				return isCompressed;
			}

			public boolean getFrequentFlushes() {
				return false;
			}

			public long getMaxFileSizeBytes() {
				return ARCConstants.DEFAULT_MAX_ARC_FILE_SIZE;
			}

			public List<String> getMetadata() {
				return metadata;
			}

			public String getPrefix() {
				return prefix;
			}

			public String getTemplate() {
				return "${prefix}-${timestamp17}-${serialno}";
			}

			public int getWriteBufferSize() {
				return 4096;
			}

			public RecordIDGenerator getRecordIDGenerator() {
				return new UUIDGenerator();
			}
		};
	}

	public DataBean read() throws DSException {
		DataBean dataBean = null;
		try {
			WarcRecord warcRecord = null;
			String url = null;

			do {
				warcRecord = WarcRecord.readNextWarcRecord(dis);
				if (warcRecord == null)
					return null;

				for (Entry<String, String> entries : warcRecord
						.getHeaderMetadata()) {
					logger.debug("key: " + entries.getKey()
							+ ", value: " + entries.getValue());
				}

				logger.debug("URI_tag_param: "
						+ this.getDataSourceConfig().getCustomParams()
								.get(URL_TAG).getValue());

				url = warcRecord.getHeaderMetadataItem(this
						.getDataSourceConfig().getCustomParams().get(URL_TAG)
						.getValue());
			} while (url == null);

			dataBean = new DataBean();
			dataBean.setUrl(url);

			dataBean.setData(warcRecord.getContent());

			boolean isSpam = false;
			// If it's not specify either isSpam or not, set spam
			if (this.getDataSourceConfig().getSpam() != null) {
				isSpam = this.getDataSourceConfig().getSpam();
			}

			dataBean.setSpam(isSpam);
			dataBean.setTypeDS(DS_TYPE);

		} catch (IOException e) {
			throw new DSException(e);
		}

		return dataBean;
	}

	public void write(DataBean bean) throws DSException {
		// Write a warcinfo record with description about how this WARC
		// was made.
		if (bean.getData() != null) {
			try {
				InputStream is = null;
				if (bean.getData() instanceof String) {
					is = new ByteArrayInputStream(
							((String) bean.getData())
									.getBytes(Constants.outputEnconding));
				} else {
					is = new ByteArrayInputStream((byte[]) bean.getData());
				}

				String pageLanguage = getPageLanguage(String.valueOf(bean.getData()));
				ANVLRecord headers = new ANVLRecord(1);
				headers.addLabelValue("WARC-Language", pageLanguage);
				writer.writeResponseRecord(bean.getUrl(),
						ArchiveUtils.get14DigitDate(),
						bean.getContentType(),
						new URI(bean.getUrl()),
						headers,
						is,
						is.available());
				is.close();

			} catch (IOException e) {
				e.printStackTrace();
				throw new WriteException(e);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			logger.info("There is not content to write. Check internet connection.");
		}

	}

	public void close() throws DSException {
		try {
			if (writer != null)
				writer.close();

			// Check if the output file is empty and remove it
			if (warc != null && warc.length() == 0) {
				warc.delete();
			}
		} catch (IOException e) {
			throw new CloseException(e);
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
}
