package com.warcgenerator.core.datasource.warc;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.archive.io.ArchiveRecord;
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

public class WarcDS extends DataSource implements IDataSource {
	public static final String DS_TYPE = "WarcDS";
	public static final String URL_TAG = "WarcURLTag";
	public static final String REGEXP_URL_TAG = "RegExpURLAttribute";

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

			List<String> metadata = new ArrayList<String>(1);
			// metadata.add("something");

			writer = new WARCWriter(new AtomicInteger(), bos, warc,
					getSettings(false, "", null, metadata));

			// Write a warcinfo record with description about how this WARC
			// was made.
			// If you want to write something in the head of warc
			writer.writeWarcinfoRecord(warc.getName(), "toConfigure");
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

				ANVLRecord headers = new ANVLRecord(1);
				if (bean.getLanguage() != null && !bean.getLanguage().isEmpty()) {
                    headers.addLabelValue("WARC-language", bean.getLanguage());
                }
				writer.writeResponseRecord(bean.getUrl(),
						ArchiveUtils.get14DigitDate(),
						bean.getTypeDS(),
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

}
