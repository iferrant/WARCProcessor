package com.warcgenerator.core.datasource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.archive.io.ArchiveRecord;
import org.archive.io.arc.ARCConstants;
import org.archive.io.warc.WARCReader;
import org.archive.io.warc.WARCReaderFactory;
import org.archive.io.warc.WARCWriter;
import org.archive.io.warc.WARCWriterPoolSettings;
import org.archive.uid.RecordIDGenerator;
import org.archive.uid.UUIDGenerator;

import com.warcgenerator.core.config.DataSourceConfig;
import com.warcgenerator.core.config.OutputWarcConfig;
import com.warcgenerator.core.exception.datasource.CloseException;
import com.warcgenerator.core.exception.datasource.DSException;
import com.warcgenerator.core.exception.datasource.OpenException;
import com.warcgenerator.core.exception.datasource.WriteException;

public class WarcDS extends DataSource implements IDataSource {
	@SuppressWarnings("unused")
	private OutputWarcConfig config;
	private WARCWriter writer;
	private WARCReader reader;
	private Iterator<ArchiveRecord> archIt;
	private File warc;

	private static Logger logger = Logger.getLogger(WarcDS.class);

	/**
	 * Open a Warc datasource in read mode
	 * 
	 * @param path
	 * @throws DSException
	 */
	public WarcDS(DataSourceConfig dsConfig) throws DSException {
		super(dsConfig);
		try {
			System.out.println("abriendo fichero: " + dsConfig.getFilePath());
			//create a temp file
	    	/*File temp = File.createTempFile("prueba", ".tmp"); 
			
	    	try {
				(new Warc2Arc()).transform(new File(path),
						  temp, "WARC2ARC", null, false);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	    	
			/*(new Arc2Warc()).transform(new File(path),
				  temp, true);*/
			
			//System.out.println("temp file: " + temp.getAbsolutePath());		
	    	reader = WARCReaderFactory.get(dsConfig.getFilePath());
	
			archIt = reader.iterator();

		} catch (IOException e) {
			System.out.println("excepcion " + e);

			throw new OpenException(e);
		}
	}

	/**
	 * Open a Warc datasource in write mode
	 * 
	 * @param config
	 * @throws DSException
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
					getSettings(false, null, null, metadata));

			// Write a warcinfo record with description about how this WARC
			// was made.
			// If you want to write something in the head of warc
			// writer.writeWarcinfoRecord(warc.getName(), "toConfigure");
		} catch (IOException e) {
			System.out.println("excepcion " + e);

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
		System.out.println("Leyendo datasource!!");
		DataBean dataBean = null;
		ArchiveRecord ar = null;
		if (archIt.hasNext()) {
			ar = archIt.next();
		}
		if (ar != null) {
			// Get the filename
			String url = (String) ar.getHeader()
					.getHeaderFields().get("WARC-Filename");
			dataBean = new DataBean();
			dataBean.setUrl(url);
			dataBean.setSpam(this.getDataSourceConfig().isSpam());
			//System.out.println("databean: " + dataBean.getData());
		}
		
		return dataBean;
	}

	public void write(DataBean bean) throws DSException {
			// Write a warcinfo record with description about how this WARC
			// was made.
			try {
				writer.writeWarcinfoRecord(bean.getUrl(),
						bean.getData());
			} catch (IOException e) {
				throw new WriteException(e);
			}
	}

	public void close() throws DSException {
		try {
			if (writer != null)
				writer.close();
			if (reader != null)
				reader.close();

			// Check if the output file is empty and remove it
			if (warc != null && warc.length() == 0) {
				warc.delete();
			}
		} catch (IOException e) {
			throw new CloseException(e);
		}
	}

}
